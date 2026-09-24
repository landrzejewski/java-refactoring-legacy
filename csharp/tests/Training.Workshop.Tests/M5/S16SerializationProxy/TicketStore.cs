using System.Runtime.Serialization;
using System.Text;
using System.Text.Json;
using System.Xml;

namespace Training.Workshop.Tests.M5.S16SerializationProxy;

/// <summary>
/// Magazyn biletów w testach (cache, kolejka, sesja): zapisuje obiekt formatem, który deklaruje jego typ -
/// [DataContract] to XML z DataContractSerializer (stary format), pozostałe to JSON z System.Text.Json
/// (nowy format z proxy serializacji). Odpowiednik ObjectOutputStream/ObjectInputStream w testach Javy.
/// </summary>
internal static class TicketStore
{
    public static string Save(object value)
    {
        if (!IsDataContract(value.GetType()))
        {
            return JsonSerializer.Serialize(value, value.GetType());
        }
        var xml = new StringBuilder();
        using (var writer = XmlWriter.Create(xml, new XmlWriterSettings { OmitXmlDeclaration = true }))
        {
            new DataContractSerializer(value.GetType()).WriteObject(writer, value);
        }
        return xml.ToString();
    }

    public static T Load<T>(string data)
    {
        if (!IsDataContract(typeof(T)))
        {
            return JsonSerializer.Deserialize<T>(data) ?? throw new JsonException("pusty zapis");
        }
        using var reader = XmlReader.Create(new StringReader(data));
        return (T)new DataContractSerializer(typeof(T)).ReadObject(reader)!;
    }

    public static T RoundTrip<T>(T value)
        where T : notnull
    {
        return Load<T>(Save(value));
    }

    private static bool IsDataContract(Type type) => type.IsDefined(typeof(DataContractAttribute), inherit: false);
}
