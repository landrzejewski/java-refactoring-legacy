using System.Reflection;
using Training.Workshop.M5.S05ExtractSubclass.Step4;

namespace Training.Workshop.Tests.M5.S05ExtractSubclass;

/// <summary>Extract Subclass celowo zmienia klasę runtime - to widzą GetType(), Equals, ORM, JSON i switch.</summary>
public sealed class S05SolutionTest
{
    [Fact]
    public void BeforeExtractionFactoriesReturnOneClass()
    {
        var premiere = Training.Workshop.M5.S05ExtractSubclass.Step1.Screening.Premiere("Amator", "2D", "Anna Nowak");
        var regular = Training.Workshop.M5.S05ExtractSubclass.Step1.Screening.Regular("Amator", "2D");
        Assert.Equal(regular.GetType(), premiere.GetType());
    }

    [Fact]
    public void SolutionPicksRuntimeClassInFactory()
    {
        Assert.Equal(typeof(PremiereScreening), Screening.Premiere("Amator", "2D", "Anna Nowak").GetType());
        Assert.Equal(typeof(Screening), Screening.Regular("Amator", "2D").GetType());
    }

    [Fact]
    public void SolutionKeepsPremiereStateOnlyInSubclass()
    {
        const BindingFlags declared = BindingFlags.NonPublic | BindingFlags.Instance | BindingFlags.DeclaredOnly;
        Assert.Null(typeof(Screening).GetField("_guest", declared));
        Assert.Null(typeof(Screening).GetField("_premiere", declared));
        // odpowiednik getPermittedSubclasses(): konstruktor private protected zamyka hierarchię w assembly,
        // a jedynym podtypem w assembly jest PremiereScreening
        Assert.All(typeof(Screening).GetConstructors(declared), c => Assert.True(c.IsFamilyAndAssembly));
        Assert.Equal([typeof(PremiereScreening)],
            typeof(Screening).Assembly.GetTypes().Where(t => t.BaseType == typeof(Screening)).ToArray());
        Assert.True(typeof(PremiereScreening).IsSealed);
    }
}
