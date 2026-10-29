package pl.training.workshop.exercises.e09_cashdrawer.step2;

import java.util.List;
import java.util.stream.Collectors;

import pl.training.workshop.exercises.e09_cashdrawer.DrawerDay;
import pl.training.workshop.shared.Money;

/**
 * Krok 2: raport składa się z trzech odwiedzających i reguły rabatu grupowego z wariantem
 * podziału wybranym po nazwie w DiscountSplits. Nowy rodzaj wpisu = zmiana każdego
 * odwiedzającego (kompilator wskaże gdzie), nowa operacja albo nowy tryb podziału = jedna klasa.
 * Uwaga: wybór wariantu zapada przed sprawdzeniem wielkości grupy, więc nieznany tryb
 * zgłasza się teraz także dla grupy poniżej 10 biletów - świadoma zmiana opisana w teście.
 */
public final class CashDrawerReport {
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
        GroupDiscount rule = new GroupDiscount(DiscountSplits.forMode(mode));
        return rule.allocate(tickets)
                .map(allocation -> "Rabat grupowy " + mode + ": " + allocation.discount() + " = "
                        + allocation.shares().stream().map(Money::toString).collect(Collectors.joining(" + ")))
                .orElse("Rabat grupowy: brak (" + tickets.size() + " biletow)");
    }
}
