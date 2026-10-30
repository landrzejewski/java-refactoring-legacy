<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E02HallMap\Step2;

use InvalidArgumentException;
use IteratorAggregate;
use Training\Workshop\Exercises\E02HallMap\HallSpec;

/**
 * Krok 2: sala jest kolekcją miejsc - da się po niej przejść pętlą foreach. Typowany dostęp
 * do sektorów zniknął, bo klient już go nie potrzebuje.
 *
 * @implements IteratorAggregate<int, Seat>
 */
final class Hall extends HallGroup implements IteratorAggregate
{
    /** @param list<Section> $sections */
    private function __construct(private readonly string $name, array $sections)
    {
        parent::__construct($sections);
    }

    public static function of(HallSpec $spec): self
    {
        if ($spec->sections === []) {
            throw new InvalidArgumentException('hall ' . $spec->name . ' has no sections');
        }
        $sections = [];
        foreach ($spec->sections as $sectionSpec) {
            $sections[] = Section::of($sectionSpec);
        }
        return new self($spec->name, $sections);
    }

    public function name(): string
    {
        return $this->name;
    }

    public function getIterator(): SeatIterator
    {
        return new SeatIterator($this);
    }
}
