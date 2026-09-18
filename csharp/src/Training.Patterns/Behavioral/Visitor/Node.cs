namespace Training.Patterns.Behavioral.Visitor;

public abstract class Node
{
    // Java: protected (= also package-visible, read by the visitors) -> protected internal
    protected internal readonly string name;
    protected List<Node> children = [];

    public virtual void AddChild(Node node)
    {
        children.Add(node);
    }

    public virtual void Accept(IVisitor visitor)
    {
        children.ForEach(node => node.Accept(visitor));
    }

    protected Node(string name)
    {
        this.name = name;
    }

    public void SetChildren(List<Node> children)
    {
        this.children = children;
    }
}
