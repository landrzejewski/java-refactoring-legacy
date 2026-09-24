namespace Training.Workshop.Tests.M3.S09Lsp;

/// <summary>Kasa (na zwykłej sali) i raport (na sali archiwalnej) działają tak samo we wszystkich wariantach.</summary>
public sealed class S09EquivalenceTest
{
    private static readonly Support.Scene<int, string> Scene = Support.Scene.Variants<int, string>()
        .Variant("start", seat =>
        {
            var hall = new Training.Workshop.M3.S09Lsp.Start.Hall(10);
            hall.Reserve(1);
            return new Training.Workshop.M3.S09Lsp.Start.BoxOffice().Sell(hall, seat) + " | "
                + new Training.Workshop.M3.S09Lsp.Start.OccupancyReport().Describe(
                    new Training.Workshop.M3.S09Lsp.Start.ReadOnlyHall(10, new HashSet<int> { 1, 2, seat }));
        })
        .Variant("step1", seat =>
        {
            var hall = new Training.Workshop.M3.S09Lsp.Step1.Hall(10);
            hall.Reserve(1);
            return new Training.Workshop.M3.S09Lsp.Step1.BoxOffice().Sell(hall, seat) + " | "
                + new Training.Workshop.M3.S09Lsp.Step1.OccupancyReport().Describe(
                    new Training.Workshop.M3.S09Lsp.Step1.ReadOnlyHall(10, new HashSet<int> { 1, 2, seat }));
        })
        .Variant("step2", seat =>
        {
            var hall = new Training.Workshop.M3.S09Lsp.Step2.Hall(10);
            hall.Reserve(1);
            return new Training.Workshop.M3.S09Lsp.Step2.BoxOffice().Sell(hall, seat) + " | "
                + new Training.Workshop.M3.S09Lsp.Step2.OccupancyReport().Describe(
                    new Training.Workshop.M3.S09Lsp.Step2.ReadOnlyHall(10, new HashSet<int> { 1, 2, seat }));
        })
        .Expect("miejsce 5", 5, "sprzedano miejsce 5, wolnych: 8 | zajete 3 z 10")
        .Expect("ostatnie miejsce", 10, "sprzedano miejsce 10, wolnych: 8 | zajete 3 z 10");

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void SellingAndReportingBehaveTheSame(string test) => Scene.Run(test);
}
