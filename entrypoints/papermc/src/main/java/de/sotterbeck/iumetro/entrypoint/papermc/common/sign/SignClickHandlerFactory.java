package de.sotterbeck.iumetro.entrypoint.papermc.common.sign;

import org.bukkit.block.Sign;

public interface SignClickHandlerFactory {

    SignClickHandler create(Sign sign);

}
