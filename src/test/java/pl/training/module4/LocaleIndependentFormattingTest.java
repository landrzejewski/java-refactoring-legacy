package pl.training.module4;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Locale;

import org.junit.jupiter.api.Test;

import pl.training.module4.model.EquipmentType;
import pl.training.module4.model.RentalRequest;

final class LocaleIndependentFormattingTest {
    @Test
    void quoteFormattingDoesNotDependOnDefaultFormatLocale() {
        Locale originalLocale = Locale.getDefault(Locale.Category.FORMAT);
        try {
            Locale.setDefault(
                    Locale.Category.FORMAT,
                    Locale.forLanguageTag("ar-EG"));
            RentalRequest request = new RentalRequest(
                    "Acme",
                    EquipmentType.GENERATOR,
                    8,
                    true,
                    true);

            String approvedQuote =
                    new pl.training.module4.stage0.RentalQuoteService()
                            .createQuote(request);
            var stage1 = new pl.training.module4.stage1.RentalQuoteService();
            var stage2 = new pl.training.module4.stage2.RentalQuoteService();
            var stage3 = new pl.training.module4.stage3.RentalQuoteService();

            assertTrue(approvedQuote.contains("Days: 8\n"));
            assertAll(
                    () -> assertEquals(
                            approvedQuote,
                            stage1.createQuote(request)),
                    () -> assertEquals(
                            approvedQuote,
                            stage2.createQuote(request)),
                    () -> assertEquals(
                            approvedQuote,
                            stage3.createQuote(request)));
        } finally {
            Locale.setDefault(Locale.Category.FORMAT, originalLocale);
        }
    }
}
