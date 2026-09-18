import { Director } from './Director.js';
import { PostgresSqlConnectionUrlBuilder } from './PostgresSqlConnectionUrlBuilder.js';

export function run(): void {
  const builder = new PostgresSqlConnectionUrlBuilder()
    .host('localhost')
    .database('test');
  const director = new Director(builder);
  //------------------------------------------------------------------
  director.run();
}
