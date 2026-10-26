import {
  useCallback,
  useEffect,
  useMemo,
  useState,
} from "react";

import {
  Alert,
  Box,
  Button,
  Chip,
  CircularProgress,
  IconButton,
  InputAdornment,
  Paper,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TextField,
  Tooltip,
  Typography,
} from "@mui/material";

import AddIcon from "@mui/icons-material/Add";
import DeleteIcon from "@mui/icons-material/Delete";
import EditIcon from "@mui/icons-material/Edit";
import RefreshIcon from "@mui/icons-material/Refresh";
import SearchIcon from "@mui/icons-material/Search";
import ToggleOffIcon from "@mui/icons-material/ToggleOff";
import ToggleOnIcon from "@mui/icons-material/ToggleOn";

import {useNavigate,} from "react-router-dom";
import type {CompanyDto,} from "../../company/dto/CompanyDto";
import type {ProductDto,} from "../dto/ProductDto";
import {getCompanies,} from "../../../api";
import {activateProduct, deactivateProduct, getProducts, deleteProduct} from "../../../api/productApi";

export default function ProductListPage() {

  const navigate = useNavigate();


  // ---------------------------------------------------------
  // State
  // ---------------------------------------------------------

  // Aktuell geladene ProduktListe.
  const [products, setProducts] = useState<ProductDto[]>([]);
  // Aktuell geladene KundenListe.
  const [companies, setCompanies] = useState<CompanyDto[]>([]);
  // Initialer Ladezustand der Seite.
  const [loading, setLoading] = useState(true);
  // Zeigt an, ob die vorhandene Liste gerade manuell aktualisiert wird
  const [refreshing, setRefreshing] = useState(false);

  /**
   * ID des ProduKts, für den gerade eine Aktion ausgeführt wird.
   * Wird beispielsweise beim Aktivieren oder Deaktivieren verwendet.
   */
  const [actionProductId, setActionProductId] = useState<number | null>(null);
  // Allgemeine Fehlermeldung für die Seite
  const [error, setError] = useState<string | null>(null);
  // Suchtext der Kundenfilterung.
  const [searchText, setSearchText] = useState("");


  // ---------------------------------------------------------
  // Daten laden
  // ---------------------------------------------------------

  const loadProducts =
    useCallback(async (initialLoad = false) => {

      try {

        if (initialLoad) {
          setLoading(true);
        } else {
          setRefreshing(true);
        }

        setError(null);

        const [
          productResult,
          companyResult,
        ] = await Promise.all([
          getProducts(),
          getCompanies(),
        ]);

        setProducts(productResult,);

        setCompanies(companyResult,);

      } catch (err: unknown) {
        console.error(
          "Fehler beim Laden der Produktdaten:",
          err,
        );
        setError(
          "Die Produkte oder Firmen konnten nicht geladen werden.",
        );
      } finally {
        setLoading(false);
        setRefreshing(false);
      }

    }, []);


  // ---------------------------------------------------------
  // Initiales Laden
  // ---------------------------------------------------------

  useEffect(() => {
    void loadProducts(true);
  }, [
    loadProducts,
  ]);


  // ---------------------------------------------------------
  // Firmen nach ID zuordnen
  // ---------------------------------------------------------

  const companiesById =
    useMemo(() => {

      const result =
        new Map<number, CompanyDto>();

      companies.forEach(
        (company) => {

          result.set(
            company.id,
            company,
          );
        },
      );

      return result;

    }, [
      companies,
    ]);


  // ---------------------------------------------------------
  // Firmenname ermitteln
  // ---------------------------------------------------------

  const getCompanyName = (
    companyId: number,
  ): string => {

    const company =
      companiesById.get(
        companyId,
      );

    if (!company) {
      return `Firma-ID ${companyId}`;
    }

    const legalForm =
      company.legalForm?.trim();

    if (
      legalForm
      && !company.name
        .toLowerCase()
        .includes(
          legalForm.toLowerCase(),
        )
    ) {

      return `${company.name} ${legalForm}`;
    }

    return company.name;
  };


  // ---------------------------------------------------------
  // Produkte filtern
  // ---------------------------------------------------------

  const filteredProducts =
    useMemo(() => {

      const search =
        searchText
          .trim()
          .toLowerCase();

      if (!search) {
        return products;
      }

      return products.filter(
        (product) => {

          const company =
            companiesById.get(
              product.companyId,
            );

          const searchableValues = [
            product.productNumber,
            product.name,
            product.description,
            product.unit,
            company?.name,
            company?.legalForm,
            company?.city,
            String(product.companyId),
          ];

          return searchableValues
            .filter(
              (value): value is string =>
                typeof value === "string"
                && value.length > 0,
            )
            .some(
              (value) =>
                value
                  .toLowerCase()
                  .includes(search),
            );
        },
      );

    }, [
      companiesById,
      products,
      searchText,
    ]);


  // ---------------------------------------------------------
  // Geld formatieren
  // ---------------------------------------------------------

  const formatMoney = (
    value: number | null | undefined,
  ): string => {

    const amount =
      value ?? 0;

    return new Intl.NumberFormat(
      "de-DE",
      {
        style: "currency",
        currency: "EUR",
        minimumFractionDigits: 2,
        maximumFractionDigits: 2,
      },
    ).format(amount);
  };


  // ---------------------------------------------------------
  // Prozent formatieren
  // ---------------------------------------------------------

  const formatPercent = (
    value: number | null | undefined,
  ): string => {

    const percentage =
      value ?? 0;

    return `${percentage.toLocaleString(
      "de-DE",
      {
        minimumFractionDigits: 0,
        maximumFractionDigits: 2,
      },
    )} %`;
  };


  // ---------------------------------------------------------
  // Neues Produkt
  // ---------------------------------------------------------

  const handleCreate = () => {
    navigate("/products/new",);
  };


  // ---------------------------------------------------------
  // Produkt bearbeiten
  // ---------------------------------------------------------

  const handleEdit = (product: ProductDto,) => {
    navigate(`/products/${product.id}/edit`,);
  };


  // ---------------------------------------------------------
  // Produkt aktivieren oder deaktivieren
  // ---------------------------------------------------------

  const handleToggleActive =
    async (
      product: ProductDto,
    ) => {

      try {

        setActionProductId(
          product.id,
        );

        setError(null);

        const updatedProduct =
          product.active
            ? await deactivateProduct(product.id,)
            : await activateProduct(product.id,);

        setProducts(
          (currentProducts) =>
            currentProducts.map(
              (currentProduct) =>
                currentProduct.id ===
                updatedProduct.id
                  ? updatedProduct
                  : currentProduct,
            ),
        );

      } catch (err) {

        console.error(
          "Fehler beim Ändern des Produktstatus:",
          err,
        );

        setError(
          "Der Status des Produkts konnte nicht geändert werden.",
        );

      } finally {

        setActionProductId(
          null,
        );
      }
    };


  // ---------------------------------------------------------
  // Produkt löschen
  // ---------------------------------------------------------

  const handleDelete =
    async (
      product: ProductDto,
    ) => {

      const confirmed =
        window.confirm(
          `Soll das Produkt "${product.productNumber} - ${product.name}" wirklich gelöscht werden?`,
        );

      if (!confirmed) {
        return;
      }

      try {

        setActionProductId(
          product.id,
        );

        setError(null);

        await deleteProduct(product.id,);

        setProducts(
          (currentProducts) =>
            currentProducts.filter(
              (currentProduct) =>
                currentProduct.id !==
                product.id,
            ),
        );

      } catch (err) {

        console.error(
          "Fehler beim Löschen des Produkts:",
          err,
        );

        setError(
          "Das Produkt konnte nicht gelöscht werden.",
        );

      } finally {

        setActionProductId(
          null,
        );
      }
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
        <CircularProgress/>
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
            Produkte
          </Typography>

          <Typography
            variant="body2"
            color="text.secondary"
          >
            Verwaltung der Produktstammdaten
          </Typography>

        </Box>


        <Stack
          direction={{
            xs: "column",
            sm: "row",
          }}
          spacing={1}
        >

          <Button
            type="button"
            variant="outlined"
            disabled={refreshing}
            startIcon={
              refreshing
                ? (
                  <CircularProgress
                    size={18}
                  />
                )
                : (
                  <RefreshIcon/>
                )
            }
            onClick={() => {
              void loadProducts();
            }}
          >
            Aktualisieren
          </Button>


          <Button
            type="button"
            variant="contained"
            startIcon={
              <AddIcon/>
            }
            onClick={
              handleCreate
            }
          >
            Neues Produkt
          </Button>

        </Stack>

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
          SUCHE
         ================================================= */}

      <Paper
        variant="outlined"
        sx={{
          p: 2,
          mb: 2,
        }}
      >

        <TextField
          fullWidth
          size="small"
          label="Produkte suchen"
          placeholder={
            "Firma, Produktnummer, Bezeichnung, Beschreibung oder Einheit"
          }
          value={
            searchText
          }
          onChange={(event) => {

            setSearchText(
              event.target.value,
            );
          }}
          slotProps={{
            input: {
              startAdornment: (
                <InputAdornment
                  position="start"
                >
                  <SearchIcon
                    color="action"
                  />
                </InputAdornment>
              ),
            },
          }}
        />

      </Paper>


      {/* =================================================
          ANZAHL
         ================================================= */}

      <Stack
        direction="row"
        spacing={1}
        sx={{
          mb: 1.5,
          justifyContent: "space-between",
          alignItems: "center",
        }}
      >

        <Typography
          variant="body2"
          color="text.secondary"
        >
          {filteredProducts.length}
          {" "}
          {filteredProducts.length === 1
            ? "Produkt"
            : "Produkte"}
        </Typography>


        {searchText.trim() !== "" && (

          <Typography
            variant="body2"
            color="text.secondary"
          >
            Gefiltert aus
            {" "}
            {products.length}
            {" "}
            Datensätzen
          </Typography>

        )}

      </Stack>


      {/* =================================================
          PRODUKTTABELLE
         ================================================= */}

      <TableContainer
        component={Paper}
        variant="outlined"
      >

        <Table>

          <TableHead>

            <TableRow>

              <TableCell>
                Firma
              </TableCell>

              <TableCell>
                Produktnummer
              </TableCell>

              <TableCell>
                Bezeichnung
              </TableCell>

              <TableCell>
                Beschreibung
              </TableCell>

              <TableCell>
                Einheit
              </TableCell>

              <TableCell
                align="right"
              >
                Nettopreis
              </TableCell>

              <TableCell
                align="right"
              >
                MwSt.
              </TableCell>

              <TableCell
                align="center"
              >
                Status
              </TableCell>

              <TableCell
                align="right"
              >
                Aktionen
              </TableCell>

            </TableRow>

          </TableHead>


          <TableBody>

            {filteredProducts.map(
              (product) => {

                const actionRunning =
                  actionProductId ===
                  product.id;

                const company =
                  companiesById.get(
                    product.companyId,
                  );

                return (

                  <TableRow
                    key={
                      product.id
                    }
                    hover
                  >

                    {/* Firma */}

                    <TableCell>

                      <Typography
                        variant="body2"
                        sx={{
                          fontWeight: 600,
                        }}
                      >
                        {getCompanyName(
                          product.companyId,
                        )}
                      </Typography>

                      {company?.city && (

                        <Typography
                          variant="caption"
                          color="text.secondary"
                        >
                          {company.city}
                        </Typography>

                      )}

                    </TableCell>


                    {/* Produktnummer */}

                    <TableCell>

                      <Typography
                        variant="body2"
                        sx={{
                          fontWeight: 600,
                        }}
                      >
                        {product.productNumber}
                      </Typography>

                    </TableCell>


                    {/* Bezeichnung */}

                    <TableCell>

                      <Typography
                        variant="body2"
                        sx={{
                          fontWeight: 600,
                        }}
                      >
                        {product.name}
                      </Typography>

                    </TableCell>


                    {/* Beschreibung */}

                    <TableCell
                      sx={{
                        maxWidth: 350,
                      }}
                    >

                      <Typography
                        variant="body2"
                        color="text.secondary"
                        title={
                          product.description
                        }
                        sx={{
                          overflow: "hidden",
                          textOverflow: "ellipsis",
                          whiteSpace: "nowrap",
                        }}
                      >
                        {product.description || "-"}
                      </Typography>

                    </TableCell>


                    {/* Einheit */}

                    <TableCell>
                      {product.unit}
                    </TableCell>


                    {/* Nettopreis */}

                    <TableCell
                      align="right"
                    >

                      <Typography
                        variant="body2"
                        sx={{
                          fontWeight: 600,
                          whiteSpace: "nowrap",
                        }}
                      >
                        {formatMoney(
                          product.netPrice,
                        )}
                      </Typography>

                    </TableCell>


                    {/* Mehrwertsteuer */}

                    <TableCell
                      align="right"
                      sx={{
                        whiteSpace: "nowrap",
                      }}
                    >
                      {formatPercent(
                        product.vatRate,
                      )}
                    </TableCell>


                    {/* Status */}

                    <TableCell
                      align="center"
                    >

                      <Chip
                        size="small"
                        label={
                          product.active
                            ? "Aktiv"
                            : "Inaktiv"
                        }
                        color={
                          product.active
                            ? "success"
                            : "default"
                        }
                        variant="outlined"
                      />

                    </TableCell>


                    {/* Aktionen */}

                    <TableCell
                      align="right"
                      sx={{
                        whiteSpace: "nowrap",
                      }}
                    >

                      {/* Bearbeiten */}

                      <Tooltip
                        title="Bearbeiten"
                      >
                        <span>

                          <IconButton
                            type="button"
                            size="small"
                            disabled={
                              actionRunning
                            }
                            onClick={() => {

                              handleEdit(
                                product,
                              );
                            }}
                          >
                            <EditIcon
                              fontSize="small"
                            />
                          </IconButton>

                        </span>
                      </Tooltip>


                      {/* Aktivieren / Deaktivieren */}

                      <Tooltip
                        title={
                          product.active
                            ? "Deaktivieren"
                            : "Aktivieren"
                        }
                      >
                        <span>

                          <IconButton
                            type="button"
                            size="small"
                            color={
                              product.active
                                ? "warning"
                                : "success"
                            }
                            disabled={
                              actionRunning
                            }
                            onClick={() => {

                              void handleToggleActive(
                                product,
                              );
                            }}
                          >

                            {actionRunning
                              ? (
                                <CircularProgress
                                  size={18}
                                />
                              )
                              : product.active
                                ? (
                                  <ToggleOffIcon
                                    fontSize="small"
                                  />
                                )
                                : (
                                  <ToggleOnIcon
                                    fontSize="small"
                                  />
                                )}

                          </IconButton>

                        </span>
                      </Tooltip>


                      {/* Löschen */}

                      <Tooltip
                        title="Löschen"
                      >
                        <span>

                          <IconButton
                            type="button"
                            size="small"
                            color="error"
                            disabled={
                              actionRunning
                            }
                            onClick={() => {

                              void handleDelete(
                                product,
                              );
                            }}
                          >
                            <DeleteIcon
                              fontSize="small"
                            />
                          </IconButton>

                        </span>
                      </Tooltip>

                    </TableCell>

                  </TableRow>
                );
              },
            )}


            {/* =================================================
                KEINE PRODUKTE
               ================================================= */}

            {filteredProducts.length === 0 && (

              <TableRow>

                <TableCell
                  colSpan={9}
                  align="center"
                >

                  <Box
                    sx={{
                      py: 5,
                    }}
                  >

                    <Typography
                      color="text.secondary"
                    >
                      {searchText.trim() !== ""
                        ? "Keine Produkte entsprechen der Suche."
                        : "Keine Produkte vorhanden."}
                    </Typography>

                  </Box>

                </TableCell>

              </TableRow>

            )}

          </TableBody>

        </Table>

      </TableContainer>

    </Box>
  );
}
