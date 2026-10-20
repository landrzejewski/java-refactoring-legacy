namespace Training.Module4.Creational.AbstractFactory.Http;

public class HttpsConnection : ISecuredConnection
{
    public int Port => 443;

    public string EncryptionAlgorithm => "AES";
}
