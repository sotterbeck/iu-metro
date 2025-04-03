package de.sotterbeck.iumetro.infra.papermc.ticket;

import de.sotterbeck.iumetro.app.ticket.TicketManagingInteractor;
import de.sotterbeck.iumetro.app.ticket.TicketRequestModel;
import de.sotterbeck.iumetro.infra.papermc.common.CloudAnnotated;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Default;
import org.incendo.cloud.annotations.Permission;

import java.time.Duration;
import java.util.UUID;

public class TicketCreateCommand implements CloudAnnotated {

    private final TicketManagingInteractor ticketManagingInteractor;
    private final PaperTicketPrinter ticketPrinter;

    public TicketCreateCommand(TicketManagingInteractor ticketManagingInteractor, PaperTicketPrinter ticketPrinter) {
        this.ticketManagingInteractor = ticketManagingInteractor;
        this.ticketPrinter = ticketPrinter;
    }

    @Command("ticket create <name> [usageLimit] [timeLimit]")
    @Permission("iumetro.ticket.create")
    public void createCommand(
            CommandSender sender,
            @Argument(value = "name", description = "A ticket name") String name,
            @Argument(value = "usageLimit") @Default("0") int usageLimit,
            @Argument(value = "timeLimit") @Default("0") int timeLimit
    ) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return;
        }

        var request = new TicketRequestModel(UUID.randomUUID(), name, usageLimit, Duration.ofMinutes(timeLimit));

        var response = ticketManagingInteractor.create(request);

        if (response == null) {
            player.sendMessage("A error occurred while creating a ticket.");
            return;
        }

        ticketPrinter.printTicket(player, response);
        player.sendMessage("Ticket created.");
    }

}
