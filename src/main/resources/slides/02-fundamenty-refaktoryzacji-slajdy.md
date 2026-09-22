---
marp: true
theme: default
paginate: true
size: 16:9
style: |
  section { font-size: 24px; }
  section.lead h1 { font-size: 48px; }
  pre, code { font-size: 18px; }
---

<!-- _class: lead -->
# Moduł 2. Fundamenty refaktoryzacji

Jak zmieniać strukturę kodu legacy bezpiecznie: kontrakt, małe kroki, testy i szwy

---

## Agenda

1. Istota i cele refaktoryzacji
2. Zasady bezpiecznej refaktoryzacji
3. Znaczenie testów w procesie refaktoryzacji
4. Piramida testów i strategie testowania
5. Obiekty zastępcze: stub, spy, fake i mock
6. Pokrycie kodu: zalety, ograniczenia i nadużycia
7. Testy charakteryzujące i refaktoryzacja pod ochroną testów
8. Seams i rozrywanie zależności
9. Warsztat praktyczny, sprawdzenie wiedzy, listy kontrolne

---

## 1.1. Precyzyjna definicja refaktoryzacji

- **Refaktoryzacja** to kontrolowana zmiana wewnętrznej struktury oprogramowania, która ułatwia rozumienie i dalsze modyfikacje **bez zmiany obserwowalnego zachowania**.
- Termin ma dwa znaczenia: pojedyncza transformacja (zmiana nazwy, wydzielenie metody, przeniesienie pola) oraz proces wykonywania serii takich transformacji.
- Małe kroki nie oznaczają małego celu - duża zmiana projektu może powstać z wielu lokalnych transformacji.
- Po każdym kroku kod nadal się buduje, testy są zielone, a ostatnią różnicę łatwo przeanalizować lub wycofać.

---

## 1.2. Co oznacza zachowanie obserwowalne

| Rodzaj | Przykład |
| --- | --- |
| wynik | zwrócona kwota, kod decyzji, wygenerowany dokument |
| błąd | typ wyjątku, warunek wystąpienia, kod odpowiedzi |
| efekt uboczny | zapis rekordu, wysłanie wiadomości, publikacja zdarzenia |
| protokół | liczba i kolejność wywołań, gdy mają znaczenie transakcyjne |
| dane | format pliku, schemat komunikatu, reguła zaokrąglenia |
| kontrakt publiczny | sygnatura API, widoczność typu, kompatybilność binarna |
| pozafunkcjonalne | czas odpowiedzi, pamięć, przepustowość - jeśli są wymaganiem |

---

## 1.2. Granica obserwowalności

- Granica obserwowalności zależy od systemu i konkretnej zmiany - nie ogranicza się do wartości zwracanej przez metodę.
- Prywatna metoda, liczba klas czy kolejność czystych obliczeń zwykle **nie są kontraktem** i mogą się zmieniać, jeśli publiczne obserwacje pozostają zgodne.
- Wyjątek: gdy refleksja, serializacja, konfiguracja lub zewnętrzny konsument zależy od elementu pozornie wewnętrznego.
- Zamiast mówić „zachowanie się nie zmieni”, należy nazwać, **jakie obserwacje** mają pozostać niezmienione i skąd pochodzi o nich wiedza.

---

## 1.3. Refaktoryzacja a inne rodzaje zmian

| Zmiana | Refaktoryzacja? | Dlaczego |
| --- | --- | --- |
| zmiana nazwy prywatnej metody | zwykle tak | kontrakt bez zmian |
| korekta błędnego zaokrąglania | nie | celowo zmienia wynik |
| nowy wariant rabatu | nie | rozszerza zachowanie |
| wydzielenie obliczenia do metody | tak | transformacja struktury |
| dodanie pamięci podręcznej | zwykle optymalizacja | zmienia czas, pamięć, spójność |
| przejście na inną platformę | modernizacja | szerszy zakres i ryzyka |
| pełne przepisanie klasy | nie | nowa implementacja |
| usunięcie defektu przy porządkowaniu | nie w tym kroku | jawna zmiana zachowania |

---

## 1.3. Kontekst decyduje o charakterze zmiany

- Ta sama operacja składniowa może mieć inny charakter zależnie od celu i kontraktu, którego dotyczy.
- Zmiana publicznej nazwy w aplikacji, której wszystkich klientów kontroluje jeden zespół, może być częścią refaktoryzacji.
- Ta sama zmiana w opublikowanej bibliotece może naruszyć kompatybilność i stać się **migracją API**.
- Wniosek: klasyfikację zmiany rozstrzyga kontrakt i konsumenci, a nie nazwa operacji w IDE.

---

## 1.4. Cele ekonomiczne i projektowe

Refaktoryzacja nie jest konkursem estetycznym - ma obniżyć koszt lub ryzyko dalszej pracy.

- **Typowe cele:** przygotowanie miejsca pod funkcję, utrwalenie wiedzy z analizy, skrócenie drogi do informacji zwrotnej, rozdzielenie odpowiedzialności.
- Także: ograniczenie sprzężenia, udostępnienie punktu testowania, usunięcie potwierdzonej duplikacji wiedzy, przywrócenie małych, częstych zmian.
- **Nie są celami samymi w sobie:** więcej klas i interfejsów, wzorzec dla wzorca, arbitralna długość metod, procent pokrycia, „idealny projekt”.
- Najłatwiej uzasadnić inwestycję w obszar wkrótce zmieniany; poprawa stabilnego kodu o krótkim życiu może się nigdy nie zwrócić.

---

## 1.5. Kiedy refaktoryzować

- **Przygotowawczo** - tworzy prostszą drogę dla najbliższej funkcji.
- **Wspierająco zrozumienie** - zapisuje w strukturze wiedzę zdobytą podczas czytania kodu.
- **Oportunistycznie** - niewielka poprawa przy bezpośredniej pracy w danym miejscu.
- **Planowo / długoterminowo** - ograniczony wysiłek wobec potwierdzonego problemu lub seria małych zmian przesuwających granicę architektoniczną bez zatrzymywania rozwoju.
- Osobny projekt refaktoryzacyjny bywa potrzebny, ale nie jako domyślna odpowiedź - duże zakresy wydłużają czas bez informacji zwrotnej i kumulują ryzyko.

---

## Ćwiczenie: nazwij kontrakt (4 min)

Dla każdej zmiany wskaż zachowanie, które może być obserwowalne:

1. zmiana typu prywatnego pola z `List` na `Set`,
2. zamiana dwóch zapisów do bazy miejscami,
3. przeniesienie klasy używanej przez mechanizm refleksji,
4. zastąpienie algorytmu szybszą implementacją,
5. zmiana prywatnej nazwy bez użycia refleksji i konfiguracji tekstowej.

**Zasada:** nie klasyfikuj zmiany przed nazwaniem konsumenta, obserwacji i sposobu weryfikacji.

---

## 2.1. Zacznij od znanego stanu

