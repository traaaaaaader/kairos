package ru.trader.kairos.channel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.trader.kairos.server.Server;
import ru.trader.kairos.server.ServerReader;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChannelServiceTest {

    @Mock
    private ChannelReader channelReader;

    @Mock
    private ServerReader serverReader;

    @Mock
    private ChannelRepository channelRepository;

    @InjectMocks
    private ChannelService channelService;

    @Test
    void createChannel_shouldCreateChannel() {
        CreateChannelInput input = new CreateChannelInput(1L, "General", ChannelType.TEXT);
        Long userId = 1L;

        Server server = new Server();
        server.setId(1L);
        server.setOwnerId(userId);

        Channel savedChannel = new Channel();
        savedChannel.setId(1L);
        savedChannel.setName("General");
        savedChannel.setType(ChannelType.TEXT);
        savedChannel.setPosition(0);

        when(serverReader.findById(1L)).thenReturn(Optional.of(server));
        when(channelReader.getNextPosition(1L)).thenReturn(0);
        when(channelRepository.save(any(Channel.class))).thenReturn(savedChannel);

        Channel result = channelService.createChannel(input, userId);

        assertThat(result.getName()).isEqualTo("General");
        assertThat(result.getType()).isEqualTo(ChannelType.TEXT);
        verify(channelRepository).save(any(Channel.class));
    }

    @Test
    void createChannel_shouldThrowWhenNotOwner() {
        CreateChannelInput input = new CreateChannelInput(1L, "General", ChannelType.TEXT);
        Long userId = 2L;

        Server server = new Server();
        server.setId(1L);
        server.setOwnerId(1L);

        when(serverReader.findById(1L)).thenReturn(Optional.of(server));

        assertThatThrownBy(() -> channelService.createChannel(input, userId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Only owner can create channels");
    }

    @Test
    void getChannels_shouldReturnChannelsWhenMember() {
        Long serverId = 1L;
        Long userId = 1L;

        Channel channel = new Channel();
        channel.setId(1L);
        channel.setName("General");

        when(serverReader.isMember(serverId, userId)).thenReturn(true);
        when(channelReader.findByServerId(serverId)).thenReturn(List.of(channel));

        List<Channel> result = channelService.getChannels(serverId, userId);

        assertThat(result).hasSize(1);
    }

    @Test
    void getChannels_shouldThrowWhenNotMember() {
        Long serverId = 1L;
        Long userId = 1L;

        when(serverReader.isMember(serverId, userId)).thenReturn(false);
        when(serverReader.isOwner(serverId, userId)).thenReturn(false);

        assertThatThrownBy(() -> channelService.getChannels(serverId, userId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Access denied");
    }
}
