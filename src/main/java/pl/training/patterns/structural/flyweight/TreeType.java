package pl.training.patterns.structural.flyweight;

public class TreeType {

    private final String name;
    private final String color;

    TreeType(final String name, final String color) {
        this.name = name;
        this.color = color;
    }

    public void draw(int x, int y) {
        System.out.println(name + " (" + color + ") at " + x + "," + y);
    }

}
