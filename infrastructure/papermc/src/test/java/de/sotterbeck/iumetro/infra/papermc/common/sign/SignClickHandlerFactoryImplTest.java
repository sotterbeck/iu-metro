package de.sotterbeck.iumetro.infra.papermc.common.sign;

import org.bukkit.NamespacedKey;
import org.bukkit.block.Sign;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SignClickHandlerFactoryImplTest {

    private final SignClickHandler signClickHandlerMock = new SignClickHandler() {

    };

    private final NamespacedKey signTypeKey = new NamespacedKey("test", "sign_type");
    private final SignClickHandlerFactory underTest = new SignClickHandlerFactoryImpl(signTypeKey, Map.of(
            "mock_handler", signClickHandlerMock
    ));

    @Mock
    private Sign sign;

    @Mock
    private PersistentDataContainer container;

    @Test
    void create_ShouldThrowException_WhenSignHasNoType() {
        when(container.has(signTypeKey, PersistentDataType.STRING)).thenReturn(false);
        when(sign.getPersistentDataContainer()).thenReturn(container);

        assertThrows(IllegalArgumentException.class, () -> underTest.create(sign));
    }

    @Test
    void create_ShouldThrowException_WhenTypeHasNoHandler() {
        when(container.has(signTypeKey, PersistentDataType.STRING)).thenReturn(true);
        when(container.get(signTypeKey, PersistentDataType.STRING)).thenReturn("no_handler");
        when(sign.getPersistentDataContainer()).thenReturn(container);

        assertThrows(IllegalArgumentException.class, () -> underTest.create(sign));
    }

    @Test
    void create_ShouldReturnCorrectHandler_WhenTypeHasHandler() {
        when(container.has(signTypeKey, PersistentDataType.STRING)).thenReturn(true);
        when(container.get(signTypeKey, PersistentDataType.STRING)).thenReturn("mock_handler");
        when(sign.getPersistentDataContainer()).thenReturn(container);

        var handler = underTest.create(sign);

        assertThat(handler).isEqualTo(signClickHandlerMock);
    }

}