namespace Training.Patterns.Behavioral.State;

public interface IOrderState
{
    IOrderState Pay() => throw new InvalidOperationException("Payment is not allowed in state " + this);

    IOrderState Ship() => throw new InvalidOperationException("Shipping is not allowed in state " + this);

    IOrderState Cancel() => throw new InvalidOperationException("Cancellation is not allowed in state " + this);
}
