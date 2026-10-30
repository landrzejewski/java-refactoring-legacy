package pl.training.workshop.exercises.e02_hallmap.step3

import pl.training.workshop.shared.Money

/**
 * Krok 3: kategoria miejsca - stan wspólny wielu miejsc, niemutowalny, więc bezpieczny do
 * współdzielenia. Nie wie, w którym rzędzie ani pod jakim numerem stoi fotel.
 */
class SeatCategory internal constructor(
    val code: Char,
    val name: String,
    val surcharge: Money,
    val icon: String,
    val description: String,
)
