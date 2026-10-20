package pl.training.module5.anticorruptionlayer.after.acl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import pl.training.module5.anticorruptionlayer.LegacyCmdbRecord;
import pl.training.module5.anticorruptionlayer.after.Environment;

/**
 * Tłumacz między modelami: jedyne miejsce, które zna kody i formaty CMDB.
 */
final class CmdbTranslator {
    private static final DateTimeFormatter CMDB_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");

    private CmdbTranslator() {
    }

    static Environment toEnvironment(LegacyCmdbRecord record) {
        return new Environment(
                record.nm(),
                tier(record.envCd()),
                record.statFlg() == 1,
                date(record.lstDplyDt()));
    }

    private static Environment.Tier tier(String code) {
        return switch (code) {
            case "P" -> Environment.Tier.PRODUCTION;
            case "T" -> Environment.Tier.TEST;
            case "D" -> Environment.Tier.DEVELOPMENT;
            case null, default -> throw new IllegalArgumentException(
                    "unknown CMDB environment code: " + code);
        };
    }

    private static LocalDate date(String text) {
        try {
            return LocalDate.parse(text, CMDB_DATE);
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("invalid CMDB date: " + text, exception);
        }
    }
}
