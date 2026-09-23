package pl.training.workshop.m4.s04_extractmethod;

import java.time.LocalTime;
import java.util.List;

/**
 * Stabilny kontrakt sceny - wspólny dla start i wszystkich kroków.
 *
 * @param format 2D, 3D albo IMAX
 * @param rows   numery rzędów kupionych miejsc (rząd 10 i dalej to VIP)
 */
public record Order(String title, String format, LocalTime start, List<Integer> rows) {
}
