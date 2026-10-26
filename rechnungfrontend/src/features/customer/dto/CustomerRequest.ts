export interface CustomerRequest {
  companyId: number;

  customerNumber: string;
  name: string;

  contactPerson: string | null;

  street: string | null;
  zipCode: string | null;
  city: string | null;
  country: string | null;

  phone: string | null;
  email: string | null;
  vatId: string | null;

  active: boolean;

  // XRechnung
  leitwegId: string | null;
  electronicAddress: string | null;
  electronicAddressScheme: string | null;
  businessProcessId: string | null;
  buyerReference: string | null;
  department: string | null;
}
