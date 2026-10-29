package pl.training.workshop.exercises.e03_notifications.step3;

import java.util.List;

import pl.training.workshop.exercises.e03_notifications.Customer;
import pl.training.workshop.exercises.e03_notifications.Screening;

/** Krok 3: zdarzenie "seans przeniesiony" - wszystko, czego odbiorcy potrzebuja, bez odwolania do schedulera. */
public record ScreeningMoved(Screening before, Screening after, List<Customer> ticketHolders) {
}
