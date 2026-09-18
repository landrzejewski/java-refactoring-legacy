import type { ReleasePublished } from './ReleasePublished.js';

// Interfejs funkcyjny Javy — w TS po prostu typ funkcji.
export type ReleaseListener = (event: ReleasePublished) => void;
