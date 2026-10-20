using Training.Workshop.M5.S29BoundaryObject;
using Training.Workshop.Shared;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M5.S29BoundaryObject;

/// <summary>
/// Sesja płatności: autoryzacja pełna, częściowa (granica 300.00), odmowa, błąd kwoty,
/// nadpisanie płatności, potwierdzenia i suma autoryzacji. Wynik identyczny niezależnie od tego,
/// gdzie i ile razy rozbieramy odpowiedź terminala.
/// </summary>
public sealed class S29EquivalenceTest
{
    private static readonly Scene<IReadOnlyList<string>, string> Scene =
        Support.Scene.Variants<IReadOnlyList<string>, string>()
            .Variant("start", Session(() =>
            {
                var service = Training.Workshop.M5.S29BoundaryObject.Start.PaymentModule
                    .PaymentService(new LegacyTerminalClient());
                return Console(service.Pay, service.Receipt, service.TotalAuthorized);
            }))
            .Variant("step1", Session(() =>
            {
                var service = Training.Workshop.M5.S29BoundaryObject.Step1.PaymentModule
                    .PaymentService(new LegacyTerminalClient());
                return Console(service.Pay, service.Receipt, service.TotalAuthorized);
            }))
            .Variant("step2", Session(() =>
            {
                var service = Training.Workshop.M5.S29BoundaryObject.Step2.PaymentModule
                    .PaymentService(new LegacyTerminalClient());
                return Console(service.Pay, service.Receipt, service.TotalAuthorized);
            }))
            .Variant("step3", Session(() =>
            {
                var service = Training.Workshop.M5.S29BoundaryObject.Step3.PaymentModule
                    .PaymentService(new LegacyTerminalClient());
                return Console(service.Pay, service.Receipt, service.TotalAuthorized);
            }))
            .Expect("pełna autoryzacja", ["pay R1 45.00", "receipt R1", "total"], """
                PAID AUTH-R1
                Platnosc R1: 45.00 (AUTH-R1)
                45.00
                """.TrimEnd().ReplaceLineEndings("\n"))
            .Expect("autoryzacja częściowa", ["pay R2 450.00", "receipt R2", "total"], """
                PARTIAL AUTH-R2 brakuje 150.00
                Platnosc R2: 300.00 (AUTH-R2)
                300.00
                """.TrimEnd().ReplaceLineEndings("\n"))
            .Expect("granica 300.00 i grosz ponad", ["pay R3 300.00", "pay R4 300.01", "total"], """
                PAID AUTH-R3
                PARTIAL AUTH-R4 brakuje 0.01
                600.00
                """.TrimEnd().ReplaceLineEndings("\n"))
            .Expect("odmowa i błąd kwoty", [
                "pay R5 1500.00", "receipt R5", "pay R6 0.00", "receipt R6", "total"], """
                DECLINED 51
                Platnosc R5 nieudana: 51
                ERROR AMOUNT
                Platnosc R6 nieudana: AMOUNT
                0.00
                """.TrimEnd().ReplaceLineEndings("\n"))
            .Expect("brak płatności", ["receipt R9", "total"], """
                Brak platnosci R9
                0.00
                """.TrimEnd().ReplaceLineEndings("\n"))
            .Expect("ponowna płatność nadpisuje poprzednią", [
                "pay R1 45.00", "pay R2 25.50", "pay R1 1500.00", "receipt R1", "total"], """
                PAID AUTH-R1
                PAID AUTH-R2
                DECLINED 51
                Platnosc R1 nieudana: 51
                25.50
                """.TrimEnd().ReplaceLineEndings("\n"));

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepReadsTheTerminalTheSame(string test) => Scene.Run(test);

    private static Func<string, string> Console(Func<string, Money, string> pay,
        Func<string, string> receipt, Func<Money> total)
    {
        return line =>
        {
            var parts = line.Split(' ');
            return parts[0] switch
            {
                "pay" => pay(parts[1], Money.Of(parts[2])),
                "receipt" => receipt(parts[1]),
                "total" => total().ToString(),
                _ => throw new ArgumentException(line),
            };
        };
    }

    private static Func<IReadOnlyList<string>, string> Session(Func<Func<string, string>> console)
    {
        return lines =>
        {
            var handle = console();
            return string.Join("\n", lines.Select(handle).ToList());
        };
    }
}
