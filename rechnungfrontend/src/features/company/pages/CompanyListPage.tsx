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

import type { CompanyDto } from "../dto/CompanyDto";

import {
  activateCompany,
  deactivateCompany,
  deleteCompany,
  getCompanies,
} from "../../../api";

import {
  getErrorMessage,
} from "../../../common/api/axiosErrorHelper";


/**
 * Übersichtsseite für die Firmenverwaltung.
 *
 * Funktionen:
 *
 * - Alle Firmen laden
 * - Firmen suchen
 * - Neue Firma anlegen
 * - Firma bearbeiten
 * - Firma aktivieren
 * - Firma deaktivieren
 * - Firma löschen
 * - Firmenliste aktualisieren
 */
export default function CompanyListPage() {

  const navigate =
    useNavigate();


  // =========================================================
  // State
  // =========================================================

  const [companies, setCompanies] =
    useState<CompanyDto[]>([]);

  const [loading, setLoading] =
    useState(true);

  const [refreshing, setRefreshing] =
    useState(false);

  const [actionLoadingId, setActionLoadingId] =
    useState<number | null>(null);

  const [deleting, setDeleting] =
    useState(false);

  const [error, setError] =
    useState<string | null>(null);

  const [searchText, setSearchText] =
    useState("");

  const [companyToDelete, setCompanyToDelete] =
    useState<CompanyDto | null>(null);


  // =========================================================
  // Firmen laden
  // =========================================================

  /**
   * Lädt alle Firmen vom Backend.
   *
   * Beim initialen Laden wird ein zentraler Ladeindikator
   * angezeigt.
   *
   * Bei einer späteren Aktualisierung bleibt die Tabelle
   * sichtbar.
   */
  const loadCompanies = useCallback(
    async (
      initialLoad = false,
    ): Promise<void> => {

      try {

        if (initialLoad) {

          setLoading(true);

        } else {

          setRefreshing(true);
        }

        setError(null);


        const result =
          await getCompanies();


        /*
         * Zusätzliche Runtime-Absicherung.
         */
        if (!Array.isArray(result)) {

          throw new Error(
            "Die Firmenliste besitzt ein ungültiges Datenformat.",
          );
        }


        setCompanies(result);


      } catch (err: unknown) {

        console.error(
          "Fehler beim Laden der Firmen:",
          err,
        );


        setError(
          getErrorMessage(
            err,
            "Die Firmen konnten nicht geladen werden.",
          ),
        );


      } finally {

        setLoading(false);
        setRefreshing(false);
      }
    },
    [],
  );


  // =========================================================
  // Initiales Laden
  // =========================================================

  useEffect(() => {

    void loadCompanies(true);

  }, [loadCompanies]);


  // =========================================================
  // Suche
  // =========================================================

  const filteredCompanies =
    useMemo(() => {

      const search =
        searchText
          .trim()
          .toLocaleLowerCase("de-DE");


      if (!search) {
        return companies;
      }


      return companies.filter(
        (company) => {

          const values = [
            company.name,
            company.legalForm,
            company.taxNumber,
            company.vatId,
            company.street,
            company.zipCode,
            company.city,
            company.country,
            company.phone,
            company.email,
            company.website,
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
      companies,
      searchText,
    ]);


  // =========================================================
  // Adresse formatieren
  // =========================================================

  const formatAddress = (
    company: CompanyDto,
  ): string => {

    const street =
      company.street?.trim();


    const cityLine =
      [
        company.zipCode?.trim(),
        company.city?.trim(),
      ]
        .filter(
          (value): value is string =>
            Boolean(value),
        )
        .join(" ");


    return [
      street,
      cityLine,
      company.country?.trim(),
    ]
      .filter(
        (value): value is string =>
          Boolean(value),
      )
      .join(", ");
  };


  // =========================================================
  // Neue Firma
  // =========================================================

  const handleCreate =
    (): void => {

      navigate(
        "/companies/new",
      );
    };


  // =========================================================
  // Firma bearbeiten
  // =========================================================

  const handleEdit = (
    company: CompanyDto,
  ): void => {

    navigate(
      `/companies/${company.id}/edit`,
    );
  };


  // =========================================================
  // Aktivieren / deaktivieren
  // =========================================================

  const handleToggleActive =
    async (
      company: CompanyDto,
    ): Promise<void> => {

      if (
        actionLoadingId === company.id
      ) {
        return;
      }


      try {

        setActionLoadingId(
          company.id,
        );

        setError(null);


        const updatedCompany =
          company.active
            ? await deactivateCompany(
              company.id,
            )
            : await activateCompany(
              company.id,
            );


        /*
         * Nur die betroffene Firma aktualisieren.
         *
         * Ein erneutes Laden der gesamten Liste
         * ist nicht erforderlich.
         */
        setCompanies(
          (currentCompanies) =>
            currentCompanies.map(
              (currentCompany) =>
                currentCompany.id
                === updatedCompany.id
                  ? updatedCompany
                  : currentCompany,
            ),
        );


      } catch (err: unknown) {

        console.error(
          "Fehler beim Ändern des Firmenstatus:",
          err,
        );


        setError(
          getErrorMessage(
            err,
            "Der Firmenstatus konnte nicht geändert werden.",
          ),
        );


      } finally {

        setActionLoadingId(null);
      }
    };


  // =========================================================
  // Löschen vorbereiten
  // =========================================================

  const handleDeleteRequest = (
    company: CompanyDto,
  ): void => {

    setCompanyToDelete(company);
  };


  // =========================================================
  // Löschen abbrechen
  // =========================================================

  const handleDeleteCancel =
    (): void => {

      if (deleting) {
        return;
      }

      setCompanyToDelete(null);
    };


  // =========================================================
  // Firma löschen
  // =========================================================

  const handleDeleteConfirm =
    async (): Promise<void> => {

      if (
        !companyToDelete
        || deleting
      ) {
        return;
      }


      const companyId =
        companyToDelete.id;


      try {

        setDeleting(true);
        setError(null);


        await deleteCompany(
          companyId,
        );


        /*
         * Gelöschte Firma unmittelbar
         * aus dem lokalen State entfernen.
         */
        setCompanies(
          (currentCompanies) =>
            currentCompanies.filter(
              (company) =>
                company.id !== companyId,
            ),
        );


        setCompanyToDelete(null);


      } catch (err: unknown) {

        console.error(
          "Fehler beim Löschen der Firma:",
          err,
        );


        setError(
          getErrorMessage(
            err,
            "Die Firma konnte nicht gelöscht werden.",
          ),
        );


      } finally {

        setDeleting(false);
      }
    };


  // =========================================================
  // Initialer Ladezustand
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
            Firmen
          </Typography>

          <Typography
            variant="body2"
            color="text.secondary"
          >
            Verwaltung der Firmenstammdaten
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
              void loadCompanies();
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
            Neue Firma
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
          label="Firmen suchen"
          placeholder={
            "Name, Rechtsform, Ort, E-Mail, Steuernummer oder USt-ID"
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
          {filteredCompanies.length}
          {" "}
          {filteredCompanies.length === 1
            ? "Firma"
            : "Firmen"}
        </Typography>


        {searchText.trim() !== "" && (

          <Typography
            variant="body2"
            color="text.secondary"
          >
            Gefiltert aus{" "}
            {companies.length}{" "}
            Datensätzen
          </Typography>

        )}

      </Stack>


      {/* =====================================================
          FIRMEN-TABELLE
          ===================================================== */}

      <TableContainer
        component={Paper}
        variant="outlined"
      >

        <Table
          size="small"
          aria-label="Firmenliste"
        >

          <TableHead>

            <TableRow>

              <TableCell>
                Firma
              </TableCell>

              <TableCell>
                Rechtsform
              </TableCell>

              <TableCell>
                Adresse
              </TableCell>

              <TableCell>
                USt-IdNr.
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

            {filteredCompanies.map(
              (company) => {

                const actionLoading =
                  actionLoadingId
                  === company.id;


                return (

                  <TableRow
                    key={company.id}
                    hover
                  >

                    {/* Firmenname */}

                    <TableCell>

                      <Typography
                        variant="body2"
                        sx={{
                          fontWeight: 600,
                        }}
                      >
                        {company.name}
                      </Typography>

                    </TableCell>


                    {/* Rechtsform */}

                    <TableCell>

                      <Typography
                        variant="body2"
                        color="text.secondary"
                      >
                        {company.legalForm || "-"}
                      </Typography>

                    </TableCell>


                    {/* Adresse */}

                    <TableCell>

                      <Typography
                        variant="body2"
                        color="text.secondary"
                      >
                        {formatAddress(company) || "-"}
                      </Typography>

                    </TableCell>


                    {/* USt-ID */}

                    <TableCell>

                      <Typography
                        variant="body2"
                      >
                        {company.vatId || "-"}
                      </Typography>

                    </TableCell>


                    {/* E-Mail */}

                    <TableCell>

                      {company.email
                        ? (
                          <Link
                            href={`mailto:${company.email}`}
                            underline="hover"
                            variant="body2"
                          >
                            {company.email}
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
                        {company.phone || "-"}
                      </Typography>

                    </TableCell>


                    {/* Status */}

                    <TableCell
                      align="center"
                    >

                      <Chip
                        size="small"
                        label={
                          company.active
                            ? "Aktiv"
                            : "Inaktiv"
                        }
                        color={
                          company.active
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
                        title="Firma bearbeiten"
                      >
                        <span>

                          <IconButton
                            type="button"
                            size="small"
                            disabled={actionLoading}
                            aria-label={
                              `${company.name} bearbeiten`
                            }
                            onClick={() => {
                              handleEdit(company);
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
                          company.active
                            ? "Firma deaktivieren"
                            : "Firma aktivieren"
                        }
                      >
                        <span>

                          <IconButton
                            type="button"
                            size="small"
                            disabled={actionLoading}
                            color={
                              company.active
                                ? "default"
                                : "success"
                            }
                            aria-label={
                              company.active
                                ? `${company.name} deaktivieren`
                                : `${company.name} aktivieren`
                            }
                            onClick={() => {

                              void handleToggleActive(
                                company,
                              );
                            }}
                          >

                            {actionLoading
                              ? (
                                <CircularProgress
                                  size={20}
                                />
                              )
                              : company.active
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
                        title="Firma löschen"
                      >
                        <span>

                          <IconButton
                            type="button"
                            size="small"
                            color="error"
                            disabled={actionLoading}
                            aria-label={
                              `${company.name} löschen`
                            }
                            onClick={() => {

                              handleDeleteRequest(
                                company,
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

            {filteredCompanies.length === 0 && (

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
                        ? "Keine Firmen entsprechen den Suchkriterien."
                        : "Keine Firmen vorhanden."}
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
        open={
          companyToDelete !== null
        }
        onClose={
          deleting
            ? undefined
            : handleDeleteCancel
        }
        maxWidth="sm"
        fullWidth
      >

        <DialogTitle>
          Firma löschen
        </DialogTitle>


        <DialogContent>

          <DialogContentText>

            Soll die Firma{" "}

            <strong>
              {companyToDelete?.name}
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
            disabled={deleting}
            onClick={
              handleDeleteCancel
            }
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
