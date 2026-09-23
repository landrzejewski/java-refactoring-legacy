package pl.training.workshop.m3.s05_kiss;

import java.util.List;

/**
 * Stabilny kontrakt sceny - plan sali. Rzędy numerowane od 1.
 * Znaki w rzędzie: '.' wolne, 'X' zajęte, 'B' zablokowane (awaria), ' ' przejście.
 *
 * @param vipFromRow od tego rzędu (włącznie) miejsca są VIP
 */
public record Hall(List<String> rows, int vipFromRow) {
    public Hall {
        rows = List.copyOf(rows);
    }
}
