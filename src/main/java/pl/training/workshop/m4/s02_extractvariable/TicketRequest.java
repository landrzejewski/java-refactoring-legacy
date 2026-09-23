package pl.training.workshop.m4.s02_extractvariable;

import java.time.LocalTime;

/**
 * Stabilny kontrakt sceny.
 *
 * @param format     legacy kod formatu: 1 = 2D, 2 = 3D, 3 = IMAX
 * @param type       legacy kod biletu: "N", "S" (student), "E" (senior), "C" (dziecko)
 * @param row        rząd miejsca albo {@code null} dla wolnej widowni (bez numerowanych miejsc)
 * @param ownGlasses klient ma własne okulary 3D
 */
public record TicketRequest(int format, String type, LocalTime start, Integer row,
                            boolean ownGlasses) {
}
