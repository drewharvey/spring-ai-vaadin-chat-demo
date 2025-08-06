package com.drew.ai.chat.ui.component;

import com.drew.ai.chat.entity.CustomerEntity;
import com.drew.ai.chat.service.CustomerService;
import com.drew.ai.chat.service.OpenAiService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A chat component that allows users to interact with the Open AI service.
 */
@SpringComponent
@UIScope
public class AiChat extends VerticalLayout {

    private final CustomerService customerService;
    private final OpenAiService openAiService;

    private final MessageList messageList;
    private final Scroller scrollWrapper;

    private final List<ChatResponseListener> chatResponseListeners = new ArrayList<>();


    public AiChat(CustomerService customerService, OpenAiService openAiService) {
        this.customerService = customerService;
        this.openAiService = openAiService;

        var rememberHistory = new Checkbox("Remember History", true);

        // list of messages
        messageList = new MessageList();
        messageList.setWidthFull();
        messageList.setMarkdown(true);

        // wrap our list of messages in scroller so we can auto-scroll to bottom with each message
        scrollWrapper = new Scroller();
        scrollWrapper.setContent(messageList);

        // message input for user to type messages
        var messageInput = new MessageInput();
        messageInput.addSubmitListener(e -> {

            // add the user's message to the message list
            messageList.addItem(new MessageListItem(e.getValue(), Instant.now(), "Me"));

            // create a single response item (will get updated once we get a response from OpenAI)
            var responseItem = new MessageListItem("", Instant.now(), "Bot");
            messageList.addItem(responseItem);

            scrollChat();

            // fetch a response from OpenAI, updating the response with each chunk of content
            var ui = UI.getCurrent();
            handleResponse(ui, responseItem, openAiService.getResponse(rememberHistory.getValue(), e.getValue()));
        });

        // button to verify customers
        var customerButton = new Button("Show Customers", e -> printCustomers());
        var undoButton = new Button("Undo", e -> requestUndo());
        var historyButton = new Button("Show History", e -> showHistory());

        // organize and setup our view
        setSizeFull();
        addClassNames(LumoUtility.AlignItems.STRETCH);

        add(scrollWrapper, messageInput, new HorizontalLayout(customerButton, undoButton, historyButton, rememberHistory));
        setFlexGrow(1, scrollWrapper);
    }

    private void handleResponse(UI ui, MessageListItem responseItem, Flux<String> responseFlux) {
        responseFlux.subscribe(content -> {
            ui.access(() -> {
                responseItem.appendText(content);
                scrollChat();
            });
        }, error -> {
            ui.access(() -> {
                responseItem.appendText("Error: " + error.getMessage());
                scrollChat();
            });
        }, () -> {
            // fire event to notify listeners that a response was received
            fireChatResponseEvent(responseItem.getText());
        });
    }

    private void printCustomers() {
        var response = customerService.findAll().stream().map(CustomerEntity::toString)
                .collect(Collectors.joining("\n\n"));
        messageList.addItem(new MessageListItem(response, Instant.now(), "System"));
        scrollChat();
    }

    private void requestUndo() {
        // add a system message
        messageList.addItem(new MessageListItem("Requesting undo", Instant.now(), "System"));
        scrollChat();

        // create a single response item (will get updated once we get a response from OpenAI)
        var responseItem = new MessageListItem("", Instant.now(), "Bot");
        messageList.addItem(responseItem);
        scrollChat();

        var ui = UI.getCurrent();
        handleResponse(ui, responseItem, openAiService.undoLastRequestChange());
    }

    private void showHistory() {
        var history = openAiService.getChatHistory();
        var dialog = new Dialog("Chat history");

        var layout = new VerticalLayout(
                history.stream()
                        .map(message -> new Paragraph(message.getMessageType() + ": " + message.getText()))
                        .toArray(Paragraph[]::new)
        );
        dialog.add(layout);
        dialog.open();
    }

    private void scrollChat() {
        // workaround for auto-scrolling the chat to the bottom
        UI.getCurrent().getPage().executeJs("setTimeout(() => $0.scrollTop = $0.scrollHeight, 200)", scrollWrapper.getElement());
    }

    public Registration addChatResponseListener(ChatResponseListener listener) {
        var ui = UI.getCurrent();
        chatResponseListeners.add(e -> ui.access(() -> listener.onResponse(e)));
        return () -> chatResponseListeners.remove(listener);
    }

    private void fireChatResponseEvent(String content) {
        var event = new ChatResponseEvent(content);
        for (ChatResponseListener listener : chatResponseListeners) {
            listener.onResponse(event);
        }
    }

    @FunctionalInterface
    public interface ChatResponseListener {
        void onResponse(ChatResponseEvent event);
    }

    public static class ChatResponseEvent {
        private final String content;

        public ChatResponseEvent(String content) {
            this.content = content;
        }

        public String getContent() {
            return content;
        }
    }

}
