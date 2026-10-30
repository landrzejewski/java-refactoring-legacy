<?php

declare(strict_types=1);

namespace Training\Workshop\Tests\Support;

use Closure;

/**
 * Pomocnik testów równoważności scen warsztatu.
 * Każdy wariant sceny (start, step1, ...) jest adaptowany do wspólnej
 * funkcji {@code I -> O}, a następnie sprawdzany na tych samych przypadkach.
 * Odpowiednik {@code @TestFactory} z JUnit: {@see cases()} zwraca dane dla {@code #[DataProvider]},
 * jeden przypadek na parę "wariant: przypadek".
 *
 * <pre>
 * public static function variants(): array
 * {
 *     return Scene::variants()
 *         ->variant('start', fn (Order $o) => (new Start\PriceCalculator())->price($o))
 *         ->variant('step1', fn (Order $o) => (new Step1\PriceCalculator())->price($o))
 *         ->expect('2D normal', $order2d, '25.00')
 *         ->cases();
 * }
 *
 * #[DataProvider('variants')]
 * public function testEveryVariantBehavesTheSame(Closure $implementation, mixed $input, mixed $expected): void
 * {
 *     self::assertEquals($expected, $implementation($input));
 * }
 * </pre>
 *
 * @template I
 * @template O
 */
final class Scene
{
    /** @var array<string, Closure(I): O> */
    private array $variants = [];

    /** @var list<array{string, I, O}> */
    private array $cases = [];

    private function __construct()
    {
    }

    public static function variants(): self
    {
        return new self();
    }

    /** @param Closure(I): O $implementation */
    public function variant(string $name, Closure $implementation): self
    {
        $this->variants[$name] = $implementation;
        return $this;
    }

    /**
     * @param I $input
     * @param O $expected
     */
    public function expect(string $name, mixed $input, mixed $expected): self
    {
        $this->cases[] = [$name, $input, $expected];
        return $this;
    }

    /** @return array<string, array{Closure(I): O, I, O}> */
    public function cases(): array
    {
        $cases = [];
        foreach ($this->variants as $variant => $implementation) {
            foreach ($this->cases as [$name, $input, $expected]) {
                $cases[$variant . ': ' . $name] = [$implementation, $input, $expected];
            }
        }
        return $cases;
    }
}
