# Moduł 2. Prewencja - warsztat CineLegacy (C#): zadania

Każda scena to mały fragment systemu kina CineLegacy z jednym problemem z zakresu prewencji. Pracujesz wyłącznie w przestrzeni nazw `Start` danej sceny (katalog `csharp/src/Training.Workshop/M2/SNN.../Start`). Po każdej zmianie uruchamiaj test sceny (`scripts/warsztat.sh --lang cs test m2/sNN` albo w Rider z gutteru przy klasie testu lub z okna Unit Tests) - ma pozostać zielony, o ile zadanie nie mówi inaczej. Część testów opisuje stan startowy (np. „start narusza ADR”, „start nie przechodzi bramki”) - gdy naprawisz `Start`, zmienią kolor na czerwony, a to znaczy, że zadanie jest zrobione. Testy czytające pliki źródłowe znajdują je same (pomocnik `SourceFiles`), więc katalog, z którego je uruchamiasz, nie ma znaczenia. Nie zaglądaj do katalogów `Step1`, `Step2`, ... przed końcem - to rozwiązania wzorcowe. Po zadaniu możesz porównać swoje rozwiązanie: `scripts/warsztat.sh --lang cs diff m2/sNN 0 N`, a przywrócić stan wyjściowy: `scripts/warsztat.sh --lang cs reset m2/sNN`.

Kod leży w `csharp/src/Training.Workshop/M2/SNN.../Start`, testy w `csharp/tests/Training.Workshop.Tests/M2/SNN.../`.

Zasady:

- Małe kroki: jedna refaktoryzacja, jeden przebieg testu. Używaj automatycznych refaktoryzacji Rider tam, gdzie się da (⌃T Refactor This, ⌥⌘M Extract Method, ⌥⌘N Inline, ⌘F6 Change Signature, ⌘⌦ Safe Delete, F6 Move).
- Testy wchodzą do `Start` tylko przez publiczne API wymienione w zadaniu (albo przez klasę-punkt wejścia: `Main`, `CinemaApplication`, `CinemaApp`). Jeśli zmieniasz konstruktory, popraw tę klasę.
- Projekt ma `TreatWarningsAsErrors`, więc każde nowe ostrzeżenie kompilatora zatrzyma build. W s03 i s04 kod z ostrzeżeniami jest celowy - patrz podpowiedzi.
- Liczby domenowe: 2D 25.00, 3D 32.00, IMAX 40.00; zniżki STUDENT 25%, SENIOR 30%, CHILD 40%; seans przed 12:00 -5.00; VIP +10.00. Kwoty to `decimal` albo `Money`.
- Sceny są ułożone w kolejności tematów modułu: szybka informacja zwrotna (s03, s04), decyzje (s01, s02), projekt (s07-s10), prewencja codzienna (s05, s06).

## Scena s03. Bramka kompilatora - nullable i ostrzeżenia jako błędy
**Namespace:** `Training.Workshop.M2.S03CompilerGate.Start` · **Test:** `scripts/warsztat.sh --lang cs test m2/s03`

**Zasada:** Bramka kompilatora (tu: Roslyn z włączonym nullable, wszystkimi falami ostrzeżeń i ostrzeżeniami jako błędami) zamienia ostrzeżenia w błędy builda. Ostrzeżenia usuwa się zmianą kodu, po jednej przyczynie na krok, a tłumienie jest wyjątkiem wąskim i uzasadnionym.

**Zadanie:**
1. Sprawdź, jakie identyfikatory ostrzeżeń (CS...) zgłasza `CompilerGate` dla `Start`.
2. Usuwaj je po jednej przyczynie na krok, uruchamiając test po każdym kroku.
3. Zachowaj zachowanie raportu, w tym to, że IMAX ma też dźwięk Dolby.

