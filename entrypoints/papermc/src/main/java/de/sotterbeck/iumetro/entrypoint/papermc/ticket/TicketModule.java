package de.sotterbeck.iumetro.entrypoint.papermc.ticket;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.multibindings.ProvidesIntoSet;
import de.sotterbeck.iumetro.dataprovider.postgres.ticket.PostgresTicketRepository;
import de.sotterbeck.iumetro.entrypoint.papermc.common.AnnotatedCommand;
import de.sotterbeck.iumetro.usecase.ticket.*;
import org.bukkit.plugin.java.JavaPlugin;

import javax.sql.DataSource;

public class TicketModule extends AbstractModule {

    @Provides
    static TicketRepository provideTicketRepository(DataSource dataSource) {
        return new PostgresTicketRepository(dataSource);
    }

    @Provides
    static TicketPresenter provideTicketPresenter() {
        return new TicketResponseFormatter();
    }

    @Provides
    static TicketManagingInteractor provideTicketManagingInteractor(TicketRepository ticketRepository, TicketPresenter ticketPresenter) {
        return new TicketManagingInteractorImpl(ticketRepository, ticketPresenter);
    }

    @Provides
    static TicketInfoInteractor provideTicketInfoInteractor(TicketRepository ticketRepository) {
        return new TicketInfoInteractorImpl(ticketRepository);
    }

    @Provides
    static PaperTicketPrinter providePaperTicketPrinter(JavaPlugin plugin) {
        return new PaperTicketPrinter(plugin);
    }

    @ProvidesIntoSet
    static AnnotatedCommand provideTicketCreateCommand(TicketManagingInteractor ticketManagingInteractor, PaperTicketPrinter ticketPrinter) {
        return new TicketCreateCommand(ticketManagingInteractor, ticketPrinter);
    }

    @ProvidesIntoSet
    static AnnotatedCommand provideTicketDeleteCommand(TicketInfoInteractor ticketInfoInteractor, TicketManagingInteractor ticketManagingInteractor) {
        return new TicketDeleteCommand(ticketInfoInteractor, ticketManagingInteractor);
    }

}
