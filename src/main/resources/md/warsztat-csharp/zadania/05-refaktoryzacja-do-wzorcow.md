# Moduł 5. Refaktoryzacja do wzorców - warsztat CineLegacy (C#): zadania

Każda scena to mały fragment systemu kina CineLegacy z zapachem, który prowadzi do wzorca projektowego (albo do usunięcia wzorca, gdy przestał być potrzebny). Pracujesz w namespace `Start` danej sceny, np. `Training.Workshop.M5.S08State.Start` (katalog `csharp/src/Training.Workshop/M5/S08State/Start`). Test sceny przechodzi przez `Start` i gotowe snapshoty `StepN` - Twoja wersja jest poprawna, gdy test jest zielony, a kod spełnia warunek projektowy z zadania. Testy leżą w `csharp/tests/Training.Workshop.Tests/M5/SNN.../`.

Zasady pracy:

- Najpierw uruchom test i przeczytaj go - to on opisuje kontrakt (wyjątki, kolejność efektów, format wyniku). Nie zmieniaj testów.
- Małe kroki: jeden ruch w IDE (Rider), test, następny ruch. Wzorzec wprowadzaj stopniowo, nie przepisuj klasy od zera.
- Nie zaglądaj do katalogów `StepN` przed końcem - to rozwiązania prowadzącego.
- Test woła scenę przez klienta albo korzeń kompozycji (np. `PriceBoard`, `CancellationDesk`, `PaymentServices`, `Reports`, `Cinema`, `PaymentModule`, `SettlementModule`) - gdy zmieniasz sygnatury, popraw też klienta, a nie test.
- Wprowadzenie wzorca to nadal refaktoryzacja. Jeśli chcesz zmienić zachowanie (nowa reguła, inna kolejność, inny komunikat), zapisz to jako osobną propozycję. W scenach s21, s23, s29, s30 i s31 część zmian zachowania jest zamierzona - opisują je osobne testy i to one mówią, która zmiana jest uzgodniona.
- Projekt ma `TreatWarningsAsErrors` - ostrzeżenie kompilatora (np. switch bez ramienia `_`, wywołanie metody `[Obsolete]`, pole nieustawione w konstruktorze) zatrzymuje build.

```bash
scripts/warsztat.sh --lang cs list m5          # sceny i kroki
scripts/warsztat.sh --lang cs test m5/s08      # testy sceny (wszystkie warianty)
scripts/warsztat.sh --lang cs reset m5/s08     # przywrócenie Start do wersji z repozytorium
```

## Scena s01. Replace Conditional Logic with Strategy - polityka zniżek

**Namespace:** `Training.Workshop.M5.S01Strategy.Start` · **Test:** `scripts/warsztat.sh --lang cs test m5/s01`

**Zasada:** Strategy wydziela wymienne warianty jednego obliczenia za wspólnym interfejsem, gdy wariant wybiera się niezależnie od klasy obiektu. Wspólna walidacja zostaje w kontekście, a moment wyboru wariantu jest częścią zachowania.

**Zadanie:**
1. Wprowadź interfejs `IDiscountPolicy` i najpierw użyj go jako strategii przejściowej delegującej do starego kodu.
2. Przenieś każdy program zniżek (STANDARD, STUDENT_WEEK, PREMIERE) do osobnej strategii, po jednym.
3. Zastanów się, gdzie powinien zapaść wybór programu i co się zmieni, jeśli przeniesiesz go do konstruktora.

**Podpowiedź:** walidacja ceny i programu jest wspólna - niech zostanie w kontekście. Sprawdź w teście przypadek "PREMIERE nie sprawdza typu". Lambda nie zaimplementuje interfejsu C# - strategia przejściowa to mała klasa prywatna.
**Gotowe, gdy:** test zielony, `TicketPricer` nie zawiera łańcucha `if` po nazwie programu, a strategie są bezstanowe.

## Scena s02. Replace Conditional with Polymorphism - rodzaj seansu

**Namespace:** `Training.Workshop.M5.S02Polymorphism.Start` · **Test:** `scripts/warsztat.sh --lang cs test m5/s02`

**Zasada:** Polimorfizm podtypów przenosi zachowanie zależne od trwałego rodzaju obiektu do osobnych typów, zamiast powtarzać `switch` po polu rodzaju. Najpierw trzeba znaleźć wszystkie miejsca, w których obiekty powstają.

**Zadanie:**
1. Znajdź miejsce tworzenia obiektów `Screening`.
2. Wydziel podtyp dla jednego rodzaju seansu, uruchom test, potem kolejne.
3. Usuń pole `_kind` i nadaj każdemu rodzajowi dane o jednoznacznej nazwie zamiast `_value`.

**Podpowiedź:** `switch` w miejscu tworzenia może zostać. Znikają te w `Label`, `DurationMinutes` i `Price`. Konstruktor bazy `private protected` zamyka hierarchię w assembly.
**Gotowe, gdy:** test zielony, zachowanie rodzajów jest w podtypach, a nieznany rodzaj w danych nadal daje ten sam wyjątek.

## Scena s03. Replace Type Code with Class - format jako typ

**Namespace:** `Training.Workshop.M5.S03TypeCode.Start` · **Test:** `scripts/warsztat.sh --lang cs test m5/s03`

**Zasada:** Replace Type Code with Class zastępuje surowy kod typem, który sam pilnuje poprawnych wartości i zna operacje swojego pojęcia. Kod zapisywany trwale to osobna sprawa - musi pozostać stabilny niezależnie od kształtu typu.

