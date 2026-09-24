import { IllegalArgumentError } from '../../../../shared/errors.js';
import { Combo } from './Combo.js';
import type { MenuComponent } from './MenuComponent.js';
import { Product } from './Product.js';

/** Start: klient typuje wszystko jako MenuComponent, bo add() jest wszędzie. */
export class ComboCatalog {
  find(code: string): MenuComponent {
    switch (code) {
      case 'family': return this.family();
      case 'duo': return this.duo();
      case 'nachos': return new Product('Nachos', '14.00');
      default: throw new IllegalArgumentError(`unknown combo: ${code}`);
    }
  }

  private family(): MenuComponent {
    const combo: MenuComponent = new Combo('Zestaw Rodzinny');
    combo.add(new Product('Popcorn XL', '24.00'));
    const drinks: MenuComponent = new Combo('Napoje');
    drinks.add(new Product('Cola', '9.00'));
    drinks.add(new Product('Cola', '9.00'));
    drinks.add(new Product('Woda', '7.00'));
    combo.add(drinks);
    return combo;
  }

  private duo(): MenuComponent {
    const combo: MenuComponent = new Combo('Zestaw Duo');
    combo.add(new Product('Popcorn L', '18.00'));
    combo.add(new Product('Cola', '9.00'));
    combo.add(new Product('Cola', '9.00'));
    return combo;
  }
}
