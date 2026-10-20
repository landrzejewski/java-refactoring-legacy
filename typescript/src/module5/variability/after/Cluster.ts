import { IllegalStateError } from '../../../shared/errors.js';

/**
 * Odbiorca poleceń operatora.
 */
export class Cluster {
  private instanceCount = 2;
  private maintenanceOn = false;

  instances(): number {
    return this.instanceCount;
  }

  maintenance(): boolean {
    return this.maintenanceOn;
  }

  // W Javie trzy poniższe metody są package-private: wołają je tylko polecenia
  // z OperatorCommand. TS nie ma widoczności pakietowej, więc są publiczne (@internal).

  /** @internal */
  addInstance(): void {
    this.instanceCount++;
  }

  /** @internal */
  removeInstance(): void {
    if (this.instanceCount === 1) {
      throw new IllegalStateError('cannot scale below one instance');
    }
    this.instanceCount--;
  }

  /** @internal */
  toggleMaintenance(): void {
    this.maintenanceOn = !this.maintenanceOn;
  }

  toString(): string {
    return 'instances=' + this.instanceCount + ', maintenance=' + (this.maintenanceOn ? 'on' : 'off');
  }
}
