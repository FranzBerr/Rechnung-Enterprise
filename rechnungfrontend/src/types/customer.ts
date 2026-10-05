export interface Customer {

  id: number;

  customerNumber: string;

  companyName: string;

  firstName?: string;

  lastName?: string;

  street: string;

  postalCode: string;

  city: string;

  country: string;

  email?: string;

  phone?: string;

  vatId?: string;

  defaultPaymentTermDays: number;

  defaultDiscountPercent: number;

  defaultDiscountDays: number;

  active: boolean;
}

export interface CustomerRequest {

  customerNumber: string;

  companyName: string;

  firstName?: string;

  lastName?: string;

  street: string;

  postalCode: string;

  city: string;

  country: string;

  email?: string;

  phone?: string;

  vatId?: string;

  defaultPaymentTermDays: number;

  defaultDiscountPercent: number;

  defaultDiscountDays: number;

  active: boolean;
}
