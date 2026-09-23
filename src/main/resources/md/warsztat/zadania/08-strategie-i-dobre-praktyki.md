# Moduł 8. Strategie i dobre praktyki - warsztat CineLegacy: zadania

Każda scena ma pakiet `start` - na nim pracujesz. Po każdym ruchu uruchom test sceny (`scripts/warsztat.sh test m8/sNN`) i nie zaglądaj do katalogów `stepN` przed końcem zadania. Część testów opisuje stan startowy (np. "start wysyła dwa maile", "start narusza ADR") - gdy naprawisz `start`, zmienią kolor na czerwony, a to znaczy, że zadanie jest zrobione. Testy uruchamiaj z katalogu głównego repozytorium. Powrót do stanu wyjściowego: `scripts/warsztat.sh reset m8/sNN`.

## Scena s01. Branch by Abstraction na cenniku z CinemaManager
**Pakiet:** `pl.training.workshop.m8.s01_branchbyabstraction.start` · **Test:** `scripts/warsztat.sh test m8/s01`

**Zasada:** Branch by Abstraction wymienia implementację wewnątrz aplikacji bez długiej gałęzi w Git: stabilny kontrakt, za nim stara i nowa implementacja, a każdy krok da się od razu zintegrować. Wydzielenie kontraktu to jeszcze refaktoryzacja, przełączenie na nową implementację to już migracja.

**Zadanie:**
1. Wydziel wyliczenie ceny z `BookingService.confirm` do osobnej klasy starego cennika, bez poprawiania go.
2. Wprowadź abstrakcję `TicketPricing` zwracającą `Money` i schowaj za nią stary cennik.
3. Dopisz nową implementację obok starej i przełącznik wybierający implementację (domyślnie stara).
4. Usuń starą implementację i przełącznik.
**Podpowiedź:** po krokach 1 i 2 nic nie powinno się zmienić - to jeszcze refaktoryzacja. Konwersja `double` na `Money` tylko na granicy starego kodu.
**Gotowe, gdy:** test równoważności jest zielony po każdym kroku (w kroku 3 dla obu ustawień przełącznika), a na końcu w pakiecie nie ma ani starego cennika, ani przełącznika.

## Scena s02. Strangler Fig - fasada, która przejmuje ścieżki
**Pakiet:** `pl.training.workshop.m8.s02_stranglerfig.start` · **Test:** `scripts/warsztat.sh test m8/s02`

**Zasada:** Strangler Fig przejmuje system operacja po operacji: punkt na granicy systemu kieruje część wywołań do nowego kodu, a resztę dalej do starego, aż stary kod przestaje być potrzebny i można go usunąć.

**Zadanie:**
1. Postaw przed `LegacyCinema` fasadę implementującą `CinemaApi`, która deleguje wszystko 1:1.
2. Przenieś rezerwację do nowego modułu, a raport zostaw w legacy.
3. Przenieś raport do nowego modułu.
4. Usuń `LegacyCinema`.
**Podpowiedź:** nowy moduł rezerwacji musi pisać do `BookingLedger` w tym samym formacie co legacy, inaczej raport legacy się rozjedzie. Dodaj do fasady metodę opisującą, kto obsługuje którą operację.
**Gotowe, gdy:** scenariusz klienta w teście równoważności daje ten sam wynik na każdym etapie, a na końcu nie ma już klasy `LegacyCinema`.

## Scena s03. Równoległa weryfikacja (shadow) kalkulatora cen
**Pakiet:** `pl.training.workshop.m8.s03_parallelrun.start` · **Test:** `scripts/warsztat.sh test m8/s03`

**Zasada:** Równoległa weryfikacja (shadow) liczy wynik starą i nową implementacją na tym samym ruchu, ale autorytatywny pozostaje wynik starej, a awaria nowej nie może dotknąć klienta. Zgodność obu implementacji nie dowodzi jeszcze poprawności.

