# Moduł 4. Podstawowe refaktoryzacje - warsztat CineLegacy: zadania

Każda scena ma pakiet `start` z kodem wyjściowym i test, który na starcie jest zielony. Pracuj tylko w `start`. Po każdym ruchu (jedna technika naraz) uruchom test sceny, na przykład `scripts/warsztat.sh test m4/s05`, albo ⌃R w IntelliJ na klasie testu. Test ma być zielony po każdym kroku, a nie dopiero na końcu. Nie zaglądaj do pakietów `stepN`, dopóki nie skończysz, bo to gotowe rozwiązania. Jeśli się zgubisz, `scripts/warsztat.sh reset m4/s05` przywraca `start`.

Tam, gdzie zadanie mówi o **zmianie zachowania**, nazwij ją wprost i zrób ją jako osobny krok. Dopiero wtedy wolno zmienić oczekiwania w teście.

## Scena s00. Test charakterystyki przed pierwszą zmianą
**Pakiet:** `pl.training.workshop.m4.s00_characterization.start` · **Test:** `scripts/warsztat.sh test m4/s00`
**Zadanie:**
1. Zanim przeczytasz istniejący `S00CharacterizationTest`, napisz własny test charakterystyki dla `BookingConfirmation.confirm`: pełny dokument dla co najmniej czterech rezerwacji.
2. Spraw, żeby test był deterministyczny, choć dokument zawiera bieżący czas i zależy od `Locale`.
3. Wprowadź do `BookingConfirmation` minimalny szew, dzięki któremu test porówna także linię z czasem, i wykonaj jedną refaktoryzację pod ochroną testu.
4. Zapisz jedno znalezisko, które wygląda na błąd, ale którego nie poprawiasz.

**Podpowiedź:** zacznij od `assertEquals("", ...)` i skopiuj wynik z komunikatu błędu. Szukaj progów w regułach i sprawdzaj obie strony każdego z nich.
**Gotowe, gdy:** Twój test i test sceny są zielone, dokument jest porównywany w całości, a stary konstruktor bezargumentowy nadal istnieje.

## Scena s01. Rename - nazwy, które żyją poza Javą
**Pakiet:** `pl.training.workshop.m4.s01_rename.start` · **Test:** `scripts/warsztat.sh test m4/s01`
**Zadanie:**
1. Nadaj znaczące nazwy zmiennym lokalnym i parametrom w `SalesReport`.
2. Zmień nazwę metody `calc2` na nazwę opisującą, co zwraca.
3. Zmień nazwy komponentów rekordu `Line` (`t`, `n`, `d`).
4. Nie zmieniaj konfiguracji w `ReportJob` ani formatu CSV, bo oba są kontraktem z kimś spoza repozytorium.

**Podpowiedź:** po każdym Rename uruchom oba testy równoważności. Jeśli jeden jest czerwony, zastanów się, gdzie jeszcze żyje ta nazwa.
**Gotowe, gdy:** test jest zielony, w `SalesReport` nie ma jedno- ani dwuliterowych nazw, a zadanie z konfiguracji dalej działa.

## Scena s02. Extract Variable dla złożonego wyrażenia ceny
**Pakiet:** `pl.training.workshop.m4.s02_extractvariable.start` · **Test:** `scripts/warsztat.sh test m4/s02`
**Zadanie:**
1. Rozbij wyrażenie w `TicketPrice.price` na zmienne z nazwami pojęć z cennika.
2. Nazwij osobno warunki (poranek, miejsce VIP, okulary) i osobno kwoty.
3. Nie zmieniaj kolejności działań ani miejsca zaokrąglenia.

**Podpowiedź:** `row` może być `null`. Zastanów się, co dokładnie zaznaczasz przed ⌥⌘V przy warunku VIP.
**Gotowe, gdy:** test jest zielony, a instrukcja `return` mieści się w jednej linii i czyta się jak paragon.

## Scena s03. Stałe z nazwą zamiast magicznych liczb
**Pakiet:** `pl.training.workshop.m4.s03_magicnumbers.start` · **Test:** `scripts/warsztat.sh test m4/s03`
**Zadanie:**
1. Zastąp każdą liczbę w `OrderPricer.summary` stałą z nazwą opisującą rolę.
2. Dla każdej liczby 10 (i `BigDecimal.TEN`) zdecyduj, czy to ta sama wiedza, co inna dziesiątka.
3. Wybierz widoczność i typ stałych i uzasadnij wybór jednym zdaniem.

