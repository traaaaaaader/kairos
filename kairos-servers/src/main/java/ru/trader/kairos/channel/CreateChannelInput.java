package ru.trader.kairos.channel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateChannelInput(
    @NotNull
    Long serverId,

    @NotBlank
    @Size(min = 2, max = 100)
    String name,

    @NotNull
    ChannelType type
) {}
