package ru.trader.kairos.server;

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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServerServiceTest {

    @Mock
    private ServerReader serverReader;

    @Mock
    private ServerRepository serverRepository;

    @Mock
    private ServerMemberRepository memberRepository;

    @InjectMocks
    private ServerService serverService;

    @Test
    void createServer_shouldCreateServerAndMember() {
        CreateServerInput input = new CreateServerInput("Test Server", null);
        Long ownerId = 1L;

        Server savedServer = new Server();
        savedServer.setId(1L);
        savedServer.setName("Test Server");
        savedServer.setOwnerId(ownerId);

        when(serverRepository.save(any(Server.class))).thenReturn(savedServer);
        when(memberRepository.save(any(ServerMember.class))).thenReturn(new ServerMember());

        Server result = serverService.createServer(input, ownerId);

        assertThat(result.getName()).isEqualTo("Test Server");
        assertThat(result.getOwnerId()).isEqualTo(ownerId);
        verify(serverRepository).save(any(Server.class));
        verify(memberRepository).save(any(ServerMember.class));
    }

    @Test
    void updateServer_shouldUpdateName() {
        Long serverId = 1L;
        Long userId = 1L;
        UpdateServerInput input = new UpdateServerInput("New Name", null);

        Server server = new Server();
        server.setId(serverId);
        server.setName("Old Name");
        server.setOwnerId(userId);

        when(serverReader.findById(serverId)).thenReturn(Optional.of(server));
        when(serverRepository.save(any(Server.class))).thenReturn(server);

        Server result = serverService.updateServer(serverId, input, userId);

        assertThat(result.getName()).isEqualTo("New Name");
    }

    @Test
    void updateServer_shouldThrowWhenNotOwner() {
        Long serverId = 1L;
        Long userId = 2L;
        UpdateServerInput input = new UpdateServerInput("New Name", null);

        Server server = new Server();
        server.setId(serverId);
        server.setOwnerId(1L);

        when(serverReader.findById(serverId)).thenReturn(Optional.of(server));

        assertThatThrownBy(() -> serverService.updateServer(serverId, input, userId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Only owner can update server");
    }

    @Test
    void deleteServer_shouldDeleteWhenOwner() {
        Long serverId = 1L;
        Long userId = 1L;

        Server server = new Server();
        server.setId(serverId);
        server.setOwnerId(userId);

        when(serverReader.findById(serverId)).thenReturn(Optional.of(server));

        boolean result = serverService.deleteServer(serverId, userId);

        assertThat(result).isTrue();
        verify(serverRepository).delete(server);
    }

    @Test
    void getMyServers_shouldReturnServers() {
        Long userId = 1L;
        Server server = new Server();
        server.setId(1L);
        server.setOwnerId(userId);

        when(serverReader.findByUserId(userId)).thenReturn(List.of(server));

        List<Server> result = serverService.getMyServers(userId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getOwnerId()).isEqualTo(userId);
    }

    @Test
    void leaveServer_shouldRemoveMember() {
        Long serverId = 1L;
        Long userId = 2L;
        Long ownerId = 1L;

        Server server = new Server();
        server.setId(serverId);
        server.setOwnerId(ownerId);

        when(serverReader.findById(serverId)).thenReturn(Optional.of(server));
        when(serverReader.isMember(serverId, userId)).thenReturn(true);

        boolean result = serverService.leaveServer(serverId, userId);

        assertThat(result).isTrue();
        verify(memberRepository).deleteById(new ServerMemberId(serverId, userId));
    }

    @Test
    void leaveServer_shouldThrowWhenOwner() {
        Long serverId = 1L;
        Long userId = 1L;

        Server server = new Server();
        server.setId(serverId);
        server.setOwnerId(userId);

        when(serverReader.findById(serverId)).thenReturn(Optional.of(server));

        assertThatThrownBy(() -> serverService.leaveServer(serverId, userId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Owner cannot leave");
    }

    @Test
    void leaveServer_shouldThrowWhenNotMember() {
        Long serverId = 1L;
        Long userId = 2L;
        Long ownerId = 1L;

        Server server = new Server();
        server.setId(serverId);
        server.setOwnerId(ownerId);

        when(serverReader.findById(serverId)).thenReturn(Optional.of(server));
        when(serverReader.isMember(serverId, userId)).thenReturn(false);

        assertThatThrownBy(() -> serverService.leaveServer(serverId, userId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Not a member");
    }
}
