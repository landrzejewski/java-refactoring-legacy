# Moduł 6. Ćwiczenia utrwalające: gdzie i jaki wzorzec - warsztat CineLegacy: zadania

Dziesięć ćwiczeń na kodzie kina CineLegacy. W odróżnieniu od scen modułu 5 tytuł ćwiczenia **nie mówi, który wzorzec wprowadzić**. Każde ćwiczenie to jeden fragment systemu z dwoma albo trzema niezależnymi problemami projektowymi. Twoim zadaniem jest rozpoznać, co się w tym kodzie zmienia (albo co chciałoby się zmieniać), dobrać do każdego problemu właściwy mechanizm i wprowadzić go bez zmiany obserwowalnego zachowania. Razem ćwiczenia pokrywają wszystkie 23 wzorce GoF, ale nie wszystkie problemy mają tę samą wagę - część z nich to również pytanie, czy wzorzec w ogóle się opłaca.

Pracujesz w pakiecie `start` danego ćwiczenia, np. `pl.training.workshop.exercises.e03_notifications.start`. Test ćwiczenia przechodzi przez `start` i gotowe snapshoty `stepN` (po jednym kroku na każdy rozpoznany problem) - Twoja wersja jest poprawna, gdy test jest zielony, a kod spełnia warunek projektowy z sekcji "Gotowe, gdy".

Zasady pracy:

- Zacznij od testu równoważności `EnnEquivalenceTest` - to on opisuje kontrakt (wyjątki, kolejność efektów, format wyniku). Nie zmieniaj testów równoważności. Test `EnnSolutionTest` opisuje warunki, które ma spełnić rozwiązanie wzorcowe; na początku będzie czerwony albo nie skompiluje się dla Twojej wersji i to jest w porządku.
- Zanim cokolwiek zmienisz, odpowiedz na pytania z mapy decyzji ze slajdów (18.1-18.3): co się zmienia? kto wybiera wariant i kiedy? ile osi zmienności jest w tej klasie? Dopiero potem wybierz mechanizm. Nazwij go dla siebie, zanim zaczniesz pisać kod, i sprawdź po wszystkim, czy rozwiązanie w przewodniku dobrało ten sam.
- Jeden problem naraz: wprowadź mechanizm dla pierwszego zapachu, uruchom test, dopiero potem bierz następny. Małe kroki, najlepiej automatycznymi refaktoryzacjami IDE.
- Nie zaglądaj do pakietów `stepN` ani do przewodnika przed końcem - to rozwiązania prowadzącego.
- Test woła ćwiczenie przez stabilny punkt wejścia (klient albo korzeń kompozycji). Gdy zmieniasz sygnatury, popraw klienta, a nie test.
- Wprowadzenie wzorca to nadal refaktoryzacja. Jeśli po drodze znajdziesz błąd w starym kodzie, zapisz go jako osobną propozycję. Tam, gdzie zmiana zachowania jest zamierzona, mówi o tym osobny test i treść ćwiczenia.

```bash
scripts/warsztat.sh list exercises        # ćwiczenia i kroki
scripts/warsztat.sh test exercises/e03    # testy ćwiczenia (wszystkie warianty)
scripts/warsztat.sh reset exercises/e03   # przywrócenie start do wersji z repozytorium
```

Po skończeniu wszystkich ćwiczeń porównaj swoje decyzje z mapą 23 wzorców na końcu przewodnika: dla każdego wzorca zapisano tam sygnał w kodzie, który do niego prowadził, oraz scenę modułu 5, w której ten sam wzorzec był pokazywany krok po kroku.

## Ćwiczenie e01. Import repertuaru z plików CSV i JSON Lines

**Pakiet:** `pl.training.workshop.exercises.e01_import.start` · **Test:** `scripts/warsztat.sh test exercises/e01`

**Co widzimy:** Nocne zadanie `RepertoireImport.load` wczytuje repertuar z pliku dystrybutora i drukuje raport. `RepertoireImporter` ma dwie metody, `importCsv` i `importJsonLines`, które powstały przez skopiowanie: ta sama pętla po wierszach, to samo numerowanie wierszy do komunikatów, pomijanie pustych linii i komentarzy, ta sama walidacja (`toEntry`) i to samo sortowanie. Różni się tylko sposób rozbioru jednego wiersza na cztery pola (podział po `;` albo wyciągnięcie pól z obiektu JSON) i znak komentarza (`#` albo `//`). Rozszerzenie pliku jest sprawdzane dwa razy: klient rozpoznaje je, żeby podpisać raport (`[CSV]`, `[JSONL]`), a importer jeszcze raz, żeby wybrać metodę. Dystrybutor zapowiada trzeci format (XML).

