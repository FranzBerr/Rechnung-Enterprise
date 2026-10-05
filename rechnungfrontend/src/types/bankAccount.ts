import type { Bank } from "./bank";

export interface BankAccount {

  id: number;

  companyId: number;

  bankId: number;

  accountHolder: string;

  iban: string;

  defaultAccount: boolean;

  active: boolean;

  bank?: Bank;
}

export interface BankAccountRequest {

  companyId: number;

  bankId: number;

  accountHolder: string;

  iban: string;

  defaultAccount: boolean;

  active: boolean;
}
