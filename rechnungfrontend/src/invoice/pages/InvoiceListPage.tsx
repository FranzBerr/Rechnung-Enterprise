import {
  useEffect,
  useState,
} from "react";

import {
  Alert,
  Card,
  CardContent,
  Chip,
  CircularProgress,
  Divider,
  Stack,
  Typography,
} from "@mui/material";

import { invoiceApi } from "../../api";

import type {
  InvoiceDto,
} from "../../types";

export default function InvoiceListPage() {
  const [invoices, setInvoices] =
    useState<InvoiceDto[]>([]);

  const [loading, setLoading] =
    useState(true);

  const [error, setError] =
    useState("");

  useEffect(() => {
    let active = true;

    async function loadInvoices(): Promise<void> {
      try {
        setLoading(true);
        setError("");

        const data =
          await invoiceApi.findAll();

        if (active) {
          setInvoices(data);
        }
      } catch (reason: unknown) {
        if (!active) {
          return;
        }

        if (reason instanceof Error) {
          setError(reason.message);
        } else {
          setError(
            "Die Rechnungen konnten nicht geladen werden."
          );
        }
      } finally {
        if (active) {
          setLoading(false);
        }
      }
    }

    void loadInvoices();

    return () => {
      active = false;
    };
  }, []);

  function formatAmount(
    amount?: number
  ): string {
    return new Intl.NumberFormat(
      "de-DE",
      {
        style: "currency",
        currency: "EUR",
      }
    ).format(amount ?? 0);
  }

  function formatDate(
    value?: string
  ): string {
    if (!value) {
      return "-";
    }

    const date = new Date(value);

    if (Number.isNaN(date.getTime())) {
      return value;
    }

    return new Intl.DateTimeFormat(
      "de-DE"
    ).format(date);
  }

  function getStatusLabel(
    status: InvoiceDto["status"]
  ): string {
    switch (status) {
      case "DRAFT":
        return "Entwurf";

      case "CREATED":
        return "Erstellt";

      case "SENT":
        return "Versendet";

      case "PAID":
        return "Bezahlt";

      case "CANCELLED":
        return "Storniert";

      default:
        return status;
    }
  }

  function getStatusColor(
    status: InvoiceDto["status"]
  ):
    | "default"
    | "primary"
    | "success"
    | "warning"
    | "error" {
    switch (status) {
      case "DRAFT":
        return "default";

      case "CREATED":
        return "primary";

      case "SENT":
        return "warning";

      case "PAID":
        return "success";

      case "CANCELLED":
        return "error";

      default:
        return "default";
    }
  }

  return (
    <Stack spacing={3}>
      <Typography
        variant="h4"
        component="h1"
      >
        Rechnungen
      </Typography>

      {error && (
        <Alert severity="error">
          {error}
        </Alert>
      )}

      {loading && (
        <Stack
          direction="row"
          spacing={2}
          sx={{
            alignItems: "center",
          }}
        >
          <CircularProgress size={24} />

          <Typography color="text.secondary">
            Rechnungen werden geladen...
          </Typography>
        </Stack>
      )}

      {!loading &&
        !error &&
        invoices.length === 0 && (
          <Alert severity="info">
            Es sind keine Rechnungen vorhanden.
          </Alert>
        )}

      {!loading &&
        invoices.map((invoice) => (
          <Card
            key={invoice.id}
            variant="outlined"
          >
            <CardContent>
              <Stack spacing={2}>
                <Stack
                  direction="row"
                  spacing={2}
                  sx={{
                    justifyContent:
                      "space-between",
                    alignItems: "center",
                  }}
                >
                  <Typography
                    variant="h6"
                    component="h2"
                  >
                    {invoice.invoiceNumber ??
                      "Entwurf"}
                  </Typography>

                  <Chip
                    label={getStatusLabel(
                      invoice.status
                    )}
                    color={getStatusColor(
                      invoice.status
                    )}
                    size="small"
                  />
                </Stack>

                <Divider />

                <Stack spacing={1}>
                  <Typography>
                    Kunde:{" "}
                    {invoice.customerName ??
                      "-"}
                  </Typography>

                  <Typography>
                    Kundennummer:{" "}
                    {invoice.customerNumber ??
                      "-"}
                  </Typography>

                  <Typography>
                    Rechnungsdatum:{" "}
                    {formatDate(
                      invoice.invoiceDate
                    )}
                  </Typography>

                  <Typography>
                    Leistungsdatum:{" "}
                    {formatDate(
                      invoice.serviceDate
                    )}
                  </Typography>

                  <Typography>
                    Fällig am:{" "}
                    {formatDate(
                      invoice.dueDate
                    )}
                  </Typography>

                  <Typography>
                    Nettobetrag:{" "}
                    {formatAmount(
                      invoice.subtotalNet
                    )}
                  </Typography>

                  <Typography>
                    Umsatzsteuer:{" "}
                    {formatAmount(
                      invoice.taxAmount
                    )}
                  </Typography>
                </Stack>

                <Divider />

                <Typography
                  variant="h5"
                  color="primary"
                  sx={{
                    fontWeight: 600,
                  }}
                >
                  {formatAmount(
                    invoice.totalGross
                  )}
                </Typography>
              </Stack>
            </CardContent>
          </Card>
        ))}
    </Stack>
  );
}
