using System.Text.Json;
using System.Text.Json.Serialization;

namespace Training.Workshop.M5.S16SerializationProxy.Step2;

/// <summary>
/// Krok 2: Serialization Proxy (Effective Java) w wersji .NET - System.Text.Json + DTO. Do zapisu trafia
/// płaski record SerializedForm, a odczyt przechodzi przez publiczny konstruktor. Hierarchia klasy przestaje
/// być formatem danych. Nowy format (JSON zamiast XML kontraktu danych) to świadoma deklaracja nowej wersji
/// (odpowiednik serialVersionUID = 2L): stare dane są odrzucane głośno zamiast po cichu gubić pola -
/// ich migracja to osobne zadanie.
/// </summary>
[JsonConverter(typeof(SerializedFormConverter))]
public sealed class StudentTicket : Ticket
{
    private readonly string _studentId;

    public StudentTicket(string title, string seat, string studentId) : base(title, seat)
    {
        _studentId = studentId;
    }

    public string Describe()
    {
        return Title + " " + Seat + " (legitymacja " + _studentId + ")";
    }

    /// <summary>Płaska postać zapisu - jedyny format danych biletu, niezależny od hierarchii klas.</summary>
    private sealed record SerializedForm(string Title, string Seat, string StudentId);

    /// <summary>
    /// Zapis: bilet zamieniany na SerializedForm (odpowiednik writeReplace). Odczyt: SerializedForm
    /// zamieniany na bilet przez publiczny konstruktor (odpowiednik readResolve) - innej drogi nie ma.
    /// </summary>
    private sealed class SerializedFormConverter : JsonConverter<StudentTicket>
    {
        public override StudentTicket Read(ref Utf8JsonReader reader, Type typeToConvert, JsonSerializerOptions options)
        {
            var form = JsonSerializer.Deserialize<SerializedForm>(ref reader, options)
                ?? throw new JsonException("StudentTicket czytamy tylko przez SerializedForm");
            return new StudentTicket(form.Title, form.Seat, form.StudentId);
        }

        public override void Write(Utf8JsonWriter writer, StudentTicket value, JsonSerializerOptions options)
        {
            JsonSerializer.Serialize(writer, new SerializedForm(value.Title, value.Seat, value._studentId), options);
        }
    }
}
