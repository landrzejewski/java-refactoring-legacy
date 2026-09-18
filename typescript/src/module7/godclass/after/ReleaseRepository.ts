import type { PublishedRelease } from '../PublishedRelease.js';

export interface ReleaseRepository {
  existsById(releaseId: string): boolean;

  save(release: PublishedRelease): void;

  findAll(): readonly PublishedRelease[];
}
