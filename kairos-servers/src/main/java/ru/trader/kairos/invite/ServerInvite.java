package ru.trader.kairos.invite;

import jakarta.persistence.*;
import lombok.*;
import ru.trader.kairos.server.Server;

import java.time.OffsetDateTime;

@Entity
@Table(name = "server_invites")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ServerInvite {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "server_invites_seq")
    @SequenceGenerator(
            name = "server_invites_seq",
            sequenceName = "server_invites_id_seq",
            allocationSize = 10
    )
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "server_id", nullable = false)
    private Server server;

    @Column(name = "code", nullable = false, unique = true, length = 10)
    private String code;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "expires_at")
    private OffsetDateTime expiresAt;

    @Column(name = "max_uses")
    private Integer maxUses;

    @Column(name = "uses", nullable = false)
    private int uses = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
    }

    public boolean isExpired() {
        return expiresAt != null && OffsetDateTime.now().isAfter(expiresAt);
    }

    public boolean isExhausted() {
        return maxUses != null && uses >= maxUses;
    }

    public boolean isValid() {
        return !isExpired() && !isExhausted();
    }
}
