# Moduł 6. Refaktoryzacje do wzorców projektowych - warsztat CineLegacy: zadania

Każda scena to mały fragment systemu kina CineLegacy z zapachem, który prowadzi do wzorca projektowego. Pracujesz w pakiecie `start` danej sceny, np. `pl.training.workshop.m6.s08_state.start`. Test sceny przechodzi przez `start` i gotowe snapshoty `stepN` - Twoja wersja jest poprawna, gdy test jest zielony, a kod spełnia warunek projektowy z zadania.

Zasady pracy:

- Najpierw uruchom test i przeczytaj go - to on opisuje kontrakt (wyjątki, kolejność efektów, format wyniku). Nie zmieniaj testów.
- Małe kroki: jeden ruch w IDE, test, następny ruch. Wzorzec wprowadzaj stopniowo, nie przepisuj klasy od zera.
- Nie zaglądaj do pakietów `stepN` przed końcem - to rozwiązania prowadzącego.
- Test woła scenę przez klienta (np. `PriceBoard`, `CancellationDesk`, `PaymentServices`) - gdy zmieniasz sygnatury, popraw też klienta, a nie test.
- Wprowadzenie wzorca to nadal refaktoryzacja. Jeśli chcesz zmienić zachowanie (nowa reguła, inna kolejność, inny komunikat), zapisz to jako osobną propozycję.

```bash
scripts/warsztat.sh list m6          # sceny i kroki
scripts/warsztat.sh test m6/s08      # testy sceny (wszystkie warianty)
scripts/warsztat.sh reset m6/s08     # przywrócenie start do wersji z repozytorium
```

## Scena s01. Replace Conditional Logic with Strategy - polityka zniżek

**Pakiet:** `pl.training.workshop.m6.s01_strategy.start` · **Test:** `scripts/warsztat.sh test m6/s01`
**Zadanie:**
1. Wprowadź interfejs `DiscountPolicy` i najpierw użyj go jako strategii przejściowej delegującej do starego kodu.
2. Przenieś każdy program zniżek (STANDARD, STUDENT_WEEK, PREMIERE) do osobnej strategii, po jednym.
3. Zastanów się, gdzie powinien zapaść wybór programu i co się zmieni, jeśli przeniesiesz go do konstruktora.

**Podpowiedź:** walidacja ceny i programu jest wspólna - niech zostanie w kontekście. Sprawdź w teście przypadek "PREMIERE nie sprawdza typu".
**Gotowe, gdy:** test zielony, `TicketPricer` nie zawiera łańcucha `if` po nazwie programu, a strategie są bezstanowe.

## Scena s02. Replace Conditional with Polymorphism - rodzaj seansu

**Pakiet:** `pl.training.workshop.m6.s02_polymorphism.start` · **Test:** `scripts/warsztat.sh test m6/s02`
**Zadanie:**
1. Znajdź miejsce tworzenia obiektów `Screening`.
2. Wydziel podtyp dla jednego rodzaju seansu, uruchom test, potem kolejne.
3. Usuń pole `kind` i nadaj każdemu rodzajowi dane o jednoznacznej nazwie zamiast `value`.

**Podpowiedź:** `switch` w miejscu tworzenia może zostać. Znikają te w `label`, `durationMinutes` i `price`.
**Gotowe, gdy:** test zielony, zachowanie rodzajów jest w podtypach, a nieznany rodzaj w danych nadal daje ten sam wyjątek.

## Scena s03. Replace Type Code with Class - format jako typ

**Pakiet:** `pl.training.workshop.m6.s03_typecode.start` · **Test:** `scripts/warsztat.sh test m6/s03`
**Zadanie:**
1. Zastąp `int formatCode` typem `Format`; zamieniaj kod na typ tuż po odczycie CSV.
2. Przenieś zachowanie zależne od formatu (etykieta, cena, okulary) do typu.
3. Wydziel mapowanie kodu trwałego do osobnego mappera.

**Podpowiedź:** plik CSV musi nadal zawierać `Diuna;3`. Nie używaj `ordinal()`.
**Gotowe, gdy:** test zielony, żaden kod poza mapperem nie operuje na liczbach 1, 2, 3, a zapis CSV jest bez zmian.

## Scena s04. Encapsulate Classes with Factory - bilety

**Pakiet:** `pl.training.workshop.m6.s04_encapsulatefactory.start` · **Test:** `scripts/warsztat.sh test m6/s04`
**Zadanie:**
1. Zastąp każde `new StandardTicket/VipTicket` w `BoxOffice` metodą tworzącą.
2. Przenieś regułę "który bilet dla którego rzędu" do fabryki.
3. Ogranicz widoczność klas konkretnych biletów.

