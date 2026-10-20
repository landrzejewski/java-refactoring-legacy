import { IllegalArgumentError } from '../../../shared/errors.js';
import type { Cluster } from './Cluster.js';

// Kontrakt wspólny dla wszystkich poleceń.
interface OperatorCommandContract {
  readonly kind: string;

  name(): string;

  execute(cluster: Cluster): void;

  undo(cluster: Cluster): void;
}

// Odpowiednik rekordu OperatorCommand.ScaleUp.
export class ScaleUp implements OperatorCommandContract {
  readonly kind = 'scale-up';

  name(): string {
    return 'scale-up';
  }

  execute(cluster: Cluster): void {
    cluster.addInstance();
  }

  undo(cluster: Cluster): void {
    cluster.removeInstance();
  }
}

// Odpowiednik rekordu OperatorCommand.ScaleDown.
export class ScaleDown implements OperatorCommandContract {
  readonly kind = 'scale-down';

  name(): string {
    return 'scale-down';
  }

  execute(cluster: Cluster): void {
    cluster.removeInstance();
  }

  undo(cluster: Cluster): void {
    cluster.addInstance();
  }
}

// Odpowiednik rekordu OperatorCommand.ToggleMaintenance.
export class ToggleMaintenance implements OperatorCommandContract {
  readonly kind = 'maintenance';

  name(): string {
    return 'maintenance';
  }

  execute(cluster: Cluster): void {
    cluster.toggleMaintenance();
  }

  undo(cluster: Cluster): void {
    cluster.toggleMaintenance();
  }
}

/**
 * Command: żądanie operatora jako obiekt, który można zakolejkować, zalogować i cofnąć.
 * Wykonanie i cofnięcie są obok siebie, a nie w dwóch osobnych switch-ach.
 * Odpowiednik `sealed interface OperatorCommand`: zamknięta unia dyskryminowana polem `kind`.
 */
export type OperatorCommand = ScaleUp | ScaleDown | ToggleMaintenance;

// Odpowiednik statycznej metody OperatorCommand.byName(String).
export const OperatorCommand = Object.freeze({
  byName(action: string | null): OperatorCommand {
    switch (action) {
      case 'scale-up':
        return new ScaleUp();
      case 'scale-down':
        return new ScaleDown();
      case 'maintenance':
        return new ToggleMaintenance();
      default:
        throw new IllegalArgumentError('unknown action: ' + String(action));
    }
  },
});
