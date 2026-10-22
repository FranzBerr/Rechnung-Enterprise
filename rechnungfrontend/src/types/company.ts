export interface Company {
  id: number;

  name: string;

  legalForm?: string;

  taxNumber: string;

  vatId: string;

  street: string;

  zipCode: string;

  city: string;

  country: string;

  phone?: string;

  email?: string;

  website?: string;

  active: boolean;
}

export interface CompanyRequest {
  name: string;

  legalForm?: string | null;

  taxNumber: string;

  vatId: string;

  street: string;

  zipCode: string;

  city: string;

  country: string;

  phone?: string  | null;

  email?: string | null;

  website?: string  | null;

  active: boolean;
}
