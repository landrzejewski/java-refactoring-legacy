<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E04SeatPicker\Start;

use InvalidArgumentException;
use LogicException;
use Training\Workshop\Exercises\E04SeatPicker\HallPlan;

/**
 * Start: klient sceny - sesja kasjera wykonuje skrypt operacji na koszyku miejsc i zwraca jego stan.
 * Błąd przerywa skrypt; wynik pokazuje komunikat i stan koszyka po błędzie.
 */
final class SeatPickerSession
{
    public function __construct(private readonly HallPlan $plan)
    {
    }

    /** @param list<string> $script */
    public function run(array $script): string
    {
        $picker = new SeatPicker($this->plan);
        try {
            foreach ($script as $line) {
                $picker->apply($line);
            }
            return $picker->state();
        } catch (InvalidArgumentException | LogicException $exception) {
            return 'ERROR: ' . $exception->getMessage() . ' @ ' . $picker->state();
        }
    }
}
