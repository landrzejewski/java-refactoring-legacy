import { requireNonNull } from '../../shared/requireNonNull.js';

/** Java: @FunctionalInterface interface State extends Consumer<FunctionalStyleState>. */
export type State = (context: FunctionalStyleState) => void;

export class FunctionalStyleState {
  private static readonly LOCKED: State = (state) => {
    console.log('Locked → unlocking');
    state.setState(FunctionalStyleState.UNLOCKED);
  };

  private static readonly UNLOCKED: State = (state) => {
    console.log('Unlocked → locking');
    state.setState(FunctionalStyleState.LOCKED);
  };

  private current: State;

  constructor(initial: State) {
    this.current = requireNonNull(initial, 'initial');
  }

  setState(newState: State): void {
    this.current = requireNonNull(newState, 'newState');
  }

  onEvent(): void {
    this.current(this);
  }

  static main(): void {
    const turnstile = new FunctionalStyleState(FunctionalStyleState.LOCKED);
    turnstile.onEvent(); // unlock
    turnstile.onEvent(); // lock
    turnstile.onEvent(); // unlock again
  }
}

export function run(): void {
  FunctionalStyleState.main();
}
