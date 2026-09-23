package pl.training.workshop.m4.s06_inlinemethod;

import java.time.LocalTime;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/** Test równoważności: kasa i internet - te same kwoty po każdym kroku. Podklasa jest częścią testu! */
final class S06EquivalenceTest {
    static final Ticket IMAX_EVENING = new Ticket(3, LocalTime.of(20, 0));
    static final Ticket MORNING_3D = new Ticket(2, LocalTime.of(10, 0));
    static final Ticket NOON_2D = new Ticket(1, LocalTime.NOON);

    @TestFactory
    Stream<DynamicTest> boxOfficeTotalsStayTheSame() {
        return Scene.<Ticket, String>variants()
                .variant("start", t -> new pl.training.workshop.m4.s06_inlinemethod.start.TicketPricing()
                        .total(t).toPlainString())
                .variant("step1", t -> new pl.training.workshop.m4.s06_inlinemethod.step1.TicketPricing()
                        .total(t).toPlainString())
                .variant("step2", t -> new pl.training.workshop.m4.s06_inlinemethod.step2.TicketPricing()
                        .total(t).toPlainString())
                .expect("kasa: IMAX wieczorem", IMAX_EVENING, "40.00")
                .expect("kasa: 3D rano", MORNING_3D, "27.00")
                .expect("kasa: 2D 12:00", NOON_2D, "25.00")
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> onlineTotalsStayTheSame() {
        return Scene.<Ticket, String>variants()
                .variant("start", t -> new pl.training.workshop.m4.s06_inlinemethod.start.OnlineTicketPricing()
                        .total(t).toPlainString())
                .variant("step1", t -> new pl.training.workshop.m4.s06_inlinemethod.step1.OnlineTicketPricing()
                        .total(t).toPlainString())
                .variant("step2", t -> new pl.training.workshop.m4.s06_inlinemethod.step2.OnlineTicketPricing()
                        .total(t).toPlainString())
                .expect("online: IMAX wieczorem", IMAX_EVENING, "42.00")
                .expect("online: 3D rano", MORNING_3D, "29.00")
                .expect("online: 2D 12:00", NOON_2D, "27.00")
                .tests();
    }
}
