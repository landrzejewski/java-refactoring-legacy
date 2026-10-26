# Moduł 5. Refaktoryzacja do wzorców - warsztat CineLegacy: zadania

Każda scena to mały fragment systemu kina CineLegacy z zapachem, który prowadzi do wzorca projektowego (albo do usunięcia wzorca, gdy przestał być potrzebny). Pracujesz w pakiecie `start` danej sceny, np. `pl.training.workshop.m5.s08_state.start`. Test sceny przechodzi przez `start` i gotowe snapshoty `stepN` - Twoja wersja jest poprawna, gdy test jest zielony, a kod spełnia warunek projektowy z zadania.

Zasady pracy:

- Najpierw uruchom test i przeczytaj go - to on opisuje kontrakt (wyjątki, kolejność efektów, format wyniku). Nie zmieniaj testów.
- Małe kroki: jeden ruch w IDE, test, następny ruch. Wzorzec wprowadzaj stopniowo, nie przepisuj klasy od zera.
- Nie zaglądaj do pakietów `stepN` przed końcem - to rozwiązania prowadzącego.
- Test woła scenę przez klienta albo korzeń kompozycji (np. `PriceBoard`, `CancellationDesk`, `PaymentServices`, `Reports`, `Cinema`, `PaymentModule`, `SettlementModule`) - gdy zmieniasz sygnatury, popraw też klienta, a nie test.
- Wprowadzenie wzorca to nadal refaktoryzacja. Jeśli chcesz zmienić zachowanie (nowa reguła, inna kolejność, inny komunikat), zapisz to jako osobną propozycję. W scenach s21, s23, s29, s30 i s31 część zmian zachowania jest zamierzona - opisują je osobne testy i to one mówią, która zmiana jest uzgodniona.

```bash
scripts/warsztat.sh list m5          # sceny i kroki
scripts/warsztat.sh test m5/s08      # testy sceny (wszystkie warianty)
scripts/warsztat.sh reset m5/s08     # przywrócenie start do wersji z repozytorium
```

## Scena s01. Replace Conditional Logic with Strategy - polityka zniżek

**Pakiet:** `pl.training.workshop.m5.s01_strategy.start` · **Test:** `scripts/warsztat.sh test m5/s01`

**Zasada:** Strategy wydziela wymienne warianty jednego obliczenia za wspólnym interfejsem, gdy wariant wybiera się niezależnie od klasy obiektu. Wspólna walidacja zostaje w kontekście, a moment wyboru wariantu jest częścią zachowania.

**Zadanie:**
1. Wprowadź interfejs `DiscountPolicy` i najpierw użyj go jako strategii przejściowej delegującej do starego kodu.
2. Przenieś każdy program zniżek (STANDARD, STUDENT_WEEK, PREMIERE) do osobnej strategii, po jednym.
3. Zastanów się, gdzie powinien zapaść wybór programu i co się zmieni, jeśli przeniesiesz go do konstruktora.

**Podpowiedź:** walidacja ceny i programu jest wspólna - niech zostanie w kontekście. Sprawdź w teście przypadek "PREMIERE nie sprawdza typu".
**Gotowe, gdy:** test zielony, `TicketPricer` nie zawiera łańcucha `if` po nazwie programu, a strategie są bezstanowe.

## Scena s02. Replace Conditional with Polymorphism - rodzaj seansu

**Pakiet:** `pl.training.workshop.m5.s02_polymorphism.start` · **Test:** `scripts/warsztat.sh test m5/s02`

**Zasada:** Polimorfizm podtypów przenosi zachowanie zależne od trwałego rodzaju obiektu do osobnych typów, zamiast powtarzać `switch` po polu rodzaju. Najpierw trzeba znaleźć wszystkie miejsca, w których obiekty powstają.

**Zadanie:**
1. Znajdź miejsce tworzenia obiektów `Screening`.
2. Wydziel podtyp dla jednego rodzaju seansu, uruchom test, potem kolejne.
3. Usuń pole `kind` i nadaj każdemu rodzajowi dane o jednoznacznej nazwie zamiast `value`.

**Podpowiedź:** `switch` w miejscu tworzenia może zostać. Znikają te w `label`, `durationMinutes` i `price`.
**Gotowe, gdy:** test zielony, zachowanie rodzajów jest w podtypach, a nieznany rodzaj w danych nadal daje ten sam wyjątek.

## Scena s03. Replace Type Code with Class - format jako typ

**Pakiet:** `pl.training.workshop.m5.s03_typecode.start` · **Test:** `scripts/warsztat.sh test m5/s03`

**Zasada:** Replace Type Code with Class zastępuje surowy kod typem, który sam pilnuje poprawnych wartości i zna operacje swojego pojęcia. Kod zapisywany trwale to osobna sprawa - musi pozostać stabilny niezależnie od kształtu typu.

