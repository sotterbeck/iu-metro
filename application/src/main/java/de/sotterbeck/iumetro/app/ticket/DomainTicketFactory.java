package de.sotterbeck.iumetro.app.ticket;

import de.sotterbeck.iumetro.domain.ticket.Ticket;
import de.sotterbeck.iumetro.domain.ticket.Tickets;
import de.sotterbeck.iumetro.domain.ticket.validators.TicketValidator;
import de.sotterbeck.iumetro.domain.ticket.validators.TimeLimitValidator;
import de.sotterbeck.iumetro.domain.ticket.validators.UsageLimitValidator;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DomainTicketFactory {

    public Ticket from(TicketDto ticket) {
        return create(ticket.id(), ticket.name(), ticket.config());
    }

    public Ticket from(TicketRequestModel ticket) {
        return create(ticket.id(), ticket.name(), ticket.config());
    }

    public Ticket create(UUID id, String name, TicketConfig config) {
        List<TicketValidator> validators = toValidators(config);
        return Tickets.createTicket(name, id, validators);
    }

    private List<TicketValidator> toValidators(TicketConfig config) {
        List<TicketValidator> validators = new ArrayList<>();
        if (config != null && config.constraints() != null) {
            for (TicketConfig.Constraint constraint : config.constraints()) {
                switch (constraint) {
                    case TicketConfig.UsageLimit(int maxUsages) -> validators.add(new UsageLimitValidator(maxUsages));
                    case TicketConfig.TimeLimit(String duration) ->
                            validators.add(new TimeLimitValidator(Duration.parse(duration)));
                    case null, default -> {
                    }
                }
            }
        }
        return validators;
    }

}
