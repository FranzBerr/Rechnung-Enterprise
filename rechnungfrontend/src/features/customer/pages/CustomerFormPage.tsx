import {
  useEffect,
  useMemo,
  useState,
} from "react";

import {
  useNavigate,
  useParams,
} from "react-router-dom";

import {
  Alert,
  Box,
  Button,
  CircularProgress,
  Divider,
  FormControlLabel,
  MenuItem,
  Paper,
  Stack,
  Switch,
  TextField,
  Typography,
} from "@mui/material";

import ArrowBackIcon from "@mui/icons-material/ArrowBack";
import SaveIcon from "@mui/icons-material/Save";

import type { CustomerRequest } from "../dto/CustomerRequest";
import type { CompanyDto } from "../../company/dto/CompanyDto";

import {
  createCustomer,
  getCustomerById,
  updateCustomer,
} from "../../../api";

import {
  getCompanies,
} from "../../../api/companyApi";

import {
  getErrorMessage,
  getValidationErrors,
} from "../../../common/api/axiosErrorHelper";


/**
 * Interner Formularzustand.
 *
 * Textfelder werden innerhalb des Formulars immer
 * als string gehalten.
 *
 * Optionale leere Werte werden erst beim Erstellen
 * des CustomerRequest in null umgewandelt.
 */
interface CustomerFormData {

  companyId: string;

  customerNumber: string;
  name: string;
  contactPerson: string;

  street: string;
  zipCode: string;
  city: string;
  country: string;

  phone: string;
  email: string;
  vatId: string;

  active: boolean;

  // ---------------------------------------------------------
  // XRechnung / elektronische Rechnungsstellung
  // ---------------------------------------------------------

  leitwegId: string;
  electronicAddress: string;
  electronicAddressScheme: string;
  businessProcessId: string;
  buyerReference: string;
  department: string;
}


/**
 * Initialwerte für einen neuen Kunden.
 */
const INITIAL_FORM_DATA: CustomerFormData = {

  companyId: "",

  customerNumber: "",
  name: "",
  contactPerson: "",

  street: "",
  zipCode: "",
  city: "",
  country: "DE",

  phone: "",
  email: "",
  vatId: "",

  active: true,

  leitwegId: "",
  electronicAddress: "",
  electronicAddressScheme: "",
  businessProcessId: "",
  buyerReference: "",
  department: "",
};


/**
 * Wandelt einen String in einen optionalen Wert um.
 *
 * Leere Strings werden als null an das Backend gesendet.
 */
const emptyToNull = (
  value: string,
): string | null => {

  const trimmed =
    value.trim();

  return trimmed === ""
    ? null
    : trimmed;
};


/**
 * Gemeinsame Seite für:
 *
 * /customers/new
 *
 * und
 *
 * /customers/:id/edit
 */
