package ru.trader.kairos.reader;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.trader.kairos.entity.QUser;
import ru.trader.kairos.entity.User;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserReader {
    private final JPAQueryFactory queryFactory;
    private final QUser user = QUser.user;

    public UserReader(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(
                queryFactory.selectFrom(user)
                        .where(user.email.eq(email))
                        .fetchOne()
        );
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(
                queryFactory.selectFrom(user)
                        .where(user.id.eq(id))
                        .fetchOne()
        );
    }

    public boolean existsByEmail(String email) {
        return queryFactory.selectFrom(user)
                .where(user.email.eq(email))
                .fetchFirst() != null;
    }
}