**Zadanie:**
1. Doprowadź obie metody importu do identycznej postaci, wydzielając różnice do metod o tych samych nazwach, a potem zostaw wspólny przebieg importu w jednym miejscu, którego żaden format nie może zmienić.
2. Spraw, żeby krok "rozbierz wiersz na pola" był osobnym obiektem, który da się przetestować bez uruchamiania całego importu, a każdy importer sam decydował, jaki obiekt rozbioru tworzy.
3. Zostaw dokładnie jedno miejsce, które zna rozszerzenia plików; klient ma dostać podpis raportu od importera, a nie zgadywać go z nazwy pliku.
4. Policz, ile plików trzeba dodać i ile zmienić, żeby obsłużyć XML przed zmianą i po zmianie.

**Pytania naprowadzające:** Co jest stałe dla wszystkich formatów, a co zależy od formatu? Czy zmienny krok to tylko "inny kawałek kodu", czy obiekt, który ma sens także poza importem (podgląd wiersza w edytorze, test jednostkowy)? Kto dziś wie, że `.csv` to CSV, i ile razy?

**Gotowe, gdy:** test zielony, pętla, walidacja i sortowanie istnieją w jednym miejscu i są chronione przed nadpisaniem, rozbiór wiersza danego formatu da się przetestować osobno, klasa importera konkretnego formatu ma najwyżej dwie krótkie metody, a rozszerzenia plików zna jedna klasa.

## Ćwiczenie e02. Mapa sali: sektory, rzędy, miejsca

**Pakiet:** `pl.training.workshop.exercises.e02_hallmap.start` · **Test:** `scripts/warsztat.sh test exercises/e02`

**Co widzimy:** `HallMaps.describe` opisuje salę: pojemność, sumę dopłat, liczbę miejsc w każdej kategorii i listę miejsc w kolejności sektorów i rzędów. Plan sali przychodzi jako `HallSpec` (sektory, rzędy, układ rzędu jak `"NN_VV"`, gdzie `_` to przejście). Zapachy:

- `describe` wykonuje **cztery razy tę samą potrójną pętlę** sala, sektor, rząd, miejsce i w każdej z nich sam pomija luki (`if (seat == null) continue`). Pojemność, dopłaty, zliczanie i lista różnią się tylko jedną linijką w środku.
- Klient zna wszystkie poziomy sali. Gdy pojawi się balkon podzielony na własne sektory albo loża bez rzędów, trzeba poprawić każdą z czterech pętli. Gdy pojawi się nowe pytanie o salę (np. "ile miejsc jest w rzędach od 10 wzwyż"), powstaje piąta kopia.
- `Row.of` tworzy każde miejsce przez `switch` po kodzie i **kopiuje do każdego fotela nazwę, dopłatę, ikonę i opis kategorii**. Sala IMAX na 400 miejsc ma 400 kopii tych samych czterech zestawów danych, a zmiana opisu kategorii to zmiana w konstrukcji każdego miejsca.

**Zadanie:**
1. Spraw, żeby pojemność i sumę dopłat dało się policzyć jednym wywołaniem na sali, a tę samą metodę dało się wywołać na sektorze, rzędzie i pojedynczym miejscu. Nowy poziom (np. balkon z sektorami) nie może wymagać zmiany w kliencie.
2. Spraw, żeby po wszystkich miejscach sali dało się przejść zwykłą pętlą `for-each`, w dotychczasowej kolejności, bez znajomości poziomów i bez sprawdzania luk w kliencie. `HallMaps` ma nie importować `Section` ani `Row`.
3. Spraw, żeby dane kategorii (nazwa, dopłata, ikona, opis) istniały w **jednej instancji na kategorię**, a miejsce trzymało tylko to, co jest jego własne (etykietę z numerem) i odwołanie do kategorii. Dwa fotele VIP mają wskazywać na ten sam obiekt.
4. Po każdym kroku uruchom test równoważności; opis sali ma być identyczny co do znaku, łącznie z komunikatami błędów.

**Pytania naprowadzające:**
- Które z czterech pytań o salę są **agregatami**, które da się policzyć rekurencyjnie z odpowiedzi dzieci, a które potrzebują kolejnych miejsc po kolei? Czy to ta sama odpowiedzialność?
- Kto powinien wiedzieć, że luka nie jest miejscem: klient, rząd czy struktura danych? Czy `null` w tablicy to dobra reprezentacja przejścia?
- Co w opisie fotela zmienia się między dwoma fotelami tej samej kategorii, a co jest identyczne? Czy wspólna część jest niemutowalna, czyli bezpieczna do współdzielenia?
- Gdzie powinien zapadać wybór "jaki obiekt dla kodu `V`", żeby nieznany kod nadal dawał błąd `unknown seat category: X`?

**Gotowe, gdy:** test zielony, `HallMaps.describe` nie zawiera zagnieżdżonych pętli ani `null`-i, `HallMaps` importuje tylko `Hall` i `Seat`, pojemność sali, sektora i rzędu liczy ta sama metoda, a test rozwiązania (`E02SolutionTest`) potwierdza, że 400 miejsc dzieli najwyżej cztery obiekty kategorii.

## Ćwiczenie e03. Zmiana seansu i powiadomienia klientów

