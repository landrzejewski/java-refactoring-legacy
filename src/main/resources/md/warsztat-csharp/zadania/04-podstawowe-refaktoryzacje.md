# Moduł 4. Podstawowe refaktoryzacje - warsztat CineLegacy (C#): zadania

Każda scena ma katalog (namespace) `Start` z kodem wyjściowym i test, który na starcie jest zielony. Pracuj tylko w `Start`. Po każdym ruchu (jedna technika naraz) uruchom test sceny, na przykład `scripts/warsztat.sh --lang cs test m4/s05`, albo w Rider z gutteru przy klasie testu lub z okna Unit Tests. Test ma być zielony po każdym kroku, a nie dopiero na końcu. Nie zaglądaj do katalogów `StepN`, dopóki nie skończysz, bo to gotowe rozwiązania. Jeśli się zgubisz, `scripts/warsztat.sh --lang cs reset m4/s05` przywraca `Start`.

Kod leży w `csharp/src/Training.Workshop/M4/SNN.../Start`, testy w `csharp/tests/Training.Workshop.Tests/M4/SNN.../`.

Tam, gdzie zadanie mówi o **zmianie zachowania**, nazwij ją wprost i zrób ją jako osobny krok. Dopiero wtedy wolno zmienić oczekiwania w teście.

## Scena s00. Test charakterystyki przed pierwszą zmianą
**Namespace:** `Training.Workshop.M4.S00Characterization.Start` · **Test:** `scripts/warsztat.sh --lang cs test m4/s00`

**Zasada:** Test charakterystyki zapisuje obecne, obserwowalne zachowanie kodu - razem z dziwnymi regułami - zanim cokolwiek zmienimy. Oczekiwania zatwierdza człowiek, a nie liczy je ponownie ten sam algorytm.

**Zadanie:**
1. Zanim przeczytasz istniejący `S00CharacterizationTest`, napisz własny test charakterystyki dla `BookingConfirmation.Confirm`: pełny dokument dla co najmniej czterech rezerwacji.
2. Spraw, żeby test był deterministyczny, choć dokument zawiera bieżący czas i zależy od bieżącej kultury (`CultureInfo.CurrentCulture`).
3. Wprowadź do `BookingConfirmation` minimalny szew (`TimeProvider`), dzięki któremu test porówna także linię z czasem, i wykonaj jedną refaktoryzację pod ochroną testu.
4. Zapisz jedno znalezisko, które wygląda na błąd, ale którego nie poprawiasz.

**Podpowiedź:** zacznij od `Assert.Equal("", ...)` i skopiuj wynik z komunikatu błędu. W teście użyj `FakeTimeProvider` (`Microsoft.Extensions.Time.Testing`), a kulturę ustaw tylko na czas wywołania i przywróć w `finally`. Szukaj progów w regułach i sprawdzaj obie strony każdego z nich.
**Gotowe, gdy:** Twój test i test sceny są zielone, dokument jest porównywany w całości, a stary konstruktor bezargumentowy nadal istnieje.

## Scena s01. Rename - nazwy, które żyją poza C#
**Namespace:** `Training.Workshop.M4.S01Rename.Start` · **Test:** `scripts/warsztat.sh --lang cs test m4/s01`

**Zasada:** Rename nadaje nazwę opisującą rolę w kontekście. Nazwa używana poza C# (konfiguracja, refleksja, formaty plików) jest kontraktem i wymaga strategii migracji, a nie tylko operacji IDE.

**Zadanie:**
1. Nadaj znaczące nazwy zmiennym lokalnym i parametrom w `SalesReport`.
2. Zmień nazwę metody `Calc2` na nazwę opisującą, co zwraca.
3. Zmień nazwy właściwości rekordu `Line` (`t`, `n`, `d`).
4. Nie zmieniaj konfiguracji w `ReportJob` ani formatu CSV, bo oba są kontraktem z kimś spoza repozytorium.

**Podpowiedź:** po każdym Rename uruchom oba testy równoważności. Jeśli jeden jest czerwony, zastanów się, gdzie jeszcze żyje ta nazwa.
**Gotowe, gdy:** test jest zielony, w `SalesReport` nie ma jedno- ani dwuliterowych nazw, a zadanie z konfiguracji dalej działa.

