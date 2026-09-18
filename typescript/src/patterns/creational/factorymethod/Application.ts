import { Service } from './Service.js';
import { TestIdGeneratorFactory } from './TestIdGeneratorFactory.js';

export function run(): void {
  const idGeneratorFactory = new TestIdGeneratorFactory();
  const service = new Service(idGeneratorFactory);
  //------------------------------------------------------------------
  service.run();
}
