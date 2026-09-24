/** Najwęższy seam dla powiadomień: typ funkcyjny, w teście wystarczy lambda. */
export type ReminderSender = (to: string, subject: string, body: string) => void;
