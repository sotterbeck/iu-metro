package de.sotterbeck.iumetro.infra.papermc.faregate;

import de.sotterbeck.iumetro.app.common.PositionDto;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import static org.assertj.core.api.Assertions.assertThat;

class SpigotPlayerRepositoryTest {

    private Player player;
    private SpigotPlayerRepository underTest;

    @BeforeEach
    void setUp() {
        ServerMock server = MockBukkit.mock();
        server.addSimpleWorld("world");
        player = server.addPlayer();
        underTest = new SpigotPlayerRepository();
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void teleport_ShouldMovePlayerToTargetPosition_WhenPlayerExists() {
        PositionDto target = new PositionDto(12, 70, -5);

        underTest.teleport(player.getUniqueId(), target);

        assertThat(player.getLocation().getBlockX()).isEqualTo(target.x());
        assertThat(player.getLocation().getBlockY()).isEqualTo(target.y());
        assertThat(player.getLocation().getBlockZ()).isEqualTo(target.z());
    }

}
