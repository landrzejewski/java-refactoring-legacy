package pl.training.workshop.m7.s05_breakmethod;

import java.time.LocalTime;

/** Stabilny kontrakt sceny: seans w repertuarze dnia. */
public record Screening(String title, String format, LocalTime start, int hall, boolean cancelled) {
}
