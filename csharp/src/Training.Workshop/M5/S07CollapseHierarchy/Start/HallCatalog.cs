namespace Training.Workshop.M5.S07CollapseHierarchy.Start;

/// <summary>Start: katalog sal kina - jedyny klient ImaxHall.</summary>
public sealed class HallCatalog
{
    private readonly IReadOnlyList<Hall> _halls =
    [
        new Hall("Sala 1", 12, 15, 10),
        new Hall("Sala 2", 10, 12, 9),
        new ImaxHall("Sala IMAX", 14, 22),
    ];

    public string Describe(string name)
    {
        return _halls
            .Where(hall => hall.Name == name)
            .Select(hall => hall.Describe())
            .FirstOrDefault() ?? "brak sali: " + name;
    }

    public bool IsVip(string name, int row)
    {
        return _halls.Any(hall => hall.Name == name && hall.IsVip(row));
    }
}
