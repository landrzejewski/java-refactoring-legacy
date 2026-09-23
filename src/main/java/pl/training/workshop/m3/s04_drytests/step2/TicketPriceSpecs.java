package pl.training.workshop.m3.s04_drytests.step2;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import pl.training.workshop.m3.s04_drytests.TicketPrice;

/**
 * Krok 2 (rozwiązanie): oczekiwana cena wpisana jawnie (policzona ręcznie z regulaminu),
 * a kopia algorytmu usunięta. Wspólny zostaje tylko helper {@link #check} - fabryka
 * przypadku i format komunikatu to dopuszczalne DRY w testach. Test znów jest
 * niezależną wyrocznią: błąd w taryfie zapala czerwone światło.
 */
public final class TicketPriceSpecs {
    public List<String> run(TicketPrice price) {
        List<String> failures = new ArrayList<>();
        check(price, "normalny na wieczornym IMAX", "IMAX", "NORMAL", LocalTime.of(20, 0), "40.00", failures);
        check(price, "student na porannym 3D", "3D", "STUDENT", LocalTime.of(11, 0), "19.00", failures);
        check(price, "senior na wieczornym 2D", "2D", "SENIOR", LocalTime.of(18, 0), "17.50", failures);
        check(price, "dziecko na porannym 2D", "2D", "CHILD", LocalTime.of(10, 0), "10.00", failures);
        return failures;
    }

    private void check(TicketPrice price, String example, String format, String type, LocalTime start,
                       String expected, List<String> failures) {
        BigDecimal actual = price.of(format, type, start);
        if (actual.compareTo(new BigDecimal(expected)) != 0) {
            failures.add(example + ": oczekiwano " + expected + ", jest " + actual);
        }
    }
}
