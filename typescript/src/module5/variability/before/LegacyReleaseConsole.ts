import { assertNever } from '../../../shared/assertNever.js';
import { IllegalArgumentError, IllegalStateError } from '../../../shared/errors.js';
import { requireNonNull } from '../../../shared/requireNonNull.js';
import { CustomerTier } from '../CustomerTier.js';
import { ReleaseStatus } from '../ReleaseStatus.js';

/**
 * Jedna klasa, trzy różne osie zmienności rozwiązane tym samym narzędziem
 * (switch/if): taryfa klienta, polecenia operatora i cykl życia wydania.
 */
export class LegacyReleaseConsole {
  private static readonly ACTIONS: ReadonlySet<string> = new Set(['scale-up', 'scale-down', 'maintenance']);

  private readonly tier: CustomerTier;
  private readonly queue: string[] = [];
  private readonly history: string[] = [];
  private readonly entries: string[] = [];
  private instances = 2;
  private maintenance = false;
  private currentStatus: ReleaseStatus = ReleaseStatus.PLANNED;

  constructor(tier: CustomerTier) {
    this.tier = requireNonNull(tier, 'tier');
  }

  // (a) taryfa jest stała dla klienta, a mimo to wybierana przy każdym wywołaniu
  supportCost(hours: number): number {
    if (hours < 0) {
      throw new IllegalArgumentError('hours must not be negative');
    }
    switch (this.tier) {
      case CustomerTier.STANDARD:
        return hours * 100;
      case CustomerTier.PREMIUM:
        return 500 + hours * 80;
      case CustomerTier.ENTERPRISE:
        return Math.max(2_000, hours * 60);
      default:
        return assertNever(this.tier);
    }
  }

  // (b) żądanie operatora to tylko nazwa - kolejkowanie, log i cofanie znają ją osobno
  submit(action: string): void {
    if (!LegacyReleaseConsole.ACTIONS.has(action)) {
      throw new IllegalArgumentError('unknown action: ' + action);
    }
    this.queue.push(action);
  }

  runQueued(): void {
    let action = this.queue.shift();
    while (action !== undefined) {
      switch (action) {
        case 'scale-up':
          this.instances++;
          break;
        case 'scale-down':
          if (this.instances === 1) {
            throw new IllegalStateError('cannot scale below one instance');
          }
          this.instances--;
          break;
        case 'maintenance':
          this.maintenance = !this.maintenance;
          break;
        default:
          throw new IllegalArgumentError('unknown action: ' + action);
      }
      this.history.push(action);
      this.entries.push(action + ' -> ' + this.cluster());
      action = this.queue.shift();
    }
  }

  undoLast(): void {
    const action = this.history.pop();
    if (action === undefined) {
      throw new IllegalStateError('nothing to undo');
    }
    switch (action) {
      case 'scale-up':
        this.instances--;
        break;
      case 'scale-down':
        this.instances++;
        break;
      case 'maintenance':
        this.maintenance = !this.maintenance;
        break;
      default:
        throw new IllegalArgumentError('unknown action: ' + action);
    }
    this.entries.push('undo ' + action + ' -> ' + this.cluster());
  }

  log(): readonly string[] {
    return Object.freeze([...this.entries]);
  }

  // (c) zachowanie zależy od statusu, który zmienia się w czasie
  status(): ReleaseStatus {
    return this.currentStatus;
  }

  advance(): void {
    switch (this.currentStatus) {
      case ReleaseStatus.PLANNED:
        this.currentStatus = ReleaseStatus.DEPLOYING;
        return;
      case ReleaseStatus.DEPLOYING:
        this.currentStatus = ReleaseStatus.LIVE;
        return;
      case ReleaseStatus.LIVE:
      case ReleaseStatus.ROLLED_BACK:
        throw new IllegalStateError('cannot advance release in state ' + this.currentStatus);
      default:
        assertNever(this.currentStatus);
    }
  }

  rollback(): void {
    if (this.currentStatus === ReleaseStatus.DEPLOYING || this.currentStatus === ReleaseStatus.LIVE) {
      this.currentStatus = ReleaseStatus.ROLLED_BACK;
      return;
    }
    throw new IllegalStateError('cannot roll back release in state ' + this.currentStatus);
  }

  banner(): string {
    switch (this.currentStatus) {
      case ReleaseStatus.PLANNED:
        return 'release planned';
      case ReleaseStatus.DEPLOYING:
        return 'deployment in progress';
      case ReleaseStatus.LIVE:
        return 'release is live';
      case ReleaseStatus.ROLLED_BACK:
        return 'release rolled back';
      default:
        return assertNever(this.currentStatus);
    }
  }

  private cluster(): string {
    return 'instances=' + this.instances + ', maintenance=' + (this.maintenance ? 'on' : 'off');
  }
}
