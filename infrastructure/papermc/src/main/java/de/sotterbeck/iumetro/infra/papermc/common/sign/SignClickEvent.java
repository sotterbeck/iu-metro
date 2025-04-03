package de.sotterbeck.iumetro.infra.papermc.common.sign;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public record SignClickEvent(Sign sign, Player player) {

}
