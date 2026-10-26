import {
  Alert,
  Box,
  Button,
  Card,
  Chip,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  IconButton,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Tooltip,
  Typography,
} from "@mui/material";

import AddIcon from "@mui/icons-material/Add";
import ArchiveIcon from "@mui/icons-material/Archive";
import DeleteIcon from "@mui/icons-material/Delete";
import DownloadIcon from "@mui/icons-material/Download";
import EditIcon from "@mui/icons-material/Edit";
import PictureAsPdfIcon from "@mui/icons-material/PictureAsPdf";
import RefreshIcon from "@mui/icons-material/Refresh";
import UnarchiveIcon from "@mui/icons-material/Unarchive";

import {
  useCallback,
  useEffect,
  useState,
} from "react";

import {
  useNavigate,
} from "react-router-dom";

import {
  archiveOffer,
  deleteOffer,
  downloadArchivedOfferPdf,
  downloadOfferPdf,
  getOffers,
  undoOfferArchive,
} from "../../../api/offerApi";

import {
  getApiErrorMessage,
} from "../../../errors/axiosErrorHelper";

import type {
  OfferDto,
} from "../dto/OfferDto";

import type {
  OfferStatus,
} from "../dto/OfferStatus";

import {
  OFFER_STATUS_LABELS,
} from "../dto/OfferStatus";


// =========================================================
// Datum formatieren
// =========================================================

function formatDate(
  value: string | null,
): string {

  if (!value) {
    return "-";
  }

  const date =
    new Date(
      `${value}T00:00:00`,
    );

  if (
    Number.isNaN(
      date.getTime(),
    )
  ) {
    return value;
  }

  return new Intl.DateTimeFormat(
    "de-DE",
  ).format(
    date,
  );
}


// =========================================================
// Währung formatieren
// =========================================================

function formatCurrency(
  value: number,
  currencyCode: string | null,
): string {

  const currency =
    currencyCode ?? "EUR";

  try {
    return new Intl.NumberFormat(
      "de-DE",
      {
        style: "currency",
        currency,
      },
    ).format(
      value,
    );
  } catch {
    return value.toFixed(2);
  }
}


// =========================================================
// Statusbezeichnung
// =========================================================

function getStatusLabel(
  status: OfferStatus | null,
): string {

  if (status === null) {
    return "-";
  }

  return (
    OFFER_STATUS_LABELS[
      status
      ] ?? status
  );
}


// =========================================================
// Statusfarbe
// =========================================================

function getStatusColor(
  status: OfferStatus | null,
):
  | "default"
  | "primary"
  | "secondary"
  | "success"
  | "error"
  | "warning"
  | "info" {

  switch (status) {

    case "DRAFT":
      return "default";

    case "CREATED":
      return "primary";

    case "SENT":
      return "info";

    case "ACCEPTED":
      return "success";

    case "REJECTED":
      return "error";

    case "EXPIRED":
      return "warning";

    case "CONVERTED_TO_INVOICE":
      return "secondary";

    default:
      return "default";
  }
}


// =========================================================
// Blob herunterladen
// =========================================================

function downloadBlob(
  blob: Blob,
  fileName: string,
): void {

  const url =
    URL.createObjectURL(
      blob,
    );

  const anchor =
    document.createElement(
      "a",
    );

  anchor.href =
    url;

  anchor.download =
    fileName;

  document.body.appendChild(
    anchor,
  );

  anchor.click();

  anchor.remove();

  URL.revokeObjectURL(
    url,
  );
}


// =========================================================
// OfferListPage
// =========================================================

