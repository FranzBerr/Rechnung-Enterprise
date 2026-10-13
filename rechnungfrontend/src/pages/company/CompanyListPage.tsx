import {
  useEffect,
  useState,
} from "react";

import {
  Alert,
  Button,
  Card,
  CardContent,
  CircularProgress,
  Stack,
  Typography,
} from "@mui/material";

import { companyApi } from "../../api";

import type {
  Company,
} from "../../types";

export default function CompanyListPage() {
  const [companies, setCompanies] =
    useState<Company[]>([]);

  const [loading, setLoading] =
    useState(true);

  const [error, setError] =
    useState("");

  useEffect(() => {
    companyApi
      .findAll()
      .then((data) => {
        setCompanies(data);
      })
      .catch((reason: unknown) => {
        if (reason instanceof Error) {
          setError(reason.message);
        } else {
          setError(String(reason));
        }
      })
      .finally(() => {
        setLoading(false);
      });
  }, []);

  // useEffect(() => {
  //   companyApi
  //     .findAll()
  //     .then((data) => {
  //       setCompanies(data);
  //     })
  //     .catch((reason: unknown) => {
  //       if (reason instanceof Error) {
  //         setError(reason.message);
  //       } else {
  //         setError(String(reason));
  //       }
  //     })
  //     .finally(() => {
  //       setLoading(false);
  //     });
  // }, []);

  return (
    <Stack spacing={3}>
      <Stack
        direction="row"
        sx={{
          justifyContent: "space-between",
          alignItems: "center",
        }}
      >
        <Typography variant="h4">
          Firmen
        </Typography>

        <Button
          variant="contained"
          color="primary"
        >
          Neue Firma
        </Button>
      </Stack>

      {error && (
        <Alert severity="error">
          {error}
        </Alert>
      )}

      {loading && (
        <Stack
          direction="row"
          spacing={2}
          sx={{
            alignItems: "center",
          }}
        >
          <CircularProgress size={24} />

          <Typography>
            Firmen werden geladen...
          </Typography>
        </Stack>
      )}

      {!loading && (
        <Card>
          <CardContent>
            <Typography
              variant="h6"
              gutterBottom
            >
              Firmenliste
            </Typography>

            {companies.length === 0 ? (
              <Typography
                color="text.secondary"
              >
                Keine Firmen vorhanden.
              </Typography>
            ) : (
              <Stack spacing={1}>
                {companies.map(
                  (company) => (


                    <Typography
                      key={company.id}
                    >
                      {company.name}
                      {" - "}
                      {company.zipCode}
                      {" - "}
                      {company.city}
                      {" - "}
                      {company.country}
                    </Typography>
                  )
                )}
              </Stack>
            )}
          </CardContent>
        </Card>
      )}
    </Stack>
  );
}
