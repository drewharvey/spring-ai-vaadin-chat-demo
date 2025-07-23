package com.drew.ai.chat.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@SessionScope
public class OpenAiService {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;
    private final CustomerService customerService;

    // unique id for the conversation, this enables us to keep track of the conversation history
    private final String conversationId;


    public OpenAiService(ChatClient chatClient,
                         ChatMemory chatMemory,
                         CustomerService customerService) {
        this.chatClient = chatClient;
        this.chatMemory = chatMemory;
        this.customerService = customerService;
        conversationId = UUID.randomUUID().toString();
    }

    public Flux<String> getResponse(boolean includeChatHistory, String userMessage) {
        var request = chatClient.prompt()
                .user(userMessage)
                .tools(customerService);

        if (includeChatHistory) {
            request = request.advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId));
        }

        return request.stream().content();
    }

    public List<Message> getChatHistory() {
        return chatMemory.get(conversationId);
    }

    public Optional<Message> getLastUserMessage() {
        return chatMemory.get(conversationId).stream()
                .filter(message -> message.getMessageType() == MessageType.USER)
                .reduce((first, second) -> second); // reduce all the way to get last user message
    }

    public Flux<String> undoLastRequestChange() {
        var prevRequest = getLastUserMessage();

        if (prevRequest.isPresent()) {
            return getResponse(true, "Undo this request: " + prevRequest.get().getText());
        }

        return Flux.error(new IllegalStateException("No previous request to undo"));
    }
}