**Zadanie:**
1. Uruchamiaj kandydata w cieniu starego kalkulatora - klient zawsze dostaje wynik legacy, a wyjątek kandydata nie może przerwać sprzedaży.
2. Zbieraj wynik porównania w raporcie, który odróżnia zgodność, rozbieżność i awarię kandydata.
3. Na podstawie raportu popraw kandydata.
4. Wprowadź jawny tryb migracji (legacy, cień, kandydat) i przełącz na kandydata.
**Podpowiedź:** przepuść przez usługę kilka zapytań rano i wieczorem, z różnymi typami biletów, oraz jedno z formatem 4DX. Wzorzec rozbieżności podpowie, gdzie jest błąd.
**Gotowe, gdy:** klient dostaje tę samą cenę w każdym trybie, raport nie zawiera rozbieżności, a różnica dla 4DX jest świadomą, opisaną decyzją.

## Scena s04. Granice trybu shadow - efekty uboczne
**Pakiet:** `pl.training.workshop.m8.s04_shadowlimits.start` · **Test:** `scripts/warsztat.sh test m8/s04`

**Zasada:** Tryb shadow jest bezpieczny tylko dla czystych obliczeń - efektów ubocznych (płatność, mail, zapis) nie wolno wykonać dwa razy. Efekt zapisany jako dana można porównać zamiast go wykonywać.

**Zadanie:**
1. Uruchom test i sprawdź w dzienniku `Infrastructure`, co dostaje klient po jednej rezerwacji w trybie shadow.
2. Spraw, żeby nowa ścieżka wykonywała efekty uboczne przez port, a nie bezpośrednio.
3. W cieniu nie wykonuj efektów kandydata, tylko porównaj je z efektami, które wykonało legacy.
4. Oddziel czyste obliczenie nowej ścieżki od wykonania efektów tak, żeby cień w ogóle nie miał dostępu do efektów.
**Podpowiedź:** efekt uboczny, który jest daną (opisem "co zrobić"), można porównać, zalogować albo wykonać.
**Gotowe, gdy:** po rezerwacji w dzienniku są tylko efekty legacy (jeden mail, jedno obciążenie, jeden zapis), a cień zgłasza rozbieżność w treści maila kandydata.

## Scena s05. Boy Scout Rule - mała poprawa i jej nadużycie
**Pakiet:** `pl.training.workshop.m8.s05_boyscout.start` · **Test:** `scripts/warsztat.sh test m8/s05`

**Zasada:** Boy Scout Rule: dotykany kod zostaw w nieco lepszym stanie, ale poprawa ma być mała, lokalna i bez zmiany zachowania. Zmiana tego, co widzi użytkownik, to osobna decyzja, a nie sprzątanie.

**Zadanie:**
1. Popraw czytelność `TicketPrinter.print`: nazwy, sklejanie listy miejsc, powtórzenia.
2. Nie zmieniaj niczego, co widzi klient: kolejności miejsc, wielkości liter, linii z telefonem, formatu kwoty.
3. Zapisz osobno pomysły, które zmieniłyby zachowanie - to zadania na inny commit.
**Podpowiedź:** jeśli test równoważności zrobi się czerwony, twoje "sprzątanie" zmieniło zachowanie. Sprawdź przypadek z miejscami A9 i A10.
**Gotowe, gdy:** test równoważności jest zielony, diff dotyczy tylko jednej metody i da się go przejrzeć w minutę.

## Scena s06. Seria małych zmian gotowych do przeglądu
**Pakiet:** `pl.training.workshop.m8.s06_reviewableseries.start` · **Test:** `scripts/warsztat.sh test m8/s06`

**Zasada:** Dobry zestaw zmian ma jedną intencję, własny dowód i zielony build. Refaktoryzacji nie łączy się ze zmianą zachowania - najpierw ułatw zmianę, potem zrób łatwą zmianę.

**Zadanie:**
1. Dodaj regułę "Tani wtorek": bilet NORMAL -20% ceny bazowej we wtorek.
2. Podziel pracę na co najmniej trzy kroki (commity): refaktoryzacje bez zmiany zachowania osobno, nowa reguła osobno.
3. Do każdego kroku napisz jednozdaniowy opis: intencja i dowód.
**Podpowiedź:** najpierw spraw, żeby wybór zniżki miał dostęp do daty seansu - bez zmiany zachowania. Potem nowa reguła to kilka linii.
**Gotowe, gdy:** po krokach refaktoryzacyjnych test równoważności jest zielony, a po ostatnim 2D NORMAL we wtorek wieczorem kosztuje 20.00 i zmieniły się wyłącznie bilety NORMAL we wtorek.

