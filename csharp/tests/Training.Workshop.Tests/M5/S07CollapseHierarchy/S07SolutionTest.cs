using System.Reflection;

namespace Training.Workshop.Tests.M5.S07CollapseHierarchy;

/// <summary>Collapse Hierarchy: zbędny poziom znika, nazwa używana przez klientów (Hall) zostaje.</summary>
public sealed class S07SolutionTest
{
    [Fact]
    public void BeforeCollapseSubclassHasNoOwnStateOrBehaviour()
    {
        const BindingFlags declared = BindingFlags.Public | BindingFlags.NonPublic | BindingFlags.Instance
            | BindingFlags.Static | BindingFlags.DeclaredOnly;
        var imaxHall = typeof(Training.Workshop.M5.S07CollapseHierarchy.Step2.ImaxHall);
        Assert.Empty(imaxHall.GetFields(declared));
        Assert.Empty(imaxHall.GetMethods(declared));
    }

    [Fact]
    public void SolutionHasSingleFinalClass()
    {
        var hall = typeof(Training.Workshop.M5.S07CollapseHierarchy.Step3.Hall);
        Assert.True(hall.IsSealed);
        Assert.Null(hall.Assembly.GetType("Training.Workshop.M5.S07CollapseHierarchy.Step3.ImaxHall"));
        Assert.Equal(hall, Training.Workshop.M5.S07CollapseHierarchy.Step3.Hall.Imax("Sala IMAX", 14, 22).GetType());
    }
}
