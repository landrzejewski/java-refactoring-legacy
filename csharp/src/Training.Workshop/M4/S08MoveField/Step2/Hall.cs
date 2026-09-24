namespace Training.Workshop.M4.S08MoveField.Step2;

/// <summary>Sala kinowa - od kroku 2 jedyny właściciel progu VIP.</summary>
public sealed class Hall
{
    public Hall(string name, int vipFromRow)
    {
        Name = name;
        VipFromRow = vipFromRow;
    }

    public string Name { get; }

    public int VipFromRow { get; }
}
