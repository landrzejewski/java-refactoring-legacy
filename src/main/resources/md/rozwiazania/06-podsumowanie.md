# Moduł 6. Podsumowanie - rozwiązania dla prowadzącego

Numeracja i nazwy etapów są zgodne z plikiem zadań. Ścieżki są względne względem katalogu repozytorium.

Ćwiczenie końcowe nie ma jednego poprawnego wyniku. Poniżej jest zarys rozwiązania referencyjnego: oczekiwana diagnoza, znane luki golden mastera (potwierdzone kontrolnymi mutacjami), trzy przykładowe karty planu i szczegółowy przebieg jednej refaktoryzacji. Oceniamy przede wszystkim dyscyplinę przepływu pracy: fakty zamiast ocen, sieć przed zmianą, małe kroki, oddzielenie zmian zachowania.

---

## Ćwiczenie końcowe: CineLegacy pod golden masterem

### Organizacja

- Przed zajęciami: `mvn -q test -Dtest=CinemaManagerGoldenMasterTest` musi być zielony na czystym repozytorium.
- Przypomnij, że ostrzeżenie w nagłówku `CinemaManager` dotyczy głównej linii repozytorium - zespoły pracują na gałęziach `final/<zespół>` i porzucają je po zajęciach.
- Wydziel czas twardo (zegar na ekranie). Najczęstszy problem: zespoły spędzają 40 minut na diagnozie i nie dochodzą do etapu 4.
- Jeśli czasu jest mało, przydziel zespołom różne refaktoryzacje (A, B, C poniżej) i zrób wspólne omówienie.

### Etap 1: oczekiwana diagnoza

| Zapach | Miejsce | Dowód | Kandydat | Ryzyko |
| --- | --- | --- | --- | --- |
| God Class | cała klasa | 9 odpowiedzialności: repertuar, rezerwacje, cennik, płatności, zwroty, lojalność, powiadomienia, raporty, rozliczenia | kampania: Extract Method, Extract Class | big bang, konflikty w zespole |
| Primitive Obsession (tablice) | `LegacyDb.SCREENINGS`, `BOOKINGS` | `Object[]` z indeksami `s[1]`...`s[6]`, `b[0]`...`b[10]` i rzutowaniami | Replace Array with Object (rekordy `Screening`, `Booking`) | szeroki zasięg, format "bazy" |
| kody typu | komentarz w nagłówku, `book`, `pay` | format `1/2/3`, status `0-4`, typ biletu `N/S/E/C` | Replace Type Code with Class (enumy z kodem) | nieznany kod, format trwały |
| Long Method, Arrowhead | `book` | trzy poziomy `if` z `else` na końcu, wycena w pętli | Extract Method, Return ASAP (guard clauses) | kolejność walidacji |
| warunki po statusie | `pay`, `cancel`, `use`, `expireOld` | `if (status == ...)` w każdej operacji | Replace State-Altering Conditionals with State | kolejność efektów w `cancel` |
| sztywne powiadomienia | `book`, `pay`, `cancel`, `expireOld` | statyczne `LegacyMailer.send` i `sms` w logice | Extract Method, port powiadomień, ewentualnie Observer | kolejność wiadomości |
| ukryta zależność | `pay`, `cancel` | statyczne `LegacyPaymentGateway.charge` i `refund` | port + Adapter (moduł 5, s14 i s29) | kolejność obciążeń i zwrotów |
| globalny stan | `LegacyDb`, `sequence`, `clock` | statyczne mutowalne mapy i licznik, hak "na chwilę" z 2019 roku | Parameterize Constructor, kierunek Inline Singleton | golden master ustawia `clock` statycznie |
| duplikacja | `cancel`, `expireOld` | ta sama pętla zwalniania miejsc, powtórzone rzutowanie `(Set<String>) s[6]` | Extract Method `releaseSeats` | - |
| magiczne liczby | cała klasa | `25.00`, `0.25`, `15`, `24`, `3.00`, `500.00`, `1.08` | Replace Magic Number with Named Constant | brak |
| pieniądze na `double` | wycena, zwroty, raporty | `Math.round(x * 100) / 100.0` w kilku miejscach | **świadomie pozostawić** (zmiana zaokrągleń to zmiana kontraktu, s23) | groszowe różnice |

Zapachy, których nie warto ruszać w tym ćwiczeniu: `Object[]` w `LegacyDb` (zbyt szeroki zasięg na 20 minut), `double` dla pieniędzy (zmiana kontraktu, wymaga decyzji księgowości), statyczny `clock` (golden master od niego zależy - najpierw trzeba zmienić skrypt testu, co jest osobnym krokiem).

