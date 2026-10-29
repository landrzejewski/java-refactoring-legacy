package pl.training.workshop.exercises.e07_documents.start;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import pl.training.workshop.exercises.e07_documents.Channel;
import pl.training.workshop.exercises.e07_documents.PrintOptions;
import pl.training.workshop.exercises.e07_documents.Sale;
import pl.training.workshop.shared.Money;

/**
 * Start: punkt wejścia drukowania dokumentów sprzedaży. Trzy równoległe switch-e po kanale
 * wybierają po jednym elemencie zestawu (bilet, kod, paragon); nowy kanał to trzy nowe gałęzie,
 * a pomylenie jednej z nich kompiluje się bez ostrzeżenia. Dalej każda opcjonalna sekcja
 * (VAT, promocja, stopka lojalnościowa) to kolejny if z obliczeniami w środku tej samej metody.
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
        TicketLayout ticketLayout = ticketLayoutFor(channel);
        CodeRenderer codeRenderer = codeRendererFor(channel);
        ReceiptLayout receiptLayout = receiptLayoutFor(channel);
        StringBuilder document = new StringBuilder(
                new DocumentComposer(ticketLayout, codeRenderer, receiptLayout).compose(sale));
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

    private static TicketLayout ticketLayoutFor(Channel channel) {
        return switch (channel) {
            case KIOSK -> new KioskTicketLayout();
            case MOBILE -> new MobileTicketLayout();
            case BOX_OFFICE -> new BoxOfficeTicketLayout();
        };
    }

    private static CodeRenderer codeRendererFor(Channel channel) {
        return switch (channel) {
            case KIOSK -> new KioskCodeRenderer();
            case MOBILE -> new MobileCodeRenderer();
            case BOX_OFFICE -> new BoxOfficeCodeRenderer();
        };
    }

    private static ReceiptLayout receiptLayoutFor(Channel channel) {
        return switch (channel) {
            case KIOSK -> new KioskReceiptLayout();
            case MOBILE -> new MobileReceiptLayout();
            case BOX_OFFICE -> new BoxOfficeReceiptLayout();
        };
    }

    /** VAT zawarty w cenie brutto, np. 8% z 40.00 to 2.96. */
    private static Money vat(Money gross, int rate) {
        return new Money(gross.amount()
                .multiply(BigDecimal.valueOf(rate))
                .divide(BigDecimal.valueOf(100 + rate), 2, RoundingMode.HALF_UP));
    }
}
