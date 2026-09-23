package pl.training.workshop.m6.s09_observer;

/** Port programu lojalnościowego - istniejąca integracja, której nie zmieniamy. */
public interface LoyaltyProgram {
    void addPoints(String email, int points);
}
