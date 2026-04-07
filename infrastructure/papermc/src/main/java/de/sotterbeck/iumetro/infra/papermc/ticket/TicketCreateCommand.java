package de.sotterbeck.iumetro.infra.papermc.ticket;

import de.sotterbeck.iumetro.app.ticket.TicketConfig;
import de.sotterbeck.iumetro.app.ticket.TicketIssueService;
import de.sotterbeck.iumetro.app.ticket.TicketRequestModel;
import de.sotterbeck.iumetro.infra.papermc.common.CloudAnnotated;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Default;
import org.incendo.cloud.annotations.Permission;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TicketCreateCommand implements CloudAnnotated {

    private final TicketIssueService ticketIssueService;
    private final PaperTicketPrinter ticketPrinter;

    public TicketCreateCommand(TicketIssueService ticketIssueService, PaperTicketPrinter ticketPrinter) {
        this.ticketIssueService = ticketIssueService;
        this.ticketPrinter = ticketPrinter;
    }

    @Command("ticket create <name> [usageLimit] [timeLimit]")
    @Permission("iumetro.ticket.create")
    public void createCommand(
            CommandSourceStack source,
            @Argument(value = "name", description = "A ticket name") String name,
            @Argument(value = "usageLimit") @Default("0") int usageLimit,
            @Argument(value = "timeLimit") @Default("0") int timeLimit
    ) {
        var sender = source.getSender();
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return;
        }

        var constraints = createConstraints(usageLimit, timeLimit);
        var config = new TicketConfig(constraints);
        var request = new TicketRequestModel(UUID.randomUUID(), name, config);

        var response = ticketIssueService.create(request);

        if (response == null) {
            player.sendMessage("A error occurred while creating a ticket.");
            return;
        }

        ticketPrinter.printTicket(player, response);
        player.sendMessage("Ticket created.");
    }

    private @NotNull List<TicketConfig.Constraint> createConstraints(int usageLimit, int timeLimit) {
        List<TicketConfig.Constraint> constraints = new ArrayList<>();
        if (usageLimit > 0) {
            constraints.add(new TicketConfig.UsageLimit(usageLimit));
        }
        if (timeLimit > 0) {
            constraints.add(new TicketConfig.TimeLimit(Duration.ofMinutes(timeLimit).toString()));
        }
        return constraints;
    }

}
