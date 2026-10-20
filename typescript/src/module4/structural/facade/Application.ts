import { Facade } from './Facade.js';
import { FirstService } from './FirstService.js';
import { SecondService } from './SecondService.js';

export function run(): void {
  const library = new Facade(new FirstService(), new SecondService());
  //----------------------------------------------------------------
  library.run();
}
