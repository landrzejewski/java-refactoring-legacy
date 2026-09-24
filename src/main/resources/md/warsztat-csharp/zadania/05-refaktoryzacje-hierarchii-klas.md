# Moduł 5. Refaktoryzacje hierarchii klas - warsztat CineLegacy (C#): zadania

Każda scena to mały fragment systemu kina CineLegacy w namespace `Training.Workshop.M5.SNN....Start` (katalog `csharp/src/Training.Workshop/M5/SNN.../Start`). Pracuj wyłącznie w `Start`, małymi krokami, i po każdym ruchu uruchamiaj test sceny (`scripts/warsztat.sh --lang cs test m5/sNN` albo z gutter/okna Unit Tests w Rider). Test równoważności (`SNNEquivalenceTest`) ma być zielony przez cały czas. W scenach o pułapkach (s08-s11, s13) testy o nazwie `Start...` dokumentują błąd w kodzie wyjściowym - gdy go usuniesz, te testy zrobią się czerwone i to jest oczekiwany efekt. Nie zaglądaj do katalogów `StepN` przed zakończeniem zadania; potem porównaj swoje rozwiązanie z ostatnim krokiem (`scripts/warsztat.sh --lang cs diff m5/sNN 0 N`). Chcesz zacząć od nowa: `scripts/warsztat.sh --lang cs reset m5/sNN`.

Korzystaj z automatycznych refaktoryzacji Rider (Refactor This ⌃T: Pull Members Up, Push Members Down, Extract Superclass, Extract Interface, Extract Class, Encapsulate Field, Inline, Safe Delete, Change Signature; Generate ⌘N: Overriding Members, Delegating Members, Equality Members), a ruchy ręczne rób tak, by po każdym dało się uruchomić test. Pamiętaj, że w C# metody domyślnie nie są wirtualne: nadpisanie wymaga `virtual`/`abstract` w bazie i `override` w podklasie, a metoda bez `virtual` to odpowiednik javowej metody `final`.

## Scena s01. Pull Up Method
**Namespace:** `Training.Workshop.M5.S01PullUpMethod.Start` · **Test:** `scripts/warsztat.sh --lang cs test m5/s01`

**Zasada:** Pull Up Method przenosi metodę na najniższy poziom, na którym jest prawdziwa dla wszystkich potomków. Porównuje się kontrakty metod, a nie ich tekst, a IDE przeniesie tylko metody o identycznych ciałach.

**Zadanie:**
1. Usuń trzy kopie metody `Label()` z podklas `Ticket`, zostawiając jedną implementację.
2. Klient `BoxOffice` ma wywoływać `Label()` przez typ bazowy.
3. Zdecyduj, czy wspólna metoda może być nadpisywana (`virtual` czy nie).

**Podpowiedź:** IDE przeniesie w górę tylko metody o identycznych ciałach, a wspólna metoda potrzebuje czegoś, czego baza jeszcze nie zna.
**Gotowe, gdy:** test zielony, `Label()` jest zadeklarowane tylko w `Ticket`, a podklasy zawierają wyłącznie regułę ceny.

## Scena s02. Pull Up Field
**Namespace:** `Training.Workshop.M5.S02PullUpField.Start` · **Test:** `scripts/warsztat.sh --lang cs test m5/s02`

**Zasada:** Pull Up Field łączy pola tylko przy tym samym znaczeniu, typie, cyklu życia i momencie inicjalizacji. Pole w bazie powinno być prywatne i ustawiane przez konstruktor bazy, a nie `protected`.

**Zadanie:**
1. Przenieś informację o miejscu na sali do `Ticket` jako jedno pole.
2. Pole w bazie ma być `private readonly`, ustawiane przez konstruktor bazy (`base(...)`).
3. Nie przenoś pól, które mają inne znaczenie.

**Podpowiedź:** zanim połączysz pola, porównaj ich nazwę, typ (także `string?` kontra `string`), znaczenie, cykl życia i sposób inicjalizacji. Każda różnica to osobny krok przygotowawczy.
**Gotowe, gdy:** test zielony, żadna podklasa nie deklaruje pola miejsca, nie ma settera właściwości `Seat`, a normalizacja miejsca VIP działa jak wcześniej.

## Scena s03. Push Down Method/Field
**Namespace:** `Training.Workshop.M5.S03PushDown.Start` · **Test:** `scripts/warsztat.sh --lang cs test m5/s03`

