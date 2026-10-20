using Training.Workshop.M5.S28Chain;
using Training.Workshop.Shared;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M5.S28Chain;

/// <summary>
/// Tabela decyzji zwrotu: każdy poziom, granice limitów i 24 h, kolejność reguł (AUTO bez limitu,
/// odrzucenie po seansie przed wszystkim) oraz walidacja kwoty przed łańcuchem.
/// </summary>
public sealed class S28EquivalenceTest
{
    private static readonly DateTime Start = new(2026, 10, 20, 18, 0, 0);

    private static readonly Scene<RefundRequest, string> Scene = Support.Scene.Variants<RefundRequest, string>()
        .Variant("start", Safe(new Training.Workshop.M5.S28Chain.Start.RefundApprover().Decide))
        .Variant("step1", Safe(new Training.Workshop.M5.S28Chain.Step1.RefundApprover().Decide))
        .Variant("step2", Safe(new Training.Workshop.M5.S28Chain.Step2.RefundApprover().Decide))
        .Variant("step3", Safe(new Training.Workshop.M5.S28Chain.Step3.RefundApprover().Decide))
        .Expect("48 h przed: automatycznie", Request("50.00", Start.AddHours(-48)), "APPROVED AUTO")
        .Expect("dokładnie 24 h przed: automatycznie", Request("50.00", Start.AddHours(-24)), "APPROVED AUTO")
        .Expect("23 h 59 min przed: kasjer", Request("50.00", Start.AddHours(-24).AddMinutes(1)),
            "APPROVED CASHIER")
        .Expect("limit kasjera włącznie", Request("100.00", Start.AddHours(-2)), "APPROVED CASHIER")
        .Expect("grosz ponad limit kasjera: kierownik", Request("100.01", Start.AddHours(-2)), "APPROVED MANAGER")
        .Expect("limit kierownika włącznie", Request("1000.00", Start.AddHours(-2)), "APPROVED MANAGER")
        .Expect("ponad limit kierownika: koniec łańcucha", Request("1000.01", Start.AddHours(-2)),
            "REJECTED brak uprawnionego zatwierdzajacego")
        .Expect("AUTO nie ma limitu kwoty (kolejność reguł)", Request("5000.00", Start.AddDays(-3)),
            "APPROVED AUTO")
        .Expect("w chwili rozpoczęcia seansu", Request("10.00", Start), "REJECTED seans juz sie rozpoczal")
        .Expect("po seansie, nawet mała kwota", Request("10.00", Start.AddHours(1)),
            "REJECTED seans juz sie rozpoczal")
        .Expect("kwota zero", Request("0.00", Start.AddHours(-2)), "ERROR amount must be positive")
        .Expect("walidacja przed łańcuchem", Request("-5.00", Start.AddHours(1)),
            "ERROR amount must be positive");

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepDecidesTheSame(string test) => Scene.Run(test);

    private static RefundRequest Request(string amount, DateTime requestedAt)
    {
        return new RefundRequest(Money.Of(amount), Start, requestedAt);
    }

    private static Func<RefundRequest, string> Safe(Func<RefundRequest, RefundDecision> decide)
    {
        return request =>
        {
            try
            {
                return decide(request).ToString();
            }
            catch (ArgumentException exception)
            {
                return "ERROR " + exception.Message;
            }
        };
    }
}
