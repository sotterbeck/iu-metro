package de.sotterbeck.iumetro.infra.papermc.ticket;

import de.sotterbeck.iumetro.app.ticket.TicketResponseModel;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class PaperTicketPrinter {

    private final JavaPlugin plugin;

    public PaperTicketPrinter(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void printTicket(Player player, TicketResponseModel ticketResponseModel) {
        ItemStack ticketItem = new ItemStack(Material.NAME_TAG);
        ItemMeta meta = ticketItem.getItemMeta();
        meta.displayName(Component.text(ticketResponseModel.name(), NamedTextColor.GREEN, TextDecoration.BOLD));
        setLore(ticketResponseModel, meta);
        setPersistentDataContainer(ticketResponseModel, meta);

        ticketItem.setItemMeta(meta);
        player.getInventory().addItem(ticketItem);
    }

    private void setPersistentDataContainer(TicketResponseModel ticket, PersistentDataHolder dataHolder) {
        NamespacedKey ticketIdKey = new NamespacedKey(plugin, "ticket-id");
        NamespacedKey itemTypeKey = new NamespacedKey(plugin, "item-type");

        dataHolder.getPersistentDataContainer().set(itemTypeKey, PersistentDataType.STRING, "ticket");
        dataHolder.getPersistentDataContainer().set(ticketIdKey, PersistentDataType.STRING, ticket.fullId());
    }

    private void setLore(TicketResponseModel ticket, ItemMeta meta) {
        List<Component> lore = new ArrayList<>();

        lore.add(Component.text(ticket.shortId(), NamedTextColor.DARK_GRAY));
        meta.lore(lore);
    }

}
