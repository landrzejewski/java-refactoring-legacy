namespace Training.Patterns.Behavioral.Memento.Accounts;

public class Memento
{
    private readonly decimal balance;

    internal Memento(decimal balance)
    {
        this.balance = balance;
    }

    internal decimal Balance() => balance;
}
