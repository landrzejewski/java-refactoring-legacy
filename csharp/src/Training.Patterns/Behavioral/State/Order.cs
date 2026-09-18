namespace Training.Patterns.Behavioral.State;

public class Order
{
    public IOrderState State { get; private set; } = OrderStatus.NEW;

    public void Pay()
    {
        State = State.Pay();
    }

    public void Ship()
    {
        State = State.Ship();
    }

    public void Cancel()
    {
        State = State.Cancel();
    }
}
