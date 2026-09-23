package pl.training.workshop.m6.s01_strategy;

import pl.training.workshop.shared.Money;

/**
 * Stabilny kontrakt sceny: cena bazowa formatu, typ biletu (legacy: N, S, E, C)
 * i nazwa programu zniżek skonfigurowanego w kinie (STANDARD, STUDENT_WEEK, PREMIERE).
 */
public record PriceRequest(Money base, String ticketType, String program) {
}