- Projekt buduje się w powtarzalnym środowisku, a istniejące testy przechodzą lub ich znane awarie są jawnie odizolowane.
- Zakres zmiany i ważne kontrakty są nazwane; dostępne są reprezentatywne dane lub scenariusze.
- Wiadomo, które testy dają szybką informację lokalną, a które sprawdzają integrację większej części systemu.
- Istnieje bezpieczny sposób powrotu do poprzedniego kroku.
- Stale czerwony test nie może być czujnikiem nowej regresji - trzeba go naprawić, odizolować z uzasadnieniem albo obsłużyć osobno, inaczej traci się zaufanie do zestawu.

---

## 2.2. Oddziel dwa tryby pracy

- **Tryb strukturalny** zachowuje uzgodnione obserwacje; **tryb funkcjonalny** celowo je zmienia. W danym kroku trzeba wiedzieć, który realizujemy.
- Fowler opisuje to metaforą **dwóch kapeluszy** - dodawania funkcji i refaktoryzacji - których nie nosi się jednocześnie.
- Przebieg: zapisz nowe wymaganie → uprość drogę (testy zielone) → dodaj test nowego wymagania i zobacz porażkę → zaimplementuj → ponownie uporządkuj.
- Gdy test charakteryzujący ujawnia podejrzany wynik, nie aktualizuj go po cichu - najpierw rozstrzygnij: defekt, historyczny kontrakt czy nieistotny szczegół.

---

## 2.3. Pętla małych kroków

1. Określ najmniejszą transformację i wykonaj ją (operacja IDE lub mała zmiana ręczna).
2. Skompiluj kod i uruchom najszybsze testy obejmujące zmienione zachowanie.
3. Obejrzyj różnicę, usuń przypadkowe modyfikacje, zapisz punkt przywracania.
4. Powtarzaj; regularnie uruchamiaj szerszy zestaw, a przed zakończeniem - pełną weryfikację kontraktów.

**Dlaczego:** krok ma być na tyle mały, by przy porażce ostatnia transformacja była głównym podejrzanym. Wielogodzinna seria zmian przed pierwszym uruchomieniem testów usuwa tę przewagę.

---

## 2.4. Dobierz weryfikację do ryzyka

Kompilacja nie sprawdzi semantyki, test jednostkowy - mapowania ORM, a test systemowy wolno wskazuje przyczynę.

| Zmieniany element | Przydatna weryfikacja |
| --- | --- |
| czyste obliczenie | szybkie testy przykładów i wartości granicznych |
| mapowanie danych | test adaptera z rzeczywistym mechanizmem mapowania |
| format komunikatu | test kontraktu lub serializacji |
| kolejność skutków | test interakcji i zachowania transakcyjnego |
| zapytanie wydajnościowe | test poprawności + osobny pomiar wydajności |
| publiczne API | testy konsumenta, kompatybilność i integracja |

---

## 2.5-2.6. Szum w różnicy i granice narzędzi

- Zmiana strukturalna nie powinna mieszać się z masowym formatowaniem, aktualizacją zależności, niezwiązanymi zmianami nazw i nową funkcją - duży diff utrudnia przegląd i diagnozę.
- Formatowanie automatyczne warto wykonywać jako osobny, mechaniczny krok; punkty przywracania służą lokalnemu powrotowi, nie muszą trafiać do głównej gałęzi.
- Automatyczna refaktoryzacja IDE aktualizuje odwołania lepiej niż ręczna edycja, ale nie widzi: nazw klas w konfiguracji, refleksji, szablonów i zapytań, formatów serializacji, kodu generowanego, zewnętrznych konsumentów.
- Wniosek: narzędzie zmniejsza ryzyko transformacji, ale **nie jest dowodem** zachowania całego systemu.

---

## 2.7. Kiedy przerwać

Zatrzymaj krok i wróć do ostatniego znanego stanu, gdy:

- nie wiadomo, czy porażka testu oznacza regresję, czy zmianę wymagania,
- zakres różnicy rośnie szybciej niż zrozumienie lub trzeba naraz zmieniać wiele niezależnych kontraktów,
- testy stały się niestabilne lub zbyt wolne, aby prowadzić pracę,
- ujawniono defekt wymagający decyzji biznesowej,
- minimalne rozrywanie zależności zaczyna przypominać nieplanowaną przebudowę architektury.

**Przerwanie kroku nie jest porażką - to mechanizm kontroli ryzyka.**

---

## 3.1. Test dostarcza dowodu, nie pewności absolutnej

- Zielony zestaw oznacza tylko, że **żaden test nie wykrył różnicy** w zachowaniach, które wywołał i sprawdził.
- Nie gwarantuje, że wymagania są dobrze opisane, wykonano każdą ścieżkę, sprawdzono dane graniczne ani że asercje są wystarczająco czułe.
- Nie potwierdza też zgodności środowiska testowego z produkcją ani braku zmian właściwości pozafunkcjonalnych.
- Bezpieczeństwo rośnie przez połączenie testów z małym zakresem zmiany, analizą różnicy, kompilatorem, analizą statyczną, testami integracyjnymi i obserwacją operacyjną.

---

## 3.2. Testuj stabilne zachowanie

- Test przywiązany do prywatnych metod, liczby klas lub nieistotnej kolejności wywołań może **blokować poprawną refaktoryzację**.
- Test powinien obserwować najwęższą stabilną granicę, która nadal wykrywa ważną zmianę.
- Weryfikacja stanu lub wyniku jest zwykle odporniejsza na zmianę implementacji niż weryfikacja interakcji.
- Interakcję weryfikuj, gdy jest kontraktem: płatność nie może być pobrana dwukrotnie, zdarzenie publikowane po zatwierdzeniu transakcji, poufne dane nie trafiają do bramki, ponowienie zachowuje idempotencję.
- Sprawdzanie każdej rozmowy między obiektami „bo biblioteka pozwala” odtwarza implementację w teście.

---

## 3.3. Cechy użytecznej sieci bezpieczeństwa

| Cecha | Znaczenie podczas refaktoryzacji |
| --- | --- |
| szybkość | wynik pojawia się przed kolejnym krokiem |
| determinizm | porażka wskazuje zmianę, nie czas, sieć czy kolejność |
| czułość | istotna zmiana zachowania powoduje porażkę |
| stabilność strukturalna | poprawna zmiana wnętrza nie wymaga masowej aktualizacji testów |
| diagnostyka | komunikat i zakres testu wskazują źródło problemu |
| realizm | granice wysokiego ryzyka sprawdzane z rzeczywistą technologią |
| utrzymywalność | dane i oczekiwania są zrozumiałe i zmienialne |

Szybki test o słabej asercji nie chroni, a realistyczny test trwający 11 godzin nie prowadzi małych kroków - potrzebny jest **portfel testów**.

---

## 3.4. Punkt zmiany i punkt testowania

- Miejsce modyfikacji nie musi być najlepszym miejscem obserwacji - reguła może być prywatna, a stabilny wynik widoczny dopiero na granicy usługi.
- Na początku bezpieczniej bywa zabezpieczyć szerszy przebieg, a po poprawie struktury dodać precyzyjne testy wydzielonej reguły.

Przed pracą odpowiedz:

1. Gdzie kod zostanie zmieniony?
2. Skąd można uruchomić to zachowanie i gdzie widać jego wynik lub efekt?
3. Które zależności uniemożliwiają kontrolowane wykonanie?
4. Jaki najmniejszy test wykryje niezamierzoną zmianę?

---

## 4.1-4.2. Piramida testów to heurystyka

- Piramida przypomina, że testów małych, szybkich i precyzyjnych powinno być wiele, a wraz ze wzrostem zakresu, kosztu i niestabilności ich liczba zwykle maleje.
- Nie wynika z niej uniwersalna proporcja (np. 70/20/10) ani obowiązek zastępowania wszystkich zależności mockami.
- Szeroki test, który jest szybki, deterministyczny, tani w utrzymaniu i dobrze diagnozuje, może być bardzo wartościowy.
- Nazwy warstw są niejednoznaczne: „jednostka” to funkcja, klasa lub mały komponent; „test integracyjny” to adapter bazy albo kilka usług.
- Dlatego zespół powinien opisywać test przez **właściwości**, a nie samą etykietę.

---

## 4.2. Opis testu przez właściwości

| Właściwość | Pytanie |
| --- | --- |
| zakres | Ile kodu i procesów uruchamia test? |
| interfejs | Metoda, API, komunikat czy UI? |
| realizm zależności | Co jest rzeczywiste, a co zastąpione? |
| szybkość | Kiedy programista otrzyma wynik? |
| determinizm | Czy wynik zależy od sieci, czasu, współdzielonego stanu? |
| diagnostyka | Jak dokładnie porażka wskazuje przyczynę? |
| wykrywane ryzyko | Jaką istotną awarię test może ujawnić? |
| koszt | Ile kosztuje wykonanie i utrzymanie? |

---

## 4.3. Typowy portfel testów

| Rodzaj | Najważniejsza wartość | Typowe ograniczenie |
| --- | --- | --- |
| logika domenowa | szybka diagnoza reguł i granic | brak integracji technologicznej |
| komponentowy | współpraca kilku klas przez publiczną granicę | uproszczone zależności |
| wąski integracyjny | adapter, mapowanie, protokół, konfiguracja | zarządzanie rzeczywistą technologią |
| kontraktowy | niezgodność producenta i konsumenta | nie dowodzi całego przepływu |
| systemowy | współdziałanie aplikacji jako całości | wolniejsza diagnoza, koszt danych |
| end-to-end | najważniejsza ścieżka przez system | koszt, niestabilność, szeroka porażka |

Test akceptacyjny opisuje perspektywę i cel, nie poziom techniczny; E2E nie musi używać GUI, jeśli przepływ jest dostępny przez API lub komunikaty.

---

## 4.4. Rozmieszczenie w procesie weryfikacji

Testy warto uruchamiać według czasu i wartości informacji:

1. kompilacja i bardzo szybkie testy zmienionego obszaru,
2. szybkie testy całego modułu,
3. wąskie testy integracyjne i kontraktowe,
4. szersze testy systemowe,
5. ograniczony zestaw krytycznych scenariuszy end-to-end,
6. testy wydajnościowe, bezpieczeństwa i odporności - odpowiednio do ryzyka.

Szybki test adaptera może działać już w pierwszym etapie - **etykieta nie powinna opóźniać wartościowej informacji**.

---

## 4.5. Strategia testów dla kodu legacy

- Nie trzeba od razu budować idealnej piramidy - zacznij od planowanej zmiany i jej ryzyka.
- Wykorzystaj istniejącą stabilną granicę, nawet jeśli początkowy test jest szerszy; po utworzeniu punktów podmiany dodawaj szybsze testy niższego poziomu.
- Zachowuj szersze testy tylko tam, gdzie dają dodatkową informację; rzeczywiste adaptery sprawdzaj osobnymi testami integracyjnymi.
- Skracaj ścieżkę krytycznej informacji zwrotnej.
- Gdy szeroki test wykrył defekt, odtwórz go na najniższym poziomie zachowującym mechanizm awarii - ale nie usuwaj automatycznie testu szerokiego, jeśli nadal chroni integrację.

---

## Ćwiczenie: zaprojektuj portfel (5 min)

System **nalicza opłatę**, **zapisuje wynik w PostgreSQL** i **publikuje komunikat**.

Ostatnie regresje dotyczyły:
- wartości granicznej,
- mapowania `BigDecimal`,
- niezgodnego schematu komunikatu.

**Zadanie:** zaproponuj minimalny portfel testów. Dla każdego testu nazwij wykrywane ryzyko, rzeczywiste zależności, oczekiwany czas oraz miejsce w procesie weryfikacji.

---

## 5.1. Obiekty zastępcze (test doubles)

Nazwa roli zależy od sposobu użycia w teście, a nie od metody biblioteki, która utworzyła obiekt.

| Rola | Zadanie | Typowa weryfikacja |
| --- | --- | --- |
| stub | dostarcza zaprogramowane odpowiedzi | wynik obiektu testowanego |
| spy | rejestruje sposób użycia | odczyt zapisanych wywołań |
| fake | uproszczona, działająca implementacja | stan i kontrakt komponentu |
| mock | zaprogramowane oczekiwania interakcji | jawna weryfikacja wywołań |
| dummy | wypełnia parametr, nieużywany w scenariuszu | zwykle brak |

**Mock nie jest synonimem** każdego obiektu zastępczego - obiekt z biblioteki mockującej może być stubem albo mockiem.

---

## 5.2. Przykładowa usługa: `OrderPlacementService`

```java
public PlacedOrder place(String sku, int quantity, String paymentToken) {
    if (quantity <= 0) {
        throw new IllegalArgumentException("Quantity must be positive");
    }
    BigDecimal total = catalog.priceFor(sku)
            .multiply(BigDecimal.valueOf(quantity))
            .setScale(2, RoundingMode.HALF_UP);
    String authorizationId = paymentGateway.charge(paymentToken, total);
    long orderId = repository.save(
            new OrderDraft(sku, quantity, total, authorizationId));
    eventPublisher.publish(new OrderPlaced(orderId, total));
    return new PlacedOrder(orderId, total, authorizationId);
}
```

Czterech współpracowników (`ProductCatalog`, `PaymentGateway`, `OrderRepository`, `EventPublisher`) przekazanych przez konstruktor; przykład upraszcza transakcję - służy pokazaniu ról.

---

## 5.3. Stub, fake i spy w jednym teście

```java
ProductCatalog catalogStub = sku -> new BigDecimal("12.50");
PaymentGateway paymentStub = (token, amount) -> "AUTH-7";
InMemoryOrderRepository repositoryFake = new InMemoryOrderRepository();
RecordingEventPublisher publisherSpy = new RecordingEventPublisher();
var service = new OrderPlacementService(
        catalogStub, paymentStub, repositoryFake, publisherSpy);

PlacedOrder result = service.place("BOOK", 2, "TOKEN-1");

assertEquals(new BigDecimal("25.00"), result.total());
assertEquals(new OrderDraft("BOOK", 2, new BigDecimal("25.00"), "AUTH-7"),
        repositoryFake.find(result.orderId()));
assertEquals(List.of(new OrderPlaced(result.orderId(), new BigDecimal("25.00"))),
        publisherSpy.publishedEvents());
```

