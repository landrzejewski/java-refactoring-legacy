# Moduł 4. Podstawowe refaktoryzacje - warsztat praktyczny

## Zadania dla uczestników

## Jak pracować

- Każde ćwiczenie zaczynasz od zielonego testu. Zanim cokolwiek zmienisz, uruchom test i upewnij się, że przechodzi.
- Wykonuj jedną transformację naraz. Po każdym kroku skompiluj kod i uruchom co najmniej test najbliższy zmienianemu kodowi.
- Zapisuj małe, spójne kroki w historii (osobny commit albo przynajmniej osobny punkt w notatkach).
- Jeżeli funkcja IDE odmawia wykonania operacji, najpierw ustal, jakiego warunku wstępnego nie spełnia kod. Ręczne przepisanie kodu bez tej analizy nie jest rozwiązaniem.
- Odróżniaj refaktoryzację (zmiana struktury bez zmiany obserwowalnego zachowania) od świadomej zmiany kontraktu. Jeśli wprowadzasz zmianę zachowania, nazwij ją wprost.
- Pracujesz w Javie 25. Przykłady mają odpowiedniki w C# (`csharp/src/Training.Module4`) i TypeScript (`typescript/src/module4`), jeśli grupa pracuje w innym języku.

Uruchamianie testów modułu (Java):

~~~bash
mvn test -Dtest='pl.training.module4.**'
~~~

Pojedynczy test, na przykład test charakterystyki:

~~~bash
mvn test -Dtest='RentalQuoteServiceCharacterizationTest'
~~~

## Kontrakt studium przypadku (dotyczy ćwiczeń 1-3)

Generator przygotowuje tekstową ofertę wynajmu sprzętu. Obowiązują następujące reguły:

- wiertnica (`DRILL`) kosztuje 39,99 za dzień, a generator (`GENERATOR`) 120,00 za dzień,
- wynajem trwający co najmniej 7 dni otrzymuje 10 procent rabatu od podstawowego kosztu wynajmu,
- ubezpieczenie kosztuje 8,00 za każdy dzień,
- dostawa kosztuje jednorazowo 25,00,
- VAT wynosi 23 procent wartości netto,
- każda pośrednia kwota pieniężna jest zaokrąglana do dwóch miejsc metodą HALF_UP,
- nazwa klienta w dokumencie jest pozbawiana skrajnych białych znaków i zamieniana na wielkie litery,
- zapis liczb i tekstu nie zależy od domyślnego locale procesu,
- dokument kończy się znakiem nowej linii.

Inny moment zaokrąglenia, rabat naliczony od dodatków albo brak końcowego znaku nowej linii oznaczają zmianę zachowania.

Model wejścia (nie jest przedmiotem refaktoryzacji, jego walidacja jest stałym warunkiem wstępnym):

- `src/main/java/pl/training/module4/model/EquipmentType.java`
- `src/main/java/pl/training/module4/model/RentalRequest.java`

Przykładowy zatwierdzony dokument dla żądania `(" Acme ", GENERATOR, 8 dni, ubezpieczenie, dostawa)`:

~~~text
RENTAL QUOTE
Customer: ACME
Equipment: GENERATOR
Days: 8
Base: 960.00
Discount: 96.00
Insurance: 64.00
Delivery: 25.00
Net: 953.00
VAT: 219.19
Total: 1172.19
~~~

---

## Ćwiczenie 1: lokalne porządkowanie metody

**Cel:** uporządkować jedną metodę za pomocą Rename, Extract Constant, Replace Magic Numbers with Named Constants, Extract Variable, Extract Method, Encapsulate Conditional i Inline Variable, nie zmieniając ani jednego bajtu wyniku.

**Czas:** 30 minut pracy, 10 minut omówienia.

**Pliki wyjściowe:**

- `src/main/java/pl/training/module4/stage0/RentalQuoteService.java`
- `src/test/java/pl/training/module4/stage0/RentalQuoteServiceCharacterizationTest.java`

**Kontekst:** klasa etapu 0 oblicza poprawny wynik, ale miesza reguły wyceny, formatowanie dokumentu, jednoliterowe nazwy (`r`, `a`, `d`, `i`, `f`, `n`, `v`, `t`, `q`) i literały domenowe (`7`, `"8.00"`, `"25.00"`, `"0.23"`). Fragment wejściowy:

