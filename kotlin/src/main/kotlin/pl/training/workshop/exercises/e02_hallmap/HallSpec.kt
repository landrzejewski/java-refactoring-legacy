package pl.training.workshop.exercises.e02_hallmap

/**
 * Stabilny kontrakt sceny: plan sali z systemu rezerwacji. Sala ma nazwę i sektory
 * (np. "Parter", "Balkon"), sektor ma rzędy, a rząd ma etykietę (np. "A") i układ miejsc
 * zapisany znakami: `N` normalne, `V` VIP (+10.00), `W` miejsce dla osoby
 * na wózku, `D` dwuosobowe (+6.00), `_` przejście (luka, która zużywa numer).
 */
data class HallSpec(val name: String, val sections: List<SectionSpec>)
