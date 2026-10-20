using Training.Module4.Creational.AbstractFactory.Ftp;

namespace Training.Module4.Creational.AbstractFactory;

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