Powiązanie ze scenami modułu 5: wycena - s01, s03, s20, s25; statusy - s08; powiadomienia - s09; płatności - s14, s29; rozliczenia - s21; zwroty - s23, s28; budowa rezerwacji - s05; raporty - s16, s27.

### Etap 2: luki golden mastera

Zachowania **nieobjęte** scenariuszem `CinemaManagerScript`:

- `book`: brak miejsc (`ERROR: no seats`), miejsce poza zakresem kolumn przy poprawnym rzędzie (warunek `letter - 'A' >= ...` jest maskowany przez warunek rzędu dla `Z99`), nieznany kod formatu (cena bazowa 0) i nieznany typ biletu (brak zniżki),
- `pay`: `ERROR: no booking`, `ERROR: already used`, `ERROR: cancelled`,
- `cancel`: rezerwacja nieistniejąca, anulowanie rezerwacji nieopłaconej (zwrot `0.00` bez naliczania), anulowanie po starcie seansu, anulowanie wykorzystanej rezerwacji, zwrot ujemny obcięty do zera,
- `use`: rezerwacja nieistniejąca, ponowne użycie,
- lojalność: dolna granica `Math.max(0, ...)` (u `anna` wynik to dokładnie 0),
- `settlement`: procenty tygodniowe - oba rozliczenia w scenariuszu kończą się minimalną gwarancją `500.00`, więc ani `0.50`, ani `0.40`, ani `0.35` nie wpływają na wynik,
- `dailyReport`: więcej niż jeden tytuł w dniu (sortowanie `TreeMap`),
- `expireOld`: granica dokładnie 15 minut,
- `freeSeats`: nieznany seans.

Kontrolne mutacje sprawdzone na kodzie (golden master pozostaje **zielony**):

| Mutacja | Wynik golden mastera |
| --- | --- |
| tydzień 1: `revenue * 0.50` → `revenue * 0.60` | zielony (luka) |
| tydzień 3+: `revenue * 0.35` → `revenue * 0.30` | zielony (luka) |
| komunikat `ERROR: already used` → `ERROR: used` | zielony (luka) |
| `cancel`: gałąź zwrotu także dla statusu NEW | zielony (luka) |
| lojalność: usunięcie `Math.max(0, ...)` | zielony (luka) |
| zniżka studencka `0.25` → `0.20` | **czerwony** (obszar dobrze pokryty) |

Przykładowe testy charakterystyki zamykające luki (pakiet `pl.training.workshop.legacy` w `src/test/java`; oba testy sprawdzone na obecnym kodzie):

```java
final class CinemaManagerGapsTest {
    private final LocalDateTime[] now = {LocalDateTime.of(2026, 3, 9, 9, 0)};
    private final CinemaManager cinema = new CinemaManager();

    @BeforeEach
    void setUp() {
        LegacyDb.clear();
        CinemaManager.clock = () -> now[0];
    }

    @AfterEach
    void tearDown() {
        CinemaManager.clock = LocalDateTime::now;
        LegacyDb.clear();
    }

    @Test
    void settlementUsesWeeklySharesAboveTheMinimumGuarantee() {
        // 40 biletów normalnych IMAX = 1600.00, grupa -10% = 1440.00
        cinema.addScreening("S1", "Diuna", 3, LocalDateTime.of(2026, 3, 20, 20, 0), 40, 1, 99);
        String[] seats = IntStream.rangeClosed(1, 40).mapToObj(row -> "A" + row).toArray(String[]::new);
        String[] types = IntStream.rangeClosed(1, 40).mapToObj(row -> "N").toArray(String[]::new);
        String booking = cinema.book("S1", "kino@firma.pl", null, seats, types, false, false);
        assertEquals("OK", cinema.pay(booking, "4111111111111111"));

        assertEquals("ROZLICZENIE Diuna tydzien 1: przychod 1440.00, dla dystrybutora 720.00",
                cinema.settlement("Diuna", 1));
        assertEquals("ROZLICZENIE Diuna tydzien 2: przychod 1440.00, dla dystrybutora 576.00",
                cinema.settlement("Diuna", 2));
        assertEquals("ROZLICZENIE Diuna tydzien 3: przychod 1440.00, dla dystrybutora 504.00",
                cinema.settlement("Diuna", 3));
    }

    @Test
    void statusTransitionsMissingFromTheGoldenMaster() {
        cinema.addScreening("S1", "Amator", 1, LocalDateTime.of(2026, 3, 20, 18, 30), 10, 12, 9);
        String used = cinema.book("S1", "a@kino.pl", null, new String[] {"A1"}, new String[] {"N"}, false, false);
        String fresh = cinema.book("S1", "b@kino.pl", null, new String[] {"B1"}, new String[] {"N"}, false, false);
        cinema.pay(used, "4111111111111111");
        cinema.use(used);

        assertEquals("ERROR: no booking", cinema.pay("B99", "4111111111111111"));
        assertEquals("ERROR: already used", cinema.pay(used, "4111111111111111"));
        assertEquals("ERROR: not paid", cinema.use(used));
        assertEquals("ERROR: cannot cancel", cinema.cancel(used));
        assertEquals("REFUND 0.00", cinema.cancel(fresh));
        assertEquals("ERROR: cancelled", cinema.pay(fresh, "4111111111111111"));
    }
}
```

