package pl.training.workshop.m6.s10_implicittree;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/** Niezależne oczekiwania (policzone ręcznie) dla ceny i wydruku zestawów. */
final class S10EquivalenceTest {
    @TestFactory
    Stream<DynamicTest> everyStepPricesAndRendersCombosTheSame() {
        return Scene.<List<?>, String>variants()
                .variant("start", safe(new pl.training.workshop.m6.s10_implicittree.start.BarMenu()::price,
                        new pl.training.workshop.m6.s10_implicittree.start.BarMenu()::render))
                .variant("step1", safe(new pl.training.workshop.m6.s10_implicittree.step1.BarMenu()::price,
                        new pl.training.workshop.m6.s10_implicittree.step1.BarMenu()::render))
                .variant("step2", safe(new pl.training.workshop.m6.s10_implicittree.step2.BarMenu()::price,
                        new pl.training.workshop.m6.s10_implicittree.step2.BarMenu()::render))
                .variant("step3", safe(new pl.training.workshop.m6.s10_implicittree.step3.BarMenu()::price,
                        new pl.training.workshop.m6.s10_implicittree.step3.BarMenu()::render))
                .expect("zestaw z podzestawem", List.of("Zestaw Duo", "Popcorn L=18.00",
                        List.of("Napoje", "Cola 0.5=9.00", "Cola 0.5=9.00"), "Nachos=14.00"), """
                        50.00
                        Zestaw Duo 50.00
                          Popcorn L 18.00
                          Napoje 18.00
                            Cola 0.5 9.00
                            Cola 0.5 9.00
                          Nachos 14.00
                        """)
                .expect("pusty zestaw", List.of("Pusty"), "0.00\nPusty 0.00\n")
                .expect("brak nazwy", List.of(), "ERROR combo needs a name")
                .expect("element nieobsługiwany", List.of("Zestaw", "Cola=9.00", 5), "ERROR unsupported element: 5")
                .expect("produkt bez ceny", List.of("Zestaw", "Cola"), "ERROR product needs a price: Cola")
                .tests();
    }

    static Function<List<?>, String> safe(Function<List<?>, ?> price, Function<List<?>, String> render) {
        return definition -> {
            try {
                return price.apply(definition) + "\n" + render.apply(definition);
            } catch (IllegalArgumentException exception) {
                return "ERROR " + exception.getMessage();
            }
        };
    }
}
