package pl.training.workshop.exercises.e04_seatpicker.step1

/** Krok 1: operacja kasjera jako obiekt - wykonanie i jego odwrotność w jednym miejscu. */
interface Command {
    fun execute()

    fun undo()
}
