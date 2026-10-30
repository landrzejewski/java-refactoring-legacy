package pl.training.workshop.exercises.e07_documents

import pl.training.workshop.shared.Money

/**
 * Stabilny kontrakt sceny: sprzedaż jednego biletu z ewentualnym zakupem w barze.
 * `loyaltyPoints` to stan konta klienta przed tą sprzedażą.
 */
data class Sale(
    val number: String,
    val movie: String,
    val seat: String,
    val ticketPrice: Money,
    val barPrice: Money,
    val loyaltyPoints: Int
)
