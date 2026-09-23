# Moduł 3. Zasady dobrego projektowania - zadania dla uczestników

## Jak pracować

- Wszystkie zadania dotyczą jednego przykładu: wyceny dostawy w pakiecie `pl.training.module3` (Java), a jego odpowiedniki znajdziesz w `csharp/src/Training.Module3` oraz `typescript/src/module3`.
- Przed rozpoczęciem uruchom pełny build i upewnij się, że jest zielony:

  ```shell
  mvn clean verify
  ```

- Po każdym małym kroku uruchom odpowiedni test, a przed zakończeniem ćwiczenia ponownie `mvn verify`. Testy samego modułu: `mvn test -Dtest='pl.training.module3.**'`.
- Nie zmieniaj jednocześnie zachowania biznesowego i struktury, chyba że zadanie wyraźnie tego wymaga.
- Każdą decyzję projektową uzasadniaj aktualnym wymaganiem, ryzykiem albo kierunkiem zmiany. Sama nazwa zasady (DRY, SOLID, Clean Architecture) nie jest uzasadnieniem.
- Zapisuj decyzje w formie krótkich notatek lub tabel. Będą potrzebne podczas omówienia.
- Ćwiczenia 1 i 2 pracują na wersji zastanej. Ćwiczenia 3 i 4 zakładają, że kod ma już podział na `domain`, `application`, `adapter` i composition root (wynik ćwiczenia 2 albo stan pakietu wskazany przez prowadzącego).

Materiał teoretyczny modułu nie zawiera osobnych „krótkich aktywności”. Zajęcia praktyczne składają się z czterech ćwiczeń warsztatowych oraz sprawdzenia wiedzy.

### Pliki będące punktem wyjścia

| Plik | Rola |
| --- | --- |
| `src/main/java/pl/training/module3/legacy/LegacyDeliveryQuoteService.java` | zastany serwis wyceny dostawy |
| `src/test/java/pl/training/module3/legacy/LegacyDeliveryQuoteServiceCharacterizationTest.java` | testy charakterystyki wersji zastanej |
| `src/main/java/pl/training/module3/domain/Parcel.java` | model przesyłki (masa dodatnia) |
| `src/main/java/pl/training/module3/domain/ShippingMethod.java` | metody dostawy `STANDARD`, `EXPRESS` |
| `src/main/java/pl/training/module3/domain/DeliveryQuote.java` | wynik wyceny i jego inwarianty |
| `src/main/java/pl/training/module3/Module3Examples.java` | program demonstracyjny (miejsce uruchomienia) |

### Wersja zastana w skrócie

```java
public DeliveryQuote createQuote(String customerEmail, ShippingMethod method, Parcel parcel) {
    BigDecimal price = switch (method) {
        case STANDARD -> {
            BigDecimal base = new BigDecimal("10.00")
                    .add(parcel.weightKg().multiply(new BigDecimal("2.00")));
            yield money(base.add(base.multiply(new BigDecimal("0.08"))));
        }
        case EXPRESS -> {
            BigDecimal base = new BigDecimal("20.00")
                    .add(parcel.weightKg().multiply(new BigDecimal("3.00")));
            yield money(base.add(base.multiply(new BigDecimal("0.08"))));
        }
    };
    DeliveryQuote quote = new DeliveryQuote(customerEmail, method, parcel, price);
    storedQuotes.add(quote);
    System.out.printf("Quote ready for %s: %s costs %s%n", customerEmail, method, price);
    return quote;
}
```

`money(...)` zaokrągla do dwóch miejsc (`HALF_UP`). Serwis przechowuje oferty we własnej liście `storedQuotes` i udostępnia jej kopię.

Znane zachowanie (test charakterystyki): przesyłka 3 kg kosztuje `17.28` dla `STANDARD` oraz `31.32` dla `EXPRESS`, a utworzona oferta trafia do listy zapisanych ofert.

---

## Ćwiczenie 1: DRY, KISS i YAGNI

**Czas:** 20 minut pracy i 10 minut omówienia.