**Zasada:** Push Down zawęża zbyt szeroki kontrakt bazy do gałęzi, która naprawdę potrzebuje członka. Sygnałem jest `NotSupportedException` lub wywołanie tylko po sprawdzeniu `is`, a w bibliotece taki ruch łamie stare binaria.

**Zadanie:**
1. Usuń z `Ticket` operację dopłaty VIP i związany z nią stan - ma zostać tylko tam, gdzie ma sens.
2. Usuń override rzucający `NotSupportedException`.
3. Zapisz w komentarzu, co ta zmiana oznaczałaby dla skompilowanych klientów biblioteki (stare assembly).

**Podpowiedź:** kolejność ma znaczenie: klienci, potem zachowanie korzystające z pola, na końcu pole.
**Gotowe, gdy:** test zielony, `Ticket` nie ma `UpgradeToVip()` ani pola VIP, `BoxOffice` nie używa `is`.

## Scena s04. Extract Superclass
**Namespace:** `Training.Workshop.M5.S04ExtractSuperclass.Start` · **Test:** `scripts/warsztat.sh --lang cs test m5/s04`

**Zasada:** Extract Superclass ma sens, gdy klasy są wariantami jednego pojęcia ze wspólnym kontraktem, a nie tylko mają podobny kod. Konstruktory nie są dziedziczone, więc publiczne sygnatury trzeba zachować świadomie.

**Zadanie:**
1. Wydziel wspólną nadklasę dla seansu i wynajmu sali z nazwą opisującą pojęcie domenowe.
2. Dołączaj klasy do hierarchii pojedynczo, z testem po każdej.
3. Usuń trzy kopie warunku kolizji w `HallPlanner`.
4. Zachowaj publiczne konstruktory, fabrykę `Rental(...)` i sygnaturę `Conflicts(...)`.

**Podpowiedź:** zacznij od ⌃T > Extract Superclass na jednej klasie. Algorytm kolizji potrzebuje od podklas tylko nazwy do komunikatu.
**Gotowe, gdy:** test zielony (w tym przypadek "styk 20:00 to nie konflikt"), nadklasa jest abstrakcyjna i ma prywatne pola, `HallPlanner` ma jedną pętlę.

## Scena s05. Extract Subclass
**Namespace:** `Training.Workshop.M5.S05ExtractSubclass.Start` · **Test:** `scripts/warsztat.sh --lang cs test m5/s05`

**Zasada:** Extract Subclass tworzy podklasę dla stabilnego podzbioru instancji z dodatkowym stanem lub zachowaniem. Wariant zmienny w czasie życia obiektu to State lub Strategy, a nie podklasa.

**Zadanie:**
1. Wydziel podklasę dla premier i przenieś do niej stan i zachowanie, które dotyczą tylko premier.
2. Usuń flagę `_premiere` i wszystkie `if (_premiere)`.
3. Klient `Programme` nie może przekazywać `null` ani flagi.

**Podpowiedź:** zacznij od punktów tworzenia obiektów - to one mają wybierać klasę runtime.
**Gotowe, gdy:** test zielony, zwykły seans nie ma pola gościa, a fabryka premiery zwraca obiekt podklasy.

## Scena s06. Extract Interface i metody domyślne
**Namespace:** `Training.Workshop.M5.S06ExtractInterface.Start` · **Test:** `scripts/warsztat.sh --lang cs test m5/s06`

**Zasada:** Extract Interface wydziela rolę potrzebną konkretnym klientom, a nie kopię całego API klasy. Domyślna metoda interfejsu musi być poprawna dla każdej implementacji i korzystać tylko z operacji kontraktu.

**Zadanie:**
1. Wydziel interfejs roli, której potrzebuje koszyk - tylko z operacjami, których `Cart` używa.
2. `Cart` ma mieć jedną listę pozycji i jedno `Add(...)`.
3. Przenieś liczenie kwoty VAT pozycji do interfejsu, nie zmieniając `Ticket` ani `Snack`.

**Podpowiedź:** w Extract Interface odznacz wszystko, czego koszyk nie woła. Nowa metoda interfejsu nie może łamać istniejących implementacji. Pamiętaj, że domyślna metoda interfejsu jest widoczna tylko przez typ interfejsu.
**Gotowe, gdy:** test zielony, `Cart` nie odwołuje się do `Ticket` ani `Snack`, a nowa implementacja roli (np. okulary 3D za 3.00 z VAT 23%) działa bez zmian w koszyku.

