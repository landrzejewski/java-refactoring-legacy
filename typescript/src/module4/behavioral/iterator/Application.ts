import { Playlist } from './Playlist.js';
import { Song } from './Song.js';

export function run(): void {
  const playlist = new Playlist();
  playlist.add(new Song('Intro', 90));
  playlist.add(new Song('Main Theme', 225));
  playlist.add(new Song('Outro', 130));
  //------------------------------------------------------
  const iterator = playlist[Symbol.iterator]();
  for (let result = iterator.next(); !result.done; result = iterator.next()) { // Java: hasNext()/next()
    const song = result.value;
    console.log(`Playing: ${song.title} (${song.seconds}s)`);
  }
  for (const song of playlist.reversed()) {
    console.log(`Reversed: ${song.title}`);
  }
}
