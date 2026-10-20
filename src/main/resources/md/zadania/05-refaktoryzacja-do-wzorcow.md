# Moduł 5. Refaktoryzacja do wzorców - zadania dla uczestników

## Jak pracować

Każde ćwiczenie wykonujemy w krótkiej, powtarzalnej pętli:

1. uruchom testy bazowe,
2. nazwij zachowanie chronione w bieżącym kroku,
3. wykonaj jedną transformację (najlepiej automatem IDE),
4. uruchom test celowany,
5. uruchom test różnicowy (wersja przed i po na tych samych danych),
6. zapisz decyzję projektową (jedno, dwa zdania w notatce albo w opisie commita),
7. przejdź do następnego kroku dopiero po zielonym wyniku.

Nie zaczynaj od utworzenia wszystkich klas wzorca. Pierwszy commit powinien być mały i odwracalny. Refaktoryzacja zachowuje obserwowalne zachowanie: jeżeli w trakcie pracy chcesz dodać nowy wariant, zmienić obsługę błędów, typ wyjątku, wątek wykonania albo liczbę odbiorców, zapisz to jako osobną decyzję i wykonaj w osobnym kroku.

Przy każdej zmianie sprawdzaj nie tylko wartość zwracaną, ale również:

- typ wyjątku i jego komunikat oraz moment, w którym jest zgłaszany,
- stan obiektu po sukcesie i po błędzie,
- liczbę i kolejność efektów ubocznych,
- zachowanie dla `null`, wartości nieznanej, gałęzi domyślnej i wartości granicznych.

Każdy nowy test sprawdź **kontrolną mutacją**: zepsuj na chwilę regułę (np. przestaw dwie gałęzie albo zmień próg), zobacz czerwony test, przywróć kod.

Ćwiczenia dotyczą obu kierunków: refaktoryzacji **do** wzorca i **od** wzorca. W każdym ćwiczeniu dopuszczalnym wynikiem jest uzasadniona decyzja, że wzorzec nie jest potrzebny.

### Środowisko

Pracuj w katalogu głównym repozytorium. Kod wyjściowy modułu znajduje się w pakietach `before` (zwykle klasy z przedrostkiem `Legacy`), wersje referencyjne w pakietach `after` - **nie otwieraj ich przed omówieniem**.

```bash
# kompilacja i uruchomienie demonstracji modułu
mvn -q compile && java -cp target/classes pl.training.module5.Module5Examples

# testy modułu
mvn test -Dtest='pl.training.module5.**'

# pojedynczy test, np.
mvn test -Dtest=ChainEquivalenceTest

# testy jednej sceny warsztatu CineLegacy
scripts/warsztat.sh test m5/s26
```

Nowe klasy twórz we własnym pakiecie (np. `pl.training.module5.workshop.chain`) albo na osobnej gałęzi, tak aby zawsze móc porównać wynik z kodem wyjściowym. Testy pisz w `src/test/java/pl/training/module5/`. Dobry początek: skopiuj test równoważności danego przykładu (np. `ChainEquivalenceTest`) i zamień w nim klasę `after` na własną.

Odpowiedniki w innych językach: `csharp/src/Training.Module5/<Temat>/Before` z testami w `csharp/tests/Training.Module5.Tests` (`cd csharp && dotnet test tests/Training.Module5.Tests`) oraz `typescript/src/module5/<temat>/before` z testami w `typescript/test/module5` (`cd typescript && npx vitest run test/module5`). Nazwy klas są takie same jak w Javie.

### Przegląd zadań

