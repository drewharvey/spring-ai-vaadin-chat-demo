package com.drew.ai.chat.ui.view;

import com.drew.ai.chat.ui.component.AiChat;
import com.drew.ai.chat.entity.CustomerEntity;
import com.drew.ai.chat.service.CustomerService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("customers")
@PageTitle("Customers")
public class CustomerView extends SplitLayout {

    private final Grid<CustomerEntity> grid;
    private final CustomerService customerService;

    public CustomerView(CustomerService customerService,
                        AiChat aiChat) {

        this.customerService = customerService;

        setSizeFull();

        grid = new Grid<>(CustomerEntity.class);
        grid.removeColumn(grid.getColumnByKey("id"));
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.setSizeFull();
        addToPrimary(grid);

        aiChat.addChatResponseListener(e -> refresh());
        addToSecondary(aiChat);

        refresh();
    }

    public void refresh() {
        grid.setItems(customerService.findAll());
    }
}
