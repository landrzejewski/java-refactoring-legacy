import { IllegalArgumentError } from '../../../../shared/errors.js';
import { Film } from './Film.js';
import { Marathon } from './Marathon.js';
import type { ProgramItem } from './ProgramItem.js';
import { ShortsBlock } from './ShortsBlock.js';

/** Krok 1: bez zmian - klient budujący programy wydarzeń. */
export class ProgramCatalog {
  find(code: string): ProgramItem {
    switch (code) {
      case 'marathon': {
        const marathon = new Marathon('Diuna');
        marathon.add(new Film('Diuna', 155));
        marathon.add(new Film('Diuna: Czesc druga', 166));
        return marathon;
      }
      case 'shorts': return this.shorts();
      case 'night': {
        const night = new Marathon('Noc kina');
        night.add(this.shorts());
        night.add(new Film('Amator', 120));
        return night;
      }
      case 'empty': return new Marathon('Pusty');
      default: throw new IllegalArgumentError(`unknown program: ${code}`);
    }
  }

  private shorts(): ShortsBlock {
    const block = new ShortsBlock('Krotkie metraze');
    block.add(new Film('Kot', 12));
    block.add(new Film('Pies', 9));
    block.add(new Film('Ryba', 15));
    return block;
  }
}
