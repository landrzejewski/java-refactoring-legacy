package pl.training.workshop.m7.s13_godclass.step4;

/**
 * Krok 2: jedyne miejsce, które zna treść powiadomień i statyczny LegacyMailer.
 * Kolejny krok kampanii mógłby tu wprowadzić seam (interfejs mailera) - bez dotykania CinemaManager.
 */
final class NotificationService {
    void bookingCreated(String email, String bookingId, String title, String[] seats, double total) {
        LegacyMailer.send(email, "Rezerwacja " + bookingId,
                "Film: " + title + ", miejsca: " + String.join(",", seats)
                        + ", do zaplaty: " + Formats.amount(total));
    }

    void paymentDeclined(String email, String bookingId) {
        LegacyMailer.send(email, "Platnosc odrzucona", "Rezerwacja " + bookingId);
    }

    void ticketsPaid(String email, String phone, String bookingId, double paid, int points) {
        LegacyMailer.send(email, "Bilety " + bookingId, "Oplacono " + Formats.amount(paid)
                + ", punkty: +" + points);
        if (phone != null) {
            LegacyMailer.sms(phone, "CineLegacy: bilety " + bookingId + " oplacone");
        }
    }

    void bookingExpired(String email, String bookingId) {
        LegacyMailer.send(email, "Rezerwacja wygasla", bookingId);
    }

    void bookingCancelled(String email, String bookingId, double refund) {
        LegacyMailer.send(email, "Anulowano " + bookingId, "Zwrot: " + Formats.amount(refund));
    }
}
