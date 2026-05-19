package ru.trader.kairos.server;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.trader.kairos.server.QServer;
import ru.trader.kairos.member.QServerMember;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ServerReader {

    private final JPAQueryFactory queryFactory;
    private final QServer server = QServer.server;
    private final QServerMember member = QServerMember.serverMember;

    public ServerReader(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    public Optional<Server> findById(Long id) {
        return Optional.ofNullable(
                queryFactory.selectFrom(server)
                        .where(server.id.eq(id))
                        .fetchOne()
        );
    }

    public List<Server> findByUserId(Long userId) {
        return queryFactory.selectFrom(server)
                .leftJoin(server.members, member)
                .where(
                        server.ownerId.eq(userId)
                                .or(member.id.userId.eq(userId))
                )
                .distinct()
                .fetch();
    }

    public boolean isMember(Long serverId, Long userId) {
        return queryFactory.selectFrom(server)
                .join(server.members, member)
                .where(
                        server.id.eq(serverId)
                                .and(member.id.userId.eq(userId))
                )
                .fetchFirst() != null;
    }

    public boolean isOwner(Long serverId, Long userId) {
        return queryFactory.selectFrom(server)
                .where(
                        server.id.eq(serverId)
                                .and(server.ownerId.eq(userId))
                )
                .fetchFirst() != null;
    }
}
