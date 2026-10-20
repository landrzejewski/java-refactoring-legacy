import type { Song } from './Song.js';

/**
 * Aggregate with its own iterator. The client walks the songs through an Iterator
 * and does not know there is an array inside.
 */
export class Playlist implements Iterable<Song> {
  private songs: (Song | undefined)[] = new Array<Song | undefined>(2).fill(undefined);
  private size = 0;

  add(song: Song): void {
    if (this.size === this.songs.length) {
      // Java: Arrays.copyOf(songs, songs.length * 2)
      this.songs = [...this.songs, ...new Array<Song | undefined>(this.songs.length).fill(undefined)];
    }
    this.songs[this.size++] = song;
  }

  // Java: iterator() from Iterable<Song>
  [Symbol.iterator](): Iterator<Song> {
    return this.iteratorFrom(0, 1);
  }

  /**
   * A second way of walking the same aggregate - without copying the data.
   */
  reversed(): Iterable<Song> {
    return { [Symbol.iterator]: () => this.iteratorFrom(this.size - 1, -1) };
  }

  private iteratorFrom(start: number, step: number): Iterator<Song> {
    return new PlaylistIterator(
      (index) => this.songs[index],
      () => this.size,
      start,
      step,
    );
  }
}

/**
 * Java: private inner class PlaylistIterator implements Iterator<Song>. TypeScript has no
 * inner classes, so the (non-exported) iterator reads the aggregate's live state through
 * two accessors. Deliberately an explicit iterator object rather than a generator.
 */
class PlaylistIterator implements Iterator<Song> {
  constructor(
    private readonly songAt: (index: number) => Song | undefined,
    private readonly size: () => number,
    private cursor: number,
    private readonly step: number,
  ) {}

  // Java: next() throws NoSuchElementException when !hasNext(); the JavaScript
  // iterator protocol reports exhaustion with { done: true } instead.
  next(): IteratorResult<Song> {
    if (!this.hasNext()) {
      return { done: true, value: undefined };
    }
    const song = this.songAt(this.cursor) as Song;
    this.cursor += this.step;
    return { done: false, value: song };
  }

  private hasNext(): boolean {
    return this.cursor >= 0 && this.cursor < this.size();
  }
}
