package de.sotterbeck.iumetro.infra.papermc.faregate.sign;

import be.seeseemelk.mockbukkit.Coordinate;
import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.block.BlockMock;
import be.seeseemelk.mockbukkit.block.state.SignMock;
import de.sotterbeck.iumetro.app.common.PositionDto;
import de.sotterbeck.iumetro.app.faregate.FareGateDto;
import de.sotterbeck.iumetro.infra.papermc.common.sign.SignTypeKeyFactory;
import de.sotterbeck.iumetro.infra.papermc.faregate.FareGateKeyFactory;
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
    private NamespacedKey stationKey;

    @BeforeEach
    void setUp() {
        ServerMock server = MockBukkit.mock();
        world = server.addSimpleWorld("world");
        repository = new SpigotFareGateSignRepository(world, signTypeKeyFactory, fareGateKeyFactory);
        signTypeKey = new NamespacedKey("iumetro", "sign_type");
        fareGateTypeKey = new NamespacedKey("iumetro", "faregate_type");
        stationKey = new NamespacedKey("iumetro", "station");
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
        when(fareGateKeyFactory.getStationKey()).thenReturn(stationKey);
        setupPersistentDataContainer(sign);

        Optional<FareGateDto> result = repository.findAt(position);

        assertThat(result).isPresent();
        assertThat(result.get().type()).isEqualTo("entry");
        assertThat(result.get().stationId()).isEqualTo("bb97848b-0195-4cc4-a9c9-ab1573f09821");
    }

    private void setupPersistentDataContainer(SignMock sign) {
        PersistentDataContainer container = sign.getPersistentDataContainer();
        container.set(signTypeKey, PersistentDataType.STRING, "faregate_entry");
        container.set(fareGateTypeKey, PersistentDataType.STRING, "entry");
        container.set(stationKey, PersistentDataType.STRING, "bb97848b-0195-4cc4-a9c9-ab1573f09821");
        sign.update();
    }

}