**Zadanie:**
1. Zastąp `int formatCode` typem `Format`; zamieniaj kod na typ tuż po odczycie CSV.
2. Przenieś zachowanie zależne od formatu (etykieta, cena, okulary) do typu.
3. Wydziel mapowanie kodu trwałego do osobnego mappera.

**Podpowiedź:** plik CSV musi nadal zawierać `Diuna;3`. Nie używaj `ordinal()`.
**Gotowe, gdy:** test zielony, żaden kod poza mapperem nie operuje na liczbach 1, 2, 3, a zapis CSV jest bez zmian.

## Scena s04. Encapsulate Classes with Factory - bilety

**Pakiet:** `pl.training.workshop.m5.s04_encapsulatefactory.start` · **Test:** `scripts/warsztat.sh test m5/s04`

**Zasada:** Encapsulate Classes with Factory chowa klasy konkretne za fabryką, tak żeby klient znał tylko wspólny interfejs. Tworzenie przekierowuje się pojedynczo, a widoczność ogranicza dopiero wtedy, gdy wszyscy klienci już przeszli.

**Zadanie:**
1. Zastąp każde `new StandardTicket/VipTicket` w `BoxOffice` metodą tworzącą.
2. Przenieś regułę "który bilet dla którego rzędu" do fabryki.
3. Ogranicz widoczność klas konkretnych biletów.

**Podpowiedź:** kompilator pokaże wszystkie użycia klas konkretnych, gdy odbierzesz im `public`.
**Gotowe, gdy:** test zielony (także `S04SolutionTest` sprawdzający widoczność w rozwiązaniu wzorcowym), a `BoxOffice` importuje tylko `Ticket` i fabrykę.

## Scena s05. Extract Factory Class - tworzenie rezerwacji

**Pakiet:** `pl.training.workshop.m5.s05_extractfactory.start` · **Test:** `scripts/warsztat.sh test m5/s05`

**Zasada:** Extract Factory Class przenosi reguły tworzenia obiektu do osobnej klasy, gdy klasa, która je dziś zawiera, ma inną główną odpowiedzialność. Fabryka jest zwykłą zależnością, a kolejność operacji przy tworzeniu pozostaje częścią kontraktu.

**Zadanie:**
1. Usuń duplikację tworzenia rezerwacji między `reserve` i `reserveGroup`.
2. Wydziel klasę fabryki odpowiedzialną za numer, opłatę i termin ważności.
3. Przekaż fabrykę do serwisu jako zależność.

**Podpowiedź:** przeczytaj przypadek testowy o "spalonym" numerze - kolejność operacji jest częścią kontraktu.
**Gotowe, gdy:** test zielony, serwis nie zawiera reguł numeracji, opłat ani terminu ważności, a fabrykę da się przetestować osobno.

## Scena s06. Encapsulate Composite with Builder - repertuar dnia

**Pakiet:** `pl.training.workshop.m5.s06_builder.start` · **Test:** `scripts/warsztat.sh test m5/s06`

**Zasada:** Builder dla struktury złożonej ukrywa tworzenie i łączenie węzłów za API opisującym kształt wyniku. Opłaca się, gdy budowa jest wieloetapowa i ma własne reguły, a gotowy wynik nie powinien się już zmieniać.

**Zadanie:**
1. Napisz builder repertuaru, dzięki któremu `WeekendPlanner` nie używa `new Hall` ani `add`.
2. Uczyń drzewo niemutowalnym po zbudowaniu.
3. Zdecyduj, czy builder może być użyty ponownie i co ma się stać przy drugim `build()`.

**Podpowiedź:** porównaj builder z "bieżącą salą" i builder z lambdą dla każdej sali - który mniej ukrywa?
**Gotowe, gdy:** test zielony, kształt kodu planera odpowiada kształtowi repertuaru, a zbudowanych list nie da się zmodyfikować.

## Scena s07. Move Embellishment to Decorator - dodatki do biletu

**Pakiet:** `pl.training.workshop.m5.s07_decorator.start` · **Test:** `scripts/warsztat.sh test m5/s07`

**Zasada:** Decorator owija rdzeń obiektem o tym samym kontrakcie, który dokłada opcjonalne zachowanie i deleguje resztę do środka. Kolejność owijania jest częścią zachowania, a udekorowany obiekt nie jest przezroczysty dla pytań o typ i tożsamość.

**Zadanie:**
1. Wydziel wspólny interfejs biletu i miejsce składania biletu.
2. Przenoś dodatki (VIP, okulary 3D, ubezpieczenie) do dekoratorów po jednym, usuwając flagi z rdzenia.
3. Sprawdź, co zwraca `instanceof` i `equals` dla udekorowanego biletu.

**Podpowiedź:** opis biletu ma ustaloną kolejność dodatków. Od którego dodatku zacząć, żeby jej nie zmienić?
**Gotowe, gdy:** test zielony, rdzeń biletu nie ma żadnej flagi dodatku, a kolejność owijania jest w jednym miejscu.

## Scena s08. Replace State-Altering Conditionals with State - status rezerwacji

