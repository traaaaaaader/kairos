package ru.trader.kairos.server;

import jakarta.validation.constraints.Size;

public record UpdateServerInput(
    @Size(min = 2, max = 100)
    String name,

    @Size(max = 500)
    String iconUrl
) {}
