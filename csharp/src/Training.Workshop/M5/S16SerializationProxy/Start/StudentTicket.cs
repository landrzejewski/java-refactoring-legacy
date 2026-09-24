using System.Runtime.Serialization;

namespace Training.Workshop.M5.S16SerializationProxy.Start;

/// <summary>
/// Start: bilet zapisywany przez DataContractSerializer (np. cache, kolejka, stary serwis WCF) - odpowiednik
/// serializacji Javy: serializer czyta prywatne pola i nie woła konstruktora. Kontrakt ma stałą nazwę
/// i przestrzeń nazw (odpowiednik serialVersionUID = 1L). Postać XML to pola KAŻDEGO poziomu hierarchii
/// po kolei (najpierw baza, potem podklasa) - hierarchia jest częścią formatu danych.
/// </summary>
[DataContract(Name = "StudentTicket", Namespace = "urn:cinelegacy:tickets")]
public sealed class StudentTicket
{
    [DataMember(Name = "title")]
    private readonly string _title;

    [DataMember(Name = "seat")]
    private readonly string _seat;

    [DataMember(Name = "studentId")]
    private readonly string _studentId;

    public StudentTicket(string title, string seat, string studentId)
    {
        _title = title;
        _seat = seat;
        _studentId = studentId;
    }

    public string Describe()
    {
        return _title + " " + _seat + " (legitymacja " + _studentId + ")";
    }
}
