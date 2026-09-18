namespace Training.Patterns.Creational.AbstractFactory.Http;

public class HttpsConnection : ISecuredConnection
{
    public int Port => 443;

    public string EncryptionAlgorithm => "AES";
}
