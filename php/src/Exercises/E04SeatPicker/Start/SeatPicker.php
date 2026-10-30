<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E04SeatPicker\Start;

use InvalidArgumentException;
use LogicException;
use Training\Workshop\Exercises\E04SeatPicker\HallPlan;
use Training\Workshop\Exercises\E04SeatPicker\Seat;
use Training\Workshop\Shared\Money;

/**
 * Start: koszyk miejsc kasjera w jednej klasie. Operacje wybiera {@code switch} po nazwie,
 * a cofanie to drugi łańcuch {@code if} z odwrotnością każdej operacji. Historia trzyma jeden
 * rekord "na wszystko" (pola używane zależnie od operacji). Przed automatyczną propozycją miejsc
 * koszyk jest zabezpieczany ręcznym punktem kontrolnym: kopią pól do zmiennych lokalnych.
 * Punkt kontrolny powstał, gdy koszyk miał tylko miejsca i okulary; voucher doszedł później
 * i nikt nie dopisał go do kopii.
 */
final class SeatPicker
{
    private const string BASE = '32.00';
    private const string VIP = '10.00';
    private const string GLASSES = '3.00';
    private const int LIMIT = 4;

    /** @var list<Seat> */
    private array $seats = [];
    private bool $ownGlasses = false;
    private ?string $voucher = null;
    /** @var list<Change> stos: ostatnia zmiana na końcu */
    private array $history = [];

    public function __construct(private readonly HallPlan $plan)
    {
    }

    public function apply(string $line): void
    {
        $parts = preg_split('/\s+/', trim($line));
        $op = strtoupper($parts[0]);
        switch ($op) {
            case 'SELECT':
                $seat = Seat::parse(self::arg($parts, 1));
                $this->select($seat);
                $this->history[] = new Change($op, $seat, null, 0, false, null, null);
                break;
            case 'DESELECT':
                $seat = Seat::parse(self::arg($parts, 1));
                $index = array_search($seat, $this->seats);
                if ($index === false) {
                    throw new InvalidArgumentException('miejsca ' . $seat . ' nie ma w koszyku');
                }
                array_splice($this->seats, $index, 1);
                $this->history[] = new Change($op, $seat, null, $index, false, null, null);
                break;
            case 'SWAP':
                $from = Seat::parse(self::arg($parts, 1));
                $to = Seat::parse(self::arg($parts, 2));
                $index = array_search($from, $this->seats);
                if ($index === false) {
                    throw new InvalidArgumentException('miejsca ' . $from . ' nie ma w koszyku');
                }
                $this->checkFree($to);
                $this->seats[$index] = $to;
                $this->history[] = new Change($op, $from, $to, $index, false, null, null);
                break;
            case 'GLASSES':
                $own = self::parseGlasses(self::arg($parts, 1));
                $this->history[] = new Change($op, null, null, 0, $this->ownGlasses, null, null);
                $this->ownGlasses = $own;
                break;
            case 'VOUCHER':
                $code = self::parseVoucher(self::arg($parts, 1));
                $this->history[] = new Change($op, null, null, 0, false, $this->voucher, null);
                $this->voucher = $code;
                break;
            case 'CLEAR':
                $this->history[] = new Change($op, null, null, 0, false, null, $this->seats);
                $this->seats = [];
                break;
            case 'AUTO':
                $this->autoArrange(self::parseCount(self::arg($parts, 1)));
                break;
            case 'UNDO':
                $this->undo();
                break;
            default:
                throw new InvalidArgumentException('nieznana operacja: ' . $parts[0]);
        }
    }

    private function select(Seat $seat): void
    {
        $this->checkFree($seat);
        if (count($this->seats) >= self::LIMIT) {
            throw new LogicException('limit ' . self::LIMIT . ' miejsc');
        }
        $this->seats[] = $seat;
    }

    private function checkFree(Seat $seat): void
    {
        if (!$this->plan->exists($seat)) {
            throw new InvalidArgumentException('nie ma miejsca ' . $seat);
        }
        if ($this->plan->isTaken($seat)) {
            throw new LogicException('miejsce ' . $seat . ' jest zajete');
        }
        if (in_array($seat, $this->seats)) {
            throw new LogicException('miejsce ' . $seat . ' jest juz w koszyku');
        }
    }

