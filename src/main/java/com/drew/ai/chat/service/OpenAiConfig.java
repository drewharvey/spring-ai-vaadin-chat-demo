package com.drew.ai.chat.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAiConfig {

    // configure the main chat client
    @Bean
    ChatClient chatClient(ChatClient.Builder builder, ChatMemory chatMemory) {
        return builder
                // configure the chat client to keep a history of the conversation
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
                .build();
    }

    // configure how/what conversation history is kept
    // default is to keep the last 20 messages
    // history can be persisted with a ChatMemoryRepository
    @Bean
    ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder()
                .build();
    }
}
