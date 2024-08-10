package de.sotterbeck.iumetro.entrypoint.papermc.faregate.sign;

import be.seeseemelk.mockbukkit.Coordinate;
import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.block.BlockMock;
import be.seeseemelk.mockbukkit.block.state.SignMock;
import de.sotterbeck.iumetro.entrypoint.papermc.common.sign.SignTypeKeyFactory;
import de.sotterbeck.iumetro.entrypoint.papermc.faregate.FareGateKeyFactory;
import de.sotterbeck.iumetro.usecase.faregate.FareGateDto;
import de.sotterbeck.iumetro.usecase.faregate.PositionDto;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpigotFareGateSignRepositoryTest {

    private WorldMock world;
    private SpigotFareGateSignRepository repository;

    @Mock
    private SignTypeKeyFactory signTypeKeyFactory;
    @Mock
    private FareGateKeyFactory fareGateKeyFactory;

    private NamespacedKey signTypeKey;
    private NamespacedKey fareGateTypeKey;
    private NamespacedKey stationNameKey;

    @BeforeEach
    void setUp() {
        ServerMock server = MockBukkit.mock();
        world = server.addSimpleWorld("world");
        repository = new SpigotFareGateSignRepository(world, signTypeKeyFactory, fareGateKeyFactory);
        signTypeKey = new NamespacedKey("iumetro", "sign_type");
        fareGateTypeKey = new NamespacedKey("iumetro", "faregate_type");
        stationNameKey = new NamespacedKey("iumetro", "station_name");
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void findAt_ShouldReturnEmptyOptional_WhenBlockIsNotSign() {
        PositionDto position = new PositionDto(0, 0, 0);
        BlockMock block = world.createBlock(new Coordinate(0, 0, 0));
        block.setType(Material.STONE);

        Optional<FareGateDto> result = repository.findAt(position);

        assertThat(result).isEmpty();
    }

    @Test
    void findAt_ShouldReturnEmptyOptional_WhenSignDoesNotHaveSignTypeKey() {
        PositionDto position = new PositionDto(0, 0, 0);
        BlockMock block = world.createBlock(new Coordinate(0, 0, 0));
        block.setType(Material.OAK_SIGN);

        Optional<FareGateDto> result = repository.findAt(position);

        assertThat(result).isEmpty();
    }

    @Test
    void findAt_ShouldReturnFareGateDto_WhenValidFareGateSignExists() {
        PositionDto position = new PositionDto(0, 0, 0);
        BlockMock block = world.createBlock(new Coordinate(0, 0, 0));
        block.setType(Material.OAK_SIGN);
        SignMock sign = (SignMock) block.getState();
        when(signTypeKeyFactory.getSignTypeNamespacedKey()).thenReturn(signTypeKey);
        when(fareGateKeyFactory.getFareGateTypeKey()).thenReturn(fareGateTypeKey);
        when(fareGateKeyFactory.getStationNameKey()).thenReturn(stationNameKey);
        setupPersistentDataContainer(sign);

        Optional<FareGateDto> result = repository.findAt(position);

        assertThat(result).isPresent();
        assertThat(result.get().type()).isEqualTo("entry");
        assertThat(result.get().stationName()).isEqualTo("Central Station");
    }

    private void setupPersistentDataContainer(SignMock sign) {
        PersistentDataContainer container = sign.getPersistentDataContainer();
        container.set(signTypeKey, PersistentDataType.STRING, "faregate_entry");
        container.set(fareGateTypeKey, PersistentDataType.STRING, "entry");
        container.set(stationNameKey, PersistentDataType.STRING, "Central Station");
        sign.update();
    }

}
