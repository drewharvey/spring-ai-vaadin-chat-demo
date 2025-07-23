package com.drew.ai.chat.ui.view;

import com.drew.ai.chat.ui.component.AiChat;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("")
@PageTitle("Chat")
public class ChatView extends VerticalLayout {

    public ChatView(AiChat aiChat) {

        setSizeFull();

        aiChat.setSizeFull();
        add(aiChat);
    }

}