**Pakiet:** `pl.training.workshop.exercises.e03_notifications.start` · **Test:** `scripts/warsztat.sh test exercises/e03`

**Co widzimy:** `ScreeningScheduler` przenosi albo odwołuje seans i sam wie, co ma się potem wydarzyć: po kolei woła powiadomienia klientów, listę oczekujących i statystyki. Powiadomienia to sześć klas (`EmailMovedNotice`, `SmsMovedNotice`, `PushMovedNotice` i trzy odpowiedniki dla odwołania), w których treść wiadomości jest skopiowana trzy razy, a skracanie SMS do 70 znaków dwa razy. O tym, którym kanałem powiadomić klienta, rozstrzyga ten sam blok `if` (ważny token push, potem telefon, potem e-mail, na końcu wpis `UNDELIVERED`) wklejony do `move` i do `cancel`. Test równoważności porównuje cały dziennik: wpis repertuaru, powiadomienia w kolejności klientów, reakcję listy oczekujących i statystyki.

**Zadanie:**

1. Spraw, żeby `ScreeningScheduler` nie znał ani jednej konkretnej klasy odbiorcy zmian. Dodanie nowego odbiorcy (np. kasy, która blokuje sprzedaż) ma być rejestracją w jednym miejscu, bez edycji schedulera, a kolejność efektów ma zostać taka jak dziś.
2. Zlikwiduj siatkę sześciu klas powiadomień. Nowy rodzaj wiadomości (np. zmiana sali) i nowy kanał (np. komunikator) mają kosztować po jednej klasie, a limit długości SMS ma istnieć w kodzie dokładnie raz.
3. Zastąp powtórzony blok wyboru kanału strukturą, w której kolejność prób (push, SMS, e-mail) widać w jednym miejscu i da się złożyć inną ścieżkę, np. "tylko e-mail" dla festiwalu, bez zmiany klas powiadamiających. Wpis `UNDELIVERED` ma być jawnym końcem tej struktury, a nie ostatnim `else`.
4. Zapisz jednym zdaniem, czym różnią się trzy struktury, które powstały: która decyduje "kto reaguje", która "co i którędy wysłać", a która "kto pierwszy spróbuje".

**Pytania naprowadzające:**

- Kto wybiera wariant i kiedy? Odbiorców zmiany konfiguruje kino przy starcie, kanał wybiera się per klient w czasie wysyłki, a rodzaj wiadomości wynika ze zdarzenia. To trzy różne osie, czy jedna?
- Które z sześciu klas różnią się treścią, a które sposobem dostarczenia? Czy da się je rozdzielić tak, żeby liczba klas była sumą, a nie iloczynem?
- Czy w wyborze kanału kolejność jest regułą biznesową? Co się stanie, jeśli ktoś zamieni dwa pierwsze warunki miejscami?
- Co powinien dostać odbiorca zmiany: scheduler, czy tylko dane o tym, co się stało?

**Gotowe, gdy:** test zielony, `ScreeningScheduler` nie importuje żadnej klasy powiadomień ani `Waitlist` i `ChangeStats`, w pakiecie nie ma klasy łączącej w nazwie kanał z rodzajem wiadomości, tekst `"expired-"` i limit 70 znaków występują w kodzie po jednym razie, a `ScreeningDesk` składa zarówno zestaw odbiorców, jak i ścieżkę kanałów w jednym miejscu.

## Ćwiczenie e04. Koszyk miejsc z cofaniem

**Pakiet:** `pl.training.workshop.exercises.e04_seatpicker.start` · **Test:** `scripts/warsztat.sh test exercises/e04`

**Co widzimy:** `SeatPicker` obsługuje skrypt kasjera: SELECT, DESELECT, SWAP, GLASSES, VOUCHER, CLEAR, AUTO i UNDO. Metoda `apply` ma `switch` po nazwie operacji, a `undo` drugi łańcuch `if` z odwrotnością każdej z nich. Historia to stos rekordów `Change` z siedmioma polami, z których każda operacja używa dwóch albo trzech. Operacja AUTO czyści koszyk i proponuje miejsca obok siebie; zanim to zrobi, kopiuje pola koszyka do zmiennych lokalnych, żeby przy niepowodzeniu je przywrócić. Kopia powstała, gdy koszyk miał dwa pola (miejsca i okulary); trzecie (voucher) doszło później i do kopii nie trafiło - po nieudanym AUTO i po cofnięciu AUTO voucher znika. Klientem jest `SeatPickerSession.run(script)`, który zwraca zawartość koszyka po skrypcie albo komunikat błędu i zawartość po błędzie.

