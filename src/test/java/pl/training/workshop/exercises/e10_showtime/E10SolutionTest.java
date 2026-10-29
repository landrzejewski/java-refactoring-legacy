package pl.training.workshop.exercises.e10_showtime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import pl.training.workshop.exercises.e10_showtime.step2.AutoScheduler;
import pl.training.workshop.exercises.e10_showtime.step2.BoxOfficePanel;
import pl.training.workshop.exercises.e10_showtime.step2.Showtime;

/**
 * Krok 2: urządzenia znają tylko sterownik, kolejność reakcji jest w jednym miejscu,
 * a klienci znają tylko fasadę; podsystemy nie są publiczne.
 */
final class E10SolutionTest {
    private static final String STEP2 = "pl.training.workshop.exercises.e10_showtime.step2.";
    private static final List<String> DEVICES = List.of("Lights", "Sound", "Doors", "AdPlayer", "Projector");
    private static final List<String> SUBSYSTEM = List.of("TicketScanner", "AttendanceReport", "HallController",
            "HallMediator", "DeviceLog", "Lights", "Sound", "Doors", "AdPlayer", "Projector");

    @Test
    void noDeviceHoldsAnotherDevice() throws ClassNotFoundException {
        Set<String> allowed = Set.of("HallMediator", "DeviceLog", "int", "boolean");
        for (String device : DEVICES) {
            for (Field field : Class.forName(STEP2 + device).getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                assertTrue(allowed.contains(field.getType().getSimpleName()),
                        device + " zna " + field.getType().getSimpleName());
            }
        }
    }

    @Test
    void onlyTheHallControllerKnowsTheDevices() throws ClassNotFoundException {
        List<String> owners = new ArrayList<>();
        for (String name : List.of("HallController", "TicketScanner", "AttendanceReport", "Showtime",
                "BoxOfficePanel", "AutoScheduler", "ShowtimeRunner")) {
            boolean holdsDevice = Arrays.stream(Class.forName(STEP2 + name).getDeclaredFields())
                    .anyMatch(field -> DEVICES.contains(field.getType().getSimpleName()));
            if (holdsDevice) {
                owners.add(name);
            }
        }
        assertEquals(List.of("HallController"), owners, "kolejność reakcji urządzeń w jednym miejscu");
    }

    @Test
    void clientsKnowOnlyTheFacade() {
        for (Class<?> client : List.of(BoxOfficePanel.class, AutoScheduler.class)) {
            List<Class<?>> dependencies = Arrays.stream(client.getDeclaredFields())
                    .filter(field -> !Modifier.isStatic(field.getModifiers()))
                    .<Class<?>>map(Field::getType)
                    .toList();
            assertEquals(List.of(Showtime.class), dependencies, client.getSimpleName());
        }
    }

    @Test
    void subsystemsAreHiddenBehindTheFacade() throws ClassNotFoundException {
        for (String name : SUBSYSTEM) {
            assertFalse(Modifier.isPublic(Class.forName(STEP2 + name).getModifiers()), name);
        }
        assertEquals(0, Showtime.class.getConstructors().length, "fasadę składa korzeń kompozycji");
    }
}
