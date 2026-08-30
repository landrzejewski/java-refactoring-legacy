package pl.training.patterns.fn;

import java.util.Objects;
import java.util.function.Consumer;

public class FunctionalStyleState {
    @FunctionalInterface
    public interface State extends Consumer<FunctionalStyleState> {
    }

    private static final State LOCKED = state -> {
        System.out.println("Locked → unlocking");
        state.setState(FunctionalStyleState.UNLOCKED);
    };
    private static final State UNLOCKED = state -> {
        System.out.println("Unlocked → locking");
        state.setState(FunctionalStyleState.LOCKED);
    };

    private State current;

    public FunctionalStyleState(State initial) {
        this.current = Objects.requireNonNull(initial, "initial");
    }

    public void setState(State newState) {
        this.current = Objects.requireNonNull(newState, "newState");
    }

    public void onEvent() {
        current.accept(this);
    }

    public static void main(String[] args) {
        FunctionalStyleState turnstile = new FunctionalStyleState(LOCKED);
        turnstile.onEvent(); // unlock
        turnstile.onEvent(); // lock
        turnstile.onEvent(); // unlock again
    }
}
