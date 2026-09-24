namespace Training.Workshop.M3.S08Ocp.Step1;

// CS8524 dotyczy wartości spoza enuma (rzutowanie liczby) - wyłączone, żeby switch nie potrzebował
// gałęzi "_". Dzięki temu brak obsługi nowej stałej enuma to CS8509, czyli błąd kompilacji.
#pragma warning disable CS8524

/// <summary>
/// Krok 1: switch na enumie bez default. To jeszcze nie OCP, ale już bezpieczniejszy
/// zamknięty zbiór: nowa stała enuma = błąd kompilacji w każdym switchu, który jej nie obsłuży.
/// Dla małego, stabilnego zbioru to bywa wystarczający model.
/// </summary>
public sealed class ScreeningOffer
{
    public decimal Price(string code, bool ownGlasses)
    {
        var format = Format.Parse(code);
        var @base = format switch
        {
            Format.TwoD => 25.00m,
            Format.ThreeD => 32.00m,
            Format.Imax => 40.00m,
        };
        var glasses = format switch
        {
            Format.ThreeD => ownGlasses ? 0m : 3.00m,
            Format.TwoD or Format.Imax => 0m,
        };
        return @base + glasses;
    }

    public string Label(string code)
    {
        return Format.Parse(code) switch
        {
            Format.TwoD => "2D",
            Format.ThreeD => "3D - okulary",
            Format.Imax => "IMAX - ekran laserowy",
        };
    }
}
