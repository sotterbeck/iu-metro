package de.sotterbeck.iumetro.infra.papermc.faregate;

import be.seeseemelk.mockbukkit.Coordinate;
import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.block.BlockMock;
import de.sotterbeck.iumetro.app.common.PositionDto;
import de.sotterbeck.iumetro.app.faregate.GateDto;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SoundGroup;
import org.bukkit.block.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Gate;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

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
        block.setBlockData(new FakeGate());

        Optional<GateDto> result = gateRepository.findAt(position);

        assertThat(result).isNotEmpty();
    }

    private static class FakeGate implements Gate {

        @Override
        public boolean isInWall() {
            return false;
        }

        @Override
        public void setInWall(boolean inWall) {

        }

        @Override
        public @NotNull BlockFace getFacing() {
            return BlockFace.NORTH;
        }

        @Override
        public void setFacing(@NotNull BlockFace facing) {

        }

        @Override
        public @NotNull Set<BlockFace> getFaces() {
            return Set.of();
        }

        @Override
        public boolean isOpen() {
            return false;
        }

        @Override
        public void setOpen(boolean open) {

        }

        @Override
        public boolean isPowered() {
            return false;
        }

        @Override
        public void setPowered(boolean powered) {

        }

        @Override
        public @NotNull Material getMaterial() {
            return null;
        }

        @Override
        public @NotNull String getAsString() {
            return "";
        }

        @Override
        public @NotNull String getAsString(boolean hideUnspecified) {
            return "";
        }

        @Override
        public @NotNull BlockData merge(@NotNull BlockData data) {
            return null;
        }

        @Override
        public boolean matches(@Nullable BlockData data) {
            return false;
        }

        @Override
        public @NotNull BlockData clone() {
            return null;
        }

        @Override
        public @NotNull SoundGroup getSoundGroup() {
            return null;
        }

        @Override
        public int getLightEmission() {
            return 0;
        }

        @Override
        public boolean isOccluding() {
            return false;
        }

        @Override
        public boolean requiresCorrectToolForDrops() {
            return false;
        }

        @Override
        public boolean isPreferredTool(@NotNull ItemStack tool) {
            return false;
        }

        @Override
        public @NotNull PistonMoveReaction getPistonMoveReaction() {
            return null;
        }

        @Override
        public boolean isSupported(@NotNull Block block) {
            return false;
        }

        @Override
        public boolean isSupported(@NotNull Location location) {
            return false;
        }

        @Override
        public boolean isFaceSturdy(@NotNull BlockFace face, @NotNull BlockSupport support) {
            return false;
        }

        @Override
        public @NotNull VoxelShape getCollisionShape(@NotNull Location location) {
            return null;
        }

        @Override
        public @NotNull Color getMapColor() {
            return null;
        }

        @Override
        public @NotNull Material getPlacementMaterial() {
            return null;
        }

        @Override
        public void rotate(@NotNull StructureRotation rotation) {

        }

        @Override
        public void mirror(@NotNull Mirror mirror) {

        }

        @Override
        public void copyTo(@NotNull BlockData other) {

        }

        @Override
        public @NotNull BlockState createBlockState() {
            return null;
        }

        @Override
        public float getDestroySpeed(@NotNull ItemStack itemStack, boolean considerEnchants) {
            return 0;
        }

        @Override
        public boolean isRandomlyTicked() {
            return false;
        }

    }

}