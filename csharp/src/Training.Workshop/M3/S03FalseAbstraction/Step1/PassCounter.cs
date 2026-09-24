namespace Training.Workshop.M3.S03FalseAbstraction.Step1;

/// <summary>Krok 1: Inline Method - karnet dostał własną kopię, wciąż z cudzymi flagami.</summary>
public sealed class PassCounter
{
    public decimal Pass(int entries)
    {
        var format = "2D";
        var pass = true;
        var morning = false;
        var ownGlasses = true;
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
        return Math.Round(unit * entries, 2, MidpointRounding.AwayFromZero);
    }
}
