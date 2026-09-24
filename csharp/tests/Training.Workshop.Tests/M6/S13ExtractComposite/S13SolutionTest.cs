using Training.Workshop.M6.S13ExtractComposite.Step2;

namespace Training.Workshop.Tests.M6.S13ExtractComposite;

/// <summary>Wspólna nadklasa pilnuje kontraktu dzieci raz dla wszystkich kontenerów.</summary>
public sealed class S13SolutionTest
{
    [Fact]
    public void EveryContainerRejectsNullChild()
    {
        foreach (CompositeProgramItem container in new CompositeProgramItem[] { new Marathon("M"), new ShortsBlock("B") })
        {
            Assert.Throws<ArgumentNullException>(() => container.Add(null!));
        }
    }

    [Fact]
    public void ChildrenViewIsACopy()
    {
        var marathon = new Marathon("M");
        var children = (ICollection<IProgramItem>)marathon.Children;
        Assert.Throws<NotSupportedException>(() => children.Add(null!));
        Assert.Equal(0, marathon.Minutes);
    }
}
