# Moduł 8. Strategie i dobre praktyki - zadania dla uczestników

## Jak pracować

Ten moduł dotyczy mniej pojedynczych przekształceń kodu, a bardziej sposobu prowadzenia zmian w systemie legacy: planowania migracji, dzielenia pracy na zestawy zmian gotowe do przeglądu, dokumentowania decyzji, konfigurowania narzędzi i zarządzania ryzykiem wdrożenia.

Zasady pracy:

- Pracujecie w parach lub zespołach 3-4 osobowych. Każdy warsztat kończy się krótką prezentacją produktu (5 minut na zespół).
- Produktem warsztatu jest przede wszystkim dokument roboczy (plan, opis serii zmian, konfiguracja), a tam, gdzie polecenie tego wymaga, także kod i testy.
- Zanim zaczniecie cokolwiek zmieniać, upewnijcie się, że projekt się kompiluje, a testy są zielone:
  - Java: `mvn -q compile` oraz `mvn test`
  - C#: `cd csharp && dotnet build && dotnet test`
  - TypeScript: `cd typescript && npm ci && npm run build && npm test`
- Każdy krok w kodzie wykonujcie małymi porcjami: kompilacja, zawężone testy, przegląd diffu. Nie łączcie zmian strukturalnych ze zmianą zachowania.
- Jeżeli czegoś nie wiecie o zachowaniu kodu, zapiszcie to jako założenie albo pytanie. Nie zgadujcie po cichu.
- Czas podany przy warsztatach jest orientacyjny i może zostać zmieniony przez prowadzącego.

Ścieżki w poleceniach dotyczą wersji Java. Odpowiedniki w C# znajdują się w `csharp/src/Training.Module8`, a w TypeScript w `typescript/src/module8` (te same nazwy klas i pakietów, zapisane zgodnie z konwencjami języka).

---

## Warsztat 1. Plan przyrostowej wymiany implementacji

### Cel

Zaplanować migrację kalkulatora używanego przez krytyczną ścieżkę bez jednorazowego przełączenia.

### Czas

Orientacyjnie 60-75 minut (40-50 minut pracy w zespołach, reszta na prezentacje i dyskusję).

### Kontekst

Zespół utrzymuje system sprzedażowy. Na krytycznej ścieżce składania zamówienia wywoływany jest stary kalkulator ceny netto. Biznes chce zastąpić go nową implementacją (lepiej testowalną, z czytelnym modelem danych), ale:

- kalkulator jest wywoływany przy każdym zamówieniu, nie ma okna serwisowego,
- błędna cena oznacza realne straty finansowe albo reklamacje,
- zespół nie ma pewności, czy wszystkie reguły zaokrąglania są udokumentowane,
- stary kod nadal jest wywoływany bezpośrednio z kilku miejsc.

### Dane wejściowe

- Stara implementacja: `src/main/java/pl/training/module8/incremental/LegacyPriceCalculator.java`
  - metoda `BigDecimal calculate(BigDecimal unitPrice, int quantity, BigDecimal discountPercent)`,
  - rabat podawany jako wartość procentowa od `0` do `100`.
- Przykładowe dane do analizy kontraktu (policzcie je ręcznie albo w teście charakteryzującym na starej klasie):

| unitPrice | quantity | discountPercent |
| --- | --- | --- |
| `10.00` | 3 | `0` |
| `10.00` | 3 | `100` |
| `0.01` | 1 | `50` |
| `19.995` | 2 | `12.555` |
| `0.05` | 3 | `33.33` |
| `-0.001` | 1 | `0` |
| `10.00` | 0 | `0` |
| `10.00` | 1 | `100.01` |

### Polecenia

