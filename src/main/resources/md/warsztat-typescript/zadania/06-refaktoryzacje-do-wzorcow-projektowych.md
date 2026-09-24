# Moduł 6. Refaktoryzacje do wzorców projektowych - warsztat CineLegacy (TypeScript): zadania

Każda scena to mały fragment systemu kina CineLegacy z zapachem, który prowadzi do wzorca projektowego. Pracujesz w katalogu `start` danej sceny, np. `typescript/src/workshop/m6/s08_state/start`. Test sceny (vitest, `typescript/test/workshop/m6/sNN_.../`) przechodzi przez `start` i gotowe snapshoty `stepN` - Twoja wersja jest poprawna, gdy test jest zielony, a kod spełnia warunek projektowy z zadania.

Zasady pracy:

- Najpierw uruchom test i przeczytaj go - to on opisuje kontrakt (wyjątki, kolejność efektów, format wyniku). Nie zmieniaj testów.
- Małe kroki: jeden ruch w VS Code, test, następny ruch. Wzorzec wprowadzaj stopniowo, nie przepisuj klasy od zera.
- Vitest nie sprawdza typów. Gdy zadanie mówi o błędzie kompilacji (wyczerpujący `switch`, brak `add`, klasa bez `export`), uruchom też `npm run typecheck` w katalogu `typescript`.
- Nie zaglądaj do katalogów `stepN` przed końcem - to rozwiązania prowadzącego.
- Test woła scenę przez klienta (np. `PriceBoard`, `CancellationDesk`, `PaymentServices`) - gdy zmieniasz sygnatury, popraw też klienta, a nie test.
- Wprowadzenie wzorca to nadal refaktoryzacja. Jeśli chcesz zmienić zachowanie (nowa reguła, inna kolejność, inny komunikat), zapisz to jako osobną propozycję.

```bash
scripts/warsztat.sh --lang ts list m6          # sceny i kroki
scripts/warsztat.sh --lang ts test m6/s08      # testy sceny (wszystkie warianty)
scripts/warsztat.sh --lang ts reset m6/s08     # przywrócenie start do wersji z repozytorium
```

## Scena s01. Replace Conditional Logic with Strategy - polityka zniżek

**Katalog:** `typescript/src/workshop/m6/s01_strategy/start` · **Test:** `scripts/warsztat.sh --lang ts test m6/s01`

**Zasada:** Strategy wydziela wymienne warianty jednego obliczenia za wspólnym interfejsem, gdy wariant wybiera się niezależnie od klasy obiektu. Wspólna walidacja zostaje w kontekście, a moment wyboru wariantu jest częścią zachowania.

**Zadanie:**
1. Wprowadź interfejs `DiscountPolicy` i najpierw użyj go jako strategii przejściowej delegującej do starego kodu.
2. Przenieś każdy program zniżek (STANDARD, STUDENT_WEEK, PREMIERE) do osobnej strategii, po jednym.
3. Zastanów się, gdzie powinien zapaść wybór programu i co się zmieni, jeśli przeniesiesz go do konstruktora.

**Podpowiedź:** walidacja ceny i programu jest wspólna - niech zostanie w kontekście. Sprawdź w teście przypadek "PREMIERE nie sprawdza typu".
**Gotowe, gdy:** test zielony, `TicketPricer` nie zawiera łańcucha `if` po nazwie programu, a strategie są bezstanowe.

## Scena s02. Replace Conditional with Polymorphism - rodzaj seansu

**Katalog:** `typescript/src/workshop/m6/s02_polymorphism/start` · **Test:** `scripts/warsztat.sh --lang ts test m6/s02`

**Zasada:** Polimorfizm podtypów przenosi zachowanie zależne od trwałego rodzaju obiektu do osobnych typów, zamiast powtarzać `switch` po polu rodzaju. Najpierw trzeba znaleźć wszystkie miejsca, w których obiekty powstają.

**Zadanie:**
1. Znajdź miejsce tworzenia obiektów `Screening`.
2. Wydziel podtyp dla jednego rodzaju seansu, uruchom test, potem kolejne.
3. Usuń pole `kind` i nadaj każdemu rodzajowi dane o jednoznacznej nazwie zamiast `value`.

