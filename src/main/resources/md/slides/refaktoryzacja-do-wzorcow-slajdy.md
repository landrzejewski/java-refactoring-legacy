---
marp: true
theme: default
paginate: true
size: 16:9
style: |
  section { font-size: 22px; padding: 40px 56px; }
  section.lead h1 { font-size: 46px; }
  section.part h1 { font-size: 40px; }
  h2 { font-size: 32px; margin-bottom: 12px; }
  pre, code { font-size: 15px; }
  table { font-size: 18px; }
  li { margin-bottom: 4px; }
  p.ref { font-size: 15px; color: #666; margin-top: 8px; }
---

<!-- _class: lead -->
# Refaktoryzacja do wzorców

Jak bezpiecznie i małymi krokami doprowadzić istniejący kod do wzorca projektowego, kiedy z tego zrezygnować i kiedy wzorzec usunąć

---

## Jak czytać ten materiał

- Każdy slajd przedstawia jedną myśl: tytuł mówi, **co** twierdzimy, a punkty wyjaśniają, **dlaczego** tak jest i **jakie są tego konsekwencje**.
- Wzorce i refaktoryzacje nazywamy po polsku, tak jak w polskich wydaniach książek Bandy Czworga i Martina Fowlera. Nazwę angielską, której używają narzędzia i dokumentacja, podajemy w nawiasie.
- Przykłady kodu pochodzą z systemu kina **CineLegacy**, na którym prowadzony jest warsztat. Dopisek „Warsztat: m5/s08” wskazuje scenę z pełnym kodem, testami i kolejnymi krokami.
- Części I-III to podstawy, bez których refaktoryzacja do wzorca nie jest bezpieczna. Części IV-VI omawiają refaktoryzacje do wzorców oraz usuwanie wzorców, które przestały być potrzebne.

---

## Plan

| Część | Sekcje | Treść |
| --- | --- | --- |
| I. Refaktoryzacja | 1 | definicja, zachowanie obserwowalne, zapachy kodu |
| II. Zanim wprowadzisz wzorzec | 2 | małe kroki, YAGNI, decyzje, SOLID, granice |
| III. Siatka bezpieczeństwa | 3 | testy charakteryzujące, testy równoważności, szwy, refaktoryzacje w IDE, weryfikacja w cieniu |
| IV. Wzorce i decyzja | 4-5 | rodzaj zmienności, koszt wzorca, opłacalność |
| V. Refaktoryzacje do wzorców | 6-16 | Strategia, Stan, Polecenie, Metoda szablonowa, Dekorator, Łańcuch zobowiązań, Adapter, obiekt graniczny, Fabryka, Fasada, warstwa antykorupcyjna, Budowniczy, Obserwator, Mediator, Odwiedzający, Kompozyt i inne |
| VI. Usuwanie wzorców i podsumowanie | 17-18 | usuwanie zbędnych wzorców, mapa decyzji, lista kontrolna |

---

<!-- _class: part -->
# Część I. Refaktoryzacja

Co dokładnie zmieniamy, a czego zmienić nie wolno

---

## 1.1. Refaktoryzacja zmienia strukturę, nie zachowanie

- **Refaktoryzacja** to przebudowa wewnętrznej struktury kodu bez zmiany jego zachowania obserwowalnego z zewnątrz. Celem jest kod łatwiejszy do zrozumienia i tańszy w kolejnych zmianach.
- Refaktoryzację przeprowadza się serią **małych kroków**. Po każdym z nich kod się kompiluje, testy przechodzą, a pracę można przerwać albo cofnąć ostatni krok.
- Jeśli podczas „porządków” zmienia się wynik, komunikat błędu albo kolejność efektów, to nie jest już refaktoryzacja, lecz zmiana zachowania. Bywa potrzebna, ale wprowadzamy ją świadomie i osobno.

---

## 1.2. Zachowanie obserwowalne to więcej niż wynik metody

Klient, czyli kod wywołujący, może zauważyć każdy z tych wymiarów. Test, który sprawdza tylko jeden z nich, przepuści regresję w pozostałych.

| Wymiar | Przykład z kina |
| --- | --- |
| wynik | kwota do zapłaty `74.00` |
| wyjątki | typ, komunikat i moment błędu dla nieznanego programu zniżek |
| stan po sukcesie i po błędzie | rezerwacja zostaje w stanie `NEW`, gdy bramka płatności odmówi |
| współpracownicy | jeden mail i jedno obciążenie karty, w tej kolejności |
| czas i środowisko | godzina wygenerowania dokumentu, ustawienia regionalne (`Locale`) użyte przy formatowaniu kwot |
| granice | dane zapisane w bazie, nagłówek pliku CSV, nazwy w konfiguracji |

<p class="ref">Warsztat: m3/s03 (wektor obserwowalnego zachowania)</p>

---

## 1.3. Refaktoryzacja, zmiana zachowania, przepisanie

| Rodzaj zmiany | Co się zmienia | Jak ją weryfikujemy |
| --- | --- | --- |
| refaktoryzacja | tylko struktura kodu | ten sam test przed każdym krokiem i po nim |
| naprawa błędu, nowa funkcja | zachowanie, celowo | nowy test, który opisuje nowe zachowanie |
| zmiana kontraktu | typ, format, moment błędu | uzgodnienie z klientami, osobne zatwierdzenie zmian |
| przepisanie | wszystko naraz | trudno, bo brakuje punktu odniesienia |

- Tych rodzajów zmian **nie łączymy w jednym kroku**. Gdy wiemy, który krok zmienił zachowanie, łatwo go przejrzeć, uzasadnić i wycofać.
- Przepisanie od zera kusi, ale grozi utratą wiedzy zapisanej w starym kodzie, także w jego „dziwnych” regułach. Seria małych refaktoryzacji zwykle prowadzi do celu taniej.

---

## 1.4. Kiedy refaktoryzować

- Najlepszy moment to chwila **tuż przed zmianą**, którą i tak trzeba wprowadzić: najpierw ułatw zmianę (refaktoryzacja), a potem wprowadź łatwą już zmianę (nowa funkcja).
- Refaktoryzujemy kod, który **często się zmienia** albo jest trudny do sprawdzenia. Brzydki, ale stabilny kod, którego nikt nie rusza, może poczekać.
- Każda refaktoryzacja ma cel: konkretną zmianę, którą chcemy uczynić tańszą. Bez celu łatwo przebudowywać kod bez końca.
- Warunkiem wstępnym jest siatka bezpieczeństwa, czyli testy, które potwierdzą, że zachowanie się nie zmieniło (część III).

---

## 1.5. Zapachy kodu to hipotezy, nie wyroki

- **Zapach kodu** (ang. code smell) to objaw, który *sugeruje* problem projektowy: powielona instrukcja `switch`, długa metoda, klasa, która zna zbyt wiele innych klas.
- Zapach nie mówi, co zrobić. Ten sam `switch` może oznaczać wymienny algorytm (Strategia), cykl życia obiektu (Stan) albo po prostu dwa stabilne przypadki, które najlepiej zostawić w spokoju.
- Zanim sięgniemy po wzorzec, opisujemy problem **bez nazwy wzorca**: co się zmienia, jak często, kto o tym decyduje i ile miejsc trzeba dziś poprawić przy jednej zmianie.

---

## 1.6. Od zapachu do kierunku zmiany

| Zapach | Co zwykle oznacza | Możliwy kierunek |
| --- | --- | --- |
| ten sam `switch` po rodzaju w kilku metodach | zachowanie zależy od trwałego rodzaju obiektu | polimorfizm, Stan, Strategia |
| `if` po trybie wybieranym przez klienta | wymienny algorytm | Strategia |
| `if` po nazwie polecenia | rozdzielanie żądań | Polecenie |
| flagi `vip`, `glasses` w każdej metodzie | opcjonalne dodatki wokół rdzenia | Dekorator |
| surowe mapy i napisy z innych systemów w kodzie domeny | brak granicy danych | obiekt graniczny, warstwa antykorupcyjna |
| kilku klientów powtarza tę samą sekwencję wywołań | rozproszona koordynacja | Fasada |
| komponenty wywołują się nawzajem | rozproszone reguły współpracy | Mediator |
| pośrednik, który tylko przekazuje wywołania | nadmiar warstw | Usunięcie pośrednika |

Krótkie definicje wszystkich wzorców z tej tabeli są na slajdach 4.5-4.6.

---

<!-- _class: part -->
# Część II. Zanim wprowadzisz wzorzec

Zapobieganie: małe kroki, odroczone decyzje i kierunek zależności

---

## 2.1. Małe kroki i szybka informacja zwrotna

- Im szybciej dowiadujemy się o błędzie, tym taniej go naprawić. Kompilator odpowiada w kilka sekund, testy jednostkowe w kilka minut, przegląd kodu po kilku godzinach, a użytkownicy po kilku dniach.
- Dlatego dzielimy zmianę na kroki, które kompilator i testy mogą sprawdzić od razu. Błąd w małym kroku ma mały zasięg, a sam krok łatwo wycofać.
- Warto zawsze pracować w tej samej pętli: jeden ruch, kompilacja, testy, zatwierdzenie zmian w repozytorium (ang. commit). Duża zmiana to wiele takich pętli, a nie jeden wielki skok.

---

## 2.2. Bramki jakości w starym kodzie: stan bazowy i zapadka

- **Bramka jakości** to automatyczne sprawdzenie, które przerywa budowanie projektu, gdy kod łamie regułę, np. ostrzeżenia kompilatora traktowane jak błędy (`-Xlint:all -Werror`), zakaz `System.out` albo wymóg testów dla nowych metod.
- W dużym, starym kodzie nie włącza się bramki z dnia na dzień, bo budowanie kończyłoby się błędem przez tygodnie. Najpierw zapisujemy **stan bazowy**, potem wymagamy, żeby nie pojawiały się nowe naruszenia, i stopniowo podnosimy poprzeczkę. Poziom może tylko rosnąć, jak w zapadce (ang. ratchet).
- Bramka musi działać powtarzalnie, wskazywać konkretne miejsce i nie zgłaszać fałszywych alarmów. W przeciwnym razie zespół szybko ją wyłączy.

<p class="ref">Warsztat: m2/s03 (bramka kompilatora), m2/s04 (minimalna bramka jakości zapisana w kodzie)</p>

---

## 2.3. YAGNI: buduj dla dzisiejszej potrzeby

```java
// TicketPricer: silnik reguł z rejestrem wtyczek i kontekstem w Map, a reguły są tylko dwie
Map<String, Object> context = new HashMap<>();
context.put("start", quote.start());
context.put("row", quote.row());
for (PricingRule rule : registry.resolve(activeRules)) {
    if (rule.appliesTo(context)) price = rule.apply(context, price);
}
```

- **YAGNI** (ang. You Aren't Gonna Need It, „nie będziesz tego potrzebować”): nie buduj mechanizmu dla wymagania, które dopiero *może* się pojawić. Tutaj pięć klas obsługuje dwie reguły, a literówka w nazwie reguły ujawnia się dopiero w czasie działania programu.
- YAGNI nie zabrania pisania testów ani nadawania czytelnych nazw. To właśnie one pozwalają bezpiecznie odłożyć abstrakcję do chwili, gdy realna potrzeba pokaże, jak powinna wyglądać (zasada ostatniego odpowiedzialnego momentu).

<p class="ref">Warsztat: m2/s01</p>

---

## 2.4. Pięć pytań przed dodaniem abstrakcji

Zanim dodasz interfejs, fabrykę albo wzorzec, odpowiedz na pięć pytań. Jeśli odpowiedzi są słabe, lepszy jest prostszy kod.

1. Jakie **dzisiejsze** wymaganie wymusza tę abstrakcję? Ile wariantów istnieje teraz, a nie „kiedyś”?
2. Czy potrafię ją nazwać w języku problemu, a nie technologii (`DiscountPolicy`, a nie `PricingPluginManager`)?
3. Jaka jest prostsza alternatywa i dlaczego nie wystarczy?
4. Ile kosztuje? Nowe typy, pośredniość, konfiguracja, trudniejsze śledzenie przepływu.
5. Czy da się ją później łatwo usunąć? Decyzje odwracalne podejmujemy szybko, nieodwracalne ostrożnie.

---

## 2.5. ADR: zapisz decyzję, a najlepiej ją sprawdzaj

- **ADR** (ang. Architecture Decision Record, zapis decyzji architektonicznej) to krótki dokument opisujący decyzję o trwałych skutkach: kontekst, rozważane opcje, wybór i konsekwencje. Przyjętego ADR się nie edytuje; gdy decyzja się zmienia, zastępuje się go nowym.
- Przykład: „cennik jest czystym modułem: nie wysyła maili i nie liczy pieniędzy w `double`”. Taka reguła łatwo się zaciera, jeśli zna ją tylko autor.
- Regułę z ADR warto zamienić w **test architektury**, który sprawdza ją przy każdym budowaniu projektu. Wtedy dokument wyjaśnia, dlaczego reguła istnieje, a test sprawdza, czy nadal jest przestrzegana.
- ADR nie powstaje przy każdej zmianie nazwy, tylko przy decyzjach, które trudno odwrócić.

<p class="ref">Warsztat: m2/s02</p>

---

## 2.6. Zasada pojedynczej odpowiedzialności: jeden aktor, jeden powód zmiany

```java
private BigDecimal revenue(Sale sale) {                         // wspólna metoda pomocnicza
    return sale.ticketRevenue().add(sale.barRevenue());
}
total = total.add(revenue(sale));                               // księgowość
byTitle.merge(sale.title(), revenue(sale), BigDecimal::add);    // marketing
```

- **Zasada pojedynczej odpowiedzialności** (SRP) mówi, że moduł odpowiada przed jednym aktorem, czyli ma tylko jeden powód do zmiany. Nie oznacza to, że klasa ma „robić jedną rzecz”.
- Raport dzienny obsługuje tu dwóch aktorów: księgowość (VAT, sumy) i marketing (ranking najpopularniejszych filmów). Obie części liczą przychód tą samą metodą, więc gdy marketing zmieni sposób wyłaniania hitów, na przykład pominie przychód z baru, po cichu zmienią się też kwoty księgowe.
- Rozwiązanie: osobna klasa dla każdego aktora (`AccountingSection`, `MarketingSection`). Dwie formuły, dziś identyczne, nie są powieleniem wiedzy, bo zmieniają się z różnych powodów.

<p class="ref">Warsztat: m2/s07</p>

---

## 2.7. Zasada odwrócenia zależności: liczy się kierunek importów

- **Zasada odwrócenia zależności** (DIP) mówi, że reguły biznesowe i przypadki użycia nie powinny zależeć od szczegółów technicznych. To szczegóły mają zależeć od abstrakcji zdefiniowanej po stronie reguł.
- Przypadek użycia `ConfirmReservation` sam tworzy klienta poczty SMTP i zna protokół. Przekazanie `SmtpMailSender` przez konstruktor niczego nie odwraca, bo przypadek użycia nadal importuje klasę techniczną.
- Do odwrócenia dochodzi dopiero wtedy, gdy przypadek użycia definiuje port we własnym języku (`CustomerNotifier`), a adapter SMTP w warstwie infrastruktury go implementuje. Wywołania nadal płyną do SMTP, ale zależność w kodzie źródłowym biegnie w przeciwną stronę.

<p class="ref">Warsztat: m2/s08</p>

---

## 2.8. Reguła zależności, porty i korzeń kompozycji

- **Reguła zależności:** importy są skierowane do środka, od adapterów (HTTP, baza, poczta) do przypadków użycia i domeny. Domena nie importuje `java.sql` ani typów z bibliotek zewnętrznych.
- **Port** to interfejs należący do tej strony, która zgłasza potrzebę. Przez granicę przechodzą proste rekordy, a nie wiersze z bazy czy obiekty HTTP.
- **Korzeń kompozycji** (ang. composition root) to jedno miejsce, w którym przy starcie aplikacji tworzy się obiekty i łączy je w graf zależności. Nie zawiera reguł biznesowych.
- Regułę zależności warto sprawdzać automatycznie (ArchUnit, moduły Maven, moduły Javy), bo reguła zapisana tylko w dokumencie szybko przestaje obowiązywać w kodzie.

<p class="ref">Warsztat: m2/s09 (czysta architektura), m2/s10 (automatyczna ochrona granicy)</p>

---

## 2.9. Zasada harcerza i małe porcje zmian

- **Zasada harcerza** (ang. Boy Scout Rule): kod, który i tak zmieniasz, zostaw w nieco lepszym stanie, niż go zastałeś. To wskazówka, a nie przyzwolenie na przebudowę całego modułu. Poprawka jest lokalna i nie zmienia zachowania.
- Nadużycie wygląda tak: „skoro już tu jestem, posortuję miejsca na bilecie”. Wydruk biletu się zmienia, a w historii zmian widnieje tylko „porządki”.
- Dobra porcja zmian ma jeden cel i własny dowód poprawności. Oddzielamy refaktoryzację od nowej funkcji, formatowanie od logiki, zmianę automatyczną od ręcznej.
- Przykładowa seria: wyodrębnienie metody, następnie zmiana sygnatury, a dopiero na końcu nowa reguła „tani wtorek”. Przy każdym zatwierdzeniu recenzent sprawdza tylko jedną rzecz.

<p class="ref">Warsztat: m2/s05 (zasada harcerza), m2/s06 (seria zmian gotowych do przeglądu)</p>

---

<!-- _class: part -->
# Część III. Siatka bezpieczeństwa

Jak udowodnić, że refaktoryzacja nie zmieniła zachowania

---

## 3.1. Test charakteryzujący: zapisz, co kod robi dziś

- Gdy kod nie ma testów, nie wiemy, co jest jego kontraktem. **Test charakteryzujący** zapisuje to, co kod *faktycznie robi*, a nie to, co *powinien* robić.
- Utrwalamy także dziwne reguły. Przykład: rabat grupowy przysługuje dopiero od 11 biletów, choć reguła mówi o 10. Test nazywa to wprost („ZASTANE: 10 biletów bez rabatu”), ale niczego nie poprawia.
- Elementy, które zmieniają się między uruchomieniami (bieżący czas, ustawienia regionalne), najpierw maskujemy w teście, a potem eliminujemy, przekazując zależność z zewnątrz, np. zegar `Clock`.
- Taki test powstaje **przed pierwszą zmianą** i jest uruchamiany po każdym kolejnym kroku.

<p class="ref">Warsztat: m3/s01</p>

---

## 3.2. Wynik wzorcowy i dobór przypadków

- **Wynik wzorcowy** (ang. golden master) to pełny wynik działania programu zapisany w pliku i zatwierdzony przez człowieka. W CineLegacy są to odpowiedzi starej klasy `CinemaManager` na kilkadziesiąt operacji (rezerwacje, błędy, płatności, raporty). Każda różnica przy kolejnym uruchomieniu kończy test błędem.
- Przypadki dobieramy tak, żeby objąć **każdą gałąź**, granice warunków (100,00 i 100,01 zł, 120 i 121 minut), wartości `null` i nieznane oraz awarie w połowie sekwencji.
- Pokrycie kodu pomaga znaleźć gałęzie, przez które nie przeszedł żaden przypadek. Nie mówi jednak, czy test sprawdza właściwe rzeczy: linia może zostać wykonana, a jej skutek pozostać niesprawdzony.

---

## 3.3. Test równoważności przed zmianą i po niej

```java
Scene.<PriceRequest, String>variants()
    .variant("start", r -> run(() -> new start.PriceBoard().priceFor(r)))   // pakiety skrócone
    .variant("step3", r -> run(() -> new step3.PriceBoard().priceFor(r)))
    .expect("STANDARD student 3D", request("32.00", "S", "STANDARD"), "24.00")
    .expect("PREMIERE nie sprawdza typu", request("40.00", "X", "PREMIERE"), "40.00")
    .expect("nieznany program", request("25.00", "N", "BLACK_FRIDAY"),
            "ERROR: unknown program: BLACK_FRIDAY")
```

- Stara i nowa wersja istnieją obok siebie, a ten sam zestaw oczekiwań uruchamiamy na obu. Porównujemy wynik **i** błąd, łącznie z treścią komunikatu.
- Oczekiwania są zapisane niezależnie od kodu, jako dosłowne wartości. Test, który porównuje tylko starą wersję z nową, nie wykryje błędu obecnego w obu.

<p class="ref">Warsztat: każda scena m5 ma test równoważności, np. m5/s01</p>

---

## 3.4. Szew: miejsce, w którym podstawiamy zależność

```java
public int run() {
    LegacyDatabase database = new LegacyDatabase();     // prawdziwa baza
    LocalDateTime now = LocalDateTime.now();            // zegar systemowy
    ...
    ReminderMailer.send(booking.email(), "Przypomnienie: " + booking.title(), ...);
    database.markReminded(booking.id());
}
```

- Tej klasy nie da się uruchomić w teście: sama otwiera połączenie z bazą, odczytuje zegar systemowy i wywołuje statyczną metodę wysyłającą maile.
- **Szew** (ang. seam, pojęcie z książki M. Feathersa „Working Effectively with Legacy Code”) to miejsce, w którym można podstawić inną implementację bez zmieniania algorytmu. Wybieramy najwęższy szew, który wystarczy: parametr, zależność w konstruktorze, zegar `Clock` albo interfejs funkcyjny.
- Rozrywanie zależności zmienia miejsce, w którym tworzone są obiekty, a nie reguły biznesowe.

---

## 3.5. Rozrywanie zależności: po zmianie

```java
public ShowtimeReminderJob() {                                  // wersja produkcyjna: jak dawniej
    this(LegacyDatabase::new, Clock.systemDefaultZone(), ReminderMailer::send);
}
public ShowtimeReminderJob(Supplier<BookingStore> stores, Clock clock, ReminderSender sender) {
    this.stores = Objects.requireNonNull(stores, "stores");
    this.clock = Objects.requireNonNull(clock, "clock");
    this.sender = Objects.requireNonNull(sender, "sender");
}
```

- Domyślny konstruktor tworzy dokładnie te same implementacje co wcześniej, więc w działającym systemie nic się nie zmienia, łącznie z połączeniem z bazą otwieranym przy każdym `run()`.
- Test podstawia udawaną bazę, zegar o stałym czasie i wyrażenie lambda zamiast wysyłki maila. Pierwszy test sprawdza granicę 120 i 121 minut, której wcześniej nikt nie weryfikował.

<p class="ref">Warsztat: m3/s02</p>

---

## 3.6. Refaktoryzacje automatyczne w IDE: co dają, a czego nie widzą

- Zmiana nazwy, wyodrębnienie metody, wchłonięcie, przeniesienie i zmiana sygnatury (w IDE: Rename, Extract Method, Inline, Move, Change Signature) działają na **modelu symboli** programu. Są więc znacznie bezpieczniejsze niż „znajdź i zamień”, a gdy brakuje testów, to najbezpieczniejszy sposób zmieniania kodu.
- IDE nie widzi jednak nazw używanych poza kodem Javy: w refleksji, plikach konfiguracyjnych, szablonach, JSON, kolumnach bazy, nagłówkach CSV i u odbiorców spoza repozytorium.
- Narzędzie nie rozumie znaczenia kodu: nie wie, czy dwa podobne fragmenty wyrażają tę samą regułę.
- Zasada: jedna automatyczna refaktoryzacja, kompilacja, testy, zatwierdzenie. Ręczne poprawki robimy w osobnym kroku.

---

## 3.7. Zmiana nazwy i wyodrębnienie metody w drodze do wzorca

- **Zmiana nazwy** (Rename) nadaje nazwę opisującą rolę w kontekście (`calc2` → `revenueCsv`). Jeśli nazwa metody występuje w konfiguracji na serwerze (`report.method=calc2`), stara metoda zostaje jako przestarzała metoda pośrednicząca, oznaczona `@Deprecated`, dopóki ktoś nie zmieni konfiguracji.
- **Wyodrębnienie metody** (Extract Method) to zwykle pierwszy krok w drodze do każdego wzorca: gałąź `switch` staje się metodą, a ta później klasą strategii, stanu albo polecenia.
- Przy wydzielaniu analizujemy przepływ danych: co wchodzi jako parametr, a co wychodzi jako wynik. Fragment, który daje dwa wyniki (sumę i liczbę miejsc VIP), najpierw dzielimy na dwie pętle (Split Loop), zamiast zwracać tablicę.
- Uważaj na `return`, `break`, `try/finally` i efekty uboczne: wydzielenie metody nie może zmienić liczby ani kolejności wywołań.

<p class="ref">Warsztat: m5/s24 (zmiana nazwy), m5/s25 (wyodrębnienie metody)</p>

---

## 3.8. Weryfikacja w cieniu

```java
public Money price(TicketQuery query) {
    Money result = legacy.price(query);              // klient dostaje wynik starej wersji
    report.record(verify(query, result));
    return result;
}
private Verification verify(TicketQuery query, Money legacyPrice) {
    try {
        Money candidatePrice = candidate.price(query);
        return legacyPrice.equals(candidatePrice) ? new Agreement(query, legacyPrice)
                : new Divergence(query, legacyPrice, candidatePrice);
    } catch (RuntimeException failure) {             // awaria nowej wersji jest izolowana
        return new CandidateFailure(query, legacyPrice, failure.getMessage());
    }
}
```

- Nowa implementacja liczy wynik **w cieniu**, na prawdziwych żądaniach, ale klient nadal dostaje wynik starej. Rozbieżności trafiają do raportu (ang. parallel run, shadow mode).
- W CineLegacy raport wykazał, że nowa wersja stosuje rabat poranny i zniżkę procentową w złej kolejności, zanim jeszcze zaczęła obsługiwać klientów.
- Zgodność obu wersji nie dowodzi poprawności: obie mogą mieć ten sam błąd.

<p class="ref">Warsztat: m3/s04</p>

---

## 3.9. Granice weryfikacji w cieniu: efekty uboczne

- Podwójne wykonanie jest bezpieczne tylko w przypadku **czystych obliczeń**. Jeśli nowa wersja działająca w cieniu wysyła maile i obciąża kartę, klient dostaje dwa maile, a jego karta zostaje obciążona dwa razy.
- Rozwiązanie: oddziel decyzję od wykonania. Nowa wersja zwraca **plan efektów w postaci danych** („wyślij mail o treści X, obciąż kartę kwotą Y”). Plany można porównać, a wykonuje się tylko jeden z nich.
- Bezpieczeństwo najlepiej zagwarantować systemem typów: kod planujący w ogóle nie ma dostępu do portu, który wykonuje efekty.
- Zgodny wynik metody nie oznacza zgodnych efektów, dlatego porównujemy jedno i drugie.

<p class="ref">Warsztat: m3/s05</p>

---

## 3.10. Bezpieczna pętla refaktoryzacji do wzorca

1. **Nazwij problem** i granicę zachowania, którą chronisz: co jest wynikiem, co efektem, a co błędem.
2. **Dodaj test** charakteryzujący dla każdej gałęzi, wartości nieznanej, `null` i awarii.
3. **Wprowadź minimalny kontrakt**, który na początku deleguje do starego kodu. Pierwsze zatwierdzenie jest małe i łatwe do wycofania.
4. **Przenoś po jednej gałęzi** i uruchamiaj testy po każdym ruchu.
5. **Usuń stary kod** dopiero po przełączeniu wszystkich klientów. Zmiany zachowania, jeśli są potrzebne, zatwierdź osobno.

Ten schemat powtarza się w każdej refaktoryzacji z części V.

---

<!-- _class: part -->
# Część IV. Wzorce i decyzja

Kiedy wzorzec pomaga, a kiedy tylko dokłada klas

---

## 4.1. Czym jest wzorzec projektowy

- Wzorzec to nazwane, sprawdzone rozwiązanie powtarzającego się problemu projektowego w określonym kontekście. Opis wzorca obejmuje problem, okoliczności, które na niego wpływają, rozwiązanie i **konsekwencje**.
- Wzorzec nie jest gotowym kodem do skopiowania. Ta sama idea może przyjąć postać hierarchii klas, typu wyliczeniowego (`enum`), rekordu albo zwykłego wyrażenia lambda.
- Klasyczny katalog **Bandy Czworga** (ang. Gang of Four, GoF: Gamma, Helm, Johnson, Vlissides, 1994) opisuje 23 wzorce i dzieli je na **konstrukcyjne** (jak tworzyć obiekty), **strukturalne** (jak składać obiekty) i **operacyjne** (jak rozdzielać odpowiedzialność i współpracę).
- Największą wartością wzorców jest wspólne słownictwo: zdanie „to jest Dekorator” mówi zespołowi więcej niż opis pięciu klas.

---

## 4.2. Najpierw rodzaj zmienności, potem wzorzec

| Pytanie o kod | Typowa technika |
| --- | --- |
| Czy zmienia się algorytm, który klient wybiera dla operacji? | Strategia |
| Czy zachowanie wynika z trwałego rodzaju obiektu? | polimorfizm podtypów |
| Czy zachowanie zależy od bieżącego stanu i przejść między stanami? | Stan |
| Czy żądanie trzeba nazwać, zapisać w dzienniku lub cofnąć? | Polecenie |
| Czy reguły sprawdzamy po kolei, a działa dokładnie jedna? | Łańcuch zobowiązań |
| Czy dodatek opakowuje rdzeń o tym samym kontrakcie? | Dekorator |
| Czy problem dotyczy tylko utworzenia właściwego obiektu? | Fabryka |

Ten sam `switch` może sygnalizować każdy z tych problemów. O wyborze decyduje to, **co naprawdę się zmienia**, oraz to, czy zbiór wariantów jest otwarty, czy zamknięty.

<p class="ref">Warsztat: m5/s20 (jedna tabela cen, dwie różne struktury docelowe)</p>

---

## 4.3. Wzorzec ma swój koszt

- Każdy wzorzec dodaje typy, pośredniość i konfigurację. Przepływ, który dotąd był widoczny w jednej metodzie, rozkłada się na kilka klas.
- Zysk musi być **widoczny już przy najbliższych zmianach**: nowy wariant to nowa klasa zamiast poprawek w pięciu miejscach, test nie wymaga bazy danych, a kolejności kroków pilnuje jedno miejsce.
- Jeśli wzorzec przestanie się opłacać, można go usunąć. **Usunięcie** wzorca jest taką samą refaktoryzacją jak jego **wprowadzenie** (sekcja 17).

---

## 4.4. Podobna struktura, różna intencja

Kilka wzorców ma ten sam kształt: obiekt opakowuje inny obiekt i przekazuje mu wywołania. Różni je **cel**, w jakim to robią.

| Wzorzec | Po co opakowuje | Kontrakt widoczny z zewnątrz |
| --- | --- | --- |
| Adapter | tłumaczy obcy interfejs na nasz | inny niż obiekt wewnątrz |
| Dekorator | dodaje zachowanie wokół rdzenia | taki sam jak rdzeń |
| Pełnomocnik (Proxy) | kontroluje dostęp (leniwe tworzenie, uprawnienia, dostęp zdalny) | taki sam jak obiekt wewnątrz |
| Fasada | upraszcza dostęp do kilku podsystemów | nowy, prostszy |
| Łańcuch zobowiązań | przekazuje żądanie dalej, gdy sam go nie obsłuży | taki sam w każdym ogniwie |

---

## 4.5. Słowniczek wzorców (1/2): zachowanie i współpraca

| Wzorzec (nazwa angielska) | Na czym polega | Sekcja |
| --- | --- | --- |
| Strategia (Strategy) | rodzina wymiennych algorytmów za wspólnym interfejsem; klient wybiera jeden z nich i przekazuje go obiektowi | 6 |
| Stan (State) | obiekt przekazuje zachowanie obiektowi bieżącego stanu, a stan decyduje o przejściu do następnego | 6 |
| Polecenie (Command) | żądanie zamknięte w obiekcie, który można zapisać w dzienniku, ustawić w kolejce, wykonać później lub cofnąć | 6 |
| Metoda szablonowa (Template Method) | klasa bazowa ustala kolejność kroków, a podklasy nadpisują wybrane kroki | 7 |
| Łańcuch zobowiązań (Chain of Responsibility) | żądanie przechodzi przez łańcuch obiektów, aż jeden z nich je obsłuży | 8 |
| Obserwator (Observer) | obiekt powiadamia zarejestrowanych odbiorców o zdarzeniu, nie znając ich klas | 13 |
| Mediator (Mediator) | komponenty komunikują się przez jeden obiekt koordynujący, a nie bezpośrednio ze sobą | 14 |
| Odwiedzający (Visitor) | operacje na strukturze obiektów są wydzielone do osobnych klas, po jednej klasie na operację | 15 |

---

## 4.6. Słowniczek wzorców (2/2): struktura, tworzenie i granice

| Wzorzec (nazwa angielska) | Na czym polega | Sekcja |
| --- | --- | --- |
| Dekorator (Decorator) | obiekt opakowuje inny obiekt o tym samym interfejsie i dodaje zachowanie przed wywołaniem lub po nim | 8 |
| Adapter (Adapter) | obiekt tłumaczy interfejs obcej klasy na interfejs, którego oczekuje klient | 9 |
| Fasada (Facade) | jedna prosta operacja ukrywa współpracę kilku podsystemów i jej kolejność | 11 |
| Kompozyt (Composite) | liść i węzeł mają wspólny interfejs, więc całe drzewo obsługuje się jak pojedynczy element | 16 |
| Fabryka (Factory) | jedno miejsce decyduje, jaki obiekt utworzyć, i tworzy go w poprawnym stanie | 10 |
| Budowniczy (Builder) | obiekt buduje złożony wynik krok po kroku i zwraca gotowy obiekt z metody `build()` | 12 |
| Singleton (Singleton) | klasa ma dokładnie jedną instancję, dostępną globalnie | 16-17 |
| Obiekt graniczny (Boundary Object) | niezmienny obiekt z danymi z zewnątrz, sprawdzony na granicy systemu (spoza katalogu GoF) | 9 |
| Warstwa antykorupcyjna (Anti-Corruption Layer) | warstwa tłumacząca obcy model na model naszej domeny (z projektowania sterowanego dziedziną, spoza GoF) | 11 |

---

## 5.1. Wzorzec jest rezultatem, nie celem

- Kolejność pracy: zabezpiecz zachowanie testami → nazwij oś zmienności, czyli to, co zmienia się niezależnie od reszty → wyodrębnij role w kodzie → wprowadź **najwęższy** kontrakt, który rozwiązuje problem.
- Przenosimy po jednym wariancie i **przerywamy**, gdy problem jest rozwiązany. Celem nie jest pełna postać wzorca z katalogu, ze wszystkimi klasami z diagramu.
- Przed podjęciem decyzji potrzebujemy czterech rzeczy: znanego problemu, wariantów istniejących już **dziś**, rozważonej prostszej alternatywy oraz warunku, po którym poznamy, że wzorzec przestał być potrzebny.

---

## 5.2. Kiedy wzorzec się nie opłaca

| Sytuacja | Lepszy ruch |
| --- | --- |
| dwa stabilne warianty w jednym miejscu | czytelny `switch` lub `if` |
| wariant „na przyszłość”, bez konkretnego wymagania | poczekaj na drugi rzeczywisty przypadek |
| jeden klient i jedna implementacja | konkretna klasa; interfejs można dodać później |
| problemem są nazwy lub długość metody | zmiana nazwy, wyodrębnienie metody |
| zespół nie zna wzorca, a kod jest krytyczny | prostsza struktura i dobry test |
| wariant wygasa (umowy festiwalowe się kończą) | usuń wariant, a razem z nim wzorzec |

<p class="ref">Warsztat: m5/s21 (wzorzec jako decyzja odwracalna)</p>

---

## 5.3. Refaktoryzacja a zmiana kontraktu

```java
refund = refund - 3.00;                                   // przed: double
refund = Math.round(refund * 100) / 100.0;
return String.format(Locale.ROOT, "%.2f", refund);

BigDecimal refund = BigDecimal.valueOf(ticketsPaid)       // po: BigDecimal
        .multiply(share(minutesBeforeStart)).subtract(FEE)
        .max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
```

- Przejście z `double` na `BigDecimal` wygląda na zwykłe porządki, ale zmienia wynik. Przy rezygnacji na 2 godziny przed seansem zwrot za bilety warte 64.35 zł wynosi 50% minus 3.00 zł opłaty, czyli dokładnie 29.175 zł. Typ `double` przechowuje tę wartość jako 29.17499…, więc wynik zaokrągla się do 29.17, a `BigDecimal` liczy dokładnie i daje 29.18.
- Kryterium refaktoryzacji: klient nie dostrzega żadnej różnicy, której z nim nie uzgodniono. Zmiana typu, zaokrąglenia, formatu czy wyjątku to zmiana kontraktu. Wprowadzamy ją świadomie, w osobnym zatwierdzeniu.

<p class="ref">Warsztat: m5/s23</p>

---

## 5.4. Co trzeba zachować przy wprowadzaniu wzorca

- **Wyjątki:** typ, komunikat (jeśli ktoś go odczytuje) i moment zgłoszenia błędu. Wybór strategii w konstruktorze sprawia, że błąd „nieznany program” pojawia się wcześniej niż dotąd.
- **Stan:** po sukcesie i po błędzie. Nieudana płatność zostawia rezerwację w stanie `NEW`, tak jak przed zmianą.
- **Efekty:** liczba, kolejność i warunki wysłania maili, zapisów do bazy i obciążeń karty.
- **Granice:** format danych zapisanych w bazie lub pliku, nazwy widoczne przez refleksję, publiczne API.
- Każdą taką zmianę trzeba **nazwać, zatwierdzić i opisać** w teście albo wycofać.

---

<!-- _class: part -->
# Część V. Refaktoryzacje do wzorców

Dla każdego wzorca: sygnał w kodzie, kroki, wynik i na co uważać

---

## 6.1. Strategia, Polecenie czy Stan? Kto wybiera wariant i kiedy

| | Strategia | Polecenie | Stan |
| --- | --- | --- | --- |
| kto wybiera wariant | klient, raz, przy konfiguracji | nadawca, przy każdym żądaniu | sam obiekt, przy przejściu między stanami |
| co zamykamy w obiekcie | algorytm | żądanie (dziennik, kolejka, cofanie) | stan i dozwolone przejścia |
| typowy zapach | `if` po trybie w każdym wywołaniu | `if` po nazwie polecenia | `if (status == ...)` w każdej operacji |
| przykład z kina | program zniżek | polecenie kasjera | status rezerwacji |

W kodzie wszystkie trzy problemy wyglądają tak samo: jak `switch`. Zamiast pytać, który wzorzec pasuje do tego `switch`, pytamy: **kto zmienia wariant i kiedy?**

---

## 6.2. Strategia: sygnał w kodzie

```java
if (program.equals("PREMIERE")) {
    discount = Money.ZERO;
} else if (program.equals("STUDENT_WEEK") && ticketType.equals("S")) {
    discount = base.percent(50);
} else if (program.equals("STANDARD") || program.equals("STUDENT_WEEK")) {
    int percent = switch (ticketType) { case "N" -> 0; case "S" -> 25; ... };
    discount = base.percent(percent);
} else {
    throw new IllegalArgumentException("unknown program: " + program);
}
```

- `TicketPricer` wybiera sposób liczenia zniżki na podstawie nazwy programu ustawionego w kinie. W tej samej metodzie znajduje się też sprawdzanie poprawności ceny.
- Program zniżek wynika z konfiguracji kina, a nie z klasy żadnego obiektu. Zmienia się rzadko, ale każda zmiana wymaga dziś edycji łańcucha `if`. To klasyczny kandydat na Strategię (Strategy): wymienny algorytm wybierany z zewnątrz.

<p class="ref">Warsztat: m5/s01</p>

---

## 6.3. Strategia: kroki i wynik

```java
@FunctionalInterface
public interface DiscountPolicy { Money discount(Money base, String ticketType); }

public final class TicketPricer {
    private final DiscountPolicy policy;                 // wybrana raz, w konstruktorze
    public Money price(Money base, String ticketType) {
        if (base.compareTo(Money.ZERO) < 0) throw new IllegalArgumentException(...);
        return base.minus(policy.discount(base, ticketType));
    }
}
// PriceBoard: new TicketPricer(DiscountPrograms.forName(request.program()))
```

- Kroki: interfejs i strategia przejściowa (wyrażenie lambda wywołujące stary łańcuch `if`) → przeniesienie każdej gałęzi do osobnej klasy → wybór programu w jednym miejscu (`DiscountPrograms`).
- Sprawdzanie ceny zostaje w klasie, która korzysta ze strategii (`TicketPricer`), a same strategie nie mają stanu. Nowy program to nowa klasa (albo wyrażenie lambda) i jeden wpis w `DiscountPrograms`.
- Uwaga: wybór strategii w konstruktorze **utrwala decyzję** na cały czas życia obiektu i zmienia kolejność zgłaszania błędów. Taką zmianę trzeba zatwierdzić.

---

## 6.4. Stan: sygnał i tabela przejść

```java
public void cancel() {
    if (status == Status.NEW) {
        status = Status.CANCELLED;
        effects.add("seats released");
    } else if (status == Status.PAID) {
        status = Status.CANCELLED;
        effects.add("refund");
        effects.add("seats released");
    } else {
        throw new IllegalStateException("cannot cancel in " + status);
    }
}
```

- `Reservation` w każdej operacji (`pay`, `use`, `expire`, `cancel`) sprawdza i zmienia pole `status`. Reguły przejść są rozproszone po wielu metodach.
- Zanim powstanie pierwsza klasa stanu, zapisujemy w teście **tabelę przejść**: stan × operacja → nowy stan i efekty albo wyjątek. Ta tabela jest specyfikacją całej refaktoryzacji do wzorca Stan (State).

---

## 6.5. Stan: wynik

```java
private ReservationState state = NewState.INSTANCE;
public void cancel() { state.cancel(this); }                // rezerwacja tylko deleguje

private sealed interface ReservationState permits NewState, PaidState, ClosedState {
    default void cancel(Reservation r) { throw invalid("cancel"); }   // domyślnie: błąd
}
private enum PaidState implements ReservationState {
    INSTANCE;
    public void cancel(Reservation r) {
        r.moveTo(ClosedState.CANCELLED); r.record("refund"); r.record("seats released");
    }
}
```

- Tabelę przejść można odczytać bezpośrednio z kodu stanów. Niedozwolona operacja domyślnie rzuca wyjątek; pusta metoda po cichu zmieniłaby zachowanie.
- Obiekty stanów to stałe typu wyliczeniowego bez własnych pól, a dane rezerwacji zostają w klasie `Reservation`. Kolejność „obciążenie karty, zmiana stanu, zapis efektu” pozostaje taka sama jak przed zmianą.

<p class="ref">Warsztat: m5/s08</p>

---

## 6.6. Polecenie: łańcuch warunków zamieniony na rejestr poleceń

```java
// przed: if (command.equals("SELL")) { ... } else if (command.equals("REFUND")) { ... }
ConsoleCommand command = commands.get(parts[0].toUpperCase(Locale.ROOT));
if (command == null) {
    return "Nieznana komenda: " + parts[0];
}
return command.execute(parts.length > 1 ? parts[1] : "", till);
```

- Metoda `CashierConsole.handle` była łańcuchem `if` sprawdzającym nazwę polecenia, z pełną logiką w każdej gałęzi. Każda gałąź staje się obiektem polecenia (wzorzec Polecenie, ang. Command), a wybór gałęzi zamienia się w wyszukanie w mapie.
- Mapa może zastąpić warunki tylko wtedy, gdy klucze się nie nakładają i zachowane zostaje ujednolicanie wielkości liter (`sell` działa jak `SELL`). Przy nakładających się warunkach lepiej sprawdza się Łańcuch zobowiązań.
- Sam wzorzec Polecenie nie zapewnia kolejki, ponawiania ani cofania. Cofanie wymaga osobnego mechanizmu, który odwraca skutki operacji.

<p class="ref">Warsztat: m5/s15</p>

---

## 6.7. Trzy osie w jednej klasie: każda dostaje własny mechanizm

- Pulpit kasjera obsługuje polecenia `BOOK`, `PAY`, `USE`, `CANCEL`, `UNDO` i `REPORT` jedną instrukcją `switch`. Wewnątrz jest kolejny `switch` po statusie rezerwacji, a w samej rezerwacji jeszcze jeden, po typie biletu.
- To trzy różne osie zmienności, choć kod wygląda jednakowo:
  - **cena biletu** zależy od typu biletu wybranego raz, przy sprzedaży → Strategia (`TicketPricing`),
  - **polecenia kasjera** trzeba zapisać w dzienniku i umieć cofnąć → Polecenie (`DeskCommand`),
  - **cykl życia rezerwacji** zmienia się w wyniku jej własnych operacji → Stan (`Booking`).
- Rozplątujemy po jednej osi. Potem nowy typ biletu, nowe polecenie i nowy status to zmiany w trzech niezależnych miejscach.

<p class="ref">Warsztat: m5/s26</p>

---

## 6.8. Typowe pomyłki przy wyborze

- **Strategia zamiast Stanu**, „bo to też `switch`”. Jeśli wariant zmienia się w trakcie życia obiektu na skutek jego własnych operacji, to jest stan.
- **Stan zamiast polimorfizmu**: rodzaj seansu (premiera, maraton) nigdy się nie zmienia, więc wystarczą podtypy.
- **Polecenie bez potrzeby**: gdy nie ma dziennika, kolejki ani cofania, wystarczy metoda albo mapa funkcji.
- **Polecenie, które pamięta dane poprzedniego żądania**, albo pole konkretnej rezerwacji w obiekcie stanu. Obiekty współdzielone nie mogą przechowywać danych pojedynczych żądań.
- **Hierarchia klas dla każdej kombinacji** (`PremiumPaidStudentTicket`) zamiast złożenia niezależnych osi.

---

## 7.1. Utworzenie Metody szablonowej: wspólny szkielet, różne kroki

```java
public abstract class SalesReport {
    public final String render(List<Sale> sales) {   // stała kolejność kroków
        // sortowanie, header(), row() dla każdej sprzedaży z sumą, footer()
    }
    protected abstract String header();
    protected abstract String row(Sale sale);
    protected abstract String footer(int tickets, Money total);
}
```

- Raporty CSV i HTML miały ten sam szkielet (sortowanie, nagłówek, wiersze, suma, stopka), ale w każdej klasie zapisany nieco inaczej.
- Kroki refaktoryzacji (ang. Form Template Method): wydziel różnice do metod `header`, `row` i `footer` → doprowadź `render` do identycznej postaci w obu klasach → przenieś ją do nadklasy jako metodę `final`. Metody nadpisywane przez podklasy nazywamy **punktami zaczepienia**.
- Metoda szablonowa (Template Method) sprawdza się, gdy kolejność kroków jest stała, a warianty różnią się tylko **jednym** rodzajem szczegółów.

<p class="ref">Warsztat: m5/s16</p>

---

## 7.2. Gdy punkty zaczepienia obejmują dwie niezależne osie

```java
public abstract class ScreeningReport {
    public final String render(LocalDate day, List<ScreeningSale> sales) {
        ... if (includes(day, sale.day())) ...                 // punkt zaczepienia: okres
        StringBuilder text = new StringBuilder(header(label(day)));
        ... text.append(row(title, tickets, revenue));        // punkt zaczepienia: format
        return text.append(footer(total.tickets(), total.revenue())).toString();
    }
}
// DailyCsvReport, DailyTextReport, WeeklyCsvReport, WeeklyTextReport
```

- Pięć punktów zaczepienia układa się w dwie osie: **okres** (`includes`, `label`) i **format** (`header`, `row`, `footer`).
- Dziedziczenie mnoży klasy: 2 okresy × 2 formaty = 4 podklasy, a kod okresów i formatów jest między nimi kopiowany. Trzeci format oznacza dwie kolejne podklasy.

---

## 7.3. Zastąpienie dziedziczenia delegacją: osie jako strategie

```java
public final class ScreeningReport {
    private final ReportPeriod period;     // includes(), label()
    private final ReportFormat format;     // header(), row(), footer()

    public String render(LocalDate day, List<ScreeningSale> sales) {
        ... if (period.includes(day, sale.day())) ...
        StringBuilder text = new StringBuilder(format.header(period.label(day)));
        ...
    }
}
// Reports.of("WEEKLY", "CSV") -> new ScreeningReport(new WeeklyPeriod(), new CsvFormat())
```

- Kroki refaktoryzacji (ang. Replace Inheritance with Delegation), po jednej osi: pogrupuj punkty zaczepienia → wyodrębnij interfejs dla grupy → przekaż obiekt pomocniczy w konstruktorze → przenieś implementacje → usuń podklasy po przełączeniu klientów.
- Przy składaniu obiektów liczby klas się dodają, a nie mnożą: N okresów + M formatów. Kolejności kroków nadal pilnuje jedna metoda, a `final` przechodzi z metody na klasę.

<p class="ref">Warsztat: m5/s27</p>

---

## 7.4. Strategia czy Metoda szablonowa?

| Kryterium | Metoda szablonowa | Strategia |
| --- | --- | --- |
| liczba osi zmienności | jedna | jedna lub kilka niezależnych |
| wymiana wariantu | przez wybór podklasy | przy tworzeniu obiektu, a nawet w trakcie działania |
| dostęp do stanu klasy bazowej | tak, przez pola `protected` | tylko przez parametry |
| typowe ryzyko | kruche punkty zaczepienia, mnożenie podklas | więcej obiektów i konfiguracji |
| test wariantu | podklasa testowa | wyrażenie lambda lub atrapa |

Metoda szablonowa dobrze się sprawdza, gdy szkielet jest stabilny, a zmienia się jeden krok. Gdy punkty zaczepienia obejmują dwie osie, przejdź na Strategię przez delegację.

---

## 8.1. Przeniesienie dodatków do Dekoratora

```java
// przed: flagi w rdzeniu i te same warunki w price() oraz description()
return title + " " + format + (vip ? " +VIP" : "") + (glasses ? " +okulary 3D" : "") + ...;

// po: każdy dodatek to obiekt o tym samym kontrakcie co bilet
public record Insurance(PricedTicket inner) implements PricedTicket {
    public Money price() { return inner.price().plus(Money.of("4.00")); }
    public String description() { return inner.description() + " +ubezpieczenie"; }
}
// TicketAssembler: if (order.vip()) ticket = new VipSeat(ticket); ...
```

- Każdy opcjonalny dodatek do biletu (miejsce VIP, okulary 3D, ubezpieczenie) przenosimy do osobnego obiektu, który ma **ten sam kontrakt** co bilet i przekazuje wywołania opakowanemu obiektowi (ang. Move Embellishment to Decorator).
- Kroki: wąski interfejs `PricedTicket` → pierwszy dekorator (ubezpieczenie) → kolejne dodatki → rdzeń bez flag.

<p class="ref">Warsztat: m5/s07</p>

---

## 8.2. Dekorator: na co uważać

- **Kolejność opakowywania jest częścią zachowania.** Opis biletu kończy się na „+ubezpieczenie”, bo ubezpieczenie jest warstwą zewnętrzną. Dlatego cały łańcuch składamy w jednym miejscu (`TicketAssembler`).
- Dekorator nie jest przezroczysty dla `instanceof`, `getClass()`, porównania tożsamości i `equals`. Kod, który sprawdzał, czy to bilet VIP, potrzebuje teraz osobnej metody.
- Błąd w samym dekoratorze (np. w dodatku, który zapisuje dziennik zdarzeń) może zasłonić błąd rdzenia. Trzeba jawnie ustalić, który wyjątek dostaje klient.
- Dekorator, który niczego nie dodaje, jest zbędną warstwą i należy go usunąć.

---

## 8.3. Łańcuch `if/else`, w którym kolejność jest regułą

```java
if (!request.requestedAt().isBefore(request.screeningStart())) {
    return RefundDecision.rejected("seans juz sie rozpoczal");
} else if (Duration.between(request.requestedAt(), request.screeningStart()).toHours() >= 24) {
    return RefundDecision.approvedBy("AUTO");
} else if (!request.amount().isGreaterThan(CASHIER_LIMIT)) {        // 100.00
    return RefundDecision.approvedBy("CASHIER");
} else if (!request.amount().isGreaterThan(MANAGER_LIMIT)) {        // 1000.00
    return RefundDecision.approvedBy("MANAGER");
}
return RefundDecision.rejected("brak uprawnionego zatwierdzajacego");
```

- Zwrot automatyczny nie ma limitu kwoty tylko dlatego, że jego gałąź stoi **przed** gałęzią kasjera. Kolejność gałęzi jest regułą biznesową ukrytą w układzie kodu.
- Uporządkowane reguły, z których zawsze działa dokładnie jedna, to sygnał, by sięgnąć po Łańcuch zobowiązań (Chain of Responsibility).

---

## 8.4. Łańcuch zobowiązań: ogniwo i złożenie łańcucha

```java
public final class CashierHandler implements RefundHandler {
    public RefundDecision handle(RefundRequest request) {
        if (!request.amount().isGreaterThan(limit)) return RefundDecision.approvedBy("CASHIER");
        return next.handle(request);                        // nie mój przypadek: przekaż dalej
    }
}
public RefundApprover() {                                   // kolejność widać w jednym miejscu
    this(new AfterScreeningHandler(new AutoRefundHandler(
         new CashierHandler(CASHIER_LIMIT, new ManagerHandler(MANAGER_LIMIT,
         new NoApproverHandler())))));
}
```

- Każde ogniwo samo decyduje: obsługuje żądanie i kończy albo przekazuje je następnemu ogniwu. Na końcu łańcucha stoi ogniwo, które zawsze zwraca decyzję (`NoApproverHandler`), zamiast `null`.
- Inną ścieżkę zatwierdzania (np. festiwal bez zwrotów automatycznych) można złożyć bez zmieniania ogniw. Zmiana kolejności ogniw zmienia wynik, dlatego test sprawdza wszystkie granice progów.

<p class="ref">Warsztat: m5/s28</p>

---

## 8.5. Łańcuch zobowiązań czy Dekorator?

| | Dekorator | Łańcuch zobowiązań |
| --- | --- | --- |
| ile elementów wykonuje pracę | wszystkie warstwy | zwykle jedno ogniwo |
| przekazanie dalej | zawsze | tylko gdy ogniwo nie obsłuży żądania |
| wynik | wynik rdzenia z wkładem każdego dodatku | decyzja pierwszego właściwego ogniwa |
| kolejność oznacza | zasięg dodatków | pierwszeństwo reguł |

Pytanie kontrolne: czy po obsłużeniu żądania przez ten element **ktoś dalej** musi jeszcze coś zrobić? Tak → Dekorator. Nie → Łańcuch zobowiązań. Zapisywanie w dzienniku decyzji o zwrocie to dekorator wokół łańcucha, a nie kolejne ogniwo.

---

## 9.1. Ujednolicenie interfejsów Adapterem

```java
public interface PaymentGateway {                      // język kina: Money, wynik zamiast wyjątku
    PaymentResult pay(String reservationId, Money amount);
}
public final class RestPayAdapter implements PaymentGateway {
    public PaymentResult pay(String reservationId, Money amount) {
        try {
            return PaymentResult.accepted(rest.charge(
                    new ChargeRequest(amount.amount(), "PLN", reservationId)).transactionId());
        } catch (RestPayException exception) {
            return PaymentResult.declined(exception.code());
        }
    }
}
```

- Metoda `CheckoutService.pay` mówiła dwoma językami: dla starej bramki składała XML z kwotą w groszach, a dla nowej wywoływała usługę REST z kwotą w złotych i przechwytywała wyjątek odmowy.
- Każda bramka dostaje adapter do jednego interfejsu, wygodnego dla kina (ang. Unify Interfaces with Adapter). Usługa zna tylko `PaymentGateway`, a trzeci dostawca to nowy adapter i jeden wpis w mapie.

<p class="ref">Warsztat: m5/s14</p>

---

## 9.2. Co adapter naprawdę musi przetłumaczyć

- Podobna sygnatura nie oznacza podobnego kontraktu. Adapter tłumaczy **jednostki** (grosze i złote), strefy czasowe, precyzję i kodowanie znaków.
- Tłumaczy też znaczenie `null` i pustej wartości oraz **sposób zgłaszania błędów**: wyjątek odmowy jednej bramki staje się zwykłym wynikiem „odmowa” (`declined`).
- Adapter nie powinien udawać, że oba systemy działają identycznie. Jeśli obca bramka nie chroni przed podwójnym wykonaniem tej samej operacji, adapter tego nie zapewni.
- Jeśli różnica sprowadza się do nazwy metody, wystarczy zmiana nazwy, a adapter byłby zbędną warstwą.

---

## 9.3. Surowe dane z zewnątrz w kodzie domeny

```java
String raw = terminal.authorize(bookingId, grosze);      // np. "OK;AUTH-R1;4500"
String[] parts = raw.split(";");
if (!parts[0].equals("OK")) {
    return parts[0] + " " + parts[1];
}
long authorized = Long.parseLong(parts[2]);
if (authorized < grosze) {
    return "PARTIAL " + parts[1] + " brakuje " + ...;
}
```

- `PaymentService` dostaje od terminala płatniczego tekst (status, kod, kwota w groszach) i w każdej metodzie osobno go rozbiera: `split`, `startsWith`, `lastIndexOf`.
- Zniekształcona odpowiedź powoduje awarię głęboko w domenie (`ArrayIndexOutOfBoundsException`, `NumberFormatException`) albo pusty kod autoryzacji na paragonie. Reguła płatności ginie wśród szczegółów formatu.

---

## 9.4. Obiekt graniczny: sprawdzone dane na granicy systemu

```java
public record PaymentConfirmation(Outcome outcome, String code, Money authorized) {
    public PaymentConfirmation {
        if (code == null || code.isBlank()) throw new IllegalArgumentException(...);
        if (outcome != Outcome.APPROVED && authorized.isGreaterThan(Money.ZERO))
            throw new IllegalArgumentException("only approved payment can authorize money");
    }
    public boolean isPartialFor(Money requested) {
        return approved() && authorized.compareTo(requested) < 0;
    }
}
```

- **Obiekt graniczny** (ang. Boundary Object) powstaje tylko z poprawnych danych: jeśli istnieje, to jest poprawny. Domena niczego już nie parsuje ani nie zgaduje, a format odpowiedzi terminala zna tylko konwerter `TerminalResponseMapper`.
- Dla poprawnych odpowiedzi wynik jest identyczny. Dla zniekształconych zmienia się **typ i moment błędu**: odpowiedź zostaje odrzucona na granicy, zamiast wywołać awarię trzy metody dalej. To świadoma decyzja, którą trzeba zatwierdzić.

<p class="ref">Warsztat: m5/s29</p>

---

## 9.5. Adapter czy obiekt graniczny?

| | Adapter | Obiekt graniczny |
| --- | --- | --- |
| co tłumaczy | wywołanie: nazwy, parametry, protokół | dane: format, typy, poprawność |
| na jakie pytanie odpowiada | jak wywołać obcy system? | co wpuszczamy do domeny? |
| typowy wynik | implementacja portu domenowego | niezmienny rekord, który pilnuje swoich reguł |
| ryzyko | pozorna zgodność działania | niepotrzebne kopiowanie każdej struktury danych |

Zwykle występują razem: adapter implementuje port (`PaymentTerminal`) i zwraca obiekt graniczny (`PaymentConfirmation`).

---

## 10.1. Ukrycie klas za Fabryką

```java
// przed: klient zna klasy konkretne i regułę wyboru
tickets.add(row >= 10 ? new VipTicket(title, base, row) : new StandardTicket(title, base, row));

// po: klient zna tylko Ticket i Tickets
public final class Tickets {
    public static Ticket forSeat(String title, Money base, int row) {
        return row >= 10 ? new VipTicket(title, base, row) : new StandardTicket(title, base, row);
    }
}
record VipTicket(String title, Money base, int row) implements Ticket { ... }   // bez public
```

- Fabryka staje się jedynym publicznym sposobem tworzenia biletów, a klasy konkretne tracą modyfikator `public` (ang. Encapsulate Classes with Factory). Nowy rodzaj biletu nie wymaga zmian u żadnego klienta.
- Kroki: przekierowuj tworzenie do fabryki po jednym miejscu → przenieś regułę wyboru → na końcu ogranicz widoczność klas.

<p class="ref">Warsztat: m5/s04</p>

---

## 10.2. Wyodrębnienie klasy fabryki

```java
public final class ReservationFactory {             // wie, JAK powstaje rezerwacja
    public Reservation create(String channel, String email, List<String> seats) {
        sequence++;                                 // numer pobierany przed sprawdzeniem kanału
        String id = "R" + sequence;
        if (!channel.equals("ONLINE") && !channel.equals("BOX_OFFICE")) throw ...;
        Money fee = channel.equals("ONLINE") ? Money.of("2.00").times(seats.size()) : Money.ZERO;
        ...
    }
}
public ReservationService(ReservationFactory factory) { ... }   // wie, KIEDY wolno ją utworzyć
```

- `ReservationService` pilnował zajętości miejsc, ale też budował rezerwacje (numer, opłata internetowa, termin ważności), i to w dwóch kopiach kodu. Tworzenie przenosimy najpierw do metody, potem do osobnej klasy (ang. Extract Factory Class), a fabrykę przekazujemy przez konstruktor.
- Kolejność efektów przy tworzeniu jest częścią kontraktu: przy błędnym kanale numer rezerwacji przepada. Refaktoryzacja to zachowuje; naprawa byłaby osobną zmianą.
- Różnica: ukrycie klas za fabryką chroni klasy konkretne przed klientami, a wyodrębnienie klasy fabryki zabiera tworzenie z klasy, która ma inną odpowiedzialność.

<p class="ref">Warsztat: m5/s05</p>

---

## 10.3. Fabryka jako strażnik spójności

- Fabryka skupia w jednym miejscu wiedzę o tworzeniu obiektów: sprawdzanie danych, wybór klasy, współdzielone instancje, odczyt konfiguracji. Klient nie może utworzyć niespójnego obiektu z pominięciem tych reguł.
- Zanim ukryjesz konstruktory, znajdź miejsca tworzenia niewidoczne dla kompilatora: refleksję, kontener wstrzykiwania zależności, odczyt obiektów z plików i sieci. W bibliotece publicznej potrzebny jest okres przejściowy z adnotacją `@Deprecated`.
- Fabryka jest zwykłą zależnością przekazywaną przez konstruktor. Nie powinna zamienić się w globalny rejestr usług.
- Fabryka to nie to samo co korzeń kompozycji: fabryka tworzy obiekty na żądanie w trakcie działania programu, a korzeń kompozycji składa graf obiektów raz, przy starcie.

---

## 11.1. Klienci sami koordynują podsystemy

```java
// BoxOffice.sell: kopia OnlineShop.buy sprzed lat
Money total = prices.price(order.ticketType()).times(order.seats().size());
try {
    seats.reserve(order.screening(), order.seats());
} catch (IllegalStateException exception) {
    return "ERROR " + exception.getMessage();
}
if (!payments.charge(order.card(), total)) {
    return "ERROR payment declined";         // sklep w tym miejscu zwalnia miejsca
}
mailer.send(order.email(), ...);
```

- Sklep internetowy i kasa znają cztery podsystemy (cennik, rezerwację miejsc, płatności, maile), kolejność ich wywołań i obsługę błędów.
- Ta sama wiedza została skopiowana, a kopie już się rozjechały: kasa po odmowie płatności nie zwalnia miejsc.

---

## 11.2. Wyodrębnienie Fasady: jedna operacja zamiast znajomości kroków

```java
public final class BookingFacade {
    public String book(TicketOrder order, Money feePerSeat) {
        Money total = prices.price(order.ticketType()).plus(feePerSeat).times(...);
        seats.reserve(order.screening(), order.seats());   // (obsługa błędu pominięta)
        boolean paid = false;
        try {
            paid = payments.charge(order.card(), total);
        } finally {
            if (!paid) seats.release(order.screening(), order.seats());
        }
        ...
    }
}
```

- Kroki (ang. Extract Facade): test efektów obu klientów → wyodrębnienie metody w sklepie → przeniesienie jej do fasady → przełączenie kasy na fasadę → ukrycie podsystemów. Fasada obejmuje jedną spójną operację; fasada udostępniająca wszystkie operacje podsystemów staje się klasą, która wie i robi wszystko.
- Przełączenie kasy na fasadę zmienia jej zachowanie, bo naprawia błąd w kopii. To nie jest „czysta” refaktoryzacja, więc dokumentujemy tę zmianę osobnym testem.

<p class="ref">Warsztat: m5/s30</p>

---

## 11.3. Obcy model w regułach domeny

```java
if (!record.statusCode().equals("A")) {
    return "LICENCJA ZAWIESZONA " + filmCode;
}
int percent = Integer.parseInt(record.share().replace("%", "").strip());   // "45%", " 40 %"
Money minimum = new Money(new BigDecimal(record.minGuaranteeCents()).movePointLeft(2));
Money due = revenue.percent(percent).max(minimum);
String title = record.titleUpper().charAt(0)
        + record.titleUpper().substring(1).toLowerCase(Locale.ROOT);
```

- `SettlementService` rozlicza się z dystrybutorem, posługując się jego modelem danych: statusy `A`/`S`, daty w formacie `yyyyMMdd`, kwoty w groszach zapisane jako tekst, udział `"45%"` i tytuły pisane wielkimi literami.
- Każda zmiana formatu po stronie dystrybutora przenika do reguł rozliczeń, a reguły można przetestować tylko na rekordach obcego systemu.

---

## 11.4. Warstwa antykorupcyjna: port, tłumacz i model domenowy

```java
public record FilmLicense(String filmCode, String title, boolean active, LocalDate validFrom,
                          LocalDate validTo, int sharePercent, Money minimumGuarantee) {
    public boolean allowsScreeningOn(LocalDate day) { return active && covers(day); }
    public Money dueFor(Money revenue) { return revenue.percent(sharePercent).max(minimumGuarantee); }
}
public final class DistributorAcl implements FilmLicenses {          // port w języku kina
    public Optional<FilmLicense> find(String filmCode) {
        return Optional.ofNullable(feed.fetch(filmCode)).map(translator::toLicense);
    }
}
```

- **Warstwa antykorupcyjna** (ang. Anti-Corruption Layer, ACL) chroni język domeny przed obcym modelem. Reguły (czy licencja obejmuje dany dzień, ile należy zapłacić) są metodami modelu domenowego i można je testować bez typów dystrybutora. Konwencje dystrybutora zna tylko tłumacz `DistributorTranslator`.
- Tłumacz zaostrza sprawdzanie danych: błędna data albo udział spoza zakresu 0-100 powodują błąd już na granicy. To, co zrobić z błędnym rekordem (odrzucić, pominąć, odłożyć do wyjaśnienia), jest decyzją biznesową.

<p class="ref">Warsztat: m5/s31</p>

---

## 11.5. Fasada a warstwa antykorupcyjna

| | Fasada | Warstwa antykorupcyjna |
| --- | --- | --- |
| cel | prostszy dostęp do podsystemów | ochrona języka domeny przed obcym modelem |
| modele po obu stronach | jeden model, mniej wywołań | dwa różne modele, potrzebne tłumaczenie |
| zawartość | koordynacja kroków | port, adapter, tłumacz, często też fasada |
| co testujemy | kolejność i efekty | tłumaczenie oraz reguły bez obcych typów |

- Fasada **upraszcza**, a warstwa antykorupcyjna **tłumaczy**. Taka warstwa tam, gdzie modele się nie różnią, to tylko kosztowny pośrednik.
- Warstwa antykorupcyjna dobrze sprawdza się przy stopniowym zastępowaniu starego systemu nowym, funkcja po funkcji (ang. Strangler Fig). Port domenowy pozostaje bez zmian, a za nim można zastąpić starego dostawcę nowym. Każdy element przejściowy potrzebuje właściciela i warunku, po którym zostanie usunięty.

---

## 12.1. Ukrycie budowy Kompozytu za Budowniczym

```java
// przed: ręczne new i add, węzły można zmieniać
Hall hall2 = new Hall("Sala 2");
if (weekend) hall2.add(new Screening("Kraina Lodu", LocalTime.of(10, 0)));
hall2.add(new Screening("Amator", LocalTime.of(17, 30)));
day.add(hall2);

// po: wcięcia kodu odpowiadają poziomom drzewa
return ScheduleBuilder.day(date)
        .hall("Sala 1", hall -> hall.screening("Diuna", 18, 0).screening("Diuna", 21, 0))
        .hall("Sala 2", hall -> { ... })
        .build();
```

- Budowniczy (ang. Builder) ukrywa budowę drzewa (dzień → sala → seans) za interfejsem, w którym opisujemy, **co** ma powstać, a nie **jak** to zbudować (ang. Encapsulate Composite with Builder). Gotowego drzewa nie można już zmienić.

<p class="ref">Warsztat: m5/s06</p>

---

## 12.2. Budowniczy: kiedy i na co uważać

- Budowniczy jest uzasadniony, gdy budowa jest wieloetapowa i ma **własne reguły**, np. kolejność elementów, zasady dla pustej grupy, unikalność nazw. Dla rekordu z trzema wymaganymi polami konstruktor jest czytelniejszy.
- Moment sprawdzania danych jest częścią kontraktu: sprawdzanie w każdym kroku szybko zgłasza błąd, a sprawdzanie w `build()` widzi całość. Od tego wyboru zależy, kiedy pojawi się wyjątek.
- Budowniczy jest jednorazowy: każde wywołanie po `build()` rzuca wyjątek, a gotowy obiekt nie współdzieli wewnętrznej listy z budowniczym.
- Wymóg unikalnych nazw sal to nowa reguła, której wcześniej nie było. Dla błędnych danych zachowanie się więc zmienia i trzeba to zatwierdzić.

---

## 13.1. Zastąpienie zakodowanych powiadomień Obserwatorem

```java
// przed: usługa płatności zna wszystkich odbiorców
mailer.send(payment.email(), "Potwierdzenie platnosci " + ...);
sms.send(payment.phone(), "Oplacono " + payment.reservationId());
loyalty.addPoints(payment.email(), payment.amount().amount().intValue() / 10);

// po: usługa publikuje zdarzenie, odbiorców rejestruje korzeń kompozycji
for (Registration registration : listeners) {
    registration.listener().onPaid(event);
}
service.subscribe(new MailConfirmation(mailer));
service.subscribe(new SmsConfirmation(sms));
```

- Obserwator (ang. Observer) odwraca zależność: nadawca zna tylko interfejs odbiorcy (`PaymentListener`), a konkretnych odbiorców rejestruje się z zewnątrz (ang. Replace Hard-coded Notifications with Observer).
- `PaymentService` nie importuje już klas `Mailer`, `SmsGateway` ani `LoyaltyProgram`.

<p class="ref">Warsztat: m5/s09</p>

---

## 13.2. Obserwator: zasady powiadamiania muszą być jawne

- **Kolejność i wątek:** odbiorcy są wywoływani w kolejności rejestracji, w tym samym wątku, tak samo jak wcześniej wywołania zapisane na sztywno.
- **Błędy:** awaria wysyłki SMS przerywa naliczanie punktów, dokładnie tak jak przed zmianą. Kontynuowanie pracy po błędzie odbiorcy byłoby nową decyzją, a nie refaktoryzacją.
- **Wyrejestrowanie:** `subscribe` zwraca uchwyt, który usuwa dokładnie tę jedną rejestrację, a ponowne użycie uchwytu niczego nie psuje. Odbiorca, którego nikt nie wyrejestruje, może niepotrzebnie zatrzymywać obiekty w pamięci.
- Dodanie nowych odbiorców to **rozszerzenie** zachowania. Pierwszy krok zachowuje dokładnie te same efekty co wcześniej.

---

## 14.1. Komponenty połączone każdy z każdym

```java
// SeatMap.click: mapa miejsc zna trzy inne komponenty i kolejność ich odświeżania
if (selected.remove(seat)) {
    basket.drop(seat);
} else {
    selected.add(seat);
    basket.add(seat);
}
summary.recalculate(basket.seats());
payButton.refresh(basket.seats().size(), summary.total());
```

- Ekran wyboru miejsc ma cztery komponenty: mapę miejsc, koszyk, podsumowanie ceny i przycisk płatności. Łączy je 11 referencji.
- Po akcji użytkownika każdy komponent sam odświeża pozostałe, a kolejność „najpierw suma, potem przycisk” jest powtórzona w czterech miejscach. Nowa reguła ekranu wymaga zmian w kilku klasach.

---

## 14.2. Wprowadzenie Mediatora: reguły współpracy w jednym miejscu

```java
public final class SeatSelectionScreen implements SelectionMediator {
    private final SeatMap seatMap = new SeatMap(this);
    private final Basket basket = new Basket(this);
    ...
    public void seatToggled(String seat, boolean selected) {
        if (selected) basket.add(seat); else basket.drop(seat);
        refresh();
    }
    private void refresh() {                        // kolejność odświeżania w jednym miejscu
        summary.recalculate(basket.seats());
        payButton.refresh(basket.seats().size(), summary.total());
    }
}
```

- Komponent zgłasza, **co się stało**, a mediator decyduje, kto i w jakiej kolejności reaguje. Komponenty znają tylko interfejs mediatora.
- Ryzykiem jest mediator, który przejmuje całą logikę ekranu i staje się nową, ogromną klasą. Do mediatora trafia tylko koordynacja, a logika komponentów (cena, limit miejsc) pozostaje w komponentach.

<p class="ref">Warsztat: m5/s32</p>

---

## 14.3. Obserwator czy Mediator?

| | Obserwator | Mediator |
| --- | --- | --- |
| kierunek komunikacji | nadawca → wielu niezależnych odbiorców | komponenty ↔ jeden koordynator |
| gdzie są reguły współpracy | rozproszone w odbiorcach | skupione w mediatorze |
| kto kogo zna | odbiorcy nie znają się nawzajem | mediator zna wszystkich uczestników |
| typowe ryzyko | ukryte zależności, kolejność powiadomień, wycieki pamięci | mediator, który robi wszystko |
| pasuje do | zdarzeń domenowych, powiadomień | formularzy, ekranów, koordynacji |

Mediator często korzysta z Obserwatora do przekazywania powiadomień. Różnica polega na tym, **gdzie znajdują się reguły** współpracy.

---

## 15.1. Odwiedzający i macierz zmian

```java
// przed: łańcuch instanceof w każdej operacji, błąd dopiero w czasie działania
if (item instanceof TicketItem ticket) return vatOf(ticket.price(), 8);
else if (item instanceof SnackItem snack) return vatOf(snack.price(), 23);
else if (item instanceof VoucherItem) return Money.ZERO;
throw new IllegalArgumentException("unknown item: " + item);
```

| Co zmienia się częściej | Naturalny kierunek |
| --- | --- |
| nowe operacje, stały zestaw rodzajów elementów | Odwiedzający |
| nowe rodzaje elementów, stały zestaw operacji | metody polimorficzne w elementach |
| jedno proste zbieranie wyników | parametr zbierający |

- `ReceiptPrinter` (paragon z biletami, przekąskami i bonami) ma trzy takie operacje: wiersz paragonu, kwotę i VAT. Odwiedzający (ang. Visitor) przenosi każdą z nich do osobnej klasy, a element sam wybiera właściwą metodę w `accept(visitor)`. Dodanie operacji jest tanie, ale nowy rodzaj elementu wymaga zmian we wszystkich klasach odwiedzających.

---

## 15.2. Odwiedzający albo hierarchia `sealed` i `switch`

```java
public sealed interface OrderItem permits TicketItem, SnackItem, VoucherItem { }

Money vat(OrderItem item) {
    return switch (item) {                         // bez default: kompilator pilnuje kompletności
        case TicketItem ticket -> vatOf(ticket.price(), 8);
        case SnackItem snack -> vatOf(snack.price(), 23);
        case VoucherItem voucher -> Money.ZERO;
    };
}
```

- We współczesnej Javie `switch` obejmujący wszystkie podtypy zamkniętej (`sealed`) hierarchii daje tę samą kontrolę kompilatora co Odwiedzający, bez podwójnego wywołania metod.
- Nieobsłużona pozycja oznacza błąd kompilacji, a nie awarię przy kasie. Klasyczny Odwiedzający ma sens, gdy hierarchii nie da się zamknąć.

<p class="ref">Warsztat: m5/s19</p>

---

## 16.1. Zastąpienie instrukcji warunkowej polimorfizmem

```java
// przed: pole value oznacza raz długość filmu w minutach, a raz liczbę filmów
return switch (kind) {
    case REGULAR -> 20 + value;
    case PREMIERE -> 30 + value;
    case MARATHON -> value * 120 + (value - 1) * 15;
};
// po: zamknięta hierarchia rekordów z nazwanymi danymi
public record MarathonScreening(String title, int films) implements Screening {
    public int durationMinutes() { return films * 120 + (films - 1) * 15; }
}
```

- Ta refaktoryzacja (ang. Replace Conditional with Polymorphism) pasuje, gdy pole sterujące opisuje **trwały rodzaj** obiektu. Jeśli rodzaj zmienia się w czasie, właściwy jest Stan; jeśli algorytm wybiera klient, Strategia.
- Zaczynamy od znalezienia wszystkich miejsc, w których obiekty są tworzone i odczytywane z zapisu. Zostaje jeden `switch` w `fromRow`, czyli tam, gdzie jest miejsce na wiedzę o tworzeniu obiektów.

<p class="ref">Warsztat: m5/s02</p>

---

## 16.2. Zastąpienie kodu typu klasą

```java
public enum Format {                              // przed: int 1, 2, 3 w trzech instrukcjach switch
    TWO_D("2D", "25.00", false), THREE_D("3D", "32.00", true), IMAX("IMAX", "40.00", false);
    public String label() { ... }
    public Money basePrice() { ... }
    public boolean requiresGlasses() { ... }
}
Format format = FormatCodes.fromCode(Integer.parseInt(parts[1].trim()));   // tuż po odczycie
```

- Format seansu był zwykłą liczbą `int` z pliku CSV, a wiedza o kodach była rozproszona po trzech metodach (etykieta, cena, okulary). Nowy typ (ang. Replace Type Code with Class) przejmuje całą tę wiedzę.
- W zapisie do pliku zostaje stabilny kod liczbowy, który przelicza konwerter `FormatCodes`, a nigdy `ordinal()` ani `toString()`. Format pliku CSV się nie zmienia.
- Typ wyliczeniowy wystarcza dla małego, zamkniętego zbioru wartości. Klasa jest lepsza, gdy pojawiają się nazwy zastępcze, kody z innych systemów albo własne reguły tworzenia.

<p class="ref">Warsztat: m5/s03</p>

---

## 16.3. Zastąpienie ukrytego drzewa Kompozytem

```java
// przed: drzewo ukryte w konwencji, pierwszy element listy to nazwa zestawu
List.of("Zestaw Duo", "Popcorn L=18.00",
        List.of("Napoje", "Cola 0.5=9.00", "Cola 0.5=9.00"), "Nachos=14.00")

// po: jawny liść (Product) i węzeł (Combo) o wspólnym kontrakcie
public sealed interface MenuItem permits Product, Combo {
    Money price();
    void render(int depth, StringBuilder text);
}
public record Combo(String name, List<MenuItem> items) implements MenuItem {
    public Money price() { /* suma price() wszystkich items */ }
}
// stary format tłumaczy konwerter: MenuMapper.fromNested(combo).render(0, text)
```

- Klasa `BarMenu` wielokrotnie używała `instanceof` i rzutowań, żeby odczytać zagnieżdżone listy. Obok budujemy jawny Kompozyt (ang. Composite: `Product`, `Combo`) i konwerter, który tłumaczy stary format (ang. Replace Implicit Tree with Composite).
- Operacje (`price`, `render`) przenosimy po jednej, a stara i nowa postać danych istnieją obok siebie. Konwerter zgłasza błąd dla danych, których nie da się wiernie odwzorować.

<p class="ref">Warsztat: m5/s10</p>

---

## 16.4. Kompozyt przezroczysty czy bezpieczny

```java
// Kompozyt przezroczysty: add() jest we wspólnym MenuComponent, więc liść musi go „obsłużyć”
public void add(MenuComponent child) {                    // w klasie Product
    throw new UnsupportedOperationException("cannot add to " + name());
}
```

- W wariancie **przezroczystym** metoda `add()` jest we wspólnym interfejsie. Wywołanie `add` na produkcie się kompiluje, ale kończy się błędem dopiero w czasie działania.
- W wariancie **bezpiecznym** zarządzanie elementami podrzędnymi jest tylko w węźle (`Combo`). Błąd wychodzi już przy kompilacji, ale klient musi wiedzieć, czy ma do czynienia z węzłem.
- Gdy drzewo powstaje raz i potem się nie zmienia, dylemat znika: niezmienne rekordy w ogóle nie potrzebują metody `add`.
- Rzutowanie `(Combo)` w kodzie klienta oznacza powrót do problemu, który Kompozyt miał rozwiązać.

<p class="ref">Warsztat: m5/s11</p>

---

## 16.5. Jeden element lub wiele oraz wyodrębnienie Kompozytu

- **Zastąpienie rozróżnienia „jeden lub wiele” Kompozytem** (ang. Replace One/Many Distinctions with Composite): zamiast metod `refund(ticket)` i `refundAll(tickets)`, w których ta sama reguła jest zapisana dwa razy, powstaje jedna metoda `refund(Refundable, now)`. `Refundable` ma dwie implementacje: pojedynczy bilet (`SingleTicket`) i grupę (`TicketGroup`), która sama też jest `Refundable`.
- Przy tej zmianie nie wolno zmienić kolejności przetwarzania, wyniku dla pustej grupy ani liczby efektów naliczanych raz na wywołanie. Potrącenie 3.00 zł jest nadal naliczane raz na cały zwrot, a nie za każdy bilet.
- **Wyodrębnienie Kompozytu** (ang. Extract Composite): klasy `Marathon` i `ShortsBlock` powielają listę elementów, metodę `add` i sumowanie minut. Wspólne części przenosimy do nadklasy `CompositeProgramItem`.
- Podobne pętle mogą znaczyć co innego: tylko maraton dolicza przerwy, więc `minutes()` zostaje w podklasach. Porównujemy kontrakty, a nie wygląd kodu.

<p class="ref">Warsztat: m5/s12, m5/s13</p>

---

## 16.6. Przeniesienie zbierania wyników do parametru zbierającego

```java
// przed: każda metoda zwraca fragment tekstu z własnym separatorem
warnings += checkEmail(draft.email());
warnings += checkSeats(draft.seats());
return warnings.isEmpty() ? "OK" : warnings.substring(0, warnings.length() - 2);

// po: metody dopisują ostrzeżenia do przekazanego obiektu Warnings
Warnings warnings = new Warnings();
checkEmail(draft.email(), warnings);
checkSeats(draft.seats(), warnings);
return warnings.summary();
```

- Metody nie zwracają już fragmentów do sklejenia, tylko dopisują wyniki do przekazanego obiektu (ang. Move Accumulation to Collecting Parameter). Separator i format wyniku są teraz w jednym miejscu.
- Właścicielem obiektu zbierającego wyniki jest kod wywołujący. Trzeba ustalić, czy metoda go czyści, czy tylko dopisuje, i co w nim zostaje, gdy w połowie pracy zostanie zgłoszony wyjątek.

<p class="ref">Warsztat: m5/s18</p>

---

## 16.7. Ograniczenie liczby instancji Singletonem

```java
// przed: new PriceList().basePrice(format), czyli parsowanie taryfy przy każdej wycenie
public enum PriceList implements Tariff { INSTANCE; ... }     // jedna instancja
public TicketDesk() { this(PriceList.INSTANCE); }             // domyślnie globalna instancja
public TicketDesk(Tariff tariff) { ... }                       // test podaje własny cennik
```

- `TicketDesk` przy każdej wycenie tworzył nowy obiekt `PriceList` i od nowa parsował taryfę, choć cennik się nie zmienia.
- Ograniczenie liczby instancji do jednej (ang. Limit Instantiation with Singleton) to **decyzja o cyklu życia** obiektu. Jest bezpieczne tylko wtedy, gdy obiekt jest niezmienny, a jego instancje są sobie równoważne. Najpierw mierzymy, ile kosztuje tworzenie.
- Typ wyliczeniowy gwarantuje bezpieczne udostępnienie instancji wielu wątkom, ale oznacza jedną instancję na `ClassLoader`, a nie na całą JVM. Na końcu ukrywamy cennik za interfejsem `Tariff`, żeby testy nie zależały od stanu globalnego.

<p class="ref">Warsztat: m5/s17</p>

---

<!-- _class: part -->
# Część VI. Usuwanie wzorców i podsumowanie

Kiedy i jak usunąć wzorzec, który przestał pomagać

---

## 17.1. Kiedy wzorzec jest nadmiarem

**Usunięcie** wzorca jest tak samo uprawnioną refaktoryzacją jak jego **wprowadzenie**. Typowe sygnały, że wzorzec jest zbędny:

- interfejs, który od dawna ma jedną implementację i powtarza metody jednej klasy,
- fabryka, która wywołuje tylko jeden konstruktor, albo konfiguracja trudniejsza niż zwykłe `new`,
- implementacje, których metody w większości rzucają `UnsupportedOperationException`,
- nowy wariant nadal wymaga zmian w wielu centralnych miejscach, więc wzorzec niczego nie uprościł,
- pośrednik, który tylko przekazuje wywołania,
- globalna instancja (Singleton), która ukrywa zależność i sprawia, że testy wpływają na siebie nawzajem.

---

## 17.2. Refaktoryzacje, które usuwają wzorzec

| Nadmiar | Refaktoryzacja (nazwa angielska) |
| --- | --- |
| pośrednik przekazujący wywołania 1:1 | usunięcie pośrednika (Remove Middle Man) |
| globalny, modyfikowalny Singleton | wchłonięcie Singletona (Inline Singleton) |
| podklasa bez własnego stanu i zachowania | spłaszczenie hierarchii (Collapse Hierarchy) |
| Strategia z jedną, stałą implementacją | wchłonięcie klasy (Inline Class) |
| fabryka wywołująca jeden konstruktor | wchłonięcie metody (Inline Method), zwykłe `new` |
| Dekorator, który niczego nie dodaje | usunięcie warstwy |
| Odwiedzający, gdy często dochodzą nowe rodzaje elementów | metody polimorficzne albo `switch` po `sealed` |

Usunięcie wzorca to także refaktoryzacja: obowiązuje ten sam test równoważności i te same małe kroki.

---

## 17.3. Usunięcie pośrednika

```java
public int freeSeats(String id) {          // CinemaFacade: prawie wszystko deleguje 1:1...
    try {
        return catalog.freeSeats(id);
    } catch (NoSuchElementException e) {
        return 0;                          // ...ale tu po cichu zamienia błąd na wynik
    }
}
```

- Zanim usuniesz pośrednika (ang. Remove Middle Man), sprawdź, czy nie robi czegoś więcej: kontroli uprawnień, transakcji, pomiarów, ponawiania, tłumaczenia błędów. Fasada modułu albo szew dla testów bywają cenne.
- Tutaj fasada dla nieznanego seansu zwraca 0 wolnych miejsc. To zachowanie przenosimy do jedynego klienta, który go potrzebuje (`SeatBadge`), potem po kolei przełączamy klientów na katalog i usuwamy fasadę.
- Dziwne zachowanie jest teraz widoczne i czeka na osobną decyzję.

<p class="ref">Warsztat: m5/s22</p>

---

## 17.4. Wchłonięcie Singletona

```java
// przed: ukryta zależność od stanu globalnego
Money price = PriceList.getInstance().basePrice(format);

// po: jawna zależność; o „jednej instancji” decyduje korzeń kompozycji
public TicketDesk(Tariff tariff) { this.tariff = Objects.requireNonNull(tariff, "tariff"); }
```

- Kroki (ang. Inline Singleton): parametr konstruktora (na początku z domyślną wartością `getInstance()`) → przełączanie klientów po jednym → tworzenie instancji w korzeniu kompozycji → usunięcie `getInstance()`.
- Jeśli test musi zerować singleton między przypadkami, to znak, że stan globalny uzależnia testy od siebie.
- Jeśli singleton był modyfikowalny, a staje się niezmienną wartością, to jest to **zmiana kontraktu**: przełączanie ustawień w trakcie działania przestaje działać. Ustal, czy ktoś z tego korzystał.

<p class="ref">Warsztat: m5/s17 (krok 3: przekazanie zależności zamiast dostępu globalnego)</p>

---

## 17.5. Wzorzec jako decyzja odwracalna

- Rozliczenie z dystrybutorem miało dwa modele: procentowy oraz festiwalowy, pobierany z obcego systemu w groszach. Strategia z Adapterem były wtedy uzasadnione, bo istniały dwa rzeczywiste warianty i rzeczywista potrzeba tłumaczenia.
- Gdy umowy festiwalowe wygasły, wzorzec stracił powód istnienia. Usuwamy wariant, adapter i interfejs. Zostaje prosta metoda, którą w razie potrzeby można rozbudować z powrotem tymi samymi krokami.
- Usunięcie wariantu zmienia zachowanie (umowy `FESTIVAL` są teraz odrzucane), więc jest to osobny, wyraźnie nazwany krok.
- Wniosek: wzorzec nie jest inwestycją na zawsze. Zostaje w kodzie tak długo, jak długo istnieją powody, które go uzasadniają.

<p class="ref">Warsztat: m5/s21</p>

---

## 18.1. Mapa decyzji (1/2)

| Sytuacja w kodzie | Rozważ | Uważaj na |
| --- | --- | --- |
| wymienny algorytm, wybierany raz | Strategię | moment wyboru, stan strategii |
| żądanie, które trzeba zapisać, ustawić w kolejce lub cofnąć | Polecenie | kompletność rejestru, odwracanie skutków |
| zachowanie zależne od stanu i przejść | Stan | tabelę przejść, kolejność efektów |
| trwały rodzaj obiektu | polimorfizm podtypów | tworzenie obiektów, zapis i odczyt |
| stała sekwencja, jeden zmienny krok | Metodę szablonową | kruche punkty zaczepienia |
| kilka niezależnych osi w punktach zaczepienia | Strategię przez delegację | utratę typu podklasy |
| uporządkowane reguły, z których działa jedna | Łańcuch zobowiązań | zależność od kolejności |
| opcjonalny dodatek wokół rdzenia | Dekorator | kolejność, tożsamość, wyjątki |

---

## 18.2. Mapa decyzji (2/2)

| Sytuacja w kodzie | Rozważ | Uważaj na |
| --- | --- | --- |
| niezgodny interfejs zewnętrzny | Adapter | pozorną zgodność działania |
| surowe dane z zewnątrz w kodzie domeny | obiekt graniczny | typ i moment błędu |
| klienci sami koordynują podsystemy | Fasadę | klasę, która robi wszystko; brak transakcji |
| obcy model w regułach domeny | warstwę antykorupcyjną | koszt drugiego modelu |
| rozproszona wiedza o tworzeniu obiektów | Fabrykę | globalny rejestr |
| skomplikowana budowa drzewa | Budowniczego | moment sprawdzania danych |
| zmienny zestaw odbiorców zdarzenia | Obserwatora | kolejność, błędy odbiorców |
| komponenty znające się nawzajem | Mediatora | mediator, który robi wszystko |
| wiele operacji na stabilnych typach | Odwiedzającego albo `sealed` i `switch` | koszt dodania nowego rodzaju |
| pośrednik 1:1, stan globalny | usunięcie pośrednika, wchłonięcie Singletona | zadania, które pośrednik po cichu wykonywał |

---

## 18.3. Lista kontrolna

- **Decyzja:** problem opisany bez nazwy wzorca; warianty istnieją już dziś; rozważono prostszą alternatywę, w tym usunięcie istniejącego wzorca.
- **Kontrakt:** testy obejmują każdą gałąź, `null`, wartość nieznaną i granice progów; zachowane są wyjątki, stan po błędzie i kolejność efektów.
- **Kroki:** jeden kontrakt naraz, automatyczne refaktoryzacje IDE wszędzie, gdzie to możliwe; użycia niewidoczne dla IDE (refleksja, konfiguracja) sprawdzone ręcznie.
- **Granice:** obcy model i surowe dane nie przechodzą do domeny; zmiana typu lub momentu błędu jest zatwierdzona osobno.
- **Na koniec:** stary warunek i elementy przejściowe usunięte; każda świadoma zmiana zachowania ma własny test i osobne zatwierdzenie.

---

## 18.4. Najważniejsze wnioski

- Refaktoryzacja do wzorca zaczyna się od **kontraktu i osi zmienności**, a nie od diagramu klas.
- Bez siatki bezpieczeństwa nie ma refaktoryzacji: test charakteryzujący i test równoważności powstają przed pierwszym ruchem.
- Podobne do siebie wzorce rozróżniamy dzięki prostym pytaniom: kto wybiera wariant i kiedy, ile elementów obsługuje żądanie, który model chronimy.
- Wzorce na granicach (Adapter, obiekt graniczny, Fasada, warstwa antykorupcyjna) przesuwają wykrywanie błędów bliżej ich źródła. To świadoma zmiana kontraktu, a nie skutek uboczny.
- Usuwanie zbędnych wzorców jest równie ważne jak ich wprowadzanie. Wzorzec zostaje tylko wtedy, gdy rozwiązuje **rzeczywisty** problem.
