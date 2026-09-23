package pl.training.workshop.m8.s01_branchbyabstraction;

import java.time.LocalDateTime;

/**
 * Stabilny kontrakt sceny: seans w kodach legacy.
 *
 * @param format     1 = 2D, 2 = 3D, 3 = IMAX (jak w CinemaManager)
 * @param vipFromRow pierwszy rząd VIP w sali
 */
public record Screening(String title, int format, LocalDateTime start, int vipFromRow) {
}
