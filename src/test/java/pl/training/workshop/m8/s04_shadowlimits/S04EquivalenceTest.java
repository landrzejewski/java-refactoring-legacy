package pl.training.workshop.m8.s04_shadowlimits;

import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/** Test równoważności: odpowiedź dla klienta (wynik legacy) jest taka sama w każdym kroku. */
final class S04EquivalenceTest {
    @TestFactory
    Stream<DynamicTest> customerGetsTheSameAnswer() {
        return Scene.<BookingRequest, String>variants()
                .variant("start", r -> new pl.training.workshop.m8.s04_shadowlimits.start.ShadowBooking(
                        new Infrastructure()).book(r))
                .variant("step1", r -> new pl.training.workshop.m8.s04_shadowlimits.step1.ShadowBooking(
                        new Infrastructure()).book(r))
                .variant("step2", r -> new pl.training.workshop.m8.s04_shadowlimits.step2.ShadowBooking(
                        new Infrastructure()).book(r))
                .variant("step3", r -> new pl.training.workshop.m8.s04_shadowlimits.step3.ShadowBooking(
                        new Infrastructure()).book(r))
                .expect("2 bilety online", new BookingRequest("anna@kino.pl", "4111-1111", "Amator", 2), "OK 54.00")
                .expect("1 bilet online", new BookingRequest("jan@kino.pl", "5500-2222", "Diuna", 1), "OK 27.00")
                .tests();
    }
}