Wniosek do omówienia: golden master jest bardzo dobry dla wyceny (każdy format, każdy typ biletu, poranek, VIP, okulary, grupa, opłata online), a słaby dla rozliczeń i rzadkich przejść statusów. Plan refaktoryzacji powinien to uwzględniać: wycenę można zmieniać od razu, State i rozliczenia - dopiero po dopisaniu testów.

### Etap 3: przykładowe karty planu

**Karta A: wycena biletu (niskie ryzyko, rekomendowana do etapu 4)**

| Pole | Treść |
| --- | --- |
| zapach i dowód | kody formatu `1/2/3` i typu `N/S/E/C` w łańcuchach `if` w środku pętli `book`; ceny i zniżki jako magiczne liczby |
| refaktoryzacja | Extract Method, Replace Type Code with Class, Move Method (Extract Class) |
| struktura docelowa | enumy `ScreeningFormat` i `TicketType` z kodem, ceną bazową i zniżką; czysta funkcja wyceny jednego biletu |
| zachowanie chronione | kolejność operacji: zniżka, poranek, VIP, okulary; zaokrąglenie tylko sumy; rabat grupowy na sumie; opłata online poza sumą biletów |
| test chroniący | golden master (pokrywa wszystkie formaty i typy) + test dla nieznanego formatu i typu |
| luki i decyzje | nieznany format daje cenę 0, nieznany typ - brak zniżki: **zachowujemy**, mimo że to wygląda na błąd |
| czego nie robimy | `BigDecimal`, walidacja kodów w `addScreening`, zmiana kodów w "bazie" |

**Karta B: State dla statusu rezerwacji (średnie ryzyko, wymaga testów z etapu 2)**

| Pole | Treść |
| --- | --- |
| zapach i dowód | `if (status == ...)` w `pay`, `cancel`, `use`, `expireOld`; status jako `int` w `b[7]` |
| refaktoryzacja | Replace Type Code with Class (enum `BookingStatus` z kodem), potem Replace State-Altering Conditionals with State |
| struktura docelowa | enum stanów z metodami zwracającymi następny stan albo komunikat błędu; `b[7]` nadal przechowuje kod liczbowy |
| zachowanie chronione | tabela przejść poniżej; w `cancel` kolejność: zwolnienie miejsc, status, zwrot i lojalność (tylko z PAID), mail |
| test chroniący | golden master + `statusTransitionsMissingFromTheGoldenMaster` |
| luki i decyzje | `cancel` czyta **stary** status do decyzji o zwrocie - nie zgubić tego przy przenoszeniu |
| czego nie robimy | zmiana komunikatów, nowe przejścia, zmiana zapisu w `LegacyDb` |

| Status | pay | cancel | use | expireOld |
| --- | --- | --- | --- | --- |
| NEW (0) | PAID albo `payment declined` (status bez zmian) | CANCELLED, zwrot `0.00` | `not paid` | EXPIRED po 15 min |
| PAID (1) | `already paid` | CANCELLED, zwrot według czasu | USED | - |
| USED (2) | `already used` | `cannot cancel` | `not paid` | - |
| EXPIRED (3) | `expired` | `cannot cancel` | `not paid` | - |
| CANCELLED (4) | `cancelled` | `cannot cancel` | `not paid` | - |

**Karta C: port powiadomień (średnie ryzyko)**

