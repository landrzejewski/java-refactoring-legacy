namespace Training.Workshop.Tests.M3.S08Ocp;

/// <summary>
/// Nowy format 4DX to zmiana zachowania, więc bez start (edytowanego na żywo):
/// w kroku 2 nieznany, w kroku 3 obsłużony bez zmiany ScreeningOffer.
/// </summary>
public sealed class S08NewFormatTest
{
    [Fact]
    public void Step2DoesNotKnow4dxYet()
    {
        Assert.Throws<ArgumentException>(
            () => new Training.Workshop.M3.S08Ocp.Step2.ScreeningOffer().Price("4DX", false));
    }

    [Fact]
    public void Step3Prices4dxWithGlasses()
    {
        var offer = new Training.Workshop.M3.S08Ocp.Step3.ScreeningOffer();
        Assert.Equal(48.00m, offer.Price("4DX", false));
        Assert.Equal(45.00m, offer.Price("4DX", true));
        Assert.Equal("4DX - ruchome fotele", offer.Label("4DX"));
    }
}