## Scena s07. Collapse Hierarchy
**Namespace:** `Training.Workshop.M5.S07CollapseHierarchy.Start` · **Test:** `scripts/warsztat.sh --lang cs test m5/s07`

**Zasada:** Collapse Hierarchy scala poziomy, gdy rozróżnienie nie jest już kontraktem, wariantem ani punktem rozszerzenia. Pusty typ może jednak być markerem lub kontraktem konfiguracji, więc trzeba to sprawdzić przed usunięciem.

**Zadanie:**
1. Usuń zbędny poziom hierarchii sal, zachowując nazwę klasy używaną przez klientów.
2. Wiedza o regule VIP w sali IMAX nie może zniknąć.
3. Opisz w komentarzu, co trzeba by sprawdzić, zanim zrobi się to w bibliotece.

**Podpowiedź:** najpierw pozbądź się członków, które tylko wołają `base`. Jedyna różnica między klasami to sposób tworzenia.
**Gotowe, gdy:** test zielony, istnieje jedna klasa `sealed` `Hall` (bez członków `virtual`), sala IMAX ma VIP od przedostatniego rzędu.

## Scena s08. Replace Inheritance with Composition
**Namespace:** `Training.Workshop.M5.S08Composition.Start` · **Test:** `scripts/warsztat.sh --lang cs test m5/s08`

**Zasada:** Kompozycja zastępuje dziedziczenie, gdy dziedziczenie służy tylko do ponownego użycia kodu. Dziedziczenie po cudzej klasie wiąże nas z jej szczegółami: które metody są wirtualne i które wołają inne metody na `this`. Ukrycie metody słowem `new` to nie nadpisanie.

**Zadanie:**
1. Uruchom test i wyjaśnij, dlaczego `UnionWith` z dwoma miejscami wywołane przez zmienną typu `HashSet<string>` daje 0 kliknięć.
2. Zastąp dziedziczenie po `HashSet<string>` delegatem.
3. Zadbaj, by żadna metoda ani właściwość nie wydawała delegata na zewnątrz.
4. Wypisz operacje, które klienci świadomie tracą.

**Podpowiedź:** metody `HashSet` nie są wirtualne - sprawdź, co naprawdę robi `new` przy `Add` i `UnionWith`. Delegowanie wygeneruje Generate ⌘N > Delegating Members; sprawdź, co zwraca właściwość wydająca zbiór.
**Gotowe, gdy:** testy nie-`Start` zielone, `UnionWith` liczy każde miejsce raz, `SeatSelection` nie jest `ISet<string>`, a lista miejsc zwracana klientowi jest niemodyfikowalna i zachowuje kolejność wyboru.

## Scena s09. Overriding a overloading
**Namespace:** `Training.Workshop.M5.S09Overloading.Start` · **Test:** `scripts/warsztat.sh --lang cs test m5/s09`

**Zasada:** Override wybiera CLR w czasie wykonania według klasy obiektu, a przeciążenie wybiera kompilator według typu deklarowanego. Obowiązkowe słowo `override` zamienia pomyłkę przeciążenia w błąd kompilacji.

**Zadanie:**
1. Uruchom test i wyjaśnij, dlaczego student w `Checkout.Total(IReadOnlyList<Ticket>)` płaci pełną cenę, choć `PriceList` ma metodę dla `StudentTicket`.
2. Popraw model tak, by zniżka zależała od klasy runtime biletu.
3. Popraw porównywanie biletów tak, by `Contains` działało.

**Podpowiedź:** który z mechanizmów wybiera kompilator, a który CLR w czasie wykonania? Którą metodę `Equals` woła `List<T>.Contains` dla typu bez `IEquatable<T>`?
**Gotowe, gdy:** testy nie-`Start` zielone, `PriceList` ma jedno `Price(...)`, `Equals(object?)` i `GetHashCode()` są nadpisane (`override`).

## Scena s10. Ukrywanie pól i metod static
**Namespace:** `Training.Workshop.M5.S10FieldHiding.Start` · **Test:** `scripts/warsztat.sh --lang cs test m5/s10`

