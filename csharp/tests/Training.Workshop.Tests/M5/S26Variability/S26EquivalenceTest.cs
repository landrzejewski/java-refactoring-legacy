using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M5.S26Variability;

/// <summary>
/// Sesja kasjera przechodzi przez wszystkie trzy osie naraz: ceny typów biletów, przejścia
/// statusów i cofanie poleceń. Ta sama sekwencja daje te same odpowiedzi i ten sam stan kasy.
/// </summary>
public sealed class S26EquivalenceTest
{
    private static readonly Scene<IReadOnlyList<string>, string> Scene =
        Support.Scene.Variants<IReadOnlyList<string>, string>()
            .Variant("start", Session(() => new Training.Workshop.M5.S26Variability.Start.BoxOfficeDesk().Handle))
            .Variant("step1", Session(() => new Training.Workshop.M5.S26Variability.Step1.BoxOfficeDesk().Handle))
            .Variant("step2", Session(() => new Training.Workshop.M5.S26Variability.Step2.BoxOfficeDesk().Handle))
            .Variant("step3", Session(() => new Training.Workshop.M5.S26Variability.Step3.BoxOfficeDesk().Handle))
            .Expect("ceny wg typu biletu", [
                "BOOK B1 IMAX N S E C", "BOOK B2 3D S", "BOOK B3 2D C", "REPORT"], """
                B1 NEW 122.00
                B2 NEW 24.00
                B3 NEW 15.00
                Kasa: 0.00; B1=NEW, B2=NEW, B3=NEW; dziennik: BOOK B1, BOOK B2, BOOK B3
                """.TrimEnd().ReplaceLineEndings("\n"))
            .Expect("cykl życia: CANCEL w NEW zwalnia, w PAID oddaje pieniądze", [
                "BOOK B1 2D N N", "PAY B1", "USE B1",
                "BOOK B2 IMAX N", "CANCEL B2",
                "BOOK B3 3D E", "PAY B3", "CANCEL B3", "REPORT"], """
                B1 NEW 50.00
                B1 PAID 50.00
                B1 USED
                B2 NEW 40.00
                B2 CANCELLED
                B3 NEW 22.40
                B3 PAID 22.40
                B3 CANCELLED zwrot 22.40
                Kasa: 50.00; B1=USED, B2=CANCELLED, B3=CANCELLED; dziennik: BOOK B1, PAY B1, USE B1, BOOK B2, CANCEL B2, BOOK B3, PAY B3, CANCEL B3
                """.TrimEnd().ReplaceLineEndings("\n"))
            .Expect("niedozwolone przejścia nie zmieniają stanu ani dziennika", [
                "BOOK B1 2D N", "USE B1", "PAY B1", "PAY B1", "USE B1", "CANCEL B1", "PAY B1",
                "BOOK B2 2D S", "CANCEL B2", "CANCEL B2", "USE B2", "REPORT"], """
                B1 NEW 25.00
                Blad: cannot use in NEW
                B1 PAID 25.00
                Blad: cannot pay in PAID
                B1 USED
                Blad: cannot cancel in USED
                Blad: cannot pay in USED
                B2 NEW 18.75
                B2 CANCELLED
                Blad: cannot cancel in CANCELLED
                Blad: cannot use in CANCELLED
                Kasa: 25.00; B1=USED, B2=CANCELLED; dziennik: BOOK B1, PAY B1, USE B1, BOOK B2, CANCEL B2
                """.TrimEnd().ReplaceLineEndings("\n"))
            .Expect("cofanie w odwrotnej kolejności przywraca kasę i status", [
                "BOOK B1 IMAX N", "PAY B1", "CANCEL B1", "REPORT",
                "UNDO", "REPORT", "UNDO", "UNDO", "REPORT", "UNDO"], """
                B1 NEW 40.00
                B1 PAID 40.00
                B1 CANCELLED zwrot 40.00
                Kasa: 0.00; B1=CANCELLED; dziennik: BOOK B1, PAY B1, CANCEL B1
                Cofnieto: CANCEL B1
                Kasa: 40.00; B1=PAID; dziennik: BOOK B1, PAY B1
                Cofnieto: PAY B1
                Cofnieto: BOOK B1
                Kasa: 0.00; -; dziennik: -
                Blad: nothing to undo
                """.TrimEnd().ReplaceLineEndings("\n"))
            .Expect("po cofnięciu decyduje przywrócony status", [
                "BOOK B1 2D N", "PAY B1", "USE B1", "UNDO", "CANCEL B1", "REPORT"], """
                B1 NEW 25.00
                B1 PAID 25.00
                B1 USED
                Cofnieto: USE B1
                B1 CANCELLED zwrot 25.00
                Kasa: 0.00; B1=CANCELLED; dziennik: BOOK B1, PAY B1, CANCEL B1
                """.TrimEnd().ReplaceLineEndings("\n"))
            .Expect("błędy nie trafiają do dziennika", [
                "book B1 2D N", "BOOK B1 3D N", "BOOK B1 4DX N", "BOOK B2 2D N X", "BOOK B2",
                "PAY", "pay B9", "PRINT", "UNDO", "REPORT"], """
                B1 NEW 25.00
                Blad: booking exists: B1
                Blad: unknown format: 4DX
                Blad: unknown ticket type: X
                Blad: BOOK <id> <format> <typ>...
                Blad: PAY <id>
                Blad: no booking B9
                Nieznana operacja: PRINT
                Cofnieto: BOOK B1
                Kasa: 0.00; -; dziennik: -
                """.TrimEnd().ReplaceLineEndings("\n"));

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepHandlesTheSessionTheSame(string test) => Scene.Run(test);

    private static Func<IReadOnlyList<string>, string> Session(Func<Func<string, string>> desk)
    {
        return lines =>
        {
            var handle = desk();
            return string.Join("\n", lines.Select(handle).ToList());
        };
    }
}
