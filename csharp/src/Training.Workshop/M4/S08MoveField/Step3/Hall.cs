namespace Training.Workshop.M4.S08MoveField.Step3;

/// <summary>Sala kinowa - właściciel progu VIP i pytania "czy ten rząd jest VIP".</summary>
public sealed class Hall
{
    private readonly int _vipFromRow;

    public Hall(string name, int vipFromRow)
    {
        Name = name;
        _vipFromRow = vipFromRow;
    }

    public string Name { get; }

    public bool IsVip(int row)
    {
        return row >= _vipFromRow;
    }
}
