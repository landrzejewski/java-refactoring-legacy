# Moduł 7. Zaawansowane refaktoryzacje - warsztat CineLegacy (TypeScript): zadania

Każda scena ma katalog `start` z kodem wyjściowym i test, który na starcie jest zielony (wyjątek: w s01 kodu startowego nie da się uruchomić w teście - to część zadania). Pracuj tylko w `start`. Po każdym ruchu (jedna technika naraz) uruchom test sceny, na przykład `scripts/warsztat.sh --lang ts test m7/s02`, albo test z rozszerzenia Vitest w VS Code. Test ma być zielony po każdym kroku, a nie dopiero na końcu. Vitest nie sprawdza typów, więc po zmianach sygnatur uruchom też `npm run typecheck` w katalogu `typescript`. Nie zaglądaj do katalogów `stepN`, dopóki nie skończysz, bo to gotowe rozwiązania. Jeśli się zgubisz, `scripts/warsztat.sh --lang ts reset m7/s02` przywraca `start`.

Przy każdej scenie zadaj sobie pytanie ze slajdu "Wektor obserwowalnego zachowania": co obserwuje test - wynik, wyjątek, stan, efekty uboczne, ich kolejność? Tam, gdzie zadanie mówi o **zmianie kontraktu**, nazwij ją wprost i zrób ją jako osobny krok. Dopiero wtedy wolno zmienić oczekiwania w teście.

## Scena s01. Break Dependencies - seam dla zadania przypomnień
**Katalog:** `typescript/src/workshop/m7/s01_breakdependencies/start` · **Test:** `scripts/warsztat.sh --lang ts test m7/s01`

**Zasada:** Break Dependencies tworzy seam, czyli miejsce, w którym można podstawić inną implementację zależności bez edycji algorytmu. Wybiera się najwęższy skuteczny seam, który nie zmienia czasu życia ani momentu tworzenia zależności.

**Zadanie:**
1. Uruchom `ShowtimeReminderJob.run()` w teście i zapisz, co stoi na przeszkodzie (są trzy przeszkody).
2. Usuń przeszkody po kolei, każdą osobnym ruchem, tak aby wywołanie `new ShowtimeReminderJob()` bez argumentów nadal składało te same implementacje produkcyjne.
3. Napisz test, który sprawdza wysłane przypomnienia i oznaczone rezerwacje dla seansu za 90, 120 i 121 minut.
4. Zachowaj moment, w którym powstaje połączenie z bazą.

**Podpowiedź:** najwęższy seam to często parametr konstruktora, `Clock` albo typ funkcyjny. Zastanów się, co by się stało, gdyby parametr domyślny konstruktora był gotową instancją `new LegacyDatabase()`, a nie fabryką.
**Gotowe, gdy:** Twój test jest zielony bez sieci i bazy, `ShowtimeReminderJob` nie ma metod `protected` dodanych tylko dla testu, a nowe typy nie mają metod, których zadanie nie używa.

## Scena s02. Extract Method Object - wycena zamówienia grupowego
**Katalog:** `typescript/src/workshop/m7/s02_methodobject/start` · **Test:** `scripts/warsztat.sh --lang ts test m7/s02`

**Zasada:** Extract Method Object przenosi metodę do krótko żyjącego obiektu, którego pola niosą stan lokalny. Stosuje się go, gdy splątane zmienne lokalne blokują zwykłe ekstrakcje.

**Zadanie:**
1. Spróbuj Extract to function (⌃⇧R) na pętli w `GroupPricing.quote` i zapisz, co zaproponował VS Code i dlaczego to nie rozwiązuje problemu.
2. Przenieś algorytm do obiektu metody tak, aby publiczne API `GroupPricing` się nie zmieniło.
3. Rozbij algorytm na metody bez parametrów, które czytają się jak kroki wyceny.

**Podpowiedź:** najpierw kopia bez upraszczania i delegacja, dopiero potem pola i ekstrakcje.
**Gotowe, gdy:** test jest zielony, obiekt metody powstaje na każde wywołanie, a metoda główna obiektu ma najwyżej 6 linii.

