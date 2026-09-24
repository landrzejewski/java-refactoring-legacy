using Training.Workshop.M6.S06Builder.Step3;

namespace Training.Workshop.Tests.M6.S06Builder;

/// <summary>Niezmienniki buildera: jednorazowość, unikalne sale, niemutowalny wynik.</summary>
public sealed class S06SolutionTest
{
    private static readonly DateOnly Day = new(2026, 10, 3);

    [Fact]
    public void BuilderCannotBeReused()
    {
        var builder = ScheduleBuilder.Day(Day).Hall("Sala 1", hall => hall.Screening("Diuna", 18, 0));
        builder.Build();
        Assert.Throws<InvalidOperationException>(builder.Build);
        Assert.Throws<InvalidOperationException>(() => builder.Hall("Sala 2", hall => { }));
    }

    [Fact]
    public void HallNamesMustBeUnique()
    {
        var error = Assert.Throws<InvalidOperationException>(() => ScheduleBuilder.Day(Day)
            .Hall("Sala 1", hall => { })
            .Hall("Sala 1", hall => { }));
        Assert.Equal("duplicate hall: Sala 1", error.Message);
    }

    [Fact]
    public void BuiltTreeIsImmutable()
    {
        var day = ScheduleBuilder.Day(Day).Hall("Sala 1", hall => hall.Screening("Diuna", 18, 0)).Build();
        Assert.Throws<NotSupportedException>(() => ((ICollection<Hall>)day.Halls).Clear());
        Assert.Throws<NotSupportedException>(() => ((ICollection<Screening>)day.Halls[0].Screenings).Clear());
    }

    [Fact]
    public void ClassicBuilderRejectsScreeningBeforeHall()
    {
        var builder = new Training.Workshop.M6.S06Builder.Step1.ScheduleBuilder(Day);
        Assert.Throws<InvalidOperationException>(() => builder.Screening("Diuna", 18, 0));
    }
}