**Pakiet:** `pl.training.workshop.m5.s08_state.start` · **Test:** `scripts/warsztat.sh test m5/s08`

**Zasada:** State przenosi zachowanie zależne od bieżącego stanu do obiektów stanów, a kontekst tylko do nich deleguje. Tabelę przejść ustala się przed budową hierarchii, a niedozwolone przejście nadal musi kończyć się tym samym błędem.

**Zadanie:**
1. Przeczytaj tabelę przejść w teście i porównaj ją z kodem.
2. Zastąp pole `status` obiektem stanu.
3. Przenoś przejścia (`pay`, `use`, `expire`, `cancel`) do stanów po jednym; niedozwolone przejście ma rzucać ten sam wyjątek.

**Podpowiedź:** gdy bramka płatności zawiedzie, rezerwacja zostaje NEW. Zachowaj kolejność: obciążenie, zmiana stanu, efekt.
**Gotowe, gdy:** wszystkie 84 testy zielone, a metody `Reservation` tylko delegują do stanu.

## Scena s09. Replace Hard-coded Notifications with Observer - po opłaceniu

**Pakiet:** `pl.training.workshop.m5.s09_observer.start` · **Test:** `scripts/warsztat.sh test m5/s09`

**Zasada:** Observer sprawia, że źródło zdarzenia zna tylko wspólny interfejs odbiorców, a nie ich konkretne klasy. Kolejność powiadomień i polityka błędów to kontrakt, którego refaktoryzacja nie zmienia.

**Zadanie:**
1. Wprowadź zdarzenie "rezerwacja opłacona" i jedno miejsce powiadamiania.
2. Zamień maila, SMS i punkty lojalnościowe na obserwatorów wspólnego interfejsu.
3. Umożliw rejestrowanie i wyrejestrowanie odbiorców, a składanie standardowego zestawu przenieś do `PaymentServices`.

**Podpowiedź:** wyjątek w SMS przerywa naliczanie punktów - to obecny kontrakt, nie błąd do naprawy w tej refaktoryzacji.
**Gotowe, gdy:** test zielony (kolejność i awarie), a serwis nie importuje `Mailer`, `SmsGateway` ani `LoyaltyProgram`.

## Scena s10. Replace Implicit Tree with Composite - zestawy baru

**Pakiet:** `pl.training.workshop.m5.s10_implicittree.start` · **Test:** `scripts/warsztat.sh test m5/s10`

**Zasada:** Composite zastępuje drzewo ukryte w konwencji danych jawnymi typami liścia i węzła o wspólnym kontrakcie. Operacje przenosi się po jednej, a stary format tłumaczy mapper, który zachowuje dotychczasowe błędy.

**Zadanie:**
1. Zaprojektuj jawny Composite zestawu (produkt i zestaw) oraz mapper z zagnieżdżonych list.
2. Przenieś `price`, a potem `render` na Composite - po jednej operacji.
3. Usuń stary kod z `instanceof`.

**Podpowiedź:** komunikaty błędów dla złych danych też są kontraktem. Test różnicowy porównuje wynik `start` z rozwiązaniem wzorcowym na 500 losowych drzewach.
**Gotowe, gdy:** testy równoważności i różnicowy zielone, a `BarMenu` nie zawiera `instanceof` ani rzutowań.

## Scena s11. Transparent vs Safe Composite - add() na liściu

**Pakiet:** `pl.training.workshop.m5.s11_safecomposite.start` · **Test:** `scripts/warsztat.sh test m5/s11`

**Zasada:** Composite może trzymać zarządzanie dziećmi we wspólnym interfejsie (Transparent) albo tylko w węźle (Safe). Pierwszy wariant upraszcza klienta kosztem błędów w runtime, drugi przenosi te błędy do kompilacji.

**Zadanie:**
1. Spraw, żeby wywołanie `add` na produkcie było błędem kompilacji, a nie wyjątkiem w runtime.
2. Popraw typy w `ComboCatalog` tam, gdzie dodajesz elementy.
3. Rozważ wersję, w której zestaw jest niemutowalny i `add` nie istnieje wcale.

**Podpowiedź:** w IntelliJ poszukaj refaktoryzacji przenoszącej metody do podklasy.
**Gotowe, gdy:** test zielony, a wspólny typ `MenuComponent` nie ma metod `add` ani `children`.

## Scena s12. Replace One/Many Distinctions with Composite - zwroty

**Pakiet:** `pl.training.workshop.m5.s12_onemany.start` · **Test:** `scripts/warsztat.sh test m5/s12`

**Zasada:** Composite pozwala traktować jeden element i grupę elementów przez ten sam kontrakt, dzięki czemu API nie potrzebuje osobnych ścieżek "jeden" i "wiele". Efekty naliczane raz na wywołanie muszą przy tym pozostać naliczane raz.

