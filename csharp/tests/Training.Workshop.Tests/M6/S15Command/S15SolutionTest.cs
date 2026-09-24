using Training.Workshop.M6.S15Command.Step3;

namespace Training.Workshop.Tests.M6.S15Command;

/// <summary>Rejestr komend jest otwarty na nowe komendy bez zmiany dyspozytora.</summary>
public sealed class S15SolutionTest
{
    [Fact]
    public void NewCommandIsJustARegistryEntry()
    {
        var console = new CashierConsole(new Dictionary<string, IConsoleCommand>
        {
            ["REPORT"] = new ReportCommand(),
            ["HELLO"] = new HelloCommand(),
        });
        Assert.Equal("Dzien dobry Anna", console.Handle("hello Anna"));
        Assert.Equal("Nieznana komenda: SELL", console.Handle("SELL 1 Diuna"));
    }

    private sealed class HelloCommand : IConsoleCommand
    {
        public string Execute(string args, Till till) => "Dzien dobry " + args;
    }
}