| Nr | Nazwa | Czas | Kod wyjściowy | Wariant CineLegacy |
| --- | --- | --- | --- | --- |
| Ćwiczenie 1 | Usunięcie zbędnego wzorca | 30 min | `middleman`, `inlinesingleton` | s21, s22 |
| Ćwiczenie 2 | Command, Strategy czy State | 60 min | `variability` | s26 |
| Ćwiczenie 3 | Template Method → Strategy, Chain czy Decorator | 60 min | `templatetostrategy`, `chain` | s27, s28 |
| Ćwiczenie 4 | Granice: Adapter, Boundary Object, Facade, ACL | 60 min | `boundaryobject`, `facade`, `anticorruptionlayer` | s29, s30, s31 |
| Dodatkowe D1 | State, Observer i Decorator | 60 min | `state`, `observer`, `decorator` | s07-s09 |
| Dodatkowe D2 | Rodzina refaktoryzacji Composite | 60-90 min | `composite` | s10-s13, s18-s19 |
| Dodatkowe D3 | Mediator | 30 min | `mediator` | s32 |

Czas obejmuje omówienie (ok. 10 minut na ćwiczenie). Ćwiczenia dodatkowe są dla szybszych zespołów albo do samodzielnej pracy po zajęciach.

---

## Ćwiczenie 1: usunięcie zbędnego wzorca

**Cel:** ocenić, czy pośrednik i globalny singleton chronią wartościową granicę, a jeśli nie - usunąć je małymi, odwracalnymi krokami, oddzielając refaktoryzację od zmiany kontraktu.

**Czas:** ok. 30 minut (20 minut pracy, 10 minut omówienia).

### Kontekst

Pliki wyjściowe:

- `src/main/java/pl/training/module5/middleman/before/ReleaseService.java` - pośrednik: `update` i `statusOf` przekazują wywołania do rejestru,
- `src/main/java/pl/training/module5/middleman/before/ReleaseDashboard.java` - klient pośrednika (`render(deploymentId)`),
- `src/main/java/pl/training/module5/middleman/before/DeploymentRegistry.java` - rejestr statusów z walidacją identyfikatora,
- `src/main/java/pl/training/module5/inlinesingleton/before/LegacyFeatureFlags.java` - globalny, mutowalny singleton flag (`getInstance`, `enable`, `disable`, `isEnabled`, `reset`),
- `src/main/java/pl/training/module5/inlinesingleton/before/LegacyRolloutService.java` - `plan(releaseId)` zależny od flag `canary` i `dark-launch`, pobieranych w środku metody.

### Polecenia

**Część A: Remove Middle Man (10 minut).**

1. Zanim cokolwiek zmienisz, zapisz ocenę pośrednika: czy realizuje autoryzację, transakcje, telemetrię, retry, translację błędów, cache albo stanowi granicę modułu? Dla każdego punktu: tak, nie, nie wiadomo.
2. Dodaj test charakterystyki dla `render` i `statusOf`: identyfikator nieznany (`UNKNOWN`), po aktualizacji, po nadpisaniu, identyfikator `null`, pusty i biały, `null` jako status.
3. Przepnij `ReleaseDashboard` na `DeploymentRegistry` (Inline Method albo Remove Middle Man w IDE).
4. Usuń `ReleaseService` dopiero, gdy nie ma żadnych klientów (Safe Delete).
5. Zapisz, czy zmienił się jakikolwiek element kontraktu (np. komunikat w konstruktorze dashboardu).

**Część B: Inline Singleton (10 minut).**

1. Napisz test, który pokazuje, że dwa niezależne serwisy dzielą stan przez singleton. Zwróć uwagę, czego wymaga od testu sprzątanie globalnego stanu.
2. Wprowadź niezmienny obiekt flag przekazywany przez konstruktor nowej wersji serwisu. Starą klasę zostaw, dopóki klienci nie zostaną przepięci jeden po drugim.
3. Przenieś tworzenie flag do korzenia kompozycji (np. metoda `create(String flagsConfig)`).
4. Usuń starą wersję serwisu i jej odwołania do `getInstance()`. Nie usuwaj `LegacyFeatureFlags`, dopóki nie sprawdzisz innych użyć.
5. Odpowiedz pisemnie: czy przejście z mutowalnego singletonu na niezmienną wartość jest refaktoryzacją? Jakie pytanie trzeba zadać właścicielowi systemu?

