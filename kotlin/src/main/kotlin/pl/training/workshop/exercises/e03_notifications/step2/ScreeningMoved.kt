package pl.training.workshop.exercises.e03_notifications.step2

import pl.training.workshop.exercises.e03_notifications.Customer
import pl.training.workshop.exercises.e03_notifications.Screening

/** Krok 2: zdarzenie "seans przeniesiony" - wszystko, czego odbiorcy potrzebuja, bez odwolania do schedulera. */
data class ScreeningMoved(val before: Screening, val after: Screening, val ticketHolders: List<Customer>)
