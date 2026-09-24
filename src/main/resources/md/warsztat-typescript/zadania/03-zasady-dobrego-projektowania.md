# Moduł 3. Zasady dobrego projektowania - warsztat CineLegacy (TypeScript): zadania

Każda scena to mały fragment systemu kina CineLegacy z jednym problemem projektowym. Pracujesz wyłącznie w katalogu `start` danej sceny (`typescript/src/workshop/m3/sNN_.../start`). Po każdej zmianie uruchamiaj test sceny - ma pozostać zielony (o ile zadanie nie mówi inaczej). Nie zaglądaj do katalogów `step1`, `step2`, ... przed końcem - to rozwiązania wzorcowe. Po zadaniu możesz porównać swoje rozwiązanie: `scripts/warsztat.sh --lang ts diff m3/sNN 0 N`, a przywrócić stan wyjściowy: `scripts/warsztat.sh --lang ts reset m3/sNN`.

Zasady:

- Małe kroki: jedna refaktoryzacja, jeden przebieg testu. Używaj refaktoryzacji VS Code tam, gdzie się da (⌃⇧R Refactor..., F2 Rename Symbol, ⌘. Quick Fix, przenoszenie plików w eksploratorze z "Update imports"). Ruchów, których VS Code nie ma (Inline Method, Extract Class, Change Signature, Introduce Parameter), dokonuj ręcznie, a kompilator (`npx tsc --noEmit` albo czerwone podkreślenia) niech wskaże miejsca do poprawy.
- Testy wchodzą do `start` tylko przez publiczne API wymienione w zadaniu (albo przez punkt wejścia: funkcję `confirmReservation()` w `Main.ts`, `reservationController()` w `CinemaApplication.ts`, `describe()` w `CinemaApp.ts` lub klasę `TicketDesk`). Jeśli zmieniasz konstruktory, popraw ten punkt wejścia.
- Kwoty to `Decimal` z decimal.js (w s12 - `Money`). Liczby domenowe: 2D 25.00, 3D 32.00, IMAX 40.00; zniżki STUDENT 25%, SENIOR 30%, CHILD 40%; seans przed 12:00 -5.00; VIP +10.00; okulary 3D +3.00; opłata rezerwacyjna online 2.00 za bilet; zwrot 100% / 50% / 0% minus 3.00.

## Scena s01. DRY - jedna wiedza, dwie reprezentacje
**Katalog:** `typescript/src/workshop/m3/s01_dryknowledge/start` · **Test:** `scripts/warsztat.sh --lang ts test m3/s01`

**Zasada:** DRY dotyczy wiedzy, nie tekstu: jedna reguła biznesowa powinna mieć jedną autorytatywną reprezentację, nawet jeśli jej kopie są napisane zupełnie inaczej.

**Zadanie:**
1. Znajdź w `BoxOffice` wiedzę zapisaną dwa razy (choć tekst jest różny).
2. Doprowadź do tego, żeby reguła ceny biletu miała jedną, nazwaną reprezentację.
3. Nie zmieniaj sygnatur `sell` i `refund`.

**Podpowiedź:** najpierw nadaj obu kopiom nazwy (⌃⇧R > Extract to method in class), potem zastąp jedną drugą i pozwól testowi rozstrzygnąć, czy liczyły to samo.
**Gotowe, gdy:** test zielony, zmiana zniżki studenckiej wymaga edycji jednego miejsca, a reguły zwrotu nie trafiły do klasy z ceną biletu.

## Scena s02. Podobieństwo to nie duplikacja
**Katalog:** `typescript/src/workshop/m3/s02_similarity/start` · **Test:** `scripts/warsztat.sh --lang ts test m3/s02`

**Zasada:** Podobny kod nie oznacza tej samej wiedzy - o duplikacji decyduje to, czy reguły zmieniają się razem i dla tego samego właściciela. Scalenie niezależnych reguł tworzy fałszywą zależność.

**Zadanie:**
1. Oceń klasę `ServiceFee`: jaką wiedzę reprezentuje i kto jest właścicielem każdej z reguł?
2. Rozdziel fałszywie scaloną wiedzę tak, żeby zmiana opłaty rezerwacyjnej nie mogła wpłynąć na zwroty.
3. Nadaj kwotom nazwy w języku domeny.

