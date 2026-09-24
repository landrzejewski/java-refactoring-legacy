namespace Training.Workshop.M6.S13ExtractComposite.Step1;

/// <summary>Krok 1: pole _children, Add i Children podciągnięte do CompositeProgramItem.</summary>
public sealed class Marathon : CompositeProgramItem
{
    private readonly string _name;

    public Marathon(string name)
    {
        _name = name;
    }

    public override int Minutes
    {
        get
        {
            var children = Children;
            var total = 0;
            foreach (var child in children)
            {
                total += child.Minutes;
            }
            return children.Count == 0 ? 0 : total + 15 * (children.Count - 1);
        }
    }

    public override string Describe()
    {
        return "Maraton " + _name + " (" + Minutes + " min) "
            + "[" + string.Join(", ", Children.Select(child => child.Describe())) + "]";
    }
}
