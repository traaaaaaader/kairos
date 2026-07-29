package ru.trader.kairos.channel;

import jakarta.persistence.*;
import lombok.*;
import ru.trader.kairos.server.Server;

import java.time.OffsetDateTime;

@Entity
@Table(name = "channels")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Channel {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "channels_seq")
    @SequenceGenerator(
            name = "channels_seq",
            sequenceName = "channels_id_seq",
            allocationSize = 10
    )
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "server_id", nullable = false)
    private Server server;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ChannelType type;

    @Column(name = "position", nullable = false)
    private int position;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        updatedAt = OffsetDateTime.now();
    }
}