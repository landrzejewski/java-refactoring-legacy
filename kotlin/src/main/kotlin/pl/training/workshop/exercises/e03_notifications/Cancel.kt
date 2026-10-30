package pl.training.workshop.exercises.e03_notifications

/** Odwolanie seansu z podanego powodu. */
data class Cancel(val reason: String?) : Change
