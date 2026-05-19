package ru.trader.kairos.channel;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.trader.kairos.channel.QChannel;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ChannelReader {

    private final JPAQueryFactory queryFactory;
    private final QChannel channel = QChannel.channel;

    public ChannelReader(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    public Optional<Channel> findById(Long id) {
        return Optional.ofNullable(
                queryFactory.selectFrom(channel)
                        .where(channel.id.eq(id))
                        .fetchOne()
        );
    }

    public List<Channel> findByServerId(Long serverId) {
        return queryFactory.selectFrom(channel)
                .where(channel.server.id.eq(serverId))
                .orderBy(channel.position.asc())
                .fetch();
    }

    public int getNextPosition(Long serverId) {
        Integer maxPosition = queryFactory.select(channel.position.max())
                .from(channel)
                .where(channel.server.id.eq(serverId))
                .fetchOne();
        return (maxPosition != null ? maxPosition : -1) + 1;
    }
}