**Podpowiedź:** `switch` w miejscu tworzenia może zostać. Znikają te w `label`, `durationMinutes` i `price`. Podklasę trzymaj w tym samym pliku co klasa bazowa - w ESM cykl importów przy `extends` kończy się `ReferenceError`.
**Gotowe, gdy:** test zielony, zachowanie rodzajów jest w podtypach, a nieznany rodzaj w danych nadal daje ten sam wyjątek.

## Scena s03. Replace Type Code with Class - format jako typ

**Katalog:** `typescript/src/workshop/m6/s03_typecode/start` · **Test:** `scripts/warsztat.sh --lang ts test m6/s03`

**Zasada:** Replace Type Code with Class zastępuje surowy kod typem, który sam pilnuje poprawnych wartości i zna operacje swojego pojęcia. Kod zapisywany trwale to osobna sprawa - musi pozostać stabilny niezależnie od kształtu typu.

**Zadanie:**
1. Zastąp `formatCode: number` typem `Format`; zamieniaj kod na typ tuż po odczycie CSV.
2. Przenieś zachowanie zależne od formatu (etykieta, cena, okulary) do typu.
3. Wydziel mapowanie kodu trwałego do osobnego mappera.

**Podpowiedź:** plik CSV musi nadal zawierać `Diuna;3`. `enum` TypeScriptu nie ma pól - użyj klasy ze stałymi instancjami. Nie zapisuj pozycji w `values()`.
**Gotowe, gdy:** test zielony, żaden kod poza mapperem nie operuje na liczbach 1, 2, 3, a zapis CSV jest bez zmian.

## Scena s04. Encapsulate Classes with Factory - bilety

**Katalog:** `typescript/src/workshop/m6/s04_encapsulatefactory/start` · **Test:** `scripts/warsztat.sh --lang ts test m6/s04`

**Zasada:** Encapsulate Classes with Factory chowa klasy konkretne za fabryką, tak żeby klient znał tylko wspólny interfejs. Tworzenie przekierowuje się pojedynczo, a widoczność ogranicza dopiero wtedy, gdy wszyscy klienci już przeszli.

**Zadanie:**
1. Zastąp każde `new StandardTicket/VipTicket` w `BoxOffice` metodą tworzącą.
2. Przenieś regułę "który bilet dla którego rzędu" do fabryki.
3. Ogranicz widoczność klas konkretnych biletów.

**Podpowiedź:** TypeScript nie ma widoczności pakietowej - granicą jest moduł. Gdy przeniesiesz klasy biletów do pliku fabryki i odbierzesz im `export`, `npm run typecheck` pokaże wszystkie użycia spoza modułu.
**Gotowe, gdy:** test zielony (także `S04SolutionTest` sprawdzający widoczność w rozwiązaniu wzorcowym), a `BoxOffice` importuje tylko `Ticket` i fabrykę.

## Scena s05. Extract Factory Class - tworzenie rezerwacji

**Katalog:** `typescript/src/workshop/m6/s05_extractfactory/start` · **Test:** `scripts/warsztat.sh --lang ts test m6/s05`

**Zasada:** Extract Factory Class przenosi reguły tworzenia obiektu do osobnej klasy, gdy klasa, która je dziś zawiera, ma inną główną odpowiedzialność. Fabryka jest zwykłą zależnością, a kolejność operacji przy tworzeniu pozostaje częścią kontraktu.

**Zadanie:**
1. Usuń duplikację tworzenia rezerwacji między `reserve` i `reserveGroup`.
2. Wydziel klasę fabryki odpowiedzialną za numer, opłatę i termin ważności.
3. Przekaż fabrykę do serwisu jako zależność.

**Podpowiedź:** przeczytaj przypadek testowy o "spalonym" numerze - kolejność operacji jest częścią kontraktu. TypeScript nie ma przeciążonych konstruktorów: klienci tworzący serwis z zegarem muszą nadal działać.
**Gotowe, gdy:** test zielony, serwis nie zawiera reguł numeracji, opłat ani terminu ważności, a fabrykę da się przetestować osobno.

## Scena s06. Encapsulate Composite with Builder - repertuar dnia

**Katalog:** `typescript/src/workshop/m6/s06_builder/start` · **Test:** `scripts/warsztat.sh --lang ts test m6/s06`

