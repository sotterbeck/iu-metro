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
import io.javalin.openapi.plugin.OpenApiPlugin;
import io.javalin.openapi.plugin.redoc.ReDocPlugin;
import io.javalin.openapi.plugin.swagger.SwaggerPlugin;
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

            config.registerPlugin(new OpenApiPlugin(pluginConfig -> {
                pluginConfig.withDefinitionConfiguration((version, definition) -> {
                    definition.info(info -> {
                        info.title("IuMetro API");
                        info.version("1.0.0");
                        info.description("REST API for the IuMetro metro ticketing system.");
                    });
                    definition.withBearerAuth("bearerAuth");
                    definition.withCookieAuth("refreshTokenCookie", "refresh_token");
                });
                // TODO: This needs to be cleaned up.
                pluginConfig.withDefinitionProcessor(content -> {
                    content.put("openapi", "3.0.3");

                    var components = content.with("components");
                    var schemas = components.with("schemas");

                    var usageLimit = schemas.putObject("UsageLimit");
                    usageLimit.put("type", "object");
                    var usageLimitProps = usageLimit.putObject("properties");
                    usageLimitProps.putObject("type").put("type", "string");
                    usageLimitProps.putObject("maxUsages").put("type", "integer").put("format", "int32");
                    usageLimit.putArray("required").add("type").add("maxUsages");

                    var timeLimit = schemas.putObject("TimeLimit");
                    timeLimit.put("type", "object");
                    var timeLimitProps = timeLimit.putObject("properties");
                    timeLimitProps.putObject("type").put("type", "string");
                    timeLimitProps.putObject("duration").put("type", "string");
                    timeLimit.putArray("required").add("type").add("duration");

                    var constraint = schemas.with("Constraint");
                    constraint.remove(List.of("type", "properties"));
                    var oneOf = constraint.putArray("oneOf");
                    oneOf.addObject().put("$ref", "#/components/schemas/UsageLimit");
                    oneOf.addObject().put("$ref", "#/components/schemas/TimeLimit");
                    var discriminator = constraint.putObject("discriminator");
                    discriminator.put("propertyName", "type");
                    var mapping = discriminator.putObject("mapping");
                    mapping.put("usage_limit", "UsageLimit");
                    mapping.put("time_limit", "TimeLimit");

                    var paths = content.with("paths");
                    paths.fields().forEachRemaining(pathEntry -> {
                        pathEntry.getValue().fields().forEachRemaining(methodEntry -> {
                            var operation = (com.fasterxml.jackson.databind.node.ObjectNode) methodEntry.getValue();
                            if (operation.has("security")) {
                                var responses = operation.with("responses");
                                if (!responses.has("401")) {
                                    var unauthorized = responses.putObject("401");
                                    unauthorized.put("description", "Unauthorized - invalid or missing bearer token");
                                    var contentNode = unauthorized.putObject("content");
                                    var jsonContent = contentNode.putObject("application/json");
                                    jsonContent.putObject("schema").put("$ref", "#/components/schemas/ErrorResponse");
                                }
                            }
                        });
                    });

                    return content.toPrettyString();
                });
            }));
            config.registerPlugin(new SwaggerPlugin());
            config.registerPlugin(new ReDocPlugin());

            config.routes.beforeMatched(jwtAccessManager::handle);
            config.routes.apiBuilder(() -> {
                path("/api", () -> {
                    routes.forEach(Routing::bindRoutes);
                });
            });
        });
    }

}
