// "Serwer SMTP" starego systemu - statyczna skrzynka nadawcza.
export class LegacyMailer {
  static readonly SENT: string[] = [];

  private constructor() {}

  static send(to: string, subject: string, body: string): void {
    LegacyMailer.SENT.push(`MAIL to=${to} subject=${subject} body=${body}`);
  }

  static sms(phone: string, text: string): void {
    LegacyMailer.SENT.push(`SMS to=${phone} text=${text}`);
  }
}
