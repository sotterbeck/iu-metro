package de.sotterbeck.iumetro.entrypoint.papermc.ticket;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.multibindings.ProvidesIntoSet;
import de.sotterbeck.iumetro.dataprovider.postgres.ticket.PostgresTicketRepository;
import de.sotterbeck.iumetro.entrypoint.papermc.common.CloudAnnotated;
import de.sotterbeck.iumetro.usecase.ticket.*;
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
    static TicketManagingInteractor provideTicketManagingInteractor(TicketRepository ticketRepository, TicketPresenter ticketPresenter) {
        return new TicketManagingInteractorImpl(ticketRepository, ticketPresenter);
    }

    @Provides
    @Singleton
    static TicketInfoInteractor provideTicketInfoInteractor(TicketRepository ticketRepository) {
        return new TicketInfoInteractorImpl(ticketRepository);
    }

    @Provides
    @Singleton
    static PaperTicketPrinter providePaperTicketPrinter(JavaPlugin plugin) {
        return new PaperTicketPrinter(plugin);
    }

    @ProvidesIntoSet
    static CloudAnnotated provideTicketCreateCommand(TicketManagingInteractor ticketManagingInteractor, PaperTicketPrinter ticketPrinter) {
        return new TicketCreateCommand(ticketManagingInteractor, ticketPrinter);
    }

    @ProvidesIntoSet
    static CloudAnnotated provideTicketDeleteCommand(TicketInfoInteractor ticketInfoInteractor, TicketManagingInteractor ticketManagingInteractor) {
        return new TicketDeleteCommand(ticketInfoInteractor, ticketManagingInteractor);
    }

}