Katalog i bramka dają stałe odpowiedzi (**stub**), repozytorium trzyma stan (**fake**), publikator zapisuje zdarzenia (**spy**).

---

## 5.3. Mock: weryfikacja protokołu płatności

```java
private static final class ExpectingPaymentGateway implements PaymentGateway {
    // expectedToken, expectedAmount, authorizationId, calls
    @Override
    public String charge(String paymentToken, BigDecimal amount) {
        assertEquals(expectedToken, paymentToken);
        assertEquals(expectedAmount, amount);
        calls++;
        return authorizationId;
    }
    void verify() { assertEquals(1, calls); }
}
// w teście:
service.place("COURSE", 3, "TOKEN-2");
paymentMock.verify();
```

- Mock ma zaprogramowane oczekiwania: właściwy token, kwota `120.00` i **dokładnie jedno** wywołanie.

---

## 5.4. Weryfikacja stanu i interakcji

- Pierwszy test weryfikuje głównie **stan i wynik** - wewnętrzna kolejność obliczeń może się zmienić, a test pozostanie użyteczny.
- Drugi test opisuje **protokół płatności**: token, kwotę i dokładnie jedno wywołanie.
- Taka weryfikacja interakcji jest uzasadniona, gdy liczba obciążeń jest częścią kontraktu.
- Mock może też zwracać zaprogramowaną wartość, więc role techniczne się nakładają - klasyfikacja ma wyjaśniać **intencję testu**, a nie wymuszać czyste kategorie.

---

## 5.5. Ryzyka obiektów zastępczych

- **Fake** może zachowywać się inaczej niż produkcyjny adapter - warto uruchamiać wspólny zestaw testów kontraktu dla obu implementacji.
- Rozbudowana konfiguracja mocków powiela implementację i utrudnia refaktoryzację; mockowanie obiektów wartości i kolekcji zwykle tylko podnosi koszt.
- Baza w pamięci nie sprawdza zapytań, ograniczeń, transakcji ani mapowania typów.
- **Spy** może skłaniać do sprawdzania nieistotnej kolejności wywołań, a **stub** z nierealnymi danymi - omijać ważne zachowanie graniczne.
- Uwaga: `spy` w konkretnej bibliotece bywa częściowym mockiem wywołującym rzeczywiste metody - to nie ta sama rola co klasyczny spy.

---

## 6.1. Co mierzy JaCoCo

- JaCoCo analizuje **skompilowany kod bajtowy**, a nie bezpośrednio źródła.
- Raportuje pokrycie instrukcji, gałęzi (`if`, `switch`), linii (gdy klasy mają numery linii) oraz wykonanie metod i klas.
- Wyznacza też złożoność cyklomatyczną wraz z częścią pokrytą i niepokrytą, na podstawie pokrycia gałęzi.
- Obsługa wyjątków nie jest liczona jako gałąź; konstruktor i inicjalizator statyczny są z perspektywy bajtkodu metodami.
- Kod syntetyczny generowany przez kompilator może dawać wyniki nieoczywiste na poziomie źródła.

---

## 6.2. Pełne linie i brakująca gałąź

```java
public static int fee(boolean premium) {
    int fee = 100;
    if (premium) {
        fee = 0;
    }
    return fee;
}

@Test
void premiumCustomerHasFreeDelivery() {
    assertEquals(0, DeliveryFee.fee(true));
}
```

- Test wykonuje **wszystkie linie**, ale tylko prawdziwą gałąź warunku - opłata dla klienta bez premium nie jest sprawdzona.
- Wysokie pokrycie linii nie ujawnia tej luki tak wyraźnie jak pokrycie gałęzi.

---

## 6.3. Poprawne pytania do raportu pokrycia

**Pokrycie dobrze odpowiada na pytania:**
- Których instrukcji i gałęzi nie wykonał zestaw testów i czy testy weszły w obszar planowanej zmiany?
- Czy ważny warunek ma niewykonaną stronę i czy nowy kod pogłębia istniejącą lukę?

**Nie odpowiada natomiast na pytania:**
- Czy asercja sprawdziła właściwy wynik i czy oczekiwanie odpowiada wymaganiu?
- Czy dane są reprezentatywne i czy wykonano wszystkie istotne kombinacje ścieżek?
- Czy system jest wolny od defektów?

---

## 6.4. Progi i nadużycia pokrycia

- Próg może chronić przed niekontrolowanym pogarszaniem wyniku, ale **nie istnieje uniwersalna wartość** - krytyczna reguła wymaga innej ochrony niż kod generowany.
- Nadużycia: testy bez istotnych asercji dla raportu, jedna średnia dla całego systemu, wykluczanie trudnego kodu bez uzasadnienia.
- Także: testowanie banalnych akcesorów kosztem ryzykownych reguł, traktowanie 100% jako dowodu poprawności, rozliczanie zespołów z liczby zamiast z ryzyka.
- **Test mutacyjny** wprowadza kontrolowane zmiany i sprawdza, czy testy je wykrywają - ujawnia kod wykonany, ale chroniony słabymi asercjami.
- Wynik mutacji również wymaga interpretacji i nie jest samodzielną miarą jakości.

---

## Ćwiczenie: raport bez celu (4 min)

Otwórz raport po `mvn clean verify` i znajdź `DeliveryFee`. Odpowiedz:

1. Która gałąź nie została wykonana?
2. Jaki test ją uruchomi?
3. Jaka asercja wykryje zmianę `100` na `200`?
4. Czy podniesienie globalnego pokrycia przez testy innych klas zmniejszy to ryzyko?

---

## 7.1. Testy charakteryzujące - cel i zakres

- **Test charakteryzujący** rejestruje aktualne, zaobserwowane zachowanie kodu; jego zadaniem jest wykrywanie niezamierzonych zmian, a nie potwierdzanie idealnego wymagania.
- Test wymagania pyta, jak system **powinien** działać; test charakteryzujący - jak działa **obecnie** dla wybranej obserwacji.
- Ten sam test może później pełnić obie role, jeśli oczekiwanie zostanie potwierdzone jako poprawny kontrakt.
- Zapisany wynik może zawierać historyczny defekt - test nie nadaje mu statusu wymagania, lecz wymusza świadomą decyzję przed jego korektą.

---

## 7.2. Praktyczny przebieg charakteryzacji

1. Wybierz wąską granicę uruchomienia związaną z planowaną zmianą.
2. Uczyń wykonanie deterministycznym: kontroluj czas, losowość, kolejność i dane zewnętrzne.
3. Uruchom kod na małym, reprezentatywnym zestawie danych i zapisz istotny wynik, wyjątek lub efekt.
4. Zweryfikuj oczekiwanie (przegląd kodu, dokumentacja, dane produkcyjne, domena); dodaj przypadki graniczne i z historii defektów.
5. Wywołaj kontrolowaną mutację, by upewnić się, że test wykrywa zmianę - dopiero wtedy zacznij transformacje.

Nie kopiuj bezrefleksyjnie dużego wyniku - oddziel stabilny kontrakt od szumu (znaczniki czasu, ID techniczne, kolejność niezależnych rekordów).