~~~java
public String createQuote(RentalRequest r) {
    BigDecimal a = money(rates.get(r.equipmentType())
            .multiply(BigDecimal.valueOf(r.days())));
    BigDecimal d = r.days() >= 7
            ? money(a.multiply(disc))
            : money(BigDecimal.ZERO);
    BigDecimal i = r.insurance()
            ? money(new BigDecimal("8.00")
                    .multiply(BigDecimal.valueOf(r.days())))
            : money(BigDecimal.ZERO);
    BigDecimal f = r.delivery() ? new BigDecimal("25.00") : money(BigDecimal.ZERO);
    BigDecimal n = money(a.subtract(d).add(i).add(f));
    BigDecimal v = money(n.multiply(new BigDecimal("0.23")));
    BigDecimal t = money(n.add(v));
    String q = "RENTAL QUOTE\n" + /* ... sklejanie dokumentu ... */;
    return q;
}
~~~

**Polecenia:**

1. Uruchom test charakterystyki i celowo zmień próg rabatu, aby sprawdzić, czy test jest czuły na błąd.
2. Cofnij kontrolowaną zmianę.
3. Zmień nazwy parametru oraz lokalnych wartości tak, aby opisywały role w wycenie.
4. Nazwij próg rabatu, stawkę ubezpieczenia, opłatę dostawy i VAT.
5. Wydziel obliczenia rabatu, ubezpieczenia i dostawy.
6. Nazwij predykat kwalifikacji do rabatu.
7. Wydziel budowę dokumentu.
8. Usuń zmienne, które po wydzieleniach jedynie przekazują wynik dalej i nie wnoszą znaczenia.

**Ograniczenia:**

- nie zmieniaj publicznej sygnatury `createQuote`,
- zachowaj kolejność i miejsca zaokrąglania,
- nie przenoś jeszcze kodu do nowej klasy,
- po każdym kroku uruchom test.

**Kryteria akceptacji / oczekiwany produkt:**

- pełny dokument jest identyczny bajt po bajcie,
- 6 dni nie daje rabatu, a 7 dni go daje,
- VAT dla jednodniowego wynajmu wiertnicy wynosi 9,20,
- w metodzie publicznej nie pozostały jednoliterowe nazwy,
- każdy nazwany literał ma jednoznaczne znaczenie domenowe,
- wydzielone metody mają spójny poziom abstrakcji,
- produkt: uporządkowana klasa oraz lista wykonanych kroków w kolejności ich wykonania.

---

## Ćwiczenie 2: Extract i Inline pod presją semantyki

**Cel:** nauczyć się oceniać, czy Extract Variable, Inline Variable, Extract Method, Inline Method i Extract Constant zachowują zachowanie, a następnie wykonać jeden bezpieczny Inline Method w kodzie projektu.

**Czas:** 25 minut pracy, 10 minut omówienia.

**Pliki wyjściowe (część praktyczna):**

- `src/main/java/pl/training/module4/stage2/RentalQuoteService.java`
- `src/test/java/pl/training/module4/RentalQuoteStagesEquivalenceTest.java`

**Część A - analiza.** Dla każdego przypadku ustal, czy transformacja zachowuje zachowanie. Zapisz obserwację, która może się zmienić, oraz test potrzebny przed operacją.

1. Zmienna `now` przechowuje pojedynczy wynik `clock.instant()` używany dwa razy. Rozważ Inline Variable.
2. Warunek ma postać `account != null && account.isActive()`. Rozważ wcześniejsze obliczenie prawego operandu przez Extract Variable.
3. Argument metody wykonuje zapis audytowy, a po ręcznym Extract Method pojawia się w dwóch wywołaniach.
4. Wyrażenie lambda jest przekazywane do przeciążonej metody. Rozważ Extract Variable z `var` oraz z jawnym typem docelowym.
5. Jednoliniowa metoda instancyjna może być przesłonięta w podklasie. Rozważ Inline Method w klasie bazowej.
6. Jednoliniowa metoda jest oznaczona `synchronized`. Rozważ osobno wariant instancyjny i statyczny przed zastąpieniem wywołania jej treścią.
7. Prywatny delegat `calculatePrice` z etapu 2 tylko wywołuje `pricing.calculate(request)`. Rozważ Inline Method.
8. Lokalna zmienna przechowuje `new BigDecimal("0.23")` w dwóch miejscach. Rozważ Extract Constant i oceń, czy liczba reprezentuje jedno pojęcie.

Wynik części A zapisz w tabeli:

| Przypadek | Ocena (bezpieczne / niebezpieczne / zależy) | Obserwacja, która może się zmienić | Test potrzebny przed operacją |
| --- | --- | --- | --- |
| 1 | | | |
| ... | | | |

**Część B - praktyka.**

1. Otwórz klasę etapu 2 i znajdź prywatną metodę `calculatePrice`.
2. Zanim wykonasz operację, sprawdź, czy metoda nie jest punktem rozszerzenia, celem refleksji, miejscem przechwytywania przez proxy ani granicą synchronizacji.
3. Wykonaj Inline Method (najlepiej funkcją IDE), przejrzyj podgląd zmiany.
4. Uruchom `RentalQuoteStagesEquivalenceTest`.

