package pl.training.workshop.m4.s01_rename;

import java.math.BigDecimal;

/** Stabilny kontrakt sceny: jedna sprzedaż biletów na film. */
public record Sale(String title, int tickets, BigDecimal amount, boolean online) {
}
