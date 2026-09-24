using System.Globalization;

namespace Training.Workshop.Tests.M3.S08Ocp;

/// <summary>Dla istniejących formatów start i każdy krok dają tę samą cenę i etykietę.</summary>
public sealed class S08EquivalenceTest
{
    public sealed record Case(string Format, bool OwnGlasses);

    private static readonly Support.Scene<Case, string> Scene = Support.Scene.Variants<Case, string>()
        .Variant("start", c =>
        {
            var offer = new Training.Workshop.M3.S08Ocp.Start.ScreeningOffer();
            return offer.Label(c.Format) + ": " + Show(offer.Price(c.Format, c.OwnGlasses));
        })
        .Variant("step1", c =>
        {
            var offer = new Training.Workshop.M3.S08Ocp.Step1.ScreeningOffer();
            return offer.Label(c.Format) + ": " + Show(offer.Price(c.Format, c.OwnGlasses));
        })
        .Variant("step2", c =>
        {
            var offer = new Training.Workshop.M3.S08Ocp.Step2.ScreeningOffer();
            return offer.Label(c.Format) + ": " + Show(offer.Price(c.Format, c.OwnGlasses));
        })
        .Variant("step3", c =>
        {
            var offer = new Training.Workshop.M3.S08Ocp.Step3.ScreeningOffer();
            return offer.Label(c.Format) + ": " + Show(offer.Price(c.Format, c.OwnGlasses));
        })
        .Expect("2D", new Case("2D", false), "2D: 25.00")
        .Expect("3D z wypozyczeniem okularow", new Case("3D", false), "3D - okulary: 35.00")
        .Expect("3D z wlasnymi okularami", new Case("3D", true), "3D - okulary: 32.00")
        .Expect("IMAX", new Case("IMAX", false), "IMAX - ekran laserowy: 40.00");

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void ExistingFormatsBehaveTheSame(string test) => Scene.Run(test);

    private static string Show(decimal amount) => amount.ToString("0.00", CultureInfo.InvariantCulture);
}