    /**
     * Automatyczna propozycja: czyści koszyk i wybiera {@code count} wolnych miejsc obok siebie
     * w najlepszym rzędzie. Gdy się nie uda, przywraca koszyk z punktu kontrolnego.
     */
    private function autoArrange(int $count): void
    {
        if ($count < 1 || $count > self::LIMIT) {
            throw new InvalidArgumentException('limit ' . self::LIMIT . ' miejsc');
        }
        // punkt kontrolny: kopia pól do zmiennych lokalnych
        $seatsBefore = $this->seats;
        $glassesBefore = $this->ownGlasses;
        $this->seats = [];
        $this->ownGlasses = false;
        $this->voucher = null;
        try {
            foreach ($this->plan->rowsFromMiddle() as $row) {
                $first = $this->freeRun($row, $count);
                if ($first > 0) {
                    for ($i = 0; $i < $count; $i++) {
                        $this->seats[] = new Seat($row, $first + $i);
                    }
                    $this->history[] = new Change('AUTO', null, null, 0, $glassesBefore, null, $seatsBefore);
                    return;
                }
            }
            throw new LogicException('brak ' . $count . ' miejsc obok siebie');
        } catch (LogicException $exception) {
            // Różnica względem Javy: InvalidArgumentException dziedziczy po LogicException, więc ten catch
            // złapałby też błąd argumentu - w tym bloku żaden taki błąd nie powstaje.
            $this->seats = $seatsBefore;
            $this->ownGlasses = $glassesBefore;
            throw $exception;
        }
    }

    /** Numer pierwszego miejsca ciągu {@code count} wolnych miejsc w rzędzie albo 0. */
    private function freeRun(int $row, int $count): int
    {
        $run = 0;
        for ($number = 1; $number <= $this->plan->seatsPerRow; $number++) {
            $run = $this->plan->isTaken(new Seat($row, $number)) ? 0 : $run + 1;
            if ($run === $count) {
                return $number - $count + 1;
            }
        }
        return 0;
    }

    private function undo(): void
    {
        if ($this->history === []) {
            throw new LogicException('nie ma czego cofnac');
        }
        $change = array_pop($this->history);
        if ($change->op === 'SELECT') {
            $index = array_search($change->seat, $this->seats);
            if ($index !== false) {
                array_splice($this->seats, $index, 1);
            }
        } elseif ($change->op === 'DESELECT') {
            array_splice($this->seats, $change->index, 0, [$change->seat]);
        } elseif ($change->op === 'SWAP') {
            $this->seats[$change->index] = $change->seat;
        } elseif ($change->op === 'GLASSES') {
            $this->ownGlasses = $change->flag;
        } elseif ($change->op === 'VOUCHER') {
            $this->voucher = $change->text;
        } elseif ($change->op === 'CLEAR') {
            array_push($this->seats, ...$change->seatsBefore);
        } elseif ($change->op === 'AUTO') {
            $this->seats = $change->seatsBefore;
            $this->ownGlasses = $change->flag;
        }
    }

    public function state(): string
    {
        $list = $this->seats === [] ? '-' : implode(' ', array_map(strval(...), $this->seats));
        return 'miejsca: ' . $list
            . ' | okulary: ' . ($this->ownGlasses ? 'wlasne' : 'wypozyczone')
            . ' | voucher: ' . ($this->voucher ?? '-')
            . ' | suma: ' . $this->total();
    }

    private function total(): Money
    {
        $sum = Money::zero();
        foreach ($this->seats as $seat) {
            $price = Money::of(self::BASE);
            if ($this->plan->isVip($seat)) {
                $price = $price->plus(Money::of(self::VIP));
            }
            if (!$this->ownGlasses) {
                $price = $price->plus(Money::of(self::GLASSES));
            }
            $sum = $sum->plus($price);
        }
        if ($this->voucher === 'KINO10') {
            $sum = $sum->minus($sum->percent(10));
        }
        return $sum;
    }

    /** @param list<string> $parts */
    private static function arg(array $parts, int $index): string
    {
        if (count($parts) <= $index) {
            throw new InvalidArgumentException('brak argumentu operacji ' . strtoupper($parts[0]));
        }
        return $parts[$index];
    }

    private static function parseGlasses(string $text): bool
    {
        return match (strtolower($text)) {
            'own' => true,
            'rent' => false,
            default => throw new InvalidArgumentException('okulary: own albo rent'),
        };
    }

    private static function parseVoucher(string $text): ?string
    {
        if (strcasecmp($text, 'none') === 0) {
            return null;
        }
        if ($text === 'KINO10') {
            return $text;
        }
        throw new InvalidArgumentException('nieznany voucher: ' . $text);
    }

    private static function parseCount(string $text): int
    {
        if (preg_match('/^[+-]?\d+$/', $text) !== 1 || (int) $text < -2147483648 || (int) $text > 2147483647) {
            throw new InvalidArgumentException('zla liczba miejsc: ' . $text);
        }
        return (int) $text;
    }
}