**Zadanie:**
1. Zastąp `int formatCode` typem `Format`; zamieniaj kod na typ tuż po odczycie CSV.
2. Przenieś zachowanie zależne od formatu (etykieta, cena, okulary) do typu.
3. Wydziel mapowanie kodu trwałego do osobnego mappera.

**Podpowiedź:** plik CSV musi nadal zawierać `Diuna;3`. Nie używaj `(int)format`. Enum C# nie ma metod - operacje typu umieść w bloku członków rozszerzeń `extension(Format format)`.
**Gotowe, gdy:** test zielony, żaden kod poza mapperem nie operuje na liczbach 1, 2, 3, a zapis CSV jest bez zmian.

## Scena s04. Encapsulate Classes with Factory - bilety

**Namespace:** `Training.Workshop.M5.S04EncapsulateFactory.Start` · **Test:** `scripts/warsztat.sh --lang cs test m5/s04`

**Zasada:** Encapsulate Classes with Factory chowa klasy konkretne za fabryką, tak żeby klient znał tylko wspólny interfejs. Tworzenie przekierowuje się pojedynczo, a widoczność ogranicza dopiero wtedy, gdy wszyscy klienci już przeszli.

**Zadanie:**
1. Zastąp każde `new StandardTicket/VipTicket` w `BoxOffice` metodą tworzącą.
2. Przenieś regułę "który bilet dla którego rzędu" do fabryki.
3. Ogranicz widoczność klas konkretnych biletów.

**Podpowiedź:** `internal` nie ukryje klas przed `BoxOffice` (to samo assembly). Kompilator pokaże wszystkie użycia klas konkretnych, gdy staną się prywatnymi typami zagnieżdżonymi w fabryce (`partial class` pozwala zostawić każdy bilet we własnym pliku).
**Gotowe, gdy:** test zielony (także `S04SolutionTest` sprawdzający widoczność w rozwiązaniu wzorcowym), a `BoxOffice` używa tylko `ITicket` i fabryki.

## Scena s05. Extract Factory Class - tworzenie rezerwacji

**Namespace:** `Training.Workshop.M5.S05ExtractFactory.Start` · **Test:** `scripts/warsztat.sh --lang cs test m5/s05`

**Zasada:** Extract Factory Class przenosi reguły tworzenia obiektu do osobnej klasy, gdy klasa, która je dziś zawiera, ma inną główną odpowiedzialność. Fabryka jest zwykłą zależnością, a kolejność operacji przy tworzeniu pozostaje częścią kontraktu.

**Zadanie:**
1. Usuń duplikację tworzenia rezerwacji między `Reserve` i `ReserveGroup`.
2. Wydziel klasę fabryki odpowiedzialną za numer, opłatę i termin ważności.
3. Przekaż fabrykę do serwisu jako zależność.

**Podpowiedź:** przeczytaj przypadek testowy o "spalonym" numerze - kolejność operacji jest częścią kontraktu. Zegar to `TimeProvider` - przenieś go razem z tworzeniem.
**Gotowe, gdy:** test zielony, serwis nie zawiera reguł numeracji, opłat ani terminu ważności, a fabrykę da się przetestować osobno.

## Scena s06. Encapsulate Composite with Builder - repertuar dnia

**Namespace:** `Training.Workshop.M5.S06Builder.Start` · **Test:** `scripts/warsztat.sh --lang cs test m5/s06`

**Zasada:** Builder dla struktury złożonej ukrywa tworzenie i łączenie węzłów za API opisującym kształt wyniku. Opłaca się, gdy budowa jest wieloetapowa i ma własne reguły, a gotowy wynik nie powinien się już zmieniać.

**Zadanie:**
1. Napisz builder repertuaru, dzięki któremu `WeekendPlanner` nie używa `new Hall` ani `Add`.
2. Uczyń drzewo niemutowalnym po zbudowaniu.
3. Zdecyduj, czy builder może być użyty ponownie i co ma się stać przy drugim `Build()`.

**Podpowiedź:** porównaj builder z "bieżącą salą" i builder z lambdą (`Action<HallBuilder>`) dla każdej sali - który mniej ukrywa?
**Gotowe, gdy:** test zielony, kształt kodu planera odpowiada kształtowi repertuaru, a zbudowanych list nie da się zmodyfikować (także po rzutowaniu na `ICollection<T>`).

## Scena s07. Move Embellishment to Decorator - dodatki do biletu

**Namespace:** `Training.Workshop.M5.S07Decorator.Start` · **Test:** `scripts/warsztat.sh --lang cs test m5/s07`

**Zasada:** Decorator owija rdzeń obiektem o tym samym kontrakcie, który dokłada opcjonalne zachowanie i deleguje resztę do środka. Kolejność owijania jest częścią zachowania, a udekorowany obiekt nie jest przezroczysty dla pytań o typ i tożsamość.

**Zadanie:**
1. Wydziel wspólny interfejs biletu i miejsce składania biletu.
2. Przenoś dodatki (VIP, okulary 3D, ubezpieczenie) do dekoratorów po jednym, usuwając flagi z rdzenia.
3. Sprawdź, co zwraca `is` i `Equals` dla udekorowanego biletu.

**Podpowiedź:** opis biletu ma ustaloną kolejność dodatków. Od którego dodatku zacząć, żeby jej nie zmienić?
**Gotowe, gdy:** test zielony, rdzeń biletu nie ma żadnej flagi dodatku, a kolejność owijania jest w jednym miejscu.

