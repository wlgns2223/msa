package com.sparta.msa.lesson.domain.ai.service;

import com.sparta.msa.lesson.domain.ai.dto.response.ContextChatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiChatService {

    private final ChatClient chatClient;

    private final Map<String, List<Message>> conversations = new ConcurrentHashMap<>();

    public ContextChatResponse chat(String question) {
        String response = chatClient.prompt()
                .user(question)
                .call()
                .content();

        return ContextChatResponse.builder()
                .message(response)
                .conversationId(UUID.randomUUID().toString())
                .timestamp(LocalDateTime.now())
                .build();
    }

    public ContextChatResponse chatWithHistory(String message, String conversationId) {
        if (conversationId == null || conversationId.isBlank()) {
            conversationId = UUID.randomUUID().toString();
        }

        List<Message> history = conversations.getOrDefault(conversationId, new ArrayList<>());
        UserMessage userMessage = new UserMessage(message);
        history.add(userMessage);

        try {
            ChatResponse response = chatClient.prompt()
                    .messages(history)
                    .call()
                    .chatResponse();

            String assistantResponse = response.getResult().getOutput().getText();

            AssistantMessage assistantMessage = new AssistantMessage(assistantResponse);
            history.add(assistantMessage);

            conversations.put(conversationId, history);

            var usage = response.getMetadata().getUsage();
            ContextChatResponse.TokenUsage tokenUsage = ContextChatResponse.TokenUsage
                    .builder()
                    .promptTokens(usage.getPromptTokens().intValue())
                    .completionTokens(usage.getCompletionTokens().intValue())
                    .totalTokens(usage.getTotalTokens())
                    .build();

            return ContextChatResponse.builder()
                    .message(assistantResponse)
                    .conversationId(conversationId)
                    .timestamp(LocalDateTime.now())
                    .tokenUsage(tokenUsage)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Flux<String> chatStream(String question) {
        return chatClient.prompt()
                .user(question)
                .stream()
                .content();
    }

}