### Kryteria akceptacji

- ocena pośrednika istnieje przed pierwszym ruchem i obejmuje wszystkie punkty z polecenia A1,
- renderowanie i walidacja są identyczne przed i po (te same komunikaty),
- migracja przebiega jednym klientem naraz, a usunięcie klasy jest osobnym krokiem,
- serwis rolloutu nie odwołuje się do globalnego stanu, a zależność jest widoczna w konstruktorze,
- testy nowej wersji nie potrzebują `reset()` ani blokady zasobu,
- zmiana kontraktu (brak przełączania flag w locie) jest nazwana i opisana, a nie ukryta.

### Oczekiwany produkt

- pisemna ocena pośrednika (tabela: punkt - tak/nie/nie wiadomo - uzasadnienie),
- test charakterystyki i test równoważności dla obu części,
- 4-6 małych commitów, w tym osobny commit na usunięcie klasy,
- notatka: co było refaktoryzacją, a co zmianą kontraktu.

### Wariant CineLegacy

- `scripts/warsztat.sh test m5/s22` - pośrednik `CinemaFacade`, który "prawie" tylko deleguje (`freeSeats` tłumaczy brak seansu na `0`). Co zrobisz z tą regułą przed usunięciem pośrednika?
- `scripts/warsztat.sh test m5/s21` - Strategy rozliczeń z dystrybutorem, z której po wygaśnięciu wariantu festiwalowego zostaje jedna implementacja.

---

## Ćwiczenie 2: Command, Strategy czy State

**Cel:** rozpoznać, jaki rodzaj zmienności kryje się za podobnie wyglądającymi warunkami, i dla każdej osi wprowadzić właściwy mechanizm bez zmiany zachowania.

**Czas:** ok. 60 minut (45 minut pracy, 15 minut omówienia).

### Kontekst

Plik wyjściowy: `src/main/java/pl/training/module5/variability/before/LegacyReleaseConsole.java`. Wspólne typy: `src/main/java/pl/training/module5/variability/CustomerTier.java`, `src/main/java/pl/training/module5/variability/ReleaseStatus.java`.

Klasa obsługuje trzy osie, wszystkie za pomocą `switch` lub `if`:

- (a) koszt wsparcia według taryfy klienta (`STANDARD`, `PREMIUM`, `ENTERPRISE`) - taryfa jest stała dla klienta, ale wybierana przy każdym wywołaniu,
- (b) polecenia operatora (`scale-up`, `scale-down`, `maintenance`) - kolejkowane, logowane i cofane; wykonanie i cofnięcie to dwa osobne `switch`,
- (c) cykl życia wydania (`PLANNED`, `DEPLOYING`, `LIVE`, `ROLLED_BACK`) - `advance`, `rollback`, `banner` zależne od statusu.

Tabela przejść cyklu życia (do potwierdzenia testem przed zmianą):

| Stan | advance | rollback |
| --- | --- | --- |
| PLANNED | DEPLOYING | wyjątek |
| DEPLOYING | LIVE | ROLLED_BACK |
| LIVE | wyjątek | ROLLED_BACK |
| ROLLED_BACK | wyjątek | wyjątek |

### Polecenia

1. Dla każdej osi odpowiedz na pytanie: **kto wybiera wariant i kiedy?** Na tej podstawie przypisz oś do jednego z mechanizmów: Strategy, Command, State (albo uzasadnij, że `switch` jest wystarczający).
2. Zapisz uzasadnienie w jednym zdaniu dla każdej osi.
3. Napisz test równoważności przed pierwszą zmianą. Obejmij: wszystkie taryfy dla kilku wartości godzin (w tym 0) i wartość ujemną; kolejkę kilku poleceń, log i dwa cofnięcia; nieznaną akcję; cofnięcie bez historii; `scale-down` poniżej jednej instancji (stan i log po błędzie); pełną tabelę przejść z komunikatami i bannerami.
4. Wprowadź jeden mechanizm naraz. Kolejność dowolna, ale po każdym mechanizmie uruchom testy i zrób commit.
5. Dla Command: wykonanie i cofnięcie polecenia mają być w jednym miejscu. Dla State: domyślna operacja stanu rzuca wyjątek z dotychczasowym komunikatem. Dla Strategy: walidacja godzin zostaje w kontekście.
6. Nie usuwaj starego warunku, dopóki wszystkie punkty tworzenia nie używają nowego modelu.
7. Sprawdź jedno przesunięcie: kiedy zgłaszany jest błąd nieznanej akcji (przy `submit` czy przy `runQueued`) - przed i po.

