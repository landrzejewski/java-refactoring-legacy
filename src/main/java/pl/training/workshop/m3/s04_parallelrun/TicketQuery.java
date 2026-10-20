package pl.training.workshop.m3.s04_parallelrun;

import java.time.LocalTime;

/**
 * Stabilny kontrakt sceny: pytanie o cenę jednego biletu.
 *
 * @param format 2D, 3D, IMAX (inne formaty legacy wycenia na 0.00)
 * @param type   NORMAL, STUDENT, SENIOR, CHILD
 * @param row    rząd miejsca (10 i dalej to VIP)
 */
public record TicketQuery(String format, String type, LocalTime start, int row) {
    @Override
    public String toString() {
        return format + " " + type + " " + start + " rzad " + row;
    }
}
