package pl.training.patterns.behavioral.strategy.movies;

public enum MovieType {
    REGULAR(2, 2, 1.5),
    CHILDREN(3, 1.5, 1.5),
    NEW_RELEASE(0, 0, 3);

    private final int freeRentalPeriodInDays;
    private final double initialCost;
    private final double costPerDay;

    double getValueFor(long periodInDays) {
        if (periodInDays < 0) {
            throw new IllegalArgumentException(
                    "periodInDays must not be negative");
        }
        return getInitialCost() + getValueForPeriod(periodInDays);
    }

    private double getValueForPeriod(long periodInDays) {
        long paidDays = Math.max(
                0, periodInDays - getFreeRentalPeriodInDays());
        return paidDays * getCostPerDay();
    }

    public int getFreeRentalPeriodInDays() {
        return this.freeRentalPeriodInDays;
    }

    public double getInitialCost() {
        return this.initialCost;
    }

    public double getCostPerDay() {
        return this.costPerDay;
    }

    MovieType(
            int freeRentalPeriodInDays,
            double initialCost,
            double costPerDay) {
        this.freeRentalPeriodInDays = freeRentalPeriodInDays;
        this.initialCost = initialCost;
        this.costPerDay = costPerDay;
    }
}
