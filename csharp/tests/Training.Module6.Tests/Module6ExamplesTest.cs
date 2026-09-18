namespace Training.Module6.Tests;

[Collection("Console")]
public sealed class Module6ExamplesTest
{
    [Fact]
    public void RunsAllModuleExamples()
    {
        var originalOut = Console.Out;
        using var output = new StringWriter();
        Console.SetOut(output);
        try
        {
            Module6Examples.Main([]);
        }
        finally
        {
            Console.SetOut(originalOut);
        }

        // Same text as the Java Module6Examples prints.
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
            "Extract Composite equivalent: true"
        ];
        Assert.Equal(
            expected,
            output.ToString().Split(Environment.NewLine, StringSplitOptions.RemoveEmptyEntries));
    }
}
