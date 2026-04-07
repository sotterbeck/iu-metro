package de.sotterbeck.iumetro.infra.papermc.ticket;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.bukkit.persistence.PersistentDataType.STRING;

class PaperTicketItemRepositoryTest {

    private Player player;
    private PaperTicketItemRepository ticketItemRepository;

    @BeforeEach
    void setUp() {
        ServerMock server = MockBukkit.mock();
        player = server.addPlayer();
        ticketItemRepository = new PaperTicketItemRepository();
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void findCurrentTicketId_ShouldReturnEmptyOptional_WhenNoTicketInHand() {
        var result = ticketItemRepository.findCurrentTicket(player.getUniqueId());

        assertThat(result).isEmpty();
    }

    @Test
    void findCurrentTicketId_ShouldReturnTicketId_WhenTicketInHand() {
        var ticketId = UUID.fromString("bb97848b-0195-4cc4-a9c9-ab1573f09821");
        setTicketItem(ticketId);

        var result = ticketItemRepository.findCurrentTicket(player.getUniqueId());

        assertThat(result).contains(ticketId);
    }

    @Test
    void deleteTicket_ShouldRemoveNothing_WhenNoTicketInHand() {
        var ticketId = "7be6f217-ac86-4576-90db-292f98ea3896";
        var deleted = ticketItemRepository.deleteTicket(player.getUniqueId(), UUID.fromString(ticketId));

        assertThat(deleted).isFalse();
    }

    @Test
    void deleteTicket_ShouldRemoveTicket_WhenTicketInHand() {
        var ticketId = UUID.fromString("7be6f217-ac86-4576-90db-292f98ea3896");
        setTicketItem(ticketId);
        var deleted = ticketItemRepository.deleteTicket(player.getUniqueId(), ticketId);

        assertThat(deleted).isTrue();
        assertThat(player.getInventory().getItemInMainHand().getType()).isEqualTo(Material.AIR);
    }

    @Test
    void deleteTicket_ShouldRemoveTicket_WhenTicketAnySlot() {
        var ticketId = UUID.fromString("7be6f217-ac86-4576-90db-292f98ea3896");
        var slot = 1;
        setTicketItem(ticketId, slot);
        var deleted = ticketItemRepository.deleteTicket(player.getUniqueId(), ticketId);

        assertThat(deleted).isTrue();
        assertThat(player.getInventory().getItem(slot)).isNull();
    }

    private void setTicketItem(UUID ticketId) {
        var ticketItem = createTicketItem(ticketId);
        player.getInventory().setItemInMainHand(ticketItem);
    }

    private void setTicketItem(UUID ticketId, int slot) {
        var ticketItem = createTicketItem(ticketId);
        player.getInventory().setItem(slot, ticketItem);
    }

    private @NonNull ItemStack createTicketItem(UUID ticketId) {
        var ticketItem = new ItemStack(Material.NAME_TAG);
        var meta = ticketItem.getItemMeta();
        meta.getPersistentDataContainer().set(TicketItems.ITEM_TYPE_KEY, STRING, TicketItems.ITEM_TYPE_VALUE);
        meta.getPersistentDataContainer().set(TicketItems.TICKET_ID_KEY, STRING, ticketId.toString());
        ticketItem.setItemMeta(meta);
        return ticketItem;
    }

}
