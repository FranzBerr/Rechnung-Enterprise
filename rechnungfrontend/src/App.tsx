import { AppBar, Button, Container, Toolbar, Typography } from "@mui/material";
import { Link, Route, Routes } from "react-router-dom";
import InvoiceListPage from "./invoice/InvoiceListPage";

export default function App() {
  return (
    <>
      <AppBar position="static">
        <Toolbar>
          <Typography sx={{ flexGrow: 1 }}>Rechnung | BCS GmbH</Typography>
          <Button color="inherit" component={Link} to="/">Rechnungen</Button>
        </Toolbar>
      </AppBar>
      <Container sx={{ py: 3 }}>
        <Routes>
          <Route path="/" element={<InvoiceListPage />} />
        </Routes>
      </Container>
    </>
  );
}
