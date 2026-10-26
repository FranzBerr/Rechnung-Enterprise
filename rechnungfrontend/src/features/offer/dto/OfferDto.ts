import type { OfferStatus } from "./OfferStatus";
import type { OfferItemDto } from "./OfferItemDto";

export interface OfferDto {
  id: number;

  offerNumber: string | null;

  offerDate: string;

  validUntil: string | null;

  status: OfferStatus | null;

  companyId: number;

  customerId: number;

  customerNumber: string | null;

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

  items: OfferItemDto[];
}
