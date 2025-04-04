package de.sotterbeck.iumetro.infra.papermc.ticket;

import de.sotterbeck.iumetro.app.ticket.TicketInfoService;
import de.sotterbeck.iumetro.app.ticket.TicketIssueService;
import de.sotterbeck.iumetro.infra.papermc.common.CloudAnnotated;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.suggestion.Suggestion;

import java.util.UUID;
import java.util.regex.Pattern;

public class TicketDeleteCommand implements CloudAnnotated {

    private static final Pattern UUID_REGEX =
            Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

    private final TicketInfoService ticketInfoService;
    private final TicketIssueService ticketIssueService;

    public TicketDeleteCommand(TicketInfoService ticketInfoService, TicketIssueService ticketIssueService) {
        this.ticketInfoService = ticketInfoService;
        this.ticketIssueService = ticketIssueService;
    }

    @Command("ticket delete <uuid>")
    @Permission("iumetro.ticket.delete")
    public void deleteCommand(CommandSender sender,
                              @Argument(value = "uuid", suggestions = "ticketId") String id) {

        if (!UUID_REGEX.matcher(id).matches()) {
            sender.sendRichMessage("<red>Invalid UUID");
            return;
        }

        if (!ticketInfoService.exists(id)) {
            sender.sendRichMessage("<red>Ticket with id " + id + " does not exist.");
            return;
        }

        ticketIssueService.delete(UUID.fromString(id));
        sender.sendMessage("Ticket with id " + id + " deleted.");
    }

    @Suggestions("ticketId")
    public Iterable<Suggestion> suggestions(CommandContext<CommandSender> context, String input) {
        return ticketIssueService.getAllIds().stream()
                .map(Suggestion::suggestion)
                .toList();
    }

}
