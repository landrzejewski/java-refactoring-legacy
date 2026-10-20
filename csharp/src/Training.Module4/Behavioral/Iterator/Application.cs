namespace Training.Module4.Behavioral.Iterator;

public class Application
{
    public static void Run()
    {
        var playlist = new Playlist();
        playlist.Add(new Song("Intro", 90));
        playlist.Add(new Song("Main Theme", 225));
        playlist.Add(new Song("Outro", 130));
        //------------------------------------------------------
        using var iterator = playlist.GetEnumerator();
        while (iterator.MoveNext())
        {
            var song = iterator.Current;
            Console.WriteLine("Playing: " + song.Title + " (" + song.Seconds + "s)");
        }
        foreach (var song in playlist.Reversed())
        {
            Console.WriteLine("Reversed: " + song.Title);
        }
    }
}
