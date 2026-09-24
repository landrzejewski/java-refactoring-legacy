namespace Training.Workshop.M6.S13ExtractComposite.Step2;

/// <summary>Krok 2: maraton = suma dzieci + 15 minut przerwy między pozycjami.</summary>
public sealed class Marathon : CompositeProgramItem
{
    public Marathon(string name)
        : base(name)
    {
    }

    public override int Minutes
    {
        get
        {
            var count = Children.Count;
            return count == 0 ? 0 : ChildrenMinutes() + 15 * (count - 1);
        }
    }

    protected override string Label()
    {
        return "Maraton";
    }
}
