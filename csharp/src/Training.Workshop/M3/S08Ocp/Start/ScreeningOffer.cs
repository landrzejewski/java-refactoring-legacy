namespace Training.Workshop.M3.S08Ocp.Start;

/// <summary>
/// Start: wiedza o formatach seansu rozsiana po kilku switchach na stringu.
/// Każdy nowy format (kino kupuje salę 4DX) wymaga edycji wszystkich switchy,
/// a kompilator nie podpowie, o którym zapomnieliśmy - wpadnie do default.
/// </summary>
public sealed class ScreeningOffer
{
    public decimal Price(string format, bool ownGlasses)
    {
        var @base = format switch
        {
            "2D" => 25.00m,
            "3D" => 32.00m,
            "IMAX" => 40.00m,
            _ => throw new ArgumentException("nieznany format: " + format),
        };
        var glasses = format switch
        {
            "3D" => ownGlasses ? 0m : 3.00m,
            _ => 0m,
        };
        return @base + glasses;
    }

    public string Label(string format)
    {
        return format switch
        {
            "2D" => "2D",
            "3D" => "3D - okulary",
            "IMAX" => "IMAX - ekran laserowy",
            _ => throw new ArgumentException("nieznany format: " + format),
        };
    }
}
