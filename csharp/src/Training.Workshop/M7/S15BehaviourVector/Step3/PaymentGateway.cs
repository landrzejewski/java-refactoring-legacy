using Training.Workshop.Shared;

namespace Training.Workshop.M7.S15BehaviourVector.Step3;

/// <summary>Seam dla efektu ubocznego "obciążenie karty".</summary>
public delegate bool PaymentGateway(string card, Money amount);
