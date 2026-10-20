package pl.training.workshop.m5.s29_boundaryobject.step3;

import java.util.Objects;

import pl.training.workshop.m5.s29_boundaryobject.LegacyTerminalClient;
import pl.training.workshop.shared.Money;

/**
 * Krok 3: Adapter implementujący port domeny. Łączy klienta terminala z mapperem;
 * poza tą klasą nikt nie widzi groszy ani tekstu "OK;AUTH;4500".
 */
public final class LegacyTerminalAdapter implements PaymentTerminal {
    private final LegacyTerminalClient client;
    private final TerminalResponseMapper mapper = new TerminalResponseMapper();

    public LegacyTerminalAdapter(LegacyTerminalClient client) {
        this.client = Objects.requireNonNull(client, "client");
    }

    @Override
    public PaymentConfirmation authorize(String bookingId, Money amount) {
        return mapper.toConfirmation(client.authorize(bookingId, mapper.toGrosze(amount)));
    }
}
