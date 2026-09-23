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
# Moduł 4. Podstawowe refaktoryzacje - warsztat praktyczny
Małe transformacje, precyzyjna semantyka: od Rename do Extract Class i hermetyzacji stanu

---

## Agenda

1. Kontrakt i rytm małych transformacji
2. Rename, Extract Method, Extract Variable i Extract Constant
3. Replace Magic Numbers with Named Constants
4. Inline Variable i Inline Method
5. Move Method, Move Field i Extract Class
6. Encapsulate Field, Encapsulate Collection, Encapsulate Conditional
7. Studium przypadku w Javie 25: generator oferty wynajmu
8. Warsztat praktyczny, wnioski, sprawdzenie wiedzy i lista kontrolna

---

## 1.1. Co ma pozostać niezmienione

- Refaktoryzacja zachowuje **ustalone, obserwowalne zachowanie** - zakres obserwacji zależy od systemu.
- Publiczna biblioteka: także zgodność źródłowa i binarna, refleksja, serializacja, konfiguracja.
- Kontraktem bywa kolejność wyjątków i efektów, blokady, transakcje, proxy, wydajność.
- Walidacja, kopia defensywna czy inne miejsce zaokrąglenia to już **zmiana zachowania**.
- Najpierw przenieś strukturę, a zmianę zachowania wykonaj osobno, z wymaganiem i testem.

---

## 1.2. Pętla robocza i punkt zatrzymania

- **Pętla:** test wykrywa kontrolowaną zmianę → jedna transformacja → kompilacja → najwęższy test → mały commit.
- Okresowo pełny build i testy integracyjne - test lokalny nie widzi wszystkich ryzyk.
- IDE nie zna kontraktów zewnętrznych, ciągów tekstowych ani wymagań biznesowych.
- Seria refaktoryzacji potrzebuje **konkretnego celu**; Extract i Inline to przeciwne kierunki tego samego wyboru.

---

## 2.1. Rename - cel i mechanika

| Słaba nazwa | Lepszy kierunek |
| --- | --- |
| `d` | `discount` / `deliveryDate` |
| `process()` | `createRentalQuote()` |
| `manager` | konkretna odpowiedzialność |
| `list` | `dailyRates` |

- Nazwa opisuje **rolę w kontekście**, nie typ ani implementację.
- Nazwa żyje poza Javą: refleksja, konfiguracja, JSON/SQL/ORM, szablony, serializacja.
- Publiczne API wymaga **strategii migracji** (stara metoda delegująca, oznaczona jako przestarzała), nie tylko operacji IDE.

---

## 2.2. Extract Method - cel i sygnały

- Przenosi spójny fragment do metody, gdy **nazwa wyrazi intencję** lepiej niż szczegóły.
- Sygnały: komentarz opisuje cel bloku, mieszane poziomy abstrakcji, powtórzona wiedza.
- Także: predykat z nazwą domenową, potrzeba osobnego testu lub przeniesienia odpowiedzialności.
- **Długość nie jest wystarczającym kryterium.**

---

## 2.3. Extract Method - analiza przepływu danych

| Sytuacja | Typowe rozwiązanie |
| --- | --- |
| wartość tylko odczytywana | parametr lub spójny obiekt |
| wartość powstaje i jest używana później | wartość zwracana |
| modyfikowanych kilka wartości lokalnych | podziel fragment lub obiekt wyniku |
| używa wielu pól innego obiektu | możliwy sygnał Move Method |
| lista parametrów nadmiernie rośnie | zła granica lub brak spójnego pojęcia |

Zachowaj granicę semantyczną: ani cały obiekt „na wszelki wypadek”, ani kilkanaście prymitywów.

---

## 2.4. Extract Method - mechanika i trudne przypadki

- **Mechanika:** najmniejszy fragment → nazwa → parametry i wynik → kopia bez upraszczania → wywołanie → test.
- Uwaga na `return`/`break`/`continue`, `try-with-resources`, `catch`/`finally`, `synchronized`.
- Także `this`/`super`, metody przesłaniane, lambdy, przeciążenia, efekty w getterach.
- Nie wolno przesunąć efektu przed warunek, zmienić liczby wywołań ani wydłużyć trzymania blokady.

---

## 2.5. Extract Variable i moment ewaluacji

- Nazywa **znaczenie, nie składnię**; najmniejszy zasięg, bez nadpisywania.
- Może zmienić **moment** ewaluacji, a scalenie wystąpień - **liczbę** ewaluacji.
- Ważne, gdy wyrażenie zmienia stan, czyta czas lub losowość, sięga do I/O albo rzuca wyjątek.
- Dwa `clock.instant()` → jeden `now` bywa dobre, ale **nie jest neutralne** - ustal, czy kontrakt wymaga jednego snapshotu czasu.