**Podpowiedź:** tymczasowe powtórzenie kodu jest dozwolone - zacznij od Inline Method (w VS Code ręcznie: wklej ciało `ServiceFee.of` w oba wywołania). Jeśli kompilator zgłosi porównanie stałych enuma bez części wspólnej (TS2367), to znak, że warunek jest martwy - uprość go.
**Gotowe, gdy:** test zielony, pliku `ServiceFee.ts` (ani enuma `Kind`) nie ma, każda kwota jest stałą u swojego właściciela.

## Scena s03. Fałszywa abstrakcja z flagami
**Katalog:** `typescript/src/workshop/m3/s03_falseabstraction/start` · **Test:** `scripts/warsztat.sh --lang ts test m3/s03`

**Zasada:** Fałszywa abstrakcja łączy konteksty bez wspólnej wiedzy i rośnie przez kolejne flagi. Naprawia się ją, przenosząc kod z powrotem do kontekstów pod ochroną testów i wydzielając tylko potwierdzoną wiedzę.

**Zadanie:**
1. Usuń metodę `Pricing.price` sterowaną flagami `boolean`.
2. Zostaw w `TicketCounter` i `PassCounter` tylko tę logikę, która ich dotyczy.
3. Zdecyduj, czy jest między nimi jakakolwiek wspólna wiedza do wydzielenia.

**Podpowiedź:** Inline Method (ręcznie, stałe argumenty jako lokalne stałe z jawnym typem, np. `const pass: boolean = true`), potem ⌃⇧R > Inline variable i ręczne uproszczenie martwych gałęzi.
**Gotowe, gdy:** test zielony, żadna metoda nie przyjmuje flagi `boolean`, której wywołujący nie potrzebuje, a `PassCounter` nie wie nic o formatach.

## Scena s04. DRY w testach i niezależna wyrocznia
**Katalog:** `typescript/src/workshop/m3/s04_drytests/start` · **Test:** `scripts/warsztat.sh --lang ts test m3/s04`

**Zasada:** W testach warto współdzielić fabryki danych i helpery, ale oczekiwana wartość musi pochodzić z niezależnego źródła, a nie z algorytmu, który test sprawdza. Czytelne, nazwane przypadki (DAMP) są ważniejsze niż maksymalne DRY.

**Zadanie:**
1. Przepisz `TicketPriceSpecs` tak, żeby każdy przypadek miał czytelną nazwę i jawne dane wejściowe (bez szyfru `'3D S 11'`).
2. Spraw, żeby komunikat o błędzie mówił, który przypadek, jaka oczekiwana i jaka faktyczna cena.
3. Usuń liczenie oczekiwanej ceny z taryfy - wpisz wartości policzone ręcznie z regulaminu.

**Podpowiedź:** sprawdź się w teście: dla taryfy ze zniżką studencką 20% zamiast 25% (`Tariff.standard().withDiscount('STUDENT', 20)`) Twoja specyfikacja musi zgłosić dokładnie jeden problem.
**Gotowe, gdy:** test zielony, `run` dla poprawnej taryfy zwraca pustą tablicę, a dla błędnej zgłasza przypadek studenta na porannym seansie 3D.

## Scena s05. KISS - złożoność wprowadzona kontra istotna
**Katalog:** `typescript/src/workshop/m3/s05_kiss/start` · **Test:** `scripts/warsztat.sh --lang ts test m3/s05`

**Zasada:** KISS to najmniej złożone rozwiązanie, które poprawnie spełnia wymagania: usuwamy złożoność wprowadzoną, a istotną zostawiamy i nazywamy. Krótszy kod nie zawsze jest prostszy.

**Zadanie:**
1. Usuń dynamiczne wywołanie metody po nazwie (`Reflect.get`), które wybiera rodzaj miejsc po napisie.
2. Zastąp wyrażenie regularne i tablicę numerów rzędów prostszym algorytmem.
3. Zachowaj istotne reguły: co jest wolnym miejscem i od którego rzędu zaczyna się VIP.

**Podpowiedź:** przed uproszczeniem przeczytaj przypadki testowe - jest wśród nich rząd z miejscem zablokowanym i przejściem.
**Gotowe, gdy:** test zielony, VS Code nie wyszarza żadnej metody jako nieużywanej, a w klasie nie ma `Reflect`, rzutowań ani wyrażeń regularnych.

## Scena s06. YAGNI - silnik reguł dla dwóch reguł
**Katalog:** `typescript/src/workshop/m3/s06_yagni/start` · **Test:** `scripts/warsztat.sh --lang ts test m3/s06`

