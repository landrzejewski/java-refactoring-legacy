namespace Training.Workshop.M5.S15Compatibility;

/// <summary>Stabilny kontrakt sceny: atrybut kolumny eksportu (czytany refleksją, jak w ORM/JSON/CSV).</summary>
[AttributeUsage(AttributeTargets.Method)]
public sealed class ColumnAttribute(string value) : Attribute
{
    public string Value => value;
}