**Zadanie:**
1. Spraw, żeby dodanie nowej operacji kasjera (np. NOTE z komentarzem do zamówienia) nie wymagało zmian w `undo` ani w rekordzie `Change`: operacja i jej odwrotność mają mieszkać razem, a każda linia skryptu ma dostać własny obiekt z własnymi danymi do cofnięcia.
2. Oddziel zawartość koszyka (miejsca, okulary, voucher, walidacja, suma) od tłumaczenia linii skryptu na operacje i od historii cofania.
3. Zastąp ręczne kopiowanie pól przed AUTO mechanizmem, który obejmuje cały koszyk i nie wymaga, żeby ktokolwiek poza koszykiem znał jego pola. Nowe pole koszyka ma być dopisywane w jednym miejscu.
4. Zdecyduj, czy ten sam mechanizm ma obsłużyć cofanie wszystkich operacji, czy tylko AUTO, i uzasadnij to jednym zdaniem.

**Pytania naprowadzające:** Kto wie, jak cofnąć SELECT: koszyk czy ten, kto SELECT wykonał? Czy dane potrzebne do cofnięcia (pozycja usuniętego miejsca, poprzedni voucher) należą do wspólnej historii, czy do konkretnej operacji? Co musi wiedzieć klasa, która kopiuje pola koszyka, i co się stanie, gdy pól przybędzie? Czym różni się „odwróć ostatnią zmianę” od „wróć do zapisanego punktu”?

**Podpowiedź:** test równoważności celowo nie zawiera skryptów łączących VOUCHER z AUTO. W `E04SolutionTest` zobaczysz, że w `start` voucher ginie, a w rozwiązaniu wzorcowym jest przywracany; to świadoma naprawa opisana osobnym testem, nie regresja. Reszta zachowania (komunikaty, kolejność miejsc po cofnięciu DESELECT i SWAP, zawartość koszyka po błędzie) musi zostać bez zmian.

**Gotowe, gdy:** test zielony, cofanie nie zawiera łańcucha `if` po nazwie operacji, klasa z zawartością koszyka nie zna nazw operacji skryptu, a żadna klasa poza koszykiem nie kopiuje jego pól.

## Ćwiczenie e05. Karta podarunkowa i reguły voucherów

**Pakiet:** `pl.training.workshop.exercises.e05_giftcard.start` · **Test:** `scripts/warsztat.sh test exercises/e05`

**Co widzimy:** Dwa niezależne fragmenty kodu kasy.

1. `GiftCard` trzyma status karty w polu tekstowym (`ISSUED`, `ACTIVE`, `EMPTY`, `BLOCKED`, `EXPIRED`). Każda z metod `activate`, `charge`, `block` i `expire` zaczyna się od `switch` po tym polu, więc tabela "co wolno w jakim statusie" jest rozcięta na cztery metody. Komunikat `card is blocked` występuje w trzech miejscach, a dopisanie statusu "zawieszona" oznacza zmianę w każdej metodzie. Przejście do `EMPTY` jest ukryte w środku `charge`.
2. `Vouchers` dostaje regułę ważności vouchera jako tekst z konfiguracji, np. `format=3D AND day IN MON,TUE AND NOT premiere`. Metoda `applies` rozbiera ten tekst `split`-em, żeby ocenić regułę dla seansu, a `describe` rozbiera go drugi raz, żeby zbudować opis dla kasjera. Oba rozbiory muszą się zgadzać co do składni i komunikatów błędów, a każde nowe pojęcie (np. godzina seansu) trzeba dopisać w obu metodach.

Klientami są `GiftCards.simulate` (skrypt operacji kasy) i `Vouchers.applies` / `Vouchers.describe`. Test równoważności to pełna tabela: każdy status razy każda operacja, walidacja kwoty przed sprawdzeniem statusu, każde pojęcie reguły, negacja, błędy składni i moment, w którym pada błąd.

**Zadanie:**
1. Przeczytaj test i spisz tabelę przejść karty: wiersz to status, kolumna to operacja, komórka to nowy status albo komunikat błędu. Zwróć uwagę, które sprawdzenia nie zależą od statusu.
2. Przebuduj `GiftCard` tak, żeby każdy wiersz tej tabeli był w jednym miejscu, a metody karty nie zawierały `switch` po statusie. Dodanie statusu "zawieszona" ma być nowym plikiem, nie zmianą w czterech metodach.
3. Przebuduj `Vouchers` tak, żeby tekst reguły był rozbierany dokładnie raz, w jednym miejscu, a ocena i opis działały na tym samym wyniku rozbioru. Nowe pojęcie w regule ma być nową klasą plus jedną gałęzią w miejscu rozbioru.
4. Sprawdź, czy da się złożyć w kodzie regułę "IMAX lub premiera", mimo że składnia tekstowa nie zna słowa `OR`, bez zmiany miejsca rozbioru ani `Vouchers`.

**Pytania naprowadzające:**
- W `GiftCard` co wybiera gałąź `switch`: dane z zewnątrz czy bieżący status samego obiektu? Kto i kiedy zmienia ten wybór?
- Czy saldo należy do statusu, czy do karty? Co z tego wynika dla liczby instancji obiektów reprezentujących status?
- W `Vouchers` reguła ma gramatykę (pojęcia, negacja, łącznik). Co jest tu "zdaniem", a co "operacją na zdaniu"? Ile operacji na tym samym zdaniu już masz?
- Jeśli oceniasz regułę pojęcie po pojęciu, to kiedy wykryjesz literówkę w trzecim pojęciu, gdy pierwsze jest fałszywe? Czy po refaktoryzacji ten moment się nie zmieni?

