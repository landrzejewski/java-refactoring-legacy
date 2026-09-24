using Training.Workshop.M6.S01Strategy;
using Training.Workshop.Shared;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M6.S01Strategy;

/// <summary>Tabela decyzji programu zniżek: każda gałąź, typ nieznany, program nieznany i null.</summary>
public sealed class S01EquivalenceTest
{
    private static readonly Scene<PriceRequest, string> Scene = Support.Scene.Variants<PriceRequest, string>()
        .Variant("start", r => Run(() => new Training.Workshop.M6.S01Strategy.Start.PriceBoard().PriceFor(r)))
        .Variant("step1", r => Run(() => new Training.Workshop.M6.S01Strategy.Step1.PriceBoard().PriceFor(r)))
        .Variant("step2", r => Run(() => new Training.Workshop.M6.S01Strategy.Step2.PriceBoard().PriceFor(r)))
        .Variant("step3", r => Run(() => new Training.Workshop.M6.S01Strategy.Step3.PriceBoard().PriceFor(r)))
        .Expect("STANDARD normalny 2D", Request("25.00", "N", "STANDARD"), "25.00")
        .Expect("STANDARD student 3D", Request("32.00", "S", "STANDARD"), "24.00")
        .Expect("STANDARD senior IMAX", Request("40.00", "E", "STANDARD"), "28.00")
        .Expect("STANDARD dziecko 2D", Request("25.00", "C", "STANDARD"), "15.00")
        .Expect("STUDENT_WEEK student IMAX", Request("40.00", "S", "STUDENT_WEEK"), "20.00")
        .Expect("STUDENT_WEEK dziecko 3D", Request("32.00", "C", "STUDENT_WEEK"), "19.20")
        .Expect("PREMIERE dziecko IMAX", Request("40.00", "C", "PREMIERE"), "40.00")
        .Expect("PREMIERE nie sprawdza typu", Request("40.00", "X", "PREMIERE"), "40.00")
        .Expect("STANDARD nieznany typ", Request("25.00", "X", "STANDARD"),
            "ERROR: unknown ticket type: X")
        .Expect("nieznany program", Request("25.00", "N", "BLACK_FRIDAY"),
            "ERROR: unknown program: BLACK_FRIDAY")
        .Expect("program null", Request("25.00", "N", null), "ERROR: program must not be null");

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepPricesTheSame(string test) => Scene.Run(test);

    private static PriceRequest Request(string basePrice, string type, string? program)
    {
        return new PriceRequest(Money.Of(basePrice), type, program);
    }

    internal static string Run(Func<Money> price)
    {
        try
        {
            return price().ToString();
        }
        catch (ArgumentException exception)
        {
            return "ERROR: " + exception.Message;
        }
    }
}