### Kryteria akceptacji

- nie powstała kombinatoryczna hierarchia podtypów (np. `PremiumLiveConsole`),
- stan przejściowy nie jest strategią, a taryfa nie jest stanem,
- polecenia nie przechowują danych poprzedniego żądania; polecenie, które rzuciło wyjątek, nie trafia do historii (jak w legacy),
- fabryka lub korzeń kompozycji jest jedynym miejscem mapowania taryfy na strategię i nazwy akcji na polecenie,
- testy porównują wyniki, komunikaty wyjątków, status i log wersji przed i po; zawierają też co najmniej jedno niezależne, dosłowne oczekiwanie.

### Oczekiwany produkt

- tabela: oś - kto wybiera wariant i kiedy - mechanizm - uzasadnienie,
- test równoważności (w tym `null`, wartości graniczne, awarie w środku kolejki),
- seria małych commitów, z których każdy wprowadza jeden mechanizm,
- odpowiedź: ile miejsc trzeba zmienić, aby dodać polecenie `restart` z cofnięciem - przed i po.

### Rozszerzenie (dla szybszych zespołów)

Cztery osie w osobnych klasach z poprzedniej wersji kursu: `src/main/java/pl/training/module5/strategy/before/LegacyDeploymentCostCalculator.java`, `src/main/java/pl/training/module5/polymorphism/before/LegacyDeploymentStep.java`, `src/main/java/pl/training/module5/state/before/LegacyRelease.java`, `src/main/java/pl/training/module5/command/before/LegacyDeploymentDispatcher.java`. Przypisz każdą do mechanizmu (dochodzi czwarta możliwość: polimorfizm podtypów) i porównaj z odpowiedziami dla `LegacyReleaseConsole`.

### Wariant CineLegacy

`scripts/warsztat.sh test m5/s26` - pulpit kasjera: cena według typu biletu, operacje kasjera z dziennikiem i cofaniem, cykl życia rezerwacji (ta sama operacja `CANCEL` znaczy co innego w `NEW` i w `PAID`).

---

## Ćwiczenie 3: Template Method → Strategy, Chain czy Decorator

**Cel:** rozpoznać, kiedy Template Method przestaje wystarczać, przećwiczyć Replace Inheritance with Delegation oraz zamianę łańcucha `if/else` na Chain of Responsibility, a następnie odróżnić łańcuch od dekoratora.

**Czas:** ok. 60 minut (część A 25 minut, część B 20 minut, część C 5 minut, omówienie 10 minut).

### Część A: Template Method → Strategy

Pliki wyjściowe:

- `src/main/java/pl/training/module5/templatetostrategy/before/LegacyReleaseNotesExporter.java` - szkielet `export` i cztery haki: `header`, `include`, `formatEntry`, `footer`,
- podklasy `LegacyMarkdownChangelogExporter.java`, `LegacyMarkdownHighlightsExporter.java`, `LegacyHtmlChangelogExporter.java`, `LegacyHtmlHighlightsExporter.java` w tym samym pakiecie,
- `src/main/java/pl/training/module5/templatetostrategy/ChangeEntry.java` - wpis zmiany (`FEATURE`, `FIX`, `BREAKING`).

Polecenia:

