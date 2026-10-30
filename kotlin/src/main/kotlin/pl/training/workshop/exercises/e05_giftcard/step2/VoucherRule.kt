package pl.training.workshop.exercises.e05_giftcard.step2

import pl.training.workshop.exercises.e05_giftcard.ScreeningFacts

/**
 * Krok 2: wyrażenie reguły vouchera. Każdy węzeł umie się ocenić dla faktów o seansie
 * i opisać dla kasjera, więc obie operacje chodzą po tym samym drzewie zamiast po dwóch
 * niezależnych rozbiorach tekstu.
 */
sealed interface VoucherRule {
    fun applies(facts: ScreeningFacts): Boolean

    fun describe(): String
}