## Scena s08. Replace State-Altering Conditionals with State - status rezerwacji

**Namespace:** `Training.Workshop.M5.S08State.Start` · **Test:** `scripts/warsztat.sh --lang cs test m5/s08`

**Zasada:** State przenosi zachowanie zależne od bieżącego stanu do obiektów stanów, a kontekst tylko do nich deleguje. Tabelę przejść ustala się przed budową hierarchii, a niedozwolone przejście nadal musi kończyć się tym samym błędem.

**Zadanie:**
1. Przeczytaj tabelę przejść w teście i porównaj ją z kodem.
2. Zastąp pole `_status` obiektem stanu.
3. Przenoś przejścia (`Pay`, `Use`, `Expire`, `Cancel`) do stanów po jednym; niedozwolone przejście ma rzucać ten sam wyjątek (np. `cannot pay in New`).

**Podpowiedź:** gdy bramka płatności zawiedzie, rezerwacja zostaje New. Zachowaj kolejność: obciążenie, zmiana stanu, efekt. Domyślna implementacja metody w interfejsie stanu może rzucać wyjątek - wtedy stan implementuje tylko dozwolone przejścia.
**Gotowe, gdy:** wszystkie 84 testy zielone, a metody `Reservation` tylko delegują do stanu.

## Scena s09. Replace Hard-coded Notifications with Observer - po opłaceniu

**Namespace:** `Training.Workshop.M5.S09Observer.Start` · **Test:** `scripts/warsztat.sh --lang cs test m5/s09`

**Zasada:** Observer sprawia, że źródło zdarzenia zna tylko wspólny interfejs odbiorców, a nie ich konkretne klasy. Kolejność powiadomień i polityka błędów to kontrakt, którego refaktoryzacja nie zmienia.

**Zadanie:**
1. Wprowadź zdarzenie "rezerwacja opłacona" i jedno miejsce powiadamiania.
2. Zamień maila, SMS i punkty lojalnościowe na obserwatorów wspólnego interfejsu.
3. Umożliw rejestrowanie i wyrejestrowanie odbiorców (uchwyt `IDisposable`), a składanie standardowego zestawu przenieś do `PaymentServices`.

**Podpowiedź:** wyjątek w SMS przerywa naliczanie punktów - to obecny kontrakt, nie błąd do naprawy w tej refaktoryzacji.
**Gotowe, gdy:** test zielony (kolejność i awarie), a serwis nie zna `IMailer`, `ISmsGateway` ani `ILoyaltyProgram`.

## Scena s10. Replace Implicit Tree with Composite - zestawy baru

**Namespace:** `Training.Workshop.M5.S10ImplicitTree.Start` · **Test:** `scripts/warsztat.sh --lang cs test m5/s10`

**Zasada:** Composite zastępuje drzewo ukryte w konwencji danych jawnymi typami liścia i węzła o wspólnym kontrakcie. Operacje przenosi się po jednej, a stary format tłumaczy mapper, który zachowuje dotychczasowe błędy.

**Zadanie:**
1. Zaprojektuj jawny Composite zestawu (produkt i zestaw) oraz mapper z zagnieżdżonych list (`IReadOnlyList<object>`).
2. Przenieś `Price`, a potem `Render` na Composite - po jednej operacji.
3. Usuń stary kod z testami `is`.

**Podpowiedź:** komunikaty błędów dla złych danych też są kontraktem. Test różnicowy porównuje wynik `Start` z rozwiązaniem wzorcowym na 500 losowych drzewach.
**Gotowe, gdy:** testy równoważności i różnicowy zielone, a `BarMenu` nie zawiera testów `is` ani rzutowań.

## Scena s11. Transparent vs Safe Composite - add() na liściu

**Namespace:** `Training.Workshop.M5.S11SafeComposite.Start` · **Test:** `scripts/warsztat.sh --lang cs test m5/s11`

**Zasada:** Composite może trzymać zarządzanie dziećmi we wspólnym typie (Transparent) albo tylko w węźle (Safe). Pierwszy wariant upraszcza klienta kosztem błędów w runtime, drugi przenosi te błędy do kompilacji.

**Zadanie:**
1. Spraw, żeby wywołanie `Add` na produkcie było błędem kompilacji, a nie wyjątkiem w runtime.
2. Popraw typy w `ComboCatalog` tam, gdzie dodajesz elementy.
3. Rozważ wersję, w której zestaw jest niemutowalny i `Add` nie istnieje wcale.

**Podpowiedź:** w Rider poszukaj refaktoryzacji przenoszącej składowe do podklasy (Refactor This ⌃T).
**Gotowe, gdy:** test zielony, a wspólny typ `MenuComponent` nie ma `Add` ani `Children`.

## Scena s12. Replace One/Many Distinctions with Composite - zwroty

**Namespace:** `Training.Workshop.M5.S12OneMany.Start` · **Test:** `scripts/warsztat.sh --lang cs test m5/s12`

**Zasada:** Composite pozwala traktować jeden element i grupę elementów przez ten sam kontrakt, dzięki czemu API nie potrzebuje osobnych ścieżek "jeden" i "wiele". Efekty naliczane raz na wywołanie muszą przy tym pozostać naliczane raz.

