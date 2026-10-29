package pl.training.workshop.exercises.e07_documents;

import pl.training.workshop.shared.Money;

/**
 * Stabilny kontrakt sceny: sprzedaż jednego biletu z ewentualnym zakupem w barze.
 * {@code loyaltyPoints} to stan konta klienta przed tą sprzedażą.
 */
public record Sale(String number, String movie, String seat, Money ticketPrice, Money barPrice, int loyaltyPoints) {
}
