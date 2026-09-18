namespace Training.Module5.Tests;

[Collection(GlobalStateCollection.Name)]
public sealed class Module5ExamplesTest
{
    [Fact]
    public void RunsAllModuleExamples()
    {
        var originalOut = Console.Out;
        using var output = new StringWriter();
        try
        {
            Console.SetOut(output);

            var exception = Record.Exception(() => Module5Examples.Main([]));

            Assert.Null(exception);
        }
        finally
        {
            Console.SetOut(originalOut);
        }

        var nl = Environment.NewLine;
        Assert.Equal(
            "Hierarchy stages equivalent: true" + nl
            + "Extract subclass equivalent: true" + nl
            + "Collapse hierarchy equivalent: true" + nl
            + "Composition client behavior equivalent: true" + nl
            + "Batch results: [mail-1|OPS|Ready|EMAIL|SENT, sms-1|OPS|Ready|SMS|SENT|RECEIPT]" + nl,
            output.ToString());
    }
}
