using Training.Module6.Command.After;
using Training.Module6.Command.Before;
using DeploymentAction = Training.Module6.Command.After.DeploymentAction;

namespace Training.Module6.Tests;

public sealed class CommandEquivalenceTest
{
    [Fact]
    public void LookupPreservesEveryLegacyDispatchBranch()
    {
        var before = new LegacyDeploymentDispatcher();
        var after = new DeploymentCommandDispatcher(
            new Dictionary<DeploymentAction, IDeploymentCommand>
            {
                [DeploymentAction.Pause] = new PauseDeployment(),
                [DeploymentAction.Rollback] = new RollbackDeployment()
            });

        Assert.Equal(
            before.Dispatch(LegacyDeploymentDispatcher.DeploymentAction.Pause, "rel-42"),
            after.Dispatch(DeploymentAction.Pause, "rel-42"));
        Assert.Equal(
            before.Dispatch(LegacyDeploymentDispatcher.DeploymentAction.Rollback, "rel-42"),
            after.Dispatch(DeploymentAction.Rollback, "rel-42"));
    }

    [Fact]
    public void RegistryIsDefensivelyCopiedAndMustBeComplete()
    {
        var source = new Dictionary<DeploymentAction, IDeploymentCommand>
        {
            [DeploymentAction.Pause] = new PauseDeployment(),
            [DeploymentAction.Rollback] = new RollbackDeployment()
        };
        var dispatcher = new DeploymentCommandDispatcher(source);
        source[DeploymentAction.Pause] = new ConstantCommand("changed");
        source.Remove(DeploymentAction.Rollback);

        Assert.Equal("paused:rel-42", dispatcher.Dispatch(DeploymentAction.Pause, "rel-42"));
        Assert.Equal(
            "rolled-back:rel-42",
            dispatcher.Dispatch(DeploymentAction.Rollback, "rel-42"));
        Assert.Equal(
            "missing commands: [Rollback]",
            Assert.Throws<ArgumentException>(
                () => new DeploymentCommandDispatcher(
                    new Dictionary<DeploymentAction, IDeploymentCommand>
                    {
                        [DeploymentAction.Pause] = new PauseDeployment()
                    })).Message);
    }

    private sealed class ConstantCommand(string result) : IDeploymentCommand
    {
        public string Execute(string releaseId) => result;
    }
}