---

## 2.6. Extract Constant i `final`

- Stała dla stabilnej wartości wspólnego pojęcia z jednym właścicielem.
- Nie dla konfiguracji środowiska ani danych zmienianych bez wdrożenia.
- `final` blokuje tylko przypisanie - `static final List` może być modyfikowalna.
- Publiczne `static final int`/`String` z wyrażeniem stałym to **stała czasu kompilacji** - klient bez rekompilacji widzi starą wartość.
- `static final BigDecimal` nie jest wklejany do kodu klienta.

---

## 3.1. Replace Magic Numbers with Named Constants

- Problemem jest **ukryta decyzja**, nie sama liczba.
- Studium: `7` = próg długiego wynajmu, `8.00` = ubezpieczenie, `25.00` = dostawa, `0.23` = VAT.
- Nazwa opisuje rolę (`LONG_RENTAL_DAYS`), nie wartość (`SEVEN`).
- Dwa identyczne literały to nie zawsze ta sama wiedza: 7 dni na zwrot ≠ 7 dni progu rabatu.
- Extract Constant to mechanika, Replace Magic Number to powód.

---

## 4.1. Inline Variable i typ docelowy

- Gdy nazwa nic nie wnosi lub blokuje dalszą refaktoryzację; zastępuj **jedno użycie na raz**.
- Inicjalizator wykonany raz i czytany trzy razy - po naiwnym Inline wykona się trzy razy.
- Jawny typ lokalny może wybrać **inne przeciążenie** niż `var`; prymityw a opakowanie - unboxing.
- Lambda wymaga typu docelowego i nie może być inicjalizatorem `var`.

---

## 4.2. Inline Method i jego ryzyka

- Usuwa pośrednictwo bez znaczenia; **liczba linii nie przesądza** - `qualifiesForLongRentalDiscount()` zostaje.
- Parametr użyty dwa razy nie może stać się dwoma wywołaniami argumentu z efektem.
- Wklejenie ciała **usuwa dynamiczną dyspozycję** metody przesłanianej.
- Może zniknąć blokada `synchronized` oraz granica proxy (transakcje, autoryzacja).
- Najbezpieczniej: **prywatny, niepolimorficzny delegat z jednym wywołaniem**.

---

## 5.1. Move Method i Move Field - wybór właściciela

- Zachowanie i stan trafiają do typu o właściwej odpowiedzialności i cyklu życia.
- Sygnały: metoda używa głównie danych innego obiektu, zmiana reguły dotyka nie-właściciela.
- Także: pole używane głównie przez inny komponent, dane i zachowanie tworzące spójny klaster.
- **Feature Envy to sygnał do analizy, nie nakaz** - metoda może celowo koordynować obiekty.

---

## 5.2. Move Method krok po kroku i ryzyka

- Zabezpiecz testem wynik, wyjątki, efekty i kolejność.
- Utwórz metodę w klasie docelowej, przenieś ciało bez zmian, starą zamień w **delegat**; migruj wywołania pojedynczo.
- Delegat może zostać jako fasada kompatybilności.
- Ryzyka: pominięte przesłonięcie, zmiana dostępu, `synchronized` na innym obiekcie, adnotacje na starym delegacie.

---

## 5.3. Move Field krok po kroku i ryzyka

- Znajdź wszystkie odczyty, zapisy i użycia refleksyjne.
- Migruj najpierw odczyty, potem zapisy; stare pole usuń po potwierdzeniu **jednego źródła prawdy**.
- Bez dual write - dwie zapisywalne kopie rozjadą się po wyjątku lub błędzie współbieżności.
- Zachowaj `final`/`volatile`/`transient`/`static`; sprawdź serializację, ORM, `equals`/`hashCode`.

---

## 5.4. Extract Class - cel, mechanika i zły wynik

- Spójne pola i operacje z **odrębnym powodem zmiany**; w studium cena ≠ format → `RentalPricing`.
- Mechanika: nowa klasa, relacja własności, przenoś pola, potem metody; źródło jako delegująca fasada.
- Decyzja projektowa; Move Field i Move Method to mechanika jej realizacji.
- Referencja zwrotna tworzy cykl - lepiej przekazać wartości lub wąski kontrakt.
- **Zły wynik:** dane przeniesione, logika nadal w źródle przez gettery.

---

## 6.1. Encapsulate Field

