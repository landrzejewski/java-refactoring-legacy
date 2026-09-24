using System.Data.SqlTypes;
using System.Globalization;

namespace Training.Workshop.M3.S13BoundaryCheck.Step1.Adapter;

/// <summary>Adapter bazy: wiersz tabeli seansów (typy SQL z System.Data.SqlTypes).</summary>
public sealed record ScreeningRow(string Table, string Title, SqlDateTime Start)
{
    /// <summary>SqlDateTime.ToString zależy od bieżącej kultury - opis wiersza w stałym formacie.</summary>
    public override string ToString() => string.Create(CultureInfo.InvariantCulture,
        $"ScreeningRow {{ Table = {Table}, Title = {Title}, Start = {Start.Value:yyyy-MM-dd HH:mm:ss} }}");
}
