namespace Training.Workshop.M4.S08MoveField.Step1;

/// <summary>Sala kinowa. Układ sali (od którego rzędu VIP) mieszka... gdzie indziej.</summary>
public sealed class Hall
{
    public Hall(string name)
    {
        Name = name;
    }

    public string Name { get; }
}
