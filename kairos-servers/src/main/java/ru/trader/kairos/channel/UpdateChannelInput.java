package ru.trader.kairos.channel;

import jakarta.validation.constraints.Size;

public record UpdateChannelInput(
    @Size(min = 2, max = 100)
    String name,

    Integer position
) {}
