package pl.training.workshop.m5.s09_observer;

/** Port programu lojalnościowego - istniejąca integracja, której nie zmieniamy. */
public interface LoyaltyProgram {
    void addPoints(String email, int points);
}
