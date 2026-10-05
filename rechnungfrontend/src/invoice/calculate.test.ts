import { describe, expect, it } from "vitest";
import { calculate } from "./calculate";

describe("calculate", () => {
  it("berechnet Rechnungssummen", () => {
    expect(calculate([{
      productId: 1,
      description: "Beratung",
      quantity: 2,
      unitPriceNet: 100,
      discountPercent: 10,
      vatRate: 19,
    }])).toEqual({ net: 180, tax: 34.2, gross: 214.2 });
  });
});
