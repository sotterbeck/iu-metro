package de.sotterbeck.iumetro.infra.papermc.auth;

import de.sotterbeck.iumetro.app.auth.MagicLinkService;
import de.sotterbeck.iumetro.infra.papermc.common.CloudAnnotated;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

public class LoginCommand implements CloudAnnotated {

    private static final String ADMIN_PERMISSION = "iumetro.web.role.admin";

    private final MagicLinkService magicLinkService;

    public LoginCommand(MagicLinkService magicLinkService) {
        this.magicLinkService = magicLinkService;
    }

    @Command("login")
    @Permission("iumetro.login")
    public void login(CommandSourceStack source) {
        var sender = source.getSender();
        if (!(sender instanceof Player player)) {
            sender.sendRichMessage("<red>Only players can use this command!");
            return;
        }

        // This can be extracted into the Route enum, but it could be more complex in the case a user has multiple role permissions.
        // So this is a possible refactoring for the future. For now, we keep it simple.
        String role = player.hasPermission(ADMIN_PERMISSION) ? "admin" : "player";
        var result = magicLinkService.generateLink(player.getUniqueId(), player.getName(), role);
        var url = result.url();

        var message = Component.text("[Click here to login]")
                .color(NamedTextColor.GREEN)
                .decorate(TextDecoration.BOLD)
                .clickEvent(ClickEvent.openUrl(url));

        player.sendMessage(message);
    }

}
