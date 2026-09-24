using Training.Workshop.M8.S12ExpandContract;
using Training.Workshop.Shared;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M8.S12ExpandContract;

/// <summary>Test równoważności: w każdym kroku zapisana rezerwacja wraca w niezmienionej postaci.</summary>
public sealed class S12EquivalenceTest
{
    internal static readonly Booking Anna = new("B1", "anna@kino.pl", ["A5", "A10"], Money.Of("84.00"));
    internal static readonly Booking Jan = new("B2", "jan@kino.pl", ["C7"], Money.Of("25.00"));

    private static readonly Scene<Booking, Booking?> Scene = Support.Scene.Variants<Booking, Booking?>()
        .Variant("start", booking =>
        {
            var repository = new Training.Workshop.M8.S12ExpandContract.Start.BookingRepository(new BookingTable());
            repository.Save(booking);
            return repository.Find(booking.Id);
        })
        .Variant("step1", booking =>
        {
            var repository = new Training.Workshop.M8.S12ExpandContract.Step1.BookingRepository(new BookingTable());
            repository.Save(booking);
            return repository.Find(booking.Id);
        })
        .Variant("step2", booking =>
        {
            var repository = new Training.Workshop.M8.S12ExpandContract.Step2.BookingRepository(new BookingTable());
            repository.Save(booking);
            return repository.Find(booking.Id);
        })
        .Variant("step3", booking =>
        {
            var repository = new Training.Workshop.M8.S12ExpandContract.Step3.BookingRepository(new BookingTable());
            repository.Save(booking);
            return repository.Find(booking.Id);
        })
        .Variant("step4", booking =>
        {
            var repository = new Training.Workshop.M8.S12ExpandContract.Step4.BookingRepository(new BookingTable());
            repository.Save(booking);
            return repository.Find(booking.Id);
        })
        .Expect("dwa miejsca", Anna, Anna)
        .Expect("jedno miejsce", Jan, Jan);

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepReadsBackWhatItSaved(string test) => Scene.Run(test);
}