1. Napisz test równoważności dla każdej z czterech podklas na liście z trzema wpisami (w tym opis ze znakami `<`, `>` i `&`) oraz na pustej liście.
2. Pogrupuj haki według osi zmienności. Który hak należy do której osi? Gdzie jest ukryty tytuł raportu?
3. Wprowadź interfejs dla każdej osi. Przejściowo podklasy mogą delegować do nowych obiektów.
4. Przenieś szkielet do jednej klasy finalnej, która dostaje obie strategie w konstruktorze.
5. Pokaż, że nowa kombinacja (np. "tylko zmiany łamiące" w Markdown) nie wymaga nowej klasy.
6. Usuń podklasy dopiero po migracji wszystkich miejsc tworzenia.

### Część B: łańcuch `if/else` → Chain of Responsibility

Pliki wyjściowe:

- `src/main/java/pl/training/module5/chain/before/LegacyApprovalRouter.java` - pięć gałęzi i domyślne odrzucenie,
- `src/main/java/pl/training/module5/chain/ChangeRequest.java`, `src/main/java/pl/training/module5/chain/Decision.java` - wspólne typy.

Polecenia:

1. Zbuduj test różnicowy na siatce wartości granicznych: rozmiary 0, 50, 51, 500, 501, 2000, 2001, 5000, 5001, każde ryzyko, produkcja i nie-produkcja. Policz, ile przypadków porównujesz.
2. Znajdź gałąź, której warunek jest poprawny **tylko dzięki** gałęziom wcześniejszym. Podaj żądanie, dla którego przestawienie dwóch gałęzi zmieni wynik.
3. Wprowadź ogniwo łańcucha z metodą przekazania dalej i jawnym końcem łańcucha (decyzja, nie `null`).
4. Przenoś jedną gałąź naraz do ogniwa. Złóż łańcuch w jednym, nazwanym miejscu.
5. Kontrolna mutacja: przestaw dwa ogniwa w złożeniu i sprawdź, czy test to wykrywa.

### Część C: decyzja

Dział zgodności chce zapisywać w audycie każdą decyzję zatwierdzania (kto, dlaczego, dla jakiego żądania). Zaproponuj miejsce: kolejne ogniwo łańcucha czy dekorator wokół routera? Uzasadnij w trzech zdaniach, odwołując się do tego, ile elementów pracuje dla jednego żądania.

### Kryteria akceptacji

- eksporter: N formatów i M zakresów to N + M klas, kolejność kroków chroni jedna metoda, a klasa szkieletu jest zamknięta na dziedziczenie,
- znaki specjalne w HTML są uciekane dokładnie tak jak w legacy (nie mniej i nie więcej),
- łańcuch: każde żądanie obsługuje dokładnie jedno ogniwo, koniec łańcucha zwraca decyzję odrzucenia z dotychczasowym komunikatem,
- kolejność ogniw jest widoczna w jednym wyrażeniu, a zależność od kolejności jest opisana,
- test różnicowy łańcucha obejmuje wszystkie granice progów i zawiera niezależne oczekiwanie (np. po jednym żądaniu na każdego zatwierdzającego).

### Oczekiwany produkt

- tabela haków: hak - oś - nowe miejsce,
- eksporter z dwiema strategiami i test równoważności dla czterech kombinacji,
- łańcuch ogniw z testem na siatce przypadków i wynikiem kontrolnej mutacji,
- krótka notatka z decyzją z części C.

### Wariant CineLegacy

- `scripts/warsztat.sh test m5/s27` - raporty sprzedaży: okres (dzień, tydzień) × format (CSV, tekst) zaszyte w podklasach.
- `scripts/warsztat.sh test m5/s28` - zatwierdzanie zwrotów: kolejność warunków jest regułą biznesową (zwrot automatyczny stoi przed kasjerem).

---

## Ćwiczenie 4: granice - Adapter, Boundary Object, Facade, ACL

