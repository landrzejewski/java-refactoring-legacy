namespace Training.Workshop.M3.S03FalseAbstraction.Start;

/// <summary>
/// Start: fałszywa abstrakcja. Jedna "uniwersalna" metoda wycenia bilety i karnety
/// (karnet: 20.00 za wejście na seans 2D), sterowana flagami bool. Każdy wywołujący
/// podaje flagi, które go nie dotyczą, a zmiana reguły biletów grozi zmianą karnetów.
/// </summary>
public sealed class Pricing
{
    public decimal Price(string format, int quantity, bool pass,
                         bool morning, bool ownGlasses)
    {
        decimal unit;
        if (pass)
        {
            unit = 20.00m;
        }
        else
        {
            unit = format switch
            {
                "IMAX" => 40.00m,
                "3D" => 32.00m,
                _ => 25.00m,
            };
            if (morning)
            {
                unit -= 5.00m;
            }
        }
        if (format == "3D" && !ownGlasses && !pass)
        {
            unit += 3.00m;
        }
        return Math.Round(unit * quantity, 2, MidpointRounding.AwayFromZero);
    }
}
