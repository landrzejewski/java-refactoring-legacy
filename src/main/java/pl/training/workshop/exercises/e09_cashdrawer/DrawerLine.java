package pl.training.workshop.exercises.e09_cashdrawer;

import pl.training.workshop.shared.Money;

/**
 * Stabilny kontrakt ćwiczenia: surowy wpis dziennika szuflady z systemu kasowego.
 * Rodzaj: CASH (sprzedaż gotówką), CARD (sprzedaż kartą, note = 4 ostatnie cyfry karty),
 * REFUND (zwrot gotówki, note = powód), DEPOSIT (pogotowie kasowe, note = opis).
 */
public record DrawerLine(String kind, String reference, Money amount, String note) {
}