**Podpowiedź:** gdy IntelliJ zapyta "Replace all occurrences?", nie odpowiadaj odruchowo. Pomyśl, co by się stało, gdyby jedna z reguł się zmieniła.
**Gotowe, gdy:** test jest zielony, w metodzie nie ma literałów liczbowych, a żadna stała nie nazywa się jak swoja wartość.

## Scena s04. Extract Method - przepływ danych i wiele wyjść
**Pakiet:** `pl.training.workshop.m4.s04_extractmethod.start` · **Test:** `scripts/warsztat.sh test m4/s04`
**Zadanie:**
1. Zamień bloki opisane komentarzami w `TicketSummary.describe` na metody z nazwami.
2. Poradź sobie z pętlą, która ma dwie wartości wyjściowe.
3. Oddziel obliczenia od składania tekstu.

**Podpowiedź:** jeśli IDE odmawia ekstrakcji, przeczytaj dokładnie, dlaczego. Rozwiązaniem nie jest tablica ani mutowalny "holder".
**Gotowe, gdy:** test jest zielony, `describe` ma najwyżej trzy linie, a każda wydzielona metoda ma jedno wyjście.

## Scena s05. Inline Variable - liczba i moment ewaluacji
**Pakiet:** `pl.training.workshop.m4.s05_inlinevariable.start` · **Test:** `scripts/warsztat.sh test m4/s05`
**Zadanie:**
1. Dla każdej zmiennej lokalnej w `TicketIssuer.issue` zdecyduj, czy wolno ją wkleić (Inline Variable), i zapisz uzasadnienie.
2. Wklej te, które wolno. Test ma zostać zielony.
3. Jedna ze zmiennych da się wkleić bezpiecznie dopiero po innym, przygotowawczym ruchu. Znajdź ją i wykonaj oba ruchy.

**Podpowiedź:** zegar w teście przesuwa się o sekundę przy każdym odczycie. Zwróć uwagę na metody o tej samej nazwie i różnych typach parametrów.
**Gotowe, gdy:** test jest zielony, a zmienne, które zostały, mają pisemne uzasadnienie (komentarz albo notatka).

## Scena s06. Inline Method i nadpisanie w podklasie
**Pakiet:** `pl.training.workshop.m4.s06_inlinemethod.start` · **Test:** `scripts/warsztat.sh test m4/s06`
**Zadanie:**
1. Usuń z `TicketPricing` pośredniki, które nie dodają znaczenia (Inline Method).
2. Zanim wkleisz którąkolwiek metodę, sprawdź, czy nie jest nadpisywana.
3. Wyjaśnij jednym zdaniem, dlaczego jedna z trzech małych metod musi zostać.

**Podpowiedź:** test uruchamia dwie klasy. Kasa może być zielona, a internet czerwony.
**Gotowe, gdy:** test jest zielony dla kasy i internetu, a w `TicketPricing` nie ma prywatnej metody, która tylko przekazuje wywołanie dalej.

## Scena s07. Move Method - Feature Envy i pułapka przeciążenia
**Pakiet:** `pl.training.workshop.m4.s07_movemethod.start` · **Test:** `scripts/warsztat.sh test m4/s07`
**Zadanie:**
1. Wskaż metody `BookingPrinter`, które używają danych tylko jednej innej klasy, i przenieś je do właściciela tych danych.
2. Nadaj przeniesionym metodom nazwy pasujące do nowego właściciela.
3. Uzasadnij, dlaczego `print` zostaje w `BookingPrinter`.

**Podpowiedź:** po przeniesieniu nie zmieniaj typów parametrów. Sprawdź, czy wszystkie przypadki testowe w ogóle odróżniają dwa przeciążenia `List.remove`.
**Gotowe, gdy:** test jest zielony, a `BookingPrinter` nie ma żadnej prywatnej metody.

## Scena s08. Move Field - próg VIP należy do sali
**Pakiet:** `pl.training.workshop.m4.s08_movefield.start` · **Test:** `scripts/warsztat.sh test m4/s08`
**Zadanie:**
1. Przenieś `vipFromRow` z `Screening` do `Hall`.
2. Rób to małymi krokami, bez okresu, w którym obie klasy mają zapisywalną kopię.
3. Na koniec żaden klient nie powinien porównywać rzędu z surowym progiem.

