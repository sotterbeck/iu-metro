package de.sotterbeck.iumetro.entrypoint.papermc.faregate;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.multibindings.ProvidesIntoMap;
import com.google.inject.multibindings.ProvidesIntoSet;
import com.google.inject.multibindings.StringMapKey;
import de.sotterbeck.iumetro.entrypoint.papermc.common.CloudAnnotated;
import de.sotterbeck.iumetro.entrypoint.papermc.common.IuMetroConfig;
import de.sotterbeck.iumetro.entrypoint.papermc.common.sign.SignClickHandler;
import de.sotterbeck.iumetro.entrypoint.papermc.common.sign.SignInitializer;
import de.sotterbeck.iumetro.entrypoint.papermc.common.sign.SignTypeKeyFactory;
import de.sotterbeck.iumetro.entrypoint.papermc.faregate.sign.FareGateSignClickHandler;
import de.sotterbeck.iumetro.entrypoint.papermc.faregate.sign.FareGateSignInitializer;
import de.sotterbeck.iumetro.entrypoint.papermc.faregate.sign.FareGateSignItemCreator;
import de.sotterbeck.iumetro.entrypoint.papermc.faregate.sign.SpigotFareGateSignRepository;
import de.sotterbeck.iumetro.usecase.faregate.*;
import de.sotterbeck.iumetro.usecase.station.MetroStationManagingInteractor;
import jakarta.inject.Singleton;
import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class FareGateModule extends AbstractModule {

    @Provides
    @Singleton
    static FareGateKeyFactory provideFareGateNamespacedKey(JavaPlugin plugin) {
        return new FareGateKeyFactory(plugin);
    }

    @Provides
    @Singleton
    static FareGateSignRepository provideFareGateSignRepository(JavaPlugin plugin, SignTypeKeyFactory signTypeKeyFactory, FareGateKeyFactory fareGateKeyFactory) {
        World world = plugin.getServer().getWorlds().getFirst();
        return new SpigotFareGateSignRepository(world,
                signTypeKeyFactory,
                fareGateKeyFactory);
    }

    @Provides
    @Singleton
    static GateRepository provideGateRepository(JavaPlugin plugin) {
        World world = plugin.getServer().getWorlds().getFirst();
        return new SpigotGateRepository(world);
    }

    @Provides
    @Singleton
    static GateControlAdapter provideGateControlAdapter(JavaPlugin plugin) {
        World world = plugin.getServer().getWorlds().getFirst();
        return new SpigotGateControlAdapter(world, plugin);
    }

    @Provides
    @Singleton
    static FareGateProtectionInteractor provideFareGateProtectionInteractor(FareGateSignRepository fareGateSignRepository) {
        return new FareGateProtectionInteractor(fareGateSignRepository);
    }

    @Provides
    @Singleton
    static FareGateControlInteractor provideFareGateControlInteractor(GateRepository gateRepository, GateControlAdapter gateControlAdapter) {
        return new FareGateControlInteractor(gateRepository, gateControlAdapter);
    }

    @Provides
    @Singleton
    static FareGateSignItemCreator provideFareGateSignItemCreator(SignTypeKeyFactory signTypeKeyFactory, FareGateKeyFactory fareGateKeyFactory, IuMetroConfig iuMetroConfig) {
        return new FareGateSignItemCreator(signTypeKeyFactory, fareGateKeyFactory, iuMetroConfig);
    }

    @Provides
    @Singleton
    static FareGateSignClickHandler provideFareGateSignClickHandler(FareGateControlInteractor fareGateControlInteractor, FareGateKeyFactory fareGateKeyFactory) {
        return new FareGateSignClickHandler(fareGateControlInteractor, fareGateKeyFactory);
    }

    @ProvidesIntoSet
    static CloudAnnotated provideFareGateCreateCommand(MetroStationManagingInteractor metroStationManagingInteractor, FareGateSignItemCreator signItemCreator) {
        return new FareGateCreateCommand(metroStationManagingInteractor, signItemCreator);
    }

    @ProvidesIntoSet
    static Listener provideGateInteractListener(FareGateProtectionInteractor fareGateProtectionInteractor) {
        return new GateInteractListener(fareGateProtectionInteractor);
    }

    @ProvidesIntoMap
    @StringMapKey("faregate_entry")
    static SignInitializer provideFareGateEntrySignInitializer(SignTypeKeyFactory signTypeKeyFactory, FareGateKeyFactory fareGateKeyFactory, IuMetroConfig config) {
        return new FareGateSignInitializer(signTypeKeyFactory, fareGateKeyFactory, config.signLines("faregate_entry"));
    }

    @ProvidesIntoMap
    @StringMapKey("faregate_entry")
    static SignClickHandler provideFareGateEntrySignClickHandler(FareGateSignClickHandler signClickHandler) {
        return signClickHandler;
    }

    @ProvidesIntoMap
    @StringMapKey("faregate_exit")
    static SignInitializer provideFareGateExitSignInitializer(SignTypeKeyFactory signTypeKeyFactory, FareGateKeyFactory fareGateKeyFactory, IuMetroConfig config) {
        return new FareGateSignInitializer(signTypeKeyFactory, fareGateKeyFactory, config.signLines("faregate_exit"));
    }

    @ProvidesIntoMap
    @StringMapKey("faregate_exit")
    static SignClickHandler provideFareGateExitSignClickHandler(FareGateSignClickHandler signClickHandler) {
        return signClickHandler;
    }

}
