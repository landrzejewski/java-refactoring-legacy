# Moduł 2. Fundamenty refaktoryzacji - zadania dla uczestników

## Jak pracować

- Pracujemy w krótkich iteracjach. Po każdej transformacji kod ma się kompilować, a adekwatne testy mają być zielone.
- W każdym kroku wiedz, w jakim trybie pracujesz: strukturalnym (zachowujesz obserwacje) czy funkcjonalnym (celowo je zmieniasz). Nie mieszaj tych trybów w jednym kroku.
- Zanim cokolwiek zmienisz, nazwij obserwowalne zachowanie, jego konsumenta i sposób weryfikacji.
- Zapisuj małe punkty przywracania (np. lokalne commity albo `git stash`), aby móc wrócić do ostatniego znanego zielonego stanu.
- Nie aktualizuj oczekiwań w teście, dopóki nie rozumiesz, skąd bierze się różnica.

### Przygotowanie środowiska

1. Sprawdź `mvn -version` i potwierdź, że Maven korzysta z Javy 25.
2. Przejdź do katalogu `refactoring-legacy`.
3. Uruchom `mvn clean verify`. Wszystkie testy powinny być zielone.
4. Pracuj wyłącznie w pakiecie `pl.training.module2` (`src/main/java/pl/training/module2` i `src/test/java/pl/training/module2`).
5. Ustal sposób zapisywania punktów przywracania.

Szybkie uruchomienie samych testów modułu:

```bash
mvn test -Dtest='pl.training.module2.**'
```

