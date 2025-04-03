package de.sotterbeck.iumetro.infra.papermc.common.sign;

import java.util.Map;

public class SignInitializerFactoryImpl implements SignInitializerFactory {

    private final Map<String, SignInitializer> initializers;

    public SignInitializerFactoryImpl(Map<String, SignInitializer> initializers) {
        this.initializers = initializers;
    }

    @Override
    public SignInitializer createInitializer(String signId) {
        SignInitializer initializer = initializers.get(signId);
        if (initializer == null) {
            throw new IllegalArgumentException("Unknown sign id: " + signId);
        }
        return initializer;
    }

}
