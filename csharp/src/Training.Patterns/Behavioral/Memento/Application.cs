using Training.Patterns.Behavioral.Memento.Accounts;

namespace Training.Patterns.Behavioral.Memento;

public class Application
{
    private static readonly JulLogger log = JulLogger.GetLogger(typeof(Application));

    public static void Run()
    {
        var account = new Account(Guid.NewGuid());
        IReadOnlyList<Accounts.Memento> caretaker = [account.CreateMemento()];
        account.Deposit(10m);
        log.Info("Deposited balance: " + account.Balance);
        account.RestoreMemento(caretaker[0]);
        log.Info("Restored balance: " + account.Balance);
    }
}