**Zadanie:**
1. Usuń duplikację reguły zwrotu między `refund` i `refundAll`.
2. Wprowadź wspólny kontrakt dla jednego biletu i grupy biletów oraz jedną metodę `refund`.
3. Oznacz stare metody jako przestarzałe, a potem usuń je.

**Podpowiedź:** potrącenie 3.00 jest naliczane raz na zwrot, nie raz na bilet.
**Gotowe, gdy:** test zielony, reguła zwrotu jest w jednym miejscu, a ani `RefundService`, ani klient `CancellationDesk` nie rozróżniają jednego biletu i wielu.

## Scena s13. Extract Composite - kontenery programu

**Pakiet:** `pl.training.workshop.m5.s13_extractcomposite.start` · **Test:** `scripts/warsztat.sh test m5/s13`

**Zasada:** Extract Composite przenosi powieloną obsługę dzieci z kilku klas kontenerów do wspólnej nadklasy. Podciąga się tylko to, co ma ten sam kontrakt, a nie wszystko, co wygląda podobnie.

**Zadanie:**
1. Wydziel wspólną nadklasę dla `Marathon` i `ShortsBlock` z obsługą dzieci.
2. Podciągnij do niej to, co naprawdę jest wspólne; zostaw w podklasach to, co się różni.

**Podpowiedź:** obie klasy sumują minuty, ale tylko maraton dolicza przerwy.
**Gotowe, gdy:** test zielony, lista dzieci i `add` istnieją w jednym miejscu, a podklasy mają po kilkanaście linii.

## Scena s14. Unify Interfaces with Adapter - dwie bramki płatności

**Pakiet:** `pl.training.workshop.m5.s14_adapter.start` · **Test:** `scripts/warsztat.sh test m5/s14`

**Zasada:** Adapter tłumaczy obcy interfejs na kontrakt, którego oczekuje klient, tak żeby klient nie znał szczegółów zewnętrznej biblioteki. Tłumaczyć trzeba nie tylko nazwy, ale też jednostki, formaty i sposób zgłaszania błędów.

**Zadanie:**
1. Wydziel obsługę każdej bramki do metody o tej samej sygnaturze.
2. Zdefiniuj preferowany interfejs płatności kina i dwa adaptery.
3. Spraw, żeby `CheckoutService` zależał wyłącznie od tego interfejsu.

**Podpowiedź:** stara bramka liczy w groszach i zwraca odmowę w XML, nowa liczy w złotych i zgłasza odmowę wyjątkiem. Nie zmieniaj klas bramek.
**Gotowe, gdy:** test zielony, a metoda `pay` w `CheckoutService` korzysta wyłącznie z preferowanego interfejsu (szczegóły XML i REST są tylko w adapterach).

## Scena s15. Replace Conditional Dispatcher with Command - konsola kasjera

**Pakiet:** `pl.training.workshop.m5.s15_command.start` · **Test:** `scripts/warsztat.sh test m5/s15`

**Zasada:** Command zamienia każdą gałąź dyspozytora w osobny obiekt o wspólnym interfejsie, a wybór gałęzi w wyszukanie w rejestrze. Rejestr jest równoważny warunkom tylko przy rozłącznych kluczach i zachowanej normalizacji.

**Zadanie:**
1. Wydziel ciało każdej gałęzi do metody o wspólnej sygnaturze.
2. Zamień gałęzie na obiekty komend; stan kasy wydziel z konsoli.
3. Zastąp łańcuch `if` rejestrem komend.

**Podpowiedź:** `sell` i `SELL` działają tak samo - nie zgub normalizacji klucza.
**Gotowe, gdy:** test zielony, komendy są bezstanowe, a dodanie komendy nie wymaga zmiany metody `handle`.

## Scena s16. Form Template Method - raporty CSV i HTML

**Pakiet:** `pl.training.workshop.m5.s16_templatemethod.start` · **Test:** `scripts/warsztat.sh test m5/s16`

**Zasada:** Template Method zapisuje stałą sekwencję kroków raz w nadklasie, a podklasy dostarczają tylko kroki, które się różnią. Najpierw doprowadza się podobne metody do identycznej postaci, dopiero potem je podciąga.

**Zadanie:**
1. Doprowadź `render` obu raportów do identycznej postaci, wydzielając różnice do metod o tych samych nazwach.
2. Przenieś wspólny szkielet do nadklasy i zabezpiecz go przed nadpisaniem.

**Podpowiedź:** różnice to nagłówek, wiersz (z escapowaniem) i stopka.
**Gotowe, gdy:** test zielony, sortowanie i sumowanie są w jednym miejscu, a raporty zawierają tylko formatowanie.

## Scena s17. Limit Instantiation with Singleton - cennik

**Pakiet:** `pl.training.workshop.m5.s17_singleton.start` · **Test:** `scripts/warsztat.sh test m5/s17`

**Zasada:** Singleton ogranicza tworzenie obiektu do jednej instancji i jest decyzją o cyklu życia, bezpieczną tylko dla obiektów niemutowalnych i równoważnych. Globalny dostęp do tej instancji ukrywa zależności i utrudnia testy.

