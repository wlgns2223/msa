package com.sparta.msa.lesson.domain.function.service;

import com.sparta.msa.lesson.domain.function.tools.FunctionTools;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FunctionService {

  private final ChatClient.Builder builder;
  private final FunctionTools functionTools;

  public String chat(String userMessage) {
    log.info("user message {}", userMessage);
    try {
      return builder.build()
          .prompt()
          .user(userMessage)
          .tools(functionTools)
          .call()
          .content();

    } catch (Exception e) {
      log.error(e.getMessage());
      throw new RuntimeException(e);
    }
  }

  public String chatWithSystemMessage(String systemMessage, String userMessage) {
    log.info("user message {}", userMessage);
    try {
      return builder.build()
          .prompt()
          .system(systemMessage)
          .user(userMessage)
          .tools(functionTools)
          .call()
          .content();

    } catch (Exception e) {
      log.error(e.getMessage());
      throw new RuntimeException(e);
    }

  }

}
