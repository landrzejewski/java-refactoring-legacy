import { IllegalArgumentError } from '../../../../shared/errors.js';
import { MorningRule } from './MorningRule.js';
import type { PricingRule } from './PricingRule.js';
import { VipRule } from './VipRule.js';

/** Start: rejestr pluginów konfigurowany napisem "morning,vip" - "na przyszłość". */
export class RuleRegistry {
  private readonly plugins = new Map<string, () => PricingRule>();

  static withDefaults(): RuleRegistry {
    const registry = new RuleRegistry();
    registry.register('morning', () => new MorningRule());
    registry.register('vip', () => new VipRule());
    return registry;
  }

  register(name: string, plugin: () => PricingRule): void {
    this.plugins.set(name, plugin);
  }

  resolve(activeRules: string): readonly PricingRule[] {
    return Object.freeze(activeRules.split(',')
      .map((name) => name.trim())
      .map((name) => this.create(name))
      .sort((a, b) => a.priority() - b.priority()));
  }

  private create(name: string): PricingRule {
    const plugin = this.plugins.get(name);
    if (plugin === undefined) {
      throw new IllegalArgumentError(`brak reguly: ${name}`);
    }
    return plugin();
  }
}
