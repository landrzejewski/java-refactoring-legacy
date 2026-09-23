# Moduł 5. Refaktoryzacje hierarchii klas - warsztat CineLegacy: zadania

Każda scena to mały fragment systemu kina CineLegacy w pakiecie `pl.training.workshop.m5.sNN_.../start`. Pracuj wyłącznie w `start`, małymi krokami, i po każdym ruchu uruchamiaj test sceny (`scripts/warsztat.sh test m5/sNN`). Test równoważności (`SNNEquivalenceTest`) ma być zielony przez cały czas. W scenach o pułapkach (s08-s11, s13) testy o nazwie `start...` dokumentują błąd w kodzie wyjściowym - gdy go usuniesz, te testy zrobią się czerwone i to jest oczekiwany efekt. Nie zaglądaj do katalogów `stepN` przed zakończeniem zadania; potem porównaj swoje rozwiązanie z ostatnim krokiem (`scripts/warsztat.sh diff m5/sNN 0 N`). Chcesz zacząć od nowa: `scripts/warsztat.sh reset m5/sNN`.

Korzystaj z automatycznych refaktoryzacji IntelliJ IDEA (menu Refactor: Pull Members Up, Push Members Down, Extract Superclass, Extract Interface, Replace Inheritance with Delegation, Inline, Safe Delete), a ruchy ręczne rób tak, by po każdym dało się uruchomić test.

## Scena s01. Pull Up Method
**Pakiet:** `pl.training.workshop.m5.s01_pullupmethod.start` · **Test:** `scripts/warsztat.sh test m5/s01`
**Zadanie:**
1. Usuń trzy kopie metody `label()` z podklas `Ticket`, zostawiając jedną implementację.
2. Klient `BoxOffice` ma wywoływać `label()` przez typ bazowy.
3. Zdecyduj, czy wspólna metoda może być nadpisywana.

**Podpowiedź:** IDE przeniesie w górę tylko metody o identycznych ciałach, a wspólna metoda potrzebuje czegoś, czego baza jeszcze nie zna.
**Gotowe, gdy:** test zielony, `label()` jest zadeklarowane tylko w `Ticket`, a podklasy zawierają wyłącznie regułę ceny.

## Scena s02. Pull Up Field
**Pakiet:** `pl.training.workshop.m5.s02_pullupfield.start` · **Test:** `scripts/warsztat.sh test m5/s02`
**Zadanie:**
1. Przenieś informację o miejscu na sali do `Ticket` jako jedno pole.
2. Pole w bazie ma być `private final`, ustawiane przez konstruktor bazy.
3. Nie przenoś pól, które mają inne znaczenie.

**Podpowiedź:** zanim połączysz pola, porównaj ich nazwę, typ, znaczenie, cykl życia i sposób inicjalizacji. Każda różnica to osobny krok przygotowawczy.
**Gotowe, gdy:** test zielony, żadna podklasa nie deklaruje pola miejsca, nie ma settera, a normalizacja miejsca VIP działa jak wcześniej.

## Scena s03. Push Down Method/Field
**Pakiet:** `pl.training.workshop.m5.s03_pushdown.start` · **Test:** `scripts/warsztat.sh test m5/s03`
**Zadanie:**
1. Usuń z `Ticket` operację dopłaty VIP i związany z nią stan - ma zostać tylko tam, gdzie ma sens.
2. Usuń override rzucający `UnsupportedOperationException`.
3. Zapisz w komentarzu, co ta zmiana oznaczałaby dla skompilowanych klientów biblioteki.

**Podpowiedź:** kolejność ma znaczenie: klienci, potem zachowanie korzystające z pola, na końcu pole.
**Gotowe, gdy:** test zielony, `Ticket` nie ma `upgradeToVip()` ani pola VIP, `BoxOffice` nie używa `instanceof`.

## Scena s04. Extract Superclass
**Pakiet:** `pl.training.workshop.m5.s04_extractsuperclass.start` · **Test:** `scripts/warsztat.sh test m5/s04`
**Zadanie:**
1. Wydziel wspólną nadklasę dla seansu i wynajmu sali z nazwą opisującą pojęcie domenowe.
2. Dołączaj klasy do hierarchii pojedynczo, z testem po każdej.
3. Usuń trzy kopie warunku kolizji w `HallPlanner`.
4. Zachowaj publiczne konstruktory, fabrykę `rental(...)` i sygnaturę `conflicts(...)`.

**Podpowiedź:** zacznij od Refactor > Extract Superclass na jednej klasie. Algorytm kolizji potrzebuje od podklas tylko nazwy do komunikatu.
**Gotowe, gdy:** test zielony (w tym przypadek "styk 20:00 to nie konflikt"), nadklasa jest abstrakcyjna i ma prywatne pola, `HallPlanner` ma jedną pętlę.