## Scena s07. ADR i wykonywalny model decyzji
**Pakiet:** `pl.training.workshop.m8.s07_adr.start` · **Test:** `scripts/warsztat.sh test m8/s07`

**Zasada:** ADR zapisuje trwałą decyzję architektoniczną z kontekstem i konsekwencjami; zaakceptowanego ADR się nie edytuje, tylko zastępuje nowym. Reguły z ADR można sprawdzać testem, żeby build pilnował decyzji.

**Zadanie:**
1. Przeczytaj `ADR-0007-cennik-jako-czysty-modul.md` i sprawdź w teście, które reguły narusza `start`.
2. Usuń zależność pakietu `pricing` od `notification` bez zmiany wysyłanych maili.
3. Zamień kwoty w pakiecie `pricing` na `Money`.
**Podpowiedź:** cennik nie musi wysyłać maila - wystarczy, że powie, czy rabat grupowy został przyznany.
**Gotowe, gdy:** `ArchitectureRules.violations` dla `start` zwraca pustą listę, a odpowiedzi i maile są takie same jak przed zmianą.

## Scena s08. Bramka kompilatora -Xlint:all -Werror
**Pakiet:** `pl.training.workshop.m8.s08_compilergate.start` · **Test:** `scripts/warsztat.sh test m8/s08`

**Zasada:** Bramka kompilatora (`-Xlint:all -Werror`) zamienia ostrzeżenia w błędy builda. Ostrzeżenia usuwa się zmianą kodu, po jednej kategorii na krok, a tłumienie jest wyjątkiem wąskim i uzasadnionym.

**Zadanie:**
1. Sprawdź, jakie kategorie ostrzeżeń zgłasza `CompilerGate` dla `start`.
2. Usuwaj je po jednej kategorii na krok, uruchamiając test po każdym kroku.
3. Zachowaj zachowanie raportu, w tym to, że IMAX ma też dźwięk Dolby.
**Podpowiedź:** nie używaj `@SuppressWarnings` - każde ostrzeżenie da się tu usunąć zmianą kodu.
**Gotowe, gdy:** `CompilerGate.check` dla `start` zwraca zero ostrzeżeń i `passed() == true`, a test równoważności raportu jest zielony.

## Scena s09. Codemod na Compiler Tree API
**Pakiet:** `pl.training.workshop.m8.s09_codemod.start` · **Test:** `scripts/warsztat.sh test m8/s09`

**Zasada:** Codemod to automatyczna transformacja wielu miejsc naraz; oparty na drzewie składni i typach jest bezpieczniejszy od wyrażeń regularnych, ale automatyzacja zwiększa też zasięg błędu. Dobra receptura ma test przed i po oraz jest idempotentna.

**Zadanie:**
1. Zastąp wyszukiwanie wyrażeniem regularnym wyszukiwaniem po drzewie składni (`JavacTask`, `TreeScanner`).
2. Przepisuj tylko dwa ostatnie argumenty wywołania według pozycji z drzewa i dopisz brakujące importy.
3. Migruj wyłącznie wywołania przestarzałej metody `cinema.BookingService.book` - użyj informacji o typach.
**Podpowiedź:** `SampleProject.TICKET_DESK` ma wywołanie w komentarzu, wywołanie rozbite na trzy linie i podobne wywołanie w `HotelService`. Sprawdź też, co się stanie po drugim uruchomieniu codemodu.
**Gotowe, gdy:** codemod znajduje linie 11 i 16, wynik kompiluje się bez ostrzeżeń razem z `SampleProject.API`, a drugie uruchomienie niczego nie zmienia.

## Scena s10. Minimalna bramka jakości jako kod
**Pakiet:** `pl.training.workshop.m8.s10_qualitygate.start` · **Test:** `scripts/warsztat.sh test m8/s10`

**Zasada:** Minimalna bramka jakości to kilka niezależnych, wykonywalnych sprawdzeń z deterministycznym wynikiem, który wskazuje miejsce problemu. Bramka, która nic nie sprawdza albo daje fałszywe alarmy, jest gorsza niż jej brak.