**Cel:** dobrać właściwy mechanizm do trzech rodzajów granicy (surowe dane z zewnątrz, orkiestracja podsystemów, obcy model) i jawnie zapisać każdą zmianę typu lub momentu błędu.

**Czas:** ok. 60 minut (każda część ok. 15 minut, omówienie 15 minut). Zespoły mogą podzielić części między siebie i zaprezentować wyniki.

### Część A: Boundary Object

Pliki wyjściowe:

- `src/main/java/pl/training/module5/boundaryobject/LegacyCiClient.java` - klient CI zwracający `Map<String, String>` z kluczami `id`, `status`, `coverage`, `failed`, `commit`,
- `src/main/java/pl/training/module5/boundaryobject/before/LegacyReleaseGate.java` - `canRelease` (sukces, zero błędów, pokrycie co najmniej 80) i `summary`, oba parsują ładunek samodzielnie.

Polecenia:

1. Test równoważności dla kilku poprawnych ładunków (w tym wartości z białymi znakami, małe litery w statusie, pokrycie 80 i 79.9).
2. Ustal, co dziś dzieje się dla ładunku bez klucza i z wartością nieliczbową. Gdzie wybucha błąd?
3. Wprowadź port domenowy zwracający niezmienny rekord z niezmiennikami (zakres pokrycia, nieujemna liczba błędów, format identyfikatora commita) i adapter na granicy.
4. Zdecyduj i zapisz, jaki błąd ma przekroczyć granicę dla ładunku niepoprawnego. Czy to jeszcze refaktoryzacja?

### Część B: Facade

Pliki wyjściowe:

- `src/main/java/pl/training/module5/facade/before/LegacyReleaseCli.java` i `src/main/java/pl/training/module5/facade/before/LegacyReleaseScheduler.java` - dwaj klienci orkiestrujący te same cztery podsystemy,
- podsystemy `ArtifactStore.java`, `ConfigRenderer.java`, `DeploymentQueue.java`, `ReleaseNotifier.java` w pakiecie `facade/before` - każdy zapisuje swój efekt do wstrzykniętego `Consumer<String>`.

Polecenia:

1. Test równoważności porównujący **listę efektów i ich kolejność** dla obu klientów oraz brak efektów przy błędnym żądaniu.
2. Porównaj obu klientów: czy ich kontrakty są naprawdę takie same (walidacja środowiska, `null`)?
3. Wydziel orkiestrację do fasady i przepnij klientów jeden po drugim.
4. Jako osobny krok zawęź widoczność podsystemów. Kogo to może zepsuć?
5. Zapisz, czego fasada nie daje (np. atomowości przy awarii w środku sekwencji).

### Część C: Anti-Corruption Layer

Pliki wyjściowe:

- `src/main/java/pl/training/module5/anticorruptionlayer/LegacyCmdbClient.java`, `src/main/java/pl/training/module5/anticorruptionlayer/LegacyCmdbRecord.java` - obcy model (kody `P`/`T`/`D`, flaga `1`/`0`/`9`, data `yyyyMMdd`),
- `src/main/java/pl/training/module5/anticorruptionlayer/before/LegacyDeploymentPlanner.java` - `canaryTargets`, `staleEnvironments`, `isProduction`.

Polecenia:

1. Przeczytaj każdą regułę planera na głos w języku biznesu. Zapisz model domenowy, który pozwala ją tak zapisać.
2. Test równoważności dla zestawu rekordów aktywnych, wyłączonych i wycofanych.
3. Wprowadź port domenowy, model domenowy i tłumacza w osobnym pakiecie. Typy CMDB nie mogą wyjść poza ten pakiet.
4. Ustal, co dziś dzieje się z rekordem o nieznanym kodzie środowiska i z niepoprawną datą w rekordzie nieaktywnym. Co dzieje się po zmianie? Zapisz decyzję.

### Kryteria akceptacji

