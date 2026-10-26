import {
  Alert,
  Autocomplete,
  Box,
  Button,
  Card,
  CardContent,
  Checkbox,
  CircularProgress,
  Divider,
  FormControlLabel,
  Grid,
  IconButton,
  MenuItem,
  Stack,
  TextField,
  Typography,
} from "@mui/material";

import AddIcon from "@mui/icons-material/Add";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";
import DeleteIcon from "@mui/icons-material/Delete";
import SaveIcon from "@mui/icons-material/Save";

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
  getActiveCompanies,
  getCompanies,
} from "../../../api/companyApi";

import {
  getActiveCustomers,
  getCustomers,
} from "../../../api/customerApi";

import {
  getActiveProducts,
  getProducts,
} from "../../../api/productApi";

import {
  createOffer,
  getOfferById,
  updateOffer,
} from "../../../api/offerApi";

import {
  getApiErrorMessage,
} from "../../../errors/axiosErrorHelper";

import type {
  CompanyDto,
} from "../../company/dto/CompanyDto";

import type {
  CustomerDto,
} from "../../customer/dto/CustomerDto";

import type {
  ProductDto,
} from "../../product/dto/ProductDto";

import type {
  OfferDto,
} from "../dto/OfferDto";

import type {
  OfferItemRequest,
} from "../dto/OfferItemRequest";

import type {
  OfferRequest,
} from "../dto/OfferRequest";

import type {
  OfferStatus,
} from "../dto/OfferStatus";

import {
  OFFER_STATUS_LABELS,
  OFFER_STATUS_OPTIONS,
} from "../dto/OfferStatus";


// =========================================================
// Datumsfunktionen
// =========================================================

function getToday(): string {
  return new Date()
    .toISOString()
    .slice(0, 10);
}


function addDays(
  date: string,
  days: number,
): string {
  const result =
    new Date(`${date}T00:00:00`);

  result.setDate(
    result.getDate() + days,
  );

  return result
    .toISOString()
    .slice(0, 10);
}


// =========================================================
// Zahlen
// =========================================================

function parseNumber(
  value: string,
  fallback = 0,
): number {
  if (value.trim() === "") {
    return fallback;
  }

  const parsedValue =
    Number(value);

  return Number.isFinite(
    parsedValue,
  )
    ? parsedValue
    : fallback;
}


// =========================================================
// Positionsberechnung
// =========================================================

function calculateLineNet(
  item: OfferItemRequest,
): number {
  const quantity =
    item.quantity ?? 0;

  const unitPriceNet =
    item.unitPriceNet ?? 0;

  const discountPercent =
    item.discountPercent ?? 0;

  const amount =
    quantity * unitPriceNet;

  return (
    amount *
    (
      1 -
      discountPercent / 100
    )
  );
}


function calculateLineTax(
  item: OfferItemRequest,
): number {
  const lineNet =
    calculateLineNet(
      item,
    );

  const vatRate =
    item.vatRate ?? 0;

  return (
    lineNet *
    vatRate /
    100
  );
}


function calculateLineGross(
  item: OfferItemRequest,
): number {
  return (
    calculateLineNet(item) +
    calculateLineTax(item)
  );
}


// =========================================================
// Währungsformatierung
// =========================================================

function formatCurrency(
  value: number,
  currencyCode: string,
): string {
  try {
    return new Intl.NumberFormat(
      "de-DE",
      {
        style: "currency",
        currency:
          currencyCode || "EUR",
      },
    ).format(value);
  } catch {
    return value.toFixed(2);
  }
}


// =========================================================
// Leere Position
// =========================================================

function createEmptyItem(
  positionNumber: number,
): OfferItemRequest {
  return {
    positionNumber,
    productId: null,
    productNumber: null,
    productName: null,
    description: null,
    quantity: 1,
    unit: null,
    unitPriceNet: null,
    discountPercent: 0,
    vatRate: 19,
    optional: false,
    notes: null,
  };
}


// =========================================================
// Initiales Angebot
// =========================================================

function createInitialOffer(): OfferRequest {
  const offerDate =
    getToday();

  return {
    offerNumber: null,

    offerDate,

    validUntil:
      addDays(
        offerDate,
        30,
      ),

    status: "DRAFT",

    companyId: 0,

    customerId: 0,

    customerName: null,

    customerContactPerson: null,

    customerDepartment: null,

    customerStreet: null,

    customerZipCode: null,

    customerCity: null,

    customerCountry: null,

    customerVatId: null,

    currencyCode: "EUR",

    paymentTermDays: 30,

    introductionText:
      "Vielen Dank für Ihre Anfrage.",

    closingText:
      "Wir freuen uns auf Ihren Auftrag.",

    notes: null,

    active: true,

    items: [],
  };
}


// =========================================================
// OfferDto in OfferRequest umwandeln
// =========================================================

function mapOfferDtoToRequest(
  source: OfferDto,
): OfferRequest {
  return {
    offerNumber:
    source.offerNumber,

    offerDate:
    source.offerDate,

    validUntil:
    source.validUntil,

    status:
    source.status,

    companyId:
    source.companyId,

    customerId:
    source.customerId,

    customerName:
    source.customerName,

    customerContactPerson:
    source.customerContactPerson,

    customerDepartment:
    source.customerDepartment,

    customerStreet:
    source.customerStreet,

    customerZipCode:
    source.customerZipCode,

    customerCity:
    source.customerCity,

    customerCountry:
    source.customerCountry,

    customerVatId:
    source.customerVatId,

    currencyCode:
    source.currencyCode,

    paymentTermDays:
    source.paymentTermDays,

    introductionText:
    source.introductionText,

    closingText:
    source.closingText,

    notes:
    source.notes,

    active:
    source.active,

    items:
      source.items.map(
        (item) => ({
          positionNumber:
          item.positionNumber,

          productId:
          item.productId,

          productNumber:
          item.productNumber,

          productName:
          item.productName,

          description:
          item.description,

          quantity:
          item.quantity,

          unit:
          item.unit,

          unitPriceNet:
          item.unitPriceNet,

          discountPercent:
          item.discountPercent,

          vatRate:
          item.vatRate,

          optional:
          item.optional,

          notes:
          item.notes,
        }),
      ),
  };
}


