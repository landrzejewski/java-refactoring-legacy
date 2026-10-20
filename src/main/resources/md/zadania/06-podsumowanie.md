# Moduł 6. Podsumowanie - zadania dla uczestników

## Jak pracować

Ćwiczenie końcowe łączy wszystkie moduły szkolenia: diagnozę (moduł 1), prewencję i opis zmian (moduł 2), sieć bezpieczeństwa (moduł 3), katalog wzorców (moduł 4) i refaktoryzację do wzorców lub od wzorców (moduł 5). Pracujemy na prawdziwej God Class starego systemu CineLegacy, chronionej przez golden master.

Zasady:

- Klasa `CinemaManager` ma w nagłówku ostrzeżenie "NIE REFAKTORYZUJ tej klasy bezpośrednio". Dotyczy ono głównej linii repozytorium. W tym ćwiczeniu pracujemy **na własnej gałęzi** i po zajęciach ją porzucamy.
- `CinemaManagerGoldenMasterTest` musi być zielony **po każdym kroku**. Pliku `src/test/resources/workshop/cinema-manager.approved.txt` nie zmieniamy - jego zmiana oznaczałaby zmianę zachowania.
- Każdą lukę w sieci bezpieczeństwa najpierw zamykamy testem charakterystyki, dopiero potem zmieniamy kod w tym obszarze.
- Jedna intencja na krok i commit. Jeśli w trakcie pracy chcesz poprawić błąd albo zmienić regułę biznesową - zapisz to na liście "zmiany zachowania na później", nie rób tego teraz.
- Pracujcie w parach albo w zespołach trzyosobowych; jedna osoba prowadzi klawiaturę, druga pilnuje planu i listy kontrolnej.

### Środowisko

```bash
# własna gałąź na czas ćwiczenia
git switch -c final/<nazwa-zespolu>

# golden master starego systemu
mvn -q test -Dtest=CinemaManagerGoldenMasterTest

# po zajęciach: powrót i porzucenie gałęzi
git switch -
git branch -D final/<nazwa-zespolu>
```

Pliki:

- `src/main/java/pl/training/workshop/legacy/CinemaManager.java` - God Class: repertuar, rezerwacje, cennik, płatności, zwroty, program lojalnościowy, powiadomienia, raporty, rozliczenia z dystrybutorem,
- `src/main/java/pl/training/workshop/legacy/LegacyDb.java`, `LegacyMailer.java`, `LegacyPaymentGateway.java` - globalny stan i statyczne integracje,
- `src/test/java/pl/training/workshop/legacy/CinemaManagerGoldenMasterTest.java` - golden master,
- `src/test/java/pl/training/workshop/legacy/CinemaManagerScript.java` - scenariusz "jednego dnia kina", który produkuje porównywany wynik.

Własne testy charakterystyki umieszczaj w pakiecie `pl.training.workshop.legacy` w `src/test/java` (dostęp do pakietowo-prywatnego pola `CinemaManager.clock`). Zawsze zaczynaj od `LegacyDb.clear()` i ustaw zegar, a w `finally` przywróć `CinemaManager.clock = LocalDateTime::now` - tak jak `CinemaManagerScript`.

Odpowiedniki w innych językach: `csharp/src/Training.Workshop/Legacy/CinemaManager.cs` z testem `csharp/tests/Training.Workshop.Tests/Legacy/CinemaManagerGoldenMasterTest.cs` (`cd csharp && dotnet test tests/Training.Workshop.Tests --filter FullyQualifiedName~CinemaManagerGoldenMaster`) oraz `typescript/src/workshop/legacy/CinemaManager.ts` z testem `typescript/test/workshop/legacy/CinemaManagerGoldenMasterTest.test.ts` (`cd typescript && npx vitest run test/workshop/legacy`).

Reguły biznesowe CineLegacy (ceny, zniżki, statusy, zwroty, rozliczenia) są zebrane w opisie warsztatu: `src/main/resources/md/warsztat/00-cinelegacy.md`, sekcja "Reguły biznesowe CineLegacy".

### Przegląd zadania

| Etap | Treść | Czas |
| --- | --- | --- |
| 1 | Diagnoza zapachów | 15 min |
| 2 | Ocena sieci bezpieczeństwa i jej luk | 10 min |
| 3 | Wybór 2-3 refaktoryzacji i plan małych kroków | 15 min |
| 4 | Wykonanie jednej refaktoryzacji | 20-25 min |
| 5 | Retrospektywa | 10 min |

W wersji skróconej (45 minut) pomiń etap 4 albo ogranicz go do pierwszych dwóch kroków planu.

---

## Ćwiczenie końcowe: CineLegacy pod golden masterem

**Cel:** przejść pełny przepływ pracy z szkolenia na jednym, realistycznym fragmencie kodu legacy: od diagnozy, przez ocenę sieci bezpieczeństwa i plan, do jednego bezpiecznie wykonanego kroku w kierunku wzorca (albo od wzorca) i retrospektywy.

**Czas:** ok. 60-75 minut.

### Etap 1: diagnoza zapachów (15 min)

1. Przeczytaj `CinemaManager` bez zmieniania kodu. Dla każdej metody publicznej zapisz jedno zdanie: co robi i z jakimi zależnościami rozmawia.
2. Wypisz co najmniej 8 zapachów. Dla każdego podaj: nazwę z katalogu (moduł 1), miejsce (metoda, fragment), dowód (fakt w kodzie, nie ocena) i skutek dla najbliższej zmiany.
3. Dla każdego zapachu wskaż kandydata z mapy decyzji (moduł 5, moduł 6): refaktoryzację, docelową strukturę (wzorzec albo prostszą postać) i główne ryzyko.
4. Zaznacz zapachy, których **nie** warto teraz ruszać - i dlaczego.

