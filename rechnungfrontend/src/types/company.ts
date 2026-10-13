export interface Company {
  id: number;

  name: string;

  legalForm?: string;

  street: string;

  zipCode: string;

  city: string;

  country: string;

  taxNumber: string;

  vatId: string;

  phone?: string;

  email?: string;

  website?: string;

  active: boolean;
}

export interface CompanyRequest {

  companyName: string;

  street: string;

  postalCode: string;

  city: string;

  country: string;

  taxNumber: string;

  vatId: string;
}
