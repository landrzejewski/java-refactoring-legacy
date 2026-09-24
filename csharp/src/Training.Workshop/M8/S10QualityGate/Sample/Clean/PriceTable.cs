namespace Training.Workshop.M8.S10QualityGate.Sample.Clean;

/// <summary>Próbka czystego kodu domeny: bramka nie może tu niczego zgłosić (brak fałszywych alarmów).</summary>
public sealed class PriceTable
{
    private const int VipFromRow = 10;

    public int BasePrice(string format)
    {
        return format switch
        {
            "IMAX" => 40,
            "3D" => 32,
            _ => 25,
        };
    }

    public int VipSurcharge(int row)
    {
        return row >= VipFromRow ? 10 : 0;
    }
}
