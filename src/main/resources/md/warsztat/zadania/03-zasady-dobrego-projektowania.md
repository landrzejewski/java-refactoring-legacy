# Moduł 3. Zasady dobrego projektowania - warsztat CineLegacy: zadania

Każda scena to mały fragment systemu kina CineLegacy z jednym problemem projektowym. Pracujesz wyłącznie w pakiecie `start` danej sceny. Po każdej zmianie uruchamiaj test sceny - ma pozostać zielony (o ile zadanie nie mówi inaczej). Nie zaglądaj do katalogów `step1`, `step2`, ... przed końcem - to rozwiązania wzorcowe. Po zadaniu możesz porównać swoje rozwiązanie: `scripts/warsztat.sh diff m3/sNN 0 N`, a przywrócić stan wyjściowy: `scripts/warsztat.sh reset m3/sNN`.

Zasady:

- Małe kroki: jedna refaktoryzacja, jeden przebieg testu. Używaj automatycznych refaktoryzacji IntelliJ tam, gdzie się da.
- Testy wchodzą do `start` tylko przez publiczne API wymienione w zadaniu (albo przez klasę-punkt wejścia: `Main`, `CinemaApplication`, `CinemaApp`, `TicketDesk`). Jeśli zmieniasz konstruktory, popraw tę klasę.
- Liczby domenowe: 2D 25.00, 3D 32.00, IMAX 40.00; zniżki STUDENT 25%, SENIOR 30%, CHILD 40%; seans przed 12:00 -5.00; VIP +10.00; okulary 3D +3.00; opłata rezerwacyjna online 2.00 za bilet; zwrot 100% / 50% / 0% minus 3.00.

## Scena s01. DRY - jedna wiedza, dwie reprezentacje
**Pakiet:** `pl.training.workshop.m3.s01_dryknowledge.start` · **Test:** `scripts/warsztat.sh test m3/s01`
**Zadanie:**
1. Znajdź w `BoxOffice` wiedzę zapisaną dwa razy (choć tekst jest różny).
2. Doprowadź do tego, żeby reguła ceny biletu miała jedną, nazwaną reprezentację.
3. Nie zmieniaj sygnatur `sell` i `refund`.

**Podpowiedź:** najpierw nadaj obu kopiom nazwy (Extract Method), potem zastąp jedną drugą i pozwól testowi rozstrzygnąć, czy liczyły to samo.
**Gotowe, gdy:** test zielony, zmiana zniżki studenckiej wymaga edycji jednego miejsca, a reguły zwrotu nie trafiły do klasy z ceną biletu.

## Scena s02. Podobieństwo to nie duplikacja
**Pakiet:** `pl.training.workshop.m3.s02_similarity.start` · **Test:** `scripts/warsztat.sh test m3/s02`
**Zadanie:**
1. Oceń klasę `ServiceFee`: jaką wiedzę reprezentuje i kto jest właścicielem każdej z reguł?
2. Rozdziel fałszywie scaloną wiedzę tak, żeby zmiana opłaty rezerwacyjnej nie mogła wpłynąć na zwroty.
3. Nadaj kwotom nazwy w języku domeny.

**Podpowiedź:** tymczasowe powtórzenie kodu jest dozwolone - zacznij od Inline Method.
**Gotowe, gdy:** test zielony, `ServiceFee` nie istnieje, każda kwota jest stałą u swojego właściciela.

## Scena s03. Fałszywa abstrakcja z flagami
**Pakiet:** `pl.training.workshop.m3.s03_falseabstraction.start` · **Test:** `scripts/warsztat.sh test m3/s03`
**Zadanie:**
1. Usuń metodę `Pricing.price` sterowaną flagami `boolean`.
2. Zostaw w `TicketCounter` i `PassCounter` tylko tę logikę, która ich dotyczy.
3. Zdecyduj, czy jest między nimi jakakolwiek wspólna wiedza do wydzielenia.

