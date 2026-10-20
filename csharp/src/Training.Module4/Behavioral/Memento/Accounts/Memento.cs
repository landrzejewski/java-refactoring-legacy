namespace Training.Module4.Behavioral.Memento.Accounts;

public class Memento
{
    private readonly decimal balance;

    internal Memento(decimal balance)
    {
        this.balance = balance;
    }

    internal decimal Balance() => balance;
}
