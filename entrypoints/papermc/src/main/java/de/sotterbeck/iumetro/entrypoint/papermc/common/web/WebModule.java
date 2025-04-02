package de.sotterbeck.iumetro.entrypoint.papermc.common.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import de.sotterbeck.iumetro.entrypoint.papermc.retail.MetroStationResponseModelMixIn;
import de.sotterbeck.iumetro.usecase.station.MetroStationResponseModel;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;

public class WebModule extends AbstractModule {

    @Override
    protected void configure() {
        Multibinder.newSetBinder(binder(), Routing.class);
    }

    @Provides
    @Singleton
    static ObjectMapper provideObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.addMixIn(MetroStationResponseModel.class, MetroStationResponseModelMixIn.class);
        objectMapper.registerModule(new Jdk8Module());
        return objectMapper;
    }

    @Provides
    @Singleton
    static Javalin provideJavalin(ObjectMapper objectMapper) {
        return Javalin.create(config -> {
            config.jsonMapper(new JavalinJackson(objectMapper, true));
        });
    }

}
