package pl.training.workshop.m7.s02_methodobject;

import java.time.LocalTime;
import java.util.List;

/**
 * Stabilny kontrakt sceny: zamówienie (także grupowe) na jeden seans.
 *
 * @param format      2D, 3D albo IMAX
 * @param ticketTypes NORMAL, STUDENT, SENIOR albo CHILD - jeden wpis na bilet
 * @param vipSeats    ile z tych biletów to miejsca VIP
 */
public record GroupOrder(String format, LocalTime start, List<String> ticketTypes,
        int vipSeats, boolean ownGlasses, boolean online) {
}
