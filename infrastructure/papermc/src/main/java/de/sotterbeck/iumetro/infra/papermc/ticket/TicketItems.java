package de.sotterbeck.iumetro.infra.papermc.ticket;

import org.bukkit.NamespacedKey;

public final class TicketItems {

    private TicketItems() {
    }

    public static final NamespacedKey ITEM_TYPE_KEY = new NamespacedKey("iumetro", "item-type");
    public static final String ITEM_TYPE_VALUE = "ticket";

    public static final NamespacedKey TICKET_ID_KEY = new NamespacedKey("iumetro", "ticket-id");

}
