package pl.training.module7.contract.after;

public final class Contracts {
    private Contracts() {
    }

    public static void require(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void ensure(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }

    public static void invariant(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}
