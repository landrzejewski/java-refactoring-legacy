import { requireNonNull } from '../../../shared/requireNonNull.js';
import type { ReleaseListener } from './ReleaseListener.js';
import type { ReleasePublished } from './ReleasePublished.js';
import type { Subscription } from './Subscription.js';

export class ReleasePublisher {
  // Java używa CopyOnWriteArrayList; tutaj publish iteruje po migawce listy,
  // więc wypisanie w trakcie publikacji działa dopiero od następnej publikacji.
  // JS jest jednowątkowy — AtomicBoolean zastępuje zwykła flaga.
  private readonly listeners: Registration[] = [];

  subscribe(listener: ReleaseListener): Subscription {
    const registration = new Registration(requireNonNull(listener, 'listener'));
    this.listeners.push(registration);
    let active = true;
    return {
      close: () => {
        if (active) {
          active = false;
          const index = this.listeners.indexOf(registration);
          if (index >= 0) {
            this.listeners.splice(index, 1);
          }
        }
      },
    };
  }

  publish(event: ReleasePublished): void {
    requireNonNull(event, 'event');
    for (const registration of [...this.listeners]) {
      registration.notifyListener(event);
    }
  }
}

// Osobny obiekt rejestracji: ten sam listener zapisany dwa razy to dwie niezależne subskrypcje.
class Registration {
  constructor(private readonly listener: ReleaseListener) {}

  notifyListener(event: ReleasePublished): void {
    this.listener(event);
  }
}
