import type { OfferStatus } from "./OfferStatus";
import type { OfferItemRequest } from "./OfferItemRequest";

export interface OfferRequest {
  offerNumber: string | null;

  offerDate: string;

  validUntil: string | null;

  status: OfferStatus | null;

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

  currencyCode: string | null;

  paymentTermDays: number | null;

  introductionText: string | null;

  closingText: string | null;

  notes: string | null;

  active: boolean;

  items: OfferItemRequest[];
}
