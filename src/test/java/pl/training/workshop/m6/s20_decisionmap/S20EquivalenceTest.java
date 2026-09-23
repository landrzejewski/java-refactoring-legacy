package pl.training.workshop.m6.s20_decisionmap;

import java.time.DayOfWeek;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.shared.Money;
import pl.training.workshop.support.Scene;

/** Pełna tabela 3 formaty x typy dni: obie ścieżki (krok 2 i krok 3) dają te same ceny. */
final class S20EquivalenceTest {
    @TestFactory
    Stream<DynamicTest> bothPathsPriceTheSame() {
        return Scene.<String, String>variants()
                .variant("start", safe(new pl.training.workshop.m6.s20_decisionmap.start.ShowPricing()::price))
                .variant("step1", safe(new pl.training.workshop.m6.s20_decisionmap.step1.ShowPricing()::price))
                .variant("step2 (Strategy)", safe(new pl.training.workshop.m6.s20_decisionmap.step2.ShowPricing()::price))
                .variant("step3 (typ formatu)", safe(new pl.training.workshop.m6.s20_decisionmap.step3.ShowPricing()::price))
                .expect("2D poniedziałek", "MONDAY 2D", "25.00")
                .expect("2D wtorek", "TUESDAY 2D", "17.50")
                .expect("2D sobota", "SATURDAY 2D", "27.00")
                .expect("3D poniedziałek", "MONDAY 3D", "32.00")
                .expect("3D wtorek", "TUESDAY 3D", "22.40")
                .expect("3D sobota", "SATURDAY 3D", "34.00")
                .expect("IMAX poniedziałek", "MONDAY IMAX", "40.00")
                .expect("IMAX wtorek", "TUESDAY IMAX", "28.00")
                .expect("IMAX niedziela", "SUNDAY IMAX", "42.00")
                .expect("nieznany format", "TUESDAY 4DX", "ERROR unknown format: 4DX")
                .tests();
    }

    private static Function<String, String> safe(BiFunction<DayOfWeek, String, Money> price) {
        return input -> {
            String[] parts = input.split(" ");
            try {
                return price.apply(DayOfWeek.valueOf(parts[0]), parts[1]).toString();
            } catch (IllegalArgumentException exception) {
                return "ERROR " + exception.getMessage();
            }
        };
    }
}
