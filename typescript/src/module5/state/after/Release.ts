import { IllegalStateError } from '../../../shared/errors.js';

// Odpowiednik zagnieżdżonego enuma Release.Status.
export enum Status {
  DRAFT = 'DRAFT',
  APPROVED = 'APPROVED',
  DEPLOYED = 'DEPLOYED',
  CANCELLED = 'CANCELLED',
}

export class Release {
  private state: ReleaseState = DRAFT_STATE;

  status(): Status {
    return this.state.status();
  }

  approve(): void {
    this.state = this.state.approve();
  }

  deploy(): void {
    this.state = this.state.deploy();
  }

  cancel(): void {
    this.state = this.state.cancel();
  }
}

// Prywatna (nieeksportowana) hierarchia stanów. Metody domyślne interfejsu Javy
// → abstrakcyjna klasa bazowa; stany-singletony (enum INSTANCE) → stałe modułu.
abstract class ReleaseState {
  abstract status(): Status;

  approve(): ReleaseState {
    throw this.invalid('approve');
  }

  deploy(): ReleaseState {
    throw this.invalid('deploy');
  }

  cancel(): ReleaseState {
    throw this.invalid('cancel');
  }

  private invalid(action: string): IllegalStateError {
    return new IllegalStateError('cannot ' + action + ' release in state ' + this.status());
  }
}

class DraftState extends ReleaseState {
  override status(): Status {
    return Status.DRAFT;
  }

  override approve(): ReleaseState {
    return APPROVED_STATE;
  }

  override cancel(): ReleaseState {
    return CANCELLED_STATE;
  }
}

class ApprovedState extends ReleaseState {
  override status(): Status {
    return Status.APPROVED;
  }

  override deploy(): ReleaseState {
    return DEPLOYED_STATE;
  }

  override cancel(): ReleaseState {
    return CANCELLED_STATE;
  }
}

class DeployedState extends ReleaseState {
  override status(): Status {
    return Status.DEPLOYED;
  }
}

class CancelledState extends ReleaseState {
  override status(): Status {
    return Status.CANCELLED;
  }
}

const DRAFT_STATE: ReleaseState = new DraftState();
const APPROVED_STATE: ReleaseState = new ApprovedState();
const DEPLOYED_STATE: ReleaseState = new DeployedState();
const CANCELLED_STATE: ReleaseState = new CancelledState();
