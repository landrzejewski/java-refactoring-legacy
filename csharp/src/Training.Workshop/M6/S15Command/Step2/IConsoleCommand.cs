namespace Training.Workshop.M6.S15Command.Step2;

/// <summary>Krok 2: kontrakt komendy - dane żądania (args) i stan (till) przychodzą jako argumenty.</summary>
public interface IConsoleCommand
{
    string Execute(string args, Till till);
}
