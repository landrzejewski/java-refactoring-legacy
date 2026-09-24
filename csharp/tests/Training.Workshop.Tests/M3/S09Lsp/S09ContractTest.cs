namespace Training.Workshop.Tests.M3.S09Lsp;

/// <summary>
/// Testy kontraktowe: ten sam zestaw sprawdzeń dla KAŻDEJ implementacji danego typu.
/// Dają dowody zgodności (LSP) dla sprawdzonych stanów - nie formalny dowód.
/// </summary>
public sealed class S09ContractTest
{
    /// <summary>Widok testu na salę z dowolnego wariantu (w wariantach to różne klasy o tych samych nazwach).</summary>
    public interface IHallUnderTest
    {
        void Reserve(int seat);

        bool IsFree(int seat);

        int FreeSeats { get; }

        int Capacity { get; }
    }

    private static readonly Dictionary<string, Func<IHallUnderTest>> Halls = new()
    {
        ["start: Hall"] = () => Of(new Training.Workshop.M3.S09Lsp.Start.Hall(5)),
        ["step1: Hall"] = () => Of(new Training.Workshop.M3.S09Lsp.Step1.Hall(5)),
        ["step2: Hall"] = () => Of(new Training.Workshop.M3.S09Lsp.Step2.Hall(5)),
        // Step2.ReadOnlyHall nie jest już Hall - kompilator nie pozwala dodać go do tej listy.
    };

    private static readonly Dictionary<string, Func<IHallUnderTest>> Maps = new()
    {
        ["step1: ReadOnlyHall"] = () => Of(new Training.Workshop.M3.S09Lsp.Step1.ReadOnlyHall(5, new HashSet<int> { 1, 4 })),
        ["step2: ReadOnlyHall"] = () => Of(new Training.Workshop.M3.S09Lsp.Step2.ReadOnlyHall(5, new HashSet<int> { 1, 4 })),
        ["step2: Hall"] = () => Of(new Training.Workshop.M3.S09Lsp.Step2.Hall(5)),
    };

    public static TheoryData<string> HallCases => new(Halls.Keys);

    public static TheoryData<string> MapCases => new(Maps.Keys);

    /// <summary>Kontrakt Hall.Reserve: wolne miejsce zostaje zajęte, licznik maleje, druga rezerwacja - błąd.</summary>
    internal static void ObeysReserveContract(IHallUnderTest hall)
    {
        var before = hall.FreeSeats;
        Assert.True(hall.IsFree(3));
        hall.Reserve(3);
        Assert.Multiple(
            () => Assert.False(hall.IsFree(3)),
            () => Assert.Equal(before - 1, hall.FreeSeats),
            () => Assert.Throws<InvalidOperationException>(() => hall.Reserve(3)));
    }

    /// <summary>Kontrakt odczytu (ISeatMap): FreeSeats zgodne z IsFree dla wszystkich miejsc.</summary>
    internal static void ObeysReadContract(IHallUnderTest seats)
    {
        var free = Enumerable.Range(1, seats.Capacity).Count(seats.IsFree);
        Assert.Equal(free, seats.FreeSeats);
    }

    [Theory]
    [MemberData(nameof(HallCases))]
    public void EveryHallObeysTheReserveContract(string hall) => ObeysReserveContract(Halls[hall]());

    [Theory]
    [MemberData(nameof(MapCases))]
    public void EverySeatMapObeysTheReadContract(string map) => ObeysReadContract(Maps[map]());

    [Fact]
    public void ReadOnlyHallAsSubclassBreaksTheReserveContract()
    {
        // krok 1 = stan ze Start dla ReadOnlyHall (Start jest edytowany na żywo, więc sprawdzamy kopię)
        var archived = new Training.Workshop.M3.S09Lsp.Step1.ReadOnlyHall(5, new HashSet<int>());
        Assert.Throws<NotSupportedException>(() => ObeysReserveContract(Of(archived)));
    }

    // Start jest edytowany na żywo - test dotyka go tylko przez API wspólne dla wszystkich kroków.
    private static IHallUnderTest Of(Training.Workshop.M3.S09Lsp.Start.Hall hall) =>
        new View(hall.Reserve, hall.IsFree, () => hall.FreeSeats, () => hall.Capacity);

    private static IHallUnderTest Of(Training.Workshop.M3.S09Lsp.Step1.Hall hall) =>
        new View(hall.Reserve, hall.IsFree, () => hall.FreeSeats, () => hall.Capacity);

    private static IHallUnderTest Of(Training.Workshop.M3.S09Lsp.Step2.Hall hall) =>
        new View(hall.Reserve, hall.IsFree, () => hall.FreeSeats, () => hall.Capacity);

    /// <summary>Sala archiwalna z kroku 2 ma tylko rolę odczytu - Reserve w widoku testu jest niedostępne.</summary>
    private static IHallUnderTest Of(Training.Workshop.M3.S09Lsp.Step2.ReadOnlyHall seats) =>
        new View(_ => Assert.Fail("ISeatMap nie ma Reserve"), seats.IsFree, () => seats.FreeSeats, () => seats.Capacity);

    private sealed class View(Action<int> reserve, Func<int, bool> isFree, Func<int> freeSeats, Func<int> capacity)
        : IHallUnderTest
    {
        public void Reserve(int seat) => reserve(seat);

        public bool IsFree(int seat) => isFree(seat);

        public int FreeSeats => freeSeats();

        public int Capacity => capacity();
    }
}