**Gotowe, gdy:** test zielony (wszystkie warianty), `GiftCard` ma pole typu obiektowego zamiast tekstu statusu i tylko deleguje, obiekty statusów nie trzymają salda, tekst `" AND "` i prefiks `"NOT "` występują w kodzie w jednym pliku, a `Vouchers.applies` i `Vouchers.describe` mają po jednej linii.

## Ćwiczenie e06. Informacje o filmie z dwóch obcych źródeł

**Pakiet:** `pl.training.workshop.exercises.e06_movieinfo.start` · **Test:** `scripts/warsztat.sh test exercises/e06`

**Co widzimy:** Panel w kasie (`MovieInfo`) pokazuje dane filmu: tytuł, rok, czas trwania, ocenę i kategorię wiekową kina. Dane pochodzą z dwóch źródeł, których nie możemy zmienić: internetowej bazy w stylu OMDb (`OmdbLikeClient` zwraca mapę tekstów: `"Runtime": "155 min"`, `"imdbRating": "8.0/10"`, `"Rated": "PG-13"`, a nieznany film to `Response=False`) i starej tabeli kina (`LegacyMovieRows`: ocena pomnożona przez 10, kod wieku `"12"`, nieznany film to `null`). Cała wiedza o obu formatach siedzi w jednej metodzie `MovieInfoService.fetch` obok reguł kina. Do tego w każdej metodzie publicznej powtarza się `if (!user.staff())` i `cache.containsKey(...)`, a tabela kategorii wiekowych (`AgeRatingTable.load()`) czyta i parsuje plik z classpath przy każdym wyszukaniu, choć nigdy się nie zmienia. `AgeRatingResource.reads()` oraz liczniki `calls()` obu klientów pokazują, ile to kosztuje.

**Zadanie:**
1. Spraw, żeby `MovieInfoService` nie importował `OmdbLikeClient`, `LegacyMovieRows` ani `LegacyMovieRow` i nie zawierał żadnego `"min"`, `"/10"`, `Response` ani mnożenia przez 10. Dodanie trzeciego źródła danych ma być nową klasą plus jednym wpisem w miejscu składania panelu, bez edycji serwisu.
2. Wyprowadź pamięć podręczną i regułę "tylko personel" poza serwis tak, żeby każda z nich była w jednym miejscu, serwis o nich nie wiedział, a klient panelu nie widział różnicy. Gość nie może spowodować żadnego zapytania do źródła, a drugie pytanie o ten sam film też nie.
3. Zdecyduj, kto i kiedy tworzy tabelę kategorii wiekowych. Plik ma być czytany najwyżej raz na uruchomienie, ale klasa tłumacząca obce źródło ma dać się w teście złożyć z inną tabelą bez globalnego stanu.
4. Zapisz jednym zdaniem, dlaczego kolejność owijania (uprawnienia, pamięć podręczna, tłumaczenie) jest częścią zachowania, a nie szczegółem implementacji.

**Pytania naprowadzające:**
- Które fragmenty `fetch` tłumaczą obcy język na język kina, a które są regułą kina? Czy serwis musi znać oba?
- Pamięć podręczna i sprawdzenie uprawnień dokładają coś do wywołania, ale nie zmieniają jego wyniku. Czy klient powinien wiedzieć, że tam są? Jaki interfejs musi mieć obiekt, którego klient nie zauważa?
- Tabela kategorii jest niemutowalna i identyczna dla wszystkich. Co to oznacza dla liczby jej instancji? Kto powinien o tym decydować: tabela sama o sobie, czy miejsce, które składa panel?
- Co się stanie, gdy gość zapyta o film, który jest już w pamięci podręcznej? Odpowiedź zależy od tego, co jest na zewnątrz czego.

**Gotowe, gdy:** test zielony, `MovieInfoService` ma mniej niż 25 linii i importuje tylko typy z korzenia pakietu oraz własne interfejsy, `E06SolutionTest` potwierdza jedno zapytanie do źródła na film, zero zapytań dla gościa i najwyżej jeden odczyt pliku na JVM, a nowy test z własną tabelą kategorii nie wymaga żadnego statycznego "resetu".

## Ćwiczenie e07. Dokumenty sprzedaży: bilet, kod i paragon w trzech kanałach

**Pakiet:** `pl.training.workshop.exercises.e07_documents.start` · **Test:** `scripts/warsztat.sh test exercises/e07`

