package pl.training.workshop.m5.s30_facade;

import java.util.List;
import java.util.Objects;

/** Stabilny kontrakt sceny: zamówienie biletów jednego typu na kilka miejsc jednego seansu. */
public record TicketOrder(String screening, List<String> seats, String ticketType, String card, String email) {
    public TicketOrder {
        Objects.requireNonNull(screening, "screening");
        seats = List.copyOf(seats);
        Objects.requireNonNull(ticketType, "ticketType");
    }
}
