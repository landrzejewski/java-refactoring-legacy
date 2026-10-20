import { requireNonNull } from '../../../shared/requireNonNull.js';
import type { ReleasePublishingFacade } from './ReleasePublishingFacade.js';

export class ReleaseScheduler {
  private readonly publishing: ReleasePublishingFacade;

  constructor(publishing: ReleasePublishingFacade) {
    this.publishing = requireNonNull(publishing, 'publishing');
  }

  publishNightly(releaseIds: readonly string[]): string[] {
    const published: string[] = [];
    for (const releaseId of releaseIds) {
      published.push(this.publishing.publish(releaseId, 'staging'));
    }
    return published;
  }
}