| Pole | Treść |
| --- | --- |
| zapach i dowód | statyczne `LegacyMailer.send` i `sms` w pięciu miejscach logiki |
| refaktoryzacja | Extract Method dla każdej wiadomości, Extract Interface, Parameterize Constructor (z konstruktorem bezparametrowym jako domyślnym) |
| struktura docelowa | port `CustomerNotifications` (`bookingCreated`, `paymentDeclined`, `ticketsPaid`, `bookingExpired`, `bookingCancelled`) i adapter do `LegacyMailer` |
| zachowanie chronione | treść i kolejność wiadomości w `LegacyMailer.SENT` (mail przed SMS w `pay`), brak SMS bez telefonu |
| test chroniący | golden master (porównuje całą skrzynkę nadawczą) |
| luki i decyzje | Observer z wieloma odbiorcami to **rozszerzenie** - najpierw jeden odbiorca przez port |
| czego nie robimy | asynchroniczna wysyłka, kontynuacja po błędzie maila |

Kolejność: A (najlepiej pokryta, najmniejszy zasięg) → C (golden master porównuje całą skrzynkę) → B (dopiero po testach luk). Rozliczenia (Strategy dla procentów) świadomie odkładamy: jeden model rozliczeń to za mało na Strategy (s21), wystarczą Extract Method i nazwane stałe - po dopisaniu `settlementUsesWeeklySharesAboveTheMinimumGuarantee`.

### Etap 4: przebieg referencyjny dla karty A

Każdy krok kończy się kompilacją, zielonym golden masterem i commitem.

1. **Extract Method** dla ciała pętli wyceny (IntelliJ: zaznacz od `double p = 0;` do ostatniego `if` z okularami): `private static double ticketPrice(Object[] s, String type, String seat, boolean ownGlasses)`. IDE poda wynik `p` jako wartość zwracaną. Pętla: `sum = sum + ticketPrice(s, types[i], seats[i], ownGlasses);`.
2. **Extract Method** dla łańcucha formatu: `private static double basePrice(int format)` - z zachowaniem `return 0` dla nieznanego kodu.
3. **Extract Method** dla zniżki: `private static double discounted(double price, String type)` - dokładnie to samo wyrażenie `price - price * 0.25`, nie "uproszczone" `price * 0.75` (inna arytmetyka na `double` może dać inną ostatnią cyfrę binarną, a zaokrąglenie robimy dopiero na sumie).
4. **Replace Type Code with Class** dla formatu:

```java
enum ScreeningFormat {
    TWO_D(1, 25.00), THREE_D(2, 32.00), IMAX(3, 40.00);

    private final int code;
    private final double basePrice;

    ScreeningFormat(int code, double basePrice) { this.code = code; this.basePrice = basePrice; }

    static double basePriceOf(int code) {
        for (ScreeningFormat format : values()) {
            if (format.code == code) return format.basePrice;
        }
        return 0;                                   // zachowane: nieznany kod = cena 0
    }
}
```

5. **Replace Type Code with Class** dla typu biletu: enum `TicketType` (`NORMAL("N", 0)`, `STUDENT("S", 0.25)`, `SENIOR("E", 0.30)`, `CHILD("C", 0.40)`) z metodą `discountOf(String code)`. Uwaga na `null`: legacy rzuca `NullPointerException` z `types[i].equals(...)`. Implementacja przez `switch` po `String` też rzuci `NullPointerException`, ale przez `Map.getOrDefault(code, 0.0)` - **nie**, co byłoby cichą zmianą zachowania. Wybierz świadomie i dopisz test.
6. **Move Method / Extract Class:** `ticketPrice` jako czysta funkcja nowej klasy `TicketPricing` (wejście: format, typ, godzina startu, rząd, próg VIP, własne okulary). `book` woła ją w pętli. Kody w `LegacyDb` pozostają liczbami i znakami - format trwały bez zmian.

Opcjonalny krok 7: test charakterystyki dla nieznanego formatu i typu (`addScreening` z formatem `9`, bilet typu `X`), żeby polityka "cena 0, brak zniżki" była zapisana, a nie przypadkowa.

Typowe pokusy do zapisania na liście "na później": `BigDecimal` zamiast `double`, odrzucanie nieznanego formatu, walidacja `seatsPerRow`, rabat grupowy jako osobna reguła, wyciągnięcie opłaty online.

### Etap 5: wskazówki do retrospektywy

- Zespoły zwykle zauważają, że **zapach łatwiej zobaczyć niż usunąć** w miejscach z efektami (powiadomienia, płatności) - bo kolejność efektów jest częścią kontraktu.
- Przeżyte mutacje w rozliczeniach to najlepszy materiał do rozmowy o tym, że zielony golden master dowodzi tylko tego, co scenariusz wykonuje.
- Krok "większy niż w planie" to najczęściej Extract Method w `book` z kilkoma wartościami wyjściowymi albo przeniesienie warunku statusu razem z efektem.
- Kandydaci na ADR: polityka dla nieznanych kodów, decyzja o `double`, wybór mechanizmu powiadomień (port teraz, Observer dopiero przy drugim odbiorcy).

