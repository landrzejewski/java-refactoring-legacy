import { IllegalArgumentError } from '../../../shared/errors.js';
import { requireNonNull } from '../../../shared/requireNonNull.js';
import type { CustomerTier } from '../CustomerTier.js';
import type { ReleaseStatus } from '../ReleaseStatus.js';
import { CommandConsole } from './CommandConsole.js';
import { OperatorCommand } from './OperatorCommand.js';
import { ReleaseLifecycle } from './ReleaseLifecycle.js';
import { SupportCostPolicy } from './SupportCostPolicy.js';

// Odpowiednik package-info.java pakietu variability.after:
// Command, Strategy czy State? Te same switch-e z LegacyReleaseConsole rozdzielone według
// tego, kto wybiera wariant i kiedy:
// - Strategy (SupportCostPolicy) - wariant wybiera klient raz (np. przy tworzeniu obiektu)
//   i nie zmienia go w trakcie życia obiektu; rodzina algorytmów jest stabilna i wymienna.
// - Command (OperatorCommand) - żądanie staje się obiektem, bo trzeba je kolejkować,
//   logować i cofać; wariant wybiera nadawca żądania przy każdym wywołaniu.
// - State (ReleaseLifecycle) - nikt z zewnątrz nie wybiera wariantu; obiekt sam zmienia
//   zachowanie, przechodząc między stanami.

/**
 * Konsola wydania złożona z trzech mechanizmów, po jednym na każdą oś zmienności:
 * Strategy (taryfa wybrana raz przez klienta), Command (żądania operatora jako obiekty)
 * i State (cykl życia, który sam zmienia zachowanie).
 */
export class ReleaseConsole {
  private readonly costPolicy: SupportCostPolicy;
  private readonly commands = new CommandConsole();
  private readonly lifecycle = new ReleaseLifecycle();

  constructor(costPolicy: SupportCostPolicy) {
    this.costPolicy = requireNonNull(costPolicy, 'costPolicy');
  }

  static forTier(tier: CustomerTier): ReleaseConsole {
    return new ReleaseConsole(SupportCostPolicy.forTier(tier));
  }

  supportCost(hours: number): number {
    if (hours < 0) {
      throw new IllegalArgumentError('hours must not be negative');
    }
    return this.costPolicy.cost(hours);
  }

  submit(action: string): void {
    this.commands.submit(OperatorCommand.byName(action));
  }

  runQueued(): void {
    this.commands.runQueued();
  }

  undoLast(): void {
    this.commands.undoLast();
  }

  log(): readonly string[] {
    return this.commands.log();
  }

  status(): ReleaseStatus {
    return this.lifecycle.status();
  }

  advance(): void {
    this.lifecycle.advance();
  }

  rollback(): void {
    this.lifecycle.rollback();
  }

  banner(): string {
    return this.lifecycle.banner();
  }
}
