using Training.Workshop.M4.S07MoveMethod;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M4.S07MoveMethod;

/// <summary>Test równoważności: ten sam wydruk rezerwacji po każdym przeniesieniu metody.</summary>
public sealed class S07EquivalenceTest
{
    internal static readonly BookingData Diuna = new("R-1", "Diuna", 3, new DateTime(2026, 9, 25, 20, 0, 0),
        1, [1, 2, 3, 4, 5], 1);

    private static readonly Scene<BookingData, string> Scene = Support.Scene.Variants<BookingData, string>()
        .Variant("start", d => new Training.Workshop.M4.S07MoveMethod.Start.BookingPrinter().Print(
            new Training.Workshop.M4.S07MoveMethod.Start.Booking(d.Id,
                new Training.Workshop.M4.S07MoveMethod.Start.Screening(
                    d.Title, d.Format, d.Start, d.Hall, d.FreeSeats), d.Seat)))
        .Variant("step1", d => new Training.Workshop.M4.S07MoveMethod.Step1.BookingPrinter().Print(
            new Training.Workshop.M4.S07MoveMethod.Step1.Booking(d.Id,
                new Training.Workshop.M4.S07MoveMethod.Step1.Screening(
                    d.Title, d.Format, d.Start, d.Hall, d.FreeSeats), d.Seat)))
        .Variant("step2", d => new Training.Workshop.M4.S07MoveMethod.Step2.BookingPrinter().Print(
            new Training.Workshop.M4.S07MoveMethod.Step2.Booking(d.Id,
                new Training.Workshop.M4.S07MoveMethod.Step2.Screening(
                    d.Title, d.Format, d.Start, d.Hall, d.FreeSeats), d.Seat)))
        .Expect("IMAX, miejsce 1 (wartość 1 != indeks 1)", Diuna, """
            Rezerwacja R-1
            Diuna (IMAX), sala 1, 2026-09-25 20:00
            Miejsce: 1
            Pozostale wolne: [2, 3, 4, 5]

            """)
        .Expect("3D rano, miejsce 8 (indeks 8 nie istnieje)",
            new BookingData("R-2", "Kraina Lodu", 2, new DateTime(2026, 9, 26, 10, 30, 0),
                2, [7, 8, 9], 8), """
            Rezerwacja R-2
            Kraina Lodu (3D), sala 2, 2026-09-26 10:30
            Miejsce: 8
            Pozostale wolne: [7, 9]

            """)
        .Expect("2D, miejsce 2 na pozycji 2 - ten przypadek NIE odróżni Remove(int index)",
            new BookingData("R-3", "Amator", 1, new DateTime(2026, 9, 26, 18, 0, 0),
                3, [3, 1, 2], 2), """
            Rezerwacja R-3
            Amator (2D), sala 3, 2026-09-26 18:00
            Miejsce: 2
            Pozostale wolne: [3, 1]

            """);

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepPrintsTheSameBooking(string test) => Scene.Run(test);
}
