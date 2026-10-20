namespace Training.Module4.Creational.AbstractFactory.Ftp;

public class FtpConnectionFactory : IConnectionAbstractFactory
{
    public IConnection CreateConnection() => new FtpConnection();

    public ISecuredConnection CreateSecuredConnection() => new SftpConnection();
}
