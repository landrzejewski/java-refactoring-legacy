package pl.training.module4.creational.abstractfactory;

import pl.training.module4.creational.abstractfactory.ftp.FtpConnectionFactory;

public class Application {

    public static void main(String[] args) {
        var connectionFactory = new FtpConnectionFactory();
        var service = new Service(connectionFactory);
        //------------------------------------------------------------------
        service.run();
    }

}
