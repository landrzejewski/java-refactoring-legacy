package pl.training.workshop.exercises.e03_notifications.start;

import pl.training.workshop.exercises.e03_notifications.Customer;
import pl.training.workshop.exercises.e03_notifications.Screening;

import java.util.List;

public record MovedEvent(Screening before, Screening after, List<Customer>  customers) {
}
