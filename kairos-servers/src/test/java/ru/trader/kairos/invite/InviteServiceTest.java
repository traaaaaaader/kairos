package ru.trader.kairos.invite;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.trader.kairos.member.ServerMember;
import ru.trader.kairos.member.ServerMemberId;
import ru.trader.kairos.member.ServerMemberRepository;
import ru.trader.kairos.server.Server;
import ru.trader.kairos.server.ServerReader;
import ru.trader.kairos.server.ServerRepository;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InviteServiceTest {

    @Mock
    private InviteReader inviteReader;

    @Mock
    private ServerReader serverReader;

    @Mock
    private ServerRepository serverRepository;

    @Mock
    private ServerMemberRepository memberRepository;

    @Mock
    private ServerInviteRepository inviteRepository;

    @InjectMocks
    private InviteService inviteService;

    @Test
    void createInvite_shouldCreateInvite() {
        Long serverId = 1L;
        Long userId = 1L;
        OffsetDateTime expiresAt = OffsetDateTime.now().plusDays(7);

        Server server = new Server();
        server.setId(serverId);
        server.setOwnerId(userId);

        ServerInvite savedInvite = new ServerInvite();
        savedInvite.setId(1L);
        savedInvite.setCode("abc12345");
        savedInvite.setServer(server);

        when(serverReader.findById(serverId)).thenReturn(Optional.of(server));
        when(serverReader.isMember(serverId, userId)).thenReturn(true);
        when(inviteReader.existsByCode(anyString())).thenReturn(false);
        when(inviteRepository.save(any(ServerInvite.class))).thenReturn(savedInvite);

        ServerInvite result = inviteService.createInvite(serverId, userId, expiresAt, 5);

        assertThat(result.getCode()).isNotBlank();
        verify(inviteRepository).save(any(ServerInvite.class));
    }

    @Test
    void joinByInvite_shouldJoinServer() {
        String code = "abc12345";
        Long userId = 2L;

        Server server = new Server();
        server.setId(1L);
        server.setOwnerId(1L);

        ServerInvite invite = new ServerInvite();
        invite.setId(1L);
        invite.setCode(code);
        invite.setServer(server);
        invite.setUses(0);
        invite.setMaxUses(5);

        when(inviteReader.findByCode(code)).thenReturn(Optional.of(invite));
        when(serverReader.isMember(1L, userId)).thenReturn(false);
        when(memberRepository.save(any(ServerMember.class))).thenReturn(new ServerMember());
        when(inviteRepository.save(any(ServerInvite.class))).thenReturn(invite);

        Server result = inviteService.joinByInvite(code, userId);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(invite.getUses()).isEqualTo(1);
        verify(memberRepository).save(any(ServerMember.class));
    }

    @Test
    void joinByInvite_shouldThrowWhenExpired() {
        String code = "expired1";
        Long userId = 2L;

        Server server = new Server();
        server.setId(1L);

        ServerInvite invite = new ServerInvite();
        invite.setId(1L);
        invite.setCode(code);
        invite.setServer(server);
        invite.setExpiresAt(OffsetDateTime.now().minusDays(1));
        invite.setUses(0);

        when(inviteReader.findByCode(code)).thenReturn(Optional.of(invite));

        assertThatThrownBy(() -> inviteService.joinByInvite(code, userId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("expired or exhausted");
    }

    @Test
    void joinByInvite_shouldThrowWhenAlreadyMember() {
        String code = "abc12345";
        Long userId = 2L;

        Server server = new Server();
        server.setId(1L);

        ServerInvite invite = new ServerInvite();
        invite.setId(1L);
        invite.setCode(code);
        invite.setServer(server);
        invite.setUses(0);

        when(inviteReader.findByCode(code)).thenReturn(Optional.of(invite));
        when(serverReader.isMember(1L, userId)).thenReturn(true);

        assertThatThrownBy(() -> inviteService.joinByInvite(code, userId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Already a member");
    }
}