---

## 7.3. Kod legacy: `LegacyInvoiceFormatter`

```java
String displayedCustomer = customer == null
        ? "UNKNOWN" : customer.trim().toUpperCase(Locale.ROOT);
for (InvoiceLine line : lines) {
    BigDecimal lineTotal = line.unitPrice()
            .multiply(BigDecimal.valueOf(line.quantity()));
    subtotal = subtotal.add(lineTotal);
    result.append(line.sku()).append(" x ").append(line.quantity())
          .append(" = ").append(lineTotal.setScale(2, RoundingMode.HALF_UP))
          .append('\n');
}
BigDecimal tax = subtotal.multiply(new BigDecimal("0.23"))
        .setScale(2, RoundingMode.HALF_UP);
BigDecimal total = subtotal.add(tax).setScale(2, RoundingMode.HALF_UP);
```

Klasa łączy normalizację danych klienta, obliczenia, zaokrąglenia i budowę tekstu w jednej metodzie `format`.

---

## 7.3. Test charakteryzujący formatera

```java
@Test
void documentsCurrentFormattingAndRounding() {
    List<InvoiceLine> lines = List.of(
            new InvoiceLine("BOOK", 2, new BigDecimal("19.99")),
            new InvoiceLine("PEN", 1, new BigDecimal("5.00")));

    String result = formatter.format("  Acme  ", lines);

    assertEquals(String.join("\n", "INVOICE", "Customer: ACME",
            "BOOK x 2 = 39.98", "PEN x 1 = 5.00", "Subtotal: 44.98",
            "Tax: 10.35", "Total: 55.33", ""), result);
}
```

- Test zapisuje kompletny tekst: usuwanie spacji, wielkie litery, stawkę podatku, zaokrąglenie i końcowy znak nowego wiersza; drugi test dokumentuje `UNKNOWN` dla `null`.
- Słowo `documents` w nazwie sygnalizuje, że świadomie dokumentujemy zastany wynik, a nie wymaganie.

---

## 7.4. Dobór przypadków

Nie trzeba odtwarzać całej przestrzeni wejść. Priorytet mają:

- granice warunków i wartości tuż po obu ich stronach; wartości puste, brakujące, zerowe i ujemne,
- reguły zaokrąglania, strefy czasowe i przejścia dat,
- rozgałęzienia prowadzące do efektów zewnętrznych oraz formaty konsumowane poza komponentem,
- dane spotykane w produkcji, przypadki z historii awarii, błędy i częściowo wykonane operacje.

Test nie powinien utrwalać przypadkowej implementacji: gdy kolejność nie jest kontraktem - porównuj zbiory; gdy ID jest losowe - sprawdź format lub kontroluj źródło ID.

---

## 7.5. Golden master, snapshot i approval testing

- **Golden master** - zapisany duży wynik traktowany jako historyczna wyrocznia dla kolejnych wykonań.
- **Snapshot** - zapisany wynik porównywany w kolejnych wykonaniach; **approval testing** akcentuje świadomy przegląd i zatwierdzanie różnicy.
- Terminologia nie jest ustandaryzowana i pojęcia się nakładają, ale żadne nie jest dokładnym synonimem testu charakteryzującego.
- Duży zapis ma wartość, gdy format jest stabilny i przeglądalny, dane reprezentują istotny scenariusz, różnica jest czytelna, a niedeterminizm znormalizowany.
- Aktualizacja pliku oczekiwanego bez czytania różnicy usuwa zabezpieczenie; snapshot z setkami nieistotnych pól ukrywa ważną zmianę w szumie.

---

## 8.1. Refaktoryzacja pod ochroną testów

```java
public String format(String customer, List<InvoiceLine> lines) {
    BigDecimal subtotal = calculateSubtotal(lines);
    BigDecimal tax = money(subtotal.multiply(TAX_RATE));
    BigDecimal total = money(subtotal.add(tax));
    StringBuilder result = new StringBuilder("INVOICE\n")
            .append("Customer: ").append(displayedCustomer(customer))
            .append('\n');
    appendLines(result, lines);
    return result
            .append("Subtotal: ").append(money(subtotal)).append('\n')
            .append("Tax: ").append(tax).append('\n')
            .append("Total: ").append(total).append('\n')
            .toString();
}
private static BigDecimal money(BigDecimal v) { return v.setScale(2, RoundingMode.HALF_UP); }
```

Stała stawka, brak waluty i walidacji to świadome uproszczenia - refaktoryzacja **nie upoważnia** do dodawania tych reguł.

---

## 8.2. Porównanie implementacji (test różnicowy)

```java
@ParameterizedTest
@MethodSource("representativeInvoices")
void refactoringPreservesObservedOutput(String customer, List<InvoiceLine> lines) {
    assertEquals(legacy.format(customer, lines),
                 refactored.format(customer, lines));
}
```

- Gdy stara implementacja jest deterministyczna, można przez ograniczony czas uruchamiać obie wersje dla tych samych danych - szczególnie dla wielu danych generowanych lub zanonimizowanych.
- Ograniczenia: potwierdza zgodność tylko dla wykonanych danych, nie wykryje defektu obecnego w obu wersjach, stara implementacja nie jest niezależną specyfikacją.
- Efekty uboczne mogą uniemożliwiać podwójne wykonanie, a wydajność wymaga osobnego pomiaru - dlatego zachowaj też testy ze stałymi, przejrzanymi oczekiwaniami.

---

## 8.3. Sekwencja transformacji formatera

1. Uruchomić testy charakteryzujące i zapisać zieloną bazę.
2. Wyodrębnić `displayedCustomer` → testy.
3. Wyodrębnić `lineTotal` → testy.
4. Wyodrębnić wspólną regułę `money` → testy.
5. Wyodrębnić `calculateSubtotal` → testy.
6. Wyodrębnić `appendLines` → testy.
7. Nazwać stawkę jako `TAX_RATE` → testy.
8. Uruchomić pełny zestaw i obejrzeć różnicę.

Każdy krok ma **jedną intencję** - gdy po wydzieleniu `money` zmieni się wynik, zakres poszukiwania przyczyny jest niewielki.

---

## 8.4. Jawna zmiana zachowania

Właściciel produktu potwierdza: brak klienta ma powodować wyjątek zamiast `UNKNOWN`. Praca przechodzi w **tryb funkcjonalny**:

1. Zapisać nowe wymaganie w osobnym teście.
2. Zobaczyć oczekiwaną porażkę tego testu.
3. Zaimplementować nową regułę.
4. Świadomie usunąć lub zmienić sprzeczne oczekiwanie charakteryzujące.
5. Uruchomić testy regresji i opisać zmianę kontraktu.

Tej korekty nie należy przedstawiać jako refaktoryzacji - obserwowalny wynik został **celowo zmieniony**.

---

## 9.1. Seam i punkt aktywacji