**Zadanie:**
1. Usuń duplikację reguły zwrotu między `Refund` i `RefundAll`.
2. Wprowadź wspólny kontrakt dla jednego biletu i grupy biletów oraz jedną metodę `Refund`.
3. Oznacz stare metody jako przestarzałe (`[Obsolete]`), a potem usuń je.

**Podpowiedź:** potrącenie 3.00 jest naliczane raz na zwrot, nie raz na bilet. Przy ostrzeżeniach traktowanych jak błędy wywołanie metody `[Obsolete]` nie skompiluje się - przenieś klienta na nowy kontrakt w tym samym kroku.
**Gotowe, gdy:** test zielony, reguła zwrotu jest w jednym miejscu, a ani `RefundService`, ani klient `CancellationDesk` nie rozróżniają jednego biletu i wielu.

## Scena s13. Extract Composite - kontenery programu

**Namespace:** `Training.Workshop.M5.S13ExtractComposite.Start` · **Test:** `scripts/warsztat.sh --lang cs test m5/s13`

**Zasada:** Extract Composite przenosi powieloną obsługę dzieci z kilku klas kontenerów do wspólnej nadklasy. Podciąga się tylko to, co ma ten sam kontrakt, a nie wszystko, co wygląda podobnie.

**Zadanie:**
1. Wydziel wspólną nadklasę dla `Marathon` i `ShortsBlock` z obsługą dzieci.
2. Podciągnij do niej to, co naprawdę jest wspólne; zostaw w podklasach to, co się różni.

**Podpowiedź:** obie klasy sumują minuty, ale tylko maraton dolicza przerwy.
**Gotowe, gdy:** test zielony, lista dzieci i `Add` istnieją w jednym miejscu, a podklasy mają po kilkanaście linii.

## Scena s14. Unify Interfaces with Adapter - dwie bramki płatności

**Namespace:** `Training.Workshop.M5.S14Adapter.Start` · **Test:** `scripts/warsztat.sh --lang cs test m5/s14`

**Zasada:** Adapter tłumaczy obcy interfejs na kontrakt, którego oczekuje klient, tak żeby klient nie znał szczegółów zewnętrznej biblioteki. Tłumaczyć trzeba nie tylko nazwy, ale też jednostki, formaty i sposób zgłaszania błędów.

**Zadanie:**
1. Wydziel obsługę każdej bramki do metody o tej samej sygnaturze.
2. Zdefiniuj preferowany interfejs płatności kina i dwa adaptery.
3. Spraw, żeby `CheckoutService` zależał wyłącznie od tego interfejsu.

**Podpowiedź:** stara bramka liczy w groszach i zwraca odmowę w XML, nowa liczy w złotych i zgłasza odmowę wyjątkiem. Nie zmieniaj klas bramek.
**Gotowe, gdy:** test zielony, a metoda `Pay` w `CheckoutService` korzysta wyłącznie z preferowanego interfejsu (szczegóły XML i REST są tylko w adapterach).

## Scena s15. Replace Conditional Dispatcher with Command - konsola kasjera

**Namespace:** `Training.Workshop.M5.S15Command.Start` · **Test:** `scripts/warsztat.sh --lang cs test m5/s15`

**Zasada:** Command zamienia każdą gałąź dyspozytora w osobny obiekt o wspólnym interfejsie, a wybór gałęzi w wyszukanie w rejestrze. Rejestr jest równoważny warunkom tylko przy rozłącznych kluczach i zachowanej normalizacji.

**Zadanie:**
1. Wydziel ciało każdej gałęzi do metody o wspólnej sygnaturze.
2. Zamień gałęzie na obiekty komend; stan kasy wydziel z konsoli.
3. Zastąp łańcuch `if` rejestrem komend.

**Podpowiedź:** `sell` i `SELL` działają tak samo (`ToUpperInvariant()`) - nie zgub normalizacji klucza.
**Gotowe, gdy:** test zielony, komendy są bezstanowe, a dodanie komendy nie wymaga zmiany metody `Handle`.

## Scena s16. Form Template Method - raporty CSV i HTML

**Namespace:** `Training.Workshop.M5.S16TemplateMethod.Start` · **Test:** `scripts/warsztat.sh --lang cs test m5/s16`

**Zasada:** Template Method zapisuje stałą sekwencję kroków raz w nadklasie, a podklasy dostarczają tylko kroki, które się różnią. Najpierw doprowadza się podobne metody do identycznej postaci, dopiero potem je podciąga.

**Zadanie:**
1. Doprowadź `Render` obu raportów do identycznej postaci, wydzielając różnice do metod o tych samych nazwach.
2. Przenieś wspólny szkielet do nadklasy i zabezpiecz go przed nadpisaniem.

**Podpowiedź:** różnice to nagłówek, wiersz (z escapowaniem) i stopka. W C# metoda bez `virtual` nie da się nadpisać - to odpowiednik `final` z Javy.
**Gotowe, gdy:** test zielony, sortowanie i sumowanie są w jednym miejscu, a raporty zawierają tylko formatowanie.

## Scena s17. Limit Instantiation with Singleton - cennik

**Namespace:** `Training.Workshop.M5.S17Singleton.Start` · **Test:** `scripts/warsztat.sh --lang cs test m5/s17`

**Zasada:** Singleton ogranicza tworzenie obiektu do jednej instancji i jest decyzją o cyklu życia, bezpieczną tylko dla obiektów niemutowalnych i równoważnych. Globalny dostęp do tej instancji ukrywa zależności i utrudnia testy.

