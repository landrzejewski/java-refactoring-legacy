package pl.training.workshop.exercises.e02_hallmap

/**
 * Stabilny kontrakt sceny: rząd o etykiecie (np. "A") i układzie miejsc, np. `"NN_VV"`.
 * Numer miejsca to pozycja w układzie liczona od 1, więc luka `_` zużywa numer.
 */
data class RowSpec(val label: String, val layout: String)