**Zadanie:**
1. Sprawdź w teście, ile cenników powstaje przy trzech wycenach, i uzasadnij, dlaczego jedna instancja jest bezpieczna.
2. Ogranicz tworzenie cennika do jednej instancji.
3. Spraw, żeby `TicketDesk` dało się przetestować z innym cennikiem bez globalnego stanu.

**Podpowiedź:** porównaj klasyczne `getInstance()` z enumem, a potem zastanów się, kto powinien decydować o cyklu życia.
**Gotowe, gdy:** test zielony, cennik jest tworzony raz, a `TicketDesk` przyjmuje cennik przez konstruktor.

## Scena s18. Move Accumulation to Collecting Parameter - ostrzeżenia walidacji

**Pakiet:** `pl.training.workshop.m5.s18_collectingparameter.start` · **Test:** `scripts/warsztat.sh test m5/s18`

**Zasada:** Collecting Parameter przekazuje do metod wspólny akumulator, do którego dopisują one swoje wyniki, zamiast zwracać fragmenty do sklejenia. Akumulator należy do wywołującego i powinien mieć wąski typ.

**Zadanie:**
1. Zastąp sklejanie ostrzeżeń w `String` listą.
2. Przekaż akumulator do metod pomocniczych zamiast zwracać z nich fragmenty.
3. Nadaj akumulatorowi wąski typ, który pozwala tylko dopisywać.

**Podpowiedź:** separator `"; "` powinien pojawiać się w kodzie dokładnie raz.
**Gotowe, gdy:** test zielony, metody `check...` zwracają `void`, a format wyniku jest ustalany w jednym miejscu.

## Scena s19. Visitor i macierz zmian - pozycje zamówienia

**Pakiet:** `pl.training.workshop.m5.s19_visitor.start` · **Test:** `scripts/warsztat.sh test m5/s19`

**Zasada:** Visitor wydziela operacje na strukturze do osobnych klas, a element sam wybiera właściwą metodę odwiedzającego. Tanio dodaje się w nim nowe operacje, drogo nowe rodzaje elementów.

**Zadanie:**
1. Zamień trzy łańcuchy `instanceof` na klasyczny Visitor.
2. Przygotuj alternatywę: zamknięta hierarchia pozycji i `switch` po typach bez `default`.
3. Wypełnij macierz: ile plików zmienisz przy nowej operacji, a ile przy nowym rodzaju pozycji - w obu wersjach.

**Podpowiedź:** VAT liczymy od ceny brutto każdej pozycji osobno i dopiero potem sumujemy.
**Gotowe, gdy:** test zielony dla wybranej wersji, w kodzie nie ma `instanceof`, a dodanie typu pozycji bez obsługi kończy się błędem kompilacji.

## Scena s20. Mapa decyzji - jedna tabela, dwie struktury

**Pakiet:** `pl.training.workshop.m5.s20_decisionmap.start` · **Test:** `scripts/warsztat.sh test m5/s20`

**Zasada:** Wzorzec dobiera się do rodzaju zmienności, a nie do wyglądu kodu: punkt rozszerzenia powinien leżeć na osi, która zmienia się najczęściej. Gdy nie wiadomo, która to oś, prostsza struktura bez nowych typów jest bezpieczniejsza.

**Zadanie:**
1. Rozplącz tabelę cen na dwie niezależne reguły.
2. Zaimplementuj wariant A: marketing często zmienia reguły dniowe.
3. Od stanu po punkcie 1 zaimplementuj wariant B: często dochodzą nowe formaty.
4. Zapisz jednym zdaniem, po czym poznasz w projekcie, który wariant wybrać.

**Podpowiedź:** zrób commit (albo kopię) po punkcie 1, żeby móc wrócić i zbudować drugi wariant.
**Gotowe, gdy:** test zielony dla obu wariantów, a uzasadnienie wyboru odwołuje się do tego, co się zmienia, nie do wyglądu kodu.

## Scena s21. Wzorzec jako decyzja odwracalna - rozliczenie z dystrybutorem

**Pakiet:** `pl.training.workshop.m5.s21_reversiblepattern.start` · **Test:** `scripts/warsztat.sh test m5/s21`

**Zasada:** Wzorzec projektowy to odpowiedź na konkretne siły, a nie kod na zapas - gdy siły znikają, wzorzec można usunąć. Usunięcie wzorca jest równie poprawną refaktoryzacją jak jego wprowadzenie.

**Zadanie:**
1. Wprowadź Strategy dla dwóch istniejących modeli rozliczeń; dla modelu festiwalowego napisz Adapter do `FestivalTariffClient`.
2. Umowy festiwalowe wygasły: usuń ten wariant (to świadoma zmiana zachowania).
3. Oceń, czy wzorzec nadal jest potrzebny, i jeśli nie - usuń go.

