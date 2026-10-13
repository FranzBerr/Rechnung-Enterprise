import {
  Box,
  Card,
  CardContent,
  Grid,
  Typography,
} from "@mui/material";

export default function DashboardPage() {
  return (
    <Box sx={{ p: 3 }}>

      <Typography
        variant="h4"
        gutterBottom
      >
        Dashboard
      </Typography>

      <Typography
        color="text.secondary"
        sx={{ mb: 3 }}
      >
        Willkommen bei Rechnung Enterprise
      </Typography>

      <Grid
        container
        spacing={3}
      >
        <Grid size={{ xs: 12, sm: 6, md: 3 }}>
          <Card>
            <CardContent>
              <Typography
                color="text.secondary"
                gutterBottom
              >
                Kunden
              </Typography>

              <Typography variant="h4">
                0
              </Typography>
            </CardContent>
          </Card>
        </Grid>

        <Grid size={{ xs: 12, sm: 6, md: 3 }}>
          <Card>
            <CardContent>
              <Typography
                color="text.secondary"
                gutterBottom
              >
                Produkte
              </Typography>

              <Typography variant="h4">
                0
              </Typography>
            </CardContent>
          </Card>
        </Grid>

        <Grid size={{ xs: 12, sm: 6, md: 3 }}>
          <Card>
            <CardContent>
              <Typography
                color="text.secondary"
                gutterBottom
              >
                Rechnungen
              </Typography>

              <Typography variant="h4">
                2
              </Typography>
            </CardContent>
          </Card>
        </Grid>

        <Grid size={{ xs: 12, sm: 6, md: 3 }}>
          <Card>
            <CardContent>
              <Typography
                color="text.secondary"
                gutterBottom
              >
                Offene Rechnungen
              </Typography>

              <Typography variant="h4">
                2
              </Typography>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      <Box sx={{ mt: 4 }}>
        <Typography
          variant="h5"
          gutterBottom
        >
          Schnellzugriff
        </Typography>

        <Typography>
          • Rechnungen anzeigen
        </Typography>

        <Typography>
          • Neue Rechnung erstellen
        </Typography>

        <Typography>
          • Kunden verwalten
        </Typography>

        <Typography>
          • Produkte verwalten
        </Typography>
      </Box>

    </Box>
  );
}
