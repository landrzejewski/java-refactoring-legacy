using Training.Workshop.M4.S11EncapsulateCollection;

namespace Training.Workshop.Tests.M4.S11EncapsulateCollection;

/// <summary>
/// Trzy kontrakty kolekcji - tabela ze slajdu jako test. Dla każdego wariantu sprawdzamy:
/// czy klient może zmienić listę z właściwości i czy WCZEŚNIEJ pobrana lista widzi późniejsze zmiany właściciela.
/// Tu CELOWO nie ma równoważności: Step2 i Step3 zmieniają kontrakt właściwości.
/// </summary>
public sealed class S11CollectionContractTest
{
    private static readonly Seat Later = new(10, 2);

    [Fact]
    public void StartPublicFieldIsTheLiveMutableList()
    {
        var booking = new Training.Workshop.M4.S11EncapsulateCollection.Start.Booking();
        Assert.Equal("klient zmienia: tak, widzi zmiany: tak", Describe(() => booking.Seats, booking.Seats.Add));
    }

    [Fact]
    public void Step1GetterStillReturnsTheSameAlias()
    {
        var booking = new Training.Workshop.M4.S11EncapsulateCollection.Step1.Booking();
        Assert.Equal("klient zmienia: tak, widzi zmiany: tak", Describe(() => booking.Seats, booking.AddSeat));
    }

    [Fact]
    public void Step2AsReadOnlyIsAReadOnlyLiveView()
    {
        var booking = new Training.Workshop.M4.S11EncapsulateCollection.Step2.Booking();
        Assert.Equal("klient zmienia: nie, widzi zmiany: tak", Describe(() => booking.Seats, booking.AddSeat));
    }

    [Fact]
    public void Step3ToImmutableListIsASnapshot()
    {
        var booking = new Training.Workshop.M4.S11EncapsulateCollection.Step3.Booking();
        Assert.Equal("klient zmienia: nie, widzi zmiany: nie", Describe(() => booking.Seats, booking.AddSeat));
    }

    /// <summary>
    /// Klient próbuje zmienić listę przez <see cref="ICollection{T}"/> - tak jak zrobiłby to kod,
    /// który rzutuje <c>IReadOnlyList</c> na interfejs modyfikujący.
    /// </summary>
    private static string Describe(Func<IReadOnlyList<Seat>> getter, Action<Seat> ownerAdds)
    {
        var seenByClient = getter();
        bool clientCanModify;
        try
        {
            var asCollection = (ICollection<Seat>)seenByClient;
            asCollection.Add(new Seat(1, 1));
            asCollection.Remove(new Seat(1, 1));
            clientCanModify = true;
        }
        catch (NotSupportedException)
        {
            clientCanModify = false;
        }
        ownerAdds(Later);
        bool seesLaterChanges = seenByClient.Contains(Later);
        return "klient zmienia: " + (clientCanModify ? "tak" : "nie")
            + ", widzi zmiany: " + (seesLaterChanges ? "tak" : "nie");
    }
}
