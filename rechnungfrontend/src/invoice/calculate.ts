// import type { InvoiceItem } from "../types";
//
// export function calculate(items: InvoiceItem[]) {
//   const values = items.reduce(
//     (sum, item) => {
//       const net = item.quantity * item.unitPriceNet * (1 - item.discountPercent / 100);
//       return { net: sum.net + net, tax: sum.tax + net * item.vatRate / 100 };
//     },
//     { net: 0, tax: 0 },
//   );
//
//   return {
//     net: Number(values.net.toFixed(2)),
//     tax: Number(values.tax.toFixed(2)),
//     gross: Number((values.net + values.tax).toFixed(2)),
//   };
// }