- Dostęp przez operacje właściciela - kontrola zmian, inwariant, możliwa zmiana reprezentacji.
- `renameTo` komunikuje dozwoloną zmianę lepiej niż ogólne `setName`.
- **Migracja:** trywialne akcesory na tym samym polu → migracja klientów → zwężenie widoczności.
- Walidację i operacje domenowe dodaj osobno - zmieniają akceptowane dane i wyjątki.
- `volatile`, blokady i adnotacje nie przechodzą same na metodę.

---

## 6.2. Encapsulate Collection

- Właściciel kontroluje członkostwo: odczyt przez kontrakt, zmiana przez `add`/`remove` lub polecenia.
- Najpierw scharakteryzuj kolejność, duplikaty, `null`, aliasy i zachowanie iteratorów.
- Kopia wejścia w konstruktorze **albo świadome zachowanie** starego aliasu.
- Walidację i zakaz `null` oddziel od samej hermetyzacji.
- Migawka **nie zapewnia bezpieczeństwa wątkowego** obiektu.

---

## 6.3. Trzy różne kontrakty kolekcji

| Implementacja | Klient zmienia | Widzi późniejsze zmiany | Kopia głęboka |
| --- | :---: | :---: | :---: |
| `Collections.unmodifiableList(internal)` | nie | tak (żywy widok) | nie |
| `List.copyOf(internal)` | nie | nie (migawka) | nie |
| `new ArrayList<>(internal)` | tak, własną kopię | nie | nie |

- Niemodyfikowalność blokuje członkostwo, **nie wnętrze elementów**.
- `List.copyOf` odrzuca `null` i może zwrócić istniejący obiekt - nie opieraj kontraktu na tożsamości.

---

## 6.4. Encapsulate Conditional

- Pytanie o implementację → pytanie o znaczenie: `request.days() >= 7` → `qualifiesForLongRentalDiscount(request)`.
- Decompose Conditional: osobno predykat i obie gałęzie, każdy krok testowany.
- **Krótkie spięcie jest zachowaniem** - nie obliczaj wcześniej prawej strony osłony przed `null`.
- `instanceof` z wzorcem: zakres zmiennej zależy od przepływu.
- Nazwa `is`/`has` nie gwarantuje czystości predykatu.

---

## 7.1. Studium przypadku: kontrakt generatora oferty

- Wiertnica 39,99/dzień, generator 120,00; od 7 dni rabat 10% **od podstawowego kosztu**.
- Ubezpieczenie 8,00/dzień, dostawa 25,00, VAT 23% netto.
- Każda pośrednia kwota HALF_UP do 2 miejsc; klient przycięty i wielkimi literami.
- Niezależność od locale, dokument kończy się nową linią.
- Inny moment zaokrąglenia czy rabat od dodatków = **zmiana zachowania**.

---

## 7.2. Etap 0: kod początkowy

```java
public String createQuote(RentalRequest r) {
    BigDecimal a = money(rates.get(r.equipmentType())
            .multiply(BigDecimal.valueOf(r.days())));
    BigDecimal d = r.days() >= 7
            ? money(a.multiply(disc)) : money(BigDecimal.ZERO);
    BigDecimal i = r.insurance()
            ? money(new BigDecimal("8.00")
                    .multiply(BigDecimal.valueOf(r.days())))
            : money(BigDecimal.ZERO);
    BigDecimal f = r.delivery() ? new BigDecimal("25.00") : money(BigDecimal.ZERO);
    BigDecimal n = money(a.subtract(d).add(i).add(f));
    BigDecimal v = money(n.multiply(new BigDecimal("0.23")));
    BigDecimal t = money(n.add(v));
    String q = "RENTAL QUOTE\n" + "Customer: " + ... + "Total: " + t.toPlainString() + "\n";
    return q;
}
```

Wynik poprawny, ale reguły, formatowanie, nazwy i literały są wymieszane.

---

## 7.3. Test charakterystyki przed pierwszą zmianą

- **Pełny dokument**, oba boki progu rabatu (6 i 7 dni), warianty ubezpieczenia i dostawy.
- Przypadki zaokrągleń: 1 dzień wiertnicy → VAT 9.20, 15 dni → rabat 59.99.
- Test **nie ocenia** reguł - chroni je przed przypadkową „poprawką”.

```java
assertEquals("""
        RENTAL QUOTE
        Customer: ACME
        ...
        Net: 953.00
        VAT: 219.19
        Total: 1172.19
        """, service.createQuote(request));
```

---

## 7.4. Etap 1: lokalne transformacje