**Podpowiedź:** nie używaj `#pragma warning disable`, `[SuppressMessage]` ani nie dopisuj reguł do `.editorconfig` - każde ostrzeżenie da się tu usunąć zmianą kodu. Plik `M2/S03CompilerGate/.editorconfig` wycisza te ostrzeżenia tylko w buildzie projektu; `CompilerGate` go nie czyta.
**Gotowe, gdy:** `CompilerGate.Check` dla `Start` zwraca zero ostrzeżeń i `Passed == true`, a test równoważności raportu jest zielony.

## Scena s04. Minimalna bramka jakości jako kod
**Namespace:** `Training.Workshop.M2.S04QualityGate.Start` · **Test:** `scripts/warsztat.sh --lang cs test m2/s04`

**Zasada:** Minimalna bramka jakości to kilka niezależnych, wykonywalnych sprawdzeń z deterministycznym wynikiem, który wskazuje miejsce problemu. Bramka, która nic nie sprawdza albo daje fałszywe alarmy, jest gorsza niż jej brak.

**Zadanie:**
1. Zamień listę kontrolną z komentarza `QualityGate` na wykonywalne sprawdzenia, po jednym na krok.
2. Każdy wynik ma wskazywać plik i linię albo metodę.
3. Czysta próbka (`Sample/Clean`) musi przejść bramkę bez żadnego wyniku.

**Podpowiedź:** zacznij od najtańszego sprawdzenia (skan tekstu źródeł), kompilator i uruchamianie testów zostaw na koniec. Próbki i ich testy nie wchodzą do kompilacji projektu - kompilację i uruchomienie testów musi zrobić sama bramka (Roslyn w pamięci).
**Gotowe, gdy:** bramka zgłasza wszystkie problemy próbki `Sample/Dirty`, zatrzymuje przypadek z nieprzechodzącym testem (`Sample/Broken`) i przepuszcza `Sample/Clean`.

## Scena s01. YAGNI - silnik reguł dla dwóch reguł
**Namespace:** `Training.Workshop.M2.S01Yagni.Start` · **Test:** `scripts/warsztat.sh --lang cs test m2/s01`

**Zasada:** YAGNI mówi, żeby nie budować mechanizmów dla przewidywanych wymagań, tylko dla aktualnych. Nie zabrania testów, refaktoryzacji ani czytelnych nazw.

**Zadanie:**
1. Przejdź filtr decyzyjny: jakie aktualne wymaganie uzasadnia rejestr, konfigurację napisem, priorytety i kontekst `IDictionary<string, object>`?
2. Uprość `TicketPricer` w kilku krokach, zachowując publiczne `new TicketPricer()` i `Price(TicketQuote)`.
3. Zapisz w komentarzu XML (`/// <summary>`), czego YAGNI nie zabrania i co świadomie zostawiasz.

**Podpowiedź:** zacznij od elementu, którego nikt nie używa (konfiguracja napisem), potem typowane dane, na końcu same reguły.
**Gotowe, gdy:** test zielony, zostało najwyżej kilka nazwanych metod w jednej klasie, a literówka w nazwie reguły nie może przejść kompilacji.

## Scena s02. ADR i wykonywalny model decyzji
**Namespace:** `Training.Workshop.M2.S02Adr.Start` · **Test:** `scripts/warsztat.sh --lang cs test m2/s02`

**Zasada:** ADR zapisuje trwałą decyzję architektoniczną z kontekstem i konsekwencjami; zaakceptowanego ADR się nie edytuje, tylko zastępuje nowym. Reguły z ADR można sprawdzać testem, żeby build pilnował decyzji.

**Zadanie:**
1. Przeczytaj `ADR-0007-cennik-jako-czysty-modul.md` (katalog sceny `M2/S02Adr`) i sprawdź w teście, które reguły narusza `Start`.
2. Usuń zależność przestrzeni nazw `Pricing` od `Notification` bez zmiany wysyłanych maili.
3. Zamień kwoty w przestrzeni nazw `Pricing` na `Money`.

