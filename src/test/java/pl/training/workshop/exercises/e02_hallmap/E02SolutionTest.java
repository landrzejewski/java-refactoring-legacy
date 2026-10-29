package pl.training.workshop.exercises.e02_hallmap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static pl.training.workshop.exercises.e02_hallmap.E02EquivalenceTest.hall;
import static pl.training.workshop.exercises.e02_hallmap.E02EquivalenceTest.row;
import static pl.training.workshop.exercises.e02_hallmap.E02EquivalenceTest.section;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import pl.training.workshop.exercises.e02_hallmap.step3.Hall;
import pl.training.workshop.exercises.e02_hallmap.step3.HallElement;
import pl.training.workshop.exercises.e02_hallmap.step3.HallGroup;
import pl.training.workshop.exercises.e02_hallmap.step3.Row;
import pl.training.workshop.exercises.e02_hallmap.step3.Seat;
import pl.training.workshop.exercises.e02_hallmap.step3.SeatCategories;
import pl.training.workshop.exercises.e02_hallmap.step3.SeatCategory;
import pl.training.workshop.exercises.e02_hallmap.step3.Section;

/** Kontrakt rozwiązania: jedno drzewo, jedno przejście, jedna instancja kategorii na kod. */
final class E02SolutionTest {
    @Test
    void fourHundredSeatsShareAtMostFourCategoryInstances() {
        List<RowSpec> rows = IntStream.rangeClosed(1, 20)
                .mapToObj(i -> row("R" + i, i <= 16 ? "NNNNNNNNNNNNNNNNNNNN" : "VVVVVVVVVVVVVVVVVVVV"))
                .toList();
        Hall imax = Hall.of(hall("IMAX", new SectionSpec("Parter", rows)));

        Set<SeatCategory> instances = Collections.newSetFromMap(new IdentityHashMap<>());
        List<Seat> seats = new ArrayList<>();
        for (Seat seat : imax) {
            instances.add(seat.category());
            seats.add(seat);
        }

        assertEquals(400, seats.size());
        assertEquals(2, instances.size(), "jedna instancja na kod kategorii");
        assertSame(seats.get(0).category(), seats.get(319).category(), "pierwszy i ostatni fotel normalny");
        assertSame(seats.get(320).category(), seats.get(399).category(), "pierwszy i ostatni fotel VIP");
        assertTrue(SeatCategories.known().size() <= 4, "fabryka zna najwyzej cztery kategorie");
    }

    @Test
    void seatKeepsOnlyItsOwnStateAndCategoryKnowsNoPlace() {
        assertEquals(Set.of("label", "category"), fieldNames(Seat.class));
        assertTrue(fieldNames(SeatCategory.class).stream().noneMatch(name -> name.contains("label") || name.contains("row")),
                "kategoria nie wie, gdzie stoi fotel");
        assertTrue(Arrays.stream(SeatCategory.class.getDeclaredFields())
                .allMatch(field -> java.lang.reflect.Modifier.isFinal(field.getModifiers())),
                "wspoldzielony stan musi byc niemutowalny");
    }

    @Test
    void iterationSkipsGapsAndKeepsSectionAndRowOrder() {
        Hall hall = Hall.of(hall("Mala", section("Parter", row("A", "N_N"), row("B", "_V")), section("Balkon", row("C", "D_"))));
        List<String> labels = new ArrayList<>();
        hall.forEach(seat -> labels.add(seat.label()));
        assertEquals(List.of("A1", "A3", "B2", "C1"), labels);
    }

    @Test
    void everyLevelOfTheHallAnswersTheSameQuestions() {
        for (Class<?> level : List.of(Hall.class, Section.class, Row.class)) {
            assertTrue(HallGroup.class.isAssignableFrom(level), level.getSimpleName() + " jest wezlem drzewa");
        }
        assertTrue(HallElement.class.isAssignableFrom(Seat.class), "miejsce jest lisciem o tym samym kontrakcie");

        Hall hall = Hall.of(hall("Mala", section("Parter", row("A", "NV"), row("B", "DD"))));
        int counted = 0;
        for (Seat ignored : hall) {
            counted++;
        }
        assertEquals(counted, hall.capacity(), "pojemnosc drzewa zgadza sie z liczba odwiedzonych miejsc");
        assertEquals("22.00", hall.surcharges().toString());
    }

    @Test
    void factoryRejectsUnknownCodeWithTheSameMessageAsStart() {
        var error = assertThrows(IllegalArgumentException.class, () -> SeatCategories.of('X'));
        assertEquals("unknown seat category: X", error.getMessage());
    }

    private static Set<String> fieldNames(Class<?> type) {
        return Arrays.stream(type.getDeclaredFields()).map(Field::getName).collect(Collectors.toSet());
    }
}
