namespace Training.Patterns.Behavioral.State;

/// <summary>
/// Java: <c>enum OrderStatus implements OrderState</c> with constant-specific bodies.
/// C# enums cannot implement interfaces, so this is a closed class with one singleton per constant;
/// each constant re-implements <see cref="IOrderState"/> and overrides only the allowed transitions,
/// the rest fall back to the default interface methods.
/// </summary>
public abstract class OrderStatus : IOrderState
{
    public static readonly OrderStatus NEW = new NewStatus();
    public static readonly OrderStatus PAID = new PaidStatus();
    public static readonly OrderStatus SHIPPED = new PlainStatus("SHIPPED");
    public static readonly OrderStatus CANCELLED = new PlainStatus("CANCELLED");

    private readonly string name;

    private OrderStatus(string name)
    {
        this.name = name;
    }

    public static IReadOnlyList<OrderStatus> Values() => [NEW, PAID, SHIPPED, CANCELLED];

    public string Name() => name;

    public override string ToString() => name;

    private sealed class NewStatus() : OrderStatus("NEW"), IOrderState
    {
        public IOrderState Pay() => PAID;

        public IOrderState Cancel() => CANCELLED;
    }

    private sealed class PaidStatus() : OrderStatus("PAID"), IOrderState
    {
        public IOrderState Ship() => SHIPPED;

        public IOrderState Cancel() => CANCELLED;
    }

    private sealed class PlainStatus(string name) : OrderStatus(name);
}
