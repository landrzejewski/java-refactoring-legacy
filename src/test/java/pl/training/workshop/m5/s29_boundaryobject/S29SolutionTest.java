package pl.training.workshop.m5.s29_boundaryobject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import pl.training.workshop.m5.s29_boundaryobject.step3.PaymentConfirmation;
import pl.training.workshop.m5.s29_boundaryobject.step3.PaymentConfirmation.Outcome;
import pl.training.workshop.m5.s29_boundaryobject.step3.PaymentService;
import pl.training.workshop.m5.s29_boundaryobject.step3.TerminalResponseMapper;
import pl.training.workshop.shared.Money;

/** Obiekt graniczny jest zawsze poprawny, format terminala zna tylko granica, domena widzi port. */
final class S29SolutionTest {
    private final TerminalResponseMapper mapper = new TerminalResponseMapper();

    @Test
    void boundaryObjectCannotBeInvalid() {
        assertThrows(IllegalArgumentException.class,
                () -> new PaymentConfirmation(Outcome.APPROVED, " ", Money.of("10.00")));
        assertThrows(IllegalArgumentException.class,
                () -> new PaymentConfirmation(Outcome.APPROVED, "AUTH", Money.of("-0.01")));
        assertThrows(IllegalArgumentException.class,
                () -> new PaymentConfirmation(Outcome.DECLINED, "51", Money.of("10.00")));
    }

    @Test
    void malformedResponseIsRejectedAtTheBoundary() {
        for (String raw : List.of("OK;AUTH-R1", "MAYBE;X;0", "OK;;4500", "OK;AUTH-R1;abc")) {
            assertThrows(IllegalArgumentException.class, () -> mapper.toConfirmation(raw), raw);
        }
    }

    @Test
    void mapperTranslatesUnitsBothWays() {
        assertEquals(4500, mapper.toGrosze(Money.of("45.00")));
        assertEquals(new PaymentConfirmation(Outcome.APPROVED, "AUTH-R1", Money.of("45.00")),
                mapper.toConfirmation("OK;AUTH-R1;4500"));
    }

    @Test
    void serviceWorksWithAnyTerminalBehindThePort() {
        PaymentService service = new PaymentService(
                (bookingId, amount) -> new PaymentConfirmation(Outcome.APPROVED, "FAKE-" + bookingId, amount));
        assertEquals("PAID FAKE-R1", service.pay("R1", Money.of("999.00")));
        assertEquals(Money.of("999.00"), service.totalAuthorized());
    }

    @Test
    void domainServiceSeesNeitherTheLegacyClientNorTheMapper() {
        List<Class<?>> used = new ArrayList<>();
        Arrays.stream(PaymentService.class.getDeclaredFields()).map(Field::getType).forEach(used::add);
        for (Constructor<?> constructor : PaymentService.class.getConstructors()) {
            used.addAll(List.of(constructor.getParameterTypes()));
        }
        for (Method method : PaymentService.class.getDeclaredMethods()) {
            used.addAll(List.of(method.getParameterTypes()));
            used.add(method.getReturnType());
        }
        assertFalse(used.contains(LegacyTerminalClient.class), "serwis nie zna klienta terminala");
        assertFalse(used.contains(TerminalResponseMapper.class), "serwis nie zna formatu odpowiedzi");
    }
}
