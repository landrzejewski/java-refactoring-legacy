package pl.training.examples.behavioral.iterator;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Agregat z własnym iteratorem. Klient przechodzi po utworach przez
 * {@link Iterator} i nie wie, że w środku jest tablica.
 */
public class Playlist implements Iterable<Song> {

    private Song[] songs = new Song[2];
    private int size;

    public void add(Song song) {
        if (size == songs.length) {
            songs = Arrays.copyOf(songs, songs.length * 2);
        }
        songs[size++] = song;
    }

    @Override
    public Iterator<Song> iterator() {
        return new PlaylistIterator(0, 1);
    }

    /**
     * Drugi sposób przejścia po tym samym agregacie - bez kopiowania danych.
     */
    public Iterable<Song> reversed() {
        return () -> new PlaylistIterator(size - 1, -1);
    }

    private class PlaylistIterator implements Iterator<Song> {

        private int cursor;
        private final int step;

        private PlaylistIterator(int start, int step) {
            this.cursor = start;
            this.step = step;
        }

        @Override
        public boolean hasNext() {
            return cursor >= 0 && cursor < size;
        }

        @Override
        public Song next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            var song = songs[cursor];
            cursor += step;
            return song;
        }

    }

}