// =========================================================
// OfferFormPage
// =========================================================

export default function OfferFormPage() {
  const navigate =
    useNavigate();

  const {
    id,
  } = useParams();

  const editMode =
    id !== undefined;

  const offerId =
    id !== undefined
      ? Number(id)
      : null;

  const validOfferId =
    offerId !== null &&
    Number.isInteger(offerId) &&
    offerId > 0;


  // =========================================================
  // State
  // =========================================================

  const [offer, setOffer] =
    useState<OfferRequest>(
      createInitialOffer,
    );

  const [companies, setCompanies] =
    useState<CompanyDto[]>([]);

  const [customers, setCustomers] =
    useState<CustomerDto[]>([]);

  const [products, setProducts] =
    useState<ProductDto[]>([]);

  const [archived, setArchived] =
    useState<boolean>(false);

  const [loading, setLoading] =
    useState<boolean>(true);

  const [saving, setSaving] =
    useState<boolean>(false);

  const [error, setError] =
    useState<string | null>(
      null,
    );

  const [success, setSuccess] =
    useState<string | null>(
      null,
    );


  // =========================================================
  // Stammdaten und Angebot laden
  // =========================================================

  useEffect(() => {
    let cancelled = false;

    const loadData =
      async (): Promise<void> => {
        setLoading(true);
        setError(null);
        setSuccess(null);

        if (
          editMode &&
          !validOfferId
        ) {
          setError(
            "Die Angebots-ID ist ungültig.",
          );

          setLoading(false);

          return;
        }

        try {
          const existingOfferPromise:
            Promise<OfferDto | null> =
            editMode &&
            offerId !== null
              ? getOfferById(
                offerId,
              )
              : Promise.resolve(
                null,
              );

          const [
            companyData,
            customerData,
            productData,
            existingOffer,
          ] = await Promise.all([
            editMode
              ? getCompanies()
              : getActiveCompanies(),

            editMode
              ? getCustomers()
              : getActiveCustomers(),

            editMode
              ? getProducts()
              : getActiveProducts(),

            existingOfferPromise,
          ]);

          if (cancelled) {
            return;
          }

          setCompanies(
            companyData,
          );

          setCustomers(
            customerData,
          );

          setProducts(
            productData,
          );

          if (
            existingOffer !== null
          ) {
            setOffer(
              mapOfferDtoToRequest(
                existingOffer,
              ),
            );

            setArchived(
              existingOffer.archived,
            );
          } else {
            setOffer(
              createInitialOffer(),
            );

            setArchived(false);
          }
        } catch (
          loadError: unknown
          ) {
          if (cancelled) {
            return;
          }

          setError(
            getApiErrorMessage(
              loadError,
              editMode
                ? "Das Angebot konnte nicht geladen werden."
                : "Die Stammdaten konnten nicht geladen werden.",
            ),
          );
        } finally {
          if (!cancelled) {
            setLoading(false);
          }
        }
      };

    void loadData();

    return () => {
      cancelled = true;
    };
  }, [
    editMode,
    offerId,
    validOfferId,
  ]);


  // =========================================================
  // Angebotsfeld aktualisieren
  // =========================================================

  // const updateOfferField = <
  //   K extends keyof OfferRequest,
  // >(
  //   field: K,
  //   value: OfferRequest[K],
  // ): void => {
  //   setOffer((current) => ({
  //     ...current,
  //     [field]: value,
  //   }));
  //
  //   setError(null);
  //   setSuccess(null);
  // };
  const updateOfferField = <
    K extends keyof OfferRequest,
  >(
    field: K,
    value: OfferRequest[K],
  ): void => {
    setOffer((current) => ({
      ...current,
      [field]: value,
    }));

    setError(null);
    setSuccess(null);
  };

  // =========================================================
  // Ausgewählte Firma
  // =========================================================

  const selectedCompany =
    companies.find(
      (company) =>
        company.id ===
        offer.companyId,
    ) ?? null;


  // =========================================================
  // Firma auswählen
  // =========================================================

  const handleCompanyChange = (
    company: CompanyDto | null,
  ): void => {
    setOffer((current) => ({
      ...current,

      companyId:
        company?.id ?? 0,

      customerId: 0,

      customerName: null,

      customerContactPerson: null,

      customerDepartment: null,

      customerStreet: null,

      customerZipCode: null,

      customerCity: null,

      customerCountry: null,

      customerVatId: null,

      items: [],
    }));

    setError(null);
    setSuccess(null);
  };


  // =========================================================
  // Kunden
  // =========================================================

  const availableCustomers =
    useMemo(
      () =>
        customers.filter(
          (customer) =>
            customer.companyId ===
            offer.companyId,
        ),
      [
        customers,
        offer.companyId,
      ],
    );


  const selectedCustomer =
    availableCustomers.find(
      (customer) =>
        customer.id ===
        offer.customerId,
    ) ?? null;


  // =========================================================
  // Kunde auswählen
  // =========================================================

  const handleCustomerChange = (
    customer: CustomerDto | null,
  ): void => {
    setError(null);
    setSuccess(null);

    if (customer === null) {
      setOffer((current) => ({
        ...current,

        customerId: 0,

        customerName: null,

        customerContactPerson: null,

        customerDepartment: null,

        customerStreet: null,

        customerZipCode: null,

        customerCity: null,

        customerCountry: null,

        customerVatId: null,
      }));

      return;
    }

    setOffer((current) => ({
      ...current,

      customerId:
      customer.id,

      customerName:
      customer.name,

      customerContactPerson:
      customer.contactPerson,

      customerDepartment:
      customer.department,

      customerStreet:
      customer.street,

      customerZipCode:
      customer.zipCode,

      customerCity:
      customer.city,

      customerCountry:
      customer.country,

      customerVatId:
      customer.vatId,
    }));
  };


  // =========================================================
  // Produkte
  // =========================================================

  const availableProducts =
    useMemo(
      () =>
        products.filter(
          (product) =>
            product.companyId ===
            offer.companyId,
        ),
      [
        products,
        offer.companyId,
      ],
    );


  // =========================================================
  // Position hinzufügen
  // =========================================================

  const handleAddItem =
    (): void => {
      setOffer((current) => ({
        ...current,

        items: [
          ...current.items,

          createEmptyItem(
            current.items.length + 1,
          ),
        ],
      }));

      setError(null);
      setSuccess(null);
    };


  // =========================================================
  // Positionsfeld aktualisieren
  // =========================================================

  // const updateItem = <
  //   K extends keyof OfferItemRequest,
  // >(
  //   index: number,
  //   field: K,
  //   value: OfferItemRequest[K],
  // ): void => {
  //   setOffer((current) => ({
  //     ...current,
  //
  //     items: current.items.map(
  //       (item, itemIndex) =>
  //         itemIndex === index
  //           ? {
  //             ...item,
  //             [field]: value,
  //           }
  //           : item,
  //     ),
  //   }));
  //
  //   setError(null);
  //   setSuccess(null);
  // };
  const updateItem = <
    K extends keyof OfferItemRequest,
  >(
    index: number,
    field: K,
    value: OfferItemRequest[K],
  ): void => {
    setOffer((current) => ({
      ...current,

      items:
        current.items.map(
          (
            item,
            itemIndex,
          ) =>
            itemIndex === index
              ? {
                ...item,
                [field]: value,
              }
              : item,
        ),
    }));

    setError(null);
    setSuccess(null);
  };

  // =========================================================
  // Produkt auswählen
  // =========================================================

  const handleProductChange = (
    index: number,
    product: ProductDto | null,
  ): void => {
    setOffer((current) => ({
      ...current,

      items:
        current.items.map(
          (
            item,
            itemIndex,
          ) => {
            if (
              itemIndex !== index
            ) {
              return item;
            }

            if (
              product === null
            ) {
              return {
                ...item,

                productId: null,

                productNumber: null,

                productName: null,

                description: null,

                unit: null,

                unitPriceNet: null,
              };
            }

            return {
              ...item,

              productId:
              product.id,

              productNumber:
              product.productNumber,

              productName:
              product.name,

              description:
              product.description,

              unit:
              product.unit,

              unitPriceNet:
              product.netPrice,

              vatRate:
              product.vatRate,
            };
          },
        ),
    }));

    setError(null);
    setSuccess(null);
  };


  // =========================================================
  // Position löschen
  // =========================================================

  const handleDeleteItem = (
    index: number,
  ): void => {
    setOffer((current) => {
      const remainingItems =
        current.items
          .filter(
            (
              _item,
              itemIndex,
            ) =>
              itemIndex !== index,
          )
          .map(
            (
              item,
              itemIndex,
            ) => ({
              ...item,

              positionNumber:
                itemIndex + 1,
            }),
          );

      return {
        ...current,
        items: remainingItems,
      };
    });

    setError(null);
    setSuccess(null);
  };


  // =========================================================
  // Summen
  // =========================================================

  const totals =
    useMemo(() => {
      let subtotalNet = 0;
      let taxAmount = 0;

      for (
        const currentItem of
        offer.items
        ) {
        if (
          currentItem.optional
        ) {
          continue;
        }

        subtotalNet +=
          calculateLineNet(
            currentItem,
          );

        taxAmount +=
          calculateLineTax(
            currentItem,
          );
      }

      return {
        subtotalNet,

        taxAmount,

        totalGross:
          subtotalNet +
          taxAmount,
      };
    }, [offer.items]);


  // =========================================================
  // Validierung
  // =========================================================

  const validate = (): string | null => {
    if (!offer.offerDate) {
      return "Bitte geben Sie ein Angebotsdatum ein.";
    }

    if (offer.companyId <= 0) {
      return "Bitte wählen Sie eine Firma aus.";
    }

    if (offer.customerId <= 0) {
      return "Bitte wählen Sie einen Kunden aus.";
    }

    if (
      offer.validUntil !== null &&
      offer.validUntil < offer.offerDate
    ) {
      return (
        "Das Gültigkeitsdatum darf nicht " +
        "vor dem Angebotsdatum liegen."
      );
    }

    if (offer.items.length === 0) {
      return "Bitte erfassen Sie mindestens eine Angebotsposition.";
    }

    for (
      let index = 0;
      index < offer.items.length;
      index += 1
    ) {
      const currentItem =
        offer.items[index];

      if (currentItem.quantity <= 0) {
        return (
          `Position ${index + 1}: ` +
          "Die Menge muss größer als 0 sein."
        );
      }

      if (currentItem.unitPriceNet === null) {
        return (
          `Position ${index + 1}: ` +
          "Bitte geben Sie einen Einzelpreis ein."
        );
      }

      if (currentItem.unitPriceNet < 0) {
        return (
          `Position ${index + 1}: ` +
          "Der Einzelpreis darf nicht negativ sein."
        );
      }

      if (
        currentItem.discountPercent !== null &&
        (
          currentItem.discountPercent < 0 ||
          currentItem.discountPercent > 100
        )
      ) {
        return (
          `Position ${index + 1}: ` +
          "Der Rabatt muss zwischen 0 und 100 Prozent liegen."
        );
      }

      if (currentItem.vatRate < 0) {
        return (
          `Position ${index + 1}: ` +
          "Der Umsatzsteuersatz darf nicht negativ sein."
        );
      }
    }

    return null;
  };


  // =========================================================
  // Speichern
  // =========================================================

  // const handleSave =
  //   async (): Promise<void> => {
  //     console.log(
  //       "Speichern ausgelöst:",
  //       offer,
  //     );
  //
  //     if (archived) {
  //       setError(
  //         "Ein archiviertes Angebot kann nicht geändert werden.",
  //       );
  //
  //       return;
  //     }
  //
  //     if (
  //       editMode &&
  //       !validOfferId
  //     ) {
  //       setError(
  //         "Die Angebots-ID ist ungültig.",
  //       );
  //
  //       return;
  //     }
  //
  //     const validationError =
  //       validate();
  //
  //     if (
  //       validationError !== null
  //     ) {
  //       console.error(
  //         "Frontend-Validierung:",
  //         validationError,
  //       );
  //
  //       setError(
  //         validationError,
  //       );
  //
  //       return;
  //     }
  //
  //     try {
  //       setSaving(true);
  //       setError(null);
  //       setSuccess(null);
  //
  //       if (
  //         editMode &&
  //         offerId !== null
  //       ) {
  //         console.log(
  //           "PUT OfferRequest:",
  //           offer,
  //         );
  //
  //         const updatedOffer =
  //           await updateOffer(
  //             offerId,
  //             offer,
  //           );
  //
  //         setOffer(
  //           mapOfferDtoToRequest(
  //             updatedOffer,
  //           ),
  //         );
  //
  //         setArchived(
  //           updatedOffer.archived,
  //         );
  //
  //         setSuccess(
  //           "Das Angebot wurde erfolgreich gespeichert.",
  //         );
  //
  //         return;
  //       }
  //
  //       console.log(
  //         "POST OfferRequest:",
  //         offer,
  //       );
  //
  //       const createdOffer =
  //         await createOffer(
  //           offer,
  //         );
  //
  //       console.log(
  //         "Erstelltes Angebot:",
  //         createdOffer,
  //       );
  //
  //       navigate(
  //         `/offers/${createdOffer.id}/edit`,
  //         {
  //           replace: true,
  //         },
  //       );
  //     } catch (
  //       saveError: unknown
  //       ) {
  //       console.error(
  //         "Fehler beim Speichern des Angebots:",
  //         saveError,
  //       );
  //
  //       setError(
  //         getApiErrorMessage(
  //           saveError,
  //           editMode
  //             ? "Das Angebot konnte nicht aktualisiert werden."
  //             : "Das Angebot konnte nicht erstellt werden.",
  //         ),
  //       );
  //     } finally {
  //       setSaving(false);
  //     }
  //   };
  const handleSave =
    async (): Promise<void> => {
      if (archived) {
        setError(
          "Ein archiviertes Angebot kann nicht geändert werden.",
        );

        return;
      }

      if (
        editMode &&
        !validOfferId
      ) {
        setError(
          "Die Angebots-ID ist ungültig.",
        );

        return;
      }

      const validationError =
        validate();

      if (
        validationError !== null
      ) {
        setError(
          validationError,
        );

        return;
      }

      try {
        setSaving(true);
        setError(null);
        setSuccess(null);

        if (
          editMode &&
          offerId !== null
        ) {
          const updatedOffer =
            await updateOffer(
              offerId,
              offer,
            );

          setOffer(
            mapOfferDtoToRequest(
              updatedOffer,
            ),
          );

          setArchived(
            updatedOffer.archived,
          );

          setSuccess(
            "Das Angebot wurde erfolgreich gespeichert.",
          );

          return;
        }

        console.log(
          "POST OfferRequest:",
          offer,
        );

        const createdOffer =
          await createOffer(
            offer,
          );

        console.log(
          "Angebot erstellt:",
          createdOffer,
        );

        navigate(
          `/offers/${createdOffer.id}/edit`,
          {
            replace: true,
          },
        );

      } catch (
        saveError: unknown
        ) {
        console.error(
          "Fehler beim Speichern:",
          saveError,
        );

        setError(
          getApiErrorMessage(
            saveError,
            editMode
              ? "Das Angebot konnte nicht aktualisiert werden."
              : "Das Angebot konnte nicht erstellt werden.",
          ),
        );
      } finally {
        setSaving(false);
      }
    };


  // =========================================================
  // Ladeanzeige
  // =========================================================

  if (loading) {
    return (
      <Box
        sx={{
          minHeight: 240,
          p: 3,
          display: "flex",
          flexDirection:
            "column",
          alignItems:
            "center",
          justifyContent:
            "center",
          gap: 2,
        }}
      >
        <CircularProgress />

        <Typography>
          {editMode
            ? "Angebot wird geladen ..."
            : "Stammdaten werden geladen ..."}
        </Typography>
      </Box>
    );
  }


  // =========================================================
  // Oberfläche
  // =========================================================

  return (
    <Box
      sx={{
        p: 3,
      }}
    >
      <Stack
        direction={{
          xs: "column",
          md: "row",
        }}
        spacing={2}
        sx={{
          mb: 3,
          justifyContent:
            "space-between",
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
          >
            {editMode
              ? "Angebot bearbeiten"
              : "Neues Angebot"}
          </Typography>

          <Typography
            color="text.secondary"
          >
            {editMode
              ? "Vorhandenes Angebot bearbeiten"
              : "Angebot mit Kunde und Positionen erstellen"}
          </Typography>
        </Box>

        <Stack
          direction="row"
          spacing={1}
        >
          <Button
            startIcon={
              <ArrowBackIcon />
            }
            onClick={() =>
              navigate(-1)
            }
          >
            Zurück
          </Button>

          <Button
            variant="contained"
            startIcon={
              <SaveIcon />
            }
            disabled={
              saving ||
              archived ||
              (
                editMode &&
                !validOfferId
              )
            }
            onClick={
              handleSave
            }
          >
            {saving
              ? "Speichern ..."
              : editMode
                ? "Änderungen speichern"
                : "Angebot erstellen"}
          </Button>
        </Stack>
      </Stack>

      {error !== null && (
        <Alert
          severity="error"
          sx={{
            mb: 3,
          }}
          onClose={() =>
            setError(null)
          }
        >
          {error}
        </Alert>
      )}

      {success !== null && (
        <Alert
          severity="success"
          sx={{
            mb: 3,
          }}
          onClose={() =>
            setSuccess(null)
          }
        >
          {success}
        </Alert>
      )}

      {archived && (
        <Alert
          severity="info"
          sx={{
            mb: 3,
          }}
        >
          Dieses Angebot ist archiviert und kann nicht mehr geändert werden.
        </Alert>
      )}

      <Card
        sx={{
          mb: 3,
        }}
      >
        <CardContent>
          <Typography
            variant="h6"
          >
            Angebotsdaten
          </Typography>

          <Divider
            sx={{
              my: 2,
            }}
          />

          <Grid
            container
            spacing={2}
          >
            <Grid
              size={{
                xs: 12,
                md: 3,
              }}
            >
              {/*<TextField*/}
              {/*  fullWidth*/}
              {/*  label="Angebotsnummer"*/}
              {/*  value={*/}
              {/*    offer.offerNumber ??*/}
              {/*    ""*/}
              {/*  }*/}
              {/*  disabled={archived}*/}
              {/*  onChange={(event) =>*/}
              {/*    updateOfferField(*/}
              {/*      "offerNumber",*/}
              {/*      event.target.value ||*/}
              {/*      null,*/}
              {/*    )*/}
              {/*  }*/}
              {/*/>*/}
              <TextField
                fullWidth
                label="Angebotsnummer"
                value={
                  editMode
                    ? offer.offerNumber ?? ""
                    : "Wird automatisch vergeben"
                }
                disabled
                helperText={
                  editMode
                    ? "Die Angebotsnummer kann nicht geändert werden."
                    : "Die Angebotsnummer wird beim Speichern automatisch erzeugt."
                }
              />
            </Grid>

            <Grid
              size={{
                xs: 12,
                md: 3,
              }}
            >
              <TextField
                fullWidth
                required
                type="date"
                label="Angebotsdatum"
                value={
                  offer.offerDate
                }
                disabled={archived}
                slotProps={{
                  inputLabel: {
                    shrink: true,
                  },
                }}
                onChange={(event) =>
                  updateOfferField(
                    "offerDate",
                    event.target.value,
                  )
                }
              />
            </Grid>

            <Grid
              size={{
                xs: 12,
                md: 3,
              }}
            >
              <TextField
                fullWidth
                type="date"
                label="Gültig bis"
                value={
                  offer.validUntil ??
                  ""
                }
                disabled={archived}
                slotProps={{
                  inputLabel: {
                    shrink: true,
                  },
                }}
                onChange={(event) =>
                  updateOfferField(
                    "validUntil",
                    event.target.value ||
                    null,
                  )
                }
              />
            </Grid>

            <Grid
              size={{
                xs: 12,
                md: 3,
              }}
            >
              <TextField
                select
                fullWidth
                label="Status"
                value={
                  offer.status ??
                  "DRAFT"
                }
                disabled={archived}
                onChange={(event) =>
                  updateOfferField(
                    "status",
                    event.target
                      .value as OfferStatus,
                  )
                }
              >
                {OFFER_STATUS_OPTIONS.map(
                  (status) => (
                    <MenuItem
                      key={status}
                      value={status}
                    >
                      {
                        OFFER_STATUS_LABELS[
                          status
                          ]
                      }
                    </MenuItem>
                  ),
                )}
              </TextField>
            </Grid>

            <Grid
              size={{
                xs: 12,
                md: 6,
              }}
            >
              <Autocomplete
                options={companies}
                value={selectedCompany}
                disabled={archived}
                getOptionLabel={(
                  company,
                ) =>
                  company.name
                }
                isOptionEqualToValue={(
                  option,
                  value,
                ) =>
                  option.id ===
                  value.id
                }
                onChange={(
                  _event,
                  company,
                ) =>
                  handleCompanyChange(
                    company,
                  )
                }
                renderInput={(
                  params,
                ) => (
                  <TextField
                    {...params}
                    required
                    label="Firma"
                  />
                )}
              />
            </Grid>

            <Grid
              size={{
                xs: 12,
                md: 2,
              }}
            >
              <TextField
                select
                fullWidth
                label="Währung"
                value={
                  offer.currencyCode ??
                  "EUR"
                }
                disabled={archived}
                onChange={(event) =>
                  updateOfferField(
                    "currencyCode",
                    event.target.value,
                  )
                }
              >
                <MenuItem value="EUR">
                  EUR
                </MenuItem>

                <MenuItem value="CHF">
                  CHF
                </MenuItem>

                <MenuItem value="USD">
                  USD
                </MenuItem>
              </TextField>
            </Grid>

            <Grid
              size={{
                xs: 12,
                md: 2,
              }}
            >
              <TextField
                fullWidth
                type="number"
                label="Zahlungsziel"
                value={
                  offer.paymentTermDays ??
                  ""
                }
                disabled={archived}
                slotProps={{
                  htmlInput: {
                    min: 0,
                  },
                }}
                onChange={(event) =>
                  updateOfferField(
                    "paymentTermDays",
                    event.target.value ===
                    ""
                      ? null
                      : Number(
                        event.target.value,
                      ),
                  )
                }
              />
            </Grid>

            <Grid
              size={{
                xs: 12,
                md: 2,
              }}
            >
              <FormControlLabel
                control={
                  <Checkbox
                    checked={
                      offer.active
                    }
                    disabled={archived}
                    onChange={(event) =>
                      updateOfferField(
                        "active",
                        event.target.checked,
                      )
                    }
                  />
                }
                label="Aktiv"
              />
            </Grid>
          </Grid>
        </CardContent>
      </Card>

      <Card
        sx={{
          mb: 3,
        }}
      >
        <CardContent>
          <Typography
            variant="h6"
          >
            Kunde
          </Typography>

          <Divider
            sx={{
              my: 2,
            }}
          />

          <Grid
            container
            spacing={2}
          >
            <Grid
              size={{
                xs: 12,
              }}
            >
              <Autocomplete
                options={
                  availableCustomers
                }
                value={
                  selectedCustomer
                }
                disabled={
                  archived ||
                  offer.companyId <= 0
                }
                getOptionLabel={(
                  customer,
                ) =>
                  `${customer.customerNumber} - ${customer.name}`
                }
                isOptionEqualToValue={(
                  option,
                  value,
                ) =>
                  option.id ===
                  value.id
                }
                noOptionsText="Keine Kunden verfügbar"
                onChange={(
                  _event,
                  customer,
                ) =>
                  handleCustomerChange(
                    customer,
                  )
                }
                renderInput={(
                  params,
                ) => (
                  <TextField
                    {...params}
                    required
                    label="Kunde"
                  />
                )}
              />
            </Grid>

            <Grid
              size={{
                xs: 12,
                md: 6,
              }}
            >
              <TextField
                fullWidth
                label="Kundenname"
                value={
                  offer.customerName ??
                  ""
                }
                disabled={archived}
                onChange={(event) =>
                  updateOfferField(
                    "customerName",
                    event.target.value ||
                    null,
                  )
                }
              />
            </Grid>

            <Grid
              size={{
                xs: 12,
                md: 3,
              }}
            >
              <TextField
                fullWidth
                label="Ansprechpartner"
                value={
                  offer.customerContactPerson ??
                  ""
                }
                disabled={archived}
                onChange={(event) =>
                  updateOfferField(
                    "customerContactPerson",
                    event.target.value ||
                    null,
                  )
                }
              />
            </Grid>

            <Grid
              size={{
                xs: 12,
                md: 3,
              }}
            >
              <TextField
                fullWidth
                label="Abteilung"
                value={
                  offer.customerDepartment ??
                  ""
                }
                disabled={archived}
                onChange={(event) =>
                  updateOfferField(
                    "customerDepartment",
                    event.target.value ||
                    null,
                  )
                }
              />
            </Grid>

            <Grid
              size={{
                xs: 12,
                md: 6,
              }}
            >
              <TextField
                fullWidth
                label="Straße"
                value={
                  offer.customerStreet ??
                  ""
                }
                disabled={archived}
                onChange={(event) =>
                  updateOfferField(
                    "customerStreet",
                    event.target.value ||
                    null,
                  )
                }
              />
            </Grid>

            <Grid
              size={{
                xs: 12,
                md: 2,
              }}
            >
              <TextField
                fullWidth
                label="PLZ"
                value={
                  offer.customerZipCode ??
                  ""
                }
                disabled={archived}
                onChange={(event) =>
                  updateOfferField(
                    "customerZipCode",
                    event.target.value ||
                    null,
                  )
                }
              />
            </Grid>

            <Grid
              size={{
                xs: 12,
                md: 4,
              }}
            >
              <TextField
                fullWidth
                label="Ort"
                value={
                  offer.customerCity ??
                  ""
                }
                disabled={archived}
                onChange={(event) =>
                  updateOfferField(
                    "customerCity",
                    event.target.value ||
                    null,
                  )
                }
              />
            </Grid>

            <Grid
              size={{
                xs: 12,
                md: 6,
              }}
            >
              <TextField
                fullWidth
                label="Land"
                value={
                  offer.customerCountry ??
                  ""
                }
                disabled={archived}
                onChange={(event) =>
                  updateOfferField(
                    "customerCountry",
                    event.target.value ||
                    null,
                  )
                }
              />
            </Grid>

            <Grid
              size={{
                xs: 12,
                md: 6,
              }}
            >
              <TextField
                fullWidth
                label="USt-ID"
                value={
                  offer.customerVatId ??
                  ""
                }
                disabled={archived}
                onChange={(event) =>
                  updateOfferField(
                    "customerVatId",
                    event.target.value ||
                    null,
                  )
                }
              />
            </Grid>
          </Grid>
        </CardContent>
      </Card>

      <Card
        sx={{
          mb: 3,
        }}
      >
        <CardContent>
          <Stack
            direction="row"
            spacing={2}
            sx={{
              justifyContent:
                "space-between",
              alignItems:
                "center",
            }}
          >
            <Typography
              variant="h6"
            >
              Angebotspositionen
            </Typography>

            <Button
              variant="outlined"
              startIcon={
                <AddIcon />
              }
              disabled={
                archived ||
                offer.companyId <= 0
              }
              onClick={
                handleAddItem
              }
            >
              Position hinzufügen
            </Button>
          </Stack>

          <Divider
            sx={{
              my: 2,
            }}
          />

          {offer.items.length === 0 && (
            <Alert
              severity="info"
            >
              Das Angebot enthält noch keine Positionen.
            </Alert>
          )}

          <Stack
            spacing={2}
          >
            {offer.items.map(
              (
                currentItem,
                index,
              ) => {
                const selectedProduct =
                  availableProducts.find(
                    (product) =>
                      product.id ===
                      currentItem.productId,
                  ) ?? null;

                return (
                  <Card
                    key={
                      currentItem.positionNumber
                    }
                    variant="outlined"
                  >
                    <CardContent>
                      <Stack
                        direction="row"
                        sx={{
                          mb: 2,
                          justifyContent:
                            "space-between",
                          alignItems:
                            "center",
                        }}
                      >
                        <Typography
                          variant="subtitle1"
                          sx={{
                            fontWeight:
                              600,
                          }}
                        >
                          Position{" "}
                          {
                            currentItem.positionNumber
                          }
                        </Typography>

                        <IconButton
                          color="error"
                          disabled={archived}
                          aria-label="Position löschen"
                          onClick={() =>
                            handleDeleteItem(
                              index,
                            )
                          }
                        >
                          <DeleteIcon />
                        </IconButton>
                      </Stack>

                      <Grid
                        container
                        spacing={2}
                      >
                        <Grid
                          size={{
                            xs: 12,
                            md: 6,
                          }}
                        >
                          <Autocomplete
                            options={
                              availableProducts
                            }
                            value={
                              selectedProduct
                            }
                            disabled={archived}
                            getOptionLabel={(
                              product,
                            ) =>
                              `${product.productNumber} - ${product.name}`
                            }
                            isOptionEqualToValue={(
                              option,
                              value,
                            ) =>
                              option.id ===
                              value.id
                            }
                            noOptionsText="Keine Produkte verfügbar"
                            onChange={(
                              _event,
                              product,
                            ) =>
                              handleProductChange(
                                index,
                                product,
                              )
                            }
                            renderInput={(
                              params,
                            ) => (
                              <TextField
                                {...params}
                                label="Produkt"
                              />
                            )}
                          />
                        </Grid>

                        <Grid
                          size={{
                            xs: 12,
                            md: 6,
                          }}
                        >
                          <TextField
                            fullWidth
                            label="Produktbezeichnung"
                            value={
                              currentItem.productName ??
                              ""
                            }
                            disabled={archived}
                            onChange={(event) =>
                              updateItem(
                                index,
                                "productName",
                                event.target.value ||
                                null,
                              )
                            }
                          />
                        </Grid>

                        <Grid
                          size={{
                            xs: 12,
                          }}
                        >
                          <TextField
                            fullWidth
                            multiline
                            minRows={2}
                            label="Beschreibung"
                            value={
                              currentItem.description ??
                              ""
                            }
                            disabled={archived}
                            onChange={(event) =>
                              updateItem(
                                index,
                                "description",
                                event.target.value ||
                                null,
                              )
                            }
                          />
                        </Grid>

                        <Grid
                          size={{
                            xs: 12,
                            sm: 3,
                          }}
                        >
                          <TextField
                            fullWidth
                            required
                            type="number"
                            label="Menge"
                            value={
                              currentItem.quantity
                            }
                            disabled={archived}
                            slotProps={{
                              htmlInput: {
                                min: 0.0001,
                                step: 0.0001,
                              },
                            }}
                            onFocus={(event) => {
                              event.target.select();
                            }}
                            onChange={(event) => {
                              const value =
                                Number(
                                  event.target.value,
                                );

                              updateItem(
                                index,
                                "quantity",
                                Number.isFinite(value)
                                  ? value
                                  : 0,
                              );
                            }}
                          />
                        </Grid>

                        <Grid
                          size={{
                            xs: 12,
                            sm: 2,
                          }}
                        >
                          <TextField
                            fullWidth
                            label="Einheit"
                            value={
                              currentItem.unit ??
                              ""
                            }
                            disabled={archived}
                            onChange={(event) =>
                              updateItem(
                                index,
                                "unit",
                                event.target.value ||
                                null,
                              )
                            }
                          />
                        </Grid>

                        <Grid
                          size={{
                            xs: 12,
                            sm: 3,
                          }}
                        >
                          <TextField
                            fullWidth
                            type="number"
                            label="Einzelpreis Netto"
                            value={
                              currentItem.unitPriceNet ??
                              ""
                            }
                            disabled={archived}
                            slotProps={{
                              htmlInput: {
                                min: 0,
                                step: 0.01,
                              },
                            }}
                            onChange={(event) =>
                              updateItem(
                                index,
                                "unitPriceNet",
                                event.target.value ===
                                ""
                                  ? null
                                  : Number(
                                    event.target.value,
                                  ),
                              )
                            }
                          />
                        </Grid>

                        <Grid
                          size={{
                            xs: 12,
                            sm: 2,
                          }}
                        >
                          <TextField
                            fullWidth
                            type="number"
                            label="Rabatt %"
                            value={
                              currentItem.discountPercent ??
                              0
                            }
                            disabled={archived}
                            slotProps={{
                              htmlInput: {
                                min: 0,
                                max: 100,
                                step: 0.01,
                              },
                            }}
                            onChange={(event) =>
                              updateItem(
                                index,
                                "discountPercent",
                                parseNumber(
                                  event.target.value,
                                  0,
                                ),
                              )
                            }
                          />
                        </Grid>

                        <Grid
                          size={{
                            xs: 12,
                            sm: 2,
                          }}
                        >
                          <TextField
                            fullWidth
                            required
                            type="number"
                            label="MwSt. %"
                            value={
                              currentItem.vatRate
                            }
                            disabled={archived}
                            slotProps={{
                              htmlInput: {
                                min: 0,
                                step: 0.01,
                              },
                            }}
                            onChange={(event) =>
                              updateItem(
                                index,
                                "vatRate",
                                parseNumber(
                                  event.target.value,
                                  0,
                                ),
                              )
                            }
                          />
                        </Grid>

                        <Grid
                          size={{
                            xs: 12,
                            md: 8,
                          }}
                        >
                          <TextField
                            fullWidth
                            label="Anmerkung"
                            value={
                              currentItem.notes ??
                              ""
                            }
                            disabled={archived}
                            onChange={(event) =>
                              updateItem(
                                index,
                                "notes",
                                event.target.value ||
                                null,
                              )
                            }
                          />
                        </Grid>

                        <Grid
                          size={{
                            xs: 12,
                            md: 4,
                          }}
                        >
                          <FormControlLabel
                            control={
                              <Checkbox
                                checked={
                                  currentItem.optional
                                }
                                disabled={archived}
                                onChange={(event) =>
                                  updateItem(
                                    index,
                                    "optional",
                                    event.target.checked,
                                  )
                                }
                              />
                            }
                            label="Optionale Position"
                          />
                        </Grid>
                      </Grid>

                      <Divider
                        sx={{
                          my: 2,
                        }}
                      />

                      <Stack
                        direction={{
                          xs: "column",
                          sm: "row",
                        }}
                        spacing={3}
                        sx={{
                          justifyContent:
                            "flex-end",
                        }}
                      >
                        <Typography>
                          Netto:{" "}
                          <Box
                            component="span"
                            sx={{
                              fontWeight:
                                600,
                            }}
                          >
                            {formatCurrency(
                              calculateLineNet(
                                currentItem,
                              ),
                              offer.currencyCode ??
                              "EUR",
                            )}
                          </Box>
                        </Typography>

                        <Typography>
                          Steuer:{" "}
                          <Box
                            component="span"
                            sx={{
                              fontWeight:
                                600,
                            }}
                          >
                            {formatCurrency(
                              calculateLineTax(
                                currentItem,
                              ),
                              offer.currencyCode ??
                              "EUR",
                            )}
                          </Box>
                        </Typography>

                        <Typography>
                          Brutto:{" "}
                          <Box
                            component="span"
                            sx={{
                              fontWeight:
                                600,
                            }}
                          >
                            {formatCurrency(
                              calculateLineGross(
                                currentItem,
                              ),
                              offer.currencyCode ??
                              "EUR",
                            )}
                          </Box>
                        </Typography>
                      </Stack>

                      {currentItem.optional && (
                        <Alert
                          severity="info"
                          sx={{
                            mt: 2,
                          }}
                        >
                          Diese Position wird nicht in die Angebotssumme eingerechnet.
                        </Alert>
                      )}
                    </CardContent>
                  </Card>
                );
              },
            )}
          </Stack>
        </CardContent>
      </Card>

      <Card
        sx={{
          mb: 3,
        }}
      >
        <CardContent>
          <Typography
            variant="h6"
          >
            Angebotssumme
          </Typography>

          <Divider
            sx={{
              my: 2,
            }}
          />

          <Box
            sx={{
              maxWidth: 420,
              ml: "auto",
            }}
          >
            <Stack
              spacing={1.5}
            >
              <Stack
                direction="row"
                sx={{
                  justifyContent:
                    "space-between",
                }}
              >
                <Typography>
                  Nettosumme
                </Typography>

                <Typography>
                  {formatCurrency(
                    totals.subtotalNet,
                    offer.currencyCode ??
                    "EUR",
                  )}
                </Typography>
              </Stack>

              <Stack
                direction="row"
                sx={{
                  justifyContent:
                    "space-between",
                }}
              >
                <Typography>
                  Umsatzsteuer
                </Typography>

                <Typography>
                  {formatCurrency(
                    totals.taxAmount,
                    offer.currencyCode ??
                    "EUR",
                  )}
                </Typography>
              </Stack>

              <Divider />

              <Stack
                direction="row"
                sx={{
                  justifyContent:
                    "space-between",
                }}
              >
                <Typography
                  variant="h6"
                >
                  Gesamtbetrag
                </Typography>

                <Typography
                  variant="h6"
                >
                  {formatCurrency(
                    totals.totalGross,
                    offer.currencyCode ??
                    "EUR",
                  )}
                </Typography>
              </Stack>

              <Typography
                variant="caption"
                color="text.secondary"
              >
                Optionale Positionen sind nicht in der Angebotssumme enthalten.
              </Typography>
            </Stack>
          </Box>
        </CardContent>
      </Card>

      <Card
        sx={{
          mb: 3,
        }}
      >
        <CardContent>
          <Typography
            variant="h6"
          >
            Texte und Anmerkungen
          </Typography>

          <Divider
            sx={{
              my: 2,
            }}
          />

          <Stack
            spacing={2}
          >
            <TextField
              fullWidth
              multiline
              minRows={3}
              label="Einleitungstext"
              value={
                offer.introductionText ??
                ""
              }
              disabled={archived}
              onChange={(event) =>
                updateOfferField(
                  "introductionText",
                  event.target.value ||
                  null,
                )
              }
            />

            <TextField
              fullWidth
              multiline
              minRows={3}
              label="Schlusstext"
              value={
                offer.closingText ??
                ""
              }
              disabled={archived}
              onChange={(event) =>
                updateOfferField(
                  "closingText",
                  event.target.value ||
                  null,
                )
              }
            />

            <TextField
              fullWidth
              multiline
              minRows={2}
              label="Interne Anmerkungen"
              value={
                offer.notes ??
                ""
              }
              disabled={archived}
              onChange={(event) =>
                updateOfferField(
                  "notes",
                  event.target.value ||
                  null,
                )
              }
            />
          </Stack>
        </CardContent>
      </Card>

      <Stack
        direction="row"
        spacing={2}
        sx={{
          justifyContent:
            "flex-end",
        }}
      >
        <Button
          startIcon={
            <ArrowBackIcon />
          }
          onClick={() =>
            navigate(-1)
          }
        >
          Zurück
        </Button>

        <Button
          variant="contained"
          startIcon={
            <SaveIcon />
          }
          disabled={
            saving ||
            archived ||
            (
              editMode &&
              !validOfferId
            )
          }
          onClick={
            handleSave
          }
        >
          {saving
            ? "Speichern ..."
            : editMode
              ? "Änderungen speichern"
              : "Angebot erstellen"}
        </Button>
      </Stack>
    </Box>
  );
}