- domena nie zna nazw kluczy ładunku CI, kodów CMDB ani podsystemów publikacji,
- dla danych poprawnych decyzje, podsumowania i efekty są identyczne przed i po,
- każda zmiana typu lub momentu błędu dla danych niepoprawnych jest nazwana i zatwierdzona jako osobna decyzja,
- fasada wymusza kolejność kroków i walidację przed pierwszym efektem; zawężenie widoczności jest osobnym krokiem,
- domenę planowania można przetestować bez żadnego typu CMDB.

### Oczekiwany produkt

- tabela: granica - mechanizm (Adapter, Boundary Object, Facade, ACL) - uzasadnienie - przesunięcia błędów,
- trzy testy równoważności, każdy z niezależnym oczekiwaniem,
- notatka z decyzjami dla danych niepoprawnych (ładunek CI, rekord CMDB).

### Wariant CineLegacy

- `scripts/warsztat.sh test m5/s29` - surowa odpowiedź terminala płatniczego `OK;AUTH-R1;4500` w serwisie płatności,
- `scripts/warsztat.sh test m5/s30` - sklep internetowy i kasa biletowa z tą samą, skopiowaną orkiestracją (cena, miejsca, płatność, mail); kasa nie pobiera opłaty rezerwacyjnej - porównaj obie metody, zanim wydzielisz fasadę,
- `scripts/warsztat.sh test m5/s31` - rozliczenie z dystrybutorem w modelu dystrybutora: kody statusu, daty `yyyyMMdd`, grosze jako tekst, `45%`.

---

## Ćwiczenia dodatkowe

### D1: State, Observer i Decorator

**Cel:** połączyć kilka wzorców współpracy wokół jednego obiektu domenowego i jawnie zapisać kontrakt kolejności, błędów i subskrypcji.

**Czas:** ok. 60 minut.

Pliki wyjściowe:

- `src/main/java/pl/training/module5/state/before/LegacyRelease.java` - stany `DRAFT`, `APPROVED`, `DEPLOYED`, `CANCELLED`; niedozwolona operacja rzuca `IllegalStateException` z komunikatem `cannot <akcja> release in state <STAN>`,
- `src/main/java/pl/training/module5/decorator/before/LegacyDeploymentRunner.java` - wdrożenie z wplecionym audytem (`start:`, `success:`, `failure:<id>:<TypWyjątku>`),
- `src/main/java/pl/training/module5/observer/before/LegacyReleasePublisher.java` - publikacja do dwóch na stałe wpisanych odbiorców (`auditLog`, potem `metrics`).

Polecenia:

1. Ustal, czy audyt jest częścią przejścia State, dekoracją operacji, czy osobnym zdarzeniem.
2. Zapisz kolejność stanu, audytu i publikacji dla sukcesu i dla awarii.
3. Dodaj listener, który usuwa inną subskrypcję podczas callbacku, i listener rzucający wyjątek.
4. Udokumentuj politykę fail-fast albo kontynuacji i przetestuj stan po awarii audytu oraz listenera.

Kryteria: brak wywołań listenerów pod nieudokumentowaną blokadą, idempotentne zamykanie uchwytu, brak niejawnej asynchroniczności, błąd dodatku nie zasłania pierwotnej przyczyny bez jawnej decyzji.

### D2: rodzina refaktoryzacji Composite

**Cel:** zastąpić ukryte drzewo jawnym modelem Composite i przećwiczyć powiązane techniki: jeden kontrakt dla jednego i wielu, Builder, Collecting Parameter i Visitor.

**Czas:** ok. 60-90 minut.

Pliki wyjściowe:

- `src/main/java/pl/training/module5/composite/before/LegacyPathPlan.java` - plan jako płaska lista `Entry(path, minutes)`; hierarchia istnieje tylko w konwencji ścieżki, np. `release/database/backup`,
- `src/main/java/pl/training/module5/composite/before/LegacyPlanExecutor.java` - para metod `execute(Task)` i `executeAll(List<Task>)`.

Polecenia:

