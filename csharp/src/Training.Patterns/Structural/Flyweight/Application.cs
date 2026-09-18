namespace Training.Patterns.Structural.Flyweight;

public class Application
{
    public static void Run()
    {
        IReadOnlyList<Tree> forest =
        [
            new Tree(1, 2, TreeFactory.GetTreeType("Oak", "green")),
            new Tree(5, 3, TreeFactory.GetTreeType("Pine", "dark green")),
            new Tree(9, 8, TreeFactory.GetTreeType("Oak", "green")),
        ];
        foreach (var tree in forest)
        {
            tree.Draw();
        }
        Console.WriteLine(JavaText.Of(ReferenceEquals(TreeFactory.GetTreeType("Oak", "green"), TreeFactory.GetTreeType("Oak", "green"))));
    }
}
