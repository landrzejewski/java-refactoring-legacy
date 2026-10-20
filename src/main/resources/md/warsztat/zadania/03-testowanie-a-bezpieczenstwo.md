# Moduł 3. Testowanie a bezpieczeństwo - warsztat CineLegacy: zadania

Każda scena ma pakiet `start` z kodem wyjściowym i test, który na starcie jest zielony (wyjątek: w s02 kodu startowego nie da się uruchomić w teście - to część zadania). Pracuj tylko w `start`. Po każdym ruchu (jedna technika naraz) uruchom test sceny, na przykład `scripts/warsztat.sh test m3/s02`, albo ⌃R w IntelliJ na klasie testu. Test ma być zielony po każdym kroku, a nie dopiero na końcu. Nie zaglądaj do pakietów `stepN`, dopóki nie skończysz, bo to gotowe rozwiązania. Jeśli się zgubisz, `scripts/warsztat.sh reset m3/s02` przywraca `start`. Testy uruchamiaj z katalogu głównego repozytorium.

Przy każdej scenie zadaj sobie pytanie ze slajdu "Wektor obserwowalnego zachowania": co obserwuje test - wynik, wyjątek, stan, efekty uboczne, ich kolejność? Tam, gdzie zadanie mówi o **zmianie zachowania**, nazwij ją wprost i zrób ją jako osobny krok. Dopiero wtedy wolno zmienić oczekiwania w teście.

W s04 i s05 część testów opisuje stan startowy (np. "start obciąża kartę dwa razy i wysyła dwa maile") - gdy naprawisz `start`, zmienią kolor na czerwony, a to znaczy, że zadanie jest zrobione.

Punkt odniesienia: `CinemaManagerGoldenMasterTest` (`mvn -q test -Dtest=CinemaManagerGoldenMasterTest`) chroni cały stary system jednym testem porównującym pełny wektor zachowania z plikiem `src/test/resources/workshop/cinema-manager.approved.txt`. Zanim zaczniesz, otwórz ten plik i zapisz, które wymiary wektora w nim widać.

## Scena s01. Test charakterystyki przed pierwszą zmianą
**Pakiet:** `pl.training.workshop.m3.s01_characterization.start` · **Test:** `scripts/warsztat.sh test m3/s01`

**Zasada:** Test charakterystyki zapisuje obecne, obserwowalne zachowanie kodu - razem z dziwnymi regułami - zanim cokolwiek zmienimy. Oczekiwania zatwierdza człowiek, a nie liczy je ponownie ten sam algorytm.

**Zadanie:**
1. Zanim przeczytasz istniejący `S01CharacterizationTest`, napisz własny test charakterystyki dla `BookingConfirmation.confirm`: pełny dokument dla co najmniej czterech rezerwacji.
2. Spraw, żeby test był deterministyczny, choć dokument zawiera bieżący czas i zależy od `Locale`.
3. Wprowadź do `BookingConfirmation` minimalny szew, dzięki któremu test porówna także linię z czasem, i wykonaj jedną refaktoryzację pod ochroną testu.
4. Zapisz jedno znalezisko, które wygląda na błąd, ale którego nie poprawiasz.

**Podpowiedź:** zacznij od `assertEquals("", ...)` i skopiuj wynik z komunikatu błędu. Szukaj progów w regułach i sprawdzaj obie strony każdego z nich.
**Gotowe, gdy:** Twój test i test sceny są zielone, dokument jest porównywany w całości, a stary konstruktor bezargumentowy nadal istnieje.

## Scena s02. Break Dependencies - seam dla zadania przypomnień
**Pakiet:** `pl.training.workshop.m3.s02_breakdependencies.start` · **Test:** `scripts/warsztat.sh test m3/s02`

**Zasada:** Break Dependencies tworzy seam, czyli miejsce, w którym można podstawić inną implementację zależności bez edycji algorytmu. Wybiera się najwęższy skuteczny seam, który nie zmienia czasu życia ani momentu tworzenia zależności.

**Zadanie:**
1. Uruchom `ShowtimeReminderJob.run()` w teście i zapisz, co stoi na przeszkodzie (są trzy przeszkody).
2. Usuń przeszkody po kolei, każdą osobnym ruchem, tak aby produkcyjny konstruktor bezargumentowy nadal składał te same implementacje.
3. Napisz test, który sprawdza wysłane przypomnienia i oznaczone rezerwacje dla seansu za 90, 120 i 121 minut.
4. Zachowaj moment, w którym powstaje połączenie z bazą.

