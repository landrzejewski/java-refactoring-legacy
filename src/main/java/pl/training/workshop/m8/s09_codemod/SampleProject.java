package pl.training.workshop.m8.s09_codemod;

import java.util.List;

/**
 * Próbka "projektu" dla codemodu: API kina ze starą sygnaturą book(..., boolean, boolean)
 * oznaczoną jako przestarzała, łudząco podobne API hotelu i klasa kliencka do migracji.
 * Kod jako tekst - codemod działa na źródłach, nie na skompilowanych klasach sceny.
 */
public final class SampleProject {
    public static final List<String> API = List.of("""
            package cinema;

            public class BookingService {
                /** @deprecated boolean-y zamieniono na Channel i Glasses */
                @Deprecated
                public String book(String screening, String email, String[] seats, String[] types,
                        boolean web, boolean ownGlasses) {
                    return book(screening, email, seats, types, web ? Channel.WEB : Channel.BOX_OFFICE,
                            ownGlasses ? Glasses.OWN : Glasses.RENTED);
                }

                public String book(String screening, String email, String[] seats, String[] types,
                        Channel channel, Glasses glasses) {
                    return screening + " " + channel + " " + glasses;
                }
            }
            """, """
            package cinema;

            public enum Channel { WEB, BOX_OFFICE }
            """, """
            package cinema;

            public enum Glasses { OWN, RENTED }
            """, """
            package hotel;

            public class HotelService {
                public String book(String hotel, String email, String[] rooms, String[] guests,
                        boolean breakfast, boolean parking) {
                    return hotel;
                }
            }
            """);

    /** Klient do migracji; numery linii mają znaczenie dla testów (wywołania w 11, 16-18, 22). */
    public static final String TICKET_DESK = """
            package desk;

            import cinema.BookingService;
            import hotel.HotelService;

            public class TicketDesk {
                private final BookingService bookings = new BookingService();
                private final HotelService hotels = new HotelService();

                public String online(String email, String[] seats, String[] types) {
                    return bookings.book("S1", email, seats, types, true, false);
                }

                public String boxOffice(String[] seats, String[] types, boolean own) {
                    // stary przyklad: bookings.book("S1", "x", seats, types, false, true)
                    return bookings.book("S2", "kasa@kino.pl",
                            seats, types,
                            false, own);
                }

                public String stay(String email, String[] rooms, String[] guests) {
                    return hotels.book("H1", email, rooms, guests, true, true);
                }
            }
            """;

    private SampleProject() {
    }
}
