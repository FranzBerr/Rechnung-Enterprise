import {
  useCallback,
  useEffect,
  useMemo,
  useState,
} from "react";

import { useNavigate } from "react-router-dom";

import {
  Alert,
  Box,
  Button,
  Chip,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  IconButton,
  InputAdornment,
  Link,
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

import type { CustomerDto } from "../dto/CustomerDto";

import {
  activateCustomer,
  deactivateCustomer,
  deleteCustomer,
  getCustomers,
} from "../../../api";
import {
  getErrorMessage,
} from "../../../common/api/axiosErrorHelper";


/**
 * Übersichtsseite für die Kundenverwaltung.
 *
 * Unterstützte Funktionen:
 *
 * - Alle Kunden anzeigen
 * - Kunden suchen und filtern
 * - Neuen Kunden anlegen
 * - Bestehenden Kunden bearbeiten
 * - Kunden aktivieren
 * - Kunden deaktivieren
 * - Kunden löschen
 * - Kundenliste aktualisieren
 */
export default function CustomerListPage() {

  const navigate = useNavigate();


  // ---------------------------------------------------------
  // State
  // ---------------------------------------------------------


    // Aktuell geladene Kundenliste.
  const [customers, setCustomers] = useState<CustomerDto[]>([]);
  /**
   * Initialer Ladezustand der Seite.
   */
  const [loading, setLoading] =  useState(true);
  // Zeigt an, ob die vorhandene Liste gerade  manuell aktualisiert wird.*/
  const [refreshing, setRefreshing] =  useState(false);
  /**
   * ID des Kunden, für den gerade eine Aktion
   * ausgeführt wird.
   *
   * Wird beispielsweise beim Aktivieren oder
   * Deaktivieren verwendet.
   */
  const [actionLoadingId, setActionLoadingId] =  useState<number | null>(null);
   // Gibt an, ob aktuell ein Löschvorgang läuft.
  const [deleting, setDeleting] =  useState(false);
  // Allgemeine Fehlermeldung für die Seite
  const [error, setError] =  useState<string | null>(null);
   //  Suchtext der Kundenfilterung.
  const [searchText, setSearchText] =  useState("");

  /**
   * Kunde, der nach Bestätigung gelöscht werden soll.
   *
   * null bedeutet, dass der Löschdialog geschlossen ist.
   */
  const [customerToDelete, setCustomerToDelete] =  useState<CustomerDto | null>(null);


  // ---------------------------------------------------------
  // Kunden laden
  // ---------------------------------------------------------

  /**
   * Lädt alle Kunden über die Customer-REST-API.
   *
   * Beim initialen Laden wird ein großer Ladeindikator
   * angezeigt.
   *
   * Bei einer späteren Aktualisierung bleibt die
   * vorhandene Tabelle sichtbar.
   */
  const loadCustomers = useCallback(
    async (
      initialLoad = false, ): Promise<void> => {

      try {

        if (initialLoad) {
          setLoading(true);
        } else {
          setRefreshing(true);
        }

        setError(null);

        const result =
          await getCustomers();

        /*
         * Runtime-Absicherung.
         *
         * Normalerweise garantiert bereits der TypeScript-Typ,
         * dass getCustomers() ein CustomerDto[] liefert.
         *
         * Die zusätzliche Prüfung verhindert dennoch Fehler
         * bei einer unerwarteten Backend-Antwort.
         */
        if (!Array.isArray(result)) {

          throw new Error(
            "Die Kundenliste besitzt ein ungültiges Datenformat.",
          );
        }

        setCustomers(result);

      } catch (err: unknown) {

        console.error(
          "Fehler beim Laden der Kunden:",
          err,
        );

        setError(
          getErrorMessage(
            err,
            "Die Kunden konnten nicht geladen werden.",
          ),
        );

      } finally {

        setLoading(false);
        setRefreshing(false);
      }
    },
    [],
  );


  // ---------------------------------------------------------
  // Initiales Laden
  // ---------------------------------------------------------

  useEffect(() => {

    void loadCustomers(true);

  }, [loadCustomers]);


  // ---------------------------------------------------------
  // Suche
  // ---------------------------------------------------------

  /**
   * Normalisierte Suche.
   *
   * Die Suche berücksichtigt:
   *
   * - Kundennummer
   * - Kundenname
   * - Ansprechpartner
   * - Straße
   * - PLZ
   * - Ort
   * - Land
   * - E-Mail
   * - Telefon
   * - Umsatzsteuer-ID
   */
  const filteredCustomers =
    useMemo(() => {

      const search =
        searchText
          .trim()
          .toLocaleLowerCase("de-DE");

      if (!search) {
        return customers;
      }

      return customers.filter(
        (customer) => {

          const values = [
            customer.customerNumber,
            customer.name,
            customer.contactPerson,
            customer.street,
            customer.zipCode,
            customer.city,
            customer.country,
            customer.email,
            customer.phone,
            customer.vatId,
          ];

          return values.some(
            (value) => {

              if (typeof value !== "string") {
                return false;
              }

              return value
                .toLocaleLowerCase("de-DE")
                .includes(search);
            },
          );
        },
      );

    }, [
      customers,
      searchText,
    ]);


  // ---------------------------------------------------------
  // Adresse formatieren
  // ---------------------------------------------------------

  /**
   * Erzeugt aus den einzelnen Adressfeldern
   * eine lesbare Adresszeile.
   */
  const formatAddress = (
    customer: CustomerDto,
  ): string => {

    const street =
      customer.street?.trim();

    const cityLine =
      [
        customer.zipCode?.trim(),
        customer.city?.trim(),
      ]
        .filter(
          (value): value is string =>
            Boolean(value),
        )
        .join(" ");

    return [
      street,
      cityLine,
      customer.country?.trim(),
    ]
      .filter(
        (value): value is string =>
          Boolean(value),
      )
      .join(", ");
  };


  // ---------------------------------------------------------
  // Navigation
  // ---------------------------------------------------------

  /**
   * Öffnet das Formular zum Anlegen eines neuen Kunden.
   */
  const handleCreate = (): void => {
    navigate("/customers/new");
  };


  /**
   * Öffnet das Formular zum Bearbeiten eines Kunden.
   */
  const handleEdit = (customer: CustomerDto,): void => {
    navigate(`/customers/${customer.id}/edit`,);
  };


  // ---------------------------------------------------------
  // Aktivieren / Deaktivieren
  // ---------------------------------------------------------

  /**
   * Wechselt den Aktivstatus des Kunden.
   *
   * Der vom Backend zurückgegebene Kunde ersetzt
   * anschließend direkt den bisherigen Datensatz
   * im lokalen State.
   *
   * Dadurch ist kein vollständiges erneutes Laden
   * der Kundenliste erforderlich.
   */
  const handleToggleActive = async (
    customer: CustomerDto,
  ): Promise<void> => {

    /*
     * Verhindert einen erneuten Klick,
     * solange die Aktion bereits läuft.
     */
    if (actionLoadingId === customer.id) {
      return;
    }

    try {

      setActionLoadingId(customer.id);
      setError(null);

      const updatedCustomer =
        customer.active
          ? await deactivateCustomer(
            customer.id,
          )
          : await activateCustomer(
            customer.id,
          );

      setCustomers(
        (currentCustomers) =>
          currentCustomers.map(
            (currentCustomer) =>
              currentCustomer.id
              === updatedCustomer.id
                ? updatedCustomer
                : currentCustomer,
          ),
      );

    } catch (err: unknown) {

      console.error(
        "Fehler beim Ändern des Kundenstatus:",
        err,
      );

      setError(
        getErrorMessage(
          err,
          "Der Kundenstatus konnte nicht geändert werden.",
        ),
      );

    } finally {

      setActionLoadingId(null);
    }
  };


  // ---------------------------------------------------------
  // Löschen vorbereiten
  // ---------------------------------------------------------

  /**
   * Öffnet den Löschdialog für einen Kunden.
   */
  const handleDeleteRequest = (
    customer: CustomerDto,
  ): void => {

    setCustomerToDelete(customer);
  };


  // ---------------------------------------------------------
  // Löschen abbrechen
  // ---------------------------------------------------------

  /**
   * Schließt den Löschdialog.
   *
   * Während eines laufenden Löschvorgangs
   * kann der Dialog nicht geschlossen werden.
   */
  const handleDeleteCancel =
    (): void => {

      if (deleting) {
        return;
      }

      setCustomerToDelete(null);
    };


  // ---------------------------------------------------------
  // Kunde löschen
  // ---------------------------------------------------------

  /**
   * Löscht den ausgewählten Kunden nach Bestätigung.
   */
  const handleDeleteConfirm =
    async (): Promise<void> => {

      if (!customerToDelete || deleting) {
        return;
      }

      const customerId =
        customerToDelete.id;

      try {

        setDeleting(true);
        setError(null);

        await deleteCustomer(
          customerId,
        );

        /*
         * Erfolgreich gelöschten Kunden unmittelbar
         * aus dem lokalen State entfernen.
         */
        setCustomers(
          (currentCustomers) =>
            currentCustomers.filter(
              (customer) =>
                customer.id !== customerId,
            ),
        );

        setCustomerToDelete(null);

      } catch (err: unknown) {

        console.error(
          "Fehler beim Löschen des Kunden:",
          err,
        );

        setError(
          getErrorMessage(
            err,
            "Der Kunde konnte nicht gelöscht werden.",
          ),
        );

      } finally {

        setDeleting(false);
      }
    };


  // ---------------------------------------------------------
  // Initialer Ladezustand
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

      {/* =====================================================
          HEADER
          ===================================================== */}

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
            Kunden
          </Typography>

          <Typography
            variant="body2"
            color="text.secondary"
          >
            Verwaltung der Kundenstammdaten
          </Typography>

        </Box>


        {/* Aktionen im Header */}

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
            startIcon={
              refreshing
                ? (
                  <CircularProgress
                    size={18}
                  />
                )
                : (
                  <RefreshIcon />
                )
            }
            disabled={refreshing}
            onClick={() => {
              void loadCustomers();
            }}
          >
            Aktualisieren
          </Button>

          <Button
            type="button"
            variant="contained"
            startIcon={
              <AddIcon />
            }
            onClick={handleCreate}
          >
            Neuer Kunde
          </Button>

        </Stack>

      </Stack>


      {/* =====================================================
          FEHLER
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


      {/* =====================================================
          SUCHE
          ===================================================== */}

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
          label="Kunden suchen"
          placeholder={
            "Kundennummer, Name, Ansprechpartner, Ort, E-Mail, Telefon oder USt-ID"
          }
          value={searchText}
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


      {/* =====================================================
          ANZAHL
          ===================================================== */}

      <Stack
        direction={{
          xs: "column",
          sm: "row",
        }}
        spacing={1}
        sx={{
          mb: 1.5,
          justifyContent: "space-between",
          alignItems: {
            xs: "flex-start",
            sm: "center",
          },
        }}
      >

        <Typography
          variant="body2"
          color="text.secondary"
        >
          {filteredCustomers.length}
          {" "}
          {filteredCustomers.length === 1
            ? "Kunde"
            : "Kunden"}
        </Typography>

        {searchText.trim() !== "" && (

          <Typography
            variant="body2"
            color="text.secondary"
          >
            Gefiltert aus{" "}
            {customers.length}{" "}
            Datensätzen
          </Typography>

        )}

      </Stack>


      {/* =====================================================
          KUNDENTABELLE
          ===================================================== */}

      <TableContainer
        component={Paper}
        variant="outlined"
      >

        <Table
          size="small"
          aria-label="Kundenliste"
        >

          <TableHead>

            <TableRow>

              <TableCell>
                Kundennummer
              </TableCell>

              <TableCell>
                Kunde
              </TableCell>

              <TableCell>
                Ansprechpartner
              </TableCell>

              <TableCell>
                Adresse
              </TableCell>

              <TableCell>
                E-Mail
              </TableCell>

              <TableCell>
                Telefon
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

            {filteredCustomers.map(
              (customer) => {

                const actionLoading =
                  actionLoadingId
                  === customer.id;

                return (

                  <TableRow
                    key={customer.id}
                    hover
                  >

                    {/* Kundennummer */}

                    <TableCell>

                      <Typography
                        variant="body2"
                        sx={{
                          fontWeight: 600,
                        }}
                      >
                        {customer.customerNumber}
                      </Typography>

                    </TableCell>


                    {/* Kundenname */}

                    <TableCell>

                      <Typography
                        variant="body2"
                        sx={{
                          fontWeight: 600,
                        }}
                      >
                        {customer.name}
                      </Typography>

                    </TableCell>


                    {/* Ansprechpartner */}

                    <TableCell>

                      <Typography
                        variant="body2"
                        color="text.secondary"
                      >
                        {customer.contactPerson || "-"}
                      </Typography>

                    </TableCell>


                    {/* Adresse */}

                    <TableCell>

                      <Typography
                        variant="body2"
                        color="text.secondary"
                      >
                        {formatAddress(customer) || "-"}
                      </Typography>

                    </TableCell>


                    {/* E-Mail */}

                    <TableCell>

                      {customer.email
                        ? (
                          <Link
                            href={`mailto:${customer.email}`}
                            underline="hover"
                            variant="body2"
                          >
                            {customer.email}
                          </Link>
                        )
                        : (
                          <Typography
                            variant="body2"
                            color="text.secondary"
                          >
                            -
                          </Typography>
                        )}

                    </TableCell>


                    {/* Telefon */}

                    <TableCell>

                      <Typography
                        variant="body2"
                      >
                        {customer.phone || "-"}
                      </Typography>

                    </TableCell>


                    {/* Status */}

                    <TableCell
                      align="center"
                    >

                      <Chip
                        size="small"
                        label={
                          customer.active
                            ? "Aktiv"
                            : "Inaktiv"
                        }
                        color={
                          customer.active
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
                        title="Kunde bearbeiten"
                      >
                        <span>

                          <IconButton
                            type="button"
                            size="small"
                            aria-label={
                              `${customer.name} bearbeiten`
                            }
                            disabled={actionLoading}
                            onClick={() => {
                              handleEdit(customer);
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
                          customer.active
                            ? "Kunde deaktivieren"
                            : "Kunde aktivieren"
                        }
                      >
                        <span>

                          <IconButton
                            type="button"
                            size="small"
                            disabled={actionLoading}
                            aria-label={
                              customer.active
                                ? `${customer.name} deaktivieren`
                                : `${customer.name} aktivieren`
                            }
                            color={
                              customer.active
                                ? "default"
                                : "success"
                            }
                            onClick={() => {
                              void handleToggleActive(
                                customer,
                              );
                            }}
                          >

                            {actionLoading
                              ? (
                                <CircularProgress
                                  size={20}
                                />
                              )
                              : customer.active
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
                        title="Kunde löschen"
                      >
                        <span>

                          <IconButton
                            type="button"
                            size="small"
                            color="error"
                            disabled={actionLoading}
                            aria-label={
                              `${customer.name} löschen`
                            }
                            onClick={() => {
                              handleDeleteRequest(
                                customer,
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


            {/* Keine Datensätze */}

            {filteredCustomers.length === 0 && (

              <TableRow>

                <TableCell
                  colSpan={8}
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
                      {searchText.trim()
                        ? "Keine Kunden entsprechen den Suchkriterien."
                        : "Keine Kunden vorhanden."}
                    </Typography>

                  </Box>

                </TableCell>

              </TableRow>

            )}

          </TableBody>

        </Table>

      </TableContainer>


      {/* =====================================================
          LÖSCHBESTÄTIGUNG
          ===================================================== */}

      <Dialog
        open={customerToDelete !== null}
        onClose={
          deleting
            ? undefined
            : handleDeleteCancel
        }
        maxWidth="sm"
        fullWidth
      >

        <DialogTitle>
          Kunde löschen
        </DialogTitle>

        <DialogContent>

          <DialogContentText>

            Soll der Kunde{" "}

            <strong>
              {customerToDelete?.customerNumber}
              {" - "}
              {customerToDelete?.name}
            </strong>

            {" "}
            wirklich gelöscht werden?

          </DialogContentText>

          <Alert
            severity="warning"
            sx={{
              mt: 2,
            }}
          >
            Der Löschvorgang kann nicht rückgängig
            gemacht werden.
          </Alert>

        </DialogContent>

        <DialogActions>

          <Button
            type="button"
            onClick={handleDeleteCancel}
            disabled={deleting}
          >
            Abbrechen
          </Button>

          <Button
            type="button"
            variant="contained"
            color="error"
            disabled={deleting}
            startIcon={
              deleting
                ? (
                  <CircularProgress
                    size={18}
                    color="inherit"
                  />
                )
                : (
                  <DeleteIcon />
                )
            }
            onClick={() => {
              void handleDeleteConfirm();
            }}
          >
            {deleting
              ? "Wird gelöscht"
              : "Löschen"}
          </Button>

        </DialogActions>

      </Dialog>

    </Box>
  );
}
