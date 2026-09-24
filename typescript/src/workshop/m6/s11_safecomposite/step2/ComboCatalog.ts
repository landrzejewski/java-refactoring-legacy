import { IllegalArgumentError } from '../../../../shared/errors.js';
import { Combo } from './Combo.js';
import type { MenuComponent } from './MenuComponent.js';
import { Product } from './Product.js';

/** Krok 2: drzewo zapisane deklaratywnie - kształt kodu to kształt zestawu. */
export class ComboCatalog {
  find(code: string): MenuComponent {
    switch (code) {
      case 'family': return Combo.of('Zestaw Rodzinny',
        new Product('Popcorn XL', '24.00'),
        Combo.of('Napoje',
          new Product('Cola', '9.00'),
          new Product('Cola', '9.00'),
          new Product('Woda', '7.00')));
      case 'duo': return Combo.of('Zestaw Duo',
        new Product('Popcorn L', '18.00'),
        new Product('Cola', '9.00'),
        new Product('Cola', '9.00'));
      case 'nachos': return new Product('Nachos', '14.00');
      default: throw new IllegalArgumentError(`unknown combo: ${code}`);
    }
  }
}
