<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E04SeatPicker\Step2;

use InvalidArgumentException;
use LogicException;
use Training\Workshop\Exercises\E04SeatPicker\HallPlan;
use Training\Workshop\Exercises\E04SeatPicker\Seat;

/**
 * Krok 2: AUTO - przed zmianą prosi koszyk o migawkę, a przywraca ją zarówno przy awarii, jak i przy
 * cofnięciu. Polecenie nie zna już pól koszyka. Świadoma zmiana zachowania: migawka obejmuje cały
 * koszyk, więc voucher nie ginie (w start i kroku 1 ginął, bo ręczna kopia go nie obejmowała).
 */
final class AutoArrange implements Command
{
    private const int LIMIT = 4;

    private ?Memento $before = null;

    public function __construct(private readonly Cart $cart, private readonly int $count)
    {
    }

    public function execute(): void
    {
        if ($this->count < 1 || $this->count > self::LIMIT) {
            throw new InvalidArgumentException('limit ' . self::LIMIT . ' miejsc');
        }
        $this->before = $this->cart->snapshot();
        $this->cart->clear();
        $this->cart->setOwnGlasses(false);
        $this->cart->setVoucher(null);
        try {
            $plan = $this->cart->plan();
            foreach ($plan->rowsFromMiddle() as $row) {
                $first = $this->freeRun($plan, $row);
                if ($first > 0) {
                    for ($i = 0; $i < $this->count; $i++) {
                        $this->cart->add(new Seat($row, $first + $i));
                    }
                    return;
                }
            }
            throw new LogicException('brak ' . $this->count . ' miejsc obok siebie');
        } catch (LogicException $exception) {
            // Różnica względem Javy: InvalidArgumentException dziedziczy po LogicException, więc ten catch
            // złapałby też błąd argumentu - w tym bloku żaden taki błąd nie powstaje.
            $this->undo();
            throw $exception;
        }
    }

    public function undo(): void
    {
        $this->cart->restore($this->before);
    }

    /** Numer pierwszego miejsca ciągu {@code count} wolnych miejsc w rzędzie albo 0. */
    private function freeRun(HallPlan $plan, int $row): int
    {
        $run = 0;
        for ($number = 1; $number <= $plan->seatsPerRow; $number++) {
            $run = $plan->isTaken(new Seat($row, $number)) ? 0 : $run + 1;
            if ($run === $this->count) {
                return $number - $this->count + 1;
            }
        }
        return 0;
    }
}
