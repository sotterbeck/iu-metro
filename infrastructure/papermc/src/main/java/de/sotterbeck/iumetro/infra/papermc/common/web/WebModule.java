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
import io.javalin.plugin.bundled.RateLimitPlugin;

import java.util.List;
import java.util.Set;

import static io.javalin.apibuilder.ApiBuilder.path;

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
    static Javalin provideJavalin(ObjectMapper objectMapper, IuMetroConfig iuMetroConfig, JwtAccessManager jwtAccessManager, Set<Routing> routes) {
        return Javalin.create(config -> {
            config.jsonMapper(new JavalinJackson(objectMapper, true));

            List<String> corsOrigins = iuMetroConfig.authCorsOrigins();
            if (!corsOrigins.isEmpty()) {
                config.bundledPlugins.enableCors(cors -> {
                    cors.addRule(rule -> {
                        for (String origin : corsOrigins) {
                            rule.allowHost(origin);
                        }
                        rule.allowCredentials = true;
                    });
                });
            }

            config.registerPlugin(new RateLimitPlugin());

            config.routes.beforeMatched(jwtAccessManager::handle);
            config.routes.apiBuilder(() -> {
                path("/api", () -> {
                    routes.forEach(Routing::bindRoutes);
                });
            });
        });
    }

}
