namespace Training.Workshop.M6.S13ExtractComposite.Step1;

/// <summary>Krok 1: pole _children, Add i Children podciągnięte do CompositeProgramItem.</summary>
public sealed class ShortsBlock : CompositeProgramItem
{
    private readonly string _name;

    public ShortsBlock(string name)
    {
        _name = name;
    }

    public override int Minutes
    {
        get
        {
            var total = 0;
            foreach (var child in Children)
            {
                total += child.Minutes;
            }
            return total;
        }
    }

    public override string Describe()
    {
        return "Blok " + _name + " (" + Minutes + " min) "
            + "[" + string.Join(", ", Children.Select(child => child.Describe())) + "]";
    }
}
