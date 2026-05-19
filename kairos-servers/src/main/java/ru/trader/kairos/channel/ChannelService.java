package ru.trader.kairos.channel;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.trader.kairos.server.Server;
import ru.trader.kairos.server.ServerReader;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChannelService {

    private final ChannelReader channelReader;
    private final ServerReader serverReader;
    private final ChannelRepository channelRepository;

    @Transactional
    public Channel createChannel(CreateChannelInput input, Long userId) {
        Server server = serverReader.findById(input.serverId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Server not found"));

        if (!server.getOwnerId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only owner can create channels");
        }

        int position = channelReader.getNextPosition(input.serverId());

        Channel channel = new Channel();
        channel.setServer(server);
        channel.setName(input.name());
        channel.setType(input.type());
        channel.setPosition(position);

        return channelRepository.save(channel);
    }

    @Transactional
    public Channel updateChannel(Long channelId, UpdateChannelInput input, Long userId) {
        Channel channel = channelReader.findById(channelId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Channel not found"));

        if (!channel.getServer().getOwnerId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only owner can update channels");
        }

        if (input.name() != null) channel.setName(input.name());
        if (input.position() != null) channel.setPosition(input.position());

        return channelRepository.save(channel);
    }

    @Transactional
    public boolean deleteChannel(Long channelId, Long userId) {
        Channel channel = channelReader.findById(channelId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Channel not found"));

        if (!channel.getServer().getOwnerId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only owner can delete channels");
        }

        channelRepository.delete(channel);
        return true;
    }

    @Transactional(readOnly = true)
    public List<Channel> getChannels(Long serverId, Long userId) {
        if (!serverReader.isMember(serverId, userId) && !serverReader.isOwner(serverId, userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        return channelReader.findByServerId(serverId);
    }
}
