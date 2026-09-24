using System.Runtime.Serialization;

namespace Training.Workshop.M5.S16SerializationProxy.Step1;

/// <summary>
/// Krok 1: Extract Superclass + Pull Up Field (title, seat) - poprawne dla kodu, groźne dla danych.
/// Pola przeszły na poziom Ticket, który DataContractSerializer czyta PRZED polami podklasy:
/// w starym XML "title" stoi po "studentId", więc przy odczycie zostaje po cichu pominięte.
/// </summary>
[DataContract(Name = "Ticket", Namespace = "urn:cinelegacy:tickets")]
public abstract class Ticket
{
    [DataMember(Name = "title")]
    private readonly string _title;

    [DataMember(Name = "seat")]
    private readonly string _seat;

    protected Ticket(string title, string seat)
    {
        _title = title;
        _seat = seat;
    }

    public string Title => _title;

    public string Seat => _seat;
}
