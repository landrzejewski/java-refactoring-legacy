package pl.training.examples.behavioral.iterator;

public class Application {

    public static void main(String[] args) {
        var playlist = new Playlist();
        playlist.add(new Song("Intro", 90));
        playlist.add(new Song("Main Theme", 225));
        playlist.add(new Song("Outro", 130));
        //------------------------------------------------------
        var iterator = playlist.iterator();
        while (iterator.hasNext()) {
            var song = iterator.next();
            System.out.println("Playing: " + song.title() + " (" + song.seconds() + "s)");
        }
        for (var song : playlist.reversed()) {
            System.out.println("Reversed: " + song.title());
        }
    }

}