**Zasada:** Builder dla struktury złożonej ukrywa tworzenie i łączenie węzłów za API opisującym kształt wyniku. Opłaca się, gdy budowa jest wieloetapowa i ma własne reguły, a gotowy wynik nie powinien się już zmieniać.

**Zadanie:**
1. Napisz builder repertuaru, dzięki któremu `WeekendPlanner` nie używa `new Hall` ani `add`.
2. Uczyń drzewo niemutowalnym po zbudowaniu.
3. Zdecyduj, czy builder może być użyty ponownie i co ma się stać przy drugim `build()`.

**Podpowiedź:** porównaj builder z "bieżącą salą" i builder z funkcją strzałkową dla każdej sali - który mniej ukrywa? `readonly` w typie nie zatrzyma `push` w runtime - do tego służy `Object.freeze`.
**Gotowe, gdy:** test zielony, kształt kodu planera odpowiada kształtowi repertuaru, a zbudowanych tablic nie da się zmodyfikować.

## Scena s07. Move Embellishment to Decorator - dodatki do biletu

**Katalog:** `typescript/src/workshop/m6/s07_decorator/start` · **Test:** `scripts/warsztat.sh --lang ts test m6/s07`

**Zasada:** Decorator owija rdzeń obiektem o tym samym kontrakcie, który dokłada opcjonalne zachowanie i deleguje resztę do środka. Kolejność owijania jest częścią zachowania, a udekorowany obiekt nie jest przezroczysty dla pytań o typ i tożsamość.

**Zadanie:**
1. Wydziel wspólny interfejs biletu i miejsce składania biletu.
2. Przenoś dodatki (VIP, okulary 3D, ubezpieczenie) do dekoratorów po jednym, usuwając flagi z rdzenia.
3. Sprawdź, co zwraca `instanceof` i porównanie strukturalne (`toStrictEqual`) dla udekorowanego biletu.

**Podpowiedź:** opis biletu ma ustaloną kolejność dodatków. Od którego dodatku zacząć, żeby jej nie zmienić?
**Gotowe, gdy:** test zielony, rdzeń biletu nie ma żadnej flagi dodatku, a kolejność owijania jest w jednym miejscu.

## Scena s08. Replace State-Altering Conditionals with State - status rezerwacji

**Katalog:** `typescript/src/workshop/m6/s08_state/start` · **Test:** `scripts/warsztat.sh --lang ts test m6/s08`

**Zasada:** State przenosi zachowanie zależne od bieżącego stanu do obiektów stanów, a kontekst tylko do nich deleguje. Tabelę przejść ustala się przed budową hierarchii, a niedozwolone przejście nadal musi kończyć się tym samym błędem.

**Zadanie:**
1. Przeczytaj tabelę przejść w teście i porównaj ją z kodem.
2. Zastąp pole `currentStatus` obiektem stanu.
3. Przenoś przejścia (`pay`, `use`, `expire`, `cancel`) do stanów po jednym; niedozwolone przejście ma rzucać ten sam wyjątek.

**Podpowiedź:** gdy bramka płatności zawiedzie, rezerwacja zostaje NEW. Zachowaj kolejność: obciążenie, zmiana stanu, efekt. Interfejs TypeScriptu nie ma metod domyślnych - domyślne "przejście rzucające wyjątek" musi dać funkcja tworząca stan albo klasa bazowa.
**Gotowe, gdy:** wszystkie 84 testy zielone, a metody `Reservation` tylko delegują do stanu.

## Scena s09. Replace Hard-coded Notifications with Observer - po opłaceniu

**Katalog:** `typescript/src/workshop/m6/s09_observer/start` · **Test:** `scripts/warsztat.sh --lang ts test m6/s09`

**Zasada:** Observer sprawia, że źródło zdarzenia zna tylko wspólny interfejs odbiorców, a nie ich konkretne klasy. Kolejność powiadomień i polityka błędów to kontrakt, którego refaktoryzacja nie zmienia.

**Zadanie:**
1. Wprowadź zdarzenie "rezerwacja opłacona" i jedno miejsce powiadamiania.
2. Zamień maila, SMS i punkty lojalnościowe na obserwatorów wspólnego interfejsu.
3. Umożliw rejestrowanie i wyrejestrowanie odbiorców, a składanie standardowego zestawu przenieś do `PaymentServices`.

