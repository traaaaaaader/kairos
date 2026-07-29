package ru.trader.kairos.invite;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateInviteInput(
    @NotNull
    Long serverId,

    @Future(message = "Expiration date must be in the future")
    String expiresAt,

    @Positive(message = "Max uses must be positive")
    Integer maxUses
) {}
