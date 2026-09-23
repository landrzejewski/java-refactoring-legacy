package pl.training.workshop.m8.s06_reviewableseries;

import java.time.LocalDateTime;

/**
 * Stabilny kontrakt sceny: pytanie o cenę jednego biletu.
 *
 * @param format 2D, 3D albo IMAX
 * @param type   NORMAL, STUDENT, SENIOR, CHILD
 * @param row    rząd (10 i dalej to VIP)
 */
public record TicketQuery(String format, String type, LocalDateTime start, int row) {
}
