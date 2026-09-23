package pl.training.workshop.m8.s11_stagedrollout.step2;

/** Krok 2 (bez zmian): router deleguje decyzję do jawnej polityki. */
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
