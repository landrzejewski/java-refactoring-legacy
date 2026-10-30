package pl.training.workshop.exercises.e03_notifications

/** Przeniesienie seansu na inny termin i do innej sali. */
data class Move(val newStart: String, val newHall: String) : Change
