export interface BankAccountDto {
    id: number;

    bankId: number | null;

    bankName: string | null;

    accountHolder: string | null;

    iban: string | null;

    bic: string | null;

    currencyCode: string | null;

    active: boolean;
}