**Kryteria akceptacji:**

- ocena uwzględnia liczbę i kolejność ewaluacji,
- analiza rozróżnia statyczne wiązanie od dynamicznej dyspozycji,
- analiza rozróżnia monitor odbiorcy metody instancyjnej od monitora obiektu `Class` metody statycznej,
- dla lambdy zostaje zachowany typ docelowy,
- delegat zostaje usunięty tylko w przypadku bez ukrytego kontraktu,
- test równoważności etapów jest zielony po operacji.

---

## Ćwiczenie 3: Extract Class oraz przeniesienie odpowiedzialności

**Cel:** wydzielić odpowiedzialność za wycenę z klasy generującej dokument, stosując Extract Class realizowane przez Move Field i Move Method, z zachowaniem identycznych ofert na każdym etapie.

**Czas:** 35 minut pracy, 15 minut omówienia.

**Pliki wyjściowe:**

- `src/main/java/pl/training/module4/stage1/RentalQuoteService.java` (klasa po lokalnych porządkach; zawiera jednocześnie wycenę i budowę dokumentu)
- `src/test/java/pl/training/module4/RentalQuoteStagesEquivalenceTest.java`
- `src/test/java/pl/training/module4/LocaleIndependentFormattingTest.java`

**Kontekst:** klasa etapu 1 ma stałe `LONG_RENTAL_DAYS`, `INSURANCE_DAILY_RATE`, `DELIVERY_FEE`, `VAT_RATE`, `ZERO_MONEY`, pola instancyjne `dailyRates` i `longRentalDiscountRate`, metody `calculateDiscount`, `qualifiesForLongRentalDiscount`, `calculateInsuranceCost`, `calculateDeliveryCost`, `money` oraz `buildDocument` przyjmującą osiem parametrów.

**Przed rozpoczęciem odpowiedz na piśmie:**

- kto jest właścicielem stawek,
- czy konfiguracja jest wspólna dla wszystkich ofert,
- jaki jest cykl życia obiektu wyceny,
- czy przenoszona metoda korzysta z monitora, przesłonięcia, dostępu pakietowego lub adnotacji frameworka,
- czy stary punkt wejścia musi pozostać ze względu na zewnętrznych klientów.

**Polecenia:**

1. Nazwij odpowiedzialność, która ma zostać wydzielona, oraz jej klientów.
2. Wprowadź typ wyniku obliczeń `PriceBreakdown`.
3. Wprowadź w klasie źródłowej prywatny punkt `calculatePrice`, który nadal wykonuje istniejący algorytm.
4. Utwórz `RentalPricing`, przenieś do niego implementację oraz zastąp ciało źródłowej metody delegowaniem.
5. Przenieś metody wyceny wraz z używanymi stawkami i stałymi.
6. Pozostaw formatowanie dokumentu w `RentalQuoteService`.
7. Dodaj konstruktor wstrzykujący obiekt wyceny, zachowując bezargumentowy punkt wejścia.
8. Usuń duplikaty stawek i reguł z klasy źródłowej.
9. Dodaj test jednostkowy nowego obiektu oraz uruchom test równoważności wszystkich etapów.

Wskazówka organizacyjna: pracuj w osobnym pakiecie (na przykład `pl.training.module4.exercise3`) albo na osobnej gałęzi, aby nie nadpisać kodu referencyjnego.

**Kryteria akceptacji:**

- istnieje jedno źródło prawdy dla stawek i progu rabatu,
- klasa dokumentu nie zna algorytmu wyceny,
- klasa wyceny nie zna tekstowego formatu dokumentu,
- zależności są przekazywane jawnie,
- wynik wyceny ma nazwane składowe,
- wszystkie zatwierdzone oferty są identyczne na każdym etapie,
- formatowanie nie zależy od domyślnego locale (test z locale `ar-EG` jest zielony).

---

## Ćwiczenie 4: kontrola mutowalnego stanu

**Cel:** przeprowadzić Encapsulate Field i Encapsulate Collection w dwóch oddzielnych fazach: najpierw refaktoryzację zachowującą dotychczasowy kontrakt, potem jawną, zatwierdzoną zmianę kontraktu.

**Czas:** 30 minut pracy, 15 minut omówienia.

**Plik wyjściowy:**

- `src/main/java/pl/training/module4/encapsulation/LegacyEquipmentCatalog.java`

**Kontekst:**

~~~java
public final class LegacyEquipmentCatalog {
    public String name;
    public final Map<EquipmentType, BigDecimal> dailyRates;

