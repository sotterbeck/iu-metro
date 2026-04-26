package de.sotterbeck.iumetro.infra.papermc.auth;

import de.sotterbeck.iumetro.app.auth.TokenProvider;
import de.sotterbeck.iumetro.app.auth.TokenRevocationService;
import de.sotterbeck.iumetro.app.auth.TokenValidationResult;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAccessManagerTest {

    private static final UUID USER_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    private static final String USER_NAME = "TestPlayer";
    private static final String ROLE = "player";
    private static final String JTI = "test-jti";
    private static final String VALID_TOKEN = "valid.jwt.token";

    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private TokenRevocationService tokenRevocationService;

    @Mock
    private Context ctx;

    private JwtAccessManager underTest;

    @BeforeEach
    void setUp() {
        underTest = new JwtAccessManager(tokenProvider, tokenRevocationService);
    }

    @Test
    void shouldAllowRequest_whenRouteHasNoRoles() {
        when(ctx.routeRoles()).thenReturn(Set.of());

        underTest.handle(ctx);

        verify(ctx, never()).header(any());
        verify(tokenProvider, never()).validate(any());
    }

    @Test
    void shouldAllowRequest_whenRouteOnlyHasAnyoneRole() {
        when(ctx.routeRoles()).thenReturn(Set.of(Role.ANYONE));

        underTest.handle(ctx);

        verify(ctx, never()).header(any());
        verify(tokenProvider, never()).validate(any());
    }

    @Test
    void shouldDenyRequest_whenAuthorizationHeaderIsMissing() {
        when(ctx.routeRoles()).thenReturn(Set.of(Role.AUTHENTICATED));
        when(ctx.header("Authorization")).thenReturn(null);

        underTest.handle(ctx);

        verify(ctx).status(HttpStatus.UNAUTHORIZED);
        verify(ctx).skipRemainingHandlers();
    }

    @Test
    void shouldDenyRequest_whenTokenIsInvalid() {
        when(ctx.routeRoles()).thenReturn(Set.of(Role.AUTHENTICATED));
        when(ctx.header("Authorization")).thenReturn("Bearer invalid-token");
        when(tokenProvider.validate("invalid-token")).thenReturn(new TokenValidationResult.Invalid());

        underTest.handle(ctx);

        verify(ctx).status(HttpStatus.UNAUTHORIZED);
        verify(ctx).skipRemainingHandlers();
    }

    @Test
    void shouldDenyRequest_whenTokenIsRevoked() {
        when(ctx.routeRoles()).thenReturn(Set.of(Role.AUTHENTICATED));
        when(ctx.header("Authorization")).thenReturn("Bearer " + VALID_TOKEN);
        when(tokenProvider.validate(VALID_TOKEN)).thenReturn(new TokenValidationResult.Success(USER_ID, USER_NAME, ROLE, JTI));
        when(tokenRevocationService.isRevoked(JTI)).thenReturn(true);

        underTest.handle(ctx);

        verify(ctx).status(HttpStatus.UNAUTHORIZED);
        verify(ctx).skipRemainingHandlers();
        verify(tokenRevocationService).isRevoked(JTI);
    }

    @Test
    void shouldAllowRequest_whenTokenIsValidAndNotRevoked() {
        when(ctx.routeRoles()).thenReturn(Set.of(Role.AUTHENTICATED));
        when(ctx.header("Authorization")).thenReturn("Bearer " + VALID_TOKEN);
        when(tokenProvider.validate(VALID_TOKEN)).thenReturn(new TokenValidationResult.Success(USER_ID, USER_NAME, ROLE, JTI));
        when(tokenRevocationService.isRevoked(JTI)).thenReturn(false);

        underTest.handle(ctx);

        verify(ctx, never()).status(any());
        verify(ctx, never()).skipRemainingHandlers();

        ArgumentCaptor<UUID> userIdCaptor = ArgumentCaptor.forClass(UUID.class);
        verify(ctx).attribute(eq("userId"), userIdCaptor.capture());
        assertThat(userIdCaptor.getValue()).isEqualTo(USER_ID);

        verify(ctx).attribute("userName", USER_NAME);
        verify(ctx).attribute("role", ROLE);
    }

    @Test
    void shouldDenyRequest_whenUserDoesNotHaveRequiredRole() {
        when(ctx.routeRoles()).thenReturn(Set.of(Role.ADMIN));
        when(ctx.header("Authorization")).thenReturn("Bearer " + VALID_TOKEN);
        when(tokenProvider.validate(VALID_TOKEN)).thenReturn(new TokenValidationResult.Success(USER_ID, USER_NAME, ROLE, JTI));
        when(tokenRevocationService.isRevoked(JTI)).thenReturn(false);

        underTest.handle(ctx);

        verify(ctx).status(HttpStatus.FORBIDDEN);
        verify(ctx).skipRemainingHandlers();
    }

}