**Co widzimy:** `SalesDocuments.print(sale, channel, options)` drukuje komplet dokumentów sprzedaży: bilet, kod do kontroli i paragon. Każdy kanał ma własny wygląd całego kompletu: kiosk drukuje na drukarce termicznej (32 kolumny, kod kreskowy), aplikacja mobilna pokazuje dokument HTML-podobny z kodem QR, a kasa drukuje A4 z kodem kontrolnym. Klasy układów już istnieją (`KioskTicketLayout`, `MobileCodeRenderer`, `BoxOfficeReceiptLayout` i sześć podobnych), ale to `SalesDocuments` dobiera je trzema równoległymi `switch (channel)` i podaje osobno do `DocumentComposer`. Nic nie broni złożyć biletu kioskowego z kodem QR aplikacji i paragonem A4 - kompilator tego nie zauważy, a test `E07SolutionTest` pokazuje, że tak się da. Po złożeniu kompletu ta sama metoda dokleja opcjonalne sekcje: VAT (bilety 8%, bar 23%), baner promocyjny i stopkę lojalnościową (1 punkt za każde pełne 10.00, próg 100 punktów). Każda sekcja to kolejny `if` z obliczeniami w środku `print`, a kolejność sekcji jest kontraktem, którego nie widać nigdzie poza kolejnością tych `if`.

**Zadanie:**
1. Uruchom test i przeczytaj przypadki "wszystkie sekcje": kolejność sekcji i format każdego kanału to kontrakt.
2. Spraw, żeby komplet dokumentów jednego kanału pochodził z jednego miejsca i żeby pomieszanie części dwóch kanałów nie dało się skompilować (klasy konkretnych układów nie powinny być widoczne poza pakietem).
3. Spraw, żeby dodanie nowego kanału (np. e-mail) wymagało jednej nowej klasy i jednego wpisu, bez dotykania składania dokumentu ani sekcji.
4. Wynieś każdą opcjonalną sekcję do osobnego obiektu o tym samym kontrakcie co dokument, tak żeby sekcje dało się składać w dowolnym zestawie, a kolejność była zapisana w jednym miejscu.

**Pytania naprowadzające:**
- Co się zmienia razem: bilet, kod i paragon jednego kanału, czy bilet kioskowy i bilet mobilny? Które z tych rzeczy powinny być "jednym produktem" z punktu widzenia klienta?
- Czy opcjonalna sekcja wie coś o kanale? Czego potrzebuje, żeby wypisać się w formacie kanału, a czego nie powinna znać?
- Ile kombinacji trzech flag obsługuje dzisiejszy `print`? Co się stanie z liczbą gałęzi, gdy dojdzie czwarta sekcja?
- Czy sekcja powinna "wiedzieć", że jest ostatnia, czy wystarczy, że dopisuje się pod tym, co dostała?

**Gotowe, gdy:** test zielony (`E07EquivalenceTest` i `E07SolutionTest`), w `SalesDocuments` nie ma żadnego `switch` po kanale ani obliczeń sekcji, klasy konkretnych układów nie są publiczne, a kolejność sekcji jest zapisana w jednym miejscu i da się ją sprawdzić testem.

## Ćwiczenie e08. Seanse cykliczne - seria tygodniowa z szablonu

**Pakiet:** `pl.training.workshop.exercises.e08_series.start` · **Test:** `scripts/warsztat.sh test exercises/e08`

**Co widzimy:** `SeriesPlanner.plan(SeriesRequest)` układa serię seansów: jeden szablon i po jednym seansie na tydzień, czasem z dopłatą premierową w pierwszym tygodniu i inną salą w ostatnim. Szablon powstaje konstruktorem `Screening` o dziewięciu parametrach, do którego wywołujący wstawia `null`, gdy "ma być domyślnie". Wartości domyślne (sala A, cena z formatu, limit wieku 12, dopłaty VIP i okulary 3D) są rozsiane po planerze, po kopii tygodnia i po samej klasie `Screening` (`describe` jeszcze raz liczy cenę z formatu). Każdy kolejny tydzień i przeniesienie do innej sali to ręczne przepisanie wszystkich dziewięciu pól do nowego konstruktora - dwie takie kopie już są, a każda nowa cecha seansu wymaga poprawienia obu. Kopia dzieli z szablonem mapę dopłat: dopisanie dopłaty PREMIERA do pierwszego tygodnia zmienia także szablon, więc kolejne tygodnie też ją dostają (zobacz `E08SolutionTest`).

**Zadanie:**
1. Spraw, żeby "kolejny tydzień", "pierwszy tydzień z premierą" i "ostatni tydzień w innej sali" nie wymagały wypisywania wszystkich pól seansu - i żeby zmiana w jednej kopii nigdy nie była widoczna w szablonie ani w innych kopiach.
2. Spraw, żeby seansu nie dało się utworzyć z `null`-ami: pola wymagane (tytuł, format, start) mają być sprawdzane przy tworzeniu, a wartości domyślne ustalane w dokładnie jednym miejscu.
3. Zdecyduj, które sprawdzenia należą do pojedynczego seansu, a które do serii (np. liczba tygodni) - i gdzie ma paść każdy wyjątek, żeby kolejność błędów z testu się nie zmieniła.
4. Dodaj nową cechę seansu (np. język audiodeskrypcji) i policz, ile miejsc trzeba było zmienić przed refaktoryzacją, a ile po niej.