## Scena s02. Extract Variable dla złożonego wyrażenia ceny
**Namespace:** `Training.Workshop.M4.S02ExtractVariable.Start` · **Test:** `scripts/warsztat.sh --lang cs test m4/s02`

**Zasada:** Extract Variable nazywa znaczenie fragmentu wyrażenia. Może przesunąć moment ewaluacji, a krótkie spięcie `&&` / `||` jest częścią zachowania.

**Zadanie:**
1. Rozbij wyrażenie w `TicketPrice.Price` na zmienne z nazwami pojęć z cennika.
2. Nazwij osobno warunki (poranek, miejsce VIP, okulary) i osobno kwoty.
3. Nie zmieniaj kolejności działań ani miejsca zaokrąglenia.

**Podpowiedź:** `Row` to `int?` i może być `null`. Zastanów się, co dokładnie zaznaczasz przed ⌥⌘V przy warunku VIP.
**Gotowe, gdy:** test jest zielony, a instrukcja `return` mieści się w jednej linii i czyta się jak paragon.

## Scena s03. Stałe z nazwą zamiast magicznych liczb
**Namespace:** `Training.Workshop.M4.S03MagicNumbers.Start` · **Test:** `scripts/warsztat.sh --lang cs test m4/s03`

**Zasada:** Replace Magic Numbers zastępuje liczbę bez nazwy stałą z nazwą roli, bo problemem jest ukryta decyzja, a nie sama liczba. Dwa identyczne literały nie zawsze oznaczają tę samą wiedzę.

**Zadanie:**
1. Zastąp każdą liczbę w `OrderPricer.Summary` stałą z nazwą opisującą rolę.
2. Dla każdej liczby 10 (także dzielnika przy punktach lojalnościowych) zdecyduj, czy to ta sama wiedza, co inna dziesiątka.
3. Wybierz widoczność i rodzaj stałych (`const` czy `static readonly`) i uzasadnij wybór jednym zdaniem.

**Podpowiedź:** gdy Rider zapyta o zastąpienie wszystkich wystąpień, nie odpowiadaj odruchowo. Pomyśl, co by się stało, gdyby jedna z reguł się zmieniła.
**Gotowe, gdy:** test jest zielony, w metodzie nie ma literałów liczbowych, a żadna stała nie nazywa się jak swoja wartość.

## Scena s04. Extract Method - przepływ danych i wiele wyjść
**Namespace:** `Training.Workshop.M4.S04ExtractMethod.Start` · **Test:** `scripts/warsztat.sh --lang cs test m4/s04`

**Zasada:** Extract Method przenosi spójny fragment do metody, której nazwa wyraża intencję. Przed ekstrakcją trzeba przeanalizować przepływ danych: co wchodzi, a co wychodzi z fragmentu.

**Zadanie:**
1. Zamień bloki opisane komentarzami w `TicketSummary.Describe` na metody z nazwami.
2. Poradź sobie z pętlą, która ma dwie wartości wyjściowe.
3. Oddziel obliczenia od składania tekstu.

**Podpowiedź:** jeśli IDE przy ekstrakcji proponuje parametr `out`, przeczytaj dokładnie, dlaczego. Rozwiązaniem nie jest `out`, tablica ani mutowalny "holder".
**Gotowe, gdy:** test jest zielony, `Describe` ma najwyżej trzy linie, a każda wydzielona metoda ma jedno wyjście (bez parametrów `out`/`ref`).

## Scena s05. Inline Variable - liczba i moment ewaluacji
**Namespace:** `Training.Workshop.M4.S05InlineVariable.Start` · **Test:** `scripts/warsztat.sh --lang cs test m4/s05`

**Zasada:** Inline Variable usuwa zmienną, która nic nie wnosi, ale może zmienić liczbę i moment ewaluacji inicjalizatora. Jawny typ zmiennej bywa typem docelowym, który wybiera przeciążenie.

**Zadanie:**
1. Dla każdej zmiennej lokalnej w `TicketIssuer.Issue` zdecyduj, czy wolno ją wkleić (Inline Variable), i zapisz uzasadnienie.
2. Wklej te, które wolno. Test ma zostać zielony.
3. Jedna ze zmiennych da się wkleić bezpiecznie dopiero po innym, przygotowawczym ruchu. Znajdź ją i wykonaj oba ruchy.