    public LegacyEquipmentCatalog(
            String name,
            Map<EquipmentType, BigDecimal> dailyRates) {
        this.name = name;
        this.dailyRates = dailyRates;
    }
}
~~~

Klient może zmienić nazwę, stawkę, usunąć wpis albo modyfikować mapę wejściową po skonstruowaniu katalogu.

Wskazówka organizacyjna: stan pośredni fazy A i stan docelowy fazy B utwórz jako nowe klasy we własnym pakiecie (albo na osobnej gałęzi), tak aby można było porównać trzy wersje.

**Faza A - zachowanie istniejącego kontraktu:**

1. Scharakteryzuj testem zapis do publicznego pola `name`.
2. Scharakteryzuj alias do mapy wejściowej i możliwość mutacji mapy zwracanej klientowi.
3. Ukryj pola.
4. Dodaj najwęższe akcesory i operacje przejściowe, które zachowują dotychczasowe obserwacje.
5. Zmigruj kontrolowanych klientów.

**Faza B - zatwierdzona zmiana kontraktu:**

1. Zdecyduj, czy konstruktor ma przejąć własność mapy przez kopię.
2. Zdefiniuj reguły nazwy i stawki.
3. Zastąp ogólny setter nazwanymi operacjami domenowymi.
4. Wybierz żywy niemodyfikowalny widok albo niemodyfikowalną migawkę.
5. Dodaj testy dla modyfikacji źródła, wyniku i właściciela po pobraniu kolekcji.
6. Oceń zachowanie `null`, mutowalnych elementów oraz współbieżności.

**Kryteria akceptacji:**

- faza A nie ukrywa zmiany zachowania pod nazwą refaktoryzacji,
- faza B ma jawnie opisany nowy kontrakt,
- żaden klient nie otrzymuje modyfikowalnego aliasu do struktury wewnętrznej,
- test rozróżnia migawkę od żywego widoku,
- walidacja jest wykonywana w jednym miejscu,
- potrafisz wyjaśnić, dlaczego niemodyfikowalna kolekcja nie oznacza kopii głębokiej ani bezpieczeństwa wątkowego,
- produkt: dwie klasy (stan po fazie A i stan po fazie B), testy charakterystyki fazy A oraz testy nowego kontraktu fazy B, krótki opis nowego kontraktu.

---

## Retrospektywa po każdym ćwiczeniu

**Cel:** utrwalić rozróżnienie między mechaniką a decyzją projektową.

**Czas:** około 5 minut po każdym ćwiczeniu (w ramach czasu omówienia).

Zespół odpowiada na cztery pytania:

1. Jaka obserwowalna właściwość była chroniona?
2. Który krok był czysto mechaniczny, a który wymagał decyzji projektowej?
3. Jaki najmniejszy test dawał wiarygodny sygnał?
4. W którym momencie należało zakończyć serię transformacji?

---

## Sprawdzenie wiedzy

1. Co musi pozostać niezmienione podczas refaktoryzacji?
2. Dlaczego zielony test jednostkowy nie dowodzi kompatybilności publicznej biblioteki?
3. Jakie trzy grupy zmiennych trzeba rozpoznać przed Extract Method?
4. Kiedy Extract Variable może zmienić zachowanie?
5. Czym Replace Magic Numbers różni się od mechanicznego Extract Constant?
6. Dlaczego publiczna stała typu prostego lub `String` wymaga ostrożności przy zmianie wartości?
7. Kiedy Inline Variable może zwiększyć liczbę efektów ubocznych?
8. Dlaczego Inline Method metody, którą może przesłonić podklasa, może zmienić wynik?
9. Co może zostać utracone przy Inline Method metody `synchronized`?
10. Jakie kryterium jest ważniejsze przy Move Method niż liczba wywołań?
11. Jak bezpiecznie wykonać Move Field?
12. Jaki sygnał uzasadnia Extract Class?
13. Dlaczego prywatne pole z publicznym setterem nie gwarantuje hermetyzacji?
14. Czym różni się niemodyfikowalny widok od niemodyfikowalnej migawki?
15. Czy `Map.copyOf` wykonuje kopię głęboką?
16. Czy zwracanie niemodyfikowalnej mapy zapewnia bezpieczeństwo wątkowe obiektu?
17. Jak Extract Method może naruszyć krótkie spięcie?
18. Dlaczego test nowej implementacji wyłącznie przez porównanie ze starą jest niewystarczający?
19. Kiedy Rename wymaga strategii migracji zamiast jednorazowej operacji IDE?
20. Dlaczego Extract i Inline nie są sprzecznymi zaleceniami?