**Podpowiedź:** cennik nie musi wysyłać maila - wystarczy, że powie, czy rabat grupowy został przyznany.
**Gotowe, gdy:** `ArchitectureRules.Violations` dla `Start` zwraca pustą listę, a odpowiedzi i maile są takie same jak przed zmianą.

## Scena s07. SRP - raport dla dwóch aktorów
**Namespace:** `Training.Workshop.M2.S07Srp.Start` · **Test:** `scripts/warsztat.sh --lang cs test m2/s07`

**Zasada:** SRP oznacza jeden aktor i jeden powód zmiany dla modułu, a nie "jedną rzecz" czy jedną metodę na klasę.

**Zadanie:**
1. Wskaż aktorów, dla których `DailyReport` się zmienia, i helper, który ich łączy.
2. Podziel raport tak, żeby zmiana definicji "hitu dnia" nie mogła zmienić kwot księgowości.
3. Zachowaj dokument co do znaku.

**Podpowiedź:** komentarze w kodzie już nazywają aktorów. Wspólny helper rozdziel, zanim wydzielisz klasy.
**Gotowe, gdy:** test zielony, każda sekcja jest w klasie swojego aktora, a `DailyReport` tylko składa wynik.

## Scena s08. DIP - kierunek zależności kontra przepływ sterowania
**Namespace:** `Training.Workshop.M2.S08Dip.Start` · **Test:** `scripts/warsztat.sh --lang cs test m2/s08`

**Zasada:** DIP dotyczy kierunku zależności źródłowych: szczegóły mają zależeć od polityki, a nie odwrotnie. Wstrzyknięcie konkretnej klasy przez konstruktor to dependency injection, ale jeszcze nie DIP.

**Zadanie:**
1. Narysuj strzałki zależności (`using`) i wywołań między `App` i `Infra`.
2. Wstrzyknij zależność przez konstruktor i sprawdź, czy kierunek zależności się zmienił.
3. Wprowadź port nazwany potrzebą przypadku użycia i adapter w `Infra`. Graf składaj w statycznej klasie `Main`.

**Podpowiedź:** nie rób Extract Interface z `SmtpMailSender` - najpierw wydziel metodę, która mówi, czego potrzebuje polityka.
**Gotowe, gdy:** test zielony, żaden plik w `App` nie ma `using ...Infra`, a przypadek użycia da się przetestować ręcznym fake'iem portu zamiast SMTP.

## Scena s09. Clean Architecture - use case, dane na granicy, composition root
**Namespace:** `Training.Workshop.M2.S09CleanArchitecture.Start` · **Test:** `scripts/warsztat.sh --lang cs test m2/s09`

**Zasada:** Polityka aplikacji nie powinna zależeć od mechanizmów (HTTP, baza, broker): potrzeby wnętrza wyrażają porty, szczegóły realizują adaptery, a graf składa jedno miejsce bez reguł biznesowych.

**Zadanie:**
1. Wydziel przypadek użycia rezerwacji z kontrolera; wejście i wyjście jako rekordy.
2. Zastąp bezpośrednie użycie `RowStore` i `Outbox` portami zdefiniowanymi przez przypadek użycia i adapterami.
3. Rozdziel kod na przestrzenie nazw `App` i `Adapter`, a składanie grafu przenieś do `CinemaApplication`.
4. Napisz test przypadku użycia (xUnit) na adapterach w pamięci, w tym przypadek: zapis się nie udał, więc nie ma powiadomienia.

**Podpowiedź:** kolejność "zapis, potem powiadomienie" to decyzja protokołu - niech będzie jawna w przypadku użycia.
**Gotowe, gdy:** test zielony, `App` nie używa `Adapter` ani `RowStore`/`Outbox`, kontroler tylko tłumaczy żądanie i odpowiedź, a `CinemaApplication` nie zawiera reguł biznesowych.

