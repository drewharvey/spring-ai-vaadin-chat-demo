package com.drew.ai.chat.ui;

import com.drew.ai.chat.ui.view.ChatView;
import com.drew.ai.chat.ui.view.CustomerView;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.theme.lumo.LumoUtility;

/**
 * Main UI 'shell' layout which contains the navigation and header.
 */
@Layout
public class MainLayout extends AppLayout {

    public MainLayout() {
        super();

        var toggle = new DrawerToggle();

        var title = new H1("Vaadin + Spring AI Chat");
        title.getStyle().set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "0");

        var nav = getSideNav();

        var scroller = new Scroller(nav);
        scroller.setClassName(LumoUtility.Padding.SMALL);

        addToDrawer(scroller);
        addToNavbar(toggle, title);
    }

    private SideNav getSideNav() {
        SideNav nav = new SideNav();

        nav.addItem(
                new SideNavItem("AI Chat w/ Customer List", CustomerView.class),
                new SideNavItem("AI Chat Standalone", ChatView.class)
        );

        return nav;
    }
}
