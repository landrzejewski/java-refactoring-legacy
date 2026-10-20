namespace Training.Workshop.M5.S31AntiCorruptionLayer.Step3;

/// <summary>
/// Krok 3: port domeny - skąd kino bierze licencje, opisane w języku kina
/// (brak = jawnie zadeklarowany null w typie <c>FilmLicense?</c>, który kompilator każe obsłużyć).
/// </summary>
public interface IFilmLicenses
{
    FilmLicense? Find(string filmCode);
}
