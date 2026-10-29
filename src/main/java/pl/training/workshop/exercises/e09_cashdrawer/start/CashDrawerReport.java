package pl.training.workshop.exercises.e09_cashdrawer.start;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import pl.training.workshop.exercises.e09_cashdrawer.DrawerDay;
import pl.training.workshop.shared.Money;

/**
 * Start: raport zamknięcia kasy. Trzy operacje na wpisach szuflady (wpływ na gotówkę, linia
 * eksportu fiskalnego, linia audytu) to trzy łańcuchy instanceof zakończone wyjątkiem w runtime:
 * nowy rodzaj wpisu kompiluje się bez błędu i wybucha przy zamknięciu dnia. Podział rabatu
 * grupowego na bilety wybiera łańcuch if po nazwie trybu z konfiguracji, wymieszany z regułą
 * "10+ biletów = -10%" i z poprawką zaokrąglenia, która musi dać dokładnie kwotę rabatu.
 */
public final class CashDrawerReport {
    private static final int GROUP_SIZE = 10;
    private static final int GROUP_PERCENT = 10;
    private static final Money CENT = Money.of("0.01");

    public String render(DrawerDay day) {
        List<DrawerEntry> entries = day.lines().stream().map(DrawerEntries::from).toList();
        Money cash = Money.ZERO;
        StringBuilder fiscal = new StringBuilder();
        StringBuilder audit = new StringBuilder();
        for (DrawerEntry entry : entries) {
            cash = cash.plus(cashEffect(entry));
            String fiscalLine = fiscalLine(entry);
            if (!fiscalLine.isEmpty()) {
                fiscal.append(fiscalLine).append('\n');
            }
            audit.append(auditLine(entry)).append('\n');
        }
        return "Gotowka w szufladzie: " + cash + '\n'
                + "Eksport fiskalny:\n" + fiscal
                + "Audyt:\n" + audit
                + groupDiscount(day.groupTickets(), day.splitMode()) + '\n';
    }

    private Money cashEffect(DrawerEntry entry) {
        if (entry instanceof CashSale sale) {
            return sale.amount();
        } else if (entry instanceof CardSale) {
            return Money.ZERO;
        } else if (entry instanceof Refund refund) {
            return Money.ZERO.minus(refund.amount());
        } else if (entry instanceof Deposit deposit) {
            return deposit.amount();
        }
        throw new IllegalArgumentException("unknown entry: " + entry);
    }

    /** Format drukarki fiskalnej: rodzaj;numer;kwota;stawka VAT[;karta]. Pogotowie kasowe nie jest fiskalne. */
    private String fiscalLine(DrawerEntry entry) {
        if (entry instanceof CashSale sale) {
            return "S;" + sale.reference() + ";" + sale.amount() + ";8";
        } else if (entry instanceof CardSale card) {
            return "K;" + card.reference() + ";" + card.amount() + ";8;" + card.cardLast4();
        } else if (entry instanceof Refund refund) {
            return "Z;" + refund.reference() + ";-" + refund.amount() + ";8";
        } else if (entry instanceof Deposit) {
            return "";
        }
        throw new IllegalArgumentException("unknown entry: " + entry);
    }

    private String auditLine(DrawerEntry entry) {
        if (entry instanceof CashSale sale) {
            return "CASH " + sale.reference() + " " + sale.amount();
        } else if (entry instanceof CardSale card) {
            return "CARD " + card.reference() + " " + card.amount() + " karta ****" + card.cardLast4();
        } else if (entry instanceof Refund refund) {
            return "REFUND " + refund.reference() + " -" + refund.amount() + " powod: " + refund.reason();
        } else if (entry instanceof Deposit deposit) {
            return "DEPOSIT " + deposit.amount() + " " + deposit.note();
        }
        throw new IllegalArgumentException("unknown entry: " + entry);
    }

    private String groupDiscount(List<Money> tickets, String mode) {
        if (tickets.size() < GROUP_SIZE) {
            return "Rabat grupowy: brak (" + tickets.size() + " biletow)";
        }
        Money sum = Money.ZERO;
        for (Money ticket : tickets) {
            sum = sum.plus(ticket);
        }
        Money discount = sum.percent(GROUP_PERCENT);
        List<Money> shares = new ArrayList<>();
        if (mode.equals("PROPORTIONAL")) {
            Money given = Money.ZERO;
            for (int i = 0; i < tickets.size() - 1; i++) {
                Money share = new Money(discount.amount().multiply(tickets.get(i).amount())
                        .divide(sum.amount(), 2, RoundingMode.HALF_UP));
                shares.add(share);
                given = given.plus(share);
            }
            shares.add(discount.minus(given));
        } else if (mode.equals("LAST")) {
            for (int i = 0; i < tickets.size() - 1; i++) {
                shares.add(Money.ZERO);
            }
            shares.add(discount);
        } else if (mode.equals("EQUAL")) {
            Money base = new Money(discount.amount()
                    .divide(BigDecimal.valueOf(tickets.size()), 2, RoundingMode.DOWN));
            int restCents = discount.minus(base.times(tickets.size())).amount().movePointRight(2).intValueExact();
            for (int i = 0; i < tickets.size(); i++) {
                shares.add(i < restCents ? base.plus(CENT) : base);
            }
        } else {
            throw new IllegalArgumentException("unknown split mode: " + mode);
        }
        return "Rabat grupowy " + mode + ": " + discount + " = "
                + shares.stream().map(Money::toString).collect(Collectors.joining(" + "));
    }
}
