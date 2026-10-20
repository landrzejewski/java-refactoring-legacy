package pl.training.workshop.m5.s31_anticorruptionlayer;

/**
 * Model z systemu dystrybutora (nie zmieniamy go): same napisy i cudze konwencje.
 * Status "A" aktywna / "S" zawieszona, daty "yyyyMMdd", minimalna gwarancja w groszach
 * jako tekst, udział jako "45%" (czasem ze spacjami: " 40 %"), tytuł wielkimi literami.
 */
public record DistributorRecord(String filmCode, String titleUpper, String statusCode,
                                String validFrom, String validTo, String minGuaranteeCents, String share) {
}
