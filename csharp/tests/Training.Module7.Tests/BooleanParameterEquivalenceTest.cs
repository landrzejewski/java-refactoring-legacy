using System.Reflection;
using Training.Module7.BooleanParameter.After;
using Training.Module7.BooleanParameter.Before;

namespace Training.Module7.Tests;

public sealed class BooleanParameterEquivalenceTest
{
    [Fact]
    public void NamedOperationsPreserveBothLegacyBranches()
    {
        var before = new LegacyDeploymentExecutor();
        var after = new DeploymentExecutor();

        Assert.Equal(before.Execute("dep-42", true), after.Preview("dep-42"));
        Assert.Equal(before.Execute("dep-42", false), after.Deploy("dep-42"));
    }

    [Fact]
    public void ValidationIsIdenticalForBothNamedOperations()
    {
        var before = new LegacyDeploymentExecutor();
        var after = new DeploymentExecutor();

        foreach (var invalidId in new string?[] { null, "", "  \t" })
        {
            var previewMessage = Assert.Throws<ArgumentException>(
                () => before.Execute(invalidId!, true)).Message;
            var deployMessage = Assert.Throws<ArgumentException>(
                () => before.Execute(invalidId!, false)).Message;

            Assert.Equal(
                previewMessage,
                Assert.Throws<ArgumentException>(() => after.Preview(invalidId!)).Message);
            Assert.Equal(
                deployMessage,
                Assert.Throws<ArgumentException>(() => after.Deploy(invalidId!)).Message);
        }
    }

    [Fact]
    public void PublicApiContainsNoBooleanParameterAndExecutorHasNoMutableState()
    {
        var hasPublicBooleanParameter = typeof(DeploymentExecutor)
            .GetMethods(BindingFlags.Public | BindingFlags.Instance
                | BindingFlags.Static | BindingFlags.DeclaredOnly)
            .SelectMany(method => method.GetParameters())
            .Any(parameter => parameter.ParameterType == typeof(bool));

        Assert.False(hasPublicBooleanParameter);

        var executor = new DeploymentExecutor();
        Assert.Equal("preview:dep-42", executor.Preview("dep-42"));
        Assert.Equal("deployed:dep-42", executor.Deploy("dep-42"));
        Assert.Equal("preview:dep-42", executor.Preview("dep-42"));
    }
}
