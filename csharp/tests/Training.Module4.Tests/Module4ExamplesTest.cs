namespace Training.Module4.Tests;

[Collection(GlobalStateCollection.Name)]
public sealed class Module4ExamplesTest
{
    [Fact]
    public void RunsAllModuleExamples()
    {
        var originalOut = Console.Out;
        using var output = new StringWriter();
        try
        {
            Console.SetOut(output);

            var exception = Record.Exception(() => Module4Examples.Main([]));

            Assert.Null(exception);
        }
        finally
        {
            Console.SetOut(originalOut);
        }

        Assert.EndsWith(
            "Total: 1172.19\n"
            + "Quote stages equivalent: true" + Environment.NewLine
            + "Catalog snapshot isolated: true" + Environment.NewLine,
            output.ToString());
    }
}
