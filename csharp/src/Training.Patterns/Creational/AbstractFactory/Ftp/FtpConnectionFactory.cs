namespace Training.Patterns.Creational.AbstractFactory.Ftp;

public class FtpConnectionFactory : IConnectionAbstractFactory
{
    public IConnection CreateConnection() => new FtpConnection();

    public ISecuredConnection CreateSecuredConnection() => new SftpConnection();
}
