import { IllegalArgumentError } from '../../../../src/shared/errors.js';
import { TicketOrder } from '../../../../src/workshop/m5/s30_facade/TicketOrder.js';

/** Wspólny kontrakt wszystkich wariantów Cinema (start, step1..3). */
export interface AnyCinema {
  onlineShop(): { buy(order: TicketOrder): string };
  boxOffice(): { sell(order: TicketOrder): string };
  state(): string;
}

/** Wiersz: "online|desk SEANS MIEJSCA,PO,PRZECINKU TYP KARTA EMAIL" albo "state". */
export function channels(cinema: AnyCinema): (line: string) => string {
  return (line) => {
    const [command = '', screening = '', seats = '', type = '', card = '', email = ''] = line.split(' ');
    if (command === 'state') {
      return cinema.state();
    }
    const order = new TicketOrder(screening, seats.split(','), type, card, email);
    try {
      return command === 'online' ? cinema.onlineShop().buy(order) : cinema.boxOffice().sell(order);
    } catch (exception) {
      if (exception instanceof IllegalArgumentError) {
        return `ERROR ${exception.message}`;
      }
      throw exception;
    }
  };
}