- **Seam (szew)** to miejsce, w którym można zmienić zachowanie programu bez edytowania kodu w tym miejscu; **punkt aktywacji** to miejsce wyboru alternatywnego zachowania.
- Przykłady: interfejs z implementacją wybieraną w konstruktorze lub konfiguracji, metoda do nadpisania, fabryka adapterów, wybór implementacji przy budowaniu/uruchamianiu.
- Sam interfejs nie jest użytecznym szwem, jeśli metoda na stałe tworzy konkretną implementację - musi istnieć dostępny punkt wyboru współpracownika.
- W Javie dominują **object seams** (polimorfizm, przekazywanie zależności); **link seams** są mniej lokalne; **preprocessing seam** nie ma praktycznego zastosowania.
- Wywołanie statyczne, np. `LocalDate.now(...)`, nie daje szwu obiektowego - trzeba przekazać źródło czasu lub owinąć wywołanie metodą.

---

## 9.2. Dwa powody rozrywania zależności

- **Separacja** - uruchomienie badanego kodu bez trudnej, wolnej lub niedostępnej zależności (np. klient bazy wymagający infrastruktury).
- **Obserwacja** - odczyt wyniku lub efektu, którego test obecnie nie widzi (np. bezpośredni zapis do `System.out`).
- Globalny zegar to inny praktyczny problem: kod da się uruchomić, ale test nie kontroluje pośredniego wejścia, więc wykonanie nie jest deterministyczne.
- Jeden szew może rozwiązać kilka problemów naraz, ale warto nazywać je osobno.

---

## 9.3. Punkt wyjścia: `LegacyReminderService`

```java
public boolean sendRenewalReminder(Subscription subscription) {
    LocalDate today = LocalDate.now(ZoneOffset.UTC);
    if (subscription.renewalDate().isAfter(today.plusDays(7))) {
        return false;
    }
    System.out.printf("Sent renewal reminder to %s for %s%n",
            subscription.email(), subscription.renewalDate());
    return true;
}
```

- Warunek ma tylko górną granicę `today + 7`: wysyłka dla daty za 7 dni, dzisiejszej i **przeszłych**; za 8 dni - brak wysyłki.
- Brak dolnej granicy może być defektem, ale jego korekta nie należy do refaktoryzacji.
- Czas pobierany globalnie, wysyłka sprzężona z decyzją biznesową - testowanie przez zmianę zegara systemowego wiąże test z procesem uruchomieniowym.

---

## 9.4. Minimalny szew przejściowy

```java
public class SeamedReminderService {
    public boolean sendRenewalReminder(Subscription subscription) {
        LocalDate today = currentDate();
        if (subscription.renewalDate().isAfter(today.plusDays(7))) {
            return false;
        }
        sendMessage(subscription.email(), subscription.renewalDate());
        return true;
    }
    protected LocalDate currentDate() { return LocalDate.now(ZoneOffset.UTC); }
    protected void sendMessage(String email, LocalDate renewalDate) {
        System.out.printf("Sent renewal reminder to %s for %s%n", email, renewalDate);
    }
}
```

Technika **Subclass and Override Method**: produkcja wykonuje te same operacje, punktem aktywacji w teście jest `new TestableReminderService(...)`, a podmianę realizuje dynamiczne wiązanie.

---

## 9.4. Test szwu i koszt rozszerzalności

```java
private static final class TestableReminderService extends SeamedReminderService {
    @Override protected LocalDate currentDate() { return today; }
    @Override protected void sendMessage(String email, LocalDate renewalDate) {
        messages.add(email + "|" + renewalDate);
    }
}
```

- Testy sprawdzają deterministycznie: 7 dni → wysyłka, 8 dni → brak, data przeszła → wysyłka (dokumentacja zastanego zachowania).
- Zmiana `final` na klasę rozszerzalną z metodami `protected` zmienia kontrakt rozszerzalności i powierzchnię API.
- To refaktoryzacja, gdy typ jest wewnętrzny, a konsumenci pod kontrolą; w publicznej bibliotece wymaga analizy kompatybilności i planu migracji.

---

## 9.4. Szew przejściowy: zalety i koszty

**Zalety:**
- wymaga niewielkiej zmiany sygnatur i miejsc tworzenia obiektu,
- szybko udostępnia czas i efekt do kontroli, pozwala scharakteryzować regułę graniczną przed większą przebudową.

**Koszty:**
- rozszerza powierzchnię dziedziczenia tylko dla zastępowania zależności i wiąże test z metodami `protected`,
- może zachęcać do rozwoju hierarchii bez znaczenia domenowego i nie nazywa jawnie portu komunikacyjnego.

Minimalny szew ma **odblokować test i następny krok** - oznacz go jako przejściowy, jeśli nie jest docelowym projektem.

---

## 9.5. Jawne zależności jako rozwiązanie docelowe

```java
public final class ReminderService {
    private final Clock clock;
    private final ReminderGateway reminderGateway;
    // konstruktor z Objects.requireNonNull(...)
    public boolean sendRenewalReminder(Subscription subscription) {
        LocalDate today = LocalDate.now(clock);
        if (subscription.renewalDate().isAfter(today.plusDays(7))) {
            return false;
        }
        reminderGateway.send(subscription.email(), subscription.renewalDate());
        return true;
    }
    public interface ReminderGateway { void send(String email, LocalDate renewalDate); }
}
```

- `Clock` kontroluje czas, `ReminderGateway` nazywa efekt; punktem aktywacji obu szwów jest `new ReminderService(...)`.
- Techniki Feathersa: **Parameterize Constructor** i **Extract Interface**.

---

## 9.5. Test docelowego projektu

```java
Clock clock = Clock.fixed(Instant.parse("2026-08-30T10:00:00Z"), ZoneOffset.UTC);
RecordingReminderGateway gateway = new RecordingReminderGateway();
ReminderService service = new ReminderService(clock, gateway);
Subscription subscription = new Subscription(
        "developer@example.com", LocalDate.of(2026, 9, 6));

boolean sent = service.sendRenewalReminder(subscription);

assertTrue(sent);
assertEquals(List.of("developer@example.com|2026-09-06"), gateway.messages());
```

- Test używa `Clock.fixed` i obiektu **spy** zapisującego wiadomości; te same trzy przypadki graniczne co wcześniej.
- W produkcji konstruktor dostaje zegar systemowy z ustaloną strefą i rzeczywisty adapter - reguła biznesowa nie zna technologii wysyłki.

---

## 9.6. Bezpieczna kolejność rozrywania zależności

1. Nazwij zachowanie i efekt do zachowania; wybierz punkt uruchomienia i obserwacji.
2. Wprowadź najmniejszy szew bez zmiany logiki.
3. Zabezpiecz zachowanie testami, w tym wartości graniczne.
4. Przenieś wybór zależności do jawnego punktu aktywacji.
5. Zastąp zależność w teście, pozostawiając rzeczywisty adapter w produkcji.
6. Dodaj osobny test integracyjny adaptera, jeśli jego kontrakt niesie ryzyko.
7. Usuń konstrukcję przejściową, gdy jawny projekt jest już bezpieczny.

---

## 9.7. Czego unikać przy rozrywaniu zależności