### Cel

Odróżnienie wspólnej wiedzy od podobnego tekstu oraz ocena kosztu abstrakcji.

### Kontekst

DRY dotyczy wiedzy, a nie podobnych linii. KISS oznacza najmniej złożone rozwiązanie, które poprawnie realizuje aktualne wymagania. YAGNI każe odróżnić potrzebę aktualną od przewidywanej.

### Sytuacje

1. W politykach STANDARD i EXPRESS występuje stawka opłaty paliwowej 8 procent oraz identyczne zaokrąglenie.
2. W dwóch niezależnych kontekstach `MaximumParcelWeight` i `MaximumWarehouseLoad` wynosi dziś 1000 kg.
3. Test polityki STANDARD i test polityki EXPRESS zawierają jawnie wartość oczekiwaną dla przesyłki 3 kg.
4. Zespół proponuje refleksyjny system pluginów, ponieważ za rok mogą pojawić się taryfy definiowane przez partnerów.
5. Produkt ma zatwierdzone wymaganie: za trzy tygodnie zostanie uruchomiona trzecia metoda dostawy z innym algorytmem.
6. Cache wycen powiela dane obliczone przez serwis cenowy, aby spełnić zmierzony limit czasu odpowiedzi.

### Polecenia

1. Dla każdej sytuacji zdecyduj: **scalić teraz**, **pozostawić lokalnie** albo **zebrać więcej informacji**.
2. Dla każdej sytuacji zapisz w tabeli:
   - reprezentowaną wiedzę,
   - właściciela i powód zmiany,
   - aktualne wymaganie lub ryzyko,
   - najprostsze poprawne rozwiązanie,
   - koszt błędnej centralizacji albo błędnego pozostawienia duplikacji,
   - sygnał, po którym decyzję trzeba ponownie ocenić.
3. Rozważ regułę opłaty paliwowej wydzieloną do osobnego typu domenowego `FuelSurcharge` (stawka od 0 do 1, metoda `addTo(baseAmount)` doliczająca opłatę i zaokrąglająca do dwóch miejsc). Odpowiedz, czy warto wydzielić dodatkowy interfejs `Surcharge`. Uzasadnienie musi odwoływać się do aktualnego wariantu lub granicy, nie do samej możliwości utworzenia interfejsu.

Szablon tabeli:

| Nr | Decyzja | Wiedza | Właściciel i powód zmiany | Wymaganie lub ryzyko | Najprostsze poprawne rozwiązanie | Koszt błędnej decyzji | Sygnał ponownej oceny |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 |  |  |  |  |  |  |  |
| 2 |  |  |  |  |  |  |  |
| 3 |  |  |  |  |  |  |  |
| 4 |  |  |  |  |  |  |  |
| 5 |  |  |  |  |  |  |  |
| 6 |  |  |  |  |  |  |  |

### Kryteria akceptacji

- decyzja odwołuje się do wiedzy, a nie liczby podobnych linii,
- KISS jest oceniane po spełnieniu aktualnych wymagań,
- przyszłe wymaganie jest odróżnione od zatwierdzonego wymagania,
- kontrolowana duplikacja ma właściciela, strategię synchronizacji i test inwariantu,
- potrafisz wskazać koszt wybranej opcji.

### Oczekiwany produkt

Wypełniona tabela sześciu sytuacji oraz 2-4 zdania decyzji w sprawie interfejsu `Surcharge`.

---

## Ćwiczenie 2: SOLID jako seria bezpiecznych zmian

**Czas:** 25 minut pracy i 10 minut omówienia.

### Cel

Refaktoryzacja `LegacyDeliveryQuoteService` z zachowaniem cen oraz jawnego protokołu efektów.

### Punkt wyjścia

- `src/main/java/pl/training/module3/legacy/LegacyDeliveryQuoteService.java`
- `src/test/java/pl/training/module3/legacy/LegacyDeliveryQuoteServiceCharacterizationTest.java`
- modele `Parcel`, `ShippingMethod`, `DeliveryQuote` w `src/main/java/pl/training/module3/domain/`

