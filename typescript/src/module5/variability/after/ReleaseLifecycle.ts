import { IllegalStateError } from '../../../shared/errors.js';
import { ReleaseStatus } from '../ReleaseStatus.js';

/**
 * State: obiekt sam zmienia zachowanie, przechodząc między stanami. Klient wywołuje
 * zawsze te same metody i nie wybiera wariantu.
 */
export class ReleaseLifecycle {
  private state: State = PLANNED;

  status(): ReleaseStatus {
    return this.state.status;
  }

  advance(): void {
    this.state = this.state.advance();
  }

  rollback(): void {
    this.state = this.state.rollback();
  }

  banner(): string {
    return this.state.banner;
  }
}

// Odpowiednik prywatnego enuma ReleaseLifecycle.State z ciałami stałych:
// nieeksportowana klasa bazowa i jedna instancja (podklasa anonimowa) na stan.
abstract class State {
  constructor(
    readonly status: ReleaseStatus,
    readonly banner: string,
  ) {}

  advance(): State {
    throw new IllegalStateError('cannot advance release in state ' + this.status);
  }

  rollback(): State {
    throw new IllegalStateError('cannot roll back release in state ' + this.status);
  }
}

const PLANNED: State = new (class extends State {
  override advance(): State {
    return DEPLOYING;
  }
})(ReleaseStatus.PLANNED, 'release planned');

const DEPLOYING: State = new (class extends State {
  override advance(): State {
    return LIVE;
  }

  override rollback(): State {
    return ROLLED_BACK;
  }
})(ReleaseStatus.DEPLOYING, 'deployment in progress');

const LIVE: State = new (class extends State {
  override rollback(): State {
    return ROLLED_BACK;
  }
})(ReleaseStatus.LIVE, 'release is live');

const ROLLED_BACK: State = new (class extends State {})(ReleaseStatus.ROLLED_BACK, 'release rolled back');
