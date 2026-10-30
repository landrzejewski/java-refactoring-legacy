<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E10Showtime\Start;

/**
 * Start: sterownik sali tylko składa urządzenia i spina je ze sobą ("każde z każdym"), a potem
 * przekazuje polecenie pierwszemu urządzeniu łańcucha. Kolejność reakcji jest rozsiana po
 * Doors, AdPlayer i Projector; tylko procedura awaryjna jest zapisana tutaj.
 */
final readonly class HallController
{
    private Lights $lights;
    private Sound $sound;
    private Doors $doors;
    private AdPlayer $adPlayer;
    private Projector $projector;

    public function __construct(DeviceLog $log)
    {
        $this->lights = new Lights($log);
        $this->sound = new Sound($log);
        $this->doors = new Doors($log);
        $this->adPlayer = new AdPlayer($log);
        $this->projector = new Projector($log);
        $this->doors->wire($this->lights, $this->adPlayer);
        $this->adPlayer->wire($this->lights, $this->sound, $this->projector);
        $this->projector->wire($this->sound, $this->lights, $this->doors);
    }

    public function startShow(string $title): void
    {
        $this->doors->close($title);
    }

    public function endShow(): void
    {
        $this->projector->stop();
    }

    public function emergency(): void
    {
        $this->lights->set(100);
        $this->doors->open();
        $this->sound->set(0);
        $this->adPlayer->stop();
        $this->projector->cut();
    }

    // Zdarzenia sprzętu (w teście symulowane poleceniami skryptu).

    public function adTick(): void
    {
        $this->adPlayer->tick();
    }

    public function doorObstacle(): void
    {
        $this->doors->obstacle();
    }

    public function doorCleared(): void
    {
        $this->doors->cleared();
    }

    public function volume(int $level): void
    {
        $this->sound->set($level);
    }
}