## Scena s03. Break Responsibilities - walidacja, wycena, powiadomienie
**Katalog:** `typescript/src/workshop/m7/s03_breakresponsibilities/start` · **Test:** `scripts/warsztat.sh --lang ts test m7/s03`

**Zasada:** Odpowiedzialność to powód zmiany, a nie liczba metod czy linii. Każdy wydzielony fragment powinien mieć jeden powód zmiany, a każdy stan jednego właściciela.

**Zadanie:**
1. Wypisz powody zmiany `BookingDesk.book` i kto w kinie za nimi stoi.
2. Wydziel każdą odpowiedzialność do osobnej klasy, jedną na raz.
3. Zostaw `BookingDesk` z niezmienionym publicznym konstruktorem i metodą `book`.

**Podpowiedź:** jedna odpowiedzialność liczy dwie wartości, z których druga jest potrzebna dopiero przy powiadomieniu.
**Gotowe, gdy:** test jest zielony, `book` ma najwyżej 7 linii, a `Outbox` ma jednego właściciela.

## Scena s04. Remove Duplication - rabat grupowy w kasie i w sklepie
**Katalog:** `typescript/src/workshop/m7/s04_removeduplication/start` · **Test:** `scripts/warsztat.sh --lang ts test m7/s04`

**Zasada:** Usuwamy duplikację wiedzy, czyli tej samej reguły, która zmienia się razem, a nie podobnego tekstu. Różnice między kopiami trzeba najpierw ujawnić i rozstrzygnąć, a dopiero potem łączyć.

**Zadanie:**
1. Znajdź regułę zapisaną dwa razy w `BoxOffice` i `WebShop` i opisz wszystkie różnice w zapisie.
2. Ujednolić zapis w obu klasach tak, żeby różniły się tylko tym, co naprawdę jest różne. Test ma być zielony bez zmian.
3. Podejmij decyzję o różnicy, która zostanie, i opisz ją jednym zdaniem jako zmianę kontraktu (albo jako świadomie zachowaną różnicę).
4. Wydziel wspólną regułę do jednego właściciela.

**Podpowiedź:** policz rabat dla koszyka 3 x 18.75 + 7 x 25.00 ręcznie w obu trybach zaokrąglenia (`Decimal.ROUND_HALF_UP` i `Decimal.ROUND_HALF_EVEN`).
**Gotowe, gdy:** testy są zielone, reguła 10+/-10% ma jedno źródło, a opłata online nadal żyje tylko w `WebShop`.

## Scena s05. Break Method - repertuar dnia
**Katalog:** `typescript/src/workshop/m7/s05_breakmethod/start` · **Test:** `scripts/warsztat.sh --lang ts test m7/s05`

**Zasada:** Break Method to seria małych ekstrakcji, po której metoda opisuje algorytm na jednym poziomie abstrakcji. Fragmenty przenosi się dosłownie, a nazwę nadaje po teście.

**Zadanie:**
1. Rozbij `RepertoireBuilder.build` na etapy tak, aby metoda publiczna była na jednym poziomie abstrakcji.
2. Zacznij od etapu z najmniejszą liczbą wejść i wyjść.
3. Zachowaj klasę i komunikat błędu (`IllegalArgumentError`) oraz nienaruszoną tablicę wejściową.

**Podpowiedź:** zastanów się, czy potrzebujesz Method Object ze sceny s02. Ile wartości przechodzi między etapami?
**Gotowe, gdy:** test jest zielony, a `build` ma trzy linie.

## Scena s06. Introduce Parameter Object - termin seansu
**Katalog:** `typescript/src/workshop/m7/s06_parameterobject/start` · **Test:** `scripts/warsztat.sh --lang ts test m7/s06`

