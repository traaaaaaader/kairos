package ru.trader.kairos.server;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.trader.kairos.member.ServerMember;
import ru.trader.kairos.member.ServerMemberId;
import ru.trader.kairos.member.ServerMemberRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServerService {

    private final ServerReader serverReader;
    private final ServerRepository serverRepository;
    private final ServerMemberRepository memberRepository;

    @Transactional
    public Server createServer(CreateServerInput input, Long ownerId) {
        Server server = new Server();
        server.setName(input.name());
        server.setOwnerId(ownerId);
        server.setIconUrl(input.iconUrl());
        Server saved = serverRepository.save(server);

        ServerMember member = new ServerMember();
        member.setId(new ServerMemberId(saved.getId(), ownerId));
        member.setServer(saved);
        memberRepository.save(member);

        return saved;
    }

    @Transactional
    public Server updateServer(Long serverId, UpdateServerInput input, Long userId) {
        Server server = serverReader.findById(serverId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Server not found"));

        if (!server.getOwnerId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only owner can update server");
        }

        if (input.name() != null) server.setName(input.name());
        if (input.iconUrl() != null) server.setIconUrl(input.iconUrl());
        return serverRepository.save(server);
    }

    @Transactional
    public boolean deleteServer(Long serverId, Long userId) {
        Server server = serverReader.findById(serverId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Server not found"));

        if (!server.getOwnerId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only owner can delete server");
        }

        serverRepository.delete(server);
        return true;
    }

    @Transactional(readOnly = true)
    public Server getServer(Long serverId, Long userId) {
        Server server = serverReader.findById(serverId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Server not found"));

        if (!serverReader.isMember(serverId, userId) && !server.getOwnerId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        return server;
    }

    @Transactional(readOnly = true)
    public List<Server> getMyServers(Long userId) {
        return serverReader.findByUserId(userId);
    }

    @Transactional
    public boolean leaveServer(Long serverId, Long userId) {
        Server server = serverReader.findById(serverId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Server not found"));

        if (server.getOwnerId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Owner cannot leave the server. Delete it instead.");
        }

        if (!serverReader.isMember(serverId, userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not a member of this server");
        }

        ServerMemberId memberId = new ServerMemberId(serverId, userId);
        memberRepository.deleteById(memberId);
        return true;
    }
}
