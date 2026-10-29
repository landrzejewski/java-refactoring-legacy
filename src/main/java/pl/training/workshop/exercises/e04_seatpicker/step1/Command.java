package pl.training.workshop.exercises.e04_seatpicker.step1;

/** Krok 1: operacja kasjera jako obiekt - wykonanie i jego odwrotność w jednym miejscu. */
public interface Command {
    void execute();

    void undo();
}
