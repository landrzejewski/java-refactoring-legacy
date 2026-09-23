package pl.training.workshop.m3.s11_dip;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * DIP to kierunek zależności, nie sposób jej dostarczenia. Sprawdzamy pakiety typów
 * pól przypadku użycia: DI w kroku 1 nie zmienia kierunku, port w kroku 3 - tak.
 * (Bez start - ten jest edytowany na żywo; krok 1 to start z wstrzykniętą zależnością.)
 */
final class S11DependencyDirectionTest {
    private static boolean dependsOnInfra(Class<?> useCase) {
        return Arrays.stream(useCase.getDeclaredFields())
                .map(Field::getType)
                .anyMatch(type -> type.getPackageName().endsWith(".infra"));
    }

    @Test
    void step1AndStep2PolicyDependsOnInfrastructure() {
        assertTrue(dependsOnInfra(pl.training.workshop.m3.s11_dip.step1.app.ConfirmReservation.class),
                "wstrzykniecie konkretnej klasy to DI, ale nie DIP");
        assertTrue(dependsOnInfra(pl.training.workshop.m3.s11_dip.step2.app.ConfirmReservation.class));
    }

    @Test
    void step3PolicyDependsOnlyOnItsOwnPort() {
        assertFalse(dependsOnInfra(pl.training.workshop.m3.s11_dip.step3.app.ConfirmReservation.class));
        assertTrue(pl.training.workshop.m3.s11_dip.step3.app.CustomerNotifier.class
                .isAssignableFrom(pl.training.workshop.m3.s11_dip.step3.infra.SmtpCustomerNotifier.class),
                "adapter z infra implementuje port z app: zaleznosc zrodlowa infra -> app");
    }

    @Test
    void step3PolicyIsTestableWithAHandWrittenFake() {
        List<String> sent = new ArrayList<>();
        var useCase = new pl.training.workshop.m3.s11_dip.step3.app.ConfirmReservation(
                (email, message) -> sent.add(email + ": " + message));

        useCase.confirm(new Reservation("anna@kino.pl", "Diuna", LocalDateTime.of(2026, 10, 2, 20, 0), 2));

        assertEquals(List.of("anna@kino.pl: Rezerwacja: Diuna, 2026-10-02T20:00, miejsc: 2. Zaplac w ciagu 15 minut."),
                sent);
    }
}