**Podpowiedź:** kompilator pokaże wszystkie użycia klas konkretnych, gdy odbierzesz im `public`.
**Gotowe, gdy:** test zielony (także `S04SolutionTest` sprawdzający widoczność w rozwiązaniu wzorcowym), a `BoxOffice` importuje tylko `Ticket` i fabrykę.

## Scena s05. Extract Factory Class - tworzenie rezerwacji

**Pakiet:** `pl.training.workshop.m6.s05_extractfactory.start` · **Test:** `scripts/warsztat.sh test m6/s05`
**Zadanie:**
1. Usuń duplikację tworzenia rezerwacji między `reserve` i `reserveGroup`.
2. Wydziel klasę fabryki odpowiedzialną za numer, opłatę i termin ważności.
3. Przekaż fabrykę do serwisu jako zależność.

**Podpowiedź:** przeczytaj przypadek testowy o "spalonym" numerze - kolejność operacji jest częścią kontraktu.
**Gotowe, gdy:** test zielony, serwis nie zawiera reguł numeracji, opłat ani terminu ważności, a fabrykę da się przetestować osobno.

## Scena s06. Encapsulate Composite with Builder - repertuar dnia

**Pakiet:** `pl.training.workshop.m6.s06_builder.start` · **Test:** `scripts/warsztat.sh test m6/s06`
**Zadanie:**
1. Napisz builder repertuaru, dzięki któremu `WeekendPlanner` nie używa `new Hall` ani `add`.
2. Uczyń drzewo niemutowalnym po zbudowaniu.
3. Zdecyduj, czy builder może być użyty ponownie i co ma się stać przy drugim `build()`.

**Podpowiedź:** porównaj builder z "bieżącą salą" i builder z lambdą dla każdej sali - który mniej ukrywa?
**Gotowe, gdy:** test zielony, kształt kodu planera odpowiada kształtowi repertuaru, a zbudowanych list nie da się zmodyfikować.

## Scena s07. Move Embellishment to Decorator - dodatki do biletu

**Pakiet:** `pl.training.workshop.m6.s07_decorator.start` · **Test:** `scripts/warsztat.sh test m6/s07`
**Zadanie:**
1. Wydziel wspólny interfejs biletu i miejsce składania biletu.
2. Przenoś dodatki (VIP, okulary 3D, ubezpieczenie) do dekoratorów po jednym, usuwając flagi z rdzenia.
3. Sprawdź, co zwraca `instanceof` i `equals` dla udekorowanego biletu.

**Podpowiedź:** opis biletu ma ustaloną kolejność dodatków. Od którego dodatku zacząć, żeby jej nie zmienić?
**Gotowe, gdy:** test zielony, rdzeń biletu nie ma żadnej flagi dodatku, a kolejność owijania jest w jednym miejscu.

## Scena s08. Replace State-Altering Conditionals with State - status rezerwacji

**Pakiet:** `pl.training.workshop.m6.s08_state.start` · **Test:** `scripts/warsztat.sh test m6/s08`
**Zadanie:**
1. Przeczytaj tabelę przejść w teście i porównaj ją z kodem.
2. Zastąp pole `status` obiektem stanu.
3. Przenoś przejścia (`pay`, `use`, `expire`, `cancel`) do stanów po jednym; niedozwolone przejście ma rzucać ten sam wyjątek.

**Podpowiedź:** gdy bramka płatności zawiedzie, rezerwacja zostaje NEW. Zachowaj kolejność: obciążenie, zmiana stanu, efekt.
**Gotowe, gdy:** wszystkie 84 testy zielone, a metody `Reservation` tylko delegują do stanu.

## Scena s09. Replace Hard-coded Notifications with Observer - po opłaceniu

**Pakiet:** `pl.training.workshop.m6.s09_observer.start` · **Test:** `scripts/warsztat.sh test m6/s09`
**Zadanie:**
1. Wprowadź zdarzenie "rezerwacja opłacona" i jedno miejsce powiadamiania.
2. Zamień maila, SMS i punkty lojalnościowe na obserwatorów wspólnego interfejsu.
3. Umożliw rejestrowanie i wyrejestrowanie odbiorców, a składanie standardowego zestawu przenieś do `PaymentServices`.

