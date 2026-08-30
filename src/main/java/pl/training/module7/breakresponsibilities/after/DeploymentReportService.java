package pl.training.module7.breakresponsibilities.after;

import java.util.List;
import java.util.Objects;

import pl.training.module7.breakresponsibilities.DeploymentSample;

public final class DeploymentReportService {
    private final DeploymentMetricsCalculator calculator;
    private final DeploymentReportFormatter formatter;

    public DeploymentReportService(
            DeploymentMetricsCalculator calculator,
            DeploymentReportFormatter formatter) {
        this.calculator = Objects.requireNonNull(calculator, "calculator");
        this.formatter = Objects.requireNonNull(formatter, "formatter");
    }

    public String generate(List<DeploymentSample> samples) {
        return formatter.format(calculator.calculate(samples));
    }
}
