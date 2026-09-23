package pl.training.workshop.m8.s11_stagedrollout;

import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/** Test równoważności: z dotychczasowymi ustawieniami każdy krok kieruje klientów tak samo. */
final class S11EquivalenceTest {
    @TestFactory
    Stream<DynamicTest> currentSettingsRouteTheSameCustomers() {
        return Scene.<String, Boolean>variants()
                .variant("start", new pl.training.workshop.m8.s11_stagedrollout.start.CheckoutRouter()::useNewCheckout)
                .variant("step1", new pl.training.workshop.m8.s11_stagedrollout.step1.CheckoutRouter()::useNewCheckout)
                .variant("step2", new pl.training.workshop.m8.s11_stagedrollout.step2.CheckoutRouter()::useNewCheckout)
                .variant("step3", new pl.training.workshop.m8.s11_stagedrollout.step3.CheckoutRouter()::useNewCheckout)
                .expect("tester Anna", "anna@kino.pl", true)
                .expect("tester Jan", "jan@kino.pl", true)
                .expect("klientka Ola", "ola@kino.pl", false)
                .expect("klient Piotr", "piotr@kino.pl", false)
                .tests();
    }
}
