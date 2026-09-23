package pl.training.workshop.m5.s07_collapsehierarchy.step1;

/** Krok 1: usunięte override'y, które tylko wołały super (IDE: "Method is identical to its super method"). */
public class ImaxHall extends Hall {
    public ImaxHall(String name, int rows, int seatsPerRow) {
        super(name, rows, seatsPerRow, rows - 1);
    }
}
