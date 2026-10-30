package pl.training.workshop.exercises.e03_notifications.step2

import pl.training.workshop.exercises.e03_notifications.Customer
import pl.training.workshop.exercises.e03_notifications.Screening

/** Krok 2: zdarzenie "seans odwolany". */
data class ScreeningCancelled(val screening: Screening, val reason: String, val ticketHolders: List<Customer>)