**Podpowiedź:** wyjątek w SMS przerywa naliczanie punktów - to obecny kontrakt, nie błąd do naprawy w tej refaktoryzacji.
**Gotowe, gdy:** test zielony (kolejność i awarie), a serwis nie importuje `Mailer`, `SmsGateway` ani `LoyaltyProgram`.

## Scena s10. Replace Implicit Tree with Composite - zestawy baru

**Pakiet:** `pl.training.workshop.m6.s10_implicittree.start` · **Test:** `scripts/warsztat.sh test m6/s10`
**Zadanie:**
1. Zaprojektuj jawny Composite zestawu (produkt i zestaw) oraz mapper z zagnieżdżonych list.
2. Przenieś `price`, a potem `render` na Composite - po jednej operacji.
3. Usuń stary kod z `instanceof`.

**Podpowiedź:** komunikaty błędów dla złych danych też są kontraktem. Test różnicowy porównuje wynik `start` z rozwiązaniem wzorcowym na 500 losowych drzewach.
**Gotowe, gdy:** testy równoważności i różnicowy zielone, a `BarMenu` nie zawiera `instanceof` ani rzutowań.

## Scena s11. Transparent vs Safe Composite - add() na liściu

**Pakiet:** `pl.training.workshop.m6.s11_safecomposite.start` · **Test:** `scripts/warsztat.sh test m6/s11`
**Zadanie:**
1. Spraw, żeby wywołanie `add` na produkcie było błędem kompilacji, a nie wyjątkiem w runtime.
2. Popraw typy w `ComboCatalog` tam, gdzie dodajesz elementy.
3. Rozważ wersję, w której zestaw jest niemutowalny i `add` nie istnieje wcale.

**Podpowiedź:** w IntelliJ poszukaj refaktoryzacji przenoszącej metody do podklasy.
**Gotowe, gdy:** test zielony, a wspólny typ `MenuComponent` nie ma metod `add` ani `children`.

## Scena s12. Replace One/Many Distinctions with Composite - zwroty

**Pakiet:** `pl.training.workshop.m6.s12_onemany.start` · **Test:** `scripts/warsztat.sh test m6/s12`
**Zadanie:**
1. Usuń duplikację reguły zwrotu między `refund` i `refundAll`.
2. Wprowadź wspólny kontrakt dla jednego biletu i grupy biletów oraz jedną metodę `refund`.
3. Oznacz stare metody jako przestarzałe, a potem usuń je.

**Podpowiedź:** potrącenie 3.00 jest naliczane raz na zwrot, nie raz na bilet.
**Gotowe, gdy:** test zielony, reguła zwrotu jest w jednym miejscu, a ani `RefundService`, ani klient `CancellationDesk` nie rozróżniają jednego biletu i wielu.

## Scena s13. Extract Composite - kontenery programu

**Pakiet:** `pl.training.workshop.m6.s13_extractcomposite.start` · **Test:** `scripts/warsztat.sh test m6/s13`
**Zadanie:**
1. Wydziel wspólną nadklasę dla `Marathon` i `ShortsBlock` z obsługą dzieci.
2. Podciągnij do niej to, co naprawdę jest wspólne; zostaw w podklasach to, co się różni.

**Podpowiedź:** obie klasy sumują minuty, ale tylko maraton dolicza przerwy.
**Gotowe, gdy:** test zielony, lista dzieci i `add` istnieją w jednym miejscu, a podklasy mają po kilkanaście linii.

## Scena s14. Unify Interfaces with Adapter - dwie bramki płatności

**Pakiet:** `pl.training.workshop.m6.s14_adapter.start` · **Test:** `scripts/warsztat.sh test m6/s14`
**Zadanie:**
1. Wydziel obsługę każdej bramki do metody o tej samej sygnaturze.
2. Zdefiniuj preferowany interfejs płatności kina i dwa adaptery.
3. Spraw, żeby `CheckoutService` zależał wyłącznie od tego interfejsu.

**Podpowiedź:** stara bramka liczy w groszach i zwraca odmowę w XML, nowa liczy w złotych i zgłasza odmowę wyjątkiem. Nie zmieniaj klas bramek.
**Gotowe, gdy:** test zielony, a metoda `pay` w `CheckoutService` korzysta wyłącznie z preferowanego interfejsu (szczegóły XML i REST są tylko w adapterach).

## Scena s15. Replace Conditional Dispatcher with Command - konsola kasjera

