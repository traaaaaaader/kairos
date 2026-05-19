package ru.trader.kairos.member;

import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public record ServerMemberId(
    Long serverId,
    Long userId
) implements Serializable {}
