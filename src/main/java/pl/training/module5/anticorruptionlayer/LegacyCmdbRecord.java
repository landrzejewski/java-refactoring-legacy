package pl.training.module5.anticorruptionlayer;

/**
 * Model starego CMDB: kryptyczne kody środowisk ({@code "P"/"T"/"D"}), status jako
 * liczba ({@code 1} aktywne, {@code 0} wyłączone, {@code 9} wycofane) i data ostatniego
 * wdrożenia jako tekst {@code yyyyMMdd}.
 */
public record LegacyCmdbRecord(String nm, String envCd, int statFlg, String lstDplyDt) {
}
