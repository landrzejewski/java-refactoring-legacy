package pl.training.workshop.m8.s12_expandcontract;

import java.util.List;

import pl.training.workshop.shared.Money;

/** Stabilny kontrakt sceny: rezerwacja zapisywana w bazie. */
public record Booking(String id, String email, List<String> seats, Money total) {
    public Booking {
        seats = List.copyOf(seats);
    }
}
