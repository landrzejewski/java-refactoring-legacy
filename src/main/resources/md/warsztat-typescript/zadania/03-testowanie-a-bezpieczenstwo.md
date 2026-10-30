# Moduł 3. Testowanie a bezpieczeństwo - warsztat CineLegacy (TypeScript): zadania

Każda scena ma katalog `start` z kodem wyjściowym i test (vitest), który na starcie jest zielony (wyjątek: w s02 kodu startowego nie da się uruchomić w teście - to część zadania). Kod scen leży w `typescript/src/workshop/m3/`, a testy w `typescript/test/workshop/m3/`. Pracuj tylko w `start`. Po każdym ruchu (jedna technika naraz) uruchom test sceny, na przykład `scripts/warsztat.sh --lang ts test m3/s02`, albo test z rozszerzenia Vitest w VS Code. Test ma być zielony po każdym kroku, a nie dopiero na końcu. Vitest nie sprawdza typów, więc po zmianach sygnatur uruchom też `npm run typecheck` w katalogu `typescript`. Nie zaglądaj do katalogów `stepN`, dopóki nie skończysz, bo to gotowe rozwiązania. Jeśli się zgubisz, `scripts/warsztat.sh --lang ts reset m3/s02` przywraca `start`.

Przy każdej scenie zadaj sobie pytanie ze slajdu 1.2 "Zachowanie obserwowalne to więcej niż wynik metody": co obserwuje test - wynik, wyjątek, stan, efekty uboczne, ich kolejność? Tam, gdzie zadanie mówi o **zmianie zachowania**, nazwij ją wprost i zrób ją jako osobny krok. Dopiero wtedy wolno zmienić oczekiwania w teście.

W s04 i s05 część testów opisuje stan startowy (np. "start obciąża kartę dwa razy i wysyła dwa maile") - gdy naprawisz `start`, zmienią kolor na czerwony, a to znaczy, że zadanie jest zrobione.

