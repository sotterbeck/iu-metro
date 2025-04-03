package de.sotterbeck.iumetro.infra.papermc.common.sign;

public interface SignInitializerFactory {

    SignInitializer createInitializer(String signId);

}
