package de.sotterbeck.iumetro.infra.papermc.faregate;

import de.sotterbeck.iumetro.app.common.PositionDto;
import de.sotterbeck.iumetro.app.faregate.GateControlAdapter;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.Openable;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.block.BlockMock;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
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
        var position = new PositionDto(0, 0, 0);
        when(world.getBlockAt(0, 0, 0)).thenReturn(new BlockMock(Material.STONE));

        assertThrows(IllegalArgumentException.class, () -> gateControlAdapter.openGate(position, () -> {
        }));
    }

    @Test
    void isGateOpen_ShouldReturnFalse_WhenNoGateIsAtPosition() {
        var position = new PositionDto(0, 0, 0);
        when(world.getBlockAt(0, 0, 0)).thenReturn(new BlockMock(Material.STONE));

        var result = gateControlAdapter.isGateOpen(position);

        assertThat(result).isFalse();
    }

    @Test
    void isGateOpen_ShouldReturnFalse_WhenGateIsClosed() {
        var position = new PositionDto(0, 0, 0);
        var blockMock = new BlockMock(Material.OAK_FENCE_GATE);
        Openable openable = (Openable) blockMock.getBlockData();
        openable.setOpen(false);
        blockMock.setBlockData(openable);
        when(world.getBlockAt(0, 0, 0)).thenReturn(blockMock);

        var result = gateControlAdapter.isGateOpen(position);

        assertThat(result).isFalse();
    }

    @Test
    void isGateOpen_ShouldReturnTrue_WhenGateIsOpen() {
        var position = new PositionDto(0, 0, 0);
        var blockMock = new BlockMock(Material.OAK_FENCE_GATE);
        Openable openable = (Openable) blockMock.getBlockData();
        openable.setOpen(true);
        blockMock.setBlockData(openable);
        when(world.getBlockAt(0, 0, 0)).thenReturn(blockMock);

        var result = gateControlAdapter.isGateOpen(position);

        assertThat(result).isTrue();
    }

}