**Podpowiedź:** najwęższy seam to często parametr konstruktora, `Clock` albo interfejs funkcyjny. Zastanów się, co by się stało, gdyby domyślny konstruktor zrobił `new LegacyDatabase()` od razu.
**Gotowe, gdy:** Twój test jest zielony bez sieci i bazy, `ShowtimeReminderJob` jest `final`, a nowe interfejsy nie mają metod, których zadanie nie używa.

## Scena s03. Wektor obserwowalnego zachowania - płatność za bilety
**Pakiet:** `pl.training.workshop.m3.s03_behaviourvector.start` · **Test:** `scripts/warsztat.sh test m3/s03`

**Zasada:** Wektor obserwowalnego zachowania obejmuje wynik, wyjątki, stan, wywołania współpracowników z ich kolejnością, czas i granice. Test, który widzi tylko wynik, przepuści regresję w pozostałych wymiarach.

**Zadanie:**
1. Przeczytaj `S03ResultOnlyTest` i wypisz, czego ten test nie widzi.
2. Wprowadź seamy tak, aby test widział wysłane maile i obciążenia karty w jednej kolejności.
3. Napisz test, który wykrywa błąd w `TicketCheckout.pay`, a potem go napraw.
4. Dodaj do testu stan rezerwacji po operacji oraz typ i komunikat wyjątku dla braku karty.

**Podpowiedź:** porównaj, co dostaje klient z kartą kończącą się na 0000, z tym, czego oczekujesz od kina.
**Gotowe, gdy:** Twój test jest czerwony dla kodu startowego i zielony po naprawie, a wynik metody `pay` w żadnym przypadku się nie zmienił.

## Scena s04. Równoległa weryfikacja (shadow) kalkulatora cen
**Pakiet:** `pl.training.workshop.m3.s04_parallelrun.start` · **Test:** `scripts/warsztat.sh test m3/s04`

**Zasada:** Równoległa weryfikacja (shadow) liczy wynik starą i nową implementacją na tym samym ruchu, ale autorytatywny pozostaje wynik starej, a awaria nowej nie może dotknąć klienta. Zgodność obu implementacji nie dowodzi jeszcze poprawności.

**Zadanie:**
1. Uruchamiaj kandydata w cieniu starego kalkulatora - klient zawsze dostaje wynik legacy, a wyjątek kandydata nie może przerwać sprzedaży.
2. Zbieraj wynik porównania w raporcie, który odróżnia zgodność, rozbieżność i awarię kandydata.
3. Na podstawie raportu popraw kandydata.
4. Wprowadź jawny tryb migracji (legacy, cień, kandydat) i przełącz na kandydata.

**Podpowiedź:** przepuść przez usługę kilka zapytań rano i wieczorem, z różnymi typami biletów, oraz jedno z formatem 4DX. Wzorzec rozbieżności podpowie, gdzie jest błąd.
**Gotowe, gdy:** klient dostaje tę samą cenę w każdym trybie, raport nie zawiera rozbieżności, a różnica dla 4DX jest świadomą, opisaną decyzją.

## Scena s05. Granice trybu shadow - efekty uboczne
**Pakiet:** `pl.training.workshop.m3.s05_shadowlimits.start` · **Test:** `scripts/warsztat.sh test m3/s05`

**Zasada:** Tryb shadow jest bezpieczny tylko dla czystych obliczeń - efektów ubocznych (płatność, mail, zapis) nie wolno wykonać dwa razy. Efekt zapisany jako dana można porównać zamiast go wykonywać.

**Zadanie:**
1. Uruchom test i sprawdź w dzienniku `Infrastructure`, co dostaje klient po jednej rezerwacji w trybie shadow.
2. Spraw, żeby nowa ścieżka wykonywała efekty uboczne przez port, a nie bezpośrednio.
3. W cieniu nie wykonuj efektów kandydata, tylko porównaj je z efektami, które wykonało legacy.
4. Oddziel czyste obliczenie nowej ścieżki od wykonania efektów tak, żeby cień w ogóle nie miał dostępu do efektów.

**Podpowiedź:** efekt uboczny, który jest daną (opisem "co zrobić"), można porównać, zalogować albo wykonać.
**Gotowe, gdy:** po rezerwacji w dzienniku są tylko efekty legacy (jeden mail, jedno obciążenie, jeden zapis), a cień zgłasza rozbieżność w treści maila kandydata.
