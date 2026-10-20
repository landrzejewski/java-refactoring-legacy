/** Seam dla efektu ubocznego "wysłany mail" - w teście lambda zapisująca do dziennika. */
export type Mailer = (to: string, text: string) => void;
