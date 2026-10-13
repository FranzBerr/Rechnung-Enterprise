export interface InvoiceItemDto {
  id?: number;

  positionNumber?: number;

  productId: number;

  productNumber?: string;

  description: string;

  quantity: number;

  unit?: string;

  unitPriceNet: number;

  discountPercent: number;

  vatRate: number;

  lineNet?: number;

  taxAmount?: number;

  lineGross?: number;
}
