package com.sparta.msa.lesson.domain.advisor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;

@Slf4j
public class ChatMemoryAdvisor implements BaseAdvisor {

  private static final String CONTEXT_USER_TEXT = "chat_memory_user_text";

  private final Map<String, List<Message>> conversationStore = new ConcurrentHashMap<>();
  private final String conversationId;
  private final int maxMessages;

  public ChatMemoryAdvisor(String conversationId, int maxMessages) {
    this.conversationId = conversationId;
    this.maxMessages = maxMessages;
  }

  @Override
  public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {

    List<Message> history = conversationStore.getOrDefault(conversationId,
        new CopyOnWriteArrayList<>());

    List<Message> fullMessages = new ArrayList<>(history);
    fullMessages.addAll(chatClientRequest.prompt().getInstructions());

    String userText = chatClientRequest.prompt().getInstructions().stream()
        .filter(m -> m.getMessageType() == MessageType.USER)
        .map(Message::getText)
        .findFirst().orElse("");

    return chatClientRequest.mutate()
        .prompt(new Prompt(fullMessages))
        .context(CONTEXT_USER_TEXT, userText)
        .build();
  }

  @Override
  public ChatClientResponse after(ChatClientResponse chatClientResponse,
      AdvisorChain advisorChain) {

    List<Message> history = conversationStore.computeIfAbsent(conversationId,
        k -> new CopyOnWriteArrayList<>());

    Optional.ofNullable(chatClientResponse.context().get(CONTEXT_USER_TEXT))
        .map(Object::toString)
        .filter(text -> !text.isBlank())
        .ifPresent(text -> history.add(new UserMessage(text)));

    if (chatClientResponse.chatResponse() != null
        && chatClientResponse.chatResponse().getResult() != null) {
      var output = chatClientResponse.chatResponse().getResult().getOutput();
      history.add(new AssistantMessage(output.getText(), output.getMetadata()));
    }

    while (history.size() > maxMessages && !history.isEmpty()) {
      history.remove(0);
    }

    return chatClientResponse;
  }

  @Override
  public int getOrder() {
    return 0;
  }
}
