import { randomUUID } from 'node:crypto';
import { Decimal } from 'decimal.js';
import { afterEach, describe, expect, it, vi } from 'vitest';
import { IllegalArgumentError, IllegalStateError } from '../../src/shared/errors.js';
import * as iteratorApplication from '../../src/patterns/behavioral/iterator/Application.js';
import { Account } from '../../src/patterns/behavioral/memento/accounts/Account.js';
import { Order as StateOrder } from '../../src/patterns/behavioral/state/Order.js';
import { OrderStatus } from '../../src/patterns/behavioral/state/OrderStatus.js';
import { MovieType } from '../../src/patterns/behavioral/strategy/movies/MovieType.js';
import { Order } from '../../src/patterns/behavioral/strategy/movies/Order.js';
import { FtpConnection } from '../../src/patterns/creational/abstractfactory/ftp/FtpConnection.js';
import * as functionalStyleState from '../../src/patterns/fn/FunctionalStyleState.js';
import { AirConditioningController } from '../../src/patterns/structural/adapter/AirConditioningController.js';
import { TemperatureControllerAdapter } from '../../src/patterns/structural/adapter/TemperatureControllerAdapter.js';
import { TreeFactory } from '../../src/patterns/structural/flyweight/TreeFactory.js';

class RecordingAirConditioningController extends AirConditioningController {
  readonly deltas: number[] = [];

  override changeTemperature(deltaInFahrenheit: number): void {
    this.deltas.push(deltaInFahrenheit);
  }
}

// Java: System.setOut(...) + output.lines().toList()
function outputOf(action: () => void): string[] {
  const lines: string[] = [];
  const spy = vi.spyOn(console, 'log').mockImplementation((...args: unknown[]) => {
    lines.push(...args.map(String).join(' ').split('\n'));
  });
  try {
    action();
  } finally {
    spy.mockRestore();
  }
  return lines;
}

describe('AdditionalPatternsExamplesTest', () => {
  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('convertsTemperatureDifferencesWithoutAnAbsoluteScaleOffset', () => {
    const controller = new RecordingAirConditioningController();
    const adapter = new TemperatureControllerAdapter(controller);

    adapter.temperatureUp(10);
    adapter.temperatureDown(10);

    expect(controller.deltas).toEqual([18.0, -18.0]);
  });

  it('doesNotSubtractAChargeDuringTheFreeRentalPeriod', () => {
    expect(new Order(MovieType.REGULAR).getTotalValue(1)).toBe(2.0);
    expect(new Order(MovieType.CHILDREN).getTotalValue(1)).toBe(1.5);
    expect(new Order(MovieType.NEW_RELEASE).getTotalValue(1)).toBe(3.0);
    expect(() => new Order(MovieType.REGULAR).getTotalValue(-1)).toThrow(IllegalArgumentError);
  });

  it('usesTheRegisteredFtpControlPort', () => {
    expect(new FtpConnection().getPort()).toBe(21);
  });

  it('restoresTheBalanceRecordedInTheMemento', () => {
    const account = new Account(randomUUID());
    const memento = account.createMemento();

    account.deposit(new Decimal(10));
    // Java BigDecimal.equals compares value and scale -> compare value and textual form
    expect(account.getBalance().equals(new Decimal(10))).toBe(true);
    expect(account.getBalance().toString()).toBe('10');

    account.restoreMemento(memento);
    expect(account.getBalance().equals(new Decimal(0))).toBe(true);
    expect(account.getBalance().toString()).toBe('0');
  });

  it('sharesOneFlyweightInstancePerIntrinsicState', () => {
    expect(TreeFactory.getTreeType('Oak', 'green')).toBe(TreeFactory.getTreeType('Oak', 'green'));
  });

  it('orderStateMovesThroughItsLifecycleAndRejectsInvalidTransitions', () => {
    const order = new StateOrder();
    expect(order.getState()).toBe(OrderStatus.NEW);

    order.pay();
    expect(order.getState()).toBe(OrderStatus.PAID);

    order.ship();
    expect(order.getState()).toBe(OrderStatus.SHIPPED);

    expect(() => order.cancel()).toThrow(IllegalStateError);
    expect(order.getState()).toBe(OrderStatus.SHIPPED);

    const cancelled = new StateOrder();
    cancelled.cancel();
    expect(cancelled.getState()).toBe(OrderStatus.CANCELLED);
    expect(() => cancelled.pay()).toThrow(IllegalStateError);
  });

  it('functionalStateDemoAlternatesTransitions', () => {
    expect(outputOf(() => functionalStyleState.run())).toEqual([
      'Locked → unlocking',
      'Unlocked → locking',
      'Locked → unlocking',
    ]);
  });

  it('iteratorDemoHasDeterministicOrder', () => {
    expect(outputOf(() => iteratorApplication.run())).toEqual(['1', '2', '3', '4', '5']);
  });
});
