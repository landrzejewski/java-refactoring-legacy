using System.Globalization;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M7.S04RemoveDuplication;

/// <summary>
/// Test równoważności dla przypadków, w których kasa i sklep od zawsze się zgadzają.
/// Przypadek brzegowy zaokrąglenia jest w S04RoundingDecisionTest.
/// </summary>
public sealed class S04EquivalenceTest
{
    internal static readonly IReadOnlyList<decimal> Two3D = Prices("32.00", 2);
    internal static readonly IReadOnlyList<decimal> Ten2D = Prices("25.00", 10);

    private static readonly Scene<IReadOnlyList<decimal>, string> BoxOffice = Support.Scene
        .Variants<IReadOnlyList<decimal>, string>()
        .Variant("start", p => Plain(new Training.Workshop.M7.S04RemoveDuplication.Start.BoxOffice().Total(p)))
        .Variant("step1", p => Plain(new Training.Workshop.M7.S04RemoveDuplication.Step1.BoxOffice().Total(p)))
        .Variant("step2", p => Plain(new Training.Workshop.M7.S04RemoveDuplication.Step2.BoxOffice().Total(p)))
        .Variant("step3", p => Plain(new Training.Workshop.M7.S04RemoveDuplication.Step3.BoxOffice().Total(p)))
        .Expect("dwa bilety 3D", Two3D, "64.00")
        .Expect("9 biletow - jeszcze bez rabatu", Prices("25.00", 9), "225.00")
        .Expect("10 biletow 2D", Ten2D, "225.00")
        .Expect("rabat z koncowka 5: 231.25 -> 208.12", S04RoundingDecisionTest.Edge, "208.12")
        .Expect("pusty koszyk", [], "0.00");

    private static readonly Scene<IReadOnlyList<decimal>, string> WebShop = Support.Scene
        .Variants<IReadOnlyList<decimal>, string>()
        .Variant("start", p => Plain(new Training.Workshop.M7.S04RemoveDuplication.Start.WebShop().Total(p)))
        .Variant("step1", p => Plain(new Training.Workshop.M7.S04RemoveDuplication.Step1.WebShop().Total(p)))
        .Variant("step2", p => Plain(new Training.Workshop.M7.S04RemoveDuplication.Step2.WebShop().Total(p)))
        .Variant("step3", p => Plain(new Training.Workshop.M7.S04RemoveDuplication.Step3.WebShop().Total(p)))
        .Expect("dwa bilety 3D + oplaty", Two3D, "68.00")
        .Expect("10 biletow 2D + oplaty", Ten2D, "245.00")
        .Expect("pusty koszyk", [], "0.00");

    public static TheoryData<string> BoxOfficeCases => BoxOffice.Tests();

    public static TheoryData<string> WebShopCases => WebShop.Tests();

    [Theory]
    [MemberData(nameof(BoxOfficeCases))]
    public void BoxOfficeNeverChanges(string test) => BoxOffice.Run(test);

    [Theory]
    [MemberData(nameof(WebShopCases))]
    public void WebShopAgreesOnOrdinaryBaskets(string test) => WebShop.Run(test);

    internal static List<decimal> Prices(string price, int count)
    {
        return Enumerable.Repeat(decimal.Parse(price, CultureInfo.InvariantCulture), count).ToList();
    }

    /// <summary>
    /// Odpowiednik toPlainString(): decimal nie niesie skali "0.00" dla pustego koszyka,
    /// więc kwotę (już zaokrągloną do 2 miejsc) formatujemy jawnie.
    /// </summary>
    internal static string Plain(decimal amount) => amount.ToString("0.00", CultureInfo.InvariantCulture);
}