**Podpowiedź:** po każdym kroku test równoważności dla modelu procentowego musi być zielony. Sygnały nadmiaru wzorca: jedna implementacja, mapa z jednym wpisem.
**Gotowe, gdy:** test zielony, rozliczenie procentowe daje te same kwoty co na początku, model FESTIVAL jest odrzucany, a w kodzie nie zostały nieużywane typy.

## Scena s22. Remove Middle Man - fasada kina

**Pakiet:** `pl.training.workshop.m5.s22_middleman.start` · **Test:** `scripts/warsztat.sh test m5/s22`

**Zasada:** Pośrednika, który tylko przekazuje wywołania dalej, można usunąć. Najpierw trzeba jednak sprawdzić, czy nie robi czegoś więcej, na przykład autoryzacji, retry albo translacji błędów.

**Zadanie:**
1. Sprawdź każdą metodę `CinemaFacade`: czy tylko deleguje, czy robi coś więcej.
2. Przenieś to, co robi więcej, tam, gdzie jest potrzebne.
3. Zmigruj klientów do `ScreeningCatalog` po jednym i usuń fasadę.

**Podpowiedź:** uruchom `SeatBadge` dla seansu `S9` przed i po zmianie.
**Gotowe, gdy:** test jest zielony, `CinemaFacade` nie istnieje, a zachowanie dla nieznanego seansu jest takie samo jak na starcie.

## Scena s23. Refaktoryzacja a zmiana kontraktu - zwrot na BigDecimal

**Pakiet:** `pl.training.workshop.m5.s23_contractchange.start` · **Test:** `scripts/warsztat.sh test m5/s23`

**Zasada:** Refaktoryzacja zachowuje obserwowalne zachowanie: klient nie może dostrzec żadnej nieuzgodnionej różnicy. Każda zmiana wyniku, formatu czy wyjątku to zmiana kontraktu, którą robi się osobno i świadomie.

**Zadanie:**
1. Zrób jedną czystą refaktoryzację `RefundCalculator.refund` bez zmiany typów.
2. Przejdź na `BigDecimal` i znajdź wszystkie różnice w zachowaniu, które to wprowadza.
3. Dla każdej różnicy zdecyduj: zachować stary kontrakt czy świadomie go zmienić. Zapisz decyzję.

**Podpowiedź:** sprawdź zwrot po starcie seansu oraz kwotę 64.35 anulowaną 2 godziny przed seansem.
**Gotowe, gdy:** testy są zielone, a każda różnica względem startu ma w teście jawne oczekiwanie i jedno zdanie uzasadnienia.

## Scena s24. Rename - nazwy, które żyją poza Javą

**Pakiet:** `pl.training.workshop.m5.s24_rename.start` · **Test:** `scripts/warsztat.sh test m5/s24`

**Zasada:** Rename nadaje nazwę opisującą rolę w kontekście. Nazwa używana poza Javą (konfiguracja, refleksja, formaty plików) jest kontraktem i wymaga strategii migracji, a nie tylko operacji IDE.

**Zadanie:**
1. Nadaj znaczące nazwy zmiennym lokalnym i parametrom w `SalesReport`.
2. Zmień nazwę metody `calc2` na nazwę opisującą, co zwraca.
3. Zmień nazwy komponentów rekordu `Line` (`t`, `n`, `d`).
4. Nie zmieniaj konfiguracji w `ReportJob` ani formatu CSV, bo oba są kontraktem z kimś spoza repozytorium.

**Podpowiedź:** po każdym Rename uruchom oba testy równoważności. Jeśli jeden jest czerwony, zastanów się, gdzie jeszcze żyje ta nazwa.
**Gotowe, gdy:** test jest zielony, w `SalesReport` nie ma jedno- ani dwuliterowych nazw, a zadanie z konfiguracji dalej działa.

## Scena s25. Extract Method - przepływ danych i wiele wyjść

**Pakiet:** `pl.training.workshop.m5.s25_extractmethod.start` · **Test:** `scripts/warsztat.sh test m5/s25`

**Zasada:** Extract Method przenosi spójny fragment do metody, której nazwa wyraża intencję. Przed ekstrakcją trzeba przeanalizować przepływ danych: co wchodzi, a co wychodzi z fragmentu.

**Zadanie:**
1. Zamień bloki opisane komentarzami w `TicketSummary.describe` na metody z nazwami.
2. Poradź sobie z pętlą, która ma dwie wartości wyjściowe.
3. Oddziel obliczenia od składania tekstu.

**Podpowiedź:** jeśli IDE odmawia ekstrakcji, przeczytaj dokładnie, dlaczego. Rozwiązaniem nie jest tablica ani mutowalny "holder".
**Gotowe, gdy:** test jest zielony, `describe` ma najwyżej trzy linie, a każda wydzielona metoda ma jedno wyjście.

## Scena s26. Strategy, Command i State w jednej klasie - pulpit kasjera

**Pakiet:** `pl.training.workshop.m5.s26_variability.start` · **Test:** `scripts/warsztat.sh test m5/s26`

