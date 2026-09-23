package pl.training.workshop.m5.s07_collapsehierarchy.step1;

import java.util.List;

/** Krok 1: bez zmian. */
public final class HallCatalog {
    private final List<Hall> halls = List.of(
            new Hall("Sala 1", 12, 15, 10),
            new Hall("Sala 2", 10, 12, 9),
            new ImaxHall("Sala IMAX", 14, 22));

    public String describe(String name) {
        return halls.stream()
                .filter(hall -> hall.name().equals(name))
                .map(Hall::describe)
                .findFirst()
                .orElse("brak sali: " + name);
    }

    public boolean isVip(String name, int row) {
        return halls.stream().anyMatch(hall -> hall.name().equals(name) && hall.isVip(row));
    }
}
