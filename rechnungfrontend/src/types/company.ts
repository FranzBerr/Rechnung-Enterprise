export interface Company {

  id: number;

  companyName: string;

  street: string;

  postalCode: string;

  city: string;

  country: string;

  taxNumber: string;

  vatId: string;
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
