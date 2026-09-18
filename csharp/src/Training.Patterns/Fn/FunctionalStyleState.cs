namespace Training.Patterns.Fn;

public class FunctionalStyleState
{
    // Java: @FunctionalInterface State extends Consumer<FunctionalStyleState>
    public delegate void State(FunctionalStyleState state);

    private static readonly State LOCKED = state =>
    {
        Console.WriteLine("Locked → unlocking");
        state.SetState(UNLOCKED!);
    };

    private static readonly State UNLOCKED = state =>
    {
        Console.WriteLine("Unlocked → locking");
        state.SetState(LOCKED);
    };

    private State current;

    public FunctionalStyleState(State initial)
    {
        ArgumentNullException.ThrowIfNull(initial);
        current = initial;
    }

    public void SetState(State newState)
    {
        ArgumentNullException.ThrowIfNull(newState);
        current = newState;
    }

    public void OnEvent()
    {
        current(this);
    }

    public static void Run()
    {
        var turnstile = new FunctionalStyleState(LOCKED);
        turnstile.OnEvent(); // unlock
        turnstile.OnEvent(); // lock
        turnstile.OnEvent(); // unlock again
    }
}
