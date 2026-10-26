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
  Typography,
} from "@mui/material";

import AddIcon from "@mui/icons-material/Add";
import RefreshIcon from "@mui/icons-material/Refresh";
import SearchIcon from "@mui/icons-material/Search";

import type {  BankAccountDto,} from "../../bankAccount/dto/BankAccountDto";

import {
  bankAccountApi,
} from "../../api/bankAccountApi";
export default function BankAccountListPage() {

  const [bankAccounts, setBankAccounts] =
    useState<BankAccountDto[]>([]);

  const [loading, setLoading] =
    useState(true);

  const [error, setError] =
    useState<string | null>(null);

  const [searchText, setSearchText] =
    useState("");

  // ---------------------------------------------------------
  // Bankkonten laden
  // ---------------------------------------------------------

  const loadBankAccounts =
    useCallback(async () => {

      try {
        setLoading(true);
        setError(null);

        const result =
          await bankAccountApi.findAll();

        setBankAccounts(
          result,
        );

      } catch (err) {

        console.error(
          "Fehler beim Laden der Bankkonten:",
          err,
        );

        setError(
          "Die Bankkonten konnten nicht geladen werden.",
        );

      } finally {

        setLoading(false);
      }

    }, []);

  useEffect(() => {

    void loadBankAccounts();

  }, [
    loadBankAccounts,
  ]);

  // ---------------------------------------------------------
  // Filtern
  // ---------------------------------------------------------

  const filteredBankAccounts =
    useMemo(() => {

      const search =
        searchText
          .trim()
          .toLowerCase();

      if (!search) {
        return bankAccounts;
      }

      return bankAccounts.filter(
        (account) => {

          return [
            account.bankName,
            account.accountHolder,
            account.iban,
            account.bic,
            account.currencyCode,
          ]
            .filter(
              (
                value,
              ): value is string =>
                Boolean(value),
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
      bankAccounts,
      searchText,
    ]);

  // ---------------------------------------------------------
  // IBAN formatieren
  // ---------------------------------------------------------

  const formatIban = (
    value: string | null | undefined,
  ): string => {

    if (!value) {
      return "-";
    }

    const normalized =
      value.replace(
        /\s+/g,
        "",
      );

    return normalized
        .match(/.{1,4}/g)
        ?.join(" ")
      ?? normalized;
  };

  // ---------------------------------------------------------
  // Ladezustand
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
            Bankkonten
          </Typography>

          <Typography
            variant="body2"
            color="text.secondary"
          >
            Verwaltung der Bankverbindungen
          </Typography>

        </Box>

        <Stack
          direction="row"
          spacing={1}
        >

          <Button
            type="button"
            variant="outlined"
            startIcon={
              <RefreshIcon />
            }
            onClick={() => {
              void loadBankAccounts();
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
          >
            Neues Bankkonto
          </Button>

        </Stack>

      </Stack>

      {/* =================================================
                FEHLER
               ================================================= */}

      {error && (

        <Alert
          severity="error"
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
          label="Bankkonten suchen"
          placeholder={
            "Bank, Kontoinhaber, IBAN, BIC oder Währung"
          }
          value={
            searchText
          }
          onChange={
            (event) => {

              setSearchText(
                event.target.value,
              );
            }
          }
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
          {filteredBankAccounts.length}
          {" "}
          Bankkonten
        </Typography>

        {searchText.trim() && (

          <Typography
            variant="body2"
            color="text.secondary"
          >
            Gefiltert aus
            {" "}
            {bankAccounts.length}
            {" "}
            Datensätzen
          </Typography>

        )}

      </Stack>

      {/* =================================================
                BANKKONTEN-TABELLE
               ================================================= */}

      <TableContainer
        component={Paper}
        variant="outlined"
      >
        <Table>

          <TableHead>

            <TableRow>

              <TableCell>
                Bank
              </TableCell>

              <TableCell>
                Kontoinhaber
              </TableCell>

              <TableCell>
                IBAN
              </TableCell>

              <TableCell>
                BIC
              </TableCell>

              <TableCell>
                Währung
              </TableCell>

              <TableCell
                align="center"
              >
                Status
              </TableCell>

            </TableRow>

          </TableHead>

          <TableBody>

            {filteredBankAccounts.map(
              (account) => (

                <TableRow
                  key={
                    account.id
                  }
                  hover
                >

                  {/* Bank */}

                  <TableCell>

                    <Typography
                      variant="body2"
                      sx={{
                        fontWeight: 600,
                      }}
                    >
                      {
                        account.bankName
                        ?? "-"
                      }
                    </Typography>

                  </TableCell>

                  {/* Kontoinhaber */}

                  <TableCell>

                    <Typography
                      variant="body2"
                    >
                      {
                        account.accountHolder
                        ?? "-"
                      }
                    </Typography>

                  </TableCell>

                  {/* IBAN */}

                  <TableCell>

                    <Typography
                      variant="body2"
                      sx={{
                        fontFamily: "monospace",
                      }}
                    >
                      {
                        formatIban(
                          account.iban,
                        )
                      }
                    </Typography>

                  </TableCell>

                  {/* BIC */}

                  <TableCell>

                    <Typography
                      variant="body2"
                      sx={{
                        fontFamily: "monospace",
                      }}
                    >
                      {
                        account.bic
                        ?? "-"
                      }
                    </Typography>

                  </TableCell>

                  {/* Währung */}

                  <TableCell>

                    <Chip
                      size="small"
                      label={
                        account.currencyCode
                        ?? "EUR"
                      }
                      variant="outlined"
                    />

                  </TableCell>

                  {/* Status */}

                  <TableCell
                    align="center"
                  >

                    <Chip
                      size="small"
                      label={
                        account.active
                          ? "Aktiv"
                          : "Inaktiv"
                      }
                      color={
                        account.active
                          ? "success"
                          : "default"
                      }
                      variant="outlined"
                    />

                  </TableCell>

                </TableRow>

              ),
            )}

            {filteredBankAccounts.length === 0 && (

              <TableRow>

                <TableCell
                  colSpan={6}
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
                      Keine Bankkonten gefunden.
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
