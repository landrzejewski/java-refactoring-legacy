<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E04SeatPicker\Step2;

use InvalidArgumentException;
use LogicException;
use Training\Workshop\Exercises\E04SeatPicker\HallPlan;

/** Krok 2: klient sceny - bez zmian. */
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
