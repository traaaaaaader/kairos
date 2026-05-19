package ru.trader.kairos.invite;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import ru.trader.kairos.security.CurrentUser;
import ru.trader.kairos.server.Server;

import java.time.OffsetDateTime;

@Controller
@RequiredArgsConstructor
public class InviteController {

    private final InviteService inviteService;
    private final CurrentUser currentUser;
    private final HttpServletRequest request;

    @MutationMapping
    public ServerInvite createInvite(@Argument CreateInviteInput input) {
        Long userId = currentUser.getId(request);

        OffsetDateTime expiresAt = input.expiresAt() != null
                ? OffsetDateTime.parse(input.expiresAt())
                : null;

        return inviteService.createInvite(
                input.serverId(),
                userId,
                expiresAt,
                input.maxUses()
        );
    }

    @MutationMapping
    public Server joinServer(@Argument String code) {
        Long userId = currentUser.getId(request);
        return inviteService.joinByInvite(code, userId);
    }
}