**Pytania naprowadzające:** Kto wie najlepiej, jak zrobić poprawną kopię seansu - planer czy sam seans? Czy kopia ma dzielić z oryginałem cokolwiek mutowalnego? Które z dziewięciu parametrów są naprawdę wymagane, a które mają sensowną wartość domyślną - i czy konstruktor potrafi to wyrazić? Czym różni się "obiekt zbudowany od zera z domyślnymi" od "obiekt zrobiony z istniejącego z jedną zmianą"?

**Gotowe, gdy:** test równoważności jest zielony, `SeriesPlanner` nie zawiera żadnego wywołania konstruktora z dziewięcioma argumentami ani sprawdzeń `!= null` dla wartości domyślnych, dopłata premierowa jest tylko w pierwszym tygodniu (świadoma naprawa opisana w `E08SolutionTest`), a `Screening` nie ma publicznego konstruktora.

## Ćwiczenie e09. Zamknięcie kasy - wpisy szuflady i rabat grupowy

**Pakiet:** `pl.training.workshop.exercises.e09_cashdrawer.start` · **Test:** `scripts/warsztat.sh test exercises/e09`

**Co widzimy:** `CashDrawerReport.render(DrawerDay)` zamyka dzień kasowy: liczy gotówkę w szufladzie, generuje eksport fiskalny i dziennik audytu z wpisów czterech rodzajów (`CashSale`, `CardSale`, `Refund`, `Deposit`), a na końcu rozdziela rabat grupowy (10+ biletów = -10% sumy) na pojedyncze bilety. Wpisy to rekordy z pustym interfejsem znacznikowym `DrawerEntry`, więc każda z trzech operacji raportu to osobny łańcuch `instanceof` zakończony wyjątkiem w runtime: nowy rodzaj wpisu (np. voucher) kompiluje się bez błędu i wybucha dopiero przy zamknięciu dnia. Trzy łańcuchy mają tę samą listę gałęzi w tej samej kolejności. Podział rabatu wybiera łańcuch `if` po nazwie trybu z konfiguracji (`PROPORTIONAL`, `LAST`, `EQUAL`), wpleciony w tę samą metodę co reguła "10+ biletów" i poprawka zaokrąglenia, która musi dać dokładnie kwotę rabatu.

```java
private Money cashEffect(DrawerEntry entry) {
    if (entry instanceof CashSale sale) {
        return sale.amount();
    } else if (entry instanceof CardSale) {
        return Money.ZERO;
    } else if (entry instanceof Refund refund) {
        return Money.ZERO.minus(refund.amount());
    } else if (entry instanceof Deposit deposit) {
        return deposit.amount();
    }
    throw new IllegalArgumentException("unknown entry: " + entry);
}
```

```java
if (mode.equals("PROPORTIONAL")) {
    ...
} else if (mode.equals("LAST")) {
    ...
} else if (mode.equals("EQUAL")) {
    ...
} else {
    throw new IllegalArgumentException("unknown split mode: " + mode);
}
```

**Zadanie:**
1. Spraw, żeby wpis bez obsługi w którejś operacji raportu był błędem kompilacji, a nie wyjątkiem przy zamknięciu dnia. W raporcie nie może zostać żaden `instanceof`.
2. Spraw, żeby nowa operacja na wpisach (np. "metoda płatności do raportu kierownika") była jedną nową klasą, bez zmian w rekordach wpisów i w `CashDrawerReport`.
3. Wydziel każdy tryb podziału rabatu tak, żeby reguła "10+ biletów = -10%" nie znała nazw trybów, a nowy tryb (np. "cały rabat na pierwszym bilecie") był jedną klasą albo lambdą.
4. Zdecyduj, gdzie powinien zapaść wybór trybu po nazwie z konfiguracji, i zapisz jednym zdaniem, co to zmienia dla grupy poniżej 10 biletów z nieznanym trybem.

**Pytania naprowadzające:**
- Co w tym kodzie będzie się zmieniać częściej: rodzaje wpisów szuflady czy operacje na nich? Ile plików dotkniesz w każdym przypadku przed zmianą i po niej?
- Kto wybiera tryb podziału rabatu i kiedy: obiekt sam na podstawie swojego stanu, czy konfiguracja kina przed obliczeniem? Czy tryb zmienia się w trakcie życia raportu?
- Które fragmenty są wspólne dla wszystkich trybów (reguła 10+, kwota rabatu, format linii), a które naprawdę się różnią?
- Hierarchię wpisów można też zamknąć słowem kluczowym Javy i użyć `switch` po typach bez `default`. Co to daje, a czego nie daje w porównaniu z Twoim rozwiązaniem?

