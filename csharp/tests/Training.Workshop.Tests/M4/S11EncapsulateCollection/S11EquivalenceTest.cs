using System.Globalization;
using Training.Workshop.M4.S11EncapsulateCollection;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M4.S11EncapsulateCollection;

/// <summary>Test równoważności: przez SeatDesk wszystkie warianty dają te same miejsca i tę samą kwotę.</summary>
public sealed class S11EquivalenceTest
{
    /// <summary>Polecenie kasy: +rząd/numer wybiera miejsce, -rząd/numer je zwalnia.</summary>
    private static readonly IReadOnlyList<string> Commands = ["+1/5", "+10/3", "-1/5", "+12/1", "+10/3"];

    private static readonly Scene<IReadOnlyList<string>, string> Scene = Support.Scene.Variants<IReadOnlyList<string>, string>()
        .Variant("start", commands =>
        {
            var desk = new Training.Workshop.M4.S11EncapsulateCollection.Start.SeatDesk();
            var booking = new Training.Workshop.M4.S11EncapsulateCollection.Start.Booking();
            return Run(commands, (seat, add) =>
            {
                if (add)
                {
                    desk.Select(booking, seat);
                }
                else
                {
                    desk.Release(booking, seat);
                }
            }, () => Show(booking.Seats) + " -> " + Show(booking.Total()));
        })
        .Variant("step1", commands =>
        {
            var desk = new Training.Workshop.M4.S11EncapsulateCollection.Step1.SeatDesk();
            var booking = new Training.Workshop.M4.S11EncapsulateCollection.Step1.Booking();
            return Run(commands, (seat, add) =>
            {
                if (add)
                {
                    desk.Select(booking, seat);
                }
                else
                {
                    desk.Release(booking, seat);
                }
            }, () => Show(booking.Seats) + " -> " + Show(booking.Total()));
        })
        .Variant("step2", commands =>
        {
            var desk = new Training.Workshop.M4.S11EncapsulateCollection.Step2.SeatDesk();
            var booking = new Training.Workshop.M4.S11EncapsulateCollection.Step2.Booking();
            return Run(commands, (seat, add) =>
            {
                if (add)
                {
                    desk.Select(booking, seat);
                }
                else
                {
                    desk.Release(booking, seat);
                }
            }, () => Show(booking.Seats) + " -> " + Show(booking.Total()));
        })
        .Variant("step3", commands =>
        {
            var desk = new Training.Workshop.M4.S11EncapsulateCollection.Step3.SeatDesk();
            var booking = new Training.Workshop.M4.S11EncapsulateCollection.Step3.Booking();
            return Run(commands, (seat, add) =>
            {
                if (add)
                {
                    desk.Select(booking, seat);
                }
                else
                {
                    desk.Release(booking, seat);
                }
            }, () => Show(booking.Seats) + " -> " + Show(booking.Total()));
        })
        .Expect("wybór, zwolnienie, duplikat zostaje (lista, nie zbiór)", Commands,
            "[10/3, 12/1, 10/3] -> 105.00")
        .Expect("pusta rezerwacja", [], "[] -> 0.00")
        .Expect("zwolnienie miejsca, którego nie ma", ["+1/1", "-2/2"], "[1/1] -> 25.00");

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryVariantSelectsSeatsTheSameWay(string test) => Scene.Run(test);

    private static string Run(IReadOnlyList<string> commands, Action<Seat, bool> desk, Func<string> result)
    {
        foreach (var command in commands)
        {
            var parts = command[1..].Split('/');
            desk(new Seat(int.Parse(parts[0], CultureInfo.InvariantCulture), int.Parse(parts[1], CultureInfo.InvariantCulture)),
                command.StartsWith('+'));
        }
        return result();
    }

    private static string Show(IEnumerable<Seat> seats) => "[" + string.Join(", ", seats) + "]";

    private static string Show(decimal amount) => amount.ToString(CultureInfo.InvariantCulture);
}
