namespace Training.Workshop.Tests.M7.S13GodClass;

/// <summary>
/// Scenariusz "jednego dnia kina" - ten sam co w golden master legacy
/// (tests/Training.Workshop.Tests/Legacy/CinemaManagerScript.cs), ale uruchamiany
/// przez ICinemaUnderTest, żeby dało się go puścić na start i na każdym kroku kampanii.
/// </summary>
internal static class S13Script
{
    internal static string Run(ICinemaUnderTest cinema)
    {
        cinema.Reset();
        var log = new List<string>();
        DateTime now = new(2026, 3, 9, 9, 0, 0);
        cinema.Clock(() => now);
        try
        {
            cinema.AddScreening("S1", "Diuna", 3, new DateTime(2026, 3, 10, 20, 0, 0), 12, 10, 10);
            cinema.AddScreening("S2", "Kraina Lodu", 2, new DateTime(2026, 3, 10, 11, 0, 0), 8, 8, 7);
            cinema.AddScreening("S3", "Amator", 1, new DateTime(2026, 3, 10, 18, 30, 0), 10, 12, 9);

            string b1 = cinema.Book("S1", "anna@kino.pl", "600100200",
                ["A5", "B5", "C10"], ["N", "S", "E"], true, false);
            string b2 = cinema.Book("S2", "jan@kino.pl", null,
                ["A1", "B1", "C1", "D7"], ["N", "C", "C", "N"], false, false);
            string b3 = cinema.Book("S2", "ola@kino.pl", "600300400",
                ["E2", "F2"], ["S", "S"], true, true);
            string[] groupSeats = ["A1", "B1", "C1", "D1", "E1", "F1", "G1", "H1", "I1", "J1"];
            string[] groupTypes = ["C", "C", "C", "C", "C", "C", "C", "C", "N", "N"];
            string b4 = cinema.Book("S3", "szkola@kino.pl", null, groupSeats, groupTypes, true, false);
            string b5 = cinema.Book("S3", "piotr@kino.pl", null,
                ["K9"], ["N"], false, false);
            log.Add("book: " + b1 + " " + b2 + " " + b3 + " " + b4 + " " + b5);
            log.Add("book taken: " + cinema.Book("S1", "x@kino.pl", null,
                ["A5"], ["N"], true, false));
            log.Add("book no seat: " + cinema.Book("S1", "x@kino.pl", null,
                ["Z99"], ["N"], true, false));
            log.Add("book mismatch: " + cinema.Book("S1", "x@kino.pl", null,
                ["A1"], [], true, false));
            log.Add("book no screening: " + cinema.Book("S9", "x@kino.pl", null,
                ["A1"], ["N"], true, false));

            log.Add("pay b1: " + cinema.Pay(b1, "4111111111111111"));
            log.Add("pay b1 again: " + cinema.Pay(b1, "4111111111111111"));
            log.Add("pay b2 declined: " + cinema.Pay(b2, "4111111111110000"));
            log.Add("pay b2: " + cinema.Pay(b2, "5555444433331111"));
            log.Add("pay b4: " + cinema.Pay(b4, "4000123412341234"));

            now = new DateTime(2026, 3, 9, 9, 20, 0);
            cinema.ExpireOld();
            log.Add("pay b3 expired: " + cinema.Pay(b3, "4111111111111111"));

            log.Add("cancel b1 early: " + cinema.Cancel(b1));
            now = new DateTime(2026, 3, 10, 10, 0, 0);
            log.Add("cancel b2 late: " + cinema.Cancel(b2));
            log.Add("cancel b2 again: " + cinema.Cancel(b2));
            log.Add("use b4: " + cinema.Use(b4));
            log.Add("use b5 unpaid: " + cinema.Use(b5));

            log.Add("points anna: " + cinema.LoyaltyPoints("anna@kino.pl"));
            log.Add("points szkola: " + cinema.LoyaltyPoints("szkola@kino.pl"));
            log.Add("free S1: " + cinema.FreeSeats("S1").Count);
            log.Add(cinema.DailyReport(new DateOnly(2026, 3, 10)).Trim());
            log.Add(cinema.Settlement("Amator", 1));
            log.Add(cinema.Settlement("Diuna", 2));
            log.AddRange(cinema.SentMessages());
            log.AddRange(cinema.GatewayOperations());
            return string.Join("\n", log) + "\n";
        }
        finally
        {
            cinema.Clock(() => DateTime.Now);
            cinema.Reset();
        }
    }
}
