package pl.training.workshop.exercises.e09_cashdrawer.step1;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import pl.training.workshop.exercises.e09_cashdrawer.DrawerDay;
import pl.training.workshop.shared.Money;

/**
 * Krok 1: trzy operacje na wpisach to trzy odwiedzający; raport tylko sumuje i skleja ich wyniki.
 * W klasie nie ma już instanceof. Podział rabatu grupowego nadal łańcuchem if po trybie.
 */
public final class CashDrawerReport {
    private static final int GROUP_SIZE = 10;
    private static final int GROUP_PERCENT = 10;
    private static final Money CENT = Money.of("0.01");

    private final CashEffectVisitor cashEffect = new CashEffectVisitor();
    private final FiscalLineVisitor fiscalLine = new FiscalLineVisitor();
    private final AuditLineVisitor auditLine = new AuditLineVisitor();

    public String render(DrawerDay day) {
        List<DrawerEntry> entries = day.lines().stream().map(DrawerEntries::from).toList();
        Money cash = Money.ZERO;
        StringBuilder fiscal = new StringBuilder();
        StringBuilder audit = new StringBuilder();
        for (DrawerEntry entry : entries) {
            cash = cash.plus(entry.accept(cashEffect));
            String line = entry.accept(fiscalLine);
            if (!line.isEmpty()) {
                fiscal.append(line).append('\n');
            }
            audit.append(entry.accept(auditLine)).append('\n');
        }
        return "Gotowka w szufladzie: " + cash + '\n'
                + "Eksport fiskalny:\n" + fiscal
                + "Audyt:\n" + audit
                + groupDiscount(day.groupTickets(), day.splitMode()) + '\n';
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