1. Przeczytajcie `LegacyPriceCalculator` i zapiszcie jego **kontrakt funkcjonalny**: walidację (kolejność, typy wyjątków, komunikaty), skale i tryb zaokrąglania, moment zaokrąglania rabatu, wynik dla danych z tabeli.
2. Zapiszcie **kontrakt operacyjny**: czy obliczenie ma skutki uboczne, czy jest deterministyczne, jakie są oczekiwania co do opóźnienia, jak często jest wywoływane.
3. Wskażcie **najmniejszą abstrakcję** potrzebną klientowi (nazwa interfejsu, sygnatura metody, typy wejścia i wyjścia). Uzasadnijcie, czym różni się od API starego kalkulatora (np. jednostka rabatu).
4. Zaprojektujcie **adapter** umieszczający starą implementację za abstrakcją oraz **wspólny test kontraktowy**, który będzie uruchamiany dla każdej implementacji. Wypiszcie konkretne przypadki testowe, w tym przynajmniej jeden chroniący moment zaokrąglenia.
5. Zdecydujcie, czy **tryb shadow** (równoległe liczenie starą i nową implementacją z porównaniem wyników) jest tu bezpieczny. Opiszcie: która implementacja jest autorytatywna, co się dzieje przy awarii kandydata, co przy awarii kanału raportowania, jakie są koszty wydajnościowe.
6. Zdefiniujcie **tryby pracy i etapy przełączenia** oraz **kohorty** ruchu. Opiszcie, jak routing będzie deterministyczny i jak będzie obserwowany.
7. Zapiszcie kryteria decyzji **`ADVANCE`**, **`HOLD`** i **`ROLLBACK`** dla każdego etapu (metryki, progi, minimalna próbka, czas obserwacji, właściciel decyzji).
8. Zdefiniujcie **warunki usunięcia starej ścieżki** i każdego elementu przejściowego (adapter, router, flaga, telemetria).
9. W planie oznaczcie każdy krok jako: refaktoryzacja, migracja albo zmiana zachowania.
10. Opcjonalnie (dla szybszych zespołów): zaimplementujcie abstrakcję, adapter i test kontraktowy w nowym pakiecie roboczym (np. `pl.training.module8.workshop1`) i uruchomcie test dla adaptera.

### Kryteria akceptacji / oczekiwany produkt

Produktem jest plan migracji (1-2 strony) oraz lista przypadków testu kontraktowego. Plan jest zaakceptowany, gdy:

- plan rozróżnia refaktoryzację od migracji i zmiany zachowania,
- kandydat nie powiela nieidempotentnych efektów,
- obie implementacje przechodzą wspólny test kontraktowy (w planie: ten sam zestaw przypadków jest uruchamiany dla obu),
- routing jest deterministyczny i obserwowalny,
- wycofanie uwzględnia kod, konfigurację i dane,
- każdy element przejściowy ma właściciela oraz kryterium usunięcia.

---

## Warsztat 2. Seria zmian gotowych do przeglądu

### Cel

Podzielić szeroką zmianę legacy na samodzielne jednostki o czytelnej intencji.

### Czas

Orientacyjnie 60-75 minut (część praktyczna w kodzie około 40 minut).

### Kontekst

Zespół dostał zadanie biznesowe: raport z wydania ma dodatkowo pokazywać liczbę nieudanych wdrożeń w ostatnim wierszu (np. `Successful: 1/2, Failed: 1/2`). Osoba, która otworzyła klasę formatera, zauważyła jednocześnie nieczytelne nazwy zmiennych, wielokrotną konkatenację napisów i powielony format wiersza. Pierwsza wersja zmiany, przygotowana w pośpiechu, zawierała wszystko naraz w jednym commicie: zmianę nazw, przeformatowanie pliku, przebudowę pętli, nową regułę raportu i poprawione testy. Osoba przeglądająca odmówiła akceptacji, bo nie była w stanie ocenić, czy stare zachowanie zostało zachowane.

Waszym zadaniem jest przygotować tę samą pracę ponownie, jako serię małych zestawów zmian.

### Dane wejściowe

- Klasa do zmiany: `src/main/java/pl/training/module8/boyscout/before/ReleaseSummaryFormatter.java`
- Model danych wejściowych (nie jest przedmiotem zmiany): `src/main/java/pl/training/module8/boyscout/DeploymentResult.java`, `src/main/java/pl/training/module8/boyscout/DeploymentStatus.java`
- Pracujcie na kopii klasy w nowym pakiecie roboczym (np. `pl.training.module8.workshop2`), tak aby oryginał pozostał punktem odniesienia do porównania.
- Każdy krok zapisujcie jako osobny commit na własnej gałęzi roboczej.

### Polecenia

