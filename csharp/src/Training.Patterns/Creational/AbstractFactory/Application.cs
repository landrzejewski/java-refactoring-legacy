using Training.Patterns.Creational.AbstractFactory.Ftp;

namespace Training.Patterns.Creational.AbstractFactory;

public class Application
{
    public static void Run()
    {
        var connectionFactory = new FtpConnectionFactory();
        var service = new Service(connectionFactory);
        //------------------------------------------------------------------
        service.Run();
    }
}
