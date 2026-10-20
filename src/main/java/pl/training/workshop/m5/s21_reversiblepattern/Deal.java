package pl.training.workshop.m5.s21_reversiblepattern;

/**
 * Stabilny kontrakt sceny - umowa z dystrybutorem filmu.
 *
 * @param model PERCENT (procent od przychodu z biletów) albo FESTIVAL (stawka z systemu festiwalu)
 */
public record Deal(String title, String model) {
}
