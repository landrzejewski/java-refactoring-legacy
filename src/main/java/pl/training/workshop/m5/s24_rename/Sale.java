package pl.training.workshop.m5.s24_rename;

import java.math.BigDecimal;

/** Stabilny kontrakt sceny: jedna sprzedaż biletów na film. */
public record Sale(String title, int tickets, BigDecimal amount, boolean online) {
}