**Zasada:** YAGNI mówi, żeby nie budować mechanizmów dla przewidywanych wymagań, tylko dla aktualnych. Nie zabrania testów, refaktoryzacji ani czytelnych nazw.

**Zadanie:**
1. Przejdź filtr decyzyjny: jakie aktualne wymaganie uzasadnia rejestr, konfigurację napisem, priorytety i kontekst `Map`?
2. Uprość `TicketPricer` w kilku krokach, zachowując publiczne `new TicketPricer()` i `price(quote: TicketQuote)`.
3. Zapisz w komentarzu dokumentacyjnym (`/** ... */`), czego YAGNI nie zabrania i co świadomie zostawiasz.

**Podpowiedź:** zacznij od elementu, którego nikt nie używa (konfiguracja napisem w parametrze domyślnym konstruktora), potem typowane dane, na końcu same reguły.
**Gotowe, gdy:** test zielony, zostało najwyżej kilka nazwanych metod w jednej klasie, a literówka w nazwie reguły nie może przejść kompilacji.

## Scena s07. SRP - raport dla dwóch aktorów
**Katalog:** `typescript/src/workshop/m3/s07_srp/start` · **Test:** `scripts/warsztat.sh --lang ts test m3/s07`

**Zasada:** SRP oznacza jeden aktor i jeden powód zmiany dla modułu, a nie "jedną rzecz" czy jedną metodę na klasę.

**Zadanie:**
1. Wskaż aktorów, dla których `DailyReport` się zmienia, i helper, który ich łączy.
2. Podziel raport tak, żeby zmiana definicji "hitu dnia" nie mogła zmienić kwot księgowości.
3. Zachowaj dokument co do znaku.

**Podpowiedź:** komentarze w kodzie już nazywają aktorów. Wspólny helper rozdziel, zanim wydzielisz klasy.
**Gotowe, gdy:** test zielony, każda sekcja jest w klasie swojego aktora (we własnym pliku), a `DailyReport` tylko składa wynik.

## Scena s08. OCP na wybranej osi - formaty seansu
**Katalog:** `typescript/src/workshop/m3/s08_ocp/start` · **Test:** `scripts/warsztat.sh --lang ts test m3/s08`

**Zasada:** OCP to możliwość rozszerzenia wybranego zachowania bez modyfikowania stabilnej części kodu. Zamyka się kod na jedną realną oś zmian, a nie każdy `switch` jest naruszeniem.

**Zadanie:**
1. Usuń rozproszenie wiedzy o formatach po kilku `switch` na napisach.
2. Doprowadź do stanu, w którym nowy format wymaga zmiany w jednym miejscu, a `ScreeningOffer` się nie zmienia.
3. Dodaj format 4DX: cena 45.00, wymaga okularów 3D, etykieta "4DX - ruchome fotele".

**Podpowiedź:** formaty to dane, nie algorytmy. Enum TS nie ma pól, więc rozważ klasę `Format` z prywatnym konstruktorem i instancjami `static readonly` zamiast hierarchii klas. Etap pośredni: enum i `switch` z `assertNever` w `default`.
**Gotowe, gdy:** test zielony, 4DX kosztuje 48.00 bez własnych okularów i 45.00 z własnymi, a dodanie 4DX nie zmieniło `ScreeningOffer`.

## Scena s09. LSP i test kontraktowy
**Katalog:** `typescript/src/workshop/m3/s09_lsp/start` · **Test:** `scripts/warsztat.sh --lang ts test m3/s09`

**Zasada:** LSP wymaga, by podtyp spełniał kontrakt typu bazowego: nie wzmacnia warunków wstępnych i nie osłabia końcowych. Sprawdza się to testem kontraktowym uruchamianym dla każdej implementacji.

**Zadanie:**
1. Przeczytaj kontrakt `Hall.reserve` i test `S09ContractTest`. Który podtyp go łamie i dlaczego?
2. Przebuduj typy tak, żeby sala archiwalna spełniała wszystkie kontrakty, które obiecuje.
3. Zachowaj działanie kasy (`BoxOffice.sell`) i raportu (`OccupancyReport.describe`).

**Podpowiedź:** raport potrzebuje tylko odczytu planu miejsc. Wydziel interfejs, potem zastąp dziedziczenie delegacją (Quick Fix ⌘. > Implement interface wygeneruje szkielety metod).
**Gotowe, gdy:** test zielony, `ReadOnlyHall` nie rzuca `UnsupportedOperationError`, a próba przekazania jej do kasy nie kompiluje się.