export default function OfferListPage() {

  const navigate =
    useNavigate();

  const [
    offers,
    setOffers,
  ] =
    useState<OfferDto[]>(
      [],
    );

  const [
    loading,
    setLoading,
  ] =
    useState<boolean>(
      true,
    );

  const [
    processingId,
    setProcessingId,
  ] =
    useState<number | null>(
      null,
    );

  const [
    error,
    setError,
  ] =
    useState<string | null>(
      null,
    );

  const [
    success,
    setSuccess,
  ] =
    useState<string | null>(
      null,
    );

  const [
    deleteCandidate,
    setDeleteCandidate,
  ] =
    useState<OfferDto | null>(
      null,
    );


  // =========================================================
  // Angebote laden
  // =========================================================

  const loadOffers =
    useCallback(
      async (): Promise<void> => {

        try {
          setLoading(true);
          setError(null);

          const data =
            await getOffers();

          setOffers(
            data,
          );

        } catch (
          loadError: unknown
          ) {

          setError(
            getApiErrorMessage(
              loadError,
              "Die Angebote konnten nicht geladen werden.",
            ),
          );

        } finally {

          setLoading(false);
        }
      },
      [],
    );


  useEffect(() => {

    void loadOffers();

  }, [
    loadOffers,
  ]);


  // =========================================================
  // Neues Angebot
  // =========================================================

  const handleCreate =
    (): void => {

      navigate(
        "/offers/new",
      );
    };


  // =========================================================
  // Angebot bearbeiten
  // =========================================================

  const handleEdit = (
    offer: OfferDto,
  ): void => {

    navigate(
      `/offers/${offer.id}/edit`,
    );
  };


  // =========================================================
  // Aktuelles Angebots-PDF herunterladen
  // =========================================================

  const handleDownloadPdf =
    async (
      offer: OfferDto,
    ): Promise<void> => {

      try {
        setProcessingId(
          offer.id,
        );

        setError(null);
        setSuccess(null);

        const blob =
          await downloadOfferPdf(
            offer.id,
          );

        const fileName =
          `${offer.offerNumber ?? `Angebot-${offer.id}`}.pdf`;

        downloadBlob(
          blob,
          fileName,
        );

      } catch (
        downloadError: unknown
        ) {

        setError(
          getApiErrorMessage(
            downloadError,
            "Das Angebots-PDF konnte nicht heruntergeladen werden.",
          ),
        );

      } finally {

        setProcessingId(
          null,
        );
      }
    };


  // =========================================================
  // Angebot archivieren
  // =========================================================

  const handleArchive =
    async (
      offer: OfferDto,
    ): Promise<void> => {

      try {
        setProcessingId(
          offer.id,
        );

        setError(null);
        setSuccess(null);

        const archivedOffer =
          await archiveOffer(
            offer.id,
          );

        setOffers(
          (current) =>
            current.map(
              (currentOffer) =>
                currentOffer.id ===
                archivedOffer.id
                  ? archivedOffer
                  : currentOffer,
            ),
        );

        setSuccess(
          `Das Angebot ${archivedOffer.offerNumber ?? ""} wurde archiviert.`,
        );

      } catch (
        archiveError: unknown
        ) {

        setError(
          getApiErrorMessage(
            archiveError,
            "Das Angebot konnte nicht archiviert werden.",
          ),
        );

      } finally {

        setProcessingId(
          null,
        );
      }
    };


  // =========================================================
  // Archiviertes PDF herunterladen
  // =========================================================

  const handleDownloadArchive =
    async (
      offer: OfferDto,
    ): Promise<void> => {

      try {
        setProcessingId(
          offer.id,
        );

        setError(null);
        setSuccess(null);

        const blob =
          await downloadArchivedOfferPdf(
            offer.id,
          );

        const fileName =
          `${offer.offerNumber ?? `Angebot-${offer.id}`}-Archiv.pdf`;

        downloadBlob(
          blob,
          fileName,
        );

      } catch (
        downloadError: unknown
        ) {

        setError(
          getApiErrorMessage(
            downloadError,
            "Das archivierte Angebots-PDF konnte nicht heruntergeladen werden.",
          ),
        );

      } finally {

        setProcessingId(
          null,
        );
      }
    };


  // =========================================================
  // Archivierung zurücksetzen
  // =========================================================

  const handleUndoArchive =
    async (
      offer: OfferDto,
    ): Promise<void> => {

      try {
        setProcessingId(
          offer.id,
        );

        setError(null);
        setSuccess(null);

        await undoOfferArchive(
          offer.id,
        );

        await loadOffers();

        setSuccess(
          `Die Archivierung von ${offer.offerNumber ?? "dem Angebot"} wurde zurückgesetzt.`,
        );

      } catch (
        undoError: unknown
        ) {

        setError(
          getApiErrorMessage(
            undoError,
            "Die Archivierung konnte nicht zurückgesetzt werden.",
          ),
        );

      } finally {

        setProcessingId(
          null,
        );
      }
    };


  // =========================================================
  // Löschen vorbereiten
  // =========================================================

  const handleDeleteRequest = (
    offer: OfferDto,
  ): void => {

    setDeleteCandidate(
      offer,
    );
  };


  // =========================================================
  // Löschen abbrechen
  // =========================================================

  const handleDeleteCancel =
    (): void => {

      setDeleteCandidate(
        null,
      );
    };


  // =========================================================
  // Angebot löschen
  // =========================================================

  const handleDeleteConfirm =
    async (): Promise<void> => {

      if (
        deleteCandidate === null
      ) {
        return;
      }

      const offerToDelete =
        deleteCandidate;

      try {
        setProcessingId(
          offerToDelete.id,
        );

        setError(null);
        setSuccess(null);

        await deleteOffer(
          offerToDelete.id,
        );

        setOffers(
          (current) =>
            current.filter(
              (offer) =>
                offer.id !==
                offerToDelete.id,
            ),
        );

        setDeleteCandidate(
          null,
        );

        setSuccess(
          `Das Angebot ${offerToDelete.offerNumber ?? ""} wurde gelöscht.`,
        );

      } catch (
        deleteError: unknown
        ) {

        setError(
          getApiErrorMessage(
            deleteError,
            "Das Angebot konnte nicht gelöscht werden.",
          ),
        );

      } finally {

        setProcessingId(
          null,
        );
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
          display: "flex",
          flexDirection:
            "column",
          alignItems:
            "center",
          justifyContent:
            "center",
          gap: 2,
          p: 3,
        }}
      >
        <CircularProgress />

        <Typography>
          Angebote werden geladen ...
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
            Angebote
          </Typography>

          <Typography
            color="text.secondary"
          >
            Angebote verwalten, bearbeiten und archivieren
          </Typography>
        </Box>

        <Stack
          direction="row"
          spacing={1}
        >
          <Button
            variant="outlined"
            startIcon={
              <RefreshIcon />
            }
            onClick={() =>
              void loadOffers()
            }
          >
            Aktualisieren
          </Button>

          <Button
            variant="contained"
            startIcon={
              <AddIcon />
            }
            onClick={
              handleCreate
            }
          >
            Neues Angebot
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

      <Card>
        <TableContainer>
          <Table
            size="small"
            stickyHeader
          >
            <TableHead>
              <TableRow>
                <TableCell>
                  Angebotsnummer
                </TableCell>

                <TableCell>
                  Datum
                </TableCell>

                <TableCell>
                  Gültig bis
                </TableCell>

                <TableCell>
                  Kunde
                </TableCell>

                <TableCell>
                  Status
                </TableCell>

                <TableCell
                  align="right"
                >
                  Netto
                </TableCell>

                <TableCell
                  align="right"
                >
                  Steuer
                </TableCell>

                <TableCell
                  align="right"
                >
                  Brutto
                </TableCell>

                <TableCell
                  align="center"
                >
                  Archiv
                </TableCell>

                <TableCell
                  align="right"
                >
                  Aktionen
                </TableCell>
              </TableRow>
            </TableHead>

            <TableBody>
              {offers.length === 0 ? (
                <TableRow>
                  <TableCell
                    colSpan={10}
                    align="center"
                  >
                    <Typography
                      color="text.secondary"
                      sx={{
                        py: 4,
                      }}
                    >
                      Es sind noch keine Angebote vorhanden.
                    </Typography>
                  </TableCell>
                </TableRow>
              ) : (
                offers.map(
                  (offer) => {

                    const processing =
                      processingId ===
                      offer.id;

                    return (
                      <TableRow
                        key={
                          offer.id
                        }
                        hover
                      >
                        <TableCell>
                          <Typography
                            sx={{
                              fontWeight:
                                600,
                            }}
                          >
                            {
                              offer.offerNumber ??
                              "-"
                            }
                          </Typography>
                        </TableCell>

                        <TableCell>
                          {formatDate(
                            offer.offerDate,
                          )}
                        </TableCell>

                        <TableCell>
                          {formatDate(
                            offer.validUntil,
                          )}
                        </TableCell>

                        <TableCell>
                          <Stack
                            spacing={0.25}
                          >
                            <Typography>
                              {
                                offer.customerName ??
                                "-"
                              }
                            </Typography>

                            {offer.customerNumber && (
                              <Typography
                                variant="caption"
                                color="text.secondary"
                              >
                                {
                                  offer.customerNumber
                                }
                              </Typography>
                            )}
                          </Stack>
                        </TableCell>

                        <TableCell>
                          <Chip
                            size="small"
                            label={
                              getStatusLabel(
                                offer.status,
                              )
                            }
                            color={
                              getStatusColor(
                                offer.status,
                              )
                            }
                          />
                        </TableCell>

                        <TableCell
                          align="right"
                        >
                          {formatCurrency(
                            offer.subtotalNet,
                            offer.currencyCode,
                          )}
                        </TableCell>

                        <TableCell
                          align="right"
                        >
                          {formatCurrency(
                            offer.taxAmount,
                            offer.currencyCode,
                          )}
                        </TableCell>

                        <TableCell
                          align="right"
                        >
                          <Typography
                            sx={{
                              fontWeight:
                                600,
                            }}
                          >
                            {formatCurrency(
                              offer.totalGross,
                              offer.currencyCode,
                            )}
                          </Typography>
                        </TableCell>

                        <TableCell
                          align="center"
                        >
                          <Chip
                            size="small"
                            label={
                              offer.archived
                                ? "Archiviert"
                                : "Offen"
                            }
                            color={
                              offer.archived
                                ? "success"
                                : "default"
                            }
                            variant={
                              offer.archived
                                ? "filled"
                                : "outlined"
                            }
                          />
                        </TableCell>

                        <TableCell
                          align="right"
                        >
                          <Stack
                            direction="row"
                            spacing={0.5}
                            sx={{
                              justifyContent:
                                "flex-end",
                            }}
                          >
                            <Tooltip
                              title={
                                offer.archived
                                  ? "Archivierte Angebote können nicht bearbeitet werden."
                                  : "Angebot bearbeiten"
                              }
                            >
                              <span>
                                <IconButton
                                  size="small"
                                  color="primary"
                                  disabled={
                                    processing ||
                                    offer.archived
                                  }
                                  onClick={() =>
                                    handleEdit(
                                      offer,
                                    )
                                  }
                                >
                                  <EditIcon />
                                </IconButton>
                              </span>
                            </Tooltip>

                            <Tooltip
                              title="Aktuelles PDF herunterladen"
                            >
                              <span>
                                <IconButton
                                  size="small"
                                  disabled={
                                    processing
                                  }
                                  onClick={() =>
                                    void handleDownloadPdf(
                                      offer,
                                    )
                                  }
                                >
                                  <PictureAsPdfIcon />
                                </IconButton>
                              </span>
                            </Tooltip>

                            {!offer.archived && (
                              <Tooltip
                                title="Angebot archivieren"
                              >
                                <span>
                                  <IconButton
                                    size="small"
                                    color="primary"
                                    disabled={
                                      processing
                                    }
                                    onClick={() =>
                                      void handleArchive(
                                        offer,
                                      )
                                    }
                                  >
                                    <ArchiveIcon />
                                  </IconButton>
                                </span>
                              </Tooltip>
                            )}

                            {offer.archived && (
                              <Tooltip
                                title="Archiviertes PDF herunterladen"
                              >
                                <span>
                                  <IconButton
                                    size="small"
                                    disabled={
                                      processing
                                    }
                                    onClick={() =>
                                      void handleDownloadArchive(
                                        offer,
                                      )
                                    }
                                  >
                                    <DownloadIcon />
                                  </IconButton>
                                </span>
                              </Tooltip>
                            )}

                            {offer.archived && (
                              <Tooltip
                                title="Archivierung zurücksetzen"
                              >
                                <span>
                                  <IconButton
                                    size="small"
                                    color="warning"
                                    disabled={
                                      processing
                                    }
                                    onClick={() =>
                                      void handleUndoArchive(
                                        offer,
                                      )
                                    }
                                  >
                                    <UnarchiveIcon />
                                  </IconButton>
                                </span>
                              </Tooltip>
                            )}

                            <Tooltip
                              title={
                                offer.archived
                                  ? "Archivierte Angebote können nicht gelöscht werden."
                                  : "Angebot löschen"
                              }
                            >
                              <span>
                                <IconButton
                                  size="small"
                                  color="error"
                                  disabled={
                                    processing ||
                                    offer.archived
                                  }
                                  onClick={() =>
                                    handleDeleteRequest(
                                      offer,
                                    )
                                  }
                                >
                                  <DeleteIcon />
                                </IconButton>
                              </span>
                            </Tooltip>
                          </Stack>
                        </TableCell>
                      </TableRow>
                    );
                  },
                )
              )}
            </TableBody>
          </Table>
        </TableContainer>
      </Card>

      <Dialog
        open={
          deleteCandidate !== null
        }
        onClose={
          handleDeleteCancel
        }
        maxWidth="sm"
        fullWidth
      >
        <DialogTitle>
          Angebot löschen
        </DialogTitle>

        <DialogContent>
          <DialogContentText>
            Soll das Angebot{" "}
            <strong>
              {
                deleteCandidate
                  ?.offerNumber ??
                ""
              }
            </strong>{" "}
            wirklich gelöscht werden?
          </DialogContentText>
        </DialogContent>

        <DialogActions>
          <Button
            onClick={
              handleDeleteCancel
            }
          >
            Abbrechen
          </Button>

          <Button
            variant="contained"
            color="error"
            disabled={
              deleteCandidate !== null &&
              processingId ===
              deleteCandidate.id
            }
            onClick={() =>
              void handleDeleteConfirm()
            }
          >
            Löschen
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}