**Podpowiedź:** Inline Method, potem Inline Variable dla stałych argumentów i ⌥⏎ Simplify.
**Gotowe, gdy:** test zielony, żadna metoda nie przyjmuje flagi `boolean`, której wywołujący nie potrzebuje, a `PassCounter` nie wie nic o formatach.

## Scena s04. DRY w testach i niezależna wyrocznia
**Pakiet:** `pl.training.workshop.m3.s04_drytests.start` · **Test:** `scripts/warsztat.sh test m3/s04`
**Zadanie:**
1. Przepisz `TicketPriceSpecs` tak, żeby każdy przypadek miał czytelną nazwę i jawne dane wejściowe (bez szyfru "3D S 11").
2. Spraw, żeby komunikat o błędzie mówił, który przypadek, jaka oczekiwana i jaka faktyczna cena.
3. Usuń liczenie oczekiwanej ceny z taryfy - wpisz wartości policzone ręcznie z regulaminu.

**Podpowiedź:** sprawdź się w teście: dla taryfy ze zniżką studencką 20% zamiast 25% Twoja specyfikacja musi zgłosić dokładnie jeden problem.
**Gotowe, gdy:** test zielony, `run` dla poprawnej taryfy zwraca pustą listę, a dla błędnej zgłasza przypadek studenta na porannym seansie 3D.

## Scena s05. KISS - złożoność wprowadzona kontra istotna
**Pakiet:** `pl.training.workshop.m3.s05_kiss.start` · **Test:** `scripts/warsztat.sh test m3/s05`
**Zadanie:**
1. Usuń refleksję wybierającą rodzaj miejsc po napisie.
2. Zastąp wyrażenie regularne i strumień indeksów prostszym algorytmem.
3. Zachowaj istotne reguły: co jest wolnym miejscem i od którego rzędu zaczyna się VIP.

**Podpowiedź:** przed uproszczeniem przeczytaj przypadki testowe - jest wśród nich rząd z miejscem zablokowanym i przejściem.
**Gotowe, gdy:** test zielony, IDE nie pokazuje nieużywanych metod, a w klasie nie ma `java.lang.reflect` ani `java.util.regex`.

## Scena s06. YAGNI - silnik reguł dla dwóch reguł
**Pakiet:** `pl.training.workshop.m3.s06_yagni.start` · **Test:** `scripts/warsztat.sh test m3/s06`
**Zadanie:**
1. Przejdź filtr decyzyjny: jakie aktualne wymaganie uzasadnia rejestr, konfigurację napisem, priorytety i kontekst `Map`?
2. Uprość `TicketPricer` w kilku krokach, zachowując publiczne `new TicketPricer()` i `price(TicketQuote)`.
3. Zapisz w Javadoc, czego YAGNI nie zabrania i co świadomie zostawiasz.

**Podpowiedź:** zacznij od elementu, którego nikt nie używa (konfiguracja napisem), potem typowane dane, na końcu same reguły.
**Gotowe, gdy:** test zielony, zostało najwyżej kilka nazwanych metod w jednej klasie, a literówka w nazwie reguły nie może przejść kompilacji.

## Scena s07. SRP - raport dla dwóch aktorów
**Pakiet:** `pl.training.workshop.m3.s07_srp.start` · **Test:** `scripts/warsztat.sh test m3/s07`
**Zadanie:**
1. Wskaż aktorów, dla których `DailyReport` się zmienia, i helper, który ich łączy.
2. Podziel raport tak, żeby zmiana definicji "hitu dnia" nie mogła zmienić kwot księgowości.
3. Zachowaj dokument co do znaku.

**Podpowiedź:** komentarze w kodzie już nazywają aktorów. Wspólny helper rozdziel, zanim wydzielisz klasy.
**Gotowe, gdy:** test zielony, każda sekcja jest w klasie swojego aktora, a `DailyReport` tylko składa wynik.

