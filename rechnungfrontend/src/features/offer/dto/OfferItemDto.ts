export interface OfferItemDto {
  id: number;

  positionNumber: number;

  productId: number | null;

  productNumber: string | null;

  productName: string | null;

  description: string | null;

  quantity: number;

  unit: string | null;

  unitPriceNet: number | null;

  discountPercent: number | null;

  vatRate: number;

  lineNet: number;

  taxAmount: number;

  lineGross: number;

  optional: boolean;

  notes: string | null;
}