export default function CustomerFormPage() {

  const navigate =
    useNavigate();

  const { id } =
    useParams<{ id: string }>();


  // =========================================================
  // Modus bestimmen
  // =========================================================

  /**
   * Wenn eine ID vorhanden ist, wird ein bestehender
   * Kunde bearbeitet.
   */
  const editMode =
    id !== undefined;


  /**
   * Kunden-ID aus der Route lesen.
   *
   * Ungültige IDs werden als null behandelt.
   */
  const customerId =
    useMemo(() => {

      if (!id) {
        return null;
      }

      const parsedId =
        Number(id);

      if (
        !Number.isInteger(parsedId)
        || parsedId <= 0
      ) {
        return null;
      }

      return parsedId;

    }, [id]);


  // =========================================================
  // State
  // =========================================================

  const [formData, setFormData] =
    useState<CustomerFormData>({
      ...INITIAL_FORM_DATA,
    });

  const [companies, setCompanies] =
    useState<CompanyDto[]>([]);

  const [loading, setLoading] =
    useState(true);

  const [saving, setSaving] =
    useState(false);

  const [error, setError] =
    useState<string | null>(null);

  const [
    validationErrors,
    setValidationErrors,
  ] =
    useState<Record<string, string>>({});


  // =========================================================
  // Daten laden
  // =========================================================

  useEffect(() => {

    let mounted = true;


    const loadData =
      async (): Promise<void> => {

        try {

          setLoading(true);
          setError(null);
          setValidationErrors({});


          // -------------------------------------------------
          // Firmen laden
          // -------------------------------------------------

          const loadedCompanies =
            await getCompanies();


          if (!mounted) {
            return;
          }


          if (Array.isArray(loadedCompanies)) {

            setCompanies(
              loadedCompanies,
            );

          } else {

            setCompanies([]);
          }


          // -------------------------------------------------
          // Neuanlage
          // -------------------------------------------------

          if (!editMode) {

            setFormData({
              ...INITIAL_FORM_DATA,
            });

            return;
          }


          // -------------------------------------------------
          // ID prüfen
          // -------------------------------------------------

          if (customerId === null) {

            setError(
              "Die Kunden-ID ist ungültig.",
            );

            return;
          }


          // -------------------------------------------------
          // Bestehenden Kunden laden
          // -------------------------------------------------

          const customer =
            await getCustomerById(
              customerId,
            );


          if (!mounted) {
            return;
          }


          // -------------------------------------------------
          // CustomerDto -> Formular
          // -------------------------------------------------

          setFormData({

            companyId:
              String(customer.companyId),

            customerNumber:
            customer.customerNumber,

            name:
            customer.name,

            contactPerson:
              customer.contactPerson ?? "",

            street:
              customer.street ?? "",

            zipCode:
              customer.zipCode ?? "",

            city:
              customer.city ?? "",

            country:
              customer.country ?? "",

            phone:
              customer.phone ?? "",

            email:
              customer.email ?? "",

            vatId:
              customer.vatId ?? "",

            active:
            customer.active,

            leitwegId:
              customer.leitwegId ?? "",

            electronicAddress:
              customer.electronicAddress ?? "",

            electronicAddressScheme:
              customer.electronicAddressScheme ?? "",

            businessProcessId:
              customer.businessProcessId ?? "",

            buyerReference:
              customer.buyerReference ?? "",

            department:
              customer.department ?? "",
          });


        } catch (err: unknown) {

          if (!mounted) {
            return;
          }

          console.error(
            "Fehler beim Laden des Kundenformulars:",
            err,
          );

          setError(
            getErrorMessage(
              err,
              "Die Kundendaten konnten nicht geladen werden.",
            ),
          );

        } finally {

          if (mounted) {
            setLoading(false);
          }
        }
      };


    void loadData();


    return () => {

      mounted = false;
    };

  }, [
    customerId,
    editMode,
  ]);


  // =========================================================
  // Textfeld ändern
  // =========================================================

  /**
   * Aktualisiert ein Textfeld des Formularzustands.
   *
   * Gleichzeitig wird ein eventuell vorhandener
   * Validierungsfehler des Feldes entfernt.
   */
  const handleTextChange = (
    field: keyof CustomerFormData,
    value: string,
  ): void => {

    setFormData(
      (current) => ({
        ...current,
        [field]: value,
      }),
    );


    setValidationErrors(
      (current) => {

        if (!(field in current)) {
          return current;
        }

        const updated = {
          ...current,
        };

        delete updated[field];

        return updated;
      },
    );
  };


  // =========================================================
  // Boolean-Feld ändern
  // =========================================================

  const handleActiveChange = (
    checked: boolean,
  ): void => {

    setFormData(
      (current) => ({
        ...current,
        active: checked,
      }),
    );
  };


  // =========================================================
  // Frontend-Validierung
  // =========================================================

  const validateForm =
    (): Record<string, string> => {

      const errors:
        Record<string, string> = {};


      // -----------------------------------------------------
      // Firma
      // -----------------------------------------------------

      const companyId =
        Number(formData.companyId);

      if (
        !formData.companyId
        || !Number.isInteger(companyId)
        || companyId <= 0
      ) {

        errors.companyId =
          "Bitte wählen Sie eine Firma aus.";
      }


      // -------------------------------------------------  // Kundennummer
      // -----------------------------------------------------

      const customerNumber =
        formData.customerNumber.trim();

      if (!customerNumber) {

        errors.customerNumber =
          "Die Kundennummer ist erforderlich.";

      } else if (
        customerNumber.length > 30
      ) {

        errors.customerNumber =
          "Die Kundennummer darf maximal 30 Zeichen enthalten.";
      }


      // -----------------------------------------------------
      // Kundenname
      // -----------------------------------------------------

      const name =
        formData.name.trim();

      if (!name) {

        errors.name =
          "Der Kundenname ist erforderlich.";

      } else if (
        name.length > 200
      ) {

        errors.name =
          "Der Kundenname darf maximal 200 Zeichen enthalten.";
      }


      // -----------------------------------------------------
      // E-Mail
      // -----------------------------------------------------

      const email =
        formData.email.trim();

      if (
        email !== ""
        && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(
          email,
        )
      ) {

        errors.email =
          "Bitte geben Sie eine gültige E-Mail-Adresse ein.";
      }


      return errors;
    };


  // =========================================================
  // CustomerRequest erzeugen
  // =========================================================

  /**
   * Wandelt den internen Formularzustand in einen
   * CustomerRequest für das Backend um.
   */
  const createRequest =
    (): CustomerRequest => {

      return {

        companyId:
          Number(formData.companyId),

        customerNumber:
          formData.customerNumber.trim(),

        name:
          formData.name.trim(),

        contactPerson:
          emptyToNull(
            formData.contactPerson,
          ),

        street:
          emptyToNull(
            formData.street,
          ),

        zipCode:
          emptyToNull(
            formData.zipCode,
          ),

        city:
          emptyToNull(
            formData.city,
          ),

        country:
          emptyToNull(
            formData.country,
          ),

        phone:
          emptyToNull(
            formData.phone,
          ),

        email:
          emptyToNull(
            formData.email,
          ),

        vatId:
          emptyToNull(
            formData.vatId,
          ),

        active:
        formData.active,

        // ---------------------------------------------------
        // XRechnung
        // ---------------------------------------------------

        leitwegId:
          emptyToNull(
            formData.leitwegId,
          ),

        electronicAddress:
          emptyToNull(
            formData.electronicAddress,
          ),

        electronicAddressScheme:
          emptyToNull(
            formData.electronicAddressScheme,
          ),

        businessProcessId:
          emptyToNull(
            formData.businessProcessId,
          ),

        buyerReference:
          emptyToNull(
            formData.buyerReference,
          ),

        department:
          emptyToNull(
            formData.department,
          ),
      };
    };


  // =========================================================
  // Speichern
  // =========================================================

  const handleSave =
    async (): Promise<void> => {

      if (saving) {
        return;
      }


      // -----------------------------------------------------
      // Frontend-Validierung
      // -----------------------------------------------------

      const frontendErrors =
        validateForm();


      if (
        Object.keys(frontendErrors).length > 0
      ) {

        setValidationErrors(
          frontendErrors,
        );

        setError(
          "Bitte korrigieren Sie die markierten Felder.",
        );

        return;
      }


      try {

        setSaving(true);
        setError(null);
        setValidationErrors({});


        const request =
          createRequest();


        // ---------------------------------------------------
        // Bearbeiten
        // ---------------------------------------------------

        if (editMode) {

          if (customerId === null) {

            setError(
              "Die Kunden-ID ist ungültig.",
            );

            return;
          }


          await updateCustomer(
            customerId,
            request,
          );


          // ---------------------------------------------------
          // Neuanlage
          // ---------------------------------------------------

        } else {

          await createCustomer(
            request,
          );
        }


        // ---------------------------------------------------
        // Zurück zur Übersicht
        // ---------------------------------------------------

        navigate(
          "/customers",
        );


      } catch (err: unknown) {

        console.error(
          "Fehler beim Speichern des Kunden:",
          err,
        );


        setError(
          getErrorMessage(
            err,
            "Der Kunde konnte nicht gespeichert werden.",
          ),
        );


        setValidationErrors(
          getValidationErrors(
            err,
          ),
        );


      } finally {

        setSaving(false);
      }
    };


  // =========================================================
  // Abbrechen
  // =========================================================

  const handleCancel =
    (): void => {

      navigate(
        "/customers",
      );
    };


  // =========================================================
  // Ladezustand
  // =========================================================

  if (loading) {

    return (
      <Box
        sx={{
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
          minHeight: 300,
        }}
      >
        <CircularProgress />
      </Box>
    );
  }


  // =========================================================
  // Rendern
  // =========================================================

  return (
    <Box
      sx={{
        maxWidth: 1200,
        mx: "auto",
      }}
    >

      {/* =====================================================
          HEADER
          ===================================================== */}

      <Stack
        direction={{
          xs: "column",
          sm: "row",
        }}
        spacing={2}
        sx={{
          mb: 3,
          justifyContent: "space-between",
          alignItems: {
            xs: "stretch",
            sm: "center",
          },
        }}
      >

        <Box>

          <Typography
            variant="h4"
            component="h1"
            sx={{
              fontWeight: 700,
            }}
          >
            {editMode
              ? "Kunde bearbeiten"
              : "Neuer Kunde"}
          </Typography>

          <Typography
            variant="body2"
            color="text.secondary"
          >
            {editMode
              ? "Kundenstammdaten und XRechnungsdaten bearbeiten"
              : "Neuen Kunden mit Stamm- und XRechnungsdaten anlegen"}
          </Typography>

        </Box>


        <Button
          type="button"
          variant="outlined"
          startIcon={
            <ArrowBackIcon />
          }
          disabled={saving}
          onClick={handleCancel}
        >
          Zurück
        </Button>

      </Stack>


      {/* =====================================================
          FEHLERMELDUNG
          ===================================================== */}

      {error !== null && (

        <Alert
          severity="error"
          onClose={() => {
            setError(null);
          }}
          sx={{
            mb: 2,
          }}
        >
          {error}
        </Alert>

      )}


      <Stack spacing={3}>

        {/* ===================================================
            GRUNDDATEN
            =================================================== */}

        <Paper
          variant="outlined"
          sx={{
            p: 3,
          }}
        >

          <Typography
            variant="h6"
            component="h2"
            sx={{
              mb: 2,
            }}
          >
            Grunddaten
          </Typography>


          <Box
            sx={{
              display: "grid",
              gap: 2,
              gridTemplateColumns: {
                xs: "1fr",
                md: "repeat(2, 1fr)",
              },
            }}
          >

            {/* Firma */}

            <TextField
              select
              required
              fullWidth
              label="Firma"
              value={
                formData.companyId
              }
              disabled={saving}
              error={
                Boolean(
                  validationErrors.companyId,
                )
              }
              helperText={
                validationErrors.companyId
              }
              onChange={(event) => {

                handleTextChange(
                  "companyId",
                  event.target.value,
                );
              }}
            >

              <MenuItem value="">
                Bitte auswählen
              </MenuItem>


              {companies.map(
                (company) => (

                  <MenuItem
                    key={company.id}
                    value={
                      String(company.id)
                    }
                  >
                    {company.name}
                  </MenuItem>

                ),
              )}

            </TextField>


            {/* Kundennummer */}

            <TextField
              required
              fullWidth
              label="Kundennummer"
              value={
                formData.customerNumber
              }
              disabled={saving}
              slotProps={{
                htmlInput: {
                  maxLength: 30,
                },
              }}
              error={
                Boolean(
                  validationErrors.customerNumber,
                )
              }
              helperText={
                validationErrors.customerNumber
              }
              onChange={(event) => {

                handleTextChange(
                  "customerNumber",
                  event.target.value,
                );
              }}
            />


            {/* Kundenname */}

            <TextField
              required
              fullWidth
              label="Kundenname"
              value={
                formData.name
              }
              disabled={saving}
              slotProps={{
                htmlInput: {
                  maxLength: 200,
                },
              }}
              error={
                Boolean(
                  validationErrors.name,
                )
              }
              helperText={
                validationErrors.name
              }
              onChange={(event) => {

                handleTextChange(
                  "name",
                  event.target.value,
                );
              }}
            />


            {/* Ansprechpartner */}

            <TextField
              fullWidth
              label="Ansprechpartner"
              value={
                formData.contactPerson
              }
              disabled={saving}
              slotProps={{
                htmlInput: {
                  maxLength: 200,
                },
              }}
              error={
                Boolean(
                  validationErrors.contactPerson,
                )
              }
              helperText={
                validationErrors.contactPerson
              }
              onChange={(event) => {

                handleTextChange(
                  "contactPerson",
                  event.target.value,
                );
              }}
            />


            {/* Abteilung */}

            <TextField
              fullWidth
              label="Abteilung"
              value={
                formData.department
              }
              disabled={saving}
              slotProps={{
                htmlInput: {
                  maxLength: 200,
                },
              }}
              error={
                Boolean(
                  validationErrors.department,
                )
              }
              helperText={
                validationErrors.department
              }
              onChange={(event) => {

                handleTextChange(
                  "department",
                  event.target.value,
                );
              }}
            />


            {/* Aktiv */}

            <Box
              sx={{
                display: "flex",
                alignItems: "center",
                minHeight: 56,
              }}
            >

              <FormControlLabel
                control={
                  <Switch
                    checked={
                      formData.active
                    }
                    disabled={saving}
                    onChange={(
                      _event,
                      checked,
                    ) => {

                      handleActiveChange(
                        checked,
                      );
                    }}
                  />
                }
                label={
                  formData.active
                    ? "Kunde aktiv"
                    : "Kunde inaktiv"
                }
              />

            </Box>

          </Box>

        </Paper>


        {/* ===================================================
            ANSCHRIFT
            =================================================== */}

        <Paper
          variant="outlined"
          sx={{
            p: 3,
          }}
        >

          <Typography
            variant="h6"
            component="h2"
            sx={{
              mb: 2,
            }}
          >
            Anschrift
          </Typography>


          <Stack spacing={2}>

            {/* Straße */}

            <TextField
              fullWidth
              label="Straße"
              value={
                formData.street
              }
              disabled={saving}
              slotProps={{
                htmlInput: {
                  maxLength: 200,
                },
              }}
              error={
                Boolean(
                  validationErrors.street,
                )
              }
              helperText={
                validationErrors.street
              }
              onChange={(event) => {

                handleTextChange(
                  "street",
                  event.target.value,
                );
              }}
            />


            <Box
              sx={{
                display: "grid",
                gap: 2,
                gridTemplateColumns: {
                  xs: "1fr",
                  sm: "160px 1fr",
                },
              }}
            >

              {/* PLZ */}

              <TextField
                fullWidth
                label="PLZ"
                value={
                  formData.zipCode
                }
                disabled={saving}
                slotProps={{
                  htmlInput: {
                    maxLength: 20,
                  },
                }}
                error={
                  Boolean(
                    validationErrors.zipCode,
                  )
                }
                helperText={
                  validationErrors.zipCode
                }
                onChange={(event) => {

                  handleTextChange(
                    "zipCode",
                    event.target.value,
                  );
                }}
              />


              {/* Ort */}

              <TextField
                fullWidth
                label="Ort"
                value={
                  formData.city
                }
                disabled={saving}
                slotProps={{
                  htmlInput: {
                    maxLength: 100,
                  },
                }}
                error={
                  Boolean(
                    validationErrors.city,
                  )
                }
                helperText={
                  validationErrors.city
                }
                onChange={(event) => {

                  handleTextChange(
                    "city",
                    event.target.value,
                  );
                }}
              />

            </Box>


            {/* Land */}

            <TextField
              fullWidth
              label="Land"
              value={
                formData.country
              }
              disabled={saving}
              slotProps={{
                htmlInput: {
                  maxLength: 100,
                },
              }}
              error={
                Boolean(
                  validationErrors.country,
                )
              }
              helperText={
                validationErrors.country
              }
              onChange={(event) => {

                handleTextChange(
                  "country",
                  event.target.value,
                );
              }}
            />

          </Stack>

        </Paper>


        {/* ===================================================
            KOMMUNIKATION UND STEUERDATEN
            =================================================== */}

        <Paper
          variant="outlined"
          sx={{
            p: 3,
          }}
        >

          <Typography
            variant="h6"
            component="h2"
            sx={{
              mb: 2,
            }}
          >
            Kommunikation und Steuerdaten
          </Typography>


          <Box
            sx={{
              display: "grid",
              gap: 2,
              gridTemplateColumns: {
                xs: "1fr",
                md: "repeat(2, 1fr)",
              },
            }}
          >

            {/* Telefon */}

            <TextField
              fullWidth
              label="Telefon"
              value={
                formData.phone
              }
              disabled={saving}
              slotProps={{
                htmlInput: {
                  maxLength: 100,
                },
              }}
              error={
                Boolean(
                  validationErrors.phone,
                )
              }
              helperText={
                validationErrors.phone
              }
              onChange={(event) => {

                handleTextChange(
                  "phone",
                  event.target.value,
                );
              }}
            />


            {/* E-Mail */}

            <TextField
              fullWidth
              type="email"
              label="E-Mail"
              value={
                formData.email
              }
              disabled={saving}
              slotProps={{
                htmlInput: {
                  maxLength: 200,
                },
              }}
              error={
                Boolean(
                  validationErrors.email,
                )
              }
              helperText={
                validationErrors.email
              }
              onChange={(event) => {

                handleTextChange(
                  "email",
                  event.target.value,
                );
              }}
            />


            {/* USt-IdNr. */}

            <TextField
              fullWidth
              label="USt-IdNr."
              value={
                formData.vatId
              }
              disabled={saving}
              slotProps={{
                htmlInput: {
                  maxLength: 50,
                },
              }}
              error={
                Boolean(
                  validationErrors.vatId,
                )
              }
              helperText={
                validationErrors.vatId
              }
              onChange={(event) => {

                handleTextChange(
                  "vatId",
                  event.target.value,
                );
              }}
            />

          </Box>

        </Paper>


        {/* ===================================================
            XRECHNUNG
            =================================================== */}

        <Paper
          variant="outlined"
          sx={{
            p: 3,
          }}
        >

          <Typography
            variant="h6"
            component="h2"
          >
            Elektronische Rechnung / XRechnung
          </Typography>

          <Typography
            variant="body2"
            color="text.secondary"
            sx={{
              mt: 0.5,
              mb: 2,
            }}
          >
            Optionale Empfänger- und Prozessdaten für
            die elektronische Rechnungsstellung.
          </Typography>


          <Divider
            sx={{
              mb: 2,
            }}
          />


          <Box
            sx={{
              display: "grid",
              gap: 2,
              gridTemplateColumns: {
                xs: "1fr",
                md: "repeat(2, 1fr)",
              },
            }}
          >

            {/* Leitweg-ID */}

            <TextField
              fullWidth
              label="Leitweg-ID"
              placeholder="z. B. 09184-12345-67"
              value={
                formData.leitwegId
              }
              disabled={saving}
              slotProps={{
                htmlInput: {
                  maxLength: 100,
                },
              }}
              error={
                Boolean(
                  validationErrors.leitwegId,
                )
              }
              helperText={
                validationErrors.leitwegId
                ?? "Optional, insbesondere für öffentliche Auftraggeber"
              }
              onChange={(event) => {

                handleTextChange(
                  "leitwegId",
                  event.target.value,
                );
              }}
            />


            {/* Käuferreferenz */}

            <TextField
              fullWidth
              label="Käuferreferenz"
              value={
                formData.buyerReference
              }
              disabled={saving}
              slotProps={{
                htmlInput: {
                  maxLength: 200,
                },
              }}
              error={
                Boolean(
                  validationErrors.buyerReference,
                )
              }
              helperText={
                validationErrors.buyerReference
              }
              onChange={(event) => {

                handleTextChange(
                  "buyerReference",
                  event.target.value,
                );
              }}
            />


            {/* Elektronische Adresse */}

            <TextField
              fullWidth
              label="Elektronische Adresse"
              value={
                formData.electronicAddress
              }
              disabled={saving}
              slotProps={{
                htmlInput: {
                  maxLength: 200,
                },
              }}
              error={
                Boolean(
                  validationErrors.electronicAddress,
                )
              }
              helperText={
                validationErrors.electronicAddress
              }
              onChange={(event) => {

                handleTextChange(
                  "electronicAddress",
                  event.target.value,
                );
              }}
            />


            {/* Adressschema */}

            <TextField
              fullWidth
              label="Adressschema"
              placeholder="z. B. 0204"
              value={
                formData.electronicAddressScheme
              }
              disabled={saving}
              slotProps={{
                htmlInput: {
                  maxLength: 20,
                },
              }}
              error={
                Boolean(
                  validationErrors.electronicAddressScheme,
                )
              }
              helperText={
                validationErrors.electronicAddressScheme
                ?? "Schema der elektronischen Adresse"
              }
              onChange={(event) => {

                handleTextChange(
                  "electronicAddressScheme",
                  event.target.value,
                );
              }}
            />


            {/* Geschäftsprozess-ID */}

            <TextField
              fullWidth
              label="Geschäftsprozess-ID"
              placeholder={
                "urn:fdc:peppol.eu:2017:poacc:billing:01:1.0"
              }
              value={
                formData.businessProcessId
              }
              disabled={saving}
              slotProps={{
                htmlInput: {
                  maxLength: 250,
                },
              }}
              error={
                Boolean(
                  validationErrors.businessProcessId,
                )
              }
              helperText={
                validationErrors.businessProcessId
              }
              onChange={(event) => {

                handleTextChange(
                  "businessProcessId",
                  event.target.value,
                );
              }}
              sx={{
                gridColumn: {
                  xs: "auto",
                  md: "1 / -1",
                },
              }}
            />

          </Box>

        </Paper>


        {/* ===================================================
            SPEICHERN
            =================================================== */}

        <Paper
          variant="outlined"
          sx={{
            p: 2,
          }}
        >

          <Stack
            direction={{
              xs: "column-reverse",
              sm: "row",
            }}
            spacing={1}
            sx={{
              justifyContent: "flex-end",
            }}
          >

            <Button
              type="button"
              variant="outlined"
              disabled={saving}
              onClick={handleCancel}
            >
              Abbrechen
            </Button>


            <Button
              type="button"
              variant="contained"
              disabled={
                saving
                || (
                  editMode
                  && customerId === null
                )
              }
              startIcon={
                saving
                  ? (
                    <CircularProgress
                      size={18}
                      color="inherit"
                    />
                  )
                  : (
                    <SaveIcon />
                  )
              }
              onClick={() => {
                void handleSave();
              }}
            >
              {saving
                ? "Wird gespeichert"
                : editMode
                  ? "Änderungen speichern"
                  : "Kunde anlegen"}
            </Button>

          </Stack>

        </Paper>

      </Stack>

    </Box>
  );
}
