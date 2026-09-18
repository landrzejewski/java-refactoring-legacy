namespace Training.Patterns.Behavioral.Memento.Accounts;

public class Account
{
    private readonly Guid number;

    public decimal Balance { get; private set; } = decimal.Zero;

    public Account(Guid number)
    {
        this.number = number; // Java: Objects.requireNonNull - Guid is a value type, never null
    }

    public void Deposit(decimal amount)
    {
        Balance += amount;
    }

    public Memento CreateMemento() => new(Balance);

    public void RestoreMemento(Memento memento)
    {
        ArgumentNullException.ThrowIfNull(memento);
        Balance = memento.Balance();
    }

    public override string ToString() => "Account(number=" + number + ", balance=" + Balance + ")";
}
