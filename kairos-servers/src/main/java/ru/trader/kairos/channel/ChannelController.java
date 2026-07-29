package ru.trader.kairos.channel;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import ru.trader.kairos.security.CurrentUser;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;
    private final CurrentUser currentUser;
    private final HttpServletRequest request;

    @QueryMapping
    public List<Channel> channels(@Argument Long serverId) {
        Long userId = currentUser.getId(request);
        return channelService.getChannels(serverId, userId);
    }

    @MutationMapping
    public Channel createChannel(@Argument @Valid CreateChannelInput input) {
        Long userId = currentUser.getId(request);
        return channelService.createChannel(input, userId);
    }

    @MutationMapping
    public Channel updateChannel(@Argument Long id, @Argument @Valid UpdateChannelInput input) {
        Long userId = currentUser.getId(request);
        return channelService.updateChannel(id, input, userId);
    }

    @MutationMapping
    public boolean deleteChannel(@Argument Long id) {
        Long userId = currentUser.getId(request);
        return channelService.deleteChannel(id, userId);
    }
}
