package ru.trader.kairos.member;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ServerMemberRepository extends JpaRepository<ServerMember, ServerMemberId> {
}