**Zadanie:**
1. Sprawdź w teście, ile cenników powstaje przy trzech wycenach, i uzasadnij, dlaczego jedna instancja jest bezpieczna.
2. Ogranicz tworzenie cennika do jednej instancji.
3. Spraw, żeby `TicketDesk` dało się przetestować z innym cennikiem bez globalnego stanu.

**Podpowiedź:** porównaj klasyczne `GetInstance()` ze statyczną właściwością `Instance { get; } = new()`, a potem zastanów się, kto powinien decydować o cyklu życia.
**Gotowe, gdy:** test zielony, cennik jest tworzony raz, a `TicketDesk` przyjmuje cennik przez konstruktor.

## Scena s18. Move Accumulation to Collecting Parameter - ostrzeżenia walidacji

**Namespace:** `Training.Workshop.M5.S18CollectingParameter.Start` · **Test:** `scripts/warsztat.sh --lang cs test m5/s18`

**Zasada:** Collecting Parameter przekazuje do metod wspólny akumulator, do którego dopisują one swoje wyniki, zamiast zwracać fragmenty do sklejenia. Akumulator należy do wywołującego i powinien mieć wąski typ.

**Zadanie:**
1. Zastąp sklejanie ostrzeżeń w `string` listą.
2. Przekaż akumulator do metod pomocniczych zamiast zwracać z nich fragmenty.
3. Nadaj akumulatorowi wąski typ, który pozwala tylko dopisywać.

**Podpowiedź:** separator `"; "` powinien pojawiać się w kodzie dokładnie raz.
**Gotowe, gdy:** test zielony, metody `Check...` zwracają `void`, a format wyniku jest ustalany w jednym miejscu.

## Scena s19. Visitor i macierz zmian - pozycje zamówienia

**Namespace:** `Training.Workshop.M5.S19Visitor.Start` · **Test:** `scripts/warsztat.sh --lang cs test m5/s19`

**Zasada:** Visitor wydziela operacje na strukturze do osobnych klas, a element sam wybiera właściwą metodę odwiedzającego. Tanio dodaje się w nim nowe operacje, drogo nowe rodzaje elementów.

