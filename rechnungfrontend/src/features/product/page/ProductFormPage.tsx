import {
  type SubmitEventHandler,
  useEffect,
  useState,
} from "react";

import {
  Alert,
  Box,
  Button,
  CircularProgress,
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

import {
  useNavigate,
  useParams,
} from "react-router-dom";

import type {
  CompanyDto,
} from "../../company/dto/CompanyDto";

import type {
  ProductDto,
} from "../dto/ProductDto";

import type {
  ProductRequest,
} from "../dto/ProductRequest";

import {getCompanies,} from "../../../api";
import {createProduct, getProductById, updateProduct} from "../../../api/productApi";


const EMPTY_PRODUCT: ProductRequest = {
  companyId: 0,
  productNumber: "",
  name: "",
  description: "",
  unit: "",
  netPrice: 0,
  vatRate: 19,
  active: true,
};


interface ValidationErrors {
  companyId?: string;
  productNumber?: string;
  name?: string;
  description?: string;
  unit?: string;
  netPrice?: string;
  vatRate?: string;
}


export default function ProductFormPage() {

  const navigate =
    useNavigate();

  const { id } =
    useParams<{
      id?: string;
    }>();


  // ---------------------------------------------------------
  // Produkt-ID und Bearbeitungsmodus
  // ---------------------------------------------------------

  const parsedProductId =
    id !== undefined
      ? Number(id)
      : null;

  const editMode =
    parsedProductId !== null
    && Number.isInteger(parsedProductId)
    && parsedProductId > 0;

  const invalidProductId =
    id !== undefined
    && !editMode;


  // ---------------------------------------------------------
  // State
  // ---------------------------------------------------------

  const [formData, setFormData] =
    useState<ProductRequest>({
      ...EMPTY_PRODUCT,
    });

  const [companies, setCompanies] =
    useState<CompanyDto[]>([]);

  const [loading, setLoading] =
    useState(true);

  const [saving, setSaving] =
    useState(false);

  const [error, setError] =
    useState<string | null>(null);

  const [validationErrors, setValidationErrors] =
    useState<ValidationErrors>({});


  // ---------------------------------------------------------
  // Firmen und gegebenenfalls Produkt laden
  // ---------------------------------------------------------

  useEffect(() => {

    let active = true;

    const loadData = async () => {

      if (invalidProductId) {

        setError(
          "Die angegebene Produkt-ID ist ungültig.",
        );

        setLoading(false);

        return;
      }

      try {

        setLoading(true);
        setError(null);

        const companyResult =
          await getCompanies();

        if (!active) {
          return;
        }

        setCompanies(
          companyResult,
        );


        // ---------------------------------------------------
        // Bearbeitungsmodus
        // ---------------------------------------------------

        if (
          editMode
          && parsedProductId !== null
        ) {

          const product: ProductDto =
            await getProductById( parsedProductId,);

          if (!active) {
            return;
          }

          setFormData({
            companyId:
            product.companyId,

            productNumber:
            product.productNumber,

            name:
            product.name,

            description:
              product.description ?? "",

            unit:
            product.unit,

            netPrice:
            product.netPrice,

            vatRate:
            product.vatRate,

            active:
            product.active,
          });

          return;
        }


        // ---------------------------------------------------
        // Neuanlage
        //
        // Wenn genau eine aktive Firma vorhanden ist,
        // wird sie automatisch ausgewählt.
        // ---------------------------------------------------

        const activeCompanies =
          companyResult.filter(
            (company) =>
              company.active,
          );

        if (
          activeCompanies.length === 1
        ) {

          setFormData(
            (current) => ({
              ...current,
              companyId:
              activeCompanies[0].id,
            }),
          );
        }

      } catch (err) {

        console.error(
          "Fehler beim Laden der Produktmaske:",
          err,
        );

        if (active) {

          setError(
            editMode
              ? "Das Produkt oder die Firmen konnten nicht geladen werden."
              : "Die Firmen konnten nicht geladen werden.",
          );
        }

      } finally {

        if (active) {
          setLoading(false);
        }
      }
    };

    void loadData();

    return () => {
      active = false;
    };

  }, [
    editMode,
    invalidProductId,
    parsedProductId,
  ]);


  // ---------------------------------------------------------
  // Firma ändern
  // ---------------------------------------------------------

  const handleCompanyChange = (
    value: string,
  ) => {

    const companyId =
      Number(value);

    setFormData(
      (current) => ({
        ...current,
        companyId:
          Number.isInteger(companyId)
            ? companyId
            : 0,
      }),
    );

    setValidationErrors(
      (current) => ({
        ...current,
        companyId: undefined,
      }),
    );
  };


  // ---------------------------------------------------------
  // Produktnummer ändern
  // ---------------------------------------------------------

  const handleProductNumberChange = (
    value: string,
  ) => {

    setFormData(
      (current) => ({
        ...current,
        productNumber: value,
      }),
    );

    setValidationErrors(
      (current) => ({
        ...current,
        productNumber: undefined,
      }),
    );
  };


  // ---------------------------------------------------------
  // Bezeichnung ändern
  // ---------------------------------------------------------

  const handleNameChange = (
    value: string,
  ) => {

    setFormData(
      (current) => ({
        ...current,
        name: value,
      }),
    );

    setValidationErrors(
      (current) => ({
        ...current,
        name: undefined,
      }),
    );
  };


  // ---------------------------------------------------------
  // Beschreibung ändern
  // ---------------------------------------------------------

  const handleDescriptionChange = (
    value: string,
  ) => {

    setFormData(
      (current) => ({
        ...current,
        description: value,
      }),
    );

    setValidationErrors(
      (current) => ({
        ...current,
        description: undefined,
      }),
    );
  };


  // ---------------------------------------------------------
  // Einheit ändern
  // ---------------------------------------------------------

  const handleUnitChange = (
    value: string,
  ) => {

    setFormData(
      (current) => ({
        ...current,
        unit: value,
      }),
    );

    setValidationErrors(
      (current) => ({
        ...current,
        unit: undefined,
      }),
    );
  };


  // ---------------------------------------------------------
  // Nettopreis ändern
  // ---------------------------------------------------------

  const handleNetPriceChange = (
    value: string,
  ) => {

    const netPrice =
      value === ""
        ? 0
        : Number(value);

    setFormData(
      (current) => ({
        ...current,
        netPrice:
          Number.isFinite(netPrice)
            ? netPrice
            : 0,
      }),
    );

    setValidationErrors(
      (current) => ({
        ...current,
        netPrice: undefined,
      }),
    );
  };


  // ---------------------------------------------------------
  // Mehrwertsteuer ändern
  // ---------------------------------------------------------

  const handleVatRateChange = (
    value: string,
  ) => {

    const vatRate =
      value === ""
        ? 0
        : Number(value);

    setFormData(
      (current) => ({
        ...current,
        vatRate:
          Number.isFinite(vatRate)
            ? vatRate
            : 0,
      }),
    );

    setValidationErrors(
      (current) => ({
        ...current,
        vatRate: undefined,
      }),
    );
  };


  // ---------------------------------------------------------
  // Aktiv-Status ändern
  // ---------------------------------------------------------

  const handleActiveChange = (
    checked: boolean,
  ) => {

    setFormData(
      (current) => ({
        ...current,
        active: checked,
      }),
    );
  };


  // ---------------------------------------------------------
  // Formular validieren
  // ---------------------------------------------------------

  const validate = (): boolean => {

    const errors:
      ValidationErrors = {};


    // Firma

    if (
      !Number.isInteger(
        formData.companyId,
      )
      || formData.companyId <= 0
    ) {

      errors.companyId =
        "Bitte eine Firma auswählen.";
    }


    // Produktnummer

    const productNumber =
      formData.productNumber.trim();

    if (!productNumber) {

      errors.productNumber =
        "Die Produktnummer ist erforderlich.";

    } else if (
      productNumber.length > 30
    ) {

      errors.productNumber =
        "Die Produktnummer darf maximal 30 Zeichen enthalten.";
    }


    // Bezeichnung

    const name =
      formData.name.trim();

    if (!name) {

      errors.name =
        "Die Bezeichnung ist erforderlich.";

    } else if (
      name.length > 200
    ) {

      errors.name =
        "Die Bezeichnung darf maximal 200 Zeichen enthalten.";
    }


    // Beschreibung

    if (
      formData.description.length > 1000
    ) {

      errors.description =
        "Die Beschreibung darf maximal 1000 Zeichen enthalten.";
    }


    // Einheit

    const unit =
      formData.unit.trim();

    if (!unit) {

      errors.unit =
        "Die Einheit ist erforderlich.";

    } else if (
      unit.length > 30
    ) {

      errors.unit =
        "Die Einheit darf maximal 30 Zeichen enthalten.";
    }


    // Nettopreis

    if (
      !Number.isFinite(
        formData.netPrice,
      )
      || formData.netPrice < 0
    ) {

      errors.netPrice =
        "Der Nettopreis muss mindestens 0,00 EUR betragen.";
    }


    // Mehrwertsteuer

    if (
      !Number.isFinite(
        formData.vatRate,
      )
      || formData.vatRate < 0
    ) {

      errors.vatRate =
        "Der Mehrwertsteuersatz muss mindestens 0 % betragen.";

    } else if (
      formData.vatRate > 100
    ) {

      errors.vatRate =
        "Der Mehrwertsteuersatz darf höchstens 100 % betragen.";
    }


    setValidationErrors(
      errors,
    );

    return (
      Object.keys(errors).length === 0
    );
  };


  // ---------------------------------------------------------
  // Speichern
  // ---------------------------------------------------------

  const handleSubmit:
    SubmitEventHandler<HTMLFormElement> =
    async (event) => {

      event.preventDefault();

      if (!validate()) {
        return;
      }

      try {

        setSaving(true);
        setError(null);

        const request:
          ProductRequest = {

          companyId:
          formData.companyId,

          productNumber:
            formData.productNumber.trim(),

          name:
            formData.name.trim(),

          description:
            formData.description.trim(),

          unit:
            formData.unit.trim(),

          netPrice:
          formData.netPrice,

          vatRate:
          formData.vatRate,

          active:
          formData.active,
        };


        if (
          editMode
          && parsedProductId !== null
        ) {

          await updateProduct( parsedProductId,request,);

        } else {
          await createProduct(request,);
        }

        navigate(
          "/products",
        );

      } catch (err) {

        console.error(
          "Fehler beim Speichern des Produkts:",
          err,
        );

        setError(
          editMode
            ? "Die Änderungen konnten nicht gespeichert werden."
            : "Das Produkt konnte nicht angelegt werden.",
        );

      } finally {

        setSaving(false);
      }
    };


  // ---------------------------------------------------------
  // Abbrechen
  // ---------------------------------------------------------

  const handleCancel = () => {

    navigate(
      "/products",
    );
  };


  // ---------------------------------------------------------
  // Loading
  // ---------------------------------------------------------

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


  // ---------------------------------------------------------
  // Rendern
  // ---------------------------------------------------------

  return (
    <Box>

      {/* =================================================
          HEADER
         ================================================= */}

      <Stack
        direction={{
          xs: "column",
          md: "row",
        }}
        spacing={2}
        sx={{
          mb: 3,
          justifyContent: "space-between",
          alignItems: {
            xs: "stretch",
            md: "center",
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
              ? "Produkt bearbeiten"
              : "Neues Produkt"}
          </Typography>

          <Typography
            variant="body2"
            color="text.secondary"
          >
            {editMode
              ? "Produktstammdaten ändern"
              : "Neues Produkt anlegen"}
          </Typography>

        </Box>


        <Button
          type="button"
          variant="outlined"
          startIcon={
            <ArrowBackIcon />
          }
          disabled={saving}
          onClick={
            handleCancel
          }
        >
          Zurück
        </Button>

      </Stack>


      {/* =================================================
          FEHLER
         ================================================= */}

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


      {/* =================================================
          FORMULAR
         ================================================= */}

      {!invalidProductId && (

        <Paper
          variant="outlined"
          sx={{
            p: {
              xs: 2,
              md: 3,
            },
            maxWidth: 900,
          }}
        >

          <Box
            component="form"
            noValidate
            onSubmit={
              handleSubmit
            }
          >

            <Stack
              spacing={3}
            >

              {/* Firma */}

              <TextField
                select
                fullWidth
                required
                label="Firma"
                value={
                  formData.companyId === 0
                    ? ""
                    : String(
                      formData.companyId,
                    )
                }
                error={
                  Boolean(
                    validationErrors.companyId,
                  )
                }
                helperText={
                  validationErrors.companyId
                  ?? "Firma auswählen, der das Produkt zugeordnet wird"
                }
                disabled={
                  saving
                  || companies.length === 0
                }
                onChange={(event) => {

                  handleCompanyChange(
                    event.target.value,
                  );
                }}
              >

                <MenuItem
                  value=""
                  disabled
                >
                  Firma auswählen
                </MenuItem>


                {companies.map(
                  (company) => {

                    const legalForm =
                      company.legalForm?.trim();

                    const nameContainsLegalForm =
                      legalForm
                      && company.name
                        .toLowerCase()
                        .includes(
                          legalForm.toLowerCase(),
                        );

                    const displayName =
                      legalForm
                      && !nameContainsLegalForm
                        ? `${company.name} ${legalForm}`
                        : company.name;

                    return (

                      <MenuItem
                        key={
                          company.id
                        }
                        value={
                          String(
                            company.id,
                          )
                        }
                        disabled={
                          !company.active
                          && company.id
                          !== formData.companyId
                        }
                      >
                        {displayName}

                        {company.city
                          ? `, ${company.city}`
                          : ""}

                        {!company.active
                          ? " (inaktiv)"
                          : ""}
                      </MenuItem>

                    );
                  },
                )}

              </TextField>


              {companies.length === 0 && (

                <Alert
                  severity="warning"
                >
                  Es ist keine Firma vorhanden. Bitte legen
                  Sie zuerst eine Firma an.
                </Alert>

              )}


              {/* Produktnummer */}

              <TextField
                fullWidth
                required
                label="Produktnummer"
                value={
                  formData.productNumber
                }
                error={
                  Boolean(
                    validationErrors.productNumber,
                  )
                }
                helperText={
                  validationErrors.productNumber
                  ?? "Eindeutige Produktnummer"
                }
                disabled={saving}
                slotProps={{
                  htmlInput: {
                    maxLength: 30,
                  },
                }}
                onChange={(event) => {

                  handleProductNumberChange(
                    event.target.value,
                  );
                }}
              />


              {/* Bezeichnung */}

              <TextField
                fullWidth
                required
                label="Bezeichnung"
                value={
                  formData.name
                }
                error={
                  Boolean(
                    validationErrors.name,
                  )
                }
                helperText={
                  validationErrors.name
                  ?? "Bezeichnung des Produkts"
                }
                disabled={saving}
                slotProps={{
                  htmlInput: {
                    maxLength: 200,
                  },
                }}
                onChange={(event) => {

                  handleNameChange(
                    event.target.value,
                  );
                }}
              />


              {/* Beschreibung */}

              <TextField
                fullWidth
                multiline
                minRows={4}
                label="Beschreibung"
                value={
                  formData.description
                }
                error={
                  Boolean(
                    validationErrors.description,
                  )
                }
                helperText={
                  validationErrors.description
                  ?? `${formData.description.length}/1000 Zeichen`
                }
                disabled={saving}
                slotProps={{
                  htmlInput: {
                    maxLength: 1000,
                  },
                }}
                onChange={(event) => {

                  handleDescriptionChange(
                    event.target.value,
                  );
                }}
              />


              {/* Einheit */}

              <TextField
                fullWidth
                required
                label="Einheit"
                placeholder="z. B. Stk., Std., kg oder m"
                value={
                  formData.unit
                }
                error={
                  Boolean(
                    validationErrors.unit,
                  )
                }
                helperText={
                  validationErrors.unit
                  ?? "Abrechnungseinheit des Produkts"
                }
                disabled={saving}
                slotProps={{
                  htmlInput: {
                    maxLength: 30,
                  },
                }}
                onChange={(event) => {

                  handleUnitChange(
                    event.target.value,
                  );
                }}
              />


              {/* Preis und Mehrwertsteuer */}

              <Stack
                direction={{
                  xs: "column",
                  sm: "row",
                }}
                spacing={2}
              >

                <TextField
                  fullWidth
                  required
                  type="number"
                  label="Nettopreis"
                  value={
                    formData.netPrice
                  }
                  error={
                    Boolean(
                      validationErrors.netPrice,
                    )
                  }
                  helperText={
                    validationErrors.netPrice
                    ?? "Preis ohne Mehrwertsteuer"
                  }
                  disabled={saving}
                  slotProps={{
                    htmlInput: {
                      min: 0,
                      step: 0.0001,
                    },
                  }}
                  onChange={(event) => {

                    handleNetPriceChange(
                      event.target.value,
                    );
                  }}
                />


                <TextField
                  select
                  fullWidth
                  required
                  label="MwSt."
                  value={
                    String(
                      formData.vatRate,
                    )
                  }
                  error={
                    Boolean(
                      validationErrors.vatRate,
                    )
                  }
                  helperText={
                    validationErrors.vatRate
                    ?? "Mehrwertsteuersatz"
                  }
                  disabled={saving}
                  onChange={(event) => {

                    handleVatRateChange(
                      event.target.value,
                    );
                  }}
                >

                  <MenuItem value="0">
                    0 %
                  </MenuItem>

                  <MenuItem value="7">
                    7 %
                  </MenuItem>

                  <MenuItem value="19">
                    19 %
                  </MenuItem>

                </TextField>

              </Stack>


              {/* Status */}

              <FormControlLabel
                control={
                  <Switch
                    checked={
                      formData.active
                    }
                    disabled={saving}
                    onChange={(event) => {

                      handleActiveChange(
                        event.target.checked,
                      );
                    }}
                  />
                }
                label={
                  formData.active
                    ? "Produkt ist aktiv"
                    : "Produkt ist inaktiv"
                }
              />


              {/* Buttons */}

              <Stack
                direction={{
                  xs: "column-reverse",
                  sm: "row",
                }}
                spacing={2}
                sx={{
                  justifyContent: "flex-end",
                  pt: 2,
                }}
              >

                <Button
                  type="button"
                  variant="outlined"
                  disabled={saving}
                  onClick={
                    handleCancel
                  }
                >
                  Abbrechen
                </Button>


                <Button
                  type="submit"
                  variant="contained"
                  disabled={
                    saving
                    || companies.length === 0
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
                >
                  {saving
                    ? "Speichern..."
                    : editMode
                      ? "Änderungen speichern"
                      : "Produkt anlegen"}
                </Button>

              </Stack>

            </Stack>

          </Box>

        </Paper>

      )}

    </Box>
  );
}
