package pl.training.workshop.m5.s29_boundaryobject;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.shared.Money;
import pl.training.workshop.support.Scene;

/**
 * Sesja płatności: autoryzacja pełna, częściowa (granica 300.00), odmowa, błąd kwoty,
 * nadpisanie płatności, potwierdzenia i suma autoryzacji. Wynik identyczny niezależnie od tego,
 * gdzie i ile razy rozbieramy odpowiedź terminala.
 */
final class S29EquivalenceTest {
    @TestFactory
    Stream<DynamicTest> everyStepReadsTheTerminalTheSame() {
        return Scene.<List<String>, String>variants()
                .variant("start", session(() -> {
                    var service = pl.training.workshop.m5.s29_boundaryobject.start.PaymentModule
                            .paymentService(new LegacyTerminalClient());
                    return console(service::pay, service::receipt, service::totalAuthorized);
                }))
                .variant("step1", session(() -> {
                    var service = pl.training.workshop.m5.s29_boundaryobject.step1.PaymentModule
                            .paymentService(new LegacyTerminalClient());
                    return console(service::pay, service::receipt, service::totalAuthorized);
                }))
                .variant("step2", session(() -> {
                    var service = pl.training.workshop.m5.s29_boundaryobject.step2.PaymentModule
                            .paymentService(new LegacyTerminalClient());
                    return console(service::pay, service::receipt, service::totalAuthorized);
                }))
                .variant("step3", session(() -> {
                    var service = pl.training.workshop.m5.s29_boundaryobject.step3.PaymentModule
                            .paymentService(new LegacyTerminalClient());
                    return console(service::pay, service::receipt, service::totalAuthorized);
                }))
                .expect("pełna autoryzacja", List.of("pay R1 45.00", "receipt R1", "total"), """
                        PAID AUTH-R1
                        Platnosc R1: 45.00 (AUTH-R1)
                        45.00""")
                .expect("autoryzacja częściowa", List.of("pay R2 450.00", "receipt R2", "total"), """
                        PARTIAL AUTH-R2 brakuje 150.00
                        Platnosc R2: 300.00 (AUTH-R2)
                        300.00""")
                .expect("granica 300.00 i grosz ponad", List.of("pay R3 300.00", "pay R4 300.01", "total"), """
                        PAID AUTH-R3
                        PARTIAL AUTH-R4 brakuje 0.01
                        600.00""")
                .expect("odmowa i błąd kwoty", List.of(
                        "pay R5 1500.00", "receipt R5", "pay R6 0.00", "receipt R6", "total"), """
                        DECLINED 51
                        Platnosc R5 nieudana: 51
                        ERROR AMOUNT
                        Platnosc R6 nieudana: AMOUNT
                        0.00""")
                .expect("brak płatności", List.of("receipt R9", "total"), """
                        Brak platnosci R9
                        0.00""")
                .expect("ponowna płatność nadpisuje poprzednią", List.of(
                        "pay R1 45.00", "pay R2 25.50", "pay R1 1500.00", "receipt R1", "total"), """
                        PAID AUTH-R1
                        PAID AUTH-R2
                        DECLINED 51
                        Platnosc R1 nieudana: 51
                        25.50""")
                .tests();
    }

    private static Function<String, String> console(BiFunction<String, Money, String> pay,
                                                    Function<String, String> receipt, Supplier<Money> total) {
        return line -> {
            String[] parts = line.split(" ");
            return switch (parts[0]) {
                case "pay" -> pay.apply(parts[1], Money.of(parts[2]));
                case "receipt" -> receipt.apply(parts[1]);
                case "total" -> total.get().toString();
                default -> throw new IllegalArgumentException(line);
            };
        };
    }

    private static Function<List<String>, String> session(Supplier<Function<String, String>> console) {
        return lines -> {
            Function<String, String> handle = console.get();
            return String.join("\n", lines.stream().map(handle).toList());
        };
    }
}
