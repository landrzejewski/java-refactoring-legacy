namespace Training.Workshop.M6.S09Observer;

/// <summary>Port programu lojalnościowego - istniejąca integracja, której nie zmieniamy.</summary>
public interface ILoyaltyProgram
{
    void AddPoints(string email, int points);
}