**Zasada:** Parameter Object nazywa jedno pojęcie ukryte w grupie parametrów, które zawsze chodzą razem, i przyciąga związane z nim zachowanie. Walidacja w nowym typie zmienia moment zgłoszenia błędu, więc jest zmianą kontraktu.

**Zadanie:**
1. Nazwij pojęcie, które ukrywa czwórka parametrów w `ScreeningPlanner`, i wprowadź dla niego klasę.
2. Zostaw stare sygnatury na okres migracji (sygnatury przeciążeń z `/** @deprecated */`).
3. Przenieś do nowego typu zachowanie, które do niego należy.
4. Jako osobny krok przenieś walidację do nowego typu i zapisz, jak zmienił się moment zgłoszenia wyjątku.

**Podpowiedź:** porównaj, co zwraca `describe` dla sali 12 przed i po kroku 4.
**Gotowe, gdy:** test jest zielony, walidacja jest w jednym miejscu, a zmiana momentu błędu jest opisana w komentarzu TSDoc albo w opisie commita.

## Scena s07. Remove Arrowhead Antipattern - bramka rezerwacji
**Katalog:** `typescript/src/workshop/m7/s07_arrowhead/start` · **Test:** `scripts/warsztat.sh --lang ts test m7/s07`

**Zasada:** Guard clauses wyciągają przypadki kończące przetwarzanie na początek metody, dzięki czemu główna ścieżka nie jest zagnieżdżona. Spłaszczenie jest bezpieczne tylko przy zachowanym priorytecie warunków i nieominiętych efektach na końcu metody.

**Zadanie:**
1. Spłaszcz `BookingGate.book` do guard clauses.
2. Nie zgub wpisu do audytu dla żadnej ścieżki.
3. Zachowaj priorytet warunków.

**Podpowiedź:** zanim wstawisz pierwszy wczesny `return`, sprawdź, co metoda robi po zagnieżdżonym `if`.
**Gotowe, gdy:** test jest zielony, zagnieżdżenie ma najwyżej jeden poziom, a zmienna `result` nie jest potrzebna w logice decyzji.

## Scena s08. Introduce Design by Contract Checks - pula miejsc
**Katalog:** `typescript/src/workshop/m7/s08_designbycontract/start` · **Test:** `scripts/warsztat.sh --lang ts test m7/s08`

**Zasada:** Warunek wstępny to obowiązek klienta, warunek końcowy to gwarancja operacji, a niezmiennik to właściwość prawdziwa między operacjami. Odrzucanie niepoprawnych wejść jest zmianą zachowania, a nie refaktoryzacją.

**Zadanie:**
1. Wypisz niepoprawne wywołania `SeatPool`, które dziś psują stan.
2. Dodaj warunki wstępne tak, aby naruszenie nie zmieniało obiektu.
3. Dodaj warunki końcowe i niezmiennik.
4. Nie zmieniaj zachowania dla poprawnych wywołań, łącznie z odpowiedzią `false` przy braku miejsc.

**Podpowiedź:** nie używaj `console.assert` - tylko wypisuje komunikat. Sprawdź, w którym miejscu metody kontrola musi stać, żeby wyjątek nie zostawił zepsutego stanu.
**Gotowe, gdy:** test jest zielony, a Ty umiesz powiedzieć, które zmiany są refaktoryzacją, a które zmianą kontraktu.

## Scena s09. Remove Double Negative - wstęp do strefy VIP
**Katalog:** `typescript/src/workshop/m7/s09_doublenegative/start` · **Test:** `scripts/warsztat.sh --lang ts test m7/s09`

**Zasada:** Pozytywna nazwa musi być dokładnym logicznym dopełnieniem negatywnej, także na granicy i dla `null`/`undefined`. Zmienia się ją przez stan przejściowy, w którym obie nazwy współistnieją.

**Zadanie:**
1. Usuń podwójne zaprzeczenia z `LoungeAccess`.
2. Przejdź przez stan, w którym stara i nowa nazwa istnieją jednocześnie.
3. Na koniec zostaw tylko pozytywne nazwy w `Customer` i `Voucher`.