```java
private static final int LONG_RENTAL_DAYS = 7;
private static final BigDecimal VAT_RATE = new BigDecimal("0.23");

public String createQuote(RentalRequest request) {
    BigDecimal dailyRate = dailyRates.get(request.equipmentType());
    BigDecimal rentalDays = BigDecimal.valueOf(request.days());
    BigDecimal baseRentalCost = money(dailyRate.multiply(rentalDays));
    BigDecimal discount = calculateDiscount(request, baseRentalCost);
    // insuranceCost, deliveryCost i netAmount - analogicznie
    BigDecimal vat = money(netAmount.multiply(VAT_RATE));
    BigDecimal total = money(netAmount.add(vat));
    return buildDocument(request, baseRentalCost, discount, /* ... */ total);
}
```

Rename → Extract Constant → Extract Variable/Method → Encapsulate Conditional → Inline `q`; test po każdym kroku, **kolejność obliczeń bez zmian**.

---

## 7.5. Extract Class: `RentalPricing` i `PriceBreakdown`

- Etap 1 miesza **wycenę i prezentację**; reguły, stawki i zaokrąglenie → `RentalPricing`.
- Wynik to rekord `PriceBreakdown` z nazwanymi kwotami (nieujemne, skala 2).
- Sekwencja: prywatny `calculatePrice` → nowy typ → ciało źródła zamienione na delegowanie.
- Zawsze **jedno źródło prawdy** dla każdej stawki i reguły.
- Walidacja w konstruktorze `RentalPricing` to nowe API - w produkcji osobna decyzja.

---

## 7.6. Etapy 2-3: delegat i Inline Method

```java
public RentalQuoteService() { this(RentalPricing.standard()); }

public String createQuote(RentalRequest request) {
    Objects.requireNonNull(request, "request");
    PriceBreakdown price = pricing.calculate(request); // po Inline calculatePrice
    return buildDocument(request, price);
}
```

- Bezargumentowy konstruktor zachowuje publiczną sygnaturę (także dla skompilowanych klientów).
- `calculatePrice`: prywatny, jedno wywołanie, bez proxy i blokad → Inline Method.
- `buildDocument` i `qualifiesForLongRentalDiscount` **zostają** - nazywają odpowiedzialność i regułę.

---

## 7.7. Test równoważności etapów

- Porównanie tylko ze starą wersją nie wystarcza - **obie mogą mieć ten sam błąd**.
- Każdy etap (0-3) porównujemy z **niezależnie zatwierdzonym wynikiem**.
- Locale `ar-EG` w teście wymusza `String.format(Locale.ROOT, ...)` - `formatted(...)` mogłoby zmienić cyfry w `%d`.

```java
assertAll(
    () -> assertEquals(expectedQuote, stage0.createQuote(request)),
    () -> assertEquals(expectedQuote, stage3.createQuote(request)));
```

---

## 7.8. Encapsulate Field i Collection w studium

```java
public final class LegacyEquipmentCatalog {
    public String name;
    public final Map<EquipmentType, BigDecimal> dailyRates; // alias bez kopii
}
// EquipmentCatalog (faza B):
public void renameTo(String newName) { name = validName(newName); }
public Map<EquipmentType, BigDecimal> dailyRates() {
    return Map.copyOf(dailyRates);
}
```

- `final` nie blokuje mutacji mapy; akcesory przejściowe **zachowują aliasowanie**.
- `EquipmentCatalog` to **świadoma zmiana kontraktu**: kopia do `EnumMap`, walidacja, migawki.

---

## 7.9. Uruchamialny przykład

- `Module4Examples` porównuje ofertę etapu 0 i etapu 3 dla tego samego żądania.
- Gdy wyniki się różnią, rzuca wyjątek - refaktoryzacja zmieniła ofertę.
- Pokazuje też, że migawka katalogu sprzed `changeDailyRate` zachowuje stawkę 39.99.
- Oczekiwane linie: `Quote stages equivalent: true` i `Catalog snapshot isolated: true`.
- Oba bieguny modułu: **równoważność** etapów i **jawna zmiana** kontraktu katalogu.

---

## Ćwiczenie 1: lokalne porządkowanie metody (30 + 10 min)

- Cel: uporządkować `stage0.RentalQuoteService` technikami lokalnymi bez zmiany bajtu wyniku.
- Najpierw sprawdź czułość testu kontrolowaną zmianą progu rabatu.
- **Nie** zmieniaj sygnatury `createQuote` ani miejsc zaokrąglania; bez nowej klasy.

Szczegóły: zadania modułu 4, Ćwiczenie 1.

---

