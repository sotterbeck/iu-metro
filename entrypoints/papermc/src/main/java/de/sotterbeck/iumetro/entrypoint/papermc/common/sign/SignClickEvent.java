package de.sotterbeck.iumetro.entrypoint.papermc.common.sign;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public record SignClickEvent(Sign sign, Player player) {

}