**Zasada:** Ten sam `switch` może kryć różne rodzaje zmienności, a mechanizm wybiera się po tym, kto wybiera wariant i kiedy. Wariant wybierany przez klienta z danych to Strategy, żądanie zapisywane w dzienniku i cofane to Command, a zachowanie zmieniające się wraz ze stanem obiektu to State.

**Zadanie:**
1. Wypisz trzy osie zmienności w `BoxOfficeDesk` i dla każdej odpowiedz: kto wybiera wariant i kiedy?
2. Wydziel cenę biletu według typu (N, S, E, C) jako strategię.
3. Zamień operacje BOOK, PAY, USE i CANCEL na polecenia z `execute` i `undo`, a rezerwacje i kasę wydziel do osobnego odbiorcy poleceń.
4. Przenieś cykl życia rezerwacji (NEW, PAID, USED, CANCELLED) do obiektów stanu.

**Podpowiedź:** jedna oś naraz, test po każdym ruchu. CANCEL w PAID oddaje pieniądze, a w NEW tylko zwalnia rezerwację - to ta sama operacja z innym zachowaniem. Cofnięcie USE musi przywrócić status, którego zwykłe przejścia nie pozwalają osiągnąć.
**Gotowe, gdy:** test zielony, pulpit nie zawiera `switch` po statusie ani po typie biletu, polecenie nie jest współdzielone między wywołaniami, a stan rezerwacji nie zna cennika ani nazw operacji.

## Scena s27. Replace Inheritance with Delegation - raporty sprzedaży

**Pakiet:** `pl.training.workshop.m5.s27_templatetostrategy.start` · **Test:** `scripts/warsztat.sh test m5/s27`

**Zasada:** Template Method dobrze obsługuje jeden zmienny krok w stabilnym szkielecie. Gdy haki niosą kilka niezależnych osi, każda kombinacja staje się podklasą, a kompozycja strategii zamienia iloczyn klas w sumę.

**Zadanie:**
1. Pogrupuj haki `ScreeningReport` według osi zmienności.
2. Wyjmij oś formatu do strategii i przekaż ją do szkieletu przez konstruktor.
3. Zrób to samo z osią okresu.
4. Zastąp cztery podklasy jedną finalną klasą raportu złożoną z dwóch strategii.

**Podpowiedź:** zanim usuniesz kopię kodu z podklasy, porównaj ją z drugą kopią. `Reports.of` musi nadal sprawdzać najpierw okres, potem format.
**Gotowe, gdy:** test zielony, nie ma podklas raportu, metoda `render` istnieje w jednym miejscu, a nowy format można dodać jedną klasą działającą z każdym okresem.

## Scena s28. Replace Conditional with Chain of Responsibility - zatwierdzanie zwrotów

**Pakiet:** `pl.training.workshop.m5.s28_chain.start` · **Test:** `scripts/warsztat.sh test m5/s28`

**Zasada:** Chain of Responsibility zamienia uporządkowany łańcuch warunków w ogniwa, z których każde obsługuje żądanie albo przekazuje je dalej. Kolejność ogniw jest regułą biznesową, a koniec łańcucha musi dawać jawną decyzję.

**Zadanie:**
1. Przeczytaj test na granicach progów i znajdź przypadek, który zależy od kolejności warunków.
2. Wyjmij pierwszą gałąź jako ogniwo, uruchom test, potem kolejne.
3. Złóż łańcuch jawnie, tak żeby można było zbudować inną ścieżkę (np. festiwal bez zwrotów automatycznych) bez edycji `RefundApprover.decide`.
4. Zdecyduj, czy audyt decyzji byłby kolejnym ogniwem, czy dekoratorem - i uzasadnij jednym zdaniem.

**Podpowiedź:** walidacja kwoty to warunek wstępny, a nie poziom zatwierdzania. Dokładnie 24 h przed seansem to jeszcze zwrot automatyczny, 23 h 59 min - już nie.
**Gotowe, gdy:** test zielony, w `decide` nie ma łańcucha `if/else`, kolejność ogniw widać w jednym miejscu, a ostatnie ogniwo zwraca decyzję zamiast `null`.

## Scena s29. Introduce Boundary Object - odpowiedź terminala płatniczego

**Pakiet:** `pl.training.workshop.m5.s29_boundaryobject.start` · **Test:** `scripts/warsztat.sh test m5/s29`

**Zasada:** Boundary Object to niezmienny, zwalidowany obraz danych z zewnątrz w języku domeny - jeśli istnieje, jest poprawny. Adapter tłumaczy wywołanie obcego systemu, a obiekt graniczny jego dane; walidacja na granicy zmienia typ i moment błędu dla danych niepoprawnych.

