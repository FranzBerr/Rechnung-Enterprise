import { useEffect, useState } from "react";
import { Alert, Stack, Typography } from "@mui/material";
import { api } from "../api";
import type { Invoice } from "../types";

export default function InvoiceListPage() {
  const [invoices, setInvoices] = useState<Invoice[]>([]);
  const [error, setError] = useState("");

  useEffect(() => {
    api.get<Invoice[]>("/invoices")
      .then((response) => setInvoices(response.data))
      .catch((reason: unknown) => setError(String(reason)));
  }, []);

  return (
    <Stack spacing={2}>
      <Typography variant="h4">Rechnungen</Typography>
      {error && <Alert severity="error">{error}</Alert>}
      {invoices.map((invoice) => (
        <Typography key={invoice.id}>
          {invoice.invoiceNumber ?? "Entwurf"} | {invoice.customerName} | {invoice.totalGross} EUR
        </Typography>
      ))}
    </Stack>
  );
}
