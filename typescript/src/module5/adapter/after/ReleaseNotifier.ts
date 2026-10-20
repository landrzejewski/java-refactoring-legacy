import type { ReleaseMessage } from './ReleaseMessage.js';

export interface ReleaseNotifier {
  send(message: ReleaseMessage): string;
}
