package pl.training.workshop.exercises.e07_documents.step1;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import pl.training.workshop.exercises.e07_documents.Channel;
import pl.training.workshop.exercises.e07_documents.PrintOptions;
import pl.training.workshop.exercises.e07_documents.Sale;
import pl.training.workshop.shared.Money;

/**
 * Krok 1: trzy switch-e zastąpione jednym wyborem rodziny. Opcjonalne sekcje nadal
 * są łańcuchem if z obliczeniami w środku - to zostaje na krok 2.
 */
public final class SalesDocuments {
    public String print(Sale sale, Channel channel, PrintOptions options) {
        if (sale == null) {
            throw new IllegalArgumentException("sale must not be null");
        }
        if (channel == null) {
            throw new IllegalArgumentException("channel must not be null");
        }
        if (options == null) {
            throw new IllegalArgumentException("options must not be null");
        }
        if (sale.ticketPrice().compareTo(Money.ZERO) < 0) {
            throw new IllegalArgumentException("ticket price must not be negative");
        }
        if (sale.barPrice().compareTo(Money.ZERO) < 0) {
            throw new IllegalArgumentException("bar price must not be negative");
        }
        DocumentFactory family = DocumentFactories.forChannel(channel);
        ReceiptLayout receiptLayout = family.receiptLayout();
        StringBuilder document = new StringBuilder(new DocumentComposer(family).compose(sale));
        if (options.withVat()) {
            List<String> lines = new ArrayList<>();
            lines.add("VAT 8% bilet: " + vat(sale.ticketPrice(), 8));
            if (sale.barPrice().isGreaterThan(Money.ZERO)) {
                lines.add("VAT 23% bar: " + vat(sale.barPrice(), 23));
            }
            document.append("\n").append(receiptLayout.section(lines));
        }
        if (options.withPromo()) {
            document.append("\n").append(receiptLayout.section(
                    List.of("Kod KINO10: -10% na nastepny bilet online")));
        }
        if (options.withLoyaltyFooter()) {
            int earned = sale.ticketPrice().amount().intValue() / 10;
            int total = sale.loyaltyPoints() + earned;
            List<String> lines = new ArrayList<>();
            lines.add("Punkty: " + sale.loyaltyPoints() + " + " + earned + " = " + total);
            if (total >= 100) {
                lines.add("Odbierz darmowy bilet 2D");
            }
            document.append("\n").append(receiptLayout.section(lines));
        }
        return document.toString();
    }

    /** VAT zawarty w cenie brutto, np. 8% z 40.00 to 2.96. */
    private static Money vat(Money gross, int rate) {
        return new Money(gross.amount()
                .multiply(BigDecimal.valueOf(rate))
                .divide(BigDecimal.valueOf(100 + rate), 2, RoundingMode.HALF_UP));
    }
}
