using System.Runtime.Serialization;

namespace Training.Workshop.M5.S16SerializationProxy.Step1;

/// <summary>
/// Krok 1: ta sama nazwa i przestrzeń kontraktu danych (odpowiednik tego samego serialVersionUID = 1L),
/// więc odczyt starych danych NIE rzuci wyjątku - title po cichu będzie null. Stały kontrakt nie migruje stanu.
/// </summary>
[DataContract(Name = "StudentTicket", Namespace = "urn:cinelegacy:tickets")]
public sealed class StudentTicket : Ticket
{
    [DataMember(Name = "studentId")]
    private readonly string _studentId;

    public StudentTicket(string title, string seat, string studentId) : base(title, seat)
    {
        _studentId = studentId;
    }

    public string Describe()
    {
        return Title + " " + Seat + " (legitymacja " + _studentId + ")";
    }
}
