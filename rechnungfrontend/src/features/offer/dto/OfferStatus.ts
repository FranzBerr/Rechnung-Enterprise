export type OfferStatus =
  | "DRAFT"
  | "CREATED"
  | "SENT"
  | "ACCEPTED"
  | "REJECTED"
  | "EXPIRED"
  | "CONVERTED_TO_INVOICE";

export const OFFER_STATUS_LABELS: Record<OfferStatus, string> = {
  DRAFT: "Entwurf",
  CREATED: "Erstellt",
  SENT: "Versendet",
  ACCEPTED: "Angenommen",
  REJECTED: "Abgelehnt",
  EXPIRED: "Abgelaufen",
  CONVERTED_TO_INVOICE: "In Rechnung umgewandelt",
};

export const OFFER_STATUS_OPTIONS: OfferStatus[] = [
  "DRAFT",
  "CREATED",
  "SENT",
  "ACCEPTED",
  "REJECTED",
  "EXPIRED",
  "CONVERTED_TO_INVOICE",
];