**Zasada:** Pola i metody `static` nie są polimorficzne - wiąże je kompilator według typu referencji. Pole redeklarowane w podklasie (z `new`) to drugi, niezależny slot w tym samym obiekcie.

**Zadanie:**
1. Wyjaśnij, dlaczego `new StudentTicket().Label()` zwraca `"BILET: NORMAL"`, choć podklasa ma własne `Type` i `Category()`.
2. Usuń ukrywanie pola.
3. Usuń ukrywanie metody statycznej.

**Podpowiedź:** pola i metody `static` są wiązane według typu, w którym stoi odwołanie. Słowo `new` tylko ucisza ostrzeżenie kompilatora.
**Gotowe, gdy:** testy nie-`Start` zielone, `Label()` studenta zwraca `"BILET ULGOWY: STUDENT"` niezależnie od typu referencji, a w kodzie nie ma `new` na członkach.

## Scena s11. Konstruktor wołający metodę nadpisywalną
**Namespace:** `Training.Workshop.M5.S11ConstructorCall.Start` · **Test:** `scripts/warsztat.sh --lang cs test m5/s11`

**Zasada:** Konstruktor klasy bazowej wykonuje się przed ciałem konstruktora podklasy, więc wywołany z niego override widzi obiekt w połowie zbudowany. Bezpieczny konstruktor nie woła metod wirtualnych.

**Zadanie:**
1. Znajdź przyczynę pustego salonika w etykiecie biletu VIP (`"Miejsce K12 (VIP: )"`) i regułę analizatora, która ją zapowiada.
2. Napraw błąd najpierw lokalnie w `VipTicket`, potem strukturalnie w `Ticket`.

**Podpowiedź:** w C# inicjalizatory pól wykonują się przed konstruktorem bazy (zobacz konstruktor główny). Czy to wystarczy dla następnej podklasy?
**Gotowe, gdy:** testy nie-`Start` zielone, konstruktor `Ticket` nie woła metod wirtualnych (reguła CA2214 nie miałaby czego zgłosić).

## Scena s12. Generyki i metody bridge
**Namespace:** `Training.Workshop.M5.S12BridgeMethods.Start` · **Test:** `scripts/warsztat.sh --lang cs test m5/s12`

**Zasada:** Generyczna rola, której instancje trzyma jeden rejestr, potrzebuje wejścia nieogólnego - w C# (bez typów wieloznacznych) to nieogólny interfejs bazowy i metoda "most" z rzutowaniem. Źródło reguły wygląda na jedną metodę `Apply`, ale refleksja widzi dwie.

**Zadanie:**
1. Wydziel generyczny interfejs reguły cenowej `IPriceRule<in T> where T : ITicket` z nieogólnym interfejsem bazowym `IPriceRule` (`Apply(ITicket)`).
2. Gdy test zrobi się czerwony, znajdź przyczynę (lista metod `Apply` w klasie reguły, np. `GetMethods` w teście albo podgląd IL/dekompilacja w Rider) i napraw rejestr.
3. Usuń zależność rejestru od refleksji.

**Podpowiedź:** jaką drugą metodę `Apply` musi mieć reguła, żeby spełnić nieogólny kontrakt? Jak odróżnić ją refleksją (`GetInterfaceMap`)? Gdzie można napisać most raz, zamiast w każdej regule?
**Gotowe, gdy:** test zielony, `RuleRegistry` nie używa `System.Reflection`, a brak reguły kończy się czytelnym `InvalidOperationException`.

## Scena s13. Hierarchie sealed
**Namespace:** `Training.Workshop.M5.S13Sealed.Start` · **Test:** `scripts/warsztat.sh --lang cs test m5/s13`

**Zasada:** Zamknięta hierarchia trzyma listę wariantów w jednym miejscu, a `switch` wymieniający każdy wariant jawnie pozwala sprawdzić, czy obsłużono wszystkie. Kompilator C# tego nie sprawdza dla hierarchii klas, więc ramię `_` powinno rzucać, a nie zwracać wartość.