**Gotowe, gdy:** test zielony dla `start` i Twoich kroków, w `CashDrawerReport` nie ma `instanceof` ani łańcucha `if` po nazwie trybu, dodanie rodzaju wpisu bez obsługi nie kompiluje się, każdy tryb podziału jest osobną bezstanową klasą, a suma udziałów zawsze równa się rabatowi (sprawdź na cenach z groszami, np. 10 x 21.35, i na 11 biletach w trybie równym).

## Ćwiczenie e10. Start seansu - urządzenia sali i dwa pulpity

**Pakiet:** `pl.training.workshop.exercises.e10_showtime.start` · **Test:** `scripts/warsztat.sh test exercises/e10`

**Co widzimy:** Sala kinowa ma pięć urządzeń: `Doors`, `Lights`, `Sound`, `AdPlayer` i `Projector`. Start seansu to sekwencja reakcji: zamknięcie drzwi przyciemnia światła i puszcza reklamy, koniec reklam gasi światła, ustawia dźwięk i włącza projektor, a zatrzymanie projektora przywraca dźwięk, światła i otwiera drzwi. Każdą z tych reakcji zna inne urządzenie: `Doors` trzyma referencje do `Lights` i `AdPlayer`, `AdPlayer` do `Lights`, `Sound` i `Projector`, `Projector` do `Sound`, `Lights` i `Doors`. `HallController` tylko spina je metodami `wire(...)` i przekazuje wywołanie pierwszemu urządzeniu w sekwencji. Drzwi pamiętają tytuł filmu, bo muszą go podać dalej, a projektor ma dwa sposoby wyłączenia: `stop()` z kaskadą i `cut()` bez niej, bo w procedurze awaryjnej kaskada dublowała wpisy w dzienniku.

Nad salą pracują dwa klienty. `BoxOfficePanel` (kasjer) i `AutoScheduler` (automat według harmonogramu) same wykonują kroki startu seansu: zamknięcie bramki biletowej, uruchomienie sali, zapis frekwencji do raportu dla dystrybutora. Kopia w automacie powstała wcześniej i nikt jej nie uzupełnił: seanse startowane automatycznie nie trafiają do raportu. Zakończenie seansu obie klasy robią tak samo.

Test `E10EquivalenceTest` uruchamia skrypt dnia (`scan S1`, `desk start S1 Diuna`, `tick`, `obstacle`, `cleared`, `emergency`, `desk end S1`, ...) przez `ShowtimeRunner` i porównuje dziennik urządzeń. Kolejność wpisów jest kontraktem. `E10BehaviourChangeTest` opisuje różnicę między kasjerem a automatem i to, co ma się z nią stać.

**Zadanie:**
1. Policz referencje między urządzeniami i wypisz, które klasy znają kolejność reakcji na zamknięcie drzwi, koniec reklam i zatrzymanie projektora. Osobno zapisz, co jest logiką samego urządzenia (limit głośności, przeszkoda w drzwiach, to, czy projektor już gra), a co regułą współpracy sali.
2. Spraw, żeby żadne urządzenie nie miało referencji do innego urządzenia, a cała kolejność reakcji była zapisana w jednym miejscu. Urządzenie ma tylko zgłaszać, co się stało. Logika urządzeń zostaje w urządzeniach, a drzwi przestają znać tytuł filmu.
3. Usuń powód istnienia dwóch sposobów wyłączania projektora tak, żeby `stop()` i `cut()` były zwykłymi operacjami urządzenia, bez wpływu na resztę sali.
4. Daj obu klientom jedną operację "uruchom seans" i jedną "zakończ seans", tak żeby żaden klient nie znał kroków ani ich kolejności, a podsystemy (bramka, sala, raport) nie były publiczne. Przepięcie automatu zmieni jego zachowanie - nazwij tę zmianę i upewnij się, że `E10BehaviourChangeTest` ją dokumentuje.

**Pytania naprowadzające:**
- Kto dziś decyduje, że po zamknięciu drzwi mają przygasnąć światła: drzwi czy sala? A kto powinien?
- Gdy dojdzie szóste urządzenie (np. kurtyna), ile klas trzeba zmienić w `start`, a ile po Twojej zmianie?
- Czym różni się "urządzenie zgłasza zdarzenie, a ktoś decyduje, co dalej" od "urządzenie powiadamia wszystkich zainteresowanych"? Które z nich pasuje do sali, w której kolejność reakcji jest regułą?
- Czy operacja "uruchom seans" dla klientów to to samo miejsce, w którym zapisana jest kolejność reakcji urządzeń? Dlaczego warto je rozdzielić?
- Co się stanie z kaskadą, jeśli jedno zdarzenie wywoła drugie (koniec reklam uruchamia projektor, a projektor zgłasza start)? Jak tego uniknąć?

**Gotowe, gdy:** testy zielone (`E10EquivalenceTest`, `E10BehaviourChangeTest`, `E10SolutionTest`), żadne urządzenie nie ma pola typu innego urządzenia, kolejność reakcji na zdarzenia urządzeń jest w jednej klasie, `BoxOfficePanel` i `AutoScheduler` mają po jednym polu, a klasy bramki, sali i raportu nie są publiczne.