**Podpowiedź:** zanim przeniesiesz pole, sprawdź, kto czyta je bezpośrednio, także z innej klasy w tym samym pakiecie.
**Gotowe, gdy:** test jest zielony, `Screening` nie ma pola ani akcesora `vipFromRow`, a dwa seanse w tej samej sali nie mogą się różnić progiem.

## Scena s09. Extract Class - klient, płatność i klasa-worek
**Pakiet:** `pl.training.workshop.m4.s09_extractclass.start` · **Test:** `scripts/warsztat.sh test m4/s09`
**Zadanie:**
1. Wydziel z `Booking` klasę z danymi klienta, a potem przenieś do niej zachowanie, które na tych danych działa.
2. Wydziel klasę płatności od razu z danymi i zachowaniem.
3. Nie zmieniaj publicznego API `Booking` (konstruktor, `contact`, `pay`, `isPaid`, `summary`).
4. Nowe klasy nie mogą mieć referencji do `Booking`.

**Podpowiedź:** po pierwszej ekstrakcji zatrzymaj się i oceń wynik. Czy nowa klasa coś robi, czy tylko przechowuje?
**Gotowe, gdy:** test jest zielony, `Booking` ma najwyżej cztery pola, a każda nowa klasa ma co najmniej jedną metodę poza akcesorami.

## Scena s10. Encapsulate Field - status, który każdy może nadpisać
**Pakiet:** `pl.training.workshop.m4.s10_encapsulatefield.start` · **Test:** `scripts/warsztat.sh test m4/s10`
**Zadanie:**
1. Ukryj pole `status` bez zmiany zachowania.
2. Zastąp ogólne ustawianie statusu operacjami, które mówią, co się dzieje w domenie.
3. Jako **osobny krok** (zmiana zachowania) spraw, żeby niedozwolone przejścia rzucały `IllegalStateException`. Zanim to zrobisz, ustal, które przypadki testu zmienią wynik.

**Podpowiedź:** kroki 1 i 2 muszą przejść `encapsulationKeepsBehaviour` bez zmiany oczekiwań. Po kroku 3 porównaj swój wynik z `step3RejectsIllegalTransitions`.
**Gotowe, gdy:** po krokach 1-2 test równoważności jest zielony, pole jest prywatne i nie ma settera, a po kroku 3 dozwolone ścieżki działają jak wcześniej.

## Scena s11. Encapsulate Collection - trzy kontrakty listy miejsc
**Pakiet:** `pl.training.workshop.m4.s11_encapsulatecollection.start` · **Test:** `scripts/warsztat.sh test m4/s11`
**Zadanie:**
1. Ukryj listę miejsc i przenieś zmiany członkostwa do `Booking`.
2. Przepisz `SeatDesk`, żeby nie dotykał listy bezpośrednio.
3. Przygotuj dwie wersje gettera: niemodyfikowalny widok i migawkę. Dla każdej napisz, co zobaczy klient, który pobrał listę przed dodaniem nowego miejsca.

**Podpowiedź:** który z tych kroków jest jeszcze refaktoryzacją, a który już zmianą kontraktu? `S11CollectionContractTest` odpowiada na to tabelą.
**Gotowe, gdy:** `S11EquivalenceTest` jest zielony dla każdej wersji, pole jest prywatne, a Twoje przewidywania zgadzają się z testem kontraktów.

## Scena s12. Encapsulate Conditional - czy przysługuje zwrot
**Pakiet:** `pl.training.workshop.m4.s12_encapsulateconditional.start` · **Test:** `scripts/warsztat.sh test m4/s12`
**Zadanie:**
1. Zamień warunki w `RefundCalculator.refund` na metody o nazwach z regulaminu zwrotów.
2. Zacznij od najmniejszego fragmentu, skończ na całym warunku i na warunku gałęzi.
3. Nie upraszczaj wyrażeń logicznych przy okazji.

**Podpowiedź:** sprawdź przypadki `promo = null` i "dokładnie 24 godziny przed seansem". Zastanów się, które uproszczenie by je zepsuło.
**Gotowe, gdy:** test jest zielony, a oba `if` w `refund` czytają się jak zdania z regulaminu.
