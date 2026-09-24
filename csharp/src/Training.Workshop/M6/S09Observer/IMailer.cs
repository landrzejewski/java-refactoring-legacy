namespace Training.Workshop.M6.S09Observer;

/// <summary>Port poczty - istniejąca integracja, której nie zmieniamy.</summary>
public interface IMailer
{
    void Send(string to, string text);
}
