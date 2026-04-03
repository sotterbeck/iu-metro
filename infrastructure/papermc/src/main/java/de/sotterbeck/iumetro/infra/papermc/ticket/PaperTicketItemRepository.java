package de.sotterbeck.iumetro.infra.papermc.ticket;

import de.sotterbeck.iumetro.app.ticket.TicketItemRepository;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class PaperTicketItemRepository implements TicketItemRepository {

    public PaperTicketItemRepository() {
    }

    @Override
    public Optional<UUID> findCurrentTicket(UUID playerId) {
        var player = Bukkit.getPlayer(playerId);
        assert player != null;

        var item = player.getInventory().getItemInMainHand();

        if (!isTicketItem(item)) {
            return Optional.empty();
        }

        var ticketId = getTicketId(item);
        return ticketId == null
                ? Optional.empty()
                : Optional.of(UUID.fromString(ticketId));
    }

    @Override
    public boolean deleteTicket(UUID playerId, UUID ticketId) {
        var player = Bukkit.getPlayer(playerId);
        assert player != null;

        for (ItemStack item : player.getInventory().getContents()) {
            if (!isTicketItem(item)) {
                continue;
            }

            var itemId = getTicketId(item);
            if (Objects.equals(itemId, ticketId.toString())) {
                player.getInventory().remove(item);
                return true;
            }
        }
        return false;
    }

    private @Nullable String getTicketId(ItemStack item) {
        return item.getItemMeta()
                .getPersistentDataContainer()
                .get(TicketItems.TICKET_ID_KEY, PersistentDataType.STRING);
    }

    private boolean isTicketItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }

        var itemType = item.getItemMeta()
                .getPersistentDataContainer()
                .get(TicketItems.ITEM_TYPE_KEY, PersistentDataType.STRING);

        return Objects.equals(itemType, TicketItems.ITEM_TYPE_VALUE);
    }

}
