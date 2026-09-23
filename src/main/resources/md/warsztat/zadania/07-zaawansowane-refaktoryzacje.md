# Moduł 7. Zaawansowane refaktoryzacje - warsztat CineLegacy: zadania

Każda scena ma pakiet `start` z kodem wyjściowym i test, który na starcie jest zielony (wyjątek: w s01 kodu startowego nie da się uruchomić w teście - to część zadania). Pracuj tylko w `start`. Po każdym ruchu (jedna technika naraz) uruchom test sceny, na przykład `scripts/warsztat.sh test m7/s02`, albo ⌃R w IntelliJ na klasie testu. Test ma być zielony po każdym kroku, a nie dopiero na końcu. Nie zaglądaj do pakietów `stepN`, dopóki nie skończysz, bo to gotowe rozwiązania. Jeśli się zgubisz, `scripts/warsztat.sh reset m7/s02` przywraca `start`.

Przy każdej scenie zadaj sobie pytanie ze slajdu "Wektor obserwowalnego zachowania": co obserwuje test - wynik, wyjątek, stan, efekty uboczne, ich kolejność? Tam, gdzie zadanie mówi o **zmianie kontraktu**, nazwij ją wprost i zrób ją jako osobny krok. Dopiero wtedy wolno zmienić oczekiwania w teście.

## Scena s01. Break Dependencies - seam dla zadania przypomnień
**Pakiet:** `pl.training.workshop.m7.s01_breakdependencies.start` · **Test:** `scripts/warsztat.sh test m7/s01`
**Zadanie:**
1. Uruchom `ShowtimeReminderJob.run()` w teście i zapisz, co stoi na przeszkodzie (są trzy przeszkody).
2. Usuń przeszkody po kolei, każdą osobnym ruchem, tak aby produkcyjny konstruktor bezargumentowy nadal składał te same implementacje.
3. Napisz test, który sprawdza wysłane przypomnienia i oznaczone rezerwacje dla seansu za 90, 120 i 121 minut.
4. Zachowaj moment, w którym powstaje połączenie z bazą.

**Podpowiedź:** najwęższy seam to często parametr konstruktora, `Clock` albo interfejs funkcyjny. Zastanów się, co by się stało, gdyby domyślny konstruktor zrobił `new LegacyDatabase()` od razu.
**Gotowe, gdy:** Twój test jest zielony bez sieci i bazy, `ShowtimeReminderJob` jest `final`, a nowe interfejsy nie mają metod, których zadanie nie używa.

## Scena s02. Extract Method Object - wycena zamówienia grupowego
**Pakiet:** `pl.training.workshop.m7.s02_methodobject.start` · **Test:** `scripts/warsztat.sh test m7/s02`
**Zadanie:**
1. Spróbuj Extract Method na pętli w `GroupPricing.quote` i zapisz, dlaczego się nie udaje.
2. Przenieś algorytm do obiektu metody tak, aby publiczne API `GroupPricing` się nie zmieniło.
3. Rozbij algorytm na metody bez parametrów, które czytają się jak kroki wyceny.

**Podpowiedź:** najpierw kopia bez upraszczania i delegacja, dopiero potem pola i ekstrakcje.
**Gotowe, gdy:** test jest zielony, obiekt metody powstaje na każde wywołanie, a metoda główna obiektu ma najwyżej 6 linii.

## Scena s03. Break Responsibilities - walidacja, wycena, powiadomienie
**Pakiet:** `pl.training.workshop.m7.s03_breakresponsibilities.start` · **Test:** `scripts/warsztat.sh test m7/s03`
**Zadanie:**
1. Wypisz powody zmiany `BookingDesk.book` i kto w kinie za nimi stoi.
2. Wydziel każdą odpowiedzialność do osobnej klasy, jedną na raz.
3. Zostaw `BookingDesk` z niezmienionym publicznym konstruktorem i metodą `book`.

**Podpowiedź:** jedna odpowiedzialność liczy dwie wartości, z których druga jest potrzebna dopiero przy powiadomieniu.
**Gotowe, gdy:** test jest zielony, `book` ma najwyżej 7 linii, a `Outbox` ma jednego właściciela.

## Scena s04. Remove Duplication - rabat grupowy w kasie i w sklepie
**Pakiet:** `pl.training.workshop.m7.s04_removeduplication.start` · **Test:** `scripts/warsztat.sh test m7/s04`
**Zadanie:**
1. Znajdź regułę zapisaną dwa razy w `BoxOffice` i `WebShop` i opisz wszystkie różnice w zapisie.
2. Ujednolić zapis w obu klasach tak, żeby różniły się tylko tym, co naprawdę jest różne. Test ma być zielony bez zmian.
3. Podejmij decyzję o różnicy, która zostanie, i opisz ją jednym zdaniem jako zmianę kontraktu (albo jako świadomie zachowaną różnicę).
4. Wydziel wspólną regułę do jednego właściciela.

