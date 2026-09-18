namespace Training.Patterns.Structural.Composite;

public abstract class Node
{
    protected readonly string name;
    protected List<Node> children = [];

    public virtual void AddChild(Node node)
    {
        children.Add(node);
    }

    public abstract void PrintInfo();

    protected Node(string name)
    {
        this.name = name;
    }

    public void SetChildren(List<Node> children)
    {
        this.children = children;
    }
}