**Podpowiedź:** voucher ważny "do dziś" jest ważny także dziś. Sprawdź dopełnienie na granicy. Getter pozwala dodać pozytywną nazwę tak, żeby później pole o tej samej nazwie zastąpiło go bez zmiany wywołań.
**Gotowe, gdy:** test jest zielony, w kodzie nie ma `!customer.notVip` ani `!voucher.isNotExpired(...)`, a adapter w teście zmieniłeś świadomie.

## Scena s10. Remove Boolean Method Parameters - migracja API
**Katalog:** `typescript/src/workshop/m7/s10_booleanparameter/start` · **Test:** `scripts/warsztat.sh --lang ts test m7/s10`

**Zasada:** Literał `true`/`false` w wywołaniu nie mówi, co się stanie, ale nie każdy `boolean` jest flagą sterującą. Publiczne API zmienia się przez okres przejściowy, w którym stara metoda deleguje do nowej.

**Zadanie:**
1. Zastąp flagi w publicznym API `TicketService` czytelnymi metodami lub typami.
2. Zostaw starą metodę jako przestarzałą (`/** @deprecated */`) i delegującą, dopóki istnieje choć jeden klient.
3. Zmigruj `MobileApp` i `BoxOfficeTerminal`, każdego osobno.
4. Usuń starą metodę dopiero wtedy, gdy Find All References i `npm run typecheck` nie pokazują już jej użyć.

**Podpowiedź:** nie każdy `boolean` jest flagą. Nie twórz metody na każdą kombinację. Zobacz test `precompiledJsClientBreaksOnlyAfterSafeDelete` - kogo nie widzi kompilator?
**Gotowe, gdy:** test jest zielony, VS Code nie przekreśla już żadnego wywołania w klientach, a wywołania w klientach da się przeczytać bez zaglądania do sygnatury.

## Scena s11. Remove Middle Man - fasada kina
**Katalog:** `typescript/src/workshop/m7/s11_middleman/start` · **Test:** `scripts/warsztat.sh --lang ts test m7/s11`

**Zasada:** Pośrednika, który tylko przekazuje wywołania dalej, można usunąć. Najpierw trzeba jednak sprawdzić, czy nie robi czegoś więcej, na przykład autoryzacji, retry albo translacji błędów.

**Zadanie:**
1. Sprawdź każdą metodę `CinemaFacade`: czy tylko deleguje, czy robi coś więcej.
2. Przenieś to, co robi więcej, tam, gdzie jest potrzebne.
3. Zmigruj klientów do `ScreeningCatalog` po jednym i usuń fasadę.

**Podpowiedź:** uruchom `SeatBadge` dla seansu `S9` przed i po zmianie.
**Gotowe, gdy:** test jest zielony, `CinemaFacade` nie istnieje, a zachowanie dla nieznanego seansu jest takie samo jak na starcie.

## Scena s12. Return ASAP - wyszukiwanie wolnego miejsca
**Katalog:** `typescript/src/workshop/m7/s12_returnasap/start` · **Test:** `scripts/warsztat.sh --lang ts test m7/s12`

**Zasada:** Return ASAP zwraca wynik tam, gdzie jest już ostateczny, zamiast nieść go w zmiennej i fladze do końca metody. Wczesny `return` nie może ominąć mutacji, która musiała się wydarzyć.

**Zadanie:**
1. Zastąp zagnieżdżenia w `seatClass` guard clauses.
2. Usuń flagę `found` i zmienną `result` z `firstFree`.
3. Zachowaj licznik `inspected()` dokładnie taki sam jak na starcie.

**Podpowiedź:** sprawdź, w której linii pętli musi stać `return`, żeby licznik się zgadzał.
**Gotowe, gdy:** test jest zielony, żadna metoda nie ma zmiennej wyniku ani flagi pętli.