**Podpowiedź:** wyjątek w SMS przerywa naliczanie punktów - to obecny kontrakt, nie błąd do naprawy w tej refaktoryzacji. Odbiorcy są wołani synchronicznie - nie wprowadzaj `async`.
**Gotowe, gdy:** test zielony (kolejność i awarie), a serwis nie importuje `Mailer`, `SmsGateway` ani `LoyaltyProgram`.

## Scena s10. Replace Implicit Tree with Composite - zestawy baru

**Katalog:** `typescript/src/workshop/m6/s10_implicittree/start` · **Test:** `scripts/warsztat.sh --lang ts test m6/s10`

**Zasada:** Composite zastępuje drzewo ukryte w konwencji danych jawnymi typami liścia i węzła o wspólnym kontrakcie. Operacje przenosi się po jednej, a stary format tłumaczy mapper, który zachowuje dotychczasowe błędy.

**Zadanie:**
1. Zaprojektuj jawny Composite zestawu (produkt i zestaw) oraz mapper z zagnieżdżonych tablic.
2. Przenieś `price`, a potem `render` na Composite - po jednej operacji.
3. Usuń stary kod ze sprawdzaniem typu elementu (`typeof`, `Array.isArray`).

**Podpowiedź:** komunikaty błędów dla złych danych też są kontraktem. Test różnicowy porównuje wynik `start` z rozwiązaniem wzorcowym na 500 losowych drzewach.
**Gotowe, gdy:** testy równoważności i różnicowy zielone, a `BarMenu` nie sprawdza typów elementów ani nie zawiera asercji typu.

## Scena s11. Transparent vs Safe Composite - add() na liściu

**Katalog:** `typescript/src/workshop/m6/s11_safecomposite/start` · **Test:** `scripts/warsztat.sh --lang ts test m6/s11`

**Zasada:** Composite może trzymać zarządzanie dziećmi we wspólnym typie (Transparent) albo tylko w węźle (Safe). Pierwszy wariant upraszcza klienta kosztem błędów w runtime, drugi przenosi te błędy do kompilacji.

**Zadanie:**
1. Spraw, żeby wywołanie `add` na produkcie było błędem kompilacji, a nie wyjątkiem w runtime.
2. Popraw typy w `ComboCatalog` tam, gdzie dodajesz elementy.
3. Rozważ wersję, w której zestaw jest niemutowalny i `add` nie istnieje wcale.

**Podpowiedź:** VS Code nie ma Push Members Down - przenieś metody do podklasy ręcznie i pozwól `npm run typecheck` wskazać miejsca do poprawy. Nie ratuj się asercją `as Combo`.
**Gotowe, gdy:** test zielony, a wspólny typ `MenuComponent` nie ma metod `add` ani `children`.

## Scena s12. Replace One/Many Distinctions with Composite - zwroty

**Katalog:** `typescript/src/workshop/m6/s12_onemany/start` · **Test:** `scripts/warsztat.sh --lang ts test m6/s12`

**Zasada:** Composite pozwala traktować jeden element i grupę elementów przez ten sam kontrakt, dzięki czemu API nie potrzebuje osobnych ścieżek "jeden" i "wiele". Efekty naliczane raz na wywołanie muszą przy tym pozostać naliczane raz.

**Zadanie:**
1. Usuń duplikację reguły zwrotu między `refund` i `refundAll`.
2. Wprowadź wspólny kontrakt dla jednego biletu i grupy biletów oraz jedną metodę `refund`.
3. Oznacz stare metody jako przestarzałe (`@deprecated`), a potem usuń je.

**Podpowiedź:** potrącenie 3.00 jest naliczane raz na zwrot, nie raz na bilet. TypeScript nie ma przeciążeń metod - stara `refund(ticket, now)` musi na czas przejścia dostać inną nazwę.
**Gotowe, gdy:** test zielony, reguła zwrotu jest w jednym miejscu, a ani `RefundService`, ani klient `CancellationDesk` nie rozróżniają jednego biletu i wielu.

## Scena s13. Extract Composite - kontenery programu

**Katalog:** `typescript/src/workshop/m6/s13_extractcomposite/start` · **Test:** `scripts/warsztat.sh --lang ts test m6/s13`

**Zasada:** Extract Composite przenosi powieloną obsługę dzieci z kilku klas kontenerów do wspólnej nadklasy. Podciąga się tylko to, co ma ten sam kontrakt, a nie wszystko, co wygląda podobnie.