## Scena s10. Automatyczna ochrona granicy i diagnostyka spójności
**Namespace:** `Training.Workshop.M2.S10BoundaryCheck.Start` · **Test:** `scripts/warsztat.sh --lang cs test m2/s10`

**Zasada:** Spójność (elementy zmieniają się z jednego powodu) i kierunek zależności (domena nie zna technologii) to dwa niezależne wymiary. Reguły granic warto sprawdzać automatycznie, a metryki traktować jako sygnał, nie wyrocznię.

**Zadanie:**
1. Uruchom test `S10ArchitectureTest` w Rider i odczytaj z wyjścia testu naruszenia granicy oraz wynik LCOM4 dla `ScreeningService`.
2. Popraw spójność `ScreeningService`, a potem kierunek zależności, tak żeby przestrzeń nazw `Domain` nie używała `Adapter` ani `System.Data`.
3. Zaproponuj silniejszą bramkę niż skan dyrektyw `using` (np. NetArchTest / ArchUnitNET, osobny projekt domeny bez referencji do adapterów, analizator Roslyn) i porównaj koszt.

**Podpowiedź:** spójność i kierunek zależności to dwa osobne kroki - sprawdź oba wskaźniki po każdym z nich.
**Gotowe, gdy:** test zielony, lista naruszeń dla `Start/Domain` jest pusta, a LCOM4 każdej klasy wynosi 1.

## Scena s05. Boy Scout Rule - mała poprawa i jej nadużycie
**Namespace:** `Training.Workshop.M2.S05BoyScout.Start` · **Test:** `scripts/warsztat.sh --lang cs test m2/s05`

**Zasada:** Boy Scout Rule: dotykany kod zostaw w nieco lepszym stanie, ale poprawa ma być mała, lokalna i bez zmiany zachowania. Zmiana tego, co widzi użytkownik, to osobna decyzja, a nie sprzątanie.

**Zadanie:**
1. Popraw czytelność `TicketPrinter.Print`: nazwy, sklejanie listy miejsc, powtórzenia.
2. Nie zmieniaj niczego, co widzi klient: kolejności miejsc, wielkości liter, linii z telefonem, formatu kwoty i daty.
3. Zapisz osobno pomysły, które zmieniłyby zachowanie - to zadania na inny commit.

**Podpowiedź:** jeśli test równoważności zrobi się czerwony, twoje "sprzątanie" zmieniło zachowanie. Sprawdź przypadek z miejscami A9 i A10.
**Gotowe, gdy:** test równoważności jest zielony, diff dotyczy tylko jednej metody i da się go przejrzeć w minutę.

## Scena s06. Seria małych zmian gotowych do przeglądu
**Namespace:** `Training.Workshop.M2.S06ReviewableSeries.Start` · **Test:** `scripts/warsztat.sh --lang cs test m2/s06`

**Zasada:** Dobry zestaw zmian ma jedną intencję, własny dowód i zielony build. Refaktoryzacji nie łączy się ze zmianą zachowania - najpierw ułatw zmianę, potem zrób łatwą zmianę.

**Zadanie:**
1. Dodaj regułę "Tani wtorek": bilet NORMAL -20% ceny bazowej we wtorek.
2. Podziel pracę na co najmniej trzy kroki (commity): refaktoryzacje bez zmiany zachowania osobno, nowa reguła osobno.
3. Do każdego kroku napisz jednozdaniowy opis: intencja i dowód.

**Podpowiedź:** najpierw spraw, żeby wybór zniżki miał dostęp do daty seansu - bez zmiany zachowania. Potem nowa reguła to kilka linii.
**Gotowe, gdy:** po krokach refaktoryzacyjnych test równoważności jest zielony, a po ostatnim 2D NORMAL we wtorek wieczorem kosztuje 20.00 i zmieniły się wyłącznie bilety NORMAL we wtorek.
