namespace Training.Workshop.M3.S08Ocp.Step1;

/// <summary>Krok 1: Replace Type Code with Enum - zamknięty, znany kompilatorowi zbiór formatów.</summary>
public enum Format { TwoD, ThreeD, Imax }

/// <summary>Kody formatów z cennika ("2D", "3D", "IMAX") - C# 14: statyczna metoda "na" enumie.</summary>
public static class FormatCodes
{
    extension(Format)
    {
        public static Format Parse(string code)
        {
            return code switch
            {
                "2D" => Format.TwoD,
                "3D" => Format.ThreeD,
                "IMAX" => Format.Imax,
                _ => throw new ArgumentException("nieznany format: " + code),
            };
        }
    }
}
