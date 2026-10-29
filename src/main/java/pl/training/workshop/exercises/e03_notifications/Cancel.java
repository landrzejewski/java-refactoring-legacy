package pl.training.workshop.exercises.e03_notifications;

/** Odwolanie seansu z podanego powodu. */
public record Cancel(String reason) implements Change {
}
