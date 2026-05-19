package ru.trader.kairos.invite;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.trader.kairos.member.ServerMember;
import ru.trader.kairos.member.ServerMemberId;
import ru.trader.kairos.member.ServerMemberRepository;
import ru.trader.kairos.server.Server;
import ru.trader.kairos.server.ServerReader;
import ru.trader.kairos.server.ServerRepository;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InviteService {

    private final InviteReader inviteReader;
    private final ServerReader serverReader;
    private final ServerRepository serverRepository;
    private final ServerMemberRepository memberRepository;
    private final ServerInviteRepository inviteRepository;

    @Transactional
    public ServerInvite createInvite(Long serverId, Long userId,
                                     OffsetDateTime expiresAt, Integer maxUses) {
        Server server = serverReader.findById(serverId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Server not found"));

        if (!serverReader.isMember(serverId, userId) && !server.getOwnerId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        String code = generateUniqueCode();

        ServerInvite invite = new ServerInvite();
        invite.setServer(server);
        invite.setCode(code);
        invite.setCreatedBy(userId);
        invite.setExpiresAt(expiresAt);
        invite.setMaxUses(maxUses);

        return inviteRepository.save(invite);
    }

    @Transactional
    public Server joinByInvite(String code, Long userId) {
        ServerInvite invite = inviteReader.findByCode(code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invite not found"));

        if (!invite.isValid()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invite is expired or exhausted");
        }

        Long serverId = invite.getServer().getId();

        if (serverReader.isMember(serverId, userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Already a member");
        }

        ServerMember member = new ServerMember();
        member.setId(new ServerMemberId(serverId, userId));
        member.setServer(invite.getServer());
        memberRepository.save(member);

        invite.setUses(invite.getUses() + 1);
        inviteRepository.save(invite);

        return invite.getServer();
    }

    private String generateUniqueCode() {
        String code;
        int attempts = 0;
        int maxAttempts = 10;
        do {
            code = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
            attempts++;
            if (attempts >= maxAttempts) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Failed to generate unique invite code");
            }
        } while (inviteReader.existsByCode(code));
        return code;
    }
}
