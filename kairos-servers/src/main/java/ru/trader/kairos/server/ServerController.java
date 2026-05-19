package ru.trader.kairos.server;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import ru.trader.kairos.security.CurrentUser;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ServerController {

    private final ServerService serverService;
    private final CurrentUser currentUser;
    private final HttpServletRequest request;

    @QueryMapping
    public Server server(@Argument Long id) {
        Long userId = currentUser.getId(request);
        return serverService.getServer(id, userId);
    }

    @QueryMapping
    public List<Server> myServers() {
        Long userId = currentUser.getId(request);
        return serverService.getMyServers(userId);
    }

    @MutationMapping
    public Server createServer(@Argument CreateServerInput input) {
        Long userId = currentUser.getId(request);
        return serverService.createServer(input, userId);
    }

    @MutationMapping
    public Server updateServer(@Argument Long id, @Argument UpdateServerInput input) {
        Long userId = currentUser.getId(request);
        return serverService.updateServer(id, input, userId);
    }

    @MutationMapping
    public boolean deleteServer(@Argument Long id) {
        Long userId = currentUser.getId(request);
        return serverService.deleteServer(id, userId);
    }

    @MutationMapping
    public boolean leaveServer(@Argument Long id) {
        Long userId = currentUser.getId(request);
        return serverService.leaveServer(id, userId);
    }
}
