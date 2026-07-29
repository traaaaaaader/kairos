package ru.trader.kairos.invite;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class InviteReader {

    private final JPAQueryFactory queryFactory;
    private final QServerInvite invite = QServerInvite.serverInvite;

    public InviteReader(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    public Optional<ServerInvite> findByCode(String code) {
        return Optional.ofNullable(
                queryFactory.selectFrom(invite)
                        .where(invite.code.eq(code))
                        .fetchOne()
        );
    }

    public boolean existsByCode(String code) {
        return queryFactory.selectFrom(invite)
                .where(invite.code.eq(code))
                .fetchFirst() != null;
    }
}
