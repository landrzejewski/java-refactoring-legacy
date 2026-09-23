package pl.training.workshop.m3.s16_temporalcoupling.step1;

import pl.training.workshop.m3.s16_temporalcoupling.Screening;

/**
 * Krok 1: Change Signature (⌘F6) - print przyjmuje wszystko, czego potrzebuje,
 * pola i settery usunięte (Safe Delete). Protokół "najpierw ustaw, potem drukuj"
 * zamienił się w kompilowalny kontrakt, a obiekt jest bezstanowy i bezpieczny współbieżnie.
 */
public final class TicketPrinter {
    public String print(Screening screening, int seat, String buyer) {
        return "BILET " + screening.title() + " (" + screening.format() + ") " + screening.start()
                + ", miejsce " + seat + ", dla " + buyer.toLowerCase();
    }
}
