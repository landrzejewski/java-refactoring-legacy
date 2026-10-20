using Training.Workshop.Shared;

namespace Training.Workshop.M3.S03BehaviourVector.Step3;

/// <summary>Seam dla efektu ubocznego "obciążenie karty".</summary>
public delegate bool PaymentGateway(string card, Money amount);