**Zadanie:**
1. Zamień trzy łańcuchy `is` na klasyczny Visitor.
2. Przygotuj alternatywę: switch expression po typach pozycji (C# wymaga ramienia `_` - niech rzuca `UnreachableException`).
3. Wypełnij macierz: ile plików zmienisz przy nowej operacji, a ile przy nowym rodzaju pozycji - w obu wersjach. Zapisz też, w której wersji kompilator C# zgłosi brakującą obsługę nowego rodzaju.

**Podpowiedź:** VAT liczymy od ceny brutto każdej pozycji osobno i dopiero potem sumujemy.
**Gotowe, gdy:** test zielony dla wybranej wersji, w kodzie nie ma łańcuchów `is`, a dodanie typu pozycji bez obsługi kończy się błędem kompilacji (Visitor) albo co najmniej `UnreachableException` wskazującym brakujący typ (switch expression).

## Scena s20. Mapa decyzji - jedna tabela, dwie struktury

**Namespace:** `Training.Workshop.M5.S20DecisionMap.Start` · **Test:** `scripts/warsztat.sh --lang cs test m5/s20`

**Zasada:** Wzorzec dobiera się do rodzaju zmienności, a nie do wyglądu kodu: punkt rozszerzenia powinien leżeć na osi, która zmienia się najczęściej. Gdy nie wiadomo, która to oś, prostsza struktura bez nowych typów jest bezpieczniejsza.

**Zadanie:**
1. Rozplącz tabelę cen na dwie niezależne reguły.
2. Zaimplementuj wariant A: marketing często zmienia reguły dniowe.
3. Od stanu po punkcie 1 zaimplementuj wariant B: często dochodzą nowe formaty.
4. Zapisz jednym zdaniem, po czym poznasz w projekcie, który wariant wybrać.

**Podpowiedź:** zrób commit (albo kopię) po punkcie 1, żeby móc wrócić i zbudować drugi wariant. Regułę dnia można wyrazić delegatem, a format - klasą z instancjami statycznymi.
**Gotowe, gdy:** test zielony dla obu wariantów, a uzasadnienie wyboru odwołuje się do tego, co się zmienia, nie do wyglądu kodu.

## Scena s21. Wzorzec jako decyzja odwracalna - rozliczenie z dystrybutorem

**Namespace:** `Training.Workshop.M5.S21ReversiblePattern.Start` · **Test:** `scripts/warsztat.sh --lang cs test m5/s21`

**Zasada:** Wzorzec projektowy to odpowiedź na konkretne siły, a nie kod na zapas - gdy siły znikają, wzorzec można usunąć. Usunięcie wzorca jest równie poprawną refaktoryzacją jak jego wprowadzenie.

**Zadanie:**
1. Wprowadź Strategy dla dwóch istniejących modeli rozliczeń; dla modelu festiwalowego napisz Adapter do `FestivalTariffClient`.
2. Umowy festiwalowe wygasły: usuń ten wariant (to świadoma zmiana zachowania).
3. Oceń, czy wzorzec nadal jest potrzebny, i jeśli nie - usuń go.

**Podpowiedź:** po każdym kroku test równoważności dla modelu procentowego musi być zielony. Sygnały nadmiaru wzorca: jedna implementacja, słownik z jednym wpisem.
**Gotowe, gdy:** test zielony, rozliczenie procentowe daje te same kwoty co na początku, model FESTIVAL jest odrzucany, a w kodzie nie zostały nieużywane typy.

## Scena s22. Remove Middle Man - fasada kina

**Namespace:** `Training.Workshop.M5.S22MiddleMan.Start` · **Test:** `scripts/warsztat.sh --lang cs test m5/s22`

**Zasada:** Pośrednika, który tylko przekazuje wywołania dalej, można usunąć. Najpierw trzeba jednak sprawdzić, czy nie robi czegoś więcej, na przykład autoryzacji, retry albo translacji błędów.

**Zadanie:**
1. Sprawdź każdą metodę `CinemaFacade`: czy tylko deleguje, czy robi coś więcej.
2. Przenieś to, co robi więcej, tam, gdzie jest potrzebne.
3. Zmigruj klientów do `ScreeningCatalog` po jednym i usuń fasadę.

**Podpowiedź:** uruchom `SeatBadge` dla seansu `S9` przed i po zmianie.
**Gotowe, gdy:** test jest zielony, `CinemaFacade` nie istnieje, a zachowanie dla nieznanego seansu jest takie samo jak na starcie.

## Scena s23. Refaktoryzacja a zmiana kontraktu - zwrot na BigDecimal

**Namespace:** `Training.Workshop.M5.S23ContractChange.Start` · **Test:** `scripts/warsztat.sh --lang cs test m5/s23`

**Zasada:** Refaktoryzacja zachowuje obserwowalne zachowanie: klient nie może dostrzec żadnej nieuzgodnionej różnicy. Każda zmiana wyniku, formatu czy wyjątku to zmiana kontraktu, którą robi się osobno i świadomie.

**Zadanie:**
1. Zrób jedną czystą refaktoryzację `RefundCalculator.Refund` bez zmiany typów.
2. Przejdź na `decimal` (odpowiednik `BigDecimal` z tytułu sceny) i znajdź wszystkie różnice w zachowaniu, które to wprowadza.
3. Dla każdej różnicy zdecyduj: zachować stary kontrakt czy świadomie go zmienić. Zapisz decyzję.

**Podpowiedź:** sprawdź zwrot po starcie seansu oraz kwotę 64.35 anulowaną 2 godziny przed seansem. Pamiętaj, że `Math.Round` na `decimal` nie dopisuje zer, a bez jawnego `MidpointRounding` zaokrągla bankiersko.
**Gotowe, gdy:** testy są zielone, a każda różnica względem startu ma w teście jawne oczekiwanie i jedno zdanie uzasadnienia.

## Scena s24. Rename - nazwy, które żyją poza C#

**Namespace:** `Training.Workshop.M5.S24Rename.Start` · **Test:** `scripts/warsztat.sh --lang cs test m5/s24`

**Zasada:** Rename nadaje nazwę opisującą rolę w kontekście. Nazwa używana poza C# (konfiguracja, refleksja, formaty plików) jest kontraktem i wymaga strategii migracji, a nie tylko operacji IDE.

**Zadanie:**
1. Nadaj znaczące nazwy zmiennym lokalnym i parametrom w `SalesReport`.
2. Zmień nazwę metody `Calc2` na nazwę opisującą, co zwraca.
3. Zmień nazwy właściwości rekordu `Line` (`t`, `n`, `d`).
4. Nie zmieniaj konfiguracji w `ReportJob` ani formatu CSV, bo oba są kontraktem z kimś spoza repozytorium.

**Podpowiedź:** po każdym Rename uruchom oba testy równoważności. Jeśli jeden jest czerwony, zastanów się, gdzie jeszcze żyje ta nazwa.
**Gotowe, gdy:** test jest zielony, w `SalesReport` nie ma jedno- ani dwuliterowych nazw, a zadanie z konfiguracji dalej działa.

## Scena s25. Extract Method - przepływ danych i wiele wyjść

**Namespace:** `Training.Workshop.M5.S25ExtractMethod.Start` · **Test:** `scripts/warsztat.sh --lang cs test m5/s25`

**Zasada:** Extract Method przenosi spójny fragment do metody, której nazwa wyraża intencję. Przed ekstrakcją trzeba przeanalizować przepływ danych: co wchodzi, a co wychodzi z fragmentu.

**Zadanie:**
1. Zamień bloki opisane komentarzami w `TicketSummary.Describe` na metody z nazwami.
2. Poradź sobie z pętlą, która ma dwie wartości wyjściowe.
3. Oddziel obliczenia od składania tekstu.

**Podpowiedź:** jeśli IDE przy ekstrakcji proponuje parametr `out`, przeczytaj dokładnie, dlaczego. Rozwiązaniem nie jest `out`, tablica ani mutowalny "holder".
**Gotowe, gdy:** test jest zielony, `Describe` ma najwyżej trzy linie, a każda wydzielona metoda ma jedno wyjście (bez parametrów `out`/`ref`).

## Scena s26. Strategy, Command i State w jednej klasie - pulpit kasjera

**Namespace:** `Training.Workshop.M5.S26Variability.Start` · **Test:** `scripts/warsztat.sh --lang cs test m5/s26`

**Zasada:** Ten sam `switch` może kryć różne rodzaje zmienności, a mechanizm wybiera się po tym, kto wybiera wariant i kiedy. Wariant wybierany przez klienta z danych to Strategy, żądanie zapisywane w dzienniku i cofane to Command, a zachowanie zmieniające się wraz ze stanem obiektu to State.

**Zadanie:**
1. Wypisz trzy osie zmienności w `BoxOfficeDesk` i dla każdej odpowiedz: kto wybiera wariant i kiedy?
2. Wydziel cenę biletu według typu (N, S, E, C) jako strategię.
3. Zamień operacje BOOK, PAY, USE i CANCEL na polecenia z `Execute` i `Undo`, a rezerwacje i kasę wydziel do osobnego odbiorcy poleceń.
4. Przenieś cykl życia rezerwacji (New, Paid, Used, Cancelled) do obiektów stanu.

**Podpowiedź:** jedna oś naraz, test po każdym ruchu. CANCEL w Paid oddaje pieniądze, a w New tylko zwalnia rezerwację - to ta sama operacja z innym zachowaniem. Cofnięcie USE musi przywrócić status, którego zwykłe przejścia nie pozwalają osiągnąć. Strategia może być delegatem, ale enum C# nie ma metod - obiekty stanu to małe klasy. Status wypisuj wielkimi literami (`ToUpperInvariant()`), jak w `Start`.
**Gotowe, gdy:** test zielony, pulpit nie zawiera `switch` po statusie ani po typie biletu, polecenie nie jest współdzielone między wywołaniami, a stan rezerwacji nie zna cennika ani nazw operacji.

## Scena s27. Replace Inheritance with Delegation - raporty sprzedaży

**Namespace:** `Training.Workshop.M5.S27TemplateToStrategy.Start` · **Test:** `scripts/warsztat.sh --lang cs test m5/s27`

**Zasada:** Template Method dobrze obsługuje jeden zmienny krok w stabilnym szkielecie. Gdy haki niosą kilka niezależnych osi, każda kombinacja staje się podklasą, a kompozycja strategii zamienia iloczyn klas w sumę.

**Zadanie:**
1. Pogrupuj haki `ScreeningReport` według osi zmienności.
2. Wyjmij oś formatu do strategii i przekaż ją do szkieletu przez konstruktor.
3. Zrób to samo z osią okresu.
4. Zastąp cztery podklasy jedną klasą raportu `sealed` złożoną z dwóch strategii.

**Podpowiedź:** zanim usuniesz kopię kodu z podklasy, porównaj ją z drugą kopią. `Reports.Of` musi nadal sprawdzać najpierw okres, potem format. `Render` ma zostać niewirtualny - to odpowiednik `final` z Javy.
**Gotowe, gdy:** test zielony, nie ma podklas raportu, metoda `Render` istnieje w jednym miejscu, a nowy format można dodać jedną klasą działającą z każdym okresem.

## Scena s28. Replace Conditional with Chain of Responsibility - zatwierdzanie zwrotów

**Namespace:** `Training.Workshop.M5.S28Chain.Start` · **Test:** `scripts/warsztat.sh --lang cs test m5/s28`

**Zasada:** Chain of Responsibility zamienia uporządkowany łańcuch warunków w ogniwa, z których każde obsługuje żądanie albo przekazuje je dalej. Kolejność ogniw jest regułą biznesową, a koniec łańcucha musi dawać jawną decyzję.

**Zadanie:**
1. Przeczytaj test na granicach progów i znajdź przypadek, który zależy od kolejności warunków.
2. Wyjmij pierwszą gałąź jako ogniwo, uruchom test, potem kolejne.
3. Złóż łańcuch jawnie, tak żeby można było zbudować inną ścieżkę (np. festiwal bez zwrotów automatycznych) bez edycji `RefundApprover.Decide`.
4. Zdecyduj, czy audyt decyzji byłby kolejnym ogniwem, czy dekoratorem - i uzasadnij jednym zdaniem.

**Podpowiedź:** walidacja kwoty to warunek wstępny, a nie poziom zatwierdzania. Dokładnie 24 h przed seansem to jeszcze zwrot automatyczny, 23 h 59 min - już nie (uważaj na `TimeSpan.Hours`, które nigdy nie przekracza 23). Przejściowy kontrakt "obsłużyłem albo nie" może zwracać `RefundDecision?`.
**Gotowe, gdy:** test zielony, w `Decide` nie ma łańcucha `if/else`, kolejność ogniw widać w jednym miejscu, a ostatnie ogniwo zwraca decyzję zamiast `null` (typ wyniku ogniwa nie dopuszcza `null`).

## Scena s29. Introduce Boundary Object - odpowiedź terminala płatniczego

**Namespace:** `Training.Workshop.M5.S29BoundaryObject.Start` · **Test:** `scripts/warsztat.sh --lang cs test m5/s29`

**Zasada:** Boundary Object to niezmienny, zwalidowany obraz danych z zewnątrz w języku domeny - jeśli istnieje, jest poprawny. Adapter tłumaczy wywołanie obcego systemu, a obiekt graniczny jego dane; walidacja na granicy zmienia typ i moment błędu dla danych niepoprawnych.

**Zadanie:**
1. Rozbieraj odpowiedź terminala tylko raz, do obiektu potwierdzenia płatności z walidacją w konstruktorze.
2. Przenieś znajomość formatu `STATUS;KOD;KWOTA` i przeliczanie groszy do osobnego mappera.
3. Schowaj `LegacyTerminalClient` za portem domeny, tak żeby `PaymentService` znał tylko port i obiekt potwierdzenia.
4. Zapisz, jak zmienia się zachowanie dla odpowiedzi zniekształconych (np. `OK;;4500`).

**Podpowiedź:** potwierdzenie odmowy nie może autoryzować pieniędzy - wtedy suma autoryzacji nie musi sprawdzać statusu. Nie zmieniaj klasy terminala. Rekord pozycyjny z `init` dałby się obejść wyrażeniem `with` - walidację umieść w jawnym konstruktorze, a właściwości zostaw tylko do odczytu. `decimal.Parse` rzuca `FormatException`, a test oczekuje `ArgumentException`.
**Gotowe, gdy:** test zielony, `PaymentService` nie zawiera `Split` ani przeliczania groszy, a zniekształcona odpowiedź jest odrzucana w mapperze.

## Scena s30. Extract Facade - zakup biletów w sklepie i w kasie

**Namespace:** `Training.Workshop.M5.S30Facade.Start` · **Test:** `scripts/warsztat.sh --lang cs test m5/s30`

**Zasada:** Facade daje klientom jedną spójną operację zamiast znajomości kroków i ich kolejności, a ukrycie podsystemów sprawia, że żadnego kroku nie da się pominąć. Gdy skopiowane orkiestracje się rozjechały, przepięcie klienta na fasadę zmienia jego zachowanie - to trzeba nazwać i zatwierdzić osobno.

**Zadanie:**
1. Porównaj `OnlineShop.Buy` i `BoxOffice.Sell` linia po linii i zapisz każdą różnicę.
2. Wydziel orkiestrację zakupu ze sklepu do fasady; to, czym różnią się kanały, przekaż parametrem.
3. Przepnij kasę na fasadę i opisz, co się zmieniło w jej zachowaniu.
4. Ukryj podsystemy przed kanałami sprzedaży i zadbaj, żeby miejsca nie zostawały zajęte także przy awarii bramki płatności.

**Podpowiedź:** przeczytaj `S30BehaviourChangeTest`, zanim zaczniesz - to on mówi, która zmiana jest zamierzona. Karta `TIMEOUT` symuluje wyjątek bramki. Warsztat to jeden projekt, więc `internal` nie odetnie podsystemów od reszty kodu - granicę sprawdza test refleksji.
**Gotowe, gdy:** testy zielone, sklep i kasa zależą tylko od fasady, podsystemy nie są publiczne, a po każdej nieudanej płatności miejsca są wolne.

## Scena s31. Anti-Corruption Layer - licencje dystrybutora

**Namespace:** `Training.Workshop.M5.S31AntiCorruptionLayer.Start` · **Test:** `scripts/warsztat.sh --lang cs test m5/s31`

**Zasada:** Anti-Corruption Layer chroni język domeny przed obcym modelem: domena rozmawia z portem we własnych pojęciach, a tłumacz jako jedyny zna konwencje obcego systemu. Fasada tylko upraszcza dostęp, ACL tłumaczy jeden model na drugi.

**Zadanie:**
1. Wprowadź licencję filmu w języku kina (daty, kwoty, procent, aktywność) z niezmiennikami i regułami "obejmuje dzień" oraz "ile się należy".
2. Wydziel tłumaczenie rekordu dystrybutora do osobnej klasy.
3. Wprowadź port domeny zwracający licencję (brak licencji zadeklarowany typem `FilmLicense?`) i schowaj za nim klienta dystrybutora.
4. Zapisz, jak zmieniło się zachowanie dla rekordów z błędnymi danymi.

**Podpowiedź:** udział bywa zapisany jako `" 40 %"`, gwarancja to grosze w tekście, a daty mają format `yyyyMMdd`. Nie zmieniaj `DistributorFeed` ani `DistributorRecord`. `DistributorFeed` jest skompilowany z `#nullable disable`, więc kompilator nie ostrzeże, że `Fetch` zwraca `null` - zadeklaruj to sam w porcie.
**Gotowe, gdy:** test zielony, `SettlementService` nie używa żadnego typu dystrybutora, a jedyne miejsce znające kody statusu i formaty dat to tłumacz.

## Scena s32. Introduce Mediator - ekran wyboru miejsc

**Namespace:** `Training.Workshop.M5.S32Mediator.Start` · **Test:** `scripts/warsztat.sh --lang cs test m5/s32`

**Zasada:** Mediator skupia reguły współpracy komponentów w jednym miejscu: komponent zgłasza, co się stało, a mediator decyduje, kto i w jakiej kolejności reaguje. Logika samych komponentów zostaje w komponentach, inaczej mediator staje się boską klasą.

**Zadanie:**
1. Policz referencje między komponentami ekranu i znajdź wszystkie miejsca, które znają kolejność odświeżania.
2. Wprowadź interfejs mediatora i przenieś do niego reakcję na kliknięcie miejsca.
3. Przenieś pozostałe reakcje komponent po komponencie, a kolejność odświeżania zapisz w jednym miejscu.
4. Spraw, żeby żaden komponent nie miał referencji do innego komponentu.

**Podpowiedź:** płatność czyści koszyk, a czyszczenie koszyka samo zgłasza zdarzenie - uważaj na kaskadę. Przycisk pokazuje kwotę, więc suma musi być przeliczona przed nim. C# nie pozwala użyć `this` w inicjalizatorze pola, więc komponenty z mediatorem twórz w konstruktorze ekranu.
**Gotowe, gdy:** test zielony, każdy komponent zna tylko interfejs mediatora, a cena VIP i limit 4 miejsc nadal są liczone w swoich komponentach.
