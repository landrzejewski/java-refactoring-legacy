namespace Training.Workshop.M7.S13GodClass.Step3;

/// <summary>
/// Krok 3: jedyny właściciel dostępu do rezerwacji. Pod spodem nadal globalny słownik LegacyDb
/// (zachowana kolejność wstawiania i współdzielenie) - zmiana magazynu to osobny krok.
/// </summary>
internal sealed class BookingRepository
{
    internal string NextId()
    {
        return "B" + (LegacyDb.Sequence++);
    }

    internal void Save(Booking booking)
    {
        LegacyDb.Bookings[booking.Id] = booking;
    }

    /// <summary>Zwraca null, gdy rezerwacji nie ma - jak LegacyDb.Bookings.TryGetValue(); inny kontrakt to osobna zmiana.</summary>
    internal Booking? Find(string id)
    {
        return LegacyDb.Bookings.GetValueOrDefault(id);
    }

    internal IReadOnlyCollection<Booking> All()
    {
        return LegacyDb.Bookings.Values;
    }
}
