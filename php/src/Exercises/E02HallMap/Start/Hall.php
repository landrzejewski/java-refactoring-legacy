<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E02HallMap\Start;

use InvalidArgumentException;
use Training\Workshop\Exercises\E02HallMap\HallSpec;

/** Start: sala to nazwa i lista sektorów. Sama nic nie liczy - liczy klient, który zna cały układ. */
final class Hall
{
    /** @param list<Section> $sections */
    private function __construct(private readonly string $name, private readonly array $sections)
    {
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

    /** @return list<Section> */
    public function sections(): array
    {
        return $this->sections;
    }
}
