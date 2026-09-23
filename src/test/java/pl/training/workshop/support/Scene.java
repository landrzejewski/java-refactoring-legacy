package pl.training.workshop.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;

/**
 * Pomocnik testów równoważności scen warsztatu.
 * Każdy wariant sceny (start, step1, ...) jest adaptowany do wspólnej
 * funkcji {@code I -> O}, a następnie sprawdzany na tych samych przypadkach.
 *
 * <pre>{@code
 * @TestFactory
 * Stream<DynamicTest> everyVariantBehavesTheSame() {
 *     return Scene.<Order, String>variants()
 *             .variant("start", o -> new start.PriceCalculator().price(o))
 *             .variant("step1", o -> new step1.PriceCalculator().price(o))
 *             .expect("2D normal", order2d, "25.00")
 *             .tests();
 * }
 * }</pre>
 */
public final class Scene<I, O> {
    private final Map<String, Function<I, O>> variants = new LinkedHashMap<>();
    private final List<Case<I, O>> cases = new ArrayList<>();

    private Scene() {
    }

    public static <I, O> Scene<I, O> variants() {
        return new Scene<>();
    }

    public Scene<I, O> variant(String name, Function<I, O> implementation) {
        variants.put(name, implementation);
        return this;
    }

    public Scene<I, O> expect(String name, I input, O expected) {
        cases.add(new Case<>(name, input, expected));
        return this;
    }

    public Stream<DynamicTest> tests() {
        return variants.entrySet().stream()
                .flatMap(variant -> cases.stream().map(testCase ->
                        DynamicTest.dynamicTest(
                                variant.getKey() + ": " + testCase.name(),
                                () -> assertEquals(
                                        testCase.expected(),
                                        variant.getValue().apply(testCase.input())))));
    }

    private record Case<I, O>(String name, I input, O expected) {
    }
}
