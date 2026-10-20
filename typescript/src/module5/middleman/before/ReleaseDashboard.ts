import { requireNonNull } from '../../../shared/requireNonNull.js';
import type { ReleaseService } from './ReleaseService.js';

export class ReleaseDashboard {
  private readonly releaseService: ReleaseService;

  constructor(releaseService: ReleaseService) {
    this.releaseService = requireNonNull(
      releaseService, 'releaseService must not be null');
  }

  render(deploymentId: string): string {
    return deploymentId + ' -> ' + this.releaseService.statusOf(deploymentId);
  }
}
