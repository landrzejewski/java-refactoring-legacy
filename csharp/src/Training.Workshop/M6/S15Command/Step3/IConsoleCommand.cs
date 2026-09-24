namespace Training.Workshop.M6.S15Command.Step3;

/// <summary>Krok 3: kontrakt komendy - dane żądania (args) i stan (till) przychodzą jako argumenty.</summary>
public interface IConsoleCommand
{
    string Execute(string args, Till till);
}
