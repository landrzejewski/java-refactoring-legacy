using Training.Workshop.Shared;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M6.S20DecisionMap;

/// <summary>Pełna tabela 3 formaty x typy dni: obie ścieżki (krok 2 i krok 3) dają te same ceny.</summary>
public sealed class S20EquivalenceTest
{
    private static readonly Scene<string, string> Scene = Support.Scene.Variants<string, string>()
        .Variant("start", Safe(new Training.Workshop.M6.S20DecisionMap.Start.ShowPricing().Price))
        .Variant("step1", Safe(new Training.Workshop.M6.S20DecisionMap.Step1.ShowPricing().Price))
        .Variant("step2 (Strategy)", Safe(new Training.Workshop.M6.S20DecisionMap.Step2.ShowPricing().Price))
        .Variant("step3 (typ formatu)", Safe(new Training.Workshop.M6.S20DecisionMap.Step3.ShowPricing().Price))
        .Expect("2D poniedziałek", "MONDAY 2D", "25.00")
        .Expect("2D wtorek", "TUESDAY 2D", "17.50")
        .Expect("2D sobota", "SATURDAY 2D", "27.00")
        .Expect("3D poniedziałek", "MONDAY 3D", "32.00")
        .Expect("3D wtorek", "TUESDAY 3D", "22.40")
        .Expect("3D sobota", "SATURDAY 3D", "34.00")
        .Expect("IMAX poniedziałek", "MONDAY IMAX", "40.00")
        .Expect("IMAX wtorek", "TUESDAY IMAX", "28.00")
        .Expect("IMAX niedziela", "SUNDAY IMAX", "42.00")
        .Expect("nieznany format", "TUESDAY 4DX", "ERROR unknown format: 4DX");

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void BothPathsPriceTheSame(string test) => Scene.Run(test);

    private static Func<string, string> Safe(Func<DayOfWeek, string, Money> price)
    {
        return input =>
        {
            var parts = input.Split(' ');
            try
            {
                return price(Enum.Parse<DayOfWeek>(parts[0], ignoreCase: true), parts[1]).ToString();
            }
            catch (ArgumentException exception)
            {
                return "ERROR " + exception.Message;
            }
        };
    }
}