## Scena s10. ISP z perspektywy klienta
**Katalog:** `typescript/src/workshop/m3/s10_isp/start` · **Test:** `scripts/warsztat.sh --lang ts test m3/s10`

**Zasada:** ISP mówi, że klient powinien zależeć tylko od metod, których używa, więc interfejsy projektuje się według ról klientów, a nie według implementacji.

**Zadanie:**
1. Dla każdego klienta (`CashDesk`, `RevenueReport`, `ScheduleBoard`) wypisz metody `CinemaAdminService`, których naprawdę używa.
2. Wprowadź interfejsy ról i przepnij klientów.
3. Zdecyduj, co zrobić z grubym interfejsem i z metodą zmiany ceny.

**Podpowiedź:** policz, ile metod musiałby mieć fake (literał obiektu) w teście kasy przed i po zmianie.
**Gotowe, gdy:** test zielony, każdy klient zależy tylko od swojej roli, a fake kasy ma dwie metody.

## Scena s11. DIP - kierunek zależności kontra przepływ sterowania
**Katalog:** `typescript/src/workshop/m3/s11_dip/start` · **Test:** `scripts/warsztat.sh --lang ts test m3/s11`

**Zasada:** DIP dotyczy kierunku zależności źródłowych: szczegóły mają zależeć od polityki, a nie odwrotnie. Wstrzyknięcie konkretnej klasy przez konstruktor to dependency injection, ale jeszcze nie DIP.

**Zadanie:**
1. Narysuj strzałki importów i wywołań między `app` i `infra`.
2. Wstrzyknij zależność przez konstruktor i sprawdź, czy kierunek importu się zmienił.
3. Wprowadź port nazwany potrzebą przypadku użycia i adapter w `infra`. Graf składaj w funkcji `confirmReservation()` w `Main.ts`.

**Podpowiedź:** nie wydzielaj interfejsu z `SmtpMailSender` - najpierw wydziel metodę, która mówi, czego potrzebuje polityka.
**Gotowe, gdy:** test zielony, żaden plik w `app` nie importuje z `infra` (także przez `import type`), a przypadek użycia da się przetestować literałem obiektu zamiast SMTP.

## Scena s12. Clean Architecture - use case, dane na granicy, composition root
**Katalog:** `typescript/src/workshop/m3/s12_cleanarchitecture/start` · **Test:** `scripts/warsztat.sh --lang ts test m3/s12`

**Zasada:** Polityka aplikacji nie powinna zależeć od mechanizmów (HTTP, baza, broker): potrzeby wnętrza wyrażają porty, szczegóły realizują adaptery, a graf składa jedno miejsce bez reguł biznesowych.

**Zadanie:**
1. Wydziel przypadek użycia rezerwacji z kontrolera; wejście i wyjście jako proste klasy danych z polami `readonly`.
2. Zastąp bezpośrednie użycie `RowStore` i `Outbox` portami zdefiniowanymi przez przypadek użycia i adapterami.
3. Rozdziel kod na katalogi `app` i `adapter`, a składanie grafu przenieś do funkcji `reservationController` w `CinemaApplication.ts`.
4. Napisz test przypadku użycia (vitest) na adapterach w pamięci, w tym przypadek: zapis się nie udał, więc nie ma powiadomienia.

**Podpowiedź:** kolejność "zapis, potem powiadomienie" to decyzja protokołu - niech będzie jawna w przypadku użycia. Pliki przenoś w eksploratorze VS Code z "Update imports".
**Gotowe, gdy:** test zielony, `app` nie importuje `adapter` ani `RowStore`/`Outbox`, kontroler tylko tłumaczy żądanie i odpowiedź, a `CinemaApplication.ts` nie zawiera reguł biznesowych.

## Scena s13. Automatyczna ochrona granicy i diagnostyka spójności
**Katalog:** `typescript/src/workshop/m3/s13_boundarycheck/start` · **Test:** `scripts/warsztat.sh --lang ts test m3/s13`

**Zasada:** Spójność (elementy zmieniają się z jednego powodu) i kierunek zależności (domena nie zna technologii) to dwa niezależne wymiary. Reguły granic warto sprawdzać automatycznie, a metryki traktować jako sygnał, nie wyrocznię.

