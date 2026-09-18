namespace Training.Patterns.Creational.AbstractFactory.Ftp;

public class SftpConnection : ISecuredConnection
{
    public int Port => 22;

    public string EncryptionAlgorithm => "AES";
}
