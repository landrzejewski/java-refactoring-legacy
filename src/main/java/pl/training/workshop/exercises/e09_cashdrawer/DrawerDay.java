package pl.training.workshop.exercises.e09_cashdrawer;

import java.util.List;

import pl.training.workshop.shared.Money;

/**
 * Stabilny kontrakt ćwiczenia: dzień kasowy do zamknięcia. Wpisy szuflady oraz ceny biletów
 * jednej sprzedaży grupowej (po zniżkach indywidualnych, mogą mieć dowolne grosze)
 * i tryb podziału rabatu grupowego z konfiguracji kina (PROPORTIONAL, LAST, EQUAL).
 */
public record DrawerDay(List<DrawerLine> lines, List<Money> groupTickets, String splitMode) {
}
