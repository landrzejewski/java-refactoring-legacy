package pl.training.workshop.exercises.e09_cashdrawer

import pl.training.workshop.shared.Money

/**
 * Stabilny kontrakt ćwiczenia: dzień kasowy do zamknięcia. Wpisy szuflady oraz ceny biletów
 * jednej sprzedaży grupowej (po zniżkach indywidualnych, mogą mieć dowolne grosze)
 * i tryb podziału rabatu grupowego z konfiguracji kina (PROPORTIONAL, LAST, EQUAL).
 */
data class DrawerDay(val lines: List<DrawerLine>, val groupTickets: List<Money>, val splitMode: String)
