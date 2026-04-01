package de.sotterbeck.iumetro.infra.papermc.faregate;

import de.sotterbeck.iumetro.app.common.PositionDto;
import de.sotterbeck.iumetro.app.faregate.GateDto;
import org.bukkit.Material;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.block.BlockMock;
import org.mockbukkit.mockbukkit.world.Coordinate;
import org.mockbukkit.mockbukkit.world.WorldMock;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class SpigotGateRepositoryTest {

    private WorldMock world;
    private SpigotGateRepository gateRepository;

    @BeforeEach
    void setUp() {
        ServerMock server = MockBukkit.mock();
        world = server.addSimpleWorld("world");

        gateRepository = new SpigotGateRepository(world);
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void findAt_ShouldReturnEmptyOptional_WhenNoGateExistsAtPosition() {
        PositionDto position = new PositionDto(0, 0, 0);

        Optional<GateDto> result = gateRepository.findAt(position);

        assertThat(result).isEmpty();
    }

    @Test
    void findAt_ShouldReturnGate_WhenGateExitsAtPosition() {
        PositionDto position = new PositionDto(0, 0, 0);
        BlockMock block = world.createBlock(new Coordinate(0, 0, 0));
        block.setType(Material.OAK_FENCE_GATE);

        Optional<GateDto> result = gateRepository.findAt(position);

        assertThat(result).isNotEmpty();
    }

}