**Zadanie:**
1. Zamień listę kontrolną z Javadocu `QualityGate` na wykonywalne sprawdzenia, po jednym na krok.
2. Każdy wynik ma wskazywać plik i linię albo metodę.
3. Czysta próbka (`sample/clean`) musi przejść bramkę bez żadnego wyniku.
**Podpowiedź:** zacznij od najtańszego sprawdzenia (skan tekstu źródeł), kompilator i uruchamianie testów zostaw na koniec.
**Gotowe, gdy:** bramka zgłasza wszystkie problemy próbki `sample/dirty`, zatrzymuje przypadek z nieprzechodzącym testem (`sample/broken`) i przepuszcza `sample/clean`.

## Scena s11. Jawna polityka wdrożenia etapowego
**Pakiet:** `pl.training.workshop.m8.s11_stagedrollout.start` · **Test:** `scripts/warsztat.sh test m8/s11`

**Zasada:** Wdrożenie etapowe to jawna, testowana polityka: kto dostaje nową ścieżkę, jaka część ruchu i jak ją natychmiast wyłączyć. Przydział klienta musi być stabilny, żeby nie przeskakiwał między ścieżkami.

**Zadanie:**
1. Zamień stałą i listę testerów na jawny obiekt polityki przekazywany do routera.
2. Dodaj wdrożenie procentowe z deterministycznym przydziałem klienta.
3. Dodaj wyłącznik awaryjny.
4. Dopisz testy polityki: stabilność, zwiększanie procentu, wyjątki, wyłącznik.
**Podpowiedź:** przydział ma być taki sam po restarcie i na każdej maszynie - nie losuj i nie polegaj na `hashCode` obiektu.
**Gotowe, gdy:** z dotychczasowymi ustawieniami router kieruje klientów tak samo jak `start`, a polityka przechodzi twoje testy.

## Scena s12. Expand and contract - format danych rezerwacji
**Pakiet:** `pl.training.workshop.m8.s12_expandcontract.start` · **Test:** `scripts/warsztat.sh test m8/s12`

**Zasada:** Expand and contract zmienia format danych w krokach tak, żeby w oknie wycofania stara wersja kodu zawsze czytała dane zapisane przez nową. Stary format usuwa się dopiero na końcu, gdy powrót nie będzie już potrzebny.

**Zadanie:**
1. Wprowadź nowy, wersjonowany format rezerwacji w kolumnie `payload` tak, żeby wycofanie do starej wersji było bezpieczne.
2. Czytaj nowy format, ale nie gub wierszy zapisanych wcześniej.
3. Uzupełnij nowy format w starych wierszach.
4. Usuń stary format z repozytorium.
**Podpowiedź:** po każdym kroku zadaj pytanie: czy repozytorium w wersji `start` przeczyta dane zapisane przez nową wersję?
**Gotowe, gdy:** rezerwacja zapisana i odczytana wraca bez zmian, stare wiersze są czytelne po migracji, a w końcowej wersji nie ma żadnego odwołania do starego formatu.

## Scena s13. Dokumentacja żywa kontra historyczna
**Pakiet:** `pl.training.workshop.m8.s13_livingdocs.start` · **Test:** `scripts/warsztat.sh test m8/s13`

**Zasada:** Dokumentacja żywa opisuje stan obecny i powinna powstawać z kodu albo być z nim sprawdzana, a historyczna (np. ADR) jest niezmienna. Ręcznie utrzymywany opis stanu systemu prędzej czy później się rozjeżdża.

**Zadanie:**
1. Sprawdź, czym `start/ROUTING.md` różni się od `Routing.routes()`.
2. Spraw, żeby dokument powstawał z kodu, i dopisz test, który wykryje jego ręczną edycję albo zestarzenie.
3. Dodaj do dokumentu właściciela i kryterium usunięcia każdej trasy do legacy.
**Podpowiedź:** ścieżkę dokumentu wylicz z pakietu klasy - wtedy generator zadziała też po skopiowaniu sceny.
**Gotowe, gdy:** zapisany `ROUTING.md` jest identyczny z wygenerowanym, a każda trasa do legacy ma właściciela i kryterium usunięcia.