1. Scharakteryzuj separator, kolejność i niepoprawne ścieżki (także `release/a/first`, `release/b/second`, `release/a/third` oraz ścieżkę będącą jednocześnie zadaniem i grupą).
2. Wprowadź wspólny kontrakt węzła, liść i grupę oraz mapper starego formatu z jawną polityką dla danych niejednoznacznych.
3. Zastąp rozróżnienie jeden lub wiele jednym kontraktem; enkapsuluj konstrukcję drzewa w Builderze.
4. Przenieś zbieranie liści do parametru zbierającego i dodaj osobny Visitor liczący czas.
5. Przetestuj puste drzewo, głęboką strukturę, przepełnienie i współdzielone poddrzewo.

Kryteria: niemutowalny wynik Buildera, jawna kolejność przechodzenia, brak zawijania przy przepełnieniu, każdy Visitor obsługuje wszystkie rodzaje węzłów, zmiana formatu trwałego oddzielona od refaktoryzacji modelu.

### D3: Mediator

**Cel:** zastąpić siatkę zależności między komponentami formularza jednym mediatorem, zachowując stan formularza po każdym zdarzeniu.

**Czas:** ok. 30 minut.

Pliki wyjściowe: `src/main/java/pl/training/module5/mediator/before/LegacyReleaseForm.java` oraz komponenty `VersionField.java`, `ApproverPicker.java`, `PublishButton.java`, `StatusLabel.java` w tym samym pakiecie; migawka `src/main/java/pl/training/module5/mediator/FormState.java`.

Polecenia:

1. Narysuj zależności między komponentami. Znajdź logikę skopiowaną w dwóch komponentach.
2. Napisz test, który dla sekwencji co najmniej 10 zdarzeń (w tym ignorowanych i cofających stan) porównuje migawkę formularza po każdym zdarzeniu.
3. Wprowadź interfejs mediatora i przenoś reguły jednego komponentu naraz.
4. Kontrolna mutacja: zmień kolejność dwóch aktualizacji w mediatorze. Który przypadek to wykrywa?

Kryteria: komponenty znają tylko mediator, migawki są identyczne po każdym zdarzeniu, metody ustawiające stan komponentów nie wywołują ponownie mediatora. Wariant CineLegacy: `scripts/warsztat.sh test m5/s32` - ekran wyboru miejsc (mapa, koszyk, podsumowanie, przycisk płatności) z 11 wzajemnymi referencjami.

---

## Sprawdzenie wiedzy

1. Jakie sygnały wskazują, że wzorzec w kodzie należy usunąć, a nie rozbudować?
2. Co trzeba sprawdzić przed usunięciem pośrednika (Remove Middle Man)?
3. Dlaczego Inline Singleton może być zmianą kontraktu, a nie czystą refaktoryzacją?
4. Kto wybiera wariant i kiedy - w Strategy, w Command i w State?
5. Dlaczego wybór strategii w konstruktorze może zmienić zachowanie?
6. Kiedy mapy komend nie można uznać za równoważną łańcuchowi warunków?
7. Po czym poznać, że haki Template Method niosą więcej niż jedną oś zmienności?
8. Co traci kod klienta po zastąpieniu podklas delegacją?
9. Dlaczego ogniwa łańcucha zatwierdzania nie są od siebie niezależne?
10. Czym różni się Chain of Responsibility od Decoratora? Gdzie umieścisz audyt decyzji?
11. Co tłumaczy Adapter, a co Boundary Object?
12. Jak walidacja na granicy zmienia zachowanie dla danych niepoprawnych?
13. Czym różni się Facade od Anti-Corruption Layer?
14. Kiedy fasada staje się Middle Manem, a kiedy God Class?
15. Czym różni się Observer od Mediatora?
16. Kiedy `switch` z dopasowaniem wzorców po typie `sealed` jest lepszy od Visitora?
17. Czego nie widzi automatyczny Rename w IDE?
18. Kiedy prosty `switch` jest lepszy od wzorca?