1. **Odkrycie zachowania.** Zanim zmienicie strukturę, zapiszcie obecny kontrakt `format(...)`: postać wyniku, obsługę spacji w `releaseId`, kolejność walidacji, typy wyjątków i komunikaty (w tym dla `null` na liście wyników i dla kombinacji kilku błędnych argumentów).
2. Przygotujcie **testy charakteryzujące** ten kontrakt. Dla każdego testu zdecydujcie, czy utrwala zachowanie zamierzone, czy przypadkowe, i zapiszcie tę decyzję.
3. **Refaktoryzacja bez zmiany zachowania.** Zaplanujcie i wykonajcie lokalne porządki (nazwy, wydzielenie walidacji i formatowania wiersza, sposób budowania napisu). Nie zmieniajcie sygnatury publicznej ani walidacji.
4. **Zmiana zachowania.** W osobnym zestawie zmian zaimplementujcie nową regułę ostatniego wiersza raportu wraz z testem opisującym nowe wymaganie.
5. Jeśli chcecie przeformatować plik lub użyć automatycznej transformacji IDE, zróbcie to jako osobny krok mechaniczny, niezmieszany z ręcznymi poprawkami.
6. Po **każdym** kroku: kompilacja, zawężone testy, pełne testy modułu, przegląd diffu. Zapiszcie, jakie polecenie uruchomiliście i jaki był wynik.
7. Dla każdego zestawu zmian przygotujcie **opis** odpowiadający na pięć pytań: dlaczego, co zmienia i czego nie zmienia, jaki kontrakt ma pozostać niezmieniony, jakie dowody potwierdzają wynik, jak wdrożyć, obserwować i wycofać.
8. Wskażcie **kolejność czytania** plików dla osoby przeglądającej.
9. **Przegląd krzyżowy.** Zamieńcie się seriami z innym zespołem. Napiszcie 3-5 komentarzy z przeglądu, oznaczając każdy jako `BLOCKER`, `SUGGESTION` albo `NIT`. Każdy komentarz ma zawierać obserwację, możliwy skutek, uzasadnienie i propozycję lub pytanie.

### Kryteria akceptacji / oczekiwany produkt

Produktem jest seria commitów (lub opis serii, jeśli zabraknie czasu na kod), opisy zestawów zmian i komentarze z przeglądu krzyżowego. Praca jest zaakceptowana, gdy:

- każdy zestaw zmian ma jedną główną intencję,
- system działa po integracji każdego kroku,
- testy nie utrwalają bezkrytycznie przypadkowego zachowania,
- mechaniczne i ręczne zmiany są rozdzielone,
- opis pozwala odtworzyć sposób weryfikacji,
- komentarze z przeglądu rozróżniają blokadę od sugestii.

---

## Warsztat 3. Wprowadzenie bramek do projektu legacy

### Cel

Zwiększyć jakość automatycznej informacji zwrotnej bez blokowania zespołu historycznymi naruszeniami.

### Czas

Orientacyjnie 60 minut.

### Kontekst

Projekt szkoleniowy jest budowany Mavenem z `maven.compiler.release` ustawionym na `25`. Poza domyślnymi ustawieniami kompilatora nie ma dziś żadnych bramek jakości: ostrzeżenia nie są zbierane, nie działa analiza statyczna, nikt nie wie, ile historycznych naruszeń zawiera kod. Zespół chce wprowadzić bramki tak, by nowe problemy nie trafiały do repozytorium, ale jednocześnie by jutro nikt nie został zablokowany przez problemy sprzed lat.

### Dane wejściowe

- Konfiguracja budowania: `pom.xml` (katalog główny repozytorium).
- Kod produkcyjny: `src/main/java/pl/training/**` (wszystkie moduły szkolenia traktujemy jak jeden system legacy).
- Narzędzie do eksperymentów z polityką ostrzeżeń: `src/main/java/pl/training/module8/tooling/InMemoryJavaCompiler.java` wraz z `WarningPolicy`, `CompilationResult` i `CompilationDiagnostic` w tym samym pakiecie. Pozwala skompilować w pamięci pojedynczy plik źródłowy z polityką `ALLOW_WARNINGS` albo `TREAT_WARNINGS_AS_ERRORS`.
- Polecenia pomocnicze:
  - wersja JDK i Mavena: `java -version`, `mvn -v`
  - kompilacja wszystkich klas ze wszystkimi ostrzeżeniami, bez zmiany `pom.xml`, np.:
    `javac --release 25 -Xlint:all -d <katalog-tymczasowy> $(find src/main/java -name '*.java')`

### Polecenia

