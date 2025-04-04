package de.sotterbeck.iumetro.infra.papermc.ticket;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.multibindings.ProvidesIntoSet;
import de.sotterbeck.iumetro.app.ticket.*;
import de.sotterbeck.iumetro.infra.papermc.common.CloudAnnotated;
import de.sotterbeck.iumetro.infra.postgres.ticket.PostgresTicketRepository;
import jakarta.inject.Singleton;
import org.bukkit.plugin.java.JavaPlugin;

import javax.sql.DataSource;

public class TicketModule extends AbstractModule {

    @Provides
    @Singleton
    static TicketRepository provideTicketRepository(DataSource dataSource) {
        return new PostgresTicketRepository(dataSource);
    }

    @Provides
    @Singleton
    static TicketPresenter provideTicketPresenter() {
        return new TicketResponseFormatter();
    }

    @Provides
    @Singleton
    static TicketIssueService provideTicketManagingInteractor(TicketRepository ticketRepository, TicketPresenter ticketPresenter) {
        return new TicketIssueService(ticketRepository, ticketPresenter);
    }

    @Provides
    @Singleton
    static TicketInfoService provideTicketInfoInteractor(TicketRepository ticketRepository) {
        return new TicketInfoService(ticketRepository);
    }

    @Provides
    @Singleton
    static PaperTicketPrinter providePaperTicketPrinter(JavaPlugin plugin) {
        return new PaperTicketPrinter(plugin);
    }

    @ProvidesIntoSet
    static CloudAnnotated provideTicketCreateCommand(TicketIssueService ticketIssueService, PaperTicketPrinter ticketPrinter) {
        return new TicketCreateCommand(ticketIssueService, ticketPrinter);
    }

    @ProvidesIntoSet
    static CloudAnnotated provideTicketDeleteCommand(TicketInfoService ticketInfoService, TicketIssueService ticketIssueService) {
        return new TicketDeleteCommand(ticketInfoService, ticketIssueService);
    }

}
