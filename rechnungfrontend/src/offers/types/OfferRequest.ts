import type { OfferItemRequest } from "./OfferItemRequest";

export type OfferStatus =
  | "DRAFT"
  | "CREATED"
  | "SENT"
  | "ACCEPTED"
  | "REJECTED"
  | "EXPIRED"
  | "CONVERTED_TO_INVOICE";

export interface OfferRequest {
  offerNumber: string;

  offerDate: string;

  validUntil: string | null;

  status: OfferStatus;

  companyId: number;

  customerId: number;

  customerName: string | null;

  customerContactPerson: string | null;

  customerDepartment: string | null;

  customerStreet: string | null;

  customerZipCode: string | null;

  customerCity: string | null;

  customerCountry: string | null;

  customerVatId: string | null;

  currencyCode: string;

  paymentTermDays: number;

  introductionText: string | null;

  closingText: string | null;

  notes: string | null;

  active: boolean;

  items: OfferItemRequest[];
}
