import { EventsBus } from './EventsBus.js';
import { Logger } from './Logger.js';
import { ServerEvent } from './ServerEvent.js';

export function run(): void {
  const subject = new EventsBus();
  const observer = new Logger();
  subject.addConsumer(observer);
  subject.publish(new ServerEvent('Started'));
}
