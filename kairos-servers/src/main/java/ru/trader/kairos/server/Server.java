package ru.trader.kairos.server;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.trader.kairos.channel.Channel;
import ru.trader.kairos.invite.ServerInvite;
import ru.trader.kairos.member.ServerMember;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "servers")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Server {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "servers_seq")
    @SequenceGenerator(
            name = "servers_seq",
            sequenceName = "servers_id_seq",
            allocationSize = 10
    )
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Column(name = "icon_url", length = 500)
    private String iconUrl;

    @OneToMany(mappedBy = "server", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Channel> channels = new ArrayList<>();

    @OneToMany(mappedBy = "server")
    private List<ServerMember> members = new ArrayList<>();

    @OneToMany(mappedBy = "server")
    private List<ServerInvite> invites = new ArrayList<>();

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