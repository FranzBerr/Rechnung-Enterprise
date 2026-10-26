export type OfferStatus =
  | "DRAFT"
  | "CREATED"
  | "SENT"
  | "ACCEPTED"
  | "REJECTED"
  | "EXPIRED"
  | "CONVERTED_TO_INVOICE";

export interface OfferItem {
  id: number;
  positionNumber: number;

  productId: number | null;
  productNumber: string | null;
  productName: string;
  description: string | null;

  quantity: number;
  unit: string;

  unitPriceNet: number;
  discountPercent: number;
  vatRate: number;

  lineNet: number;
  taxAmount: number;
  lineGross: number;

  optional: boolean;

  notes: string | null;
}

export interface Offer {
  id: number;

  offerNumber: string;
  offerDate: string;
  validUntil: string | null;

  status: OfferStatus;

  companyId: number;
  customerId: number;

  customerNumber: string;
  customerName: string;
  customerContactPerson: string | null;
  customerDepartment: string | null;
  customerStreet: string | null;
  customerZipCode: string | null;
  customerCity: string | null;
  customerCountry: string | null;
  customerVatId: string | null;

  currencyCode: string;
  paymentTermDays: number;

  subtotalNet: number;
  taxAmount: number;
  totalGross: number;

  introductionText: string | null;
  closingText: string | null;
  notes: string | null;

  active: boolean;

  archived: boolean;
  archivedAt: string | null;
  archivePath: string | null;

  items: OfferItem[];
}
