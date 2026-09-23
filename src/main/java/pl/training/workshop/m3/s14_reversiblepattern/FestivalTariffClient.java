package pl.training.workshop.m3.s14_reversiblepattern;

/**
 * Obcy interfejs (stabilny, nie nasz): klient systemu festiwalu filmowego.
 * Zwraca tygodniową opłatę w groszach jako long - inny model niż nasz BigDecimal.
 */
public final class FestivalTariffClient {
    public long weeklyFeeInCents(String title, int week) {
        return week <= 2 ? 30_000 : 15_000;
    }
}
