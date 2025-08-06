package com.drew.ai.chat.ui.view;

import com.drew.ai.chat.ui.component.AiChat;
import com.drew.ai.chat.entity.CustomerEntity;
import com.drew.ai.chat.service.CustomerService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.format.DateTimeFormatter;

/**
 * View displaying customer entities and an AI chat component. Users can interact
 * with the chat component to view, edit, and manage customer data.
 */
@Route("customers")
@PageTitle("Customers")
public class CustomerView extends SplitLayout {

    private final Grid<CustomerEntity> grid;
    private final CustomerService customerService;

    public CustomerView(CustomerService customerService, AiChat aiChat) {
        this.customerService = customerService;

        setSizeFull();

        // build grid to display customer entities
        grid = new Grid<>();
        grid.addColumn(CustomerEntity::getFirstName)
                .setHeader("First Name");
        grid.addColumn(CustomerEntity::getLastName)
                .setHeader("Last Name");
        grid.addColumn(CustomerEntity::getEmail)
                .setHeader("Email");
        grid.addColumn(new LocalDateTimeRenderer<>(CustomerEntity::getCreatedAt, () -> DateTimeFormatter.ISO_DATE))
                .setHeader("Created");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.setSizeFull();
        addToPrimary(grid);

        // ai chat component for interacting with customer data in real time
        aiChat.addChatResponseListener(e -> refreshGridItems());
        addToSecondary(aiChat);

        // set the initial items in the grid
        refreshGridItems();
    }

    public void refreshGridItems() {
        grid.setItems(customerService.findAll());
    }
}
