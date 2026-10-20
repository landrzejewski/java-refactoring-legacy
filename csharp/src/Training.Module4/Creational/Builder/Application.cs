namespace Training.Module4.Creational.Builder;

public class Application
{
    public static void Run()
    {
        var builder = new PostgresSqlConnectionUrlBuilder()
                .Host("localhost")
                .Database("test");
        var director = new Director(builder);
        //------------------------------------------------------------------
        director.Run();
    }
}