**Podpowiedź:** policz rabat dla koszyka 3 x 18.75 + 7 x 25.00 ręcznie w obu trybach zaokrąglenia.
**Gotowe, gdy:** testy są zielone, reguła 10+/-10% ma jedno źródło, a opłata online nadal żyje tylko w `WebShop`.

## Scena s05. Break Method - repertuar dnia
**Pakiet:** `pl.training.workshop.m7.s05_breakmethod.start` · **Test:** `scripts/warsztat.sh test m7/s05`
**Zadanie:**
1. Rozbij `RepertoireBuilder.build` na etapy tak, aby metoda publiczna była na jednym poziomie abstrakcji.
2. Zacznij od etapu z najmniejszą liczbą wejść i wyjść.
3. Zachowaj typ i komunikat wyjątku oraz nienaruszoną listę wejściową.

**Podpowiedź:** zastanów się, czy potrzebujesz Method Object ze sceny s02. Ile wartości przechodzi między etapami?
**Gotowe, gdy:** test jest zielony, a `build` ma trzy linie.

## Scena s06. Introduce Parameter Object - termin seansu
**Pakiet:** `pl.training.workshop.m7.s06_parameterobject.start` · **Test:** `scripts/warsztat.sh test m7/s06`
**Zadanie:**
1. Nazwij pojęcie, które ukrywa czwórka parametrów w `ScreeningPlanner`, i wprowadź dla niego typ.
2. Zostaw stare sygnatury na okres migracji.
3. Przenieś do nowego typu zachowanie, które do niego należy.
4. Jako osobny krok przenieś walidację do nowego typu i zapisz, jak zmienił się moment zgłoszenia wyjątku.

**Podpowiedź:** porównaj, co zwraca `describe` dla sali 12 przed i po kroku 4.
**Gotowe, gdy:** test jest zielony, walidacja jest w jednym miejscu, a zmiana momentu błędu jest opisana w Javadoc albo w opisie commita.

## Scena s07. Remove Arrowhead Antipattern - bramka rezerwacji
**Pakiet:** `pl.training.workshop.m7.s07_arrowhead.start` · **Test:** `scripts/warsztat.sh test m7/s07`
**Zadanie:**
1. Spłaszcz `BookingGate.book` do guard clauses.
2. Nie zgub wpisu do audytu dla żadnej ścieżki.
3. Zachowaj priorytet warunków.

**Podpowiedź:** zanim wstawisz pierwszy wczesny `return`, sprawdź, co metoda robi po zagnieżdżonym `if`.
**Gotowe, gdy:** test jest zielony, zagnieżdżenie ma najwyżej jeden poziom, a zmienna `result` nie jest potrzebna w logice decyzji.

## Scena s08. Introduce Design by Contract Checks - pula miejsc
**Pakiet:** `pl.training.workshop.m7.s08_designbycontract.start` · **Test:** `scripts/warsztat.sh test m7/s08`
**Zadanie:**
1. Wypisz niepoprawne wywołania `SeatPool`, które dziś psują stan.
2. Dodaj warunki wstępne tak, aby naruszenie nie zmieniało obiektu.
3. Dodaj warunki końcowe i niezmiennik.
4. Nie zmieniaj zachowania dla poprawnych wywołań, łącznie z odpowiedzią `false` przy braku miejsc.

**Podpowiedź:** nie używaj `assert`. Sprawdź, w którym miejscu metody kontrola musi stać, żeby wyjątek nie zostawił zepsutego stanu.
**Gotowe, gdy:** test jest zielony, a Ty umiesz powiedzieć, które zmiany są refaktoryzacją, a które zmianą kontraktu.

## Scena s09. Remove Double Negative - wstęp do strefy VIP
**Pakiet:** `pl.training.workshop.m7.s09_doublenegative.start` · **Test:** `scripts/warsztat.sh test m7/s09`
**Zadanie:**
1. Usuń podwójne zaprzeczenia z `LoungeAccess`.
2. Przejdź przez stan, w którym stara i nowa nazwa istnieją jednocześnie.
3. Na koniec zostaw tylko pozytywne nazwy w `Customer` i `Voucher`.

**Podpowiedź:** voucher ważny "do dziś" jest ważny także dziś. Sprawdź dopełnienie na granicy.
**Gotowe, gdy:** test jest zielony, w kodzie nie ma `!customer.notVip()` ani `!voucher.isNotExpired(...)`, a adapter w teście zmieniłeś świadomie.

## Scena s10. Remove Boolean Method Parameters - migracja API
**Pakiet:** `pl.training.workshop.m7.s10_booleanparameter.start` · **Test:** `scripts/warsztat.sh test m7/s10`
**Zadanie:**
1. Zastąp flagi w publicznym API `TicketService` czytelnymi metodami lub typami.
2. Zostaw starą metodę jako przestarzałą i delegującą, dopóki istnieje choć jeden klient.
3. Zmigruj `MobileApp` i `BoxOfficeTerminal`, każdego osobno.
4. Usuń starą metodę dopiero wtedy, gdy kompilator nie zgłasza już jej użyć.

