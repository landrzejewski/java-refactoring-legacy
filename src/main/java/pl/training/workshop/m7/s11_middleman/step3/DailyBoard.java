package pl.training.workshop.m7.s11_middleman.step3;

import java.util.Objects;
import java.util.stream.Collectors;

import pl.training.workshop.m7.s11_middleman.ScreeningCatalog;

/**
 * Krok 3 (rozwiązanie): ostatni klient zmigrowany, CinemaFacade usunięta (Safe Delete).
 * Odwrotny ruch to Hide Delegate - wrócimy do niego, gdy pośrednik zacznie coś wnosić.
 */
public final class DailyBoard {
    private final ScreeningCatalog catalog;

    public DailyBoard(ScreeningCatalog catalog) {
        this.catalog = Objects.requireNonNull(catalog, "catalog");
    }

    public String render() {
        return catalog.all().stream()
                .map(screening -> screening.id() + " " + screening.title() + " " + screening.format())
                .collect(Collectors.joining("\n"));
    }
}