1. **Stan faktyczny środowiska.** Sprawdźcie, jakim JDK uruchamiany jest Maven i jaki poziom języka jest ustawiony w `pom.xml`. Zapiszcie, czy to wystarcza, by zagwarantować kompilację na JDK 25 w CI.
2. **Pomiar bez zmian w kodzie.** Uruchomcie ostrzeżenia kompilatora (`-Xlint:all`) oraz wybrane narzędzie analizy statycznej (np. Checkstyle, PMD lub SpotBugs) bez modyfikowania kodu produkcyjnego. Zapiszcie liczbę i kategorie wyników.
3. **Eksperyment z polityką.** Za pomocą `InMemoryJavaCompiler` skompilujcie mały plik z surowym typem kolekcji (np. parametr `List` bez typu generycznego) z każdą z dwóch polityk. Zapiszcie, jakie diagnostyki (rodzaj i kod) zostały zwrócone i czy kompilacja zakończyła się sukcesem.
4. **Klasyfikacja.** Każdą kategorię wyników sklasyfikujcie według ryzyka (wysokie, średnie, niskie) i pewności (prawdziwy problem, wynik fałszywie dodatni, reguła niedopasowana do polityki projektu, ryzyko świadomie zaakceptowane).
5. **Stan bazowy.** Zaprojektujcie zapis stanu bazowego istniejących naruszeń (format, lokalizacja w repozytorium, kto może go zmieniać).
6. **Bramka.** Zaproponujcie konfigurację budowania (fragment `pom.xml` albo opis), która blokuje nowe naruszenia w nowym i dotykanym kodzie, nie blokując zmian z powodu naruszeń historycznych.
7. **Tłumienie.** Ustalcie format precyzyjnego tłumienia (jaka reguła, jaki zakres, uzasadnienie, właściciel, termin przeglądu). Pokażcie przykład jednego tłumienia.
8. **Plan redukcji.** Zaplanujcie stopniowe zmniejszanie stanu bazowego (kolejność kategorii, rytm, kto odpowiada).
9. Zapiszcie, gdzie w CI będzie dostępny raport i kto jest jego właścicielem.

### Kryteria akceptacji / oczekiwany produkt

Produktem jest krótki raport (pomiar, klasyfikacja, stan bazowy), propozycja konfiguracji bramki oraz plan redukcji. Praca jest zaakceptowana, gdy:

- użyte wersje narzędzi obsługują Javę 25,
- analiza otrzymuje kompletną ścieżkę klas,
- build nie generuje niezwiązanych automatycznych poprawek,
- wynik fałszywie dodatni jest odróżniony od zaakceptowanego ryzyka,
- nowe naruszenia nie powiększają stanu bazowego,
- raport jest dostępny w CI i ma właściciela.

---

## Aktywność dodatkowa A. Zapis decyzji architektonicznej (ADR)

### Cel

Udokumentować jedną istotną decyzję z Warsztatu 1 w formie ADR.

### Czas

Orientacyjnie 20 minut.

### Polecenia

1. Wybierzcie jedną decyzję o trwałym wpływie na architekturę (np. wybór sposobu współistnienia starej i nowej implementacji).
2. Zapiszcie ADR z polami: identyfikator w formacie `ADR-NNNN`, tytuł, status, kontekst, decyzja, co najmniej dwie rozważone opcje z uzasadnieniem, konsekwencje (pozytywne i negatywne, jeżeli występują), metoda weryfikacji.
3. Zastanówcie się, co by się stało z tym dokumentem, gdyby za pół roku zespół zmienił kierunek.

### Oczekiwany produkt

Dokument ADR w Markdown, gotowy do umieszczenia w repozytorium.

---

## Aktywność dodatkowa B. Decyzja o kolejnym etapie wdrożenia

### Cel

Przećwiczyć podejmowanie decyzji `ADVANCE`, `HOLD` albo `ROLLBACK` na podstawie wcześniej ustalonych progów.

### Czas

Orientacyjnie 15 minut.

### Dane wejściowe

Progi uzgodnione przed wdrożeniem: minimalna próbka 100 żądań, maksymalny wskaźnik błędów 5%, maksymalne opóźnienie p95 równe 250 ms, dopuszczalna liczba rozbieżności odpowiedzi względem starej implementacji równa 0.

| Etap | Próbka | Błędy | Rozbieżności | p95 [ms] |
| --- | --- | --- | --- | --- |
| a | 200 | 4 | 0 | 180.0 |
| b | 100 | 5 | 0 | 250.0 |
| c | 99 | 0 | 0 | 120.0 |
| d | 0 | 0 | 0 | 0.0 |
| e | 200 | 0 | 1 | 120.0 |
| f | 100 | 6 | 0 | 200.0 |
| g | 10 | 0 | 0 | 251.0 |
| h | 10 | 1 | 0 | 100.0 |

### Polecenia

1. Dla każdego etapu podejmijcie decyzję i zapiszcie jednozdaniowe uzasadnienie.
2. Zapiszcie regułę, w jakiej kolejności sprawdzacie warunki.
3. Zastanówcie się, czy te progi byłyby właściwe dla innego systemu, np. rekomendacji produktów.

### Oczekiwany produkt

Tabela decyzji z uzasadnieniami i zapisana reguła kolejności sprawdzania warunków.
