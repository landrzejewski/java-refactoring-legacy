namespace Training.Workshop.M5.S07CollapseHierarchy.Step2;

/// <summary>Krok 2: Replace Constructor with Factory Method - Hall.Imax(...) przejmuje jedyną wiedzę z ImaxHall.</summary>
public class Hall
{
    private readonly string _name;
    private readonly int _rows;
    private readonly int _seatsPerRow;
    private readonly int _vipFromRow;

    public Hall(string name, int rows, int seatsPerRow, int vipFromRow)
    {
        _name = name;
        _rows = rows;
        _seatsPerRow = seatsPerRow;
        _vipFromRow = vipFromRow;
    }

    /// <summary>Sala IMAX: VIP zawsze w dwóch ostatnich rzędach (wiedza przeniesiona z konstruktora ImaxHall).</summary>
    public static Hall Imax(string name, int rows, int seatsPerRow)
    {
        return new Hall(name, rows, seatsPerRow, rows - 1);
    }

    public string Name => _name;

    public virtual int Capacity => _rows * _seatsPerRow;

    public bool IsVip(int row)
    {
        return row >= _vipFromRow;
    }

    public virtual string Describe()
    {
        return _name + ": " + Capacity + " miejsc, VIP od rzędu " + _vipFromRow;
    }
}
