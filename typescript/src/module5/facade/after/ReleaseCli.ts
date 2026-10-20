import { requireNonNull } from '../../../shared/requireNonNull.js';
import type { ReleasePublishingFacade } from './ReleasePublishingFacade.js';

export class ReleaseCli {
  private readonly publishing: ReleasePublishingFacade;

  constructor(publishing: ReleasePublishingFacade) {
    this.publishing = requireNonNull(publishing, 'publishing');
  }

  publish(releaseId: string, environment: string): string {
    return this.publishing.publish(releaseId, environment);
  }
}
