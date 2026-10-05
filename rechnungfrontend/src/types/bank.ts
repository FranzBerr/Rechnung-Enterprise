export interface Bank {

  id: number;

  bankCode?: string;

  bankName: string;

  bic: string;

  country?: string;

  active: boolean;
}

export interface BankRequest {

  bankCode?: string;

  bankName: string;

  bic: string;

  country?: string;

  active: boolean;
}
