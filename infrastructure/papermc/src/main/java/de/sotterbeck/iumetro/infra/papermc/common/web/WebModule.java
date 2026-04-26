package de.sotterbeck.iumetro.infra.papermc.common.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import de.sotterbeck.iumetro.app.station.MetroStationResponseModel;
import de.sotterbeck.iumetro.infra.papermc.auth.JwtAccessManager;
import de.sotterbeck.iumetro.infra.papermc.common.IuMetroConfig;
import de.sotterbeck.iumetro.infra.papermc.network.MetroStationResponseModelMixIn;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;

import java.util.List;

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
    static Javalin provideJavalin(ObjectMapper objectMapper, IuMetroConfig config, JwtAccessManager jwtAccessManager) {
        return Javalin.create(javalinConfig -> {
            javalinConfig.jsonMapper(new JavalinJackson(objectMapper, true));

            List<String> corsOrigins = config.authCorsOrigins();
            if (!corsOrigins.isEmpty()) {
                javalinConfig.bundledPlugins.enableCors(cors -> {
                    cors.addRule(rule -> {
                        for (String origin : corsOrigins) {
                            rule.allowHost(origin);
                        }
                        rule.allowCredentials = true;
                    });
                });
            }

            javalinConfig.router.mount(router -> {
                router.beforeMatched(jwtAccessManager::handle);
            });
        });
    }

}
