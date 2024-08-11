package de.sotterbeck.iumetro.entrypoint.papermc.faregate.sign;

import de.sotterbeck.iumetro.entrypoint.papermc.common.sign.SignTypeKeyFactory;
import de.sotterbeck.iumetro.entrypoint.papermc.faregate.FareGateKeyFactory;
import de.sotterbeck.iumetro.usecase.faregate.FareGateDto;
import de.sotterbeck.iumetro.usecase.faregate.FareGateSignRepository;
import de.sotterbeck.iumetro.usecase.faregate.PositionDto;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Optional;

public class SpigotFareGateSignRepository implements FareGateSignRepository {

    private final World world;
    private final SignTypeKeyFactory signTypeKeyFactory;
    private final FareGateKeyFactory fareGateKeyFactory;

    public SpigotFareGateSignRepository(World world, SignTypeKeyFactory signTypeKeyFactory, FareGateKeyFactory fareGateKeyFactory) {
        this.world = world;
        this.signTypeKeyFactory = signTypeKeyFactory;
        this.fareGateKeyFactory = fareGateKeyFactory;
    }

    @Override
    public Optional<FareGateDto> findAt(PositionDto location) {
        Location worldLocation = new Location(world, location.x(), location.y(), location.z());
        Block block = worldLocation.getBlock();
        if (!(block.getState() instanceof Sign sign)) {
            return Optional.empty();
        }

        PersistentDataContainer container = sign.getPersistentDataContainer();
        if (!container.has(signTypeKeyFactory.getSignTypeNamespacedKey(), PersistentDataType.STRING)) {
            return Optional.empty();
        }


        String type = container.get(fareGateKeyFactory.getFareGateTypeKey(), PersistentDataType.STRING);
        String stationId = container.get(fareGateKeyFactory.getStationKey(), PersistentDataType.STRING);
        FareGateDto fareGateDto = new FareGateDto(location, type, stationId);

        return Optional.of(fareGateDto);
    }

}