- Zmieniania widoczności prywatnej metody tylko po to, by testować jej implementację bezpośrednio.
- Dodawania interfejsu do każdej klasy niezależnie od potrzebnej zmienności.
- Ukrywania grafu zależności w globalnym rejestrze usług.
- Zastępowania adaptera własnym fake, gdy rzeczywisty kontrakt adaptera nigdy nie jest testowany.
- Wprowadzania wielu warstw abstrakcji przed uzyskaniem pierwszego działającego testu.
- Pozostawiania rozwiązania przejściowego bez decyzji, czy ma stać się projektem docelowym.

---

## 10.1. Warsztat - sposób pracy

- Ćwiczenia wykonujemy w krótkich iteracjach: po każdej transformacji kod się kompiluje, a adekwatne testy są zielone.
- Krok zmieniający zachowanie nazywamy zmianą funkcjonalną i oddzielamy od transformacji strukturalnych.

Przed rozpoczęciem:

1. `mvn -version` - potwierdź, że Maven korzysta z Javy 25.
2. Przejdź do katalogu `refactoring-legacy` i uruchom `mvn clean verify`.
3. Pracuj wyłącznie w pakiecie `pl.training.module2`.
4. Ustal sposób zapisywania małych punktów przywracania.

---

## Ćwiczenie 1: granica refaktoryzacji (15 + 5 min)

Dla każdej propozycji uporządkowania usługi rozliczeniowej określ: czy to refaktoryzacja, jakie zachowanie jest obserwowalne, kto je konsumuje, jakiego dowodu potrzebujesz przed i po oraz czy trzeba ją podzielić.

- **A.** Zmiana nazwy prywatnej metody `calc` na `calculateTax`.
- **B.** Zastąpienie `double` przez `BigDecimal`, bo produkcja wykazuje błędy zaokrągleń.
- **C.** Przeniesienie publicznej klasy DTO do innego pakietu.
- **D.** Wydzielenie budowy komunikatu do osobnej metody bez zmiany tekstu.
- **E.** Dodanie pamięci podręcznej do odczytu kursu walut.

**Kryterium:** odpowiedź nie opiera się na nazwie operacji - wskazuje konkretną obserwację i mechanizm weryfikacji.

---

## Ćwiczenie 2: charakterystyka i refaktoryzacja formatera (35 + 10 min)

1. Przeczytaj `LegacyInvoiceFormatter` bez zmian, wypisz widoczne reguły, uruchom testy charakteryzujące.
2. Zbuduj macierz przypadków: pusta lista, klient `null`, cena wymagająca zaokrąglenia, suma niezaokrąglona ≠ suma prezentowana; dodaj testy tylko dla brakujących.
3. Ustal, które reguły są kontraktem, a które zastanym zachowaniem.
4. Refaktoryzuj małymi krokami (klient, wartość wiersza, suma, zaokrąglenie, pozycje), testując po każdym kroku.
5. Porównaj `LegacyInvoiceFormatter` i `InvoiceFormatter`, obejrzyj końcową różnicę.

**Ograniczenia:** nie zmieniaj stawki, zaokrąglania, tekstu ani białych znaków; nie dodawaj walidacji ani walut; nie łącz wydzieleń w jedną edycję.

---

## Ćwiczenie 3: obiekty zastępcze i pokrycie (25 + 10 min)

**Część A - `OrderPlacementServiceTest`:** dla każdego współpracownika określ rolę, rodzaj weryfikacji (stan/wynik/interakcja), defekt, który test wykryje i którego nie wykryje.
- Który fake nadaje się do wspólnego testu kontraktu z adapterem produkcyjnym i jak obserwować obie implementacje?
- Czy sprawdzanie kolejności zapisu i publikacji jest uzasadnione? Odwołaj się do kontraktu transakcyjnego.

**Część B - raport JaCoCo:** wyjaśnij różnicę linii i gałęzi dla `DeliveryFee`, zaproponuj test wykrywający zmianę `100` → `200`, przykład testu podnoszącego pokrycie bez ochrony oraz rozsądne użycie progu.

---

## Ćwiczenie 4: wprowadzenie szwu (25 + 10 min)

Punkt wyjścia: `LegacyReminderService` i `Subscription`.

1. Wskaż niedeterministyczne wejście i efekt trudny do obserwacji; nazwij górną granicę i skutek braku dolnej.
2. Wprowadź minimalny szew - owiń czas i wysyłkę metodami możliwymi do nadpisania.
3. Napisz deterministyczne testy: dokładnie 7 dni, 8 dni, data przeszła.
4. Zastąp szew jawnymi zależnościami `Clock` i `ReminderGateway`, przenieś testy.
5. Wskaż punkt aktywacji każdego szwu i test potrzebny dla rzeczywistego adaptera.

**Kryteria:** test nie zależy od bieżącej daty ani globalnego wyjścia; kod domenowy nie tworzy adaptera; rozwiązanie przejściowe odróżnione od docelowego.

---

## Odpowiedzi: Ćwiczenie 1 (1/2)

| Poz. | Ocena | Obserwacja i weryfikacja |
| --- | --- | --- |
| A | zwykle refaktoryzacja | sprawdź odwołania tekstowe i refleksję; potem automatyczna zmiana nazwy, kompilacja, testy |
| B | zmiana zachowania | najpierw strukturalnie wydziel obliczenie, potem osobno zmień typ i oczekiwany wynik |
| C | zależy od kontraktu | w bibliotece pakiet jest częścią nazwy typu - potrzebna migracja konsumentów |

---

## Odpowiedzi: Ćwiczenie 1 (2/2)

| Poz. | Ocena | Obserwacja i weryfikacja |
| --- | --- | --- |
| D | refaktoryzacja | kontraktem jest dokładna treść i białe znaki - test charakteryzujący tekst |
| E | zwykle optymalizacja | zmienia świeżość, opóźnienie, pamięć, awarie - testy poprawności, wygaszania i pomiar |

**Wniosek:** etykieta operacji nie rozstrzyga jej charakteru - decydują cel i obserwacje, które mają pozostać niezmienione.

---

## Odpowiedzi: Ćwiczenie 2

- Widoczne reguły: nagłówek `INVOICE`, klient `trim` + wielkie litery, `UNKNOWN` dla `null`, kolejność pozycji, 2 miejsca `HALF_UP`, podatek 23%, końcowy znak nowego wiersza.
- Kluczowe: suma częściowa liczona z wartości **przed** zaokrągleniem prezentacji - kolejność zaokrągleń może mieć istotny wpływ finansowy i wymaga potwierdzenia domenowego.
- Brakujący przypadek: dwie pozycje po `0.005` × 1 - każdy wiersz pokazuje `0.01`, a suma częściowa wynosi `0.01`, nie `0.02`.
- Stałe oczekiwania są niezależne od starej klasy, a porównanie implementacji łatwo rozszerzyć o dane - oba się uzupełniają, ale mogą utrwalić historyczny defekt.

---

## Odpowiedzi: Ćwiczenie 3 (1/2)

