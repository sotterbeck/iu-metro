package de.sotterbeck.iumetro.entrypoint.papermc.common.sign;

import org.bukkit.NamespacedKey;
import org.bukkit.block.Sign;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;

public class SignClickHandlerFactoryImpl implements SignClickHandlerFactory {

    private final NamespacedKey signTypeNamespacedKey;
    private final Map<String, SignClickHandler> handlers;

    public SignClickHandlerFactoryImpl(NamespacedKey signTypeNamespacedKey, Map<String, SignClickHandler> handlers) {
        this.signTypeNamespacedKey = signTypeNamespacedKey;
        this.handlers = handlers;
    }

    @Override
    public SignClickHandler create(Sign sign) {
        PersistentDataContainer container = sign.getPersistentDataContainer();
        if (!container.has(signTypeNamespacedKey, PersistentDataType.STRING)) {
            throw new IllegalArgumentException("Sign has no sign type key.");
        }

        String signTypeKey = container.get(signTypeNamespacedKey, PersistentDataType.STRING);

        SignClickHandler handler = handlers.get(signTypeKey);
        if (handler == null) {
            throw new IllegalArgumentException("Handler for sign type " + signTypeKey + " not found.");
        }
        return handler;
    }

}
