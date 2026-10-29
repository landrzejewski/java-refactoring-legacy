package pl.training.workshop.exercises.e03_notifications;

/** Przeniesienie seansu na inny termin i do innej sali. */
public record Move(String newStart, String newHall) implements Change {
}
