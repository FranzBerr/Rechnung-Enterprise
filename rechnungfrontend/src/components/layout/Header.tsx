import {
  AppBar,
  Box,
  Button,
  Toolbar,
  Typography,
} from "@mui/material";

import {
  Link,
  useLocation,
} from "react-router-dom";

interface MenuItem {
  label: string;
  path: string;
}

const menuItems: MenuItem[] = [
  {
    label: "Dashboard",
    path: "/dashboard",
  },
  {
    label: "Firmen",
    path: "/companies",
  },
  {
    label: "Kunden",
    path: "/customers",
  },
  {
    label: "Banken",
    path: "/banks",
  },
  {
    label: "Bankkonten",
    path: "/bank-accounts",
  },
  {
    label: "Produkte",
    path: "/products",
  },
  {
    label: "Rechnungen",
    path: "/invoices",
  },
];

export default function Header() {
  const location = useLocation();

  const isActive = (path: string) =>
    location.pathname === path;

  return (
    <AppBar position="static">
      <Toolbar>

        <Typography
          variant="h6"
          sx={{ mr: 4 }}
        >
          Rechnung Enterprise
        </Typography>

        <Box
          sx={{
            display: "flex",
            gap: 1,
            flexGrow: 1,
          }}
        >
          {menuItems.map((item) => (
            <Button
              key={item.path}
              component={Link}
              to={item.path}
              color="inherit"
              sx={{
                textTransform: "none",
                bgcolor: isActive(item.path)
                  ? "rgba(255,255,255,0.20)"
                  : "transparent",
              }}
            >
              {item.label}
            </Button>
          ))}
        </Box>

        <Typography variant="body2">
          BCS GmbH
        </Typography>

      </Toolbar>
    </AppBar>
  );
}
