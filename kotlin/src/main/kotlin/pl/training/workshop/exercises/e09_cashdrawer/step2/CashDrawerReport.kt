package pl.training.workshop.exercises.e09_cashdrawer.step2

import pl.training.workshop.exercises.e09_cashdrawer.DrawerDay
import pl.training.workshop.shared.Money

/**
 * Krok 2: raport składa się z trzech odwiedzających i reguły rabatu grupowego z wariantem
 * podziału wybranym po nazwie w DiscountSplits. Nowy rodzaj wpisu = zmiana każdego
 * odwiedzającego (kompilator wskaże gdzie), nowa operacja albo nowy tryb podziału = jedna klasa.
 * Uwaga: wybór wariantu zapada przed sprawdzeniem wielkości grupy, więc nieznany tryb
 * zgłasza się teraz także dla grupy poniżej 10 biletów - świadoma zmiana opisana w teście.
 */
class CashDrawerReport {
    private val cashEffect = CashEffectVisitor()
    private val fiscalLine = FiscalLineVisitor()
    private val auditLine = AuditLineVisitor()

    fun render(day: DrawerDay): String {
        val entries = day.lines.map(DrawerEntries::from)
        var cash = Money.ZERO
        val fiscal = StringBuilder()
        val audit = StringBuilder()
        for (entry in entries) {
            cash += entry.accept(cashEffect)
            val line = entry.accept(fiscalLine)
            if (line.isNotEmpty()) {
                fiscal.append(line).append('\n')
            }
            audit.append(entry.accept(auditLine)).append('\n')
        }
        return "Gotowka w szufladzie: $cash\n" +
            "Eksport fiskalny:\n$fiscal" +
            "Audyt:\n$audit" +
            groupDiscount(day.groupTickets, day.splitMode) + '\n'
    }

    private fun groupDiscount(tickets: List<Money>, mode: String): String {
        val rule = GroupDiscount(DiscountSplits.forMode(mode))
        return rule.allocate(tickets)
            ?.let { allocation -> "Rabat grupowy $mode: ${allocation.discount} = " + allocation.shares.joinToString(" + ") }
            ?: "Rabat grupowy: brak (${tickets.size} biletow)"
    }
}