**Podpowiedź:** zegar w teście (`TickingClock`) przesuwa się o sekundę przy każdym odczycie. Zwróć uwagę na metody o tej samej nazwie i różnych typach parametrów.
**Gotowe, gdy:** test jest zielony, a zmienne, które zostały, mają pisemne uzasadnienie (komentarz albo notatka).

## Scena s06. Inline Method i nadpisanie w podklasie
**Namespace:** `Training.Workshop.M4.S06InlineMethod.Start` · **Test:** `scripts/warsztat.sh --lang cs test m4/s06`

**Zasada:** Inline Method usuwa pośrednictwo bez znaczenia. Wklejenie ciała metody nadpisywanej (`virtual`/`override`) usuwa dynamiczną dyspozycję, więc przed inline sprawdza się hierarchię klas.

**Zadanie:**
1. Usuń z `TicketPricing` pośredniki, które nie dodają znaczenia (Inline Method).
2. Zanim wkleisz którąkolwiek metodę, sprawdź, czy nie jest nadpisywana.
3. Wyjaśnij jednym zdaniem, dlaczego jedna z trzech małych metod musi zostać.

**Podpowiedź:** test uruchamia dwie klasy. Kasa może być zielona, a internet czerwony.
**Gotowe, gdy:** test jest zielony dla kasy i internetu, a w `TicketPricing` nie ma prywatnej metody, która tylko przekazuje wywołanie dalej.

## Scena s07. Move Method - Feature Envy i pułapka przeciążenia
**Namespace:** `Training.Workshop.M4.S07MoveMethod.Start` · **Test:** `scripts/warsztat.sh --lang cs test m4/s07`

**Zasada:** Move Method przenosi zachowanie do klasy, która ma jego dane i odpowiedzialność, a Feature Envy to sygnał do analizy, nie nakaz. Po przeniesieniu zmienia się kontekst typów i przeciążeń.

**Zadanie:**
1. Wskaż metody `BookingPrinter`, które używają danych tylko jednej innej klasy, i przenieś je do właściciela tych danych.
2. Nadaj przeniesionym metodom nazwy pasujące do nowego właściciela.
3. Uzasadnij, dlaczego `Print` zostaje w `BookingPrinter`.

**Podpowiedź:** po przeniesieniu nie zmieniaj typów parametrów. Sprawdź, czy wszystkie przypadki testowe w ogóle odróżniają dwa przeciążenia `SeatList.Remove` (`Remove(int index)` i `Remove(int? seat)`).
**Gotowe, gdy:** test jest zielony, a `BookingPrinter` nie ma żadnej prywatnej metody.

## Scena s08. Move Field - próg VIP należy do sali
**Namespace:** `Training.Workshop.M4.S08MoveField.Start` · **Test:** `scripts/warsztat.sh --lang cs test m4/s08`

**Zasada:** Move Field przenosi stan do właściwego właściciela: najpierw odczyty przez akcesor (właściwość), potem zapisy, bez okresu z dwiema zapisywalnymi kopiami.

**Zadanie:**
1. Przenieś `VipFromRow` z `Screening` do `Hall`.
2. Rób to małymi krokami, bez okresu, w którym obie klasy mają zapisywalną kopię.
3. Na koniec żaden klient nie powinien porównywać rzędu z surowym progiem.

**Podpowiedź:** zanim przeniesiesz pole, sprawdź, kto czyta je bezpośrednio, także z innej klasy w tym samym assembly (pole jest `internal`).
**Gotowe, gdy:** test jest zielony, `Screening` nie ma pola ani właściwości `VipFromRow`, a dwa seanse w tej samej sali nie mogą się różnić progiem.

## Scena s09. Extract Class - klient, płatność i klasa-worek
**Namespace:** `Training.Workshop.M4.S09ExtractClass.Start` · **Test:** `scripts/warsztat.sh --lang cs test m4/s09`

**Zasada:** Extract Class wydziela spójne dane i zachowanie o odrębnym powodzie zmiany. Klasa, która tylko przechowuje dane, a logika zostaje w źródle, to zły wynik.

