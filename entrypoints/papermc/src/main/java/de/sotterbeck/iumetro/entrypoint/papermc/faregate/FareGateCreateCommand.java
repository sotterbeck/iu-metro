package de.sotterbeck.iumetro.entrypoint.papermc.faregate;

import de.sotterbeck.iumetro.entrypoint.papermc.common.AnnotatedCommand;
import de.sotterbeck.iumetro.entrypoint.papermc.faregate.sign.FareGateSignItemCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

public class FareGateCreateCommand implements AnnotatedCommand {

    private final FareGateSignItemCreator signItemCreator;

    public FareGateCreateCommand(FareGateSignItemCreator signItemCreator) {
        this.signItemCreator = signItemCreator;
    }

    @Command("faregate create <type> <stationName>")
    @Permission("iumetro.faregate.create")
    public void fareGateCreate(
            CommandSender sender,
            @Argument(value = "type") FareGateType type,
            @Argument(value = "stationName") String stationName
    ) {
        if (!(sender instanceof Player player)) {
            sender.sendRichMessage("<red>You must be a player to use this command.");
            return;
        }
        ItemStack item = signItemCreator.createItem(type.name(), stationName);

        player.getInventory().addItem(item);
        player.sendRichMessage("<green>Created " + type.name() + " sign for station " + stationName + ".");
    }

    enum FareGateType {
        ENTRY, EXIT
    }

}
