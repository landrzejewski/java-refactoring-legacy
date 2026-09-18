namespace Training.Module2.Tests;

public sealed class Module2ExamplesTest
{
    [Fact]
    public void RunsAllModuleExamples()
    {
        Assert.Null(Record.Exception(() => Module2Examples.Main([])));
    }
}
