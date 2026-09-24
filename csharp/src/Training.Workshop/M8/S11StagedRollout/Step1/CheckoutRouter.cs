namespace Training.Workshop.M8.S11StagedRollout.Step1;

/// <summary>Krok 1: router deleguje decyzję do jawnej polityki (domyślnie dotychczasowe ustawienia).</summary>
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
