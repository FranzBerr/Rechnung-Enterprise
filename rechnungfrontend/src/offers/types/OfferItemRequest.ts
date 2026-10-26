export interface OfferItemRequest {
  positionNumber: number;

  productId: number | null;

  productNumber: string | null;

  productName: string | null;

  description: string | null;

  quantity: number;

  unit: string | null;

  unitPriceNet: number | null;

  discountPercent: number;

  vatRate: number;

  optional: boolean;

  notes: string | null;
}
