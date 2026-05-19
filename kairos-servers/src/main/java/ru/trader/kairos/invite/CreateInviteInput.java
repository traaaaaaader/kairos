package ru.trader.kairos.invite;

import jakarta.validation.constraints.NotNull;

public record CreateInviteInput(
    @NotNull
    Long serverId,

    String expiresAt,

    Integer maxUses
) {}