**Zadanie:**
1. Wydziel z `Booking` klasę z danymi klienta, a potem przenieś do niej zachowanie, które na tych danych działa.
2. Wydziel klasę płatności od razu z danymi i zachowaniem.
3. Nie zmieniaj publicznego API `Booking` (konstruktor, `Contact`, `Pay`, `IsPaid`, `Summary`).
4. Nowe klasy nie mogą mieć referencji do `Booking`.

**Podpowiedź:** po pierwszej ekstrakcji zatrzymaj się i oceń wynik. Czy nowa klasa coś robi, czy tylko przechowuje?
**Gotowe, gdy:** test jest zielony, `Booking` ma najwyżej cztery pola, a każda nowa klasa ma co najmniej jedną metodę poza akcesorami właściwości (i poza tym, co kompilator generuje dla rekordu).

## Scena s10. Encapsulate Field - status, który każdy może nadpisać
**Namespace:** `Training.Workshop.M4.S10EncapsulateField.Start` · **Test:** `scripts/warsztat.sh --lang cs test m4/s10`

**Zasada:** Encapsulate Field ukrywa pole za operacjami właściciela, żeby kontrolować zmiany stanu. Sama hermetyzacja nie zmienia zachowania, a dodanie walidacji już tak.

**Zadanie:**
1. Ukryj pole `Status` bez zmiany zachowania.
2. Zastąp ogólne ustawianie statusu operacjami, które mówią, co się dzieje w domenie.
3. Jako **osobny krok** (zmiana zachowania) spraw, żeby niedozwolone przejścia rzucały `InvalidOperationException`. Zanim to zrobisz, ustal, które przypadki testu zmienią wynik.

**Podpowiedź:** kroki 1 i 2 muszą przejść `EncapsulationKeepsBehaviour` bez zmiany oczekiwań. Po kroku 3 porównaj swój wynik z `Step3RejectsIllegalTransitions`.
**Gotowe, gdy:** po krokach 1-2 test równoważności jest zielony, pole jest prywatne i właściwość `Status` nie ma settera, a po kroku 3 dozwolone ścieżki działają jak wcześniej.

## Scena s11. Encapsulate Collection - trzy kontrakty listy miejsc
**Namespace:** `Training.Workshop.M4.S11EncapsulateCollection.Start` · **Test:** `scripts/warsztat.sh --lang cs test m4/s11`

**Zasada:** Encapsulate Collection oddaje właścicielowi kontrolę nad członkostwem kolekcji. To, co zwraca właściwość (alias, widok albo kopia), jest osobnym kontraktem.

**Zadanie:**
1. Ukryj listę miejsc i przenieś zmiany członkostwa do `Booking`.
2. Przepisz `SeatDesk`, żeby nie dotykał listy bezpośrednio.
3. Przygotuj dwie wersje właściwości: niemodyfikowalny widok i migawkę. Dla każdej napisz, co zobaczy klient, który pobrał listę przed dodaniem nowego miejsca.

**Podpowiedź:** który z tych kroków jest jeszcze refaktoryzacją, a który już zmianą kontraktu? `S11CollectionContractTest` odpowiada na to tabelą. Pamiętaj, że sam typ `IReadOnlyList<T>` nie chroni listy przed rzutowaniem.
**Gotowe, gdy:** `S11EquivalenceTest` jest zielony dla każdej wersji, pole jest prywatne, a Twoje przewidywania zgadzają się z testem kontraktów.

## Scena s12. Encapsulate Conditional - czy przysługuje zwrot
**Namespace:** `Training.Workshop.M4.S12EncapsulateConditional.Start` · **Test:** `scripts/warsztat.sh --lang cs test m4/s12`

**Zasada:** Encapsulate Conditional zamienia warunek pytający o implementację na predykat z nazwą z domeny. Krótkie spięcie i kolejność warunków są częścią zachowania.

**Zadanie:**
1. Zamień warunki w `RefundCalculator.Refund` na metody o nazwach z regulaminu zwrotów.
2. Zacznij od najmniejszego fragmentu, skończ na całym warunku i na warunku gałęzi.
3. Nie upraszczaj wyrażeń logicznych przy okazji.

**Podpowiedź:** sprawdź przypadki `Promo = null` i "dokładnie 24 godziny przed seansem". Zastanów się, które uproszczenie by je zepsuło.
**Gotowe, gdy:** test jest zielony, a oba `if` w `Refund` czytają się jak zdania z regulaminu.
