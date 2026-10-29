package pl.training.workshop.exercises.e03_notifications.step3;

import java.util.List;

import pl.training.workshop.exercises.e03_notifications.Customer;
import pl.training.workshop.exercises.e03_notifications.Screening;

/** Krok 3: zdarzenie "seans odwolany". */
public record ScreeningCancelled(Screening screening, String reason, List<Customer> ticketHolders) {
}