Pracuj na kopii klasy zastanej (np. w nowym pakiecie roboczym) albo na gałęzi, na której przywrócisz wersję `legacy`. Istniejące testy charakterystyki są punktem wyjścia, nie kompletną specyfikacją. Zwróć uwagę, że nie przechwytują treści wypisywanej na konsolę.

### Polecenia

1. Wskaż aktorów zmiany dla wyceny, zapisu i powiadomień.
2. Ustal z właścicielem procesu (w roli właściciela występuje prowadzący), czy po nieudanym zapisie wolno wysłać powiadomienie, i zapisz decyzję jako wymaganie. Zastana klasa nie ma punktu podmiany pozwalającego wiarygodnie zasymulować taką awarię.
3. Wydziel regułę opłaty paliwowej bez łączenia niezależnych cenników.
4. Wydziel kontrakt polityki cenowej i przenieś jeden wariant.
5. Uruchom testy przed przeniesieniem drugiego wariantu.
6. Dodaj test kontraktowy uruchamiany dla każdej polityki.
7. Wydziel porty zapisu i powiadamiania z perspektywy przypadku użycia.
8. Po utworzeniu punktu podmiany dopisz test chroniący uzgodnione zachowanie przy awarii zapisu.
9. Przenieś konkretne efekty (zapis w pamięci, wypisanie na konsolę) do adapterów.
10. Złóż rozwiązanie ręcznie poza przypadkiem użycia (composition root).
11. Porównaj wynik programu przed i po zmianie.

Po każdym kroku zanotuj, którą oś zmiany chroni konstrukcja oraz jaki koszt dodaje. Nie przypisuj jednej klasy do jednej zasady. Jedna decyzja może wspierać kilka właściwości projektu.

Szablon dziennika kroków:

| Krok | Co zmieniono | Chroniona oś zmiany | Dodany koszt | Testy uruchomione |
| --- | --- | --- | --- | --- |
|  |  |  |  |  |

### Macierz kontraktu LSP

Uzupełnij tabelę dla kontraktu polityki cenowej (`DeliveryPricePolicy`):

| Element kontraktu | Gwarancja typu bazowego | Jak sprawdzić | Przykład naruszenia |
| --- | --- | --- | --- |
| obsługiwana metoda |  |  |  |
| dane wejściowe |  |  |  |
| wynik |  |  |  |
| zaokrąglenie |  |  |  |
| deterministyczność |  |  |  |
| efekty uboczne |  |  |  |
| wyjątki |  |  |  |

### Kryteria akceptacji

- ceny STANDARD i EXPRESS pozostają odpowiednio `17.28` oraz `31.32` dla 3 kg,
- przypadek użycia nie importuje adapterów,
- stawka opłaty paliwowej ma jedną reprezentację,
- każdy wariant przechodzi wspólny test kontraktowy,
- awaria zapisu nie prowadzi do powiadomienia (o ile taka jest uzgodniona decyzja właściciela procesu),
- `mvn verify` kończy się powodzeniem.

### Oczekiwany produkt

Zrefaktoryzowany kod z testami, dziennik kroków oraz uzupełniona macierz kontraktu LSP.

---

## Ćwiczenie 3: granica i reguła zależności

**Czas:** 25 minut pracy i 10 minut omówienia.

### Cel

Odróżnienie przepływu sterowania od zależności źródłowych i zaprojektowanie minimalnej granicy inside/outside.

### Punkt wyjścia

Kod podzielony na pakiety `pl.training.module3.domain`, `pl.training.module3.application` (przypadek użycia i porty `QuoteRepository`, `QuoteNotifier`), `pl.training.module3.adapter` oraz composition root `Module3Examples`. Port powiadomień ma postać:

```java
@FunctionalInterface
public interface QuoteNotifier {
    void quoteCreated(DeliveryQuote quote);
}
```