Punkt odniesienia: `CinemaManagerGoldenMasterTest` (`typescript/test/workshop/legacy/CinemaManagerGoldenMasterTest.test.ts`, uruchomienie: `cd typescript && npx vitest run test/workshop/legacy`) chroni cały stary system jednym testem porównującym pełny wektor zachowania z plikiem `src/test/resources/workshop/cinema-manager.approved.txt` (wspólnym dla Javy, C# i TypeScriptu). Zanim zaczniesz, otwórz ten plik i zapisz, które wymiary wektora w nim widać.

## Scena s01. Test charakterystyki przed pierwszą zmianą
**Katalog:** `typescript/src/workshop/m3/s01_characterization/start` · **Test:** `scripts/warsztat.sh --lang ts test m3/s01`

**Zasada:** Test charakterystyki zapisuje obecne, obserwowalne zachowanie kodu - razem z dziwnymi regułami - zanim cokolwiek zmienimy. Oczekiwania zatwierdza człowiek, a nie liczy je ponownie ten sam algorytm.

**Zadanie:**
1. Zanim przeczytasz istniejący `S01CharacterizationTest.test.ts`, napisz własny test charakterystyki dla `BookingConfirmation.confirm`: pełny dokument dla co najmniej czterech rezerwacji.
2. Spraw, żeby test był deterministyczny, choć dokument zawiera bieżący czas i zależy od domyślnego locale (`toLocaleString(undefined, ...)`).
3. Wprowadź do `BookingConfirmation` minimalny szew, dzięki któremu test porówna także linię z czasem, i wykonaj jedną refaktoryzację pod ochroną testu.
4. Zapisz jedno znalezisko, które wygląda na błąd, ale którego nie poprawiasz.

**Podpowiedź:** zacznij od `expect(...).toBe('')` i skopiuj wynik z diffu Vitest. Szukaj progów w regułach i sprawdzaj obie strony każdego z nich. JavaScript nie ma `Locale.setDefault` - zastanów się, jak na czas jednego wywołania podmienić domyślne locale i jak je potem przywrócić.
**Gotowe, gdy:** Twój test i test sceny są zielone, dokument jest porównywany w całości, a wywołanie `new BookingConfirmation()` bez argumentu nadal działa.

## Scena s02. Break Dependencies - seam dla zadania przypomnień
**Katalog:** `typescript/src/workshop/m3/s02_breakdependencies/start` · **Test:** `scripts/warsztat.sh --lang ts test m3/s02`

**Zasada:** Break Dependencies tworzy seam, czyli miejsce, w którym można podstawić inną implementację zależności bez edycji algorytmu. Wybiera się najwęższy skuteczny seam, który nie zmienia czasu życia ani momentu tworzenia zależności.

**Zadanie:**
1. Uruchom `ShowtimeReminderJob.run()` w teście i zapisz, co stoi na przeszkodzie (są trzy przeszkody).
2. Usuń przeszkody po kolei, każdą osobnym ruchem, tak aby wywołanie `new ShowtimeReminderJob()` bez argumentów nadal składało te same implementacje produkcyjne.
3. Napisz test, który sprawdza wysłane przypomnienia i oznaczone rezerwacje dla seansu za 90, 120 i 121 minut.
4. Zachowaj moment, w którym powstaje połączenie z bazą.

**Podpowiedź:** najwęższy seam to często parametr konstruktora, `Clock` albo typ funkcyjny. Zastanów się, co by się stało, gdyby parametr domyślny konstruktora był gotową instancją `new LegacyDatabase()`, a nie fabryką.
**Gotowe, gdy:** Twój test jest zielony bez sieci i bazy, `ShowtimeReminderJob` nie ma metod `protected` dodanych tylko dla testu, a nowe typy nie mają metod, których zadanie nie używa.

## Scena s03. Wektor obserwowalnego zachowania - płatność za bilety
**Katalog:** `typescript/src/workshop/m3/s03_behaviourvector/start` · **Test:** `scripts/warsztat.sh --lang ts test m3/s03`

**Zasada:** Wektor obserwowalnego zachowania obejmuje wynik, wyjątki, stan, wywołania współpracowników z ich kolejnością, czas i granice. Test, który widzi tylko wynik, przepuści regresję w pozostałych wymiarach.

**Zadanie:**
1. Przeczytaj `S03ResultOnlyTest` i wypisz, czego ten test nie widzi.
2. Wprowadź seamy tak, aby test widział wysłane maile i obciążenia karty w jednej kolejności.
3. Napisz test, który wykrywa błąd w `TicketCheckout.pay`, a potem go napraw.
4. Dodaj do testu stan rezerwacji po operacji oraz klasę i komunikat błędu dla braku karty.

**Podpowiedź:** porównaj, co dostaje klient z kartą kończącą się na 0000, z tym, czego oczekujesz od kina.
**Gotowe, gdy:** Twój test jest czerwony dla kodu startowego i zielony po naprawie, a wynik metody `pay` w żadnym przypadku się nie zmienił.

## Scena s04. Równoległa weryfikacja (shadow) kalkulatora cen
**Katalog:** `typescript/src/workshop/m3/s04_parallelrun/start` · **Test:** `scripts/warsztat.sh --lang ts test m3/s04`

**Zasada:** Równoległa weryfikacja (shadow) liczy wynik starą i nową implementacją na tym samym ruchu, ale autorytatywny pozostaje wynik starej, a awaria nowej nie może dotknąć klienta. Zgodność obu implementacji nie dowodzi jeszcze poprawności.

**Zadanie:**
1. Uruchamiaj kandydata w cieniu starego kalkulatora - klient zawsze dostaje wynik legacy, a błąd rzucony przez kandydata nie może przerwać sprzedaży.
2. Zbieraj wynik porównania w raporcie, który odróżnia zgodność, rozbieżność i awarię kandydata.
3. Na podstawie raportu popraw kandydata.
4. Wprowadź jawny tryb migracji (legacy, cień, kandydat) i przełącz na kandydata.

**Podpowiedź:** przepuść przez usługę kilka zapytań rano i wieczorem, z różnymi typami biletów, oraz jedno z formatem 4DX. Wzorzec rozbieżności podpowie, gdzie jest błąd. Rodzaje wpisów raportu zapisz jako unię klas z polem `kind`.
**Gotowe, gdy:** klient dostaje tę samą cenę w każdym trybie, raport nie zawiera rozbieżności, a różnica dla 4DX jest świadomą, opisaną decyzją.

## Scena s05. Granice trybu shadow - efekty uboczne
**Katalog:** `typescript/src/workshop/m3/s05_shadowlimits/start` · **Test:** `scripts/warsztat.sh --lang ts test m3/s05`

**Zasada:** Tryb shadow jest bezpieczny tylko dla czystych obliczeń - efektów ubocznych (płatność, mail, zapis) nie wolno wykonać dwa razy. Efekt zapisany jako dana można porównać zamiast go wykonywać.

**Zadanie:**
1. Uruchom test i sprawdź w dzienniku `Infrastructure`, co dostaje klient po jednej rezerwacji w trybie shadow.
2. Spraw, żeby nowa ścieżka wykonywała efekty uboczne przez port, a nie bezpośrednio.
3. W cieniu nie wykonuj efektów kandydata, tylko porównaj je z efektami, które wykonało legacy.
4. Oddziel czyste obliczenie nowej ścieżki od wykonania efektów tak, żeby cień w ogóle nie miał dostępu do efektów.

**Podpowiedź:** efekt uboczny, który jest daną (opisem "co zrobić"), można porównać, zalogować albo wykonać.
**Gotowe, gdy:** po rezerwacji w dzienniku są tylko efekty legacy (jeden mail, jedno obciążenie, jeden zapis), a cień zgłasza rozbieżność w treści maila kandydata.
