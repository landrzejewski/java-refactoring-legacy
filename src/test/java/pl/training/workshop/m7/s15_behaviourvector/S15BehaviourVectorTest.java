package pl.training.workshop.m7.s15_behaviourvector;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/**
 * Wektor obserwowalnego zachowania rośnie razem z seamami:
 * krok 1 - wynik + maile (regresja widoczna), krok 2 - naprawa,
 * krok 3 - wynik, wyjątek, maile i obciążenia w jednej kolejności, stan rezerwacji.
 */
final class S15BehaviourVectorTest {
    @Test
    void step1SeesTheRegressionThatResultOnlyTestMissed() {
        assertEquals("DECLINED [anna@kino.pl: Platnosc odrzucona B1, anna@kino.pl: Bilety B1 oplacone: 114.00]",
                withMails(Payment.DECLINED, (mailer, p) ->
                        new pl.training.workshop.m7.s15_behaviourvector.step1.TicketCheckout(mailer::add)
                                .pay(p.booking(), p.card())));
    }

    @TestFactory
    Stream<DynamicTest> fromStep2MailsAreCorrect() {
        return Scene.<Payment, String>variants()
                .variant("step2", p -> withMails(p, (mailer, payment) ->
                        new pl.training.workshop.m7.s15_behaviourvector.step2.TicketCheckout(mailer::add)
                                .pay(payment.booking(), payment.card())))
                .variant("step3", p -> withMails(p, (mailer, payment) ->
                        new pl.training.workshop.m7.s15_behaviourvector.step3.TicketCheckout(
                                mailer::add, (card, amount) -> !card.endsWith("0000"))
                                .pay(payment.booking(), payment.card())))
                .expect("sukces", Payment.SUCCESS, "OK [anna@kino.pl: Bilety B1 oplacone: 114.00]")
                .expect("karta odrzucona", Payment.DECLINED, "DECLINED [anna@kino.pl: Platnosc odrzucona B1]")
                .expect("juz oplacona", Payment.ALREADY_PAID, "ERROR: status PAID []")
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> step3ObservesTheFullVector() {
        return Scene.<Payment, String>variants()
                .variant("step3", S15BehaviourVectorTest::fullVector)
                .expect("sukces: najpierw obciazenie, potem mail, status PAID", Payment.SUCCESS,
                        "wynik=OK; zdarzenia=[CHARGE 4111111111111111 114.00, "
                                + "MAIL anna@kino.pl: Bilety B1 oplacone: 114.00]; status=PAID")
                .expect("odrzucenie: status bez zmian", Payment.DECLINED,
                        "wynik=DECLINED; zdarzenia=[CHARGE 4111111111110000 114.00, "
                                + "MAIL anna@kino.pl: Platnosc odrzucona B1]; status=NEW")
                .expect("juz oplacona: zadnych efektow", Payment.ALREADY_PAID,
                        "wynik=ERROR: status PAID; zdarzenia=[]; status=PAID")
                .expect("brak karty: typ i komunikat wyjatku, zadnych efektow", Payment.NO_CARD,
                        "wyjatek=NullPointerException: card; zdarzenia=[]; status=NEW")
                .tests();
    }

    /** Wynik + lista maili (dostępne od kroku 1). */
    private static String withMails(Payment payment, BiFunction<MailLog, Payment, String> pay) {
        MailLog mails = new MailLog();
        String result = pay.apply(mails, payment);
        return result + " " + mails.entries;
    }

    /** Pełny wektor: wynik albo wyjątek, efekty w kolejności, stan po operacji. */
    private static String fullVector(Payment payment) {
        List<String> events = new ArrayList<>();
        var checkout = new pl.training.workshop.m7.s15_behaviourvector.step3.TicketCheckout(
                (to, text) -> events.add("MAIL " + to + ": " + text),
                (card, amount) -> {
                    events.add("CHARGE " + card + " " + amount);
                    return !card.endsWith("0000");
                });
        Booking booking = payment.booking();
        Function<String, String> vector = outcome -> outcome + "; zdarzenia=" + events + "; status=" + booking.status();
        try {
            return vector.apply("wynik=" + checkout.pay(booking, payment.card()));
        } catch (RuntimeException e) {
            return vector.apply("wyjatek=" + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    private static final class MailLog {
        private final List<String> entries = new ArrayList<>();

        void add(String to, String text) {
            entries.add(to + ": " + text);
        }
    }
}