## Scena s08. OCP na wybranej osi - formaty seansu
**Pakiet:** `pl.training.workshop.m3.s08_ocp.start` · **Test:** `scripts/warsztat.sh test m3/s08`
**Zadanie:**
1. Usuń rozproszenie wiedzy o formatach po kilku `switch` na napisach.
2. Doprowadź do stanu, w którym nowy format wymaga zmiany w jednym miejscu, a `ScreeningOffer` się nie zmienia.
3. Dodaj format 4DX: cena 45.00, wymaga okularów 3D, etykieta "4DX - ruchome fotele".

**Podpowiedź:** formaty to dane, nie algorytmy - rozważ enum z polami zamiast hierarchii klas.
**Gotowe, gdy:** test zielony, 4DX kosztuje 48.00 bez własnych okularów i 45.00 z własnymi, a dodanie 4DX nie zmieniło `ScreeningOffer`.

## Scena s09. LSP i test kontraktowy
**Pakiet:** `pl.training.workshop.m3.s09_lsp.start` · **Test:** `scripts/warsztat.sh test m3/s09`
**Zadanie:**
1. Przeczytaj kontrakt `Hall.reserve` i test `S09ContractTest`. Który podtyp go łamie i dlaczego?
2. Przebuduj typy tak, żeby sala archiwalna spełniała wszystkie kontrakty, które obiecuje.
3. Zachowaj działanie kasy (`BoxOffice.sell`) i raportu (`OccupancyReport.describe`).

**Podpowiedź:** raport potrzebuje tylko odczytu planu miejsc. Extract Interface, potem Replace Inheritance with Delegation.
**Gotowe, gdy:** test zielony, `ReadOnlyHall` nie rzuca `UnsupportedOperationException`, a próba przekazania jej do kasy nie kompiluje się.

## Scena s10. ISP z perspektywy klienta
**Pakiet:** `pl.training.workshop.m3.s10_isp.start` · **Test:** `scripts/warsztat.sh test m3/s10`
**Zadanie:**
1. Dla każdego klienta (`CashDesk`, `RevenueReport`, `ScheduleBoard`) wypisz metody `CinemaAdminService`, których naprawdę używa.
2. Wprowadź interfejsy ról i przepnij klientów.
3. Zdecyduj, co zrobić z grubym interfejsem i z metodą zmiany ceny.

**Podpowiedź:** policz, ile metod musiałby mieć fake w teście kasy przed i po zmianie.
**Gotowe, gdy:** test zielony, każdy klient zależy tylko od swojej roli, a fake kasy ma dwie metody.

## Scena s11. DIP - kierunek zależności kontra przepływ sterowania
**Pakiet:** `pl.training.workshop.m3.s11_dip.start` · **Test:** `scripts/warsztat.sh test m3/s11`
**Zadanie:**
1. Narysuj strzałki importów i wywołań między `app` i `infra`.
2. Wstrzyknij zależność przez konstruktor i sprawdź, czy kierunek importu się zmienił.
3. Wprowadź port nazwany potrzebą przypadku użycia i adapter w `infra`. Graf składaj w `Main`.

**Podpowiedź:** nie rób Extract Interface z `SmtpMailSender` - najpierw wydziel metodę, która mówi, czego potrzebuje polityka.
**Gotowe, gdy:** test zielony, żaden plik w `app` nie importuje `infra`, a przypadek użycia da się przetestować lambdą zamiast SMTP.

## Scena s12. Clean Architecture - use case, dane na granicy, composition root
**Pakiet:** `pl.training.workshop.m3.s12_cleanarchitecture.start` · **Test:** `scripts/warsztat.sh test m3/s12`
**Zadanie:**
1. Wydziel przypadek użycia rezerwacji z kontrolera; wejście i wyjście jako rekordy.
2. Zastąp bezpośrednie użycie `RowStore` i `Outbox` portami zdefiniowanymi przez przypadek użycia i adapterami.
3. Rozdziel kod na pakiety `app` i `adapter`, a składanie grafu przenieś do `CinemaApplication`.
4. Napisz test przypadku użycia na adapterach w pamięci, w tym przypadek: zapis się nie udał, więc nie ma powiadomienia.