**Zadanie:**
1. Wydziel wspólną nadklasę dla `Marathon` i `ShortsBlock` z obsługą dzieci.
2. Podciągnij do niej to, co naprawdę jest wspólne; zostaw w podklasach to, co się różni.

**Podpowiedź:** obie klasy sumują minuty, ale tylko maraton dolicza przerwy.
**Gotowe, gdy:** test zielony, lista dzieci i `add` istnieją w jednym miejscu, a podklasy mają po kilkanaście linii.

## Scena s14. Unify Interfaces with Adapter - dwie bramki płatności

**Katalog:** `typescript/src/workshop/m6/s14_adapter/start` · **Test:** `scripts/warsztat.sh --lang ts test m6/s14`

**Zasada:** Adapter tłumaczy obcy interfejs na kontrakt, którego oczekuje klient, tak żeby klient nie znał szczegółów zewnętrznej biblioteki. Tłumaczyć trzeba nie tylko nazwy, ale też jednostki, formaty i sposób zgłaszania błędów.

**Zadanie:**
1. Wydziel obsługę każdej bramki do metody o tej samej sygnaturze.
2. Zdefiniuj preferowany interfejs płatności kina i dwa adaptery.
3. Spraw, żeby `CheckoutService` zależał wyłącznie od tego interfejsu.

**Podpowiedź:** stara bramka liczy w groszach i zwraca odmowę w XML, nowa liczy w złotych i zgłasza odmowę wyjątkiem. Nie zmieniaj klas bramek. Stary sposób tworzenia serwisu (`new CheckoutService(xml, rest)`) ma nadal działać.
**Gotowe, gdy:** test zielony, a metoda `pay` w `CheckoutService` korzysta wyłącznie z preferowanego interfejsu (szczegóły XML i REST są tylko w adapterach).

## Scena s15. Replace Conditional Dispatcher with Command - konsola kasjera

**Katalog:** `typescript/src/workshop/m6/s15_command/start` · **Test:** `scripts/warsztat.sh --lang ts test m6/s15`

**Zasada:** Command zamienia każdą gałąź dyspozytora w osobny obiekt o wspólnym interfejsie, a wybór gałęzi w wyszukanie w rejestrze. Rejestr jest równoważny warunkom tylko przy rozłącznych kluczach i zachowanej normalizacji.

**Zadanie:**
1. Wydziel ciało każdej gałęzi do metody o wspólnej sygnaturze.
2. Zamień gałęzie na obiekty komend; stan kasy wydziel z konsoli.
3. Zastąp łańcuch `if` rejestrem komend.

**Podpowiedź:** `sell` i `SELL` działają tak samo - nie zgub normalizacji klucza.
**Gotowe, gdy:** test zielony, komendy są bezstanowe, a dodanie komendy nie wymaga zmiany metody `handle`.

## Scena s16. Form Template Method - raporty CSV i HTML

**Katalog:** `typescript/src/workshop/m6/s16_templatemethod/start` · **Test:** `scripts/warsztat.sh --lang ts test m6/s16`

**Zasada:** Template Method zapisuje stałą sekwencję kroków raz w nadklasie, a podklasy dostarczają tylko kroki, które się różnią. Najpierw doprowadza się podobne metody do identycznej postaci, dopiero potem je podciąga.

**Zadanie:**
1. Doprowadź `render` obu raportów do identycznej postaci, wydzielając różnice do metod o tych samych nazwach.
2. Przenieś wspólny szkielet do nadklasy i zabezpiecz go przed nadpisaniem.

**Podpowiedź:** różnice to nagłówek, wiersz (z escapowaniem) i stopka. TypeScript nie ma `final` - zastanów się, jak inaczej odrzucić podklasę nadpisującą `render`.
**Gotowe, gdy:** test zielony, sortowanie i sumowanie są w jednym miejscu, a raporty zawierają tylko formatowanie.

## Scena s17. Limit Instantiation with Singleton - cennik

**Katalog:** `typescript/src/workshop/m6/s17_singleton/start` · **Test:** `scripts/warsztat.sh --lang ts test m6/s17`

