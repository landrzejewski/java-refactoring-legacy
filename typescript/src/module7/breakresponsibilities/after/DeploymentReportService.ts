import { requireNonNull } from '../../../shared/requireNonNull.js';
import type { DeploymentSample } from '../DeploymentSample.js';
import type { DeploymentMetricsCalculator } from './DeploymentMetricsCalculator.js';
import type { DeploymentReportFormatter } from './DeploymentReportFormatter.js';

export class DeploymentReportService {
  private readonly calculator: DeploymentMetricsCalculator;
  private readonly formatter: DeploymentReportFormatter;

  constructor(
    calculator: DeploymentMetricsCalculator,
    formatter: DeploymentReportFormatter,
  ) {
    this.calculator = requireNonNull(calculator, 'calculator');
    this.formatter = requireNonNull(formatter, 'formatter');
  }

  generate(samples: readonly DeploymentSample[]): string {
    return this.formatter.format(this.calculator.calculate(samples));
  }
}
