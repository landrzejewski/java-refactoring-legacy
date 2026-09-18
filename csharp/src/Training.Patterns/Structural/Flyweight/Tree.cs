namespace Training.Patterns.Structural.Flyweight;

public class Tree
{
    private readonly int x;
    private readonly int y;
    private readonly TreeType type;

    public Tree(int x, int y, TreeType type)
    {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public void Draw()
    {
        type.Draw(x, y);
    }
}
