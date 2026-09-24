namespace Training.Workshop.M8.S09Codemod;

/// <summary>
/// Próbka "projektu" dla codemodu: API kina ze starą sygnaturą Book(..., bool, bool)
/// oznaczoną jako przestarzała, łudząco podobne API hotelu i klasa kliencka do migracji.
/// Kod jako tekst - codemod działa na źródłach, nie na skompilowanych klasach sceny.
/// </summary>
public static class SampleProject
{
    public static readonly IReadOnlyList<string> Api =
    [
        """
        using System;
        using Cinema.Options;

        namespace Cinema;

        public class BookingService
        {
            [Obsolete("bool-e zamieniono na Channel i Glasses")]
            public string Book(string screening, string email, string[] seats, string[] types,
                bool web, bool ownGlasses)
            {
                return Book(screening, email, seats, types, web ? Channel.Web : Channel.BoxOffice,
                    ownGlasses ? Glasses.Own : Glasses.Rented);
            }

            public string Book(string screening, string email, string[] seats, string[] types,
                Channel channel, Glasses glasses)
            {
                return screening + " " + channel + " " + glasses;
            }
        }
        """,
        """
        namespace Cinema.Options;

        public enum Channel { Web, BoxOffice }
        """,
        """
        namespace Cinema.Options;

        public enum Glasses { Own, Rented }
        """,
        """
        namespace Hotel;

        public class HotelService
        {
            public string Book(string hotel, string email, string[] rooms, string[] guests,
                bool breakfast, bool parking)
            {
                return hotel;
            }
        }
        """,
    ];

    /// <summary>Klient do migracji; numery linii mają znaczenie dla testów (wywołania w 13, 19-21, 26).</summary>
    public const string TicketDesk = """
        using Cinema;
        using Hotel;

        namespace Desk;

        public class TicketDesk
        {
            private readonly BookingService _bookings = new BookingService();
            private readonly HotelService _hotels = new HotelService();

            public string Online(string email, string[] seats, string[] types)
            {
                return _bookings.Book("S1", email, seats, types, true, false);
            }

            public string BoxOffice(string[] seats, string[] types, bool own)
            {
                // stary przyklad: _bookings.Book("S1", "x", seats, types, false, true)
                return _bookings.Book("S2", "kasa@kino.pl",
                    seats, types,
                    false, own);
            }

            public string Stay(string email, string[] rooms, string[] guests)
            {
                return _hotels.Book("H1", email, rooms, guests, true, true);
            }
        }

        """;
}
