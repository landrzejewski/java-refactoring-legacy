<?php

declare(strict_types=1);

namespace Training\Workshop\Tests\Exercises\E02HallMap;

use InvalidArgumentException;
use PHPUnit\Framework\Attributes\Test;
use PHPUnit\Framework\TestCase;
use ReflectionClass;
use ReflectionProperty;
use SplObjectStorage;
use Training\Workshop\Exercises\E02HallMap\SectionSpec;
use Training\Workshop\Exercises\E02HallMap\Step3\Hall;
use Training\Workshop\Exercises\E02HallMap\Step3\HallElement;
use Training\Workshop\Exercises\E02HallMap\Step3\HallGroup;
use Training\Workshop\Exercises\E02HallMap\Step3\Row;
use Training\Workshop\Exercises\E02HallMap\Step3\Seat;
use Training\Workshop\Exercises\E02HallMap\Step3\SeatCategories;
use Training\Workshop\Exercises\E02HallMap\Step3\SeatCategory;
use Training\Workshop\Exercises\E02HallMap\Step3\Section;

/** Kontrakt rozwiązania: jedno drzewo, jedno przejście, jedna instancja kategorii na kod. */
final class E02SolutionTest extends TestCase
{
    #[Test]
    public function fourHundredSeatsShareAtMostFourCategoryInstances(): void
    {
        $rows = array_map(
            fn (int $i) => E02EquivalenceTest::row('R' . $i, $i <= 16 ? 'NNNNNNNNNNNNNNNNNNNN' : 'VVVVVVVVVVVVVVVVVVVV'),
            range(1, 20),
        );
        $imax = Hall::of(E02EquivalenceTest::hall('IMAX', new SectionSpec('Parter', $rows)));

        /** @var SplObjectStorage<SeatCategory, null> $instances */
        $instances = new SplObjectStorage();
        $seats = [];
        foreach ($imax as $seat) {
            $instances->offsetSet($seat->category());
            $seats[] = $seat;
        }

        self::assertCount(400, $seats);
        self::assertCount(2, $instances, 'jedna instancja na kod kategorii');
        self::assertSame($seats[0]->category(), $seats[319]->category(), 'pierwszy i ostatni fotel normalny');
        self::assertSame($seats[320]->category(), $seats[399]->category(), 'pierwszy i ostatni fotel VIP');
        self::assertLessThanOrEqual(4, count(SeatCategories::known()), 'fabryka zna najwyzej cztery kategorie');
    }

    #[Test]
    public function seatKeepsOnlyItsOwnStateAndCategoryKnowsNoPlace(): void
    {
        self::assertEqualsCanonicalizing(['label', 'category'], self::fieldNames(Seat::class));
        foreach (self::fieldNames(SeatCategory::class) as $name) {
            self::assertTrue(
                !str_contains($name, 'label') && !str_contains($name, 'row'),
                'kategoria nie wie, gdzie stoi fotel',
            );
        }
        // Różnica względem Javy: odpowiednikiem pola final jest właściwość readonly.
        foreach ((new ReflectionClass(SeatCategory::class))->getProperties() as $property) {
            self::assertTrue($property->isReadOnly(), 'wspoldzielony stan musi byc niemutowalny');
        }
    }

    #[Test]
    public function iterationSkipsGapsAndKeepsSectionAndRowOrder(): void
    {
        $hall = Hall::of(E02EquivalenceTest::hall(
            'Mala',
            E02EquivalenceTest::section('Parter', E02EquivalenceTest::row('A', 'N_N'), E02EquivalenceTest::row('B', '_V')),
            E02EquivalenceTest::section('Balkon', E02EquivalenceTest::row('C', 'D_')),
        ));
        // Różnica względem Javy: brak domyślnej metody Iterable.forEach - przejście zwykłą pętlą foreach.
        $labels = [];
        foreach ($hall as $seat) {
            $labels[] = $seat->label();
        }
        self::assertSame(['A1', 'A3', 'B2', 'C1'], $labels);
    }

    #[Test]
    public function everyLevelOfTheHallAnswersTheSameQuestions(): void
    {
        foreach ([Hall::class, Section::class, Row::class] as $level) {
            self::assertTrue(is_a($level, HallGroup::class, true), (new ReflectionClass($level))->getShortName() . ' jest wezlem drzewa');
        }
        self::assertTrue(is_a(Seat::class, HallElement::class, true), 'miejsce jest lisciem o tym samym kontrakcie');

        $hall = Hall::of(E02EquivalenceTest::hall(
            'Mala',
            E02EquivalenceTest::section('Parter', E02EquivalenceTest::row('A', 'NV'), E02EquivalenceTest::row('B', 'DD')),
        ));
        $counted = 0;
        foreach ($hall as $ignored) {
            $counted++;
        }
        self::assertSame($counted, $hall->capacity(), 'pojemnosc drzewa zgadza sie z liczba odwiedzonych miejsc');
        self::assertSame('22.00', (string) $hall->surcharges());
    }

    #[Test]
    public function factoryRejectsUnknownCodeWithTheSameMessageAsStart(): void
    {
        $this->expectException(InvalidArgumentException::class);
        $this->expectExceptionMessage('unknown seat category: X');
        SeatCategories::of('X');
    }

    /** @return list<string> */
    private static function fieldNames(string $type): array
    {
        return array_map(
            fn (ReflectionProperty $property) => $property->getName(),
            (new ReflectionClass($type))->getProperties(),
        );
    }
}
