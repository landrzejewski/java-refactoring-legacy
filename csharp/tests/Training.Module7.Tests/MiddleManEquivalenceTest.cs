using Training.Module7.MiddleMan;
using Training.Module7.MiddleMan.Before;

namespace Training.Module7.Tests;

public sealed class MiddleManEquivalenceTest
{
    [Fact]
    public void DirectCollaborationPreservesLookupUpdatesOverwriteAndRendering()
    {
        var beforeRegistry = new MiddleMan.Before.DeploymentRegistry();
        var beforeService = new ReleaseService(beforeRegistry);
        var beforeDashboard = new MiddleMan.Before.ReleaseDashboard(beforeService);

        var afterRegistry = new MiddleMan.After.DeploymentRegistry();
        var afterDashboard = new MiddleMan.After.ReleaseDashboard(afterRegistry);

        Assert.Equal(beforeService.StatusOf("dep-42"), afterRegistry.StatusOf("dep-42"));
        Assert.Equal(DeploymentStatus.Unknown, afterRegistry.StatusOf("dep-42"));
        Assert.Equal(beforeDashboard.Render("dep-42"), afterDashboard.Render("dep-42"));

        beforeService.Update("dep-42", DeploymentStatus.Running);
        afterRegistry.Update("dep-42", DeploymentStatus.Running);
        Assert.Equal(beforeService.StatusOf("dep-42"), afterRegistry.StatusOf("dep-42"));
        Assert.Equal(beforeDashboard.Render("dep-42"), afterDashboard.Render("dep-42"));

        beforeService.Update("dep-42", DeploymentStatus.Succeeded);
        afterRegistry.Update("dep-42", DeploymentStatus.Succeeded);
        Assert.Equal(beforeService.StatusOf("dep-42"), afterRegistry.StatusOf("dep-42"));
        Assert.Equal("dep-42 -> SUCCEEDED", afterDashboard.Render("dep-42"));
        Assert.Equal(beforeDashboard.Render("dep-42"), afterDashboard.Render("dep-42"));
    }

    [Fact]
    public void ValidationRemainsAtTheRegistryBoundary()
    {
        var beforeRegistry = new MiddleMan.Before.DeploymentRegistry();
        var beforeService = new ReleaseService(beforeRegistry);
        var beforeDashboard = new MiddleMan.Before.ReleaseDashboard(beforeService);

        var afterRegistry = new MiddleMan.After.DeploymentRegistry();
        var afterDashboard = new MiddleMan.After.ReleaseDashboard(afterRegistry);

        foreach (var invalidId in new string?[] { null, "", "  \t" })
        {
            Assert.Equal(
                Assert.Throws<ArgumentException>(() => beforeService.StatusOf(invalidId!)).Message,
                Assert.Throws<ArgumentException>(() => afterRegistry.StatusOf(invalidId!)).Message);
            Assert.Equal(
                Assert.Throws<ArgumentException>(() => beforeDashboard.Render(invalidId!)).Message,
                Assert.Throws<ArgumentException>(() => afterDashboard.Render(invalidId!)).Message);
        }

        // Enums are never null in C#; an undefined value plays the role of Java's null status.
        const DeploymentStatus undefinedStatus = (DeploymentStatus)42;
        Assert.Equal(
            Assert.Throws<ArgumentOutOfRangeException>(
                () => beforeService.Update("dep-42", undefinedStatus)).Message,
            Assert.Throws<ArgumentOutOfRangeException>(
                () => afterRegistry.Update("dep-42", undefinedStatus)).Message);
        Assert.Equal(DeploymentStatus.Unknown, beforeService.StatusOf("dep-42"));
        Assert.Equal(DeploymentStatus.Unknown, afterRegistry.StatusOf("dep-42"));
    }
}
