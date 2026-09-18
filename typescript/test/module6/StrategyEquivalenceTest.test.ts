import { describe, expect, it } from 'vitest';
import { ArithmeticError, IllegalArgumentError } from '../../src/shared/errors.js';
import { DeploymentCostCalculator } from '../../src/module6/strategy/after/DeploymentCostCalculator.js';
import { ExpeditedCostPolicy } from '../../src/module6/strategy/after/ExpeditedCostPolicy.js';
import { StandardCostPolicy } from '../../src/module6/strategy/after/StandardCostPolicy.js';
import {
  DeploymentMode,
  LegacyDeploymentCostCalculator,
} from '../../src/module6/strategy/before/LegacyDeploymentCostCalculator.js';

describe('StrategyEquivalenceTest', () => {
  it('preservesEveryLegacyCalculationVariant', () => {
    const legacy = new LegacyDeploymentCostCalculator();

    for (const baseCost of [0, 1, 4, 10_001]) {
      expect(new DeploymentCostCalculator(new StandardCostPolicy()).calculate(baseCost)).toBe(
        legacy.calculate(baseCost, DeploymentMode.STANDARD),
      );
      expect(new DeploymentCostCalculator(new ExpeditedCostPolicy()).calculate(baseCost)).toBe(
        legacy.calculate(baseCost, DeploymentMode.EXPEDITED),
      );
    }
  });

  it('keepsInputValidationInTheContext', () => {
    const calculator = new DeploymentCostCalculator({ calculate: () => 0 });

    expect(() => calculator.calculate(-1)).toThrow(IllegalArgumentError);
    expect(() => calculator.calculate(-1)).toThrow('base cost must not be negative');
  });

  it('preservesOverflowPolicyOfTheExpeditedVariant', () => {
    const legacy = new LegacyDeploymentCostCalculator();
    const refactored = new DeploymentCostCalculator(new ExpeditedCostPolicy());

    // Long.MAX_VALUE → Number.MAX_SAFE_INTEGER.
    expect(() => legacy.calculate(Number.MAX_SAFE_INTEGER, DeploymentMode.EXPEDITED)).toThrow(
      ArithmeticError,
    );
    expect(() => refactored.calculate(Number.MAX_SAFE_INTEGER)).toThrow(ArithmeticError);
  });
});
