namespace Training.Patterns.Structural.Flyweight;

public class TreeType
{
    private readonly string name;
    private readonly string color;

    internal TreeType(string name, string color)
    {
        this.name = name;
        this.color = color;
    }

    public void Draw(int x, int y)
    {
        Console.WriteLine(name + " (" + color + ") at " + x + "," + y);
    }
}
