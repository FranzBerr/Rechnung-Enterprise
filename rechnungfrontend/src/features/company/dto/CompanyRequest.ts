/**
 * Request-Daten zum Anlegen oder Ändern einer Firma.
 *
 * Dieser Typ entspricht dem CustomerRequest-Prinzip:
 *
 * CompanyFormPage
 *      ↓
 * CompanyRequest
 *      ↓
 * companyApi
 *      ↓
 * CompanyController
 */
export interface CompanyRequest {

  /**
   * Firmenname.
   */
  name: string;

  /**
   * Straße und Hausnummer.
   */
  street: string | null;

  /**
   * Postleitzahl.
   */
  zipCode: string | null;

  /**
   * Ort.
   */
  city: string | null;

  /**
   * Land bzw. Ländercode.
   *
   * Beispiel:
   * DE
   */
  country: string | null;

  /**
   * Telefonnummer.
   */
  phone: string | null;

  /**
   * E-Mail-Adresse.
   */
  email: string | null;

  /**
   * Umsatzsteuer-Identifikationsnummer.
   */
  vatId: string | null;

  /**
   * Kennzeichnet, ob die Firma aktiv ist.
   */
  active: boolean;
}
