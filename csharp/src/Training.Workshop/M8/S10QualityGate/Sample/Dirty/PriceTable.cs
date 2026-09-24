using System.Collections;

namespace Training.Workshop.M8.S10QualityGate.Sample.Dirty;

/// <summary>
/// Próbka "brudnego" kodu domeny dla bramki: znacznik do zrobienia, wydruk na konsolę,
/// niegeneryczna kolekcja w polu, którego nikt nie przypisuje, i metody bez testu.
/// </summary>
public sealed class PriceTable
{
    private readonly ArrayList _lookups;

    public int BasePrice(string format)
    {
        // TODO dodać 4DX
        return format switch
        {
            "IMAX" => 40,
            "3D" => 32,
            _ => 25,
        };
    }

    public int VipSurcharge(int row)
    {
        Console.WriteLine("VIP? rzad " + row);
        return row >= 10 ? 10 : 0;
    }

    public int LookupCount()
    {
        return _lookups.Count;
    }
}
