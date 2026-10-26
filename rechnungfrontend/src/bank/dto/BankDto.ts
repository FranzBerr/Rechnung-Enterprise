export interface BankDto {
  id: number;

  bankName: string;

  bic: string | null;

  country: string | null;

  active: boolean;
}