## Ćwiczenie 2: Extract i Inline pod presją semantyki (25 + 10 min)

- Cel: ocenić, czy dane Extract/Inline zachowują zachowanie, i jaki test to wykaże.
- Przypadki: `clock.instant()`, krótkie spięcie, efekt w argumencie, lambda w `var`, przesłanianie, `synchronized`.
- Na koniec bezpieczny Inline Method między etapami 2 i 3 + `RentalQuoteStagesEquivalenceTest`.

Szczegóły: zadania modułu 4, Ćwiczenie 2.

---

## Ćwiczenie 3: Extract Class i przeniesienie odpowiedzialności (35 + 15 min)

- Cel: z `stage1.RentalQuoteService` wydzielić `RentalPricing` i `PriceBreakdown`.
- Formatowanie zostaje w `RentalQuoteService`; zachowaj bezargumentowy punkt wejścia.
- Jedno źródło prawdy, jawne zależności, oferty identyczne na każdym etapie.

Szczegóły: zadania modułu 4, Ćwiczenie 3.

---

## Ćwiczenie 4: kontrola mutowalnego stanu (30 + 15 min)

- Cel: hermetyzacja `LegacyEquipmentCatalog` w dwóch fazach.
- **Faza A:** ukrycie pól z zachowaniem obserwacji (także aliasu); **faza B:** zatwierdzona zmiana kontraktu.
- Test musi odróżniać migawkę od żywego widoku.

Szczegóły: zadania modułu 4, Ćwiczenie 4.

---

## Wspólna retrospektywa (ok. 5 min po każdym ćwiczeniu)

Zespół odpowiada na cztery pytania:

1. Jaka obserwowalna właściwość była chroniona?
2. Który krok był mechaniczny, a który wymagał decyzji projektowej?
3. Jaki najmniejszy test dawał wiarygodny sygnał?
4. W którym momencie należało zakończyć serię transformacji?

---

## Kluczowe wnioski z omówienia

- Kolejność: Rename lokalnych symboli → stałe → Extract Variable/Method; nie każdą operację trzeba wydzielać.
- Inline `now`, wcześniejsze `isActive()`, efekt w argumencie, Inline `synchronized` - **niebezpieczne**.
- Inline prywatnego delegata etapu 2 - bezpieczne; stała VAT - gdy to ta sama reguła.
- `PriceBreakdown` zastępuje listę równoległych wartości spójnym typem wyniku.
- Fazy A i B ćwiczenia 4 **nie trafiają do jednego commitu**.

Pełne omówienie: rozwiązania modułu 4.

---

## Sprawdzenie wiedzy

1. Dlaczego zielony test jednostkowy nie dowodzi kompatybilności biblioteki?
2. Kiedy Extract Variable lub Inline Variable zmienia zachowanie?
3. Dlaczego publiczna stała `int`/`String` wymaga ostrożności?
4. Co może zginąć przy Inline Method metody przesłanianej lub `synchronized`?
5. Jak bezpiecznie wykonać Move Field?
6. Czym różni się niemodyfikowalny widok od migawki i czy `Map.copyOf` kopiuje głęboko?
7. Dlaczego porównanie ze starą implementacją nie wystarcza?

---

## Lista kontrolna

- **Przed krokiem:** znam chronione zachowanie; test pokrywa ryzyko; sprawdziłem ewaluację, wyjątki, efekty i kontrakt tekstowy.
- **Extract/Inline:** krótkie spięcie, przeciążenia, lambdy, przesłanianie, proxy i blokady zachowane.
- **Rename:** użycia tekstowe, konfiguracja, serializacja; publiczne API migrowane przez delegat.
- **Move/Extract Class:** właściwy właściciel, jedno źródło prawdy, brak nowych cykli.
- **Encapsulate:** znane aliasy; świadomy wybór widoku, migawki lub kopii.
- **Na koniec:** etapy równoważne, wskazane kroki będące **zmianą zachowania**.

---

## Podsumowanie - najważniejsze wnioski

- Małe transformacje są bezpieczne tylko przy **precyzyjnym rozumieniu semantyki** Javy i kontraktu.
- Rename może dotknąć kontraktu tekstowego lub publicznego.
- Extract i Inline mogą zmienić liczbę ewaluacji, przeciążenie, dyspozycję lub blokadę.
- Move wymaga jednego źródła prawdy; hermetyzacja - decyzji o aliasowaniu i własności stanu.
- Liczy się **sekwencja kroków** z jawnym celem i testem, nie nazwa techniki.
- Nowe reguły walidacji i własności to **osobna, świadoma decyzja**.
