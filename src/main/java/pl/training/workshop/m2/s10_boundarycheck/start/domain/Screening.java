package pl.training.workshop.m2.s10_boundarycheck.start.domain;

import java.time.LocalDateTime;

/** Pojęcie domenowe: seans. */
public record Screening(String title, LocalDateTime start) {
}
