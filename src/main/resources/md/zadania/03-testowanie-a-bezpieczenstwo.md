# Moduł 3. Testowanie a bezpieczeństwo - zadania dla uczestników

## Jak pracować

- Pracujemy w krótkich iteracjach. Po każdej transformacji kod ma się kompilować, a adekwatne testy mają być zielone.
- W każdym kroku wiedz, w jakim trybie pracujesz: strukturalnym (zachowujesz obserwacje) czy funkcjonalnym (celowo je zmieniasz). Nie mieszaj tych trybów w jednym kroku.
- Zanim cokolwiek zmienisz, nazwij obserwowalne zachowanie (wynik, wyjątki, stan, efekty i ich kolejność), jego konsumenta i sposób weryfikacji.
- Każdy nowy test sprawdź **kontrolną mutacją**: zepsuj na chwilę regułę, zobacz czerwony test, przywróć kod. Test, który nie zawodzi po mutacji, niczego nie chroni.
- Zapisuj małe punkty przywracania (np. lokalne commity albo `git stash`), aby móc wrócić do ostatniego znanego zielonego stanu.
- Nie aktualizuj oczekiwań w teście, dopóki nie rozumiesz, skąd bierze się różnica.

### Przygotowanie środowiska

1. Sprawdź `mvn -version` i potwierdź, że Maven korzysta z Javy 25.
2. Przejdź do katalogu głównego repozytorium.
3. Uruchom `mvn clean verify`. Wszystkie testy powinny być zielone, a raport pokrycia powstanie w `target/site/jacoco/index.html`.
4. Pracuj w pakiecie `pl.training.module3` (`src/main/java/pl/training/module3` i `src/test/java/pl/training/module3`).
5. Ustal sposób zapisywania punktów przywracania.

Szybkie uruchomienie samych testów modułu:

```bash
mvn test -Dtest='pl.training.module3.**'
```

