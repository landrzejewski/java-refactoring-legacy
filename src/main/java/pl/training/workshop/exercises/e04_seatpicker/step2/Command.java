package pl.training.workshop.exercises.e04_seatpicker.step2;

/** Krok 2: operacja kasjera jako obiekt - wykonanie i jego odwrotność w jednym miejscu. */
public interface Command {
    void execute();

    void undo();
}