**Podpowiedź:** kolejność "zapis, potem powiadomienie" to decyzja protokołu - niech będzie jawna w przypadku użycia.
**Gotowe, gdy:** test zielony, `app` nie importuje `adapter` ani `RowStore`/`Outbox`, kontroler tylko tłumaczy żądanie i odpowiedź, a `CinemaApplication` nie zawiera reguł biznesowych.

## Scena s13. Automatyczna ochrona granicy i diagnostyka spójności
**Pakiet:** `pl.training.workshop.m3.s13_boundarycheck.start` · **Test:** `scripts/warsztat.sh test m3/s13`
**Zadanie:**
1. Uruchom test i odczytaj z konsoli naruszenia granicy oraz wynik LCOM4 dla `ScreeningService`.
2. Popraw spójność `ScreeningService`, a potem kierunek zależności, tak żeby pakiet `domain` nie importował `adapter` ani `java.sql`.
3. Zaproponuj silniejszą bramkę niż skan importów i porównaj koszt.

**Podpowiedź:** spójność i kierunek zależności to dwa osobne kroki - sprawdź oba wskaźniki po każdym z nich.
**Gotowe, gdy:** test zielony, lista naruszeń dla `start/domain` jest pusta, a LCOM4 każdej klasy wynosi 1.

## Scena s14. Wzorzec jako decyzja odwracalna
**Pakiet:** `pl.training.workshop.m3.s14_reversiblepattern.start` · **Test:** `scripts/warsztat.sh test m3/s14`
**Zadanie:**
1. Wprowadź Strategy dla dwóch istniejących modeli rozliczeń; dla modelu festiwalowego napisz Adapter do `FestivalTariffClient`.
2. Umowy festiwalowe wygasły: usuń ten wariant (to świadoma zmiana zachowania).
3. Oceń, czy wzorzec nadal jest potrzebny, i jeśli nie - usuń go.

**Podpowiedź:** po każdym kroku test równoważności dla modelu procentowego musi być zielony. Sygnały nadmiaru wzorca: jedna implementacja, mapa z jednym wpisem.
**Gotowe, gdy:** test zielony, rozliczenie procentowe daje te same kwoty co na początku, model FESTIVAL jest odrzucany, a w kodzie nie zostały nieużywane typy.

## Scena s15. Inwarianty w modelu domeny
**Pakiet:** `pl.training.workshop.m3.s15_invariants.start` · **Test:** `scripts/warsztat.sh test m3/s15`
**Zadanie:**
1. Sprawdź, ile ścieżek tworzy `Reservation` i które z nich walidują dane.
2. Uczyń rezerwację niezmienną.
3. Przenieś reguły poprawności do modelu tak, żeby nie dało się utworzyć złej rezerwacji żadną ścieżką.

**Podpowiedź:** IntelliJ potrafi zamienić klasę na record (⌥⏎), a record ma kompaktowy konstruktor.
**Gotowe, gdy:** test zielony, `BookingService` nie zawiera już walidacji, a import wiersza `jan-kino.pl;0;-5.00` kończy się wyjątkiem.

## Scena s16. Sprzężenie protokołu - ukryta kolejność wywołań
**Pakiet:** `pl.training.workshop.m3.s16_temporalcoupling.start` · **Test:** `scripts/warsztat.sh test m3/s16`
**Zadanie:**
1. Zakomentuj jedno z wywołań `select...` w `TicketDesk` i uruchom test - zobacz, gdzie i jak objawia się błąd. Cofnij zmianę.
2. Usuń z `TicketPrinter` stan i wymagany protokół wywołań.
3. Zadbaj, żeby niekompletne dane biletu były odrzucane w chwili ich utworzenia.

**Podpowiedź:** Change Signature, potem Introduce Parameter Object.
**Gotowe, gdy:** test zielony, `TicketPrinter` nie ma pól, a nie da się wywołać `print` bez kompletu danych.
