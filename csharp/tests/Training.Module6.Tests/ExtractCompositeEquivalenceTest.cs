using Before = Training.Module6.ExtractComposite.Before.LegacyPlanNodes;
using After = Training.Module6.ExtractComposite.After.PlanNodes;

namespace Training.Module6.Tests;

public sealed class ExtractCompositeEquivalenceTest
{
    [Fact]
    public void ExtractedSuperclassCentralizesChildStorageAndAccumulation()
    {
        var beforeRelease = new Before.ReleaseGroup();
        beforeRelease.Add(new Before.TaskNode(5));
        beforeRelease.Add(new Before.TaskNode(8));
        var afterRelease = new After.ReleaseGroup();
        afterRelease.Add(new After.TaskNode(5));
        afterRelease.Add(new After.TaskNode(8));

        Assert.Equal(beforeRelease.TotalMinutes(), afterRelease.TotalMinutes());
        Assert.Equal(beforeRelease.Children().Count, afterRelease.Children().Count);
    }

    [Fact]
    public void ExtractedCompositeDefensivelyExposesChildren()
    {
        var group = new After.RollbackGroup();
        group.Add(new After.TaskNode(3));

        Assert.Throws<NotSupportedException>(
            () => ((ICollection<After.IPlanNode>)group.Children()).Clear());
    }
}
