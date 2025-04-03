package de.sotterbeck.iumetro.infra.papermc.faregate;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.block.BlockMock;
import de.sotterbeck.iumetro.app.common.PositionDto;
import de.sotterbeck.iumetro.app.faregate.GateControlAdapter;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpigotGateControlAdapterTest {

    @Mock
    private World world;
    private GateControlAdapter gateControlAdapter;

    @BeforeEach
    void setUp() {
        MockBukkit.mock();
        Plugin plugin = MockBukkit.createMockPlugin();
        gateControlAdapter = new SpigotGateControlAdapter(world, plugin);
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void openGate_ShouldThrowError_WhenNoGateIsAtPosition() {
        PositionDto position = new PositionDto(0, 0, 0);
        when(world.getBlockAt(0, 0, 0)).thenReturn(new BlockMock(Material.STONE));

        assertThrows(IllegalArgumentException.class, () -> gateControlAdapter.openGate(position));
    }

}