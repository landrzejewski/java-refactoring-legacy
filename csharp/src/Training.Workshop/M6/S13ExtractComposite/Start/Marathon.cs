namespace Training.Workshop.M6.S13ExtractComposite.Start;

/// <summary>
/// Start: kontener filmów. Obsługa dzieci (lista, Add, Children, suma, opis) jest skopiowana
/// w ShortsBlock. Różni się tylko reguła czasu: 15 minut przerwy między pozycjami.
/// </summary>
public sealed class Marathon : IProgramItem
{
    private readonly string _name;
    private readonly List<IProgramItem> _children = [];

    public Marathon(string name)
    {
        _name = name;
    }

    public void Add(IProgramItem child)
    {
        ArgumentNullException.ThrowIfNull(child);
        _children.Add(child);
    }

    public IReadOnlyList<IProgramItem> Children => _children.ToList().AsReadOnly();

    public int Minutes
    {
        get
        {
            var total = 0;
            foreach (var child in _children)
            {
                total += child.Minutes;
            }
            return _children.Count == 0 ? 0 : total + 15 * (_children.Count - 1);
        }
    }

    public string Describe()
    {
        return "Maraton " + _name + " (" + Minutes + " min) "
            + "[" + string.Join(", ", _children.Select(child => child.Describe())) + "]";
    }
}
