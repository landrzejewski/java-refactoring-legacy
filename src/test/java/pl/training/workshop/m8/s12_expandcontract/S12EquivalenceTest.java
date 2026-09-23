package pl.training.workshop.m8.s12_expandcontract;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.shared.Money;
import pl.training.workshop.support.Scene;

/** Test równoważności: w każdym kroku zapisana rezerwacja wraca w niezmienionej postaci. */
final class S12EquivalenceTest {
    static final Booking ANNA = new Booking("B1", "anna@kino.pl", List.of("A5", "A10"), Money.of("84.00"));
    static final Booking JAN = new Booking("B2", "jan@kino.pl", List.of("C7"), Money.of("25.00"));

    @TestFactory
    Stream<DynamicTest> everyStepReadsBackWhatItSaved() {
        return Scene.<Booking, Optional<Booking>>variants()
                .variant("start", booking -> {
                    var repository = new pl.training.workshop.m8.s12_expandcontract.start.BookingRepository(new BookingTable());
                    repository.save(booking);
                    return repository.find(booking.id());
                })
                .variant("step1", booking -> {
                    var repository = new pl.training.workshop.m8.s12_expandcontract.step1.BookingRepository(new BookingTable());
                    repository.save(booking);
                    return repository.find(booking.id());
                })
                .variant("step2", booking -> {
                    var repository = new pl.training.workshop.m8.s12_expandcontract.step2.BookingRepository(new BookingTable());
                    repository.save(booking);
                    return repository.find(booking.id());
                })
                .variant("step3", booking -> {
                    var repository = new pl.training.workshop.m8.s12_expandcontract.step3.BookingRepository(new BookingTable());
                    repository.save(booking);
                    return repository.find(booking.id());
                })
                .variant("step4", booking -> {
                    var repository = new pl.training.workshop.m8.s12_expandcontract.step4.BookingRepository(new BookingTable());
                    repository.save(booking);
                    return repository.find(booking.id());
                })
                .expect("dwa miejsca", ANNA, Optional.of(ANNA))
                .expect("jedno miejsce", JAN, Optional.of(JAN))
                .tests();
    }
}
