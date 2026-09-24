namespace Training.Workshop.M8.S11StagedRollout.Step3;

/// <summary>Krok 3 (bez zmian): router deleguje decyzję do jawnej polityki.</summary>
public sealed class CheckoutRouter
{
    private readonly RolloutPolicy _policy;

    public CheckoutRouter()
        : this(RolloutPolicy.Current())
    {
    }

    public CheckoutRouter(RolloutPolicy policy)
    {
        _policy = policy;
    }

    public bool UseNewCheckout(string email)
    {
        return _policy.Allows(email);
    }
}
