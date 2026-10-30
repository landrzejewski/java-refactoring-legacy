package pl.training.workshop.exercises.e03_notifications.start;

import pl.training.workshop.exercises.e03_notifications.Customer;

public record Notification(Customer customer, String subject, String body) {
}
