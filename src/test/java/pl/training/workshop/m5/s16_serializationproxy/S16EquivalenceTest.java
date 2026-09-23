package pl.training.workshop.m5.s16_serializationproxy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UncheckedIOException;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.shared.Money;
import pl.training.workshop.support.Scene;

/** Test równoważności: opis biletu po zapisie i odczycie w tej samej wersji oraz cena - bez zmian. */
final class S16EquivalenceTest {
    @TestFactory
    Stream<DynamicTest> everyStepRoundTripsTicketTheSameWay() {
        return Scene.<String, String>variants()
                .variant("start", id -> ((pl.training.workshop.m5.s16_serializationproxy.start.StudentTicket) roundTrip(
                        new pl.training.workshop.m5.s16_serializationproxy.start.StudentTicket("Amator", "F3", id))).describe())
                .variant("step1", id -> ((pl.training.workshop.m5.s16_serializationproxy.step1.StudentTicket) roundTrip(
                        new pl.training.workshop.m5.s16_serializationproxy.step1.StudentTicket("Amator", "F3", id))).describe())
                .variant("step2", id -> ((pl.training.workshop.m5.s16_serializationproxy.step2.StudentTicket) roundTrip(
                        new pl.training.workshop.m5.s16_serializationproxy.step2.StudentTicket("Amator", "F3", id))).describe())
                .variant("step3", id -> ((pl.training.workshop.m5.s16_serializationproxy.step3.StudentTicket) roundTrip(
                        new pl.training.workshop.m5.s16_serializationproxy.step3.StudentTicket("Amator", "F3", id))).describe())
                .expect("bilet studencki", "S-123", "Amator F3 (legitymacja S-123)")
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> everyStepPricesStudentTicketTheSameWay() {
        return Scene.<String, String>variants()
                .variant("start", p -> new pl.training.workshop.m5.s16_serializationproxy.start.TicketPricing().studentPrice(Money.of(p)).toString())
                .variant("step1", p -> new pl.training.workshop.m5.s16_serializationproxy.step1.TicketPricing().studentPrice(Money.of(p)).toString())
                .variant("step2", p -> new pl.training.workshop.m5.s16_serializationproxy.step2.TicketPricing().studentPrice(Money.of(p)).toString())
                .variant("step3", p -> new pl.training.workshop.m5.s16_serializationproxy.step3.TicketPricing().studentPrice(Money.of(p)).toString())
                .expect("studencki 3D", "32.00", "24.00")
                .tests();
    }

    static Object roundTrip(Object value) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
                out.writeObject(value);
            }
            try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
                return in.readObject();
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }
}
