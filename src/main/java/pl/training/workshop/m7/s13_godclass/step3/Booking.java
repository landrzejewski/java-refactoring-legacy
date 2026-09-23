package pl.training.workshop.m7.s13_godclass.step3;

import java.time.LocalDateTime;

/**
 * Krok 3: rezerwacja jako typ zamiast Object[] {screeningId, email, phone, seats, types, web,
 * total, status, createdAt, card, ticketsSum}. Status zostaje kodem int jak w legacy -
 * zamiana na enum to kolejny, osobny krok.
 */
final class Booking {
    // status: 0 = NEW, 1 = PAID, 2 = USED, 3 = EXPIRED, 4 = CANCELLED
    private final String id;
    private final String screeningId;
    private final String email;
    private final String phone;
    private final String[] seats;
    private final String[] types;
    private final boolean web;
    private final double total;
    private final LocalDateTime createdAt;
    private final double ticketsSum;
    private int status;
    private String card;

    Booking(String id, String screeningId, String email, String phone, String[] seats, String[] types,
            boolean web, double total, LocalDateTime createdAt, double ticketsSum) {
        this.id = id;
        this.screeningId = screeningId;
        this.email = email;
        this.phone = phone;
        this.seats = seats;
        this.types = types;
        this.web = web;
        this.total = total;
        this.createdAt = createdAt;
        this.ticketsSum = ticketsSum;
    }

    String id() {
        return id;
    }

    String screeningId() {
        return screeningId;
    }

    String email() {
        return email;
    }

    String phone() {
        return phone;
    }

    String[] seats() {
        return seats;
    }

    String[] types() {
        return types;
    }

    boolean web() {
        return web;
    }

    double total() {
        return total;
    }

    LocalDateTime createdAt() {
        return createdAt;
    }

    double ticketsSum() {
        return ticketsSum;
    }

    int status() {
        return status;
    }

    void status(int status) {
        this.status = status;
    }

    String card() {
        return card;
    }

    void markPaid(String card) {
        this.status = 1;
        this.card = card;
    }
}
