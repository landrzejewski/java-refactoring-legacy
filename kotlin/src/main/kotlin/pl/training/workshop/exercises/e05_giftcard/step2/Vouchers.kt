package pl.training.workshop.exercises.e05_giftcard.step2

import pl.training.workshop.exercises.e05_giftcard.ScreeningFacts

/**
 * Krok 2: Vouchers nie zna już składni tekstu - parser buduje drzewo raz, a applies i describe
 * to dwie operacje na tym samym drzewie. Nowe pojęcie w regule to nowy węzeł i jedna gałąź
 * w parserze, bez dotykania tej klasy.
 */
object Vouchers {
    fun applies(rule: String, facts: ScreeningFacts): Boolean = VoucherRuleParser.parse(rule).applies(facts)

    fun describe(rule: String): String = VoucherRuleParser.parse(rule).describe()
}