**Zadanie:**
1. Uruchom test z logami (`scripts/warsztat.sh --lang ts test m3/s13`) i odczytaj z konsoli naruszenia granicy oraz wynik LCOM4 dla `ScreeningService`.
2. Popraw spójność `ScreeningService`, a potem kierunek zależności, tak żeby katalog `domain` nie importował z `adapter` ani z `sql`.
3. Zaproponuj silniejszą bramkę niż skan importów (dependency-cruiser, eslint z `no-restricted-imports` lub eslint-plugin-boundaries, pakiety workspace, project references) i porównaj koszt.

**Podpowiedź:** spójność i kierunek zależności to dwa osobne kroki - sprawdź oba wskaźniki po każdym z nich.
**Gotowe, gdy:** test zielony, lista naruszeń dla `start/domain` jest pusta, a LCOM4 każdej klasy wynosi 1.

## Scena s14. Wzorzec jako decyzja odwracalna
**Katalog:** `typescript/src/workshop/m3/s14_reversiblepattern/start` · **Test:** `scripts/warsztat.sh --lang ts test m3/s14`

**Zasada:** Wzorzec projektowy to odpowiedź na konkretne siły, a nie kod na zapas - gdy siły znikają, wzorzec można usunąć. Refaktoryzacja od wzorca jest równie poprawna jak do wzorca.

**Zadanie:**
1. Wprowadź Strategy dla dwóch istniejących modeli rozliczeń; dla modelu festiwalowego napisz Adapter do `FestivalTariffClient`.
2. Umowy festiwalowe wygasły: usuń ten wariant (to świadoma zmiana zachowania).
3. Oceń, czy wzorzec nadal jest potrzebny, i jeśli nie - usuń go.

**Podpowiedź:** po każdym kroku test równoważności dla modelu procentowego musi być zielony. Sygnały nadmiaru wzorca: jedna implementacja, mapa z jednym wpisem.
**Gotowe, gdy:** test zielony, rozliczenie procentowe daje te same kwoty co na początku, model FESTIVAL jest odrzucany, a w kodzie nie zostały nieużywane typy ani pliki.

## Scena s15. Inwarianty w modelu domeny
**Katalog:** `typescript/src/workshop/m3/s15_invariants/start` · **Test:** `scripts/warsztat.sh --lang ts test m3/s15`

**Zasada:** Inwariant to warunek, który obiekt domeny spełnia zawsze, niezależnie od ścieżki, którą powstał. Jego właścicielem powinien być sam model, a nie każdy serwis z osobna.

**Zadanie:**
1. Sprawdź, ile ścieżek tworzy `Reservation` i które z nich walidują dane.
2. Uczyń rezerwację niezmienną.
3. Przenieś reguły poprawności do modelu tak, żeby nie dało się utworzyć złej rezerwacji żadną ścieżką.

**Podpowiedź:** pola `readonly` jako właściwości parametrów konstruktora (`constructor(readonly email: string, ...)`) dają niezmienny obiekt tworzony jednym wywołaniem, a ciało konstruktora to naturalne miejsce na strażników.
**Gotowe, gdy:** test zielony, `BookingService` nie zawiera już walidacji, a import wiersza `jan-kino.pl;0;-5.00` kończy się wyjątkiem.

## Scena s16. Sprzężenie protokołu - ukryta kolejność wywołań
**Katalog:** `typescript/src/workshop/m3/s16_temporalcoupling/start` · **Test:** `scripts/warsztat.sh --lang ts test m3/s16`

**Zasada:** Sprzężenie czasowe to ukryte wymaganie kolejności wywołań, którego nie widać w typach. Usuwa się je, zamieniając stan i kolejność na jawne, kompletne dane wejściowe.

**Zadanie:**
1. Zakomentuj jedno z wywołań `select...` w `TicketDesk` i uruchom test - zobacz, gdzie i jak objawia się błąd. Cofnij zmianę.
2. Usuń z `TicketPrinter` stan i wymagany protokół wywołań.
3. Zadbaj, żeby niekompletne dane biletu były odrzucane w chwili ich utworzenia - także `null` spoza systemu typów (użyj `requireNonNull` z `src/shared/requireNonNull.ts`).

**Podpowiedź:** Change Signature, potem Introduce Parameter Object - w VS Code oba ręcznie, kompilator wskaże wywołania.
**Gotowe, gdy:** test zielony, `TicketPrinter` nie ma pól, a nie da się wywołać `print` bez kompletu danych.
