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

import type {
  BankDto,
} from "../../bank/dto/BankDto";

import {
  bankApi,
} from "../../api";

export default function BankListPage() {

  const [banks, setBanks] =
    useState<BankDto[]>([]);

  const [loading, setLoading] =
    useState(true);

  const [error, setError] =
    useState<string | null>(
      null,
    );

  const [searchText, setSearchText] =
    useState("");

  // ---------------------------------------------------------
  // Banken laden
  // ---------------------------------------------------------

  const loadBanks =
    useCallback(async () => {

      try {
        setLoading(true);
        setError(null);

        const result =
          await bankApi.findAll();

        setBanks(result,);
        console.log(result);
      } catch (err) {

        console.error(
          "Fehler beim Laden der Banken:",
          err,
        );

        setError(
          "Die Banken konnten nicht geladen werden.",
        );

      } finally {

        setLoading(false);
      }

    }, []);

  useEffect(() => {

    void loadBanks();

  }, [
    loadBanks,
  ]);

  // ---------------------------------------------------------
  // Banken filtern
  // ---------------------------------------------------------

  const filteredBanks =
    useMemo(() => {

      const search =
        searchText
          .trim()
          .toLowerCase();

      if (!search) {
        return banks;
      }

      return banks.filter(
        (bank) => {

          return [
            bank.bankName,
            bank.bic,
            bank.country,
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
                  .includes(
                    search,
                  ),
            );
        },
      );

    }, [
      banks,
      searchText,
    ]);

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
            sx={{
              fontWeight: 700,
            }}
          >
            Banken
          </Typography>

          <Typography
            variant="body2"
            color="text.secondary"
          >
            Verwaltung der Bankstammdaten
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
              <RefreshIcon/>
            }
            onClick={() => {
              void loadBanks();
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
          >
            Neue Bank
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
          label="Banken suchen"
          placeholder="Name, BIC oder Land"
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

          justifyContent:
            "space-between",

          alignItems:
            "center",
        }}
      >

        <Typography
          variant="body2"
          color="text.secondary"
        >
          {filteredBanks.length}
          {" "}
          Banken
        </Typography>

        {searchText.trim() && (

          <Typography
            variant="body2"
            color="text.secondary"
          >
            Gefiltert aus
            {" "}
            {banks.length}
            {" "}
            Datensätzen
          </Typography>

        )}

      </Stack>

      {/* =================================================
                BANKEN-TABELLE
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
                BIC
              </TableCell>

              <TableCell>
                Land
              </TableCell>

              <TableCell
                align="center"
              >
                Status
              </TableCell>

            </TableRow>

          </TableHead>

          <TableBody>

            {filteredBanks.map(
              (bank) => (

                <TableRow
                  key={
                    bank.id
                  }
                  hover
                >

                  {/* Bankname */}

                  <TableCell>

                    <Typography
                      variant="body2"
                      sx={{
                        fontWeight: 600,
                      }}
                    >
                      {bank.bankName}
                    </Typography>

                  </TableCell>

                  {/* BIC */}

                  <TableCell>

                    <Typography
                      variant="body2"
                    >
                      {
                        bank.bic
                        ?? "-"
                      }
                    </Typography>

                  </TableCell>

                  {/* Land */}

                  <TableCell>

                    <Typography
                      variant="body2"
                      color="text.secondary"
                    >
                      {
                        bank.country
                        ?? "-"
                      }
                    </Typography>

                  </TableCell>

                  {/* Status */}

                  <TableCell
                    align="center"
                  >

                    <Chip
                      size="small"
                      label={
                        bank.active
                          ? "Aktiv"
                          : "Inaktiv"
                      }
                      color={
                        bank.active
                          ? "success"
                          : "default"
                      }
                      variant="outlined"
                    />

                  </TableCell>

                </TableRow>

              ),
            )}

            {/* =====================================
                            Keine Datensätze
                ===================================== */}

            {
              filteredBanks.length === 0
              && (

                <TableRow>

                  <TableCell
                    colSpan={4}
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
                        Keine Banken gefunden.
                      </Typography>

                    </Box>

                  </TableCell>

                </TableRow>

              )
            }

          </TableBody>

        </Table>

      </TableContainer>

    </Box>
  );
}
