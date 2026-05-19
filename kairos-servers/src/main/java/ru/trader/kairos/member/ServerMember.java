package ru.trader.kairos.member;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.trader.kairos.server.Server;

import java.time.OffsetDateTime;

@Entity
@Table(name = "server_members")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ServerMember {

    @EmbeddedId
    @EqualsAndHashCode.Include
    private ServerMemberId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("serverId")
    @JoinColumn(name = "server_id")
    private Server server;

    @Column(name = "joined_at", nullable = false, updatable = false)
    private OffsetDateTime joinedAt;

    @PrePersist
    protected void onCreate() {
        joinedAt = OffsetDateTime.now();
    }
}
