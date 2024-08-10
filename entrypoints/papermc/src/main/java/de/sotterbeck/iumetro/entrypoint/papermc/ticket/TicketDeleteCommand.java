package de.sotterbeck.iumetro.entrypoint.papermc.ticket;

import de.sotterbeck.iumetro.entrypoint.papermc.common.AnnotatedCommand;
import de.sotterbeck.iumetro.usecase.ticket.TicketInfoInteractor;
import de.sotterbeck.iumetro.usecase.ticket.TicketManagingInteractor;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.suggestion.Suggestion;

import java.util.UUID;
import java.util.regex.Pattern;

public class TicketDeleteCommand implements AnnotatedCommand {

    private static final Pattern UUID_REGEX =
            Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

    private final TicketInfoInteractor ticketInfoInteractor;
    private final TicketManagingInteractor ticketManagingInteractor;

    public TicketDeleteCommand(TicketInfoInteractor ticketInfoInteractor, TicketManagingInteractor ticketManagingInteractor) {
        this.ticketInfoInteractor = ticketInfoInteractor;
        this.ticketManagingInteractor = ticketManagingInteractor;
    }

    @Command("ticket delete <uuid>")
    @Permission("iumetro.ticket.delete")
    public void deleteCommand(CommandSender sender,
                              @Argument(value = "uuid", suggestions = "ticketId") String id) {

        if (!UUID_REGEX.matcher(id).matches()) {
            sender.sendRichMessage("<red>Invalid UUID");
            return;
        }

        if (!ticketInfoInteractor.exists(id)) {
            sender.sendRichMessage("<red>Ticket with id " + id + " does not exist.");
            return;
        }

        ticketManagingInteractor.delete(UUID.fromString(id));
        sender.sendMessage("Ticket with id " + id + " deleted.");
    }

    @Suggestions("ticketId")
    public Iterable<Suggestion> suggestions(CommandContext<CommandSender> context, String input) {
        return ticketManagingInteractor.getAllIds().stream()
                .map(Suggestion::suggestion)
                .toList();
    }

}
