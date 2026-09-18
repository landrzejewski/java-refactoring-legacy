using Training.Module6.Decorator.After;
using Training.Module6.Decorator.Before;

namespace Training.Module6.Tests;

public sealed class DecoratorEquivalenceTest
{
    [Fact]
    public void PreservesResultAndAuditOrderOnSuccess()
    {
        var beforeAudit = new List<string>();
        var afterAudit = new List<string>();

        var before = new LegacyDeploymentRunner(beforeAudit.Add).Run("rel-42");
        var after = new AuditedDeploymentRunner(
            new BasicDeploymentRunner(), afterAudit.Add).Run("rel-42");

        Assert.Equal(before, after);
        Assert.Equal(beforeAudit, afterAudit);
    }

    [Fact]
    public void PreservesAuditOrderAndPropagatesTheSameFailure()
    {
        var audit = new List<string>();
        Exception failure = new InvalidOperationException("gateway unavailable");
        var runner = new AuditedDeploymentRunner(new FailingRunner(failure), audit.Add);

        var propagated = Assert.ThrowsAny<Exception>(() => runner.Run("rel-42"));

        Assert.Same(failure, propagated);
        Assert.Equal(["start:rel-42", "failure:rel-42:InvalidOperationException"], audit);
    }

    [Fact]
    public void InvalidInputPreservesFailureAndAuditTrace()
    {
        var beforeAudit = new List<string>();
        var afterAudit = new List<string>();

        var beforeFailure = Assert.ThrowsAny<Exception>(
            () => new LegacyDeploymentRunner(beforeAudit.Add).Run(" "));
        var afterFailure = Assert.ThrowsAny<Exception>(
            () => new AuditedDeploymentRunner(
                new BasicDeploymentRunner(), afterAudit.Add).Run(" "));

        Assert.Equal(beforeFailure.GetType(), afterFailure.GetType());
        Assert.Equal(beforeFailure.Message, afterFailure.Message);
        Assert.Equal(beforeAudit, afterAudit);
    }

    // Java passes a throwing lambda; a C# lambda cannot implement an interface.
    private sealed class FailingRunner(Exception failure) : IDeploymentRunner
    {
        public string Run(string? releaseId) => throw failure;
    }
}