Wskazówki do przejrzenia: struktura `Object[]` w `LegacyDb`, kody liczbowe i znakowe w komentarzu na początku klasy, zagnieżdżenie w `book`, warunki po statusie w `pay`, `cancel`, `use`, `expireOld`, wywołania `LegacyMailer` i `LegacyPaymentGateway`, pole `clock`, powtórzone fragmenty w `cancel` i `expireOld`, stałe liczbowe.

### Etap 2: ocena sieci bezpieczeństwa (10 min)

1. Przeczytaj `CinemaManagerScript` i zatwierdzony wynik. Wypisz, które zachowania golden master obejmuje, a których nie (np. które komunikaty błędów i które przejścia statusów nie występują w scenariuszu).
2. Wykonaj co najmniej trzy kontrolne mutacje w obszarze, który chcesz zmieniać (np. zmień komunikat błędu, procent lub próg). Dla każdej zapisz: mutacja - wynik golden mastera (czerwony / zielony).
3. Każda mutacja, która przeżyła, to luka. Dopisz test charakterystyki zamykający lukę w obszarze, który będziesz zmieniać w etapie 4. Cofnij mutację i sprawdź, że nowy test jest zielony, a z mutacją - czerwony.

### Etap 3: plan (15 min)

1. Wybierz 2-3 refaktoryzacje do wzorca lub od wzorca. Co najmniej jedna ma mieć niskie ryzyko i dać się wykonać w 20 minut.
2. Dla każdej wypełnij kartę:

| Pole | Treść |
| --- | --- |
| zapach i dowód | |
| refaktoryzacja (nazwa z katalogu) | |
| struktura docelowa | wzorzec albo prostsza postać |
| zachowanie chronione | wynik, komunikaty, efekty, kolejność, zaokrąglenia |
| test chroniący | golden master, nowy test charakterystyki |
| luki i decyzje | np. polityka dla nieznanego kodu |
| kroki | maksymalnie 6; każdy kompiluje się i zostawia zielone testy |
| czego nie robimy | zmiany zachowania odłożone na później |

3. Ustal kolejność refaktoryzacji i uzasadnij ją (np. "najpierw wycena, bo jest najlepiej pokryta golden masterem").

### Etap 4: wykonanie jednej refaktoryzacji (20-25 min)

1. Wykonaj pierwszą refaktoryzację z planu, krok po kroku, najlepiej automatami IDE.
2. Po każdym kroku: kompilacja, golden master, własne testy charakterystyki, commit z opisem intencji.
3. Jeśli krok okaże się większy niż planowany - zatrzymaj się, cofnij do ostatniego zielonego commita i podziel krok.
4. Zapisz w notatce każde miejsce, w którym kusiło cię "poprawić przy okazji" (np. arytmetykę na `double`, nazwę statusu, komunikat).

### Etap 5: retrospektywa (10 min)

Odpowiedzcie w zespole na pytania:

1. Który zapach był najłatwiejszy do zobaczenia, a który najtrudniejszy do usunięcia?
2. Ile mutacji przeżyło golden master? Co to mówi o jego zakresie i o tym, jak go używać?
3. Który krok okazał się większy, niż zakładał plan? Dlaczego?
4. Gdzie pojawiła się pokusa zmiany zachowania? Jak ją zapisaliście?
5. Która decyzja zasługuje na ADR?
6. Co z tego przepływu zastosujecie w pierwszym tygodniu pracy po szkoleniu?

### Kryteria akceptacji

- lista zapachów zawiera fakty z kodu, nie tylko oceny, i co najmniej jeden zapach świadomie pozostawiony,
- każda wybrana refaktoryzacja ma nazwę z katalogu, strukturę docelową, ryzyko i test chroniący,
- luki golden mastera w obszarze zmiany są potwierdzone mutacją i zamknięte testem charakterystyki przed zmianą,
- golden master jest zielony po każdym commicie, a plik zatwierdzonego wyniku nie został zmieniony,
- historia commitów pokazuje małe kroki z jedną intencją,
- zmiany zachowania są na liście "na później", a nie w kodzie.

### Oczekiwany produkt

- tabela zapachów (nazwa - miejsce - dowód - kandydat - ryzyko),
- wyniki kontrolnych mutacji i nowe testy charakterystyki,
- 2-3 karty planu,
- seria commitów dla jednej wykonanej refaktoryzacji,
- lista zmian zachowania odłożonych na później,
- notatka z retrospektywy (6 odpowiedzi, po jednym, dwóch zdaniach).

---

## Pytania końcowe

1. Po czym poznać, że zmiana jest refaktoryzacją, a nie zmianą zachowania?
2. Dlaczego zielony golden master nie dowodzi bezpieczeństwa każdej zmiany?
3. Jak wybrać pierwszy obszar do refaktoryzacji w dużym systemie legacy?
4. Kiedy wzorzec należy usunąć zamiast go rozbudowywać?
5. Jak rozstrzygnąć między Strategy, Command i State dla podobnego `switch`?
6. Co odróżnia Facade od Anti-Corruption Layer w kontekście migracji?
7. Jakie elementy powinien mieć opis zestawu zmian z refaktoryzacją?
8. Jak zmierzyć, czy refaktoryzacja w zespole przynosi efekt?