**Zasada:** Singleton ogranicza tworzenie obiektu do jednej instancji i jest decyzją o cyklu życia, bezpieczną tylko dla obiektów niemutowalnych i równoważnych. Globalny dostęp do tej instancji ukrywa zależności i utrudnia testy.

**Zadanie:**
1. Sprawdź w teście, ile cenników powstaje przy trzech wycenach, i uzasadnij, dlaczego jedna instancja jest bezpieczna.
2. Ogranicz tworzenie cennika do jednej instancji.
3. Spraw, żeby `TicketDesk` dało się przetestować z innym cennikiem bez globalnego stanu.

**Podpowiedź:** porównaj klasyczne `getInstance()` z zamrożoną stałą modułu, a potem zastanów się, kto powinien decydować o cyklu życia.
**Gotowe, gdy:** test zielony, cennik jest tworzony raz, a `TicketDesk` przyjmuje cennik przez konstruktor.

## Scena s18. Move Accumulation to Collecting Parameter - ostrzeżenia walidacji

**Katalog:** `typescript/src/workshop/m6/s18_collectingparameter/start` · **Test:** `scripts/warsztat.sh --lang ts test m6/s18`

**Zasada:** Collecting Parameter przekazuje do metod wspólny akumulator, do którego dopisują one swoje wyniki, zamiast zwracać fragmenty do sklejenia. Akumulator należy do wywołującego i powinien mieć wąski typ.

**Zadanie:**
1. Zastąp sklejanie ostrzeżeń w łańcuchu tablicą.
2. Przekaż akumulator do metod pomocniczych zamiast zwracać z nich fragmenty.
3. Nadaj akumulatorowi wąski typ, który pozwala tylko dopisywać.

**Podpowiedź:** separator `'; '` powinien pojawiać się w kodzie dokładnie raz.
**Gotowe, gdy:** test zielony, metody `check...` zwracają `void`, a format wyniku jest ustalany w jednym miejscu.

## Scena s19. Visitor i macierz zmian - pozycje zamówienia

**Katalog:** `typescript/src/workshop/m6/s19_visitor/start` · **Test:** `scripts/warsztat.sh --lang ts test m6/s19`

**Zasada:** Visitor wydziela operacje na strukturze do osobnych klas, a element sam wybiera właściwą metodę odwiedzającego. Tanio dodaje się w nim nowe operacje, drogo nowe rodzaje elementów.

**Zadanie:**
1. Zamień trzy łańcuchy `instanceof` na klasyczny Visitor.
2. Przygotuj alternatywę: zamknięta unia dyskryminowana pozycji (pole `kind`) i `switch` zakończony `default: return assertNever(item)`.
3. Wypełnij macierz: ile plików zmienisz przy nowej operacji, a ile przy nowym rodzaju pozycji - w obu wersjach.

**Podpowiedź:** VAT liczymy od ceny brutto każdej pozycji osobno i dopiero potem sumujemy. Zwykły `default` bez `assertNever` wyłącza kontrolę kompilatora.
**Gotowe, gdy:** test zielony dla wybranej wersji, w kodzie nie ma `instanceof`, a dodanie typu pozycji bez obsługi kończy się błędem kompilacji (`npm run typecheck`).

## Scena s20. Mapa decyzji - jedna tabela, dwie struktury

**Katalog:** `typescript/src/workshop/m6/s20_decisionmap/start` · **Test:** `scripts/warsztat.sh --lang ts test m6/s20`

**Zasada:** Wzorzec dobiera się do rodzaju zmienności, a nie do wyglądu kodu: punkt rozszerzenia powinien leżeć na osi, która zmienia się najczęściej. Gdy nie wiadomo, która to oś, prostsza struktura bez nowych typów jest bezpieczniejsza.

**Zadanie:**
1. Rozplącz tabelę cen na dwie niezależne reguły.
2. Zaimplementuj wariant A: marketing często zmienia reguły dniowe.
3. Od stanu po punkcie 1 zaimplementuj wariant B: często dochodzą nowe formaty.
4. Zapisz jednym zdaniem, po czym poznasz w projekcie, który wariant wybrać.

**Podpowiedź:** zrób commit (albo kopię) po punkcie 1, żeby móc wrócić i zbudować drugi wariant.
**Gotowe, gdy:** test zielony dla obu wariantów, a uzasadnienie wyboru odwołuje się do tego, co się zmienia, nie do wyglądu kodu.