Odpowiedniki w innych językach (jeśli pracujesz w C# lub TypeScript): `csharp/src/Training.Module3` z testami w `csharp/tests/Training.Module3.Tests` (`cd csharp && dotnet test tests/Training.Module3.Tests`) oraz `typescript/src/module3` z testami w `typescript/test/module3` (`cd typescript && npm ci && npx vitest run test/module3`). Nazwy klas są takie same jak w Javie.

### Przegląd zadań

| Nr | Nazwa | Czas | Forma |
| --- | --- | --- | --- |
| Aktywność 1.8 | Zaprojektuj portfel | 5 min | analiza, dyskusja |
| Aktywność 2.7 | Nazwij rolę | 3 min | analiza kodu testu |
| Aktywność 3.7 | Raport bez celu | 4 min | praca z raportem JaCoCo |
| Aktywność 4.20 | Pierwsza sieć bez testów | 4 min | analiza, dyskusja |
| Ćwiczenie 1 | Charakterystyka i refaktoryzacja formatera | 25 min + 10 min omówienia | kod |
| Ćwiczenie 2 | Obiekty zastępcze i pokrycie | 15 min + 5 min omówienia | analiza + kod |
| Ćwiczenie 3 | Wprowadzenie szwu | 20 min + 10 min omówienia | kod |
| Ćwiczenie 4 | Równoległa weryfikacja i jej granice | 10 min + 5 min omówienia | eksperyment + analiza |

---

## Krótkie aktywności

### Aktywność 1.8: Zaprojektuj portfel

**Czas:** 5 minut.

**Cel:** dobrać zestaw testów do rzeczywistych ryzyk zamiast do etykiet warstw piramidy.

**Kontekst:** system nalicza opłatę, zapisuje wynik w PostgreSQL i publikuje komunikat. Ostatnie regresje dotyczyły:

- wartości granicznej w regule naliczania,
- mapowania `BigDecimal` do bazy,
- niezgodnego schematu komunikatu.

**Polecenia:**

1. Zaproponuj minimalny portfel testów dla tego systemu.
2. Dla każdego testu podaj:
   - wykrywane ryzyko,
   - rzeczywiste zależności (co jest prawdziwe, a co zastąpione),
   - oczekiwany czas wykonania,
   - miejsce w procesie weryfikacji (kiedy test jest uruchamiany).
3. Opisz testy przez ich właściwości (zakres, realizm, szybkość, diagnostyka), a nie wyłącznie przez nazwę warstwy.

**Oczekiwany produkt:** krótka tabela portfela, w której każda z trzech historycznych regresji jest wykrywana przez co najmniej jeden test.

---

### Aktywność 2.7: Nazwij rolę

**Czas:** 3 minuty.

**Cel:** rozpoznawać role obiektów zastępczych po sposobie użycia, a nie po nazwie zmiennej.

**Pliki:** `src/test/java/pl/training/module3/OrderPlacementServiceTest.java`.

**Polecenia:**

1. W teście `verifiesPaymentProtocolUsingMock` znajdź `publisherStub = event -> {}`. Czy to dummy, czy stub? Uzasadnij jednym zdaniem.
2. Który obiekt zastępczy w teście `placesOrderUsingStubFakeAndSpy` wykryłby **podwójne** obciążenie karty? Jeśli żaden, wskaż test, który to robi.
3. Opisz sytuację, w której zastąpienie bramki płatności w teście łamałoby regułę „nie mockuj tego, czego nie posiadasz”.

**Oczekiwany produkt:** trzy krótkie odpowiedzi.

---

### Aktywność 3.7: Raport bez celu

**Czas:** 4 minuty.

**Cel:** nauczyć się zadawać raportowi pokrycia właściwe pytania.

**Pliki:**

- `src/main/java/pl/training/module3/DeliveryFee.java`
- `src/test/java/pl/training/module3/DeliveryFeeLineCoverageTest.java`

**Polecenia:**

1. Uruchom `mvn clean verify`.
2. Otwórz raport JaCoCo `target/site/jacoco/index.html` i znajdź klasę `DeliveryFee` w pakiecie `pl.training.module3`.
3. Odpowiedz:
   1. Która gałąź nie została wykonana?
   2. Jaki test ją uruchomi?
   3. Jaka asercja wykryje zmianę `100` na `200`?
   4. Czy podniesienie globalnego pokrycia przez testy innych klas zmniejszy to ryzyko?

**Oczekiwany produkt:** cztery krótkie odpowiedzi oraz propozycja jednego testu (nazwa i asercja).

---

### Aktywność 4.20: Pierwsza sieć bez testów

**Czas:** 4 minuty.

**Cel:** dobrać pierwszy ruch do sytuacji, w której kod nie ma testów i nie da się go uruchomić w teście.

**Kontekst:** klasa `ShowtimeReminderJob` wysyła przypomnienia na 2 godziny przed seansem. W metodzie `run()` sama tworzy połączenie z produkcyjną bazą (`new LegacyDatabase()`), czyta `LocalDateTime.now()` i woła statyczny `ReminderMailer.send(...)`, a po wysyłce oznacza rezerwację jako przypomnianą. Konstruktor bazy rzuca wyjątek poza serwerownią. Za tydzień dochodzi przypomnienie SMS.

**Polecenia:**

1. Co można zaobserwować dziś, bez zmiany kodu?
2. Który ruch automatem IDE otwiera pierwszy szew i czego przy nim nie wolno zmienić (pomyśl o momencie otwarcia połączenia)?
3. Co zapiszesz po godzinie scratch refactoringu tej klasy, zanim wykonasz `git reset`?
4. Dlaczego równoległa weryfikacja nie jest tu dobrym pierwszym krokiem?

**Oczekiwany produkt:** cztery krótkie odpowiedzi i kolejność pierwszych trzech ruchów.

---

## Ćwiczenie 1: Charakterystyka i refaktoryzacja formatera

**Cel:** zabezpieczyć zastane zachowanie testami charakteryzującymi, sprawdzić ich czułość, a następnie rozdzielić odpowiedzialności formatera w serii małych, bezpiecznych kroków.

**Czas:** 25 minut pracy i 10 minut omówienia.

### Kontekst

Formater faktury nie ma testów wymagań, a jego tekst trafia do klientów i księgowości. Nikt nie potrafi z pamięci podać wszystkich reguł zaokrąglania. Zanim zmienisz strukturę, musisz zapisać, co kod **robi dziś**, razem z regułami, które wyglądają podejrzanie.

### Pliki wejściowe

- `src/main/java/pl/training/module3/LegacyInvoiceFormatter.java` - klasa legacy łącząca normalizację klienta, obliczenia, zaokrąglenia i budowę tekstu,
- `src/main/java/pl/training/module3/InvoiceLine.java` - obiekt wartości `InvoiceLine(String sku, int quantity, BigDecimal unitPrice)`,
- `src/test/java/pl/training/module3/LegacyInvoiceFormatterCharacterizationTest.java` - istniejące testy charakteryzujące.

Refaktoryzację wykonuj na własnej kopii klasy legacy (np. `WorkshopInvoiceFormatter` w tym samym pakiecie) albo na osobnej gałęzi. Oryginalna klasa `LegacyInvoiceFormatter` ma pozostać niezmieniona, aby można było porównać wyniki obu wersji. Nie otwieraj `InvoiceFormatter.java` ani `FormatterEquivalenceTest.java` - to rozwiązanie referencyjne.

### Polecenia

1. Przeczytaj kod `LegacyInvoiceFormatter` bez zmieniania go i wypisz wszystkie widoczne reguły (tekst, obliczenia, zaokrąglenia, białe znaki).
2. Uruchom istniejące testy charakteryzujące.
3. Zbuduj macierz przypadków obejmującą co najmniej poniższe obserwacje. Zaznacz, które są już zabezpieczone istniejącymi testami, i dodaj testy tylko dla brakujących:
   - pusta lista pozycji,
   - klient `null`,
   - cena wymagająca zaokrąglenia,
   - wiele pozycji, dla których suma wartości niezaokrąglonych różni się od sumy wartości prezentowanych.
4. Sprawdź czułość nowych testów kontrolną mutacją (np. sumowanie wartości zaokrąglonych zamiast dokładnych). Przywróć kod.
5. Ustal, które reguły są potwierdzonym kontraktem, a które tylko zastanym zachowaniem.
6. Refaktoryzuj w małych krokach, wydzielając kolejno: normalizację klienta, wartość wiersza, sumę, zaokrąglenie oraz formatowanie pozycji. Używaj automatów IDE (Extract Method, Extract Constant).
7. Po każdym kroku uruchom adekwatny test.
8. Porównaj wynik `LegacyInvoiceFormatter` i swojej nowej wersji dla reprezentatywnych danych (test parametryzowany uruchamiający obie implementacje).
9. Obejrzyj końcową różnicę (`git diff`) i wskaż każdą zmianę, której nie wymagał cel ćwiczenia.

### Ograniczenia

- Nie zmieniaj stawki podatku ani sposobu zaokrąglania.
- Nie dodawaj walidacji, wyjątków ani obsługi walut.
- Nie zmieniaj tekstu, białych znaków ani końcowego znaku nowego wiersza.
- Nie łącz wszystkich wydzieleń w jedną dużą edycję.

### Kryteria akceptacji

- Wszystkie testy są zielone.
- Każdy nowy test charakteryzujący zawodzi po kontrolnej mutacji, którą miał wykryć.
- Każda transformacja ma jedną rozpoznawalną intencję.
- Wynik starej i nowej wersji jest zgodny dla przygotowanych danych.
- Potrafisz wskazać ograniczenia uzyskanego dowodu (czego test różnicowy nie wykryje).

### Oczekiwany produkt

Lista reguł, macierz przypadków, uzupełnione testy charakteryzujące, zrefaktoryzowana klasa, test porównujący obie implementacje, lista kroków transformacji.

---

## Ćwiczenie 2: Obiekty zastępcze i pokrycie

**Cel:** nazywać role obiektów zastępczych według katalogu i ich użycia w teście, rozumieć granice każdego z nich i poprawnie interpretować raport pokrycia.

**Czas:** 15 minut pracy i 5 minut omówienia.

### Część A: role obiektów zastępczych

#### Kontekst

Usługa składania zamówienia ma czterech współpracowników przekazanych przez konstruktor. Dwa testy używają ich w różnych rolach i weryfikują różne rzeczy.

#### Pliki wejściowe

- `src/main/java/pl/training/module3/OrderPlacementService.java` - usługa z czterema współpracownikami: `ProductCatalog`, `PaymentGateway`, `OrderRepository`, `EventPublisher`,
- `src/test/java/pl/training/module3/OrderPlacementServiceTest.java` - dwa testy: `placesOrderUsingStubFakeAndSpy` i `verifiesPaymentProtocolUsingMock`.

#### Polecenia

Dla każdego współpracownika w każdym z dwóch testów (`catalogStub`, `paymentStub`, `repositoryFake`, `publisherSpy`, `paymentMock`, `publisherStub`) odpowiedz:

1. Jaką rolę z katalogu (dummy, stub, spy, mock, fake) pełni w danym teście?
2. Czy test sprawdza stan, wynik czy interakcję (weryfikacja stanu czy zachowania)?
3. Jaki defekt może wykryć?
4. Jakiego defektu nie może wykryć?
5. Która implementacja typu fake jest kandydatem do wspólnego testu kontraktu z produkcyjnym adapterem i jak można obserwować skutki obu implementacji?

Następnie oceń, czy sprawdzanie dokładnej kolejności zapisu zamówienia i publikacji zdarzenia byłoby uzasadnione. Odpowiedź musi odwoływać się do konkretnego kontraktu transakcyjnego, a nie do aktualnej kolejności linii kodu.

### Część B: raport pokrycia

#### Pliki wejściowe

- `src/main/java/pl/training/module3/DeliveryFee.java`
- `src/test/java/pl/training/module3/DeliveryFeeLineCoverageTest.java`

#### Polecenia

1. Uruchom `mvn clean verify` (jeśli nie zrobiłeś tego w Aktywności 3.7).
2. Otwórz raport pakietu `pl.training.module3` (`target/site/jacoco/index.html`).
3. Znajdź `DeliveryFee` i wyjaśnij różnicę między pokryciem linii i gałęzi.
4. Dopisz test, który wykona brakującą gałąź i wykryje zmianę opłaty `100` na `200`. Sprawdź to kontrolną mutacją: zmień tymczasowo `100` na `200`, uruchom test, przywróć kod.
5. Podaj przykład testu, który zwiększyłby pokrycie, ale nie chronił reguły opłaty. Wskaż taki test w pakiecie `pl.training.module3`.
6. Zaproponuj sposób użycia progu pokrycia, który nie zamieni go w cel sam w sobie.

### Kryteria akceptacji

- Role są nazwane według użycia w teście, a nie według nazwy zmiennej.
- Wskazano granice każdego obiektu zastępczego.
- Weryfikacja interakcji jest uzasadniona kontraktem.
- Brakująca gałąź ma asercję dotyczącą wartości biznesowej.
- Wniosek z raportu nie sprowadza jakości do jednego procentu.

### Oczekiwany produkt

Tabela sześciu obiektów zastępczych (rola, rodzaj weryfikacji, wykrywa, nie wykrywa), krótka ocena testu kolejności, nowy test `DeliveryFee` oraz trzy zdania o progu pokrycia.

---

## Ćwiczenie 3: Wprowadzenie szwu

**Cel:** udostępnić kod do deterministycznego testowania przez minimalny szew przejściowy, a następnie doprowadzić go do projektu z jawnymi zależnościami.

**Czas:** 20 minut pracy i 10 minut omówienia.

### Kontekst

Usługa wysyła przypomnienie o odnowieniu subskrypcji. Nie ma testów, bo czyta globalny czas i drukuje wiadomość na standardowe wyjście. Zespół ma wkrótce zmienić kanał wysyłki, więc najpierw potrzebny jest test, który kontroluje czas i widzi wysyłkę.

### Pliki wejściowe

- `src/main/java/pl/training/module3/LegacyReminderService.java` - usługa wysyłająca przypomnienie o odnowieniu subskrypcji,
- `src/main/java/pl/training/module3/Subscription.java` - `Subscription(String email, LocalDate renewalDate)`.

Kluczowy fragment kodu wyjściowego:

```java
public boolean sendRenewalReminder(Subscription subscription) {
    LocalDate today = LocalDate.now(ZoneOffset.UTC);

    if (subscription.renewalDate().isAfter(today.plusDays(7))) {
        return false;
    }

    System.out.printf(
            "Sent renewal reminder to %s for %s%n",
            subscription.email(),
            subscription.renewalDate());
    return true;
}
```

Klasę legacy pozostaw bez zmian jako punkt odniesienia. Wersję ze szwem i wersję docelową utwórz pod własnymi nazwami (np. `WorkshopSeamedReminderService` i `WorkshopReminderService`) albo pracuj na osobnej gałęzi.

### Polecenia

1. Wskaż niedeterministyczne wejście oraz efekt trudny do obserwacji. Nazwij, który obiekt zastępczy będzie potrzebny dla każdego z nich.
2. Nazwij górną granicę reguły oraz konsekwencję braku granicy dolnej.
3. Wprowadź minimalny szew przez owinięcie czasu i wysyłki metodami możliwymi do nadpisania (automat IDE: Extract Method z widocznością `protected`).
4. Napisz deterministyczne testy dla odnowienia dokładnie za siedem dni, za osiem dni oraz dla daty przeszłej.
5. Zastąp szew przejściowy jawnymi zależnościami `Clock` i `ReminderGateway` (interfejs z metodą `void send(String email, LocalDate renewalDate)`).
6. Przenieś testy na docelowy projekt.
7. Wskaż punkt aktywacji każdego szwu.
8. Wyjaśnij, jaki test byłby potrzebny dla rzeczywistego adaptera komunikacyjnego.

### Ograniczenia

- Nie zmieniaj reguły biznesowej (w tym braku dolnej granicy) podczas rozrywania zależności.
- Nie testuj przez zmianę zegara systemowego ani przechwytywanie `System.out`.

### Kryteria akceptacji

- Test nie zależy od bieżącej daty ani globalnego wyjścia.
- Górna granica siedmiu dni jest sprawdzona po obu stronach, a zachowanie dla daty przeszłej jest jawnie scharakteryzowane.
- Kod domenowy nie tworzy konkretnego adaptera.
- Rozwiązanie przejściowe jest odróżnione od projektu docelowego.
- Potrafisz wskazać potrzebę testu integracyjnego adaptera.

### Oczekiwany produkt

Klasa ze szwem przejściowym i jej testy, klasa docelowa z `Clock` i `ReminderGateway` z przeniesionymi testami, nazwane punkty aktywacji oraz jedno zdanie o teście adaptera.

---

## Ćwiczenie 4: Równoległa weryfikacja i jej granice

**Cel:** zobaczyć, jak tryb weryfikacji ujawnia rozbieżność bez zmiany odpowiedzi dla klienta, oraz ocenić, dla jakich operacji podwójne wykonanie jest bezpieczne.

**Czas:** 10 minut pracy i 5 minut omówienia.

### Kontekst

Stary kalkulator ceny działa na krytycznej ścieżce, a reguły zaokrąglania nie są w pełni udokumentowane. Zespół przygotował nową implementację i chce zdobyć dowód zgodności na prawdziwym ruchu, zanim nowa implementacja stanie się autorytatywna.

### Pliki wejściowe

- `src/main/java/pl/training/module3/incremental/MigratingPricingEngine.java` - router z trybami `LEGACY`, `VERIFY`, `CANDIDATE`,
- `src/main/java/pl/training/module3/incremental/CandidatePricingEngine.java` - nowa implementacja,
- `src/main/java/pl/training/module3/incremental/LegacyPricingEngineAdapter.java` - stara implementacja za abstrakcją `PricingEngine`,
- `src/test/java/pl/training/module3/incremental/PricingEngineContractTest.java`, `src/test/java/pl/training/module3/incremental/MigratingPricingEngineTest.java`,
- `src/main/java/pl/training/module3/Module3Examples.java` - metoda `runParallelVerificationExample`.

### Polecenia

1. Uruchom `Module3Examples` i znajdź linię `Parallel verification: ...`. Co oznaczają obie części wyniku?
2. Kontrolna mutacja: w `CandidatePricingEngine` usuń zaokrąglenie rabatu przed odejmowaniem (`.setScale(MONEY_SCALE, ROUNDING)`). Uruchom `mvn test -Dtest='pl.training.module3.incremental.**'`. Który test zawodzi, a który nie i dlaczego?
3. Nie przywracając mutacji, zastanów się: jakie zdarzenie zgłosiłby router w trybie `VERIFY` dla żądania `0.01 x 1` z rabatem `0.5` i jaką cenę dostałby klient? Przywróć kod.
4. Dla każdej operacji zdecyduj, czy wolno ją wykonać jednocześnie starą i nową ścieżką w trybie shadow, a jeśli nie, co porównać zamiast niej: wycena biletu, obciążenie karty, wysłanie maila z potwierdzeniem, nadanie kolejnego numeru rezerwacji, zapis audytu.
5. Wymień dwa warunki, bez których tryb `VERIFY` z tego przykładu nie nadaje się na produkcję.

### Kryteria akceptacji

- Odpowiedź odróżnia test kontraktowy implementacji od testu routera na obiektach zastępczych.
- Klient w trybie `VERIFY` zawsze dostaje wynik legacy, także przy awarii kandydata.
- Każda operacja z efektem ubocznym ma wskazany sposób porównania bez ponownego wykonania.
- Zgodność implementacji nie jest traktowana jako dowód poprawności.

### Oczekiwany produkt

Odpowiedzi na pytania 1-3, tabela pięciu operacji (shadow tak/nie, co porównać) oraz dwa warunki produkcyjne.

---

## Sprawdzenie wiedzy

1. Co dokładnie oznacza zielony zestaw testów?
2. Dlaczego piramida testów nie narzuca uniwersalnych proporcji?
3. Czym punkt zmiany różni się od punktu testowania?
4. Czym stub różni się od mocka, a spy od fake?
5. Kiedy weryfikacja zachowania (interakcji) jest uzasadniona?
6. Co oznacza reguła „nie mockuj tego, czego nie posiadasz” i co robić zamiast tego?
7. Jakie ryzyko pozostaje po zastąpieniu bazy implementacją działającą w pamięci?
8. Co pokazuje pokrycie gałęzi, czego może nie pokazać pokrycie linii?
9. Dlaczego test charakteryzujący nie dowodzi poprawności wymagania?
10. Kiedy golden master przestaje chronić?
11. Czym różni się seam od punktu aktywacji?
12. Które ruchy automatów IDE są bezpieczne bez testów i kiedy przestają takie być?
13. Co jest produktem scratch refactoringu?
14. Dlaczego tryb shadow jest bezpieczny tylko dla czystych obliczeń?