| Współpracownik | Rola | Może wykryć | Nie wykryje |
| --- | --- | --- | --- |
| `catalogStub` | stub | pominięcie ceny, złe mnożenie | błędne wyszukiwanie SKU |
| `paymentStub` | stub | zgubienie ID autoryzacji | złego tokenu, kwoty, integracji |
| `repositoryFake` | fake | zły `OrderDraft`, brak zapisu | SQL, mapowania, transakcji |
| `publisherSpy` | spy | brak/złą liczbę/treść zdarzeń | serializacji, brokera |
| `paymentMock` | mock | zły protokół, wielokrotne obciążenie | działania bramki produkcyjnej |
| `publisherStub` | stub | nic sam nie wykrywa | defektów publikacji |

`publisherStub` to stub, nie dummy - metoda `publish` jest rzeczywiście wywoływana.

---

## Odpowiedzi: Ćwiczenie 3 (2/2)

- Repozytorium w pamięci nie wykryje błędnego SQL, ograniczeń, mapowania `BigDecimal`, izolacji transakcji ani generatora ID - adapter wymaga wąskiego testu integracyjnego.
- `find` nie należy do `OrderRepository`, więc wspólny kontrakt obejmuje tylko elementy wspólne; nie dodawaj metody odczytu do interfejsu produkcyjnego tylko dla testów.
- Kolejność zapisu i publikacji warto sprawdzać tylko, gdy wynika z mechanizmu niezawodności (np. publikacja po trwałym zapisie, outbox).
- `DeliveryFee.fee(false)` z asercją na dokładnie `100`; test bez asercji podniesie pokrycie, ale nie wykryje zmiany na `200`.
- Próg może blokować spadek pokrycia zmienianego pakietu, ale globalny procent nie może ukrywać braku ochrony reguły krytycznej.

---

## Odpowiedzi: Ćwiczenie 4

- Globalny czas uniemożliwia kontrolę pośredniego wejścia, a `System.out` utrudnia obserwację efektu.
- Przypadki: 7 dni → wysyłka, 8 dni → brak, data przeszła → wysyłka (brak dolnej granicy); odrzucanie dat przeszłych to **osobna zmiana funkcjonalna**.
- `SeamedReminderService`: punkt aktywacji to utworzenie `TestableReminderService`; `ReminderService`: punkt aktywacji to `new ReminderService(...)`.
- Test jednostkowy nie potwierdzi serializacji, uwierzytelniania, obsługi odpowiedzi i ponowień adaptera - potrzebny wąski test integracyjny.
- Szew przejściowy usuń osobnym, małym krokiem po przeniesieniu wywołań i testów, a potem uruchom pełny zestaw.

---

## Sprawdzenie wiedzy (1/2)

1. **Refaktoryzacja vs zmiana funkcjonalna?** Refaktoryzacja zachowuje uzgodnione obserwacje, zmiana funkcjonalna celowo modyfikuje co najmniej jedną.
2. **Czy obserwowalne = wartość zwracana?** Nie - także wyjątki, efekty, protokoły, formaty, API i właściwości pozafunkcjonalne.
3. **Po co małe kroki?** Ostatnia mała transformacja jest głównym podejrzanym - łatwo ją przeanalizować lub wycofać.
4. **Co znaczy zielony zestaw?** Żaden wykonany test nie wykrył różnicy - to nie dowód braku wszystkich regresji.
5. **Czemu piramida nie narzuca proporcji?** Koszt i wartość testu zależą od architektury, technologii i ryzyka.
6. **Stub vs mock?** Stub dostarcza odpowiedzi; mock zawiera oczekiwania interakcji i wymaga ich weryfikacji.

---

## Sprawdzenie wiedzy (2/2)

7. **Ryzyko bazy w pamięci?** Niesprawdzone zapytania, mapowanie, ograniczenia, transakcje i zachowanie silnika.
8. **Gałęzie vs linie?** Pokrycie gałęzi pokazuje pominiętą stronę decyzji, choć wszystkie linie mogły zostać wykonane.
9. **Czemu test charakteryzujący nie dowodzi wymagania?** Zapisuje obserwowany wynik, który może być defektem lub przypadkowym szczegółem.
10. **Seam vs punkt aktywacji?** Seam to miejsce możliwej zmiany zachowania; punkt aktywacji - miejsce wyboru wariantu.
11. **Dwie potrzeby rozrywania zależności?** Separacja od trudnej zależności i obserwacja niedostępnego efektu.
12. **Kiedy szew przez dziedziczenie jest przejściowy?** Gdy dziedziczenie istnieje tylko dla testowalności i nie opisuje relacji domenowej.

---

## Listy kontrolne: przed i podczas refaktoryzacji

**Przed:**
- Cel, granica, zachowania obserwowalne i konsumenci są nazwane; projekt buduje się powtarzalnie (Java 25).
- Awarie testów są wyjaśnione, wybrano szybkie testy, ryzykowne zachowanie ma test charakteryzujący.
- Czas, losowość i dane zewnętrzne są kontrolowane; istnieje punkt przywracania.

**Podczas:**
- Krok ma jedną intencję i wiadomo, czy praca jest strukturalna, czy funkcjonalna.
- Regularna kompilacja, szybkie testy po każdym kroku, szerszy zestaw w odstępach.
- Diff bez przypadkowego formatowania; nie aktualizuje się oczekiwań bez zrozumienia różnicy; każda nowa abstrakcja rozwiązuje konkretną przeszkodę.

---

## Lista kontrolna: przed zakończeniem

- Pełny zestaw testów przechodzi, a ważne adaptery i kontrakty integracyjne zostały sprawdzone.
- Raport pokrycia przejrzano pod kątem ryzykownych luk, a dla kluczowej asercji wykonano test kontrolnej zmiany.
- Cały diff przejrzano pod kątem zmiany zachowania; zmiany funkcjonalne są oddzielone i opisane.
- Rozwiązania przejściowe zostały usunięte albo jawnie oznaczone.
- Instrukcja uruchomienia pozostaje aktualna.

---

## Podsumowanie - najważniejsze wnioski (1/2)

Bezpieczna refaktoryzacja nie wynika z jednego narzędzia ani wskaźnika - opiera się na nazwanym kontrakcie, małych transformacjach i kilku źródłach informacji zwrotnej.

- Refaktoryzacja zmienia strukturę, **zachowując nazwane obserwacje**; poprawkę defektu i nową funkcję oddzielaj od zmiany strukturalnej.
- Testy zwiększają poziom dowodu, ale nie dają pewności absolutnej.
- Piramida testów jest heurystyką kosztu i informacji, a nie receptą liczbową.
- Stub, spy, fake i mock opisują **role w konkretnym teście**, nie typy obiektów z biblioteki.

---

## Podsumowanie - najważniejsze wnioski (2/2)

- Pokrycie wskazuje niewykonany kod, ale nie ocenia poprawności wymagań ani siły asercji.
- Test charakteryzujący zapisuje zastane zachowanie, które nadal wymaga interpretacji i decyzji domenowej.
- Szew udostępnia punkt zmiany zachowania, a punkt aktywacji pozwala wybrać jego wariant.
- Rozrywanie zależności ma być **minimalnym krokiem** do kontrolowanego wykonania i obserwacji.
- Rozwiązanie przejściowe nie musi być projektem docelowym - ale wymaga świadomej decyzji o jego losie.