### Weryfikacja kryteriów akceptacji

- diagnoza z faktami: tabela z kolumną "dowód" i co najmniej jednym zapachem świadomie pozostawionym,
- karty planu z nazwą refaktoryzacji, strukturą docelową, ryzykiem i testem,
- luki potwierdzone mutacją i zamknięte testem przed zmianą w obszarze,
- golden master zielony po każdym commicie; `git diff` nie pokazuje zmian w `cinema-manager.approved.txt`,
- commity z jedną intencją,
- lista zmian zachowania "na później".

### Typowe błędy uczestników

- Zmiana pliku `cinema-manager.approved.txt` "bo test był czerwony".
- Uproszczenie arytmetyki (`p * 0.75`) albo przejście na `BigDecimal` w tym samym kroku co Extract Method.
- Przeniesienie wyceny razem z zaokrągleniem sumy do funkcji jednego biletu (zaokrąglenie per bilet zmienia wynik).
- Zgubienie odczytu **starego** statusu w `cancel` przy wprowadzaniu State.
- Wprowadzenie Observera z kolekcją odbiorców i asynchroniczną wysyłką od razu.
- Zmiana `CinemaManager.clock` na pole instancji bez dostosowania `CinemaManagerScript` (golden master przestaje kontrolować czas).
- Diagnoza złożona z ocen ("kod jest brzydki") zamiast faktów ("status jako `int` sprawdzany w czterech metodach").
- Brak commitów pośrednich - jeden duży commit po 25 minutach.

---

## Odpowiedzi do pytań końcowych

1. **Po czym poznać, że zmiana jest refaktoryzacją, a nie zmianą zachowania?**
   Klient nie dostrzega żadnej nieuzgodnionej różnicy w przyjętej granicy: te same wyniki, wyjątki (typ, komunikat, moment), stan po sukcesie i błędzie, efekty i ich kolejność. Nowa walidacja, inny typ błędu, nowy odbiorca czy inne zaokrąglenie to zmiany zachowania.

2. **Dlaczego zielony golden master nie dowodzi bezpieczeństwa każdej zmiany?**
   Golden master dowodzi zgodności tylko dla wykonanych scenariuszy. W CineLegacy procenty rozliczeń i rzadkie przejścia statusów nie wpływają na wynik, więc mutacje w tych miejscach przeżywają. Zakres sieci sprawdzamy kontrolną mutacją.

3. **Jak wybrać pierwszy obszar do refaktoryzacji w dużym systemie legacy?**
   Tam, gdzie kod zmienia się najczęściej i gdzie planowane są zmiany biznesowe (gorące punkty: historia zmian × złożoność), a nie tam, gdzie jest najbrzydszy. Pierwszy krok ma być mały, odwracalny i chroniony testem.

4. **Kiedy wzorzec należy usunąć zamiast go rozbudowywać?**
   Gdy chroni zmienność, której już nie ma (jedna implementacja), tylko przekazuje wywołania, ukrywa zależność (globalny mutowalny stan) albo mimo niego nowy wariant wymaga zmian w wielu miejscach.

5. **Jak rozstrzygnąć między Strategy, Command i State dla podobnego `switch`?**
   Pytaniem: kto wybiera wariant i kiedy. Klient raz - Strategy; nadawca przy każdym żądaniu, które trzeba kolejkować, logować lub cofać - Command; nikt z zewnątrz, obiekt sam po przejściu - State.

6. **Co odróżnia Facade od Anti-Corruption Layer w kontekście migracji?**
   Fasada upraszcza interfejs podsystemu w tym samym modelu. ACL tłumaczy obcy model na model domeny i daje port, za którym można wymienić stary system na nowy (Strangler Fig, Branch by Abstraction) bez zmian w domenie.

7. **Jakie elementy powinien mieć opis zestawu zmian z refaktoryzacją?**
   Intencję (jedną), rodzaj zmiany (refaktoryzacja, zmiana zachowania, migracja), zakres i ryzyko, test chroniący, sposób wdrożenia i wycofania, listę elementów przejściowych z kryterium usunięcia oraz odwołanie do ADR, jeśli decyzja ma trwałe skutki.

8. **Jak zmierzyć, czy refaktoryzacja w zespole przynosi efekt?**
   Trendem: czas realizacji zmian w obszarze, liczba regresji i incydentów, rozmiar i czas przeglądu zestawów zmian, liczba nowych naruszeń bramek. Nie liczbą wzorców ani jednorazowym skokiem pokrycia.
