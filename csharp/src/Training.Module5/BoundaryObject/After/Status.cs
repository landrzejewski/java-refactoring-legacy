namespace Training.Module5.BoundaryObject.After;

// Java nests this enum as BuildReport.Status; C# forbids a nested type with the
// same name as the Status property, so it lives next to the record.
public enum Status
{
    Success,
    Failed,
    Running
}
