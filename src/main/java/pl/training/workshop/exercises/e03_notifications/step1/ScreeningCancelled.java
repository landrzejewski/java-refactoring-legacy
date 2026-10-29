package pl.training.workshop.exercises.e03_notifications.step1;

import java.util.List;

import pl.training.workshop.exercises.e03_notifications.Customer;
import pl.training.workshop.exercises.e03_notifications.Screening;

/** Krok 1: zdarzenie "seans odwolany". */
public record ScreeningCancelled(Screening screening, String reason, List<Customer> ticketHolders) {
}
