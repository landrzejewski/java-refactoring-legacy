package pl.training.workshop.m3.s16_temporalcoupling.step2;

import java.util.Objects;

import pl.training.workshop.m3.s16_temporalcoupling.Screening;

/**
 * Krok 2: Introduce Parameter Object - dane biletu jako jedna wartość,
 * kompletna od chwili utworzenia (null odrzucony od razu, a nie w print).
 */
public record TicketRequest(Screening screening, int seat, String buyer) {
    public TicketRequest {
        Objects.requireNonNull(screening, "screening");
        Objects.requireNonNull(buyer, "buyer");
    }
}
