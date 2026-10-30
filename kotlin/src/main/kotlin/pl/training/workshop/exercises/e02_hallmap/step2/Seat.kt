package pl.training.workshop.exercises.e02_hallmap.step2

import pl.training.workshop.shared.Money

/** Krok 2: miejsce - liść drzewa, bez zmian; kopie danych kategorii znikną w kroku 3. */
class Seat internal constructor(
    internal val label: String,
    internal val code: Char,
    internal val categoryName: String,
    internal val surcharge: Money,
    internal val icon: String,
    internal val description: String,
) : HallElement {
    override fun capacity(): Int = 1

    override fun surcharges(): Money = surcharge
}