**Pakiet:** `pl.training.workshop.m6.s15_command.start` · **Test:** `scripts/warsztat.sh test m6/s15`
**Zadanie:**
1. Wydziel ciało każdej gałęzi do metody o wspólnej sygnaturze.
2. Zamień gałęzie na obiekty komend; stan kasy wydziel z konsoli.
3. Zastąp łańcuch `if` rejestrem komend.

**Podpowiedź:** `sell` i `SELL` działają tak samo - nie zgub normalizacji klucza.
**Gotowe, gdy:** test zielony, komendy są bezstanowe, a dodanie komendy nie wymaga zmiany metody `handle`.

## Scena s16. Form Template Method - raporty CSV i HTML

**Pakiet:** `pl.training.workshop.m6.s16_templatemethod.start` · **Test:** `scripts/warsztat.sh test m6/s16`
**Zadanie:**
1. Doprowadź `render` obu raportów do identycznej postaci, wydzielając różnice do metod o tych samych nazwach.
2. Przenieś wspólny szkielet do nadklasy i zabezpiecz go przed nadpisaniem.

**Podpowiedź:** różnice to nagłówek, wiersz (z escapowaniem) i stopka.
**Gotowe, gdy:** test zielony, sortowanie i sumowanie są w jednym miejscu, a raporty zawierają tylko formatowanie.

## Scena s17. Limit Instantiation with Singleton - cennik

**Pakiet:** `pl.training.workshop.m6.s17_singleton.start` · **Test:** `scripts/warsztat.sh test m6/s17`
**Zadanie:**
1. Sprawdź w teście, ile cenników powstaje przy trzech wycenach, i uzasadnij, dlaczego jedna instancja jest bezpieczna.
2. Ogranicz tworzenie cennika do jednej instancji.
3. Spraw, żeby `TicketDesk` dało się przetestować z innym cennikiem bez globalnego stanu.

**Podpowiedź:** porównaj klasyczne `getInstance()` z enumem, a potem zastanów się, kto powinien decydować o cyklu życia.
**Gotowe, gdy:** test zielony, cennik jest tworzony raz, a `TicketDesk` przyjmuje cennik przez konstruktor.

## Scena s18. Move Accumulation to Collecting Parameter - ostrzeżenia walidacji

**Pakiet:** `pl.training.workshop.m6.s18_collectingparameter.start` · **Test:** `scripts/warsztat.sh test m6/s18`
**Zadanie:**
1. Zastąp sklejanie ostrzeżeń w `String` listą.
2. Przekaż akumulator do metod pomocniczych zamiast zwracać z nich fragmenty.
3. Nadaj akumulatorowi wąski typ, który pozwala tylko dopisywać.

**Podpowiedź:** separator `"; "` powinien pojawiać się w kodzie dokładnie raz.
**Gotowe, gdy:** test zielony, metody `check...` zwracają `void`, a format wyniku jest ustalany w jednym miejscu.

## Scena s19. Visitor i macierz zmian - pozycje zamówienia

**Pakiet:** `pl.training.workshop.m6.s19_visitor.start` · **Test:** `scripts/warsztat.sh test m6/s19`
**Zadanie:**
1. Zamień trzy łańcuchy `instanceof` na klasyczny Visitor.
2. Przygotuj alternatywę: zamknięta hierarchia pozycji i `switch` po typach bez `default`.
3. Wypełnij macierz: ile plików zmienisz przy nowej operacji, a ile przy nowym rodzaju pozycji - w obu wersjach.

**Podpowiedź:** VAT liczymy od ceny brutto każdej pozycji osobno i dopiero potem sumujemy.
**Gotowe, gdy:** test zielony dla wybranej wersji, w kodzie nie ma `instanceof`, a dodanie typu pozycji bez obsługi kończy się błędem kompilacji.

## Scena s20. Mapa decyzji - jedna tabela, dwie struktury

**Pakiet:** `pl.training.workshop.m6.s20_decisionmap.start` · **Test:** `scripts/warsztat.sh test m6/s20`
**Zadanie:**
1. Rozplącz tabelę cen na dwie niezależne reguły.
2. Zaimplementuj wariant A: marketing często zmienia reguły dniowe.
3. Od stanu po punkcie 1 zaimplementuj wariant B: często dochodzą nowe formaty.
4. Zapisz jednym zdaniem, po czym poznasz w projekcie, który wariant wybrać.

**Podpowiedź:** zrób commit (albo kopię) po punkcie 1, żeby móc wrócić i zbudować drugi wariant.
**Gotowe, gdy:** test zielony dla obu wariantów, a uzasadnienie wyboru odwołuje się do tego, co się zmienia, nie do wyglądu kodu.
