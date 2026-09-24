using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M6.S15Command;

/// <summary>Sesja kasjera: ta sama sekwencja poleceń daje te same odpowiedzi i ten sam stan kasy.</summary>
public sealed class S15EquivalenceTest
{
    private static readonly Scene<IReadOnlyList<string>, string> Scene =
        Support.Scene.Variants<IReadOnlyList<string>, string>()
            .Variant("start", Session(() => new Training.Workshop.M6.S15Command.Start.CashierConsole().Handle))
            .Variant("step1", Session(() => new Training.Workshop.M6.S15Command.Step1.CashierConsole().Handle))
            .Variant("step2", Session(() => new Training.Workshop.M6.S15Command.Step2.CashierConsole().Handle))
            .Variant("step3", Session(() => new Training.Workshop.M6.S15Command.Step3.CashierConsole().Handle))
            .Expect("sprzedaż, zwrot, raport", [
                "SELL 2 Diuna", "sell 1 Kraina Lodu", "REFUND Diuna", "REPORT"], """
                Sprzedano 2 x Diuna = 80.00
                Sprzedano 1 x Kraina Lodu = 32.00
                Zwrot 1 x Diuna = 40.00
                Kasa: 72.00, biletow: 2
                """.TrimEnd().ReplaceLineEndings("\n"))
            .Expect("błędy nie zmieniają stanu", [
                "SELL Diuna", "SELL 2 Batman", "REFUND Amator", "PRINT", "  report  "], """
                Blad: SELL <liczba> <tytul>
                Blad: nieznany film Batman
                Blad: brak biletow do zwrotu
                Nieznana komenda: PRINT
                Kasa: 0.00, biletow: 0
                """.TrimEnd().ReplaceLineEndings("\n"));

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepHandlesTheSessionTheSame(string test) => Scene.Run(test);

    private static Func<IReadOnlyList<string>, string> Session(Func<Func<string, string>> console)
    {
        return lines =>
        {
            var handle = console();
            return string.Join("\n", lines.Select(handle).ToList());
        };
    }
}
