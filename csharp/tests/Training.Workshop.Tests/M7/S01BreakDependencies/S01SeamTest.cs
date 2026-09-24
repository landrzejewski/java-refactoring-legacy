using Microsoft.Extensions.Time.Testing;
using Training.Workshop.M7.S01BreakDependencies;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M7.S01BreakDependencies;

/// <summary>
/// Scena s01: start jest celowo nietestowalny. Każdy krok otwiera kolejny seam,
/// a test rośnie razem z nim: separacja bazy, kontrola czasu, obserwacja maili.
/// </summary>
public sealed class S01SeamTest
{
    private static readonly DateTime Now = new(2026, 3, 10, 18, 0, 0);

    private static readonly Scene<IReadOnlyList<PaidBooking>, string> Scene = Support.Scene
        .Variants<IReadOnlyList<PaidBooking>, string>()
        .Variant("step3", RunStep3)
        .Variant("step4", RunStep4)
        .Expect("seans za 90 minut",
            [Booking("B1", Now.AddMinutes(90), false)],
            "sent=1 [MAIL anna@kino.pl | Przypomnienie: Diuna | Seans zaczyna sie o 19:30]"
                + " marked=[B1]")
        .Expect("granica 120 minut wchodzi, 121 nie",
            [Booking("B1", Now.AddMinutes(120), false), Booking("B2", Now.AddMinutes(121), false)],
            "sent=1 [MAIL anna@kino.pl | Przypomnienie: Diuna | Seans zaczyna sie o 20:00]"
                + " marked=[B1]")
        .Expect("juz przypomniane i juz rozpoczete",
            [Booking("B1", Now.AddMinutes(30), true), Booking("B2", Now.AddMinutes(-1), false)],
            "sent=0 [] marked=[]");

    public static TheoryData<string> Cases => Scene.Tests();

    [Fact]
    public void StartCannotEvenRunInATest()
    {
        var job = new Training.Workshop.M7.S01BreakDependencies.Start.ShowtimeReminderJob();
        var error = Assert.Throws<InvalidOperationException>(() => job.Run());
        Assert.StartsWith("brak polaczenia", error.Message);
    }

    [Fact]
    public void Step1KeepsConnectionLifetimeOfProductionConstructor()
    {
        var job = new Training.Workshop.M7.S01BreakDependencies.Step1.ShowtimeReminderJob();
        Assert.Throws<InvalidOperationException>(() => job.Run());
    }

    [Fact]
    public void Step1RunsWithFakeStoreWhenNothingIsDue()
    {
        var store = new FakeStore(
            Booking("B1", new DateTime(2020, 1, 1, 20, 0, 0), false),
            Booking("B2", new DateTime(2026, 3, 10, 19, 0, 0), true));
        var job = new Training.Workshop.M7.S01BreakDependencies.Step1.ShowtimeReminderJob(store.Step1);
        Assert.Equal(0, job.Run());
        Assert.Empty(store.Marked);
    }

    [Fact]
    public void Step2ControlsTimeButStillHitsStaticMailer()
    {
        var clock = FixedClock();
        var notDue = new FakeStore(
            Booking("B1", Now.AddMinutes(121), false),
            Booking("B2", Now, false));
        var quiet = new Training.Workshop.M7.S01BreakDependencies.Step2.ShowtimeReminderJob(notDue.Step2, clock);
        Assert.Equal(0, quiet.Run());

        var due = new FakeStore(Booking("B3", Now.AddMinutes(120), false));
        var noisy = new Training.Workshop.M7.S01BreakDependencies.Step2.ShowtimeReminderJob(due.Step2, clock);
        var error = Assert.Throws<InvalidOperationException>(() => noisy.Run());
        Assert.StartsWith("SMTP", error.Message);
    }

    [Theory]
    [MemberData(nameof(Cases))]
    public void FirstRealTestForSteps3And4(string test) => Scene.Run(test);

    private static string RunStep3(IReadOnlyList<PaidBooking> bookings)
    {
        var store = new FakeStore([.. bookings]);
        var job = new RecordingStep3Job(store.Step3, FixedClock());
        return "sent=" + job.Run() + " " + Show(job.Mails) + " marked=" + Show(store.Marked);
    }

    private static string RunStep4(IReadOnlyList<PaidBooking> bookings)
    {
        var store = new FakeStore([.. bookings]);
        var mails = new List<string>();
        var job = new Training.Workshop.M7.S01BreakDependencies.Step4.ShowtimeReminderJob(
            store.Step4, FixedClock(),
            (to, subject, body) => mails.Add("MAIL " + to + " | " + subject + " | " + body));
        return "sent=" + job.Run() + " " + Show(mails) + " marked=" + Show(store.Marked);
    }

    private static FakeTimeProvider FixedClock() => new(new DateTimeOffset(Now, TimeSpan.Zero));

    private static PaidBooking Booking(string id, DateTime start, bool reminded) =>
        new(id, "anna@kino.pl", "Diuna", start, reminded);

    private static string Show(IEnumerable<string> items) => "[" + string.Join(", ", items) + "]";

    /// <summary>Podklasa testowa (seam z kroku 3): nadpisuje wysyłkę i zapamiętuje maile.</summary>
    private sealed class RecordingStep3Job(
        Func<Training.Workshop.M7.S01BreakDependencies.Step3.IBookingStore> stores, TimeProvider clock)
        : Training.Workshop.M7.S01BreakDependencies.Step3.ShowtimeReminderJob(stores, clock)
    {
        public List<string> Mails { get; } = [];

        protected override void SendReminder(string to, string subject, string body)
        {
            Mails.Add("MAIL " + to + " | " + subject + " | " + body);
        }
    }

    /// <summary>Ręczny fake bazy: podaje rezerwacje i zapamiętuje oznaczone jako przypomniane.</summary>
    private sealed class FakeStore(params PaidBooking[] bookings)
        : Training.Workshop.M7.S01BreakDependencies.Step1.IBookingStore,
          Training.Workshop.M7.S01BreakDependencies.Step2.IBookingStore,
          Training.Workshop.M7.S01BreakDependencies.Step3.IBookingStore,
          Training.Workshop.M7.S01BreakDependencies.Step4.IBookingStore
    {
        public List<string> Marked { get; } = [];

        public IReadOnlyList<PaidBooking> PaidBookings() => bookings;

        public void MarkReminded(string bookingId) => Marked.Add(bookingId);

        public Training.Workshop.M7.S01BreakDependencies.Step1.IBookingStore Step1() => this;

        public Training.Workshop.M7.S01BreakDependencies.Step2.IBookingStore Step2() => this;

        public Training.Workshop.M7.S01BreakDependencies.Step3.IBookingStore Step3() => this;

        public Training.Workshop.M7.S01BreakDependencies.Step4.IBookingStore Step4() => this;
    }
}
