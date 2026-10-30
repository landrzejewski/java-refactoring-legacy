package pl.training.workshop.exercises.e02_hallmap.start

import pl.training.workshop.shared.Money

/**
 * Start: miejsce niesie własną kopię wszystkiego, co opisuje jego kategorię. Dwa fotele VIP
 * w tym samym rzędzie mają dwie osobne kopie nazwy, dopłaty, ikony i opisu.
 */
class Seat internal constructor(
    internal val label: String,
    internal val code: Char,
    internal val categoryName: String,
    internal val surcharge: Money,
    internal val icon: String,
    internal val description: String,
)
