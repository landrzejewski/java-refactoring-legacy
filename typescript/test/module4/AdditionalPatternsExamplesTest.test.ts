import { randomUUID } from 'node:crypto';
import { Decimal } from 'decimal.js';
import { afterEach, describe, expect, it, vi } from 'vitest';
import { IllegalArgumentError, IllegalStateError } from '../../src/shared/errors.js';
import * as iteratorApplication from '../../src/module4/behavioral/iterator/Application.js';
import { Playlist } from '../../src/module4/behavioral/iterator/Playlist.js';
import { Song } from '../../src/module4/behavioral/iterator/Song.js';
import * as mediatorApplication from '../../src/module4/behavioral/mediator/Application.js';
import { Account } from '../../src/module4/behavioral/memento/accounts/Account.js';
import { Order as StateOrder } from '../../src/module4/behavioral/state/Order.js';
import { OrderStatus } from '../../src/module4/behavioral/state/OrderStatus.js';
import { MovieType } from '../../src/module4/behavioral/strategy/movies/MovieType.js';
import { Order } from '../../src/module4/behavioral/strategy/movies/Order.js';
import { FtpConnection } from '../../src/module4/creational/abstractfactory/ftp/FtpConnection.js';
import * as functionalStyleState from '../../src/module4/fn/FunctionalStyleState.js';
import { AirConditioningController } from '../../src/module4/structural/adapter/AirConditioningController.js';
import { TemperatureControllerAdapter } from '../../src/module4/structural/adapter/TemperatureControllerAdapter.js';
import * as bridgeApplication from '../../src/module4/structural/bridge/Application.js';
import { TreeFactory } from '../../src/module4/structural/flyweight/TreeFactory.js';

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

  it('iteratorDemoWalksThePlaylistForwardAndBackward', () => {
    expect(outputOf(() => iteratorApplication.run())).toEqual([
      'Playing: Intro (90s)',
      'Playing: Main Theme (225s)',
      'Playing: Outro (130s)',
      'Reversed: Outro',
      'Reversed: Main Theme',
      'Reversed: Intro',
    ]);
  });

  it('playlistIteratorIsExhaustedAfterTheLastSong', () => {
    const playlist = new Playlist();
    playlist.add(new Song('Only', 60));
    const iterator = playlist[Symbol.iterator]();

    expect(iterator.next()).toEqual({ done: false, value: new Song('Only', 60) });
    // Java: hasNext() == false and next() throws NoSuchElementException;
    // the JavaScript iterator protocol keeps answering { done: true } instead.
    expect(iterator.next().done).toBe(true);
    expect(iterator.next().done).toBe(true);
    expect(new Playlist().reversed()[Symbol.iterator]().next().done).toBe(true);
  });

  it('bridgeCombinesEveryMessageKindWithEverySender', () => {
    expect(outputOf(() => bridgeApplication.run())).toEqual([
      'Email to anna: Server restarted',
      'SMS to anna: Server restarted',
      'Email to anna: [URGENT] SERVER RESTARTED',
      'SMS to anna: [URGENT] SERVER RESTARTED',
    ]);
  });

  it('mediatorDeliversMessagesToEveryoneExceptTheSender', () => {
    expect(outputOf(() => mediatorApplication.run())).toEqual([
      'Alice sends: Hello everyone',
      'Bob received from Alice: Hello everyone',
      'Carol received from Alice: Hello everyone',
      'Bob sends: Hi Alice',
      'Alice received from Bob: Hi Alice',
      'Carol received from Bob: Hi Alice',
    ]);
  });
});