**Podpowiedź:** nie każdy `boolean` jest flagą. Nie twórz metody na każdą kombinację.
**Gotowe, gdy:** test jest zielony, kompilacja nie ma ostrzeżeń `[deprecation]`, a wywołania w klientach da się przeczytać bez zaglądania do sygnatury.

## Scena s11. Remove Middle Man - fasada kina
**Pakiet:** `pl.training.workshop.m7.s11_middleman.start` · **Test:** `scripts/warsztat.sh test m7/s11`
**Zadanie:**
1. Sprawdź każdą metodę `CinemaFacade`: czy tylko deleguje, czy robi coś więcej.
2. Przenieś to, co robi więcej, tam, gdzie jest potrzebne.
3. Zmigruj klientów do `ScreeningCatalog` po jednym i usuń fasadę.

**Podpowiedź:** uruchom `SeatBadge` dla seansu `S9` przed i po zmianie.
**Gotowe, gdy:** test jest zielony, `CinemaFacade` nie istnieje, a zachowanie dla nieznanego seansu jest takie samo jak na starcie.

## Scena s12. Return ASAP - wyszukiwanie wolnego miejsca
**Pakiet:** `pl.training.workshop.m7.s12_returnasap.start` · **Test:** `scripts/warsztat.sh test m7/s12`
**Zadanie:**
1. Zastąp zagnieżdżenia w `seatClass` guard clauses.
2. Usuń flagę `found` i zmienną `result` z `firstFree`.
3. Zachowaj licznik `inspected` dokładnie taki sam jak na starcie.

**Podpowiedź:** sprawdź, w której linii pętli musi stać `return`, żeby licznik się zgadzał.
**Gotowe, gdy:** test jest zielony, żadna metoda nie ma zmiennej wyniku ani flagi pętli.

## Scena s13. Remove God Class - kampania na kopii CinemaManager
**Pakiet:** `pl.training.workshop.m7.s13_godclass.start` · **Test:** `scripts/warsztat.sh test m7/s13`
**Zadanie:**
1. Narysuj mapę `CinemaManager`: metody, dane z `LegacyDb`, które czytają i zmieniają, oraz efekty (mail, SMS, bramka płatności).
2. Wybierz i wydziel jeden pionowy wycinek (na przykład cennik), zostawiając publiczne API `CinemaManager` bez zmian.
3. Powtórz dla kolejnego wycinka. Jeden z nich niech zastąpi `Object[]` nazwanym typem.
4. Po każdym wycinku uruchom golden master i zrób commit.

**Podpowiedź:** kolejność efektów jest częścią zatwierdzonego wyniku. Nie poprawiaj `double` ani innych "błędów" w tym samym kroku.
**Gotowe, gdy:** golden master jest zielony po każdym kroku, wydzielone klasy nie znają układu `Object[]`, a nie ma cyklu zależności między nimi a `CinemaManager`.

## Scena s14. Refaktoryzacja a zmiana kontraktu - zwrot na BigDecimal
**Pakiet:** `pl.training.workshop.m7.s14_contractchange.start` · **Test:** `scripts/warsztat.sh test m7/s14`
**Zadanie:**
1. Zrób jedną czystą refaktoryzację `RefundCalculator.refund` bez zmiany typów.
2. Przejdź na `BigDecimal` i znajdź wszystkie różnice w zachowaniu, które to wprowadza.
3. Dla każdej różnicy zdecyduj: zachować stary kontrakt czy świadomie go zmienić. Zapisz decyzję.

**Podpowiedź:** sprawdź zwrot po starcie seansu oraz kwotę 64.35 anulowaną 2 godziny przed seansem.
**Gotowe, gdy:** testy są zielone, a każda różnica względem startu ma w teście jawne oczekiwanie i jedno zdanie uzasadnienia.

## Scena s15. Wektor obserwowalnego zachowania - płatność za bilety
**Pakiet:** `pl.training.workshop.m7.s15_behaviourvector.start` · **Test:** `scripts/warsztat.sh test m7/s15`
**Zadanie:**
1. Przeczytaj `S15ResultOnlyTest` i wypisz, czego ten test nie widzi.
2. Wprowadź seamy tak, aby test widział wysłane maile i obciążenia karty w jednej kolejności.
3. Napisz test, który wykrywa błąd w `TicketCheckout.pay`, a potem go napraw.
4. Dodaj do testu stan rezerwacji po operacji oraz typ i komunikat wyjątku dla braku karty.

**Podpowiedź:** porównaj, co dostaje klient z kartą kończącą się na 0000, z tym, czego oczekujesz od kina.
**Gotowe, gdy:** Twój test jest czerwony dla kodu startowego i zielony po naprawie, a wynik metody `pay` w żadnym przypadku się nie zmienił.