**Zadanie:**
1. Rozbieraj odpowiedź terminala tylko raz, do obiektu potwierdzenia płatności z walidacją w konstruktorze.
2. Przenieś znajomość formatu `STATUS;KOD;KWOTA` i przeliczanie groszy do osobnego mappera.
3. Schowaj `LegacyTerminalClient` za portem domeny, tak żeby `PaymentService` znał tylko port i obiekt potwierdzenia.
4. Zapisz, jak zmienia się zachowanie dla odpowiedzi zniekształconych (np. `OK;;4500`).

**Podpowiedź:** potwierdzenie odmowy nie może autoryzować pieniędzy - wtedy suma autoryzacji nie musi sprawdzać statusu. Nie zmieniaj klasy terminala.
**Gotowe, gdy:** test zielony, `PaymentService` nie zawiera `split` ani przeliczania groszy, a zniekształcona odpowiedź jest odrzucana w mapperze.

## Scena s30. Extract Facade - zakup biletów w sklepie i w kasie

**Pakiet:** `pl.training.workshop.m5.s30_facade.start` · **Test:** `scripts/warsztat.sh test m5/s30`

**Zasada:** Facade daje klientom jedną spójną operację zamiast znajomości kroków i ich kolejności, a ukrycie podsystemów sprawia, że żadnego kroku nie da się pominąć. Gdy skopiowane orkiestracje się rozjechały, przepięcie klienta na fasadę zmienia jego zachowanie - to trzeba nazwać i zatwierdzić osobno.

**Zadanie:**
1. Porównaj `OnlineShop.buy` i `BoxOffice.sell` linia po linii i zapisz każdą różnicę.
2. Wydziel orkiestrację zakupu ze sklepu do fasady; to, czym różnią się kanały, przekaż parametrem.
3. Przepnij kasę na fasadę i opisz, co się zmieniło w jej zachowaniu.
4. Ukryj podsystemy przed kanałami sprzedaży i zadbaj, żeby miejsca nie zostawały zajęte także przy awarii bramki płatności.

**Podpowiedź:** przeczytaj `S30BehaviourChangeTest`, zanim zaczniesz - to on mówi, która zmiana jest zamierzona. Karta `TIMEOUT` symuluje wyjątek bramki.
**Gotowe, gdy:** testy zielone, sklep i kasa zależą tylko od fasady, podsystemy nie są publiczne, a po każdej nieudanej płatności miejsca są wolne.

## Scena s31. Anti-Corruption Layer - licencje dystrybutora

**Pakiet:** `pl.training.workshop.m5.s31_anticorruptionlayer.start` · **Test:** `scripts/warsztat.sh test m5/s31`

**Zasada:** Anti-Corruption Layer chroni język domeny przed obcym modelem: domena rozmawia z portem we własnych pojęciach, a tłumacz jako jedyny zna konwencje obcego systemu. Fasada tylko upraszcza dostęp, ACL tłumaczy jeden model na drugi.

**Zadanie:**
1. Wprowadź licencję filmu w języku kina (daty, kwoty, procent, aktywność) z niezmiennikami i regułami "obejmuje dzień" oraz "ile się należy".
2. Wydziel tłumaczenie rekordu dystrybutora do osobnej klasy.
3. Wprowadź port domeny zwracający licencję (brak licencji bez `null`) i schowaj za nim klienta dystrybutora.
4. Zapisz, jak zmieniło się zachowanie dla rekordów z błędnymi danymi.

**Podpowiedź:** udział bywa zapisany jako `" 40 %"`, gwarancja to grosze w tekście, a daty mają format `yyyyMMdd`. Nie zmieniaj `DistributorFeed` ani `DistributorRecord`.
**Gotowe, gdy:** test zielony, `SettlementService` nie używa żadnego typu dystrybutora, a jedyne miejsce znające kody statusu i formaty dat to tłumacz.

## Scena s32. Introduce Mediator - ekran wyboru miejsc

**Pakiet:** `pl.training.workshop.m5.s32_mediator.start` · **Test:** `scripts/warsztat.sh test m5/s32`

**Zasada:** Mediator skupia reguły współpracy komponentów w jednym miejscu: komponent zgłasza, co się stało, a mediator decyduje, kto i w jakiej kolejności reaguje. Logika samych komponentów zostaje w komponentach, inaczej mediator staje się boską klasą.

**Zadanie:**
1. Policz referencje między komponentami ekranu i znajdź wszystkie miejsca, które znają kolejność odświeżania.
2. Wprowadź interfejs mediatora i przenieś do niego reakcję na kliknięcie miejsca.
3. Przenieś pozostałe reakcje komponent po komponencie, a kolejność odświeżania zapisz w jednym miejscu.
4. Spraw, żeby żaden komponent nie miał referencji do innego komponentu.

**Podpowiedź:** płatność czyści koszyk, a czyszczenie koszyka samo zgłasza zdarzenie - uważaj na kaskadę. Przycisk pokazuje kwotę, więc suma musi być przeliczona przed nim.
**Gotowe, gdy:** test zielony, każdy komponent zna tylko interfejs mediatora, a cena VIP i limit 4 miejsc nadal są liczone w swoich komponentach.
