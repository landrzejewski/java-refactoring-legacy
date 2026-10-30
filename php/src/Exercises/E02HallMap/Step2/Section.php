<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E02HallMap\Step2;

use InvalidArgumentException;
use Training\Workshop\Exercises\E02HallMap\SectionSpec;

/** Krok 2: sektor - węzeł nad rzędami; typowany dostęp do rzędów nie jest już nikomu potrzebny. */
final class Section extends HallGroup
{
    /** @param list<Row> $rows */
    private function __construct(private readonly string $name, array $rows)
    {
        parent::__construct($rows);
    }

    public static function of(SectionSpec $spec): self
    {
        if ($spec->rows === []) {
            throw new InvalidArgumentException('section ' . $spec->name . ' has no rows');
        }
        $rows = [];
        foreach ($spec->rows as $rowSpec) {
            $rows[] = Row::of($rowSpec);
        }
        return new self($spec->name, $rows);
    }

    public function name(): string
    {
        return $this->name;
    }
}
