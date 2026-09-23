package pl.training.workshop.m4.s11_encapsulatecollection;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

/**
 * Trzy kontrakty kolekcji - tabela ze slajdu jako test. Dla każdego wariantu sprawdzamy:
 * czy klient może zmienić listę z gettera i czy WCZEŚNIEJ pobrana lista widzi późniejsze zmiany właściciela.
 * Tu CELOWO nie ma równoważności: step2 i step3 zmieniają kontrakt gettera.
 */
final class S11CollectionContractTest {
    private static final Seat LATER = new Seat(10, 2);

    @Test
    void startPublicFieldIsTheLiveMutableList() {
        var booking = new pl.training.workshop.m4.s11_encapsulatecollection.start.Booking();
        assertEquals("klient zmienia: tak, widzi zmiany: tak", describe(() -> booking.seats, booking.seats::add));
    }

    @Test
    void step1GetterStillReturnsTheSameAlias() {
        var booking = new pl.training.workshop.m4.s11_encapsulatecollection.step1.Booking();
        assertEquals("klient zmienia: tak, widzi zmiany: tak", describe(booking::seats, booking::addSeat));
    }

    @Test
    void step2UnmodifiableListIsAReadOnlyLiveView() {
        var booking = new pl.training.workshop.m4.s11_encapsulatecollection.step2.Booking();
        assertEquals("klient zmienia: nie, widzi zmiany: tak", describe(booking::seats, booking::addSeat));
    }

    @Test
    void step3CopyOfIsASnapshot() {
        var booking = new pl.training.workshop.m4.s11_encapsulatecollection.step3.Booking();
        assertEquals("klient zmienia: nie, widzi zmiany: nie", describe(booking::seats, booking::addSeat));
    }

    private static String describe(Supplier<List<Seat>> getter, Consumer<Seat> ownerAdds) {
        List<Seat> seenByClient = getter.get();
        boolean clientCanModify;
        try {
            seenByClient.add(new Seat(1, 1));
            seenByClient.remove(new Seat(1, 1));
            clientCanModify = true;
        } catch (UnsupportedOperationException e) {
            clientCanModify = false;
        }
        ownerAdds.accept(LATER);
        boolean seesLaterChanges = seenByClient.contains(LATER);
        return "klient zmienia: " + (clientCanModify ? "tak" : "nie")
                + ", widzi zmiany: " + (seesLaterChanges ? "tak" : "nie");
    }
}
