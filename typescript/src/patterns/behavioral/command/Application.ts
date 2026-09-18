import { ConnectToServer } from './ConnectToServer.js';
import { Invoker } from './Invoker.js';
import { PrintTime } from './PrintTime.js';

export function run(): void {
  const invoker = new Invoker();
  invoker.invoke(new PrintTime());
  invoker.invoke(new ConnectToServer());
}
