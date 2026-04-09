package de.sotterbeck.iumetro.infra.papermc.retail;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.multibindings.ProvidesIntoSet;
import de.sotterbeck.iumetro.app.retail.RetailTicketPresenter;
import de.sotterbeck.iumetro.app.retail.RetailTicketRepository;
import de.sotterbeck.iumetro.app.retail.RetailTicketService;
import de.sotterbeck.iumetro.infra.papermc.common.web.Routing;
import de.sotterbeck.iumetro.infra.postgres.retail.PostgresRetailTicketRepository;
import io.javalin.Javalin;
import jakarta.inject.Singleton;

import javax.sql.DataSource;

public class RetailTicketModule extends AbstractModule {

    @Provides
    @Singleton
    static RetailTicketRepository provideRetailTicketRepository(DataSource dataSource) {
        return new PostgresRetailTicketRepository(dataSource);
    }

    @Provides
    @Singleton
    static RetailTicketPresenter provideRetailTicketPresenter() {
        return new WebRetailTicketPresenter();
    }

    @Provides
    @Singleton
    static RetailTicketService provideRetailTicketService(RetailTicketRepository retailTicketRepository,
                                                          RetailTicketPresenter retailTicketPresenter) {
        return new RetailTicketService(retailTicketRepository, retailTicketPresenter);
    }

    @ProvidesIntoSet
    static Routing provideRetailTicketRouting(Javalin javalin, RetailTicketService retailTicketService) {
        return new RetailTicketRouting(javalin, retailTicketService);
    }

}