## Scena s05. Extract Subclass
**Pakiet:** `pl.training.workshop.m5.s05_extractsubclass.start` · **Test:** `scripts/warsztat.sh test m5/s05`
**Zadanie:**
1. Wydziel podklasę dla premier i przenieś do niej stan i zachowanie, które dotyczą tylko premier.
2. Usuń flagę `premiere` i wszystkie `if (premiere)`.
3. Klient `Programme` nie może przekazywać `null` ani flagi.

**Podpowiedź:** zacznij od punktów tworzenia obiektów - to one mają wybierać klasę runtime.
**Gotowe, gdy:** test zielony, zwykły seans nie ma pola gościa, a fabryka premiery zwraca obiekt podklasy.

## Scena s06. Extract Interface i metody domyślne
**Pakiet:** `pl.training.workshop.m5.s06_extractinterface.start` · **Test:** `scripts/warsztat.sh test m5/s06`
**Zadanie:**
1. Wydziel interfejs roli, której potrzebuje koszyk - tylko z operacjami, których `Cart` używa.
2. `Cart` ma mieć jedną listę pozycji i jedno `add(...)`.
3. Przenieś liczenie kwoty VAT pozycji do interfejsu, nie zmieniając `Ticket` ani `Snack`.

**Podpowiedź:** w Extract Interface odznacz wszystko, czego koszyk nie woła. Nowa metoda interfejsu nie może łamać istniejących implementacji.
**Gotowe, gdy:** test zielony, `Cart` nie importuje `Ticket` ani `Snack`, a nowa implementacja roli (np. okulary 3D za 3.00 z VAT 23%) działa bez zmian w koszyku.

## Scena s07. Collapse Hierarchy
**Pakiet:** `pl.training.workshop.m5.s07_collapsehierarchy.start` · **Test:** `scripts/warsztat.sh test m5/s07`
**Zadanie:**
1. Usuń zbędny poziom hierarchii sal, zachowując nazwę klasy używaną przez klientów.
2. Wiedza o regule VIP w sali IMAX nie może zniknąć.
3. Opisz w komentarzu, co trzeba by sprawdzić, zanim zrobi się to w bibliotece.

**Podpowiedź:** najpierw pozbądź się metod, które tylko wołają `super`. Jedyna różnica między klasami to sposób tworzenia.
**Gotowe, gdy:** test zielony, istnieje jedna finalna klasa `Hall`, sala IMAX ma VIP od przedostatniego rzędu.

## Scena s08. Replace Inheritance with Composition
**Pakiet:** `pl.training.workshop.m5.s08_composition.start` · **Test:** `scripts/warsztat.sh test m5/s08`
**Zadanie:**
1. Uruchom test i wyjaśnij, dlaczego `addAll` z dwoma miejscami daje 4 kliknięcia.
2. Zastąp dziedziczenie po `LinkedHashSet` delegatem.
3. Zadbaj, by żadna metoda nie wydawała delegata na zewnątrz.
4. Wypisz operacje, które klienci świadomie tracą.

**Podpowiedź:** Refactor > Replace Inheritance with Delegation. Sprawdź, co zwraca wygenerowany getter.
**Gotowe, gdy:** testy nie-`start` zielone, `addAll` liczy każde miejsce raz, `SeatSelection` nie jest `Set`, a lista miejsc zwracana klientowi jest niemodyfikowalna.

## Scena s09. Overriding a overloading
**Pakiet:** `pl.training.workshop.m5.s09_overloading.start` · **Test:** `scripts/warsztat.sh test m5/s09`
**Zadanie:**
1. Uruchom test i wyjaśnij, dlaczego student w `Checkout.total(List<Ticket>)` płaci pełną cenę, choć `PriceList` ma metodę dla `StudentTicket`.
2. Popraw model tak, by zniżka zależała od klasy runtime biletu.
3. Popraw porównywanie biletów tak, by `List.contains` działało.

**Podpowiedź:** który z mechanizmów wybiera kompilator, a który JVM w czasie wykonania? Jak kompilator mógłby cię ostrzec przed drugim błędem?
**Gotowe, gdy:** testy nie-`start` zielone, `PriceList` ma jedno `price(...)`, `equals` i `hashCode` są nadpisane z `@Override`.

## Scena s10. Ukrywanie pól i metod static
**Pakiet:** `pl.training.workshop.m5.s10_fieldhiding.start` · **Test:** `scripts/warsztat.sh test m5/s10`
**Zadanie:**
1. Wyjaśnij, dlaczego `new StudentTicket().label()` zwraca `"BILET: NORMAL"`.
2. Usuń ukrywanie pola.
3. Usuń ukrywanie metody statycznej.

**Podpowiedź:** pola i metody `static` są wiązane według typu, w którym stoi odwołanie.
**Gotowe, gdy:** testy nie-`start` zielone, `label()` studenta zwraca `"BILET ULGOWY: STUDENT"` niezależnie od typu referencji.

