namespace Training.Module5.Tests;

[Collection("Console")]
public sealed class Module5ExamplesTest
{
    [Fact]
    public void RunsAllModuleExamples()
    {
        var originalOut = Console.Out;
        using var output = new StringWriter();
        Console.SetOut(output);
        try
        {
            Module5Examples.Main([]);
        }
        finally
        {
            Console.SetOut(originalOut);
        }

        // Same text as the Java Module5Examples prints.
        string[] expected =
        [
            "Strategy cost: 12500",
            "Polymorphic steps: [executed:deploy.sh, approved-by:anna]",
            "Type object requires approval: true",
            "Composite total: 16",
            "Visitor total: 16",
            "Factory probe: http-ok:/health",
            "Decorated deployment: deployed:rel-42, audit=[start:rel-42, success:rel-42]",
            "State: DEPLOYED",
            "Observer events: [audit:rel-42, metric:rel-42]",
            "Adapter result: ops|release:rel-42",
            "Command result: rolled-back:rel-42",
            "Template result: ReleaseDraft[releaseId=rel-42, service=payments]",
            "Singleton timeout: PT30S",
            "Extract Composite equivalent: true",
            "Remove Middle Man: dep-42 -> RUNNING",
            "Command/Strategy/State: cost=1300, log=[scale-up -> instances=3, maintenance=off], status=DEPLOYING",
            "Template Method -> Strategy: # Highlights 1.4.0 | - [FEATURE] Blue-green deployments",
            "Chain of Responsibility: CHANGE_ADVISORY_BOARD (high-risk production change)",
            "Boundary Object: build-17 [a1b2c3d] SUCCESS coverage=82.5% failed=0, releasable=true",
            "Facade: published rel-42 to prod, effects=[stored rel-42.jar, rendered rel-42/prod.yaml, queued rel-42.jar with rel-42/prod.yaml, announced rel-42 on prod]",
            "Anti-Corruption Layer: canary targets=[test-a]",
            "Mediator: published 1.4.0, versionEditable=false",
            "Inline Singleton: rel-42: 10% -> 50% -> 100%"
        ];
        Assert.Equal(
            expected,
            output.ToString().Split(Environment.NewLine, StringSplitOptions.RemoveEmptyEntries));
    }
}
