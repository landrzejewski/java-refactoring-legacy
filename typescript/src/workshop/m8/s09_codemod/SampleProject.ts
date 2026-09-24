/** Plik próbki: ścieżka względem katalogu głównego projektu i treść. */
export interface ProjectFile {
  readonly path: string;
  readonly text: string;
}

/**
 * Próbka "projektu" dla codemodu: API kina ze starą sygnaturą book(..., boolean, boolean)
 * oznaczoną jako przestarzała, łudząco podobne API hotelu i klasa kliencka do migracji.
 * Kod jako tekst - codemod działa na źródłach, nie na modułach sceny.
 */
export class SampleProject {
  static readonly API: readonly ProjectFile[] = Object.freeze([
    {
      path: 'cinema/BookingService.ts',
      text: `import { Channel } from './Channel.js';
import { Glasses } from './Glasses.js';

export class BookingService {
  /** @deprecated boolean-y zamieniono na Channel i Glasses */
  book(screening: string, email: string, seats: string[], types: string[],
    web: boolean, ownGlasses: boolean): string;
  book(screening: string, email: string, seats: string[], types: string[],
    channel: Channel, glasses: Glasses): string;
  book(screening: string, email: string, seats: string[], types: string[],
    channel: boolean | Channel, glasses: boolean | Glasses): string {
    const how = typeof channel === 'boolean' ? (channel ? Channel.WEB : Channel.BOX_OFFICE) : channel;
    const own = typeof glasses === 'boolean' ? (glasses ? Glasses.OWN : Glasses.RENTED) : glasses;
    return screening + ' ' + how + ' ' + own + ' ' + email + seats.length + types.length;
  }
}
`,
    },
    {
      path: 'cinema/Channel.ts',
      text: `export enum Channel { WEB = 'WEB', BOX_OFFICE = 'BOX_OFFICE' }
`,
    },
    {
      path: 'cinema/Glasses.ts',
      text: `export enum Glasses { OWN = 'OWN', RENTED = 'RENTED' }
`,
    },
    {
      path: 'hotel/HotelService.ts',
      text: `export class HotelService {
  book(hotel: string, email: string, rooms: string[], guests: string[],
    breakfast: boolean, parking: boolean): string {
    return hotel + email + rooms.length + guests.length + breakfast + parking;
  }
}
`,
    },
  ]);

  /** Klient do migracji; numery linii mają znaczenie dla testów (wywołania w 11, 16-18, 22). */
  static readonly TICKET_DESK = `// desk/TicketDesk.ts - klient do migracji

import { BookingService } from '../cinema/BookingService.js';
import { HotelService } from '../hotel/HotelService.js';

export class TicketDesk {
  private readonly bookings = new BookingService();
  private readonly hotels = new HotelService();

  online(email: string, seats: string[], types: string[]): string {
    return this.bookings.book('S1', email, seats, types, true, false);
  }

  boxOffice(seats: string[], types: string[], own: boolean): string {
    // stary przyklad: bookings.book('S1', 'x', seats, types, false, true)
    return this.bookings.book('S2', 'kasa@kino.pl',
      seats, types,
      false, own);
  }

  stay(email: string, rooms: string[], guests: string[]): string {
    return this.hotels.book('H1', email, rooms, guests, true, true);
  }
}
`;

  private constructor() {}
}
