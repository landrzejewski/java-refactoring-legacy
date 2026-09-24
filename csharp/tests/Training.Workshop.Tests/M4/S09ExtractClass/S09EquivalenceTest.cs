using System.Globalization;
using System.Text;
using Training.Workshop.M4.S09ExtractClass;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M4.S09ExtractClass;

/// <summary>Test równoważności: to samo podsumowanie i te same błędy płatności po każdym Extract Class.</summary>
public sealed class S09EquivalenceTest
{
    private static readonly Scene<BookingInput, string> Scene = Support.Scene.Variants<BookingInput, string>()
        .Variant("start", input =>
        {
            var booking = new Training.Workshop.M4.S09ExtractClass.Start.Booking(
                input.Id, input.Name, input.Email, input.Phone, input.Amount);
            return Run(input, booking.Pay, booking.Summary);
        })
        .Variant("step1", input =>
        {
            var booking = new Training.Workshop.M4.S09ExtractClass.Step1.Booking(
                input.Id, input.Name, input.Email, input.Phone, input.Amount);
            return Run(input, booking.Pay, booking.Summary);
        })
        .Variant("step2", input =>
        {
            var booking = new Training.Workshop.M4.S09ExtractClass.Step2.Booking(
                input.Id, input.Name, input.Email, input.Phone, input.Amount);
            return Run(input, booking.Pay, booking.Summary);
        })
        .Variant("step3", input =>
        {
            var booking = new Training.Workshop.M4.S09ExtractClass.Step3.Booking(
                input.Id, input.Name, input.Email, input.Phone, input.Amount);
            return Run(input, booking.Pay, booking.Summary);
        })
        .Expect("nieopłacona, e-mail do normalizacji",
            new BookingInput("B-1", "Anna Nowak", " Anna@Kino.PL ", "600 100 200",
                Amount("74.00"), []), """
            Rezerwacja B-1
            Klient: Anna Nowak <anna@kino.pl>, tel. 600-100-200
            Kwota: 74.00
            Platnosc: oczekuje

            """)
        .Expect("opłacona kartą, telefon z prefiksem",
            new BookingInput("B-2", "Jan Kowalski", "jan@kino.pl", "+48 600-100-201",
                Amount("31.60"), ["4111 1111 1111 1234"]), """
            Rezerwacja B-2
            Klient: Jan Kowalski <jan@kino.pl>, tel. 600-100-201
            Kwota: 31.60
            Platnosc: oplacona karta **** 1234

            """)
        .Expect("druga płatność odrzucona",
            new BookingInput("B-3", "Jan Kowalski", "jan@kino.pl", "600100202",
                Amount("25.00"), ["4111 1111 1111 1234", "5500 0000 0000 0004"]),
            """
            Rezerwacja B-3
            Klient: Jan Kowalski <jan@kino.pl>, tel. 600-100-202
            Kwota: 25.00
            Platnosc: oplacona karta **** 1234
            BLAD: Rezerwacja B-3 jest juz oplacona

            """);

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepSummarizesBookingsTheSameWay(string test) => Scene.Run(test);

    private static string Run(BookingInput input, Action<string> pay, Func<string> summary)
    {
        var errors = new StringBuilder();
        foreach (var card in input.Cards)
        {
            try
            {
                pay(card);
            }
            catch (InvalidOperationException e)
            {
                errors.Append("BLAD: ").Append(e.Message).Append('\n');
            }
        }
        return summary() + errors;
    }

    private static decimal Amount(string value) => decimal.Parse(value, CultureInfo.InvariantCulture);
}
