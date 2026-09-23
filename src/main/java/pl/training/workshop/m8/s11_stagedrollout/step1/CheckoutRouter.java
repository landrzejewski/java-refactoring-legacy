package pl.training.workshop.m8.s11_stagedrollout.step1;

/** Krok 1: router deleguje decyzję do jawnej polityki (domyślnie dotychczasowe ustawienia). */
public final class CheckoutRouter {
    private final RolloutPolicy policy;

    public CheckoutRouter() {
        this(RolloutPolicy.current());
    }

    public CheckoutRouter(RolloutPolicy policy) {
        this.policy = policy;
    }

    public boolean useNewCheckout(String email) {
        return policy.allows(email);
    }
}