## Scena s11. Konstruktor wołający metodę nadpisywalną
**Pakiet:** `pl.training.workshop.m5.s11_constructorcall.start` · **Test:** `scripts/warsztat.sh test m5/s11`
**Zadanie:**
1. Znajdź przyczynę `"null"` w etykiecie biletu VIP i ostrzeżenie kompilatora, które ją zapowiada.
2. Napraw błąd najpierw lokalnie w `VipTicket`, potem strukturalnie w `Ticket`.

**Podpowiedź:** Java 25 pozwala na instrukcje przed `super(...)`. Czy to wystarczy dla następnej podklasy?
**Gotowe, gdy:** testy nie-`start` zielone, konstruktor `Ticket` nie woła metod nadpisywalnych i nie ma ostrzeżenia `this-escape`.

## Scena s12. Generyki i metody bridge
**Pakiet:** `pl.training.workshop.m5.s12_bridgemethods.start` · **Test:** `scripts/warsztat.sh test m5/s12`
**Zadanie:**
1. Wydziel generyczny interfejs reguły cenowej `PriceRule<T extends Ticket>`.
2. Gdy test zrobi się czerwony, znajdź przyczynę (`javap -p` na klasie reguły) i napraw rejestr.
3. Usuń zależność rejestru od refleksji.

**Podpowiedź:** co robi kompilator z `apply(T)` po erasure, gdy implementacja ma konkretne `T`?
**Gotowe, gdy:** test zielony, `RuleRegistry` nie używa `java.lang.reflect`, a brak reguły kończy się czytelnym wyjątkiem.

## Scena s13. Hierarchie sealed
**Pakiet:** `pl.training.workshop.m5.s13_sealed.start` · **Test:** `scripts/warsztat.sh test m5/s13`
**Zadanie:**
1. Zamknij hierarchię biletów.
2. Zastąp łańcuch `instanceof` wyczerpującym `switch` bez `default`.
3. Dodaj bilet dziecięcy (40%) i pozwól kompilatorowi wskazać, co trzeba obsłużyć.

**Podpowiedź:** po kroku 3 przeczytaj test `oldExhaustiveSwitchThrowsMatchExceptionForNewVariant` - co się dzieje ze skompilowanym wcześniej `switch`?
**Gotowe, gdy:** testy nie-`start` zielone, bilet dziecięcy za 25.00 kosztuje 15.00, w `PriceCalculator` nie ma `default` ani `instanceof`.

## Scena s14. Współdzielenie implementacji a podtypowanie
**Pakiet:** `pl.training.workshop.m5.s14_reuse.start` · **Test:** `scripts/warsztat.sh test m5/s14`
**Zadanie:**
1. Wskaż, który kontrakt `LoyaltyAccount` łamie `CorporateAccount`.
2. Wydziel współdzieloną logikę punktów do osobnej klasy.
3. Zerwij dziedziczenie między kontami, a raportowi daj wspólną rolę.

**Podpowiedź:** osobno odpowiedz na pytania "czy mają ten sam kod?" i "czy są wariantami jednego pojęcia?".
**Gotowe, gdy:** test zielony, `CorporateAccount` nie jest `LoyaltyAccount` i nie ma metody wymiany punktów, `LoyaltyReport` przyjmuje oba konta.

## Scena s15. Zgodność binarna, refleksja i adnotacje
**Pakiet:** `pl.training.workshop.m5.s15_compatibility.start` · **Test:** `scripts/warsztat.sh test m5/s15`
**Zadanie:**
1. Przenieś `price()` (z adnotacją `@Column`) do `Ticket`, nie psując eksportu CSV.
2. Zmień `BoxOfficeApi.quote` tak, by przyjmowało każdy bilet.
3. Zachowaj działanie wtyczek partnerów skompilowanych przeciw starej wersji API.

**Podpowiedź:** przeczytaj `S15SolutionTest` - kompiluje wtyczkę przeciw jednej wersji i uruchamia z inną. Zastanów się, która zmiana jest zgodna źródłowo, a która binarnie.
**Gotowe, gdy:** test zielony, eksport daje `Amator;cena=18.75`, stara sygnatura `quote(StudentTicket)` jest dostępna jako przestarzała i deleguje do nowej.

## Scena s16. Serializacja i proxy
**Pakiet:** `pl.training.workshop.m5.s16_serializationproxy.start` · **Test:** `scripts/warsztat.sh test m5/s16`
**Zadanie:**
1. Wydziel nadklasę `Ticket` z polami `title` i `seat` i sprawdź w teście, co się dzieje z danymi zapisanymi przez starą wersję.
2. Uniezależnij postać serializowaną od hierarchii klas.
3. Umożliw opakowanie `TicketPricing` dynamicznym proxy JDK (np. audyt wywołań), nie usuwając `final`.

**Podpowiedź:** Effective Java, "serialization proxy pattern". Czego wymaga `java.lang.reflect.Proxy`?
**Gotowe, gdy:** test zielony, stare dane nie są czytane po cichu z utratą pól, strumień nie zawiera poziomu `Ticket`, a proxy audytowe zlicza wywołania `studentPrice`.