**Zadanie:**
1. Zamknij hierarchię biletów (C# nie ma `sealed ... permits` - zamknij ją dostępnością konstruktora i `sealed record`).
2. Zastąp łańcuch `is` wyrażeniem `switch` po typach, bez ramienia zwracającego wartość domyślną.
3. Dodaj bilet dziecięcy (40%) i obsłuż go w kalkulatorze.

**Podpowiedź:** po kroku 3 przeczytaj testy `ExhaustivenessCheckFlagsUnhandledVariant` i `OldSwitchThrowsUnreachableExceptionForNewVariant` - kto w C# sprawdza wyczerpanie i co się dzieje ze skompilowanym wcześniej `switch`?
**Gotowe, gdy:** testy nie-`Start` zielone, bilet dziecięcy za 25.00 kosztuje 15.00, w `PriceCalculator` nie ma `is` ani ramienia `_` zwracającego liczbę (jest `_ => throw new UnreachableException(...)`).

## Scena s14. Współdzielenie implementacji a podtypowanie
**Namespace:** `Training.Workshop.M5.S14Reuse.Start` · **Test:** `scripts/warsztat.sh --lang cs test m5/s14`

**Zasada:** Wspólny kod uzasadnia współpracownika, a dziedziczenie dopiero wspólny kontrakt. Podtyp, który blokuje odziedziczoną operację wyjątkiem, łamie zasadę podstawienia.

**Zadanie:**
1. Wskaż, który kontrakt `LoyaltyAccount` łamie `CorporateAccount`.
2. Wydziel współdzieloną logikę punktów do osobnej klasy.
3. Zerwij dziedziczenie między kontami, a raportowi daj wspólną rolę.

**Podpowiedź:** osobno odpowiedz na pytania "czy mają ten sam kod?" i "czy są wariantami jednego pojęcia?".
**Gotowe, gdy:** test zielony, `CorporateAccount` nie jest `LoyaltyAccount` i nie ma metody wymiany punktów, `LoyaltyReport` przyjmuje oba konta.

## Scena s15. Zgodność binarna, refleksja i adnotacje
**Namespace:** `Training.Workshop.M5.S15Compatibility.Start` · **Test:** `scripts/warsztat.sh --lang cs test m5/s15`

**Zasada:** Zgodność ma kilka warstw: źródłową, binarną, refleksyjną i inne - pełny build sprawdza tylko pierwszą. Przeniesienie członka lub zmiana typu parametru może zmienić to, co widzą stare assembly i refleksja.

**Zadanie:**
1. Przenieś `Price()` (z atrybutem `[Column]`) do `Ticket`, nie psując eksportu CSV.
2. Zmień `BoxOfficeApi.Quote` tak, by przyjmowało każdy bilet.
3. Zachowaj działanie wtyczek partnerów skompilowanych przeciw starej wersji API.

**Podpowiedź:** przeczytaj `S15SolutionTest` - kompiluje wtyczkę (Roslyn) przeciw jednej wersji i uruchamia z inną, w osobnym `AssemblyLoadContext`. Zastanów się, która zmiana jest zgodna źródłowo, a która binarnie.
**Gotowe, gdy:** test zielony, eksport daje `Amator;cena=18.75`, stara sygnatura `Quote(StudentTicket)` jest dostępna jako `[Obsolete]` i deleguje do nowej.

## Scena s16. Serializacja i proxy
**Namespace:** `Training.Workshop.M5.S16SerializationProxy.Start` · **Test:** `scripts/warsztat.sh --lang cs test m5/s16`

**Zasada:** Serializator zapisujący pola (tu `DataContractSerializer`) zapisuje je poziomami hierarchii, więc ruch pola zmienia format danych, a stała nazwa kontraktu tego nie naprawia. `DispatchProxy` potrafi opakować tylko interfejsy.

**Zadanie:**
1. Wydziel nadklasę `Ticket` z polami `_title` i `_seat` i sprawdź w teście, co się dzieje z danymi zapisanymi przez starą wersję (stała XML w `S16SolutionTest`).
2. Uniezależnij postać serializowaną od hierarchii klas.
3. Umożliw opakowanie `TicketPricing` przez `DispatchProxy` (np. audyt wywołań), nie usuwając `sealed`.

**Podpowiedź:** Effective Java, "serialization proxy pattern" - w .NET: płaski DTO i `JsonConverter`. Czego wymaga `DispatchProxy.Create`?
**Gotowe, gdy:** test zielony, stare dane nie są czytane po cichu z utratą pól (są odrzucane wyjątkiem), zapis nie zawiera poziomu `Ticket`, a proxy audytowe zlicza wywołania `StudentPrice`.