Odpowiedniki w innych językach (jeśli pracujesz w C# lub TypeScript): `csharp/src/Training.Module2` z testami w `csharp/tests/Training.Module2.Tests` (`cd csharp && dotnet test`) oraz `typescript/src/module2` z testami w `typescript/test/module2` (`cd typescript && npm ci && npm test`). Nazwy klas są takie same jak w Javie.

### Przegląd zadań

| Nr | Nazwa | Czas | Forma |
| --- | --- | --- | --- |
| Aktywność 1.6 | Nazwij kontrakt | 4 min | analiza, dyskusja |
| Aktywność 4.6 | Zaprojektuj portfel | 5 min | analiza, dyskusja |
| Aktywność 6.5 | Raport bez celu | 4 min | praca z raportem JaCoCo |
| Ćwiczenie 1 | Granica refaktoryzacji | 15 min + 5 min omówienia | analiza |
| Ćwiczenie 2 | Charakterystyka i refaktoryzacja formatera | 35 min + 10 min omówienia | kod |
| Ćwiczenie 3 | Strategia testów, obiekty zastępcze i pokrycie | 25 min + 10 min omówienia | analiza + kod |
| Ćwiczenie 4 | Wprowadzenie szwu | 25 min + 10 min omówienia | kod |

---

## Krótkie aktywności

### Aktywność 1.6: Nazwij kontrakt

**Czas:** 4 minuty.

**Cel:** przećwiczyć nazywanie zachowania obserwowalnego, zanim zmiana zostanie zaklasyfikowana jako refaktoryzacja lub nie.

**Kontekst:** zachowanie obserwowalne to nie tylko wartość zwracana. Obejmuje również wyjątki, efekty uboczne, protokół (liczbę i kolejność wywołań), format danych, kontrakt publiczny oraz istotne właściwości pozafunkcjonalne.

**Zmiany do oceny:**

1. Zmiana typu prywatnego pola z `List` na `Set`.
2. Zamiana dwóch zapisów do bazy miejscami.
3. Przeniesienie klasy używanej przez mechanizm refleksji.
4. Zastąpienie algorytmu szybszą implementacją.
5. Zmiana prywatnej nazwy bez użycia refleksji i konfiguracji tekstowej.

**Polecenia:**

1. Dla każdej zmiany nazwij konsumenta, który może zauważyć różnicę (kod, system, człowiek, narzędzie).
2. Nazwij konkretną obserwację, która mogłaby się zmienić.
3. Wskaż sposób weryfikacji tej obserwacji.
4. Dopiero na końcu oceń, czy zmiana może być wykonana jako refaktoryzacja.

**Oczekiwany produkt:** tabela o kolumnach: zmiana, konsument, obserwacja, weryfikacja, ocena. Nie klasyfikuj zmiany przed wypełnieniem trzech pierwszych kolumn.

---

### Aktywność 4.6: Zaprojektuj portfel

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

### Aktywność 6.5: Raport bez celu

**Czas:** 4 minuty.

**Cel:** nauczyć się zadawać raportowi pokrycia właściwe pytania.

**Pliki:**

- `src/main/java/pl/training/module2/DeliveryFee.java`
- `src/test/java/pl/training/module2/DeliveryFeeLineCoverageTest.java`

**Polecenia:**

1. Uruchom `mvn clean verify`.
2. Otwórz raport JaCoCo `target/site/jacoco/index.html` i znajdź klasę `DeliveryFee` w pakiecie `pl.training.module2`.
3. Odpowiedz:
   1. Która gałąź nie została wykonana?
   2. Jaki test ją uruchomi?
   3. Jaka asercja wykryje zmianę `100` na `200`?
   4. Czy podniesienie globalnego pokrycia przez testy innych klas zmniejszy to ryzyko?

**Oczekiwany produkt:** cztery krótkie odpowiedzi oraz propozycja jednego testu (nazwa i asercja).

---

## Ćwiczenia warsztatowe

### Ćwiczenie 1: Granica refaktoryzacji

**Czas pracy:** 15 minut. **Omówienie:** 5 minut.

**Cel:** odróżniać refaktoryzację od zmian funkcjonalnych, optymalizacji i migracji na podstawie kontraktu, a nie nazwy operacji.

**Kontekst:** zespół planuje uporządkowanie usługi rozliczeniowej. Dla każdej propozycji określ:

1. Czy może być wykonana jako refaktoryzacja?
2. Jakie zachowanie jest obserwowalne?
3. Kto lub co jest konsumentem tego zachowania?
4. Jakiego dowodu potrzebujesz przed i po zmianie?
5. Czy propozycję trzeba podzielić na zmianę strukturalną i funkcjonalną?

**Propozycje:**

- A. Zmiana nazwy prywatnej metody `calc` na `calculateTax`.
- B. Zastąpienie `double` przez `BigDecimal`, ponieważ produkcja wykazuje błędy zaokrągleń.
- C. Przeniesienie publicznej klasy DTO do innego pakietu.
- D. Wydzielenie budowy komunikatu do osobnej metody bez zmiany tekstu.
- E. Dodanie pamięci podręcznej do odczytu kursu walut.

**Polecenia:**

1. Przygotuj tabelę z kolumnami odpowiadającymi pięciu pytaniom powyżej.
2. Wypełnij ją dla propozycji A-E.
3. Dla propozycji, które wymagają podziału, zapisz kolejność kroków (co jest strukturalne, co funkcjonalne).

**Kryterium ukończenia:** odpowiedź nie opiera się wyłącznie na nazwie operacji. Dla każdej pozycji wskazuje konkretną obserwację oraz mechanizm weryfikacji.

---

### Ćwiczenie 2: Charakterystyka i refaktoryzacja formatera

**Czas pracy:** 35 minut. **Omówienie:** 10 minut.

**Cel:** zabezpieczyć zastane zachowanie testami charakteryzującymi, a następnie rozdzielić odpowiedzialności formatera w serii małych, bezpiecznych kroków.

**Punkt wyjścia (pliki w projekcie):**

- `src/main/java/pl/training/module2/LegacyInvoiceFormatter.java` - klasa legacy łącząca normalizację klienta, obliczenia, zaokrąglenia i budowę tekstu,
- `src/main/java/pl/training/module2/InvoiceLine.java` - obiekt wartości `InvoiceLine(String sku, int quantity, BigDecimal unitPrice)`,
- `src/test/java/pl/training/module2/LegacyInvoiceFormatterCharacterizationTest.java` - istniejące testy charakteryzujące.

**Organizacja pracy:** refaktoryzację wykonuj na własnej kopii klasy legacy (np. `WorkshopInvoiceFormatter` w tym samym pakiecie) albo na osobnej gałęzi. Oryginalna klasa `LegacyInvoiceFormatter` ma pozostać niezmieniona, aby można było porównać wyniki obu wersji.

**Polecenia:**

1. Przeczytaj kod `LegacyInvoiceFormatter` bez zmieniania go i wypisz wszystkie widoczne reguły (tekst, obliczenia, zaokrąglenia, białe znaki).
2. Uruchom istniejące testy charakteryzujące.
3. Zbuduj macierz przypadków obejmującą co najmniej poniższe obserwacje. Zaznacz, które są już zabezpieczone istniejącymi testami, i dodaj testy tylko dla brakujących:
   - pusta lista pozycji,
   - klient `null`,
   - cena wymagająca zaokrąglenia,
   - wiele pozycji, dla których suma wartości niezaokrąglonych różni się od sumy wartości prezentowanych.
4. Ustal, które reguły są potwierdzonym kontraktem, a które tylko zastanym zachowaniem.
5. Refaktoryzuj w małych krokach, wydzielając kolejno: normalizację klienta, wartość wiersza, sumę, zaokrąglenie oraz formatowanie pozycji.
6. Po każdym kroku uruchom adekwatny test.
7. Porównaj wynik `LegacyInvoiceFormatter` i swojej nowej wersji dla reprezentatywnych danych (np. testem parametryzowanym uruchamiającym obie implementacje).
8. Obejrzyj końcową różnicę (`git diff`) i wskaż każdą zmianę, której nie wymagał cel ćwiczenia.

**Ograniczenia:**

- Nie zmieniaj stawki podatku ani sposobu zaokrąglania.
- Nie dodawaj walidacji, wyjątków ani obsługi walut.
- Nie zmieniaj tekstu, białych znaków ani końcowego znaku nowego wiersza.
- Nie łącz wszystkich wydzieleń w jedną dużą edycję.

**Kryteria ukończenia:**

- Wszystkie testy są zielone.
- Każda transformacja ma jedną rozpoznawalną intencję.
- Wynik starej i nowej wersji jest zgodny dla przygotowanych danych.
- Potrafisz wskazać ograniczenia uzyskanego dowodu.

**Oczekiwany produkt:** lista reguł, macierz przypadków, uzupełnione testy charakteryzujące, zrefaktoryzowana klasa, test porównujący obie implementacje, lista kroków transformacji.

---

### Ćwiczenie 3: Strategia testów, obiekty zastępcze i pokrycie

**Czas pracy:** 25 minut. **Omówienie:** 10 minut.

**Cel:** nazywać role obiektów zastępczych według ich użycia w teście, rozumieć granice każdego z nich i poprawnie interpretować raport pokrycia.

#### Część A: role obiektów zastępczych

**Pliki:**

- `src/main/java/pl/training/module2/OrderPlacementService.java` - usługa składania zamówienia z czterema współpracownikami: `ProductCatalog`, `PaymentGateway`, `OrderRepository`, `EventPublisher`,
- `src/test/java/pl/training/module2/OrderPlacementServiceTest.java` - dwa testy: `placesOrderUsingStubFakeAndSpy` i `verifiesPaymentProtocolUsingMock`.

**Polecenia:** dla każdego współpracownika w każdym z dwóch testów (`catalogStub`, `paymentStub`, `repositoryFake`, `publisherSpy`, `paymentMock`, `publisherStub`) odpowiedz:

1. Jaką rolę pełni w danym teście?
2. Czy test sprawdza stan, wynik czy interakcję?
3. Jaki defekt może wykryć?
4. Jakiego defektu nie może wykryć?
5. Która implementacja typu fake jest kandydatem do wspólnego testu kontraktu z produkcyjnym adapterem i jak można obserwować skutki obu implementacji?

Następnie oceń, czy sprawdzanie dokładnej kolejności zapisu zamówienia i publikacji zdarzenia byłoby uzasadnione. Odpowiedź musi odwoływać się do konkretnego kontraktu transakcyjnego, a nie do aktualnej kolejności linii kodu.

#### Część B: raport pokrycia

**Pliki:**

- `src/main/java/pl/training/module2/DeliveryFee.java`
- `src/test/java/pl/training/module2/DeliveryFeeLineCoverageTest.java`

**Polecenia:**

1. Uruchom `mvn clean verify`.
2. Otwórz raport pakietu `pl.training.module2` (`target/site/jacoco/index.html`).
3. Znajdź `DeliveryFee` i wyjaśnij różnicę między pokryciem linii i gałęzi.
4. Zaproponuj (i dopisz) test, który wykona brakującą gałąź i wykryje zmianę opłaty `100` na `200`. Sprawdź to kontrolną mutacją: zmień tymczasowo `100` na `200`, uruchom test, przywróć kod.
5. Podaj przykład testu, który zwiększyłby pokrycie, ale nie chronił reguły opłaty.
6. Zaproponuj sposób użycia progu pokrycia, który nie zamieni go w cel sam w sobie.

**Kryteria ukończenia:**

- Role są nazwane według użycia w teście.
- Wskazano granice każdego obiektu zastępczego.
- Brakująca gałąź ma asercję dotyczącą wartości biznesowej.
- Wniosek z raportu nie sprowadza jakości do jednego procentu.

---

### Ćwiczenie 4: Wprowadzenie szwu

**Czas pracy:** 25 minut. **Omówienie:** 10 minut.

**Cel:** udostępnić kod do deterministycznego testowania przez minimalny szew przejściowy, a następnie doprowadzić go do projektu z jawnymi zależnościami.

**Punkt wyjścia (pliki w projekcie):**

- `src/main/java/pl/training/module2/LegacyReminderService.java` - usługa wysyłająca przypomnienie o odnowieniu subskrypcji,
- `src/main/java/pl/training/module2/Subscription.java` - `Subscription(String email, LocalDate renewalDate)`.

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

**Organizacja pracy:** klasę legacy pozostaw bez zmian jako punkt odniesienia. Wersję ze szwem i wersję docelową utwórz pod własnymi nazwami (np. `WorkshopSeamedReminderService` i `WorkshopReminderService`) albo pracuj na osobnej gałęzi.

**Polecenia:**

1. Wskaż niedeterministyczne wejście oraz efekt trudny do obserwacji.
2. Nazwij górną granicę reguły oraz konsekwencję braku granicy dolnej.
3. Wprowadź minimalny szew przez owinięcie czasu i wysyłki metodami możliwymi do nadpisania.
4. Napisz deterministyczne testy dla odnowienia dokładnie za siedem dni, za osiem dni oraz dla daty przeszłej.
5. Zastąp szew przejściowy jawnymi zależnościami `Clock` i `ReminderGateway` (interfejs z metodą `void send(String email, LocalDate renewalDate)`).
6. Przenieś testy na docelowy projekt.
7. Wskaż punkt aktywacji każdego szwu.
8. Wyjaśnij, jaki test byłby potrzebny dla rzeczywistego adaptera komunikacyjnego.

**Ograniczenia:**

- Nie zmieniaj reguły biznesowej (w tym braku dolnej granicy) podczas rozrywania zależności.
- Nie testuj przez zmianę zegara systemowego ani przechwytywanie `System.out`.

**Kryteria ukończenia:**

- Test nie zależy od bieżącej daty ani globalnego wyjścia.
- Górna granica siedmiu dni jest sprawdzona po obu stronach, a zachowanie dla daty przeszłej jest jawnie scharakteryzowane.
- Kod domenowy nie tworzy konkretnego adaptera.
- Rozwiązanie przejściowe jest odróżnione od projektu docelowego.
- Potrafisz wskazać potrzebę testu integracyjnego adaptera.

---

## Sprawdzenie wiedzy

1. Co odróżnia refaktoryzację od zmiany funkcjonalnej?
2. Czy zachowanie obserwowalne ogranicza się do wartości zwracanej?
3. Dlaczego małe kroki ograniczają koszt diagnozy?
4. Co dokładnie oznacza zielony zestaw testów?
5. Dlaczego piramida testów nie narzuca uniwersalnych proporcji?
6. Czym stub różni się od mocka?
7. Jakie ryzyko pozostaje po zastąpieniu bazy implementacją działającą w pamięci?
8. Co pokazuje pokrycie gałęzi, czego może nie pokazać pokrycie linii?
9. Dlaczego test charakteryzujący nie dowodzi poprawności wymagania?
10. Czym różni się seam od punktu aktywacji?
11. Jakie dwie potrzeby realizuje rozrywanie zależności?
12. Kiedy szew przez dziedziczenie warto traktować jako przejściowy?