Przyjęty model dozwolonych zależności: domena zależy tylko od Java SE i własnych pojęć; aplikacja od domeny i własnych portów; adapter od portów aplikacji, domeny i użytej technologii; composition root od wszystkiego, co potrzebne do złożenia.

### Zadanie A: klasyfikacja zależności

Oceń każdą zależność jako **dozwoloną**, **podejrzaną** albo **niedozwoloną** w przyjętym modelu. Uzasadnij odpowiedź:

1. `domain` importuje `java.math.BigDecimal`.
2. `application` importuje `adapter.InMemoryQuoteRepository`.
3. `adapter` implementuje `application.QuoteRepository`.
4. `domain` przyjmuje encję oznaczoną adnotacjami ORM.
5. `Module3Examples` tworzy adaptery i przypadek użycia.
6. `ConsoleQuoteNotifier` importuje `DeliveryQuote`.
7. `CreateDeliveryQuote` w czasie wykonania wywołuje obiekt adaptera przez `QuoteNotifier`.
8. Port aplikacji zwraca wyjątek konkretnego sterownika JDBC.

### Zadanie B: nowy adapter

Aktualne wymaganie: powiadomienie ma być zapisane w buforze w pamięci, aby test demonstracyjny mógł sprawdzić jego treść bez przechwytywania `System.out`.

1. Dodaj `BufferingQuoteNotifier` w pakiecie `adapter`.
2. Zaimplementuj istniejący `QuoteNotifier`, bez zmiany portu.
3. Udostępnij niemodyfikowalny widok lub kopię zgromadzonych komunikatów.
4. Dodaj test adaptera.
5. Podmień adapter wyłącznie w composition root i potwierdź, że aplikacja oraz domena nie wymagają zmian.

Nie dodawaj frameworka DI ani pliku konfiguracyjnego. Aktualne wymaganie tego nie potrzebuje.

### Zadanie C: automatyczna ochrona granicy

Zaproponuj prostą kontrolę, która wykryje import z `pl.training.module3.adapter` w pakiecie `application` albo `domain`. Możesz użyć:

- reguły narzędzia do testów architektury,
- osobnych modułów Maven,
- JPMS,
- prostej kontroli statycznej w potoku CI.

Porównaj siłę gwarancji, koszt utrzymania i jakość komunikatu o błędzie. Nie implementuj wszystkich wariantów.

### Kryteria akceptacji

- nowy adapter można wybrać bez edycji przypadku użycia,
- adapter zależy od portu, a nie odwrotnie,
- kontrola obejmuje co najmniej zakaz importu adapterów do wnętrza,
- potrafisz narysować osobno zależność źródłową i wywołanie w czasie wykonania,
- pełny build pozostaje zielony.

### Oczekiwany produkt

Tabela klasyfikacji ośmiu zależności, nowy adapter z testem, zmieniony wyłącznie composition root, propozycja kontroli granicy z porównaniem wariantów oraz szkic dwóch diagramów (zależności źródłowe i przepływ sterowania).

---

## Ćwiczenie 4: wzorzec jako decyzja odwracalna

**Czas:** 20 minut pracy i 10 minut omówienia.

### Cel

Dobór Strategy i Adapter do potwierdzonego problemu oraz rozpoznanie momentu, w którym wzorzec należy uprościć.

### Punkt wyjścia

Kontrakt polityki cenowej:

```java
public interface DeliveryPricePolicy {
    /** Returns the stable, non-null shipping method handled by this policy. */
    ShippingMethod method();

    /** Returns a deterministic, non-negative amount with scale two for every
     *  valid parcel, without changing the parcel or producing side effects. */
    BigDecimal priceFor(Parcel parcel);
}
```

Dwie implementacje (STANDARD, EXPRESS) są wybierane przez kalkulator według `ShippingMethod`. `DeliveryQuote` przechowuje cenę jako `BigDecimal` bez waluty.

### Scenariusz 1: dostawa tego samego dnia

Pojawia się zatwierdzona metoda SAME_DAY. Cena pochodzi z zewnętrznego klienta przewoźnika, który:

- przyjmuje masę w gramach jako `long`,
- zwraca kwotę w najmniejszych jednostkach, ale wstępny opis nie podaje waluty ani wykładnika tych jednostek,
- sygnalizuje brak oferty własnym typem błędu,
- może wykonać operację sieciową.

Zaprojektuj zmianę, odpowiadając:

1. Czy SAME_DAY jest kolejną lokalną `DeliveryPricePolicy`, adapterem zewnętrznego klienta, czy współpracą obu ról?
2. Gdzie następuje konwersja kilogramów na gramy i co zrobić z ułamkiem grama oraz przepełnieniem typu `long`?
3. Jakiej informacji potrzeba do konwersji najmniejszych jednostek na `BigDecimal` i czy model wyniku musi zawierać walutę?
4. Jaki błąd powinien przekroczyć granicę do aplikacji?
5. Czy kontrakt `DeliveryPricePolicy` nadal może obiecywać brak efektów ubocznych?
6. Gdzie ustawić timeout i politykę ponowień?
7. Jak sprawdzić mapowanie bez wywoływania prawdziwej usługi w testach domenowych?

Nie dopasowuj zewnętrznego klienta na siłę do kontraktu, którego obietnic nie potrafi spełnić.

### Scenariusz 2: uproszczenie

Po roku produkt usuwa EXPRESS, a STANDARD zostaje jedyną stałą regułą. Nie ma planu dodawania innych wariantów.

Oceń:

- czy `DeliveryPricePolicy` nadal chroni rzeczywistą zmienność,
- czy rejestr strategii ma wartość,
- czy prostszy `DeliveryPriceCalculator` może wchłonąć jedyną regułę,
- które testy kontraktowe należy zachować jako testy zachowania,
- ile miejsc zmieniłoby się przy ponownym dodaniu wariantu.

Wykonaj uproszczenie tylko wtedy, gdy koszt bieżącej abstrakcji przewyższa koszt prawdopodobnej zmiany. Zielone testy powinny umożliwić refaktoryzację od wzorca.

### Kryteria akceptacji

- nazywasz problem rozwiązywany przez każdy wzorzec,
- kontrakt nie ukrywa operacji sieciowej jako czystego obliczenia,
- typy zewnętrznego klienta nie przechodzą do domeny,
- decyzja o zachowaniu lub usunięciu Strategy ma uzasadnienie kosztowe,
- rozwiązanie nie zawiera mechanizmu pluginów bez wymagania.

### Oczekiwany produkt

Szkic projektu dla SAME_DAY (role, porty, miejsce konwersji i tłumaczenia błędów, odpowiedzi na 7 pytań) oraz uzasadniona decyzja dla scenariusza 2, opcjonalnie wykonana refaktoryzacja na zielonych testach.

---

## Sprawdzenie wiedzy

1. Co jest przedmiotem DRY: tekst kodu czy wiedza?
2. Kiedy dwie identyczne funkcje nie powinny zostać połączone?
3. Dlaczego prostsze rozwiązanie nie zawsze ma mniej klas?
4. Czy YAGNI zabrania pisania testów dla aktualnego zachowania?
5. Jak SRP definiuje powód zmiany?
6. Czy każdy `switch` narusza OCP?
7. Jakie dwa rodzaje warunków kontraktu są kluczowe dla LSP?
8. Dlaczego `@Autowired` nie dowodzi zastosowania DIP?
9. Czy jednometodowy interfejs jest automatycznie zgodny z ISP?
10. Co interfejs robi ze sprzężeniem?
11. Czy przepływ sterowania z przypadku użycia do adaptera łamie regułę zależności?
12. Gdzie powinien należeć port wyjściowy?
13. Czy Clean Architecture wymaga dokładnie czterech warstw?
14. Kiedy Strategy może być gorsza od `switch`?
15. Dlaczego test kontraktowy nie dowodzi formalnie LSP?
16. Kiedy należy rozważyć usunięcie wcześniej zastosowanego wzorca?
