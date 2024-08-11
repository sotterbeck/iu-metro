package de.sotterbeck.iumetro.entrypoint.papermc.common.sign;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.ProvidesIntoSet;
import jakarta.inject.Singleton;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public class SignModule extends AbstractModule {

    @Override
    protected void configure() {
        MapBinder.newMapBinder(binder(), String.class, SignInitializer.class);
        MapBinder.newMapBinder(binder(), String.class, SignClickHandler.class);
    }

    @Provides
    @Singleton
    static SignTypeKeyFactory provideSignTypeNamespacedKey(JavaPlugin plugin) {
        return new SignTypeKeyFactory(plugin);
    }

    @Provides
    @Singleton
    static SignInitializerFactory provideSignFactory(Map<String, SignInitializer> initializers) {
        return new SignInitializerFactoryImpl(initializers);
    }

    @Provides
    @Singleton
    static SignClickHandlerFactory provideSignClickHandlerFactory(SignTypeKeyFactory signTypeKeyFactory, Map<String, SignClickHandler> handlers) {
        return new SignClickHandlerFactoryImpl(signTypeKeyFactory.getSignTypeNamespacedKey(), handlers);
    }

    @ProvidesIntoSet
    static Listener provideSignPlaceListener(SignTypeKeyFactory signTypeKeyFactory, SignInitializerFactory signInitializerFactory) {
        return new SignPlaceListener(signTypeKeyFactory, signInitializerFactory);
    }

    @ProvidesIntoSet
    static Listener provideSignInteractListener(SignTypeKeyFactory signTypeKeyFactory, SignClickHandlerFactory signClickHandlerFactory) {
        return new SignInteractListener(signTypeKeyFactory.getSignTypeNamespacedKey(), signClickHandlerFactory);
    }

}
