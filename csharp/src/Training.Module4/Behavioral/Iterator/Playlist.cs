using System.Collections;

namespace Training.Module4.Behavioral.Iterator;

/// <summary>
/// Agregat z własnym iteratorem. Klient przechodzi po utworach przez
/// <see cref="IEnumerator{T}"/> i nie wie, że w środku jest tablica.
/// </summary>
public class Playlist : IEnumerable<Song>
{
    private Song[] songs = new Song[2];
    private int size;

    public void Add(Song song)
    {
        if (size == songs.Length)
        {
            Array.Resize(ref songs, songs.Length * 2);
        }
        songs[size++] = song;
    }

    public IEnumerator<Song> GetEnumerator() => new PlaylistIterator(this, 0, 1);

    IEnumerator IEnumerable.GetEnumerator() => GetEnumerator();

    /// <summary>
    /// Drugi sposób przejścia po tym samym agregacie - bez kopiowania danych.
    /// </summary>
    public IEnumerable<Song> Reversed() => new ReversedView(this);

    private sealed class ReversedView(Playlist playlist) : IEnumerable<Song>
    {
        public IEnumerator<Song> GetEnumerator() => new PlaylistIterator(playlist, playlist.size - 1, -1);

        IEnumerator IEnumerable.GetEnumerator() => GetEnumerator();
    }

    // Java: Iterator (hasNext/next) -> IEnumerator (MoveNext/Current)
    private sealed class PlaylistIterator : IEnumerator<Song>
    {
        private readonly Playlist playlist;
        private readonly int start;
        private readonly int step;
        private int cursor;
        private Song? current;

        internal PlaylistIterator(Playlist playlist, int start, int step)
        {
            this.playlist = playlist;
            this.start = start;
            this.step = step;
            cursor = start;
        }

        public Song Current => current ?? throw new InvalidOperationException("Enumeration has not started or has already finished.");

        object IEnumerator.Current => Current;

        public bool MoveNext()
        {
            if (cursor < 0 || cursor >= playlist.size)
            {
                current = null;
                return false;
            }
            current = playlist.songs[cursor];
            cursor += step;
            return true;
        }

        public void Reset()
        {
            cursor = start;
            current = null;
        }

        public void Dispose()
        {
        }
    }
}
