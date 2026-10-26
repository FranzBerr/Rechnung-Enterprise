// =========================================================
// CompanyDto
// =========================================================

// Repräsentiert eine Firma, wie sie vom Backend
// an das Frontend geliefert wird.
//
// Backend:
// com.bcs.rechnung.company.dto.CompanyDto
//
// Verwendung unter anderem in:
// companyApi.ts
// CompanyListPage.tsx
// CompanyFormPage.tsx
// CustomerFormPage.tsx
// OfferFormPage.tsx

export interface CompanyDto {
  // Eindeutige technische ID
  id: number;

  // Firmenname
  name: string;

  // Rechtsform, zum Beispiel GmbH
  legalForm: string | null;

  // Steuernummer
  taxNumber: string | null;

  // Umsatzsteuer-Identifikationsnummer
  vatId: string | null;

  // Straße und Hausnummer
  street: string | null;

  // Postleitzahl
  zipCode: string | null;

  // Ort
  city: string | null;

  // Land beziehungsweise Ländercode
  country: string | null;

  // Telefonnummer
  phone: string | null;

  // E-Mail-Adresse
  email: string | null;

  // Webseite
  website: string | null;

  // Aktivstatus
  active: boolean;
}