## Scena s13. Remove God Class - kampania na kopii CinemaManager
**Katalog:** `typescript/src/workshop/m7/s13_godclass/start` · **Test:** `scripts/warsztat.sh --lang ts test m7/s13`

**Zasada:** God Class ma wiele powodów zmiany i jest centralnym węzłem zależności. Usuwa się ją kampanią małych pionowych wycinków, a nie przepisaniem, a stara klasa zostaje fasadą.

**Zadanie:**
1. Narysuj mapę `CinemaManager`: metody, dane z `LegacyDb`, które czytają i zmieniają, oraz efekty (mail, SMS, bramka płatności).
2. Wybierz i wydziel jeden pionowy wycinek (na przykład cennik), zostawiając publiczne API `CinemaManager` bez zmian.
3. Powtórz dla kolejnego wycinka. Jeden z nich niech zastąpi `unknown[]` nazwanym typem.
4. Po każdym wycinku uruchom golden master i zrób commit.

**Podpowiedź:** kolejność efektów jest częścią zatwierdzonego wyniku. Nie poprawiaj kwot w `number` ani innych "błędów" w tym samym kroku. Po zmianie typu mapy `LegacyDb.BOOKINGS` listę miejsc do poprawy da `npm run typecheck`.
**Gotowe, gdy:** golden master jest zielony po każdym kroku, wydzielone klasy nie znają układu `unknown[]`, a nie ma cyklu zależności (cyklu importów) między nimi a `CinemaManager`.

## Scena s14. Refaktoryzacja a zmiana kontraktu - zwrot na BigDecimal
**Katalog:** `typescript/src/workshop/m7/s14_contractchange/start` · **Test:** `scripts/warsztat.sh --lang ts test m7/s14`

**Zasada:** Refaktoryzacja zachowuje obserwowalne zachowanie: klient nie może dostrzec żadnej nieuzgodnionej różnicy. Każda zmiana wyniku, formatu czy wyjątku to zmiana kontraktu, którą robi się osobno i świadomie.

**Zadanie:**
1. Zrób jedną czystą refaktoryzację `RefundCalculator.refund` bez zmiany typów.
2. Przejdź z `number` na `Decimal` (decimal.js - odpowiednik `BigDecimal`) i znajdź wszystkie różnice w zachowaniu, które to wprowadza.
3. Dla każdej różnicy zdecyduj: zachować stary kontrakt czy świadomie go zmienić. Zapisz decyzję.

**Podpowiedź:** sprawdź zwrot po starcie seansu oraz kwotę 64.35 anulowaną 2 godziny przed seansem. Pamiętaj, że `Decimal` nie przechowuje liczby miejsc po przecinku.
**Gotowe, gdy:** testy są zielone, a każda różnica względem startu ma w teście jawne oczekiwanie i jedno zdanie uzasadnienia.

## Scena s15. Wektor obserwowalnego zachowania - płatność za bilety
**Katalog:** `typescript/src/workshop/m7/s15_behaviourvector/start` · **Test:** `scripts/warsztat.sh --lang ts test m7/s15`

**Zasada:** Wektor obserwowalnego zachowania obejmuje wynik, wyjątki, stan, wywołania współpracowników z ich kolejnością, czas i granice. Test, który widzi tylko wynik, przepuści regresję w pozostałych wymiarach.

**Zadanie:**
1. Przeczytaj `S15ResultOnlyTest` i wypisz, czego ten test nie widzi.
2. Wprowadź seamy tak, aby test widział wysłane maile i obciążenia karty w jednej kolejności.
3. Napisz test, który wykrywa błąd w `TicketCheckout.pay`, a potem go napraw.
4. Dodaj do testu stan rezerwacji po operacji oraz klasę i komunikat błędu dla braku karty.

**Podpowiedź:** porównaj, co dostaje klient z kartą kończącą się na 0000, z tym, czego oczekujesz od kina.
**Gotowe, gdy:** Twój test jest czerwony dla kodu startowego i zielony po naprawie, a wynik metody `pay` w żadnym przypadku się nie zmienił.
