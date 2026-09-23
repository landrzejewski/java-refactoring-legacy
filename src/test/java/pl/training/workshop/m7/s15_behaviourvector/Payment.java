package pl.training.workshop.m7.s15_behaviourvector;

import pl.training.workshop.shared.Money;

/** Wejście testów sceny: stan rezerwacji przed płatnością i numer karty. */
record Payment(BookingStatus statusBefore, String card) {
    static final Payment SUCCESS = new Payment(BookingStatus.NEW, "4111111111111111");
    static final Payment DECLINED = new Payment(BookingStatus.NEW, "4111111111110000");
    static final Payment ALREADY_PAID = new Payment(BookingStatus.PAID, "4111111111111111");
    static final Payment NO_CARD = new Payment(BookingStatus.NEW, null);

    Booking booking() {
        return new Booking("B1", "anna@kino.pl", Money.of("114.00"), statusBefore);
    }
}
