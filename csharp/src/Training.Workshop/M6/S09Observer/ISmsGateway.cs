namespace Training.Workshop.M6.S09Observer;

/// <summary>Port bramki SMS - istniejąca integracja, której nie zmieniamy.</summary>
public interface ISmsGateway
{
    void Send(string phone, string text);
}
