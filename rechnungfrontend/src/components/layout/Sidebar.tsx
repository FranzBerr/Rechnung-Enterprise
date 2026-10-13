import {
  Divider,
  Drawer,
  List,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Toolbar,
  Typography,
} from "@mui/material";

import {
  AccountBalanceOutlined,
  BusinessOutlined,
  DashboardOutlined,
  Inventory2Outlined,
  PeopleOutlined,
  ReceiptLongOutlined,
  WalletOutlined,
} from "@mui/icons-material";

import {
  Link,
  useLocation,
} from "react-router-dom";

import type { ReactNode } from "react";

const drawerWidth = 260;

interface MenuItem {
  label: string;
  path: string;
  icon: ReactNode;
}

const masterDataMenuItems: MenuItem[] = [
  {
    label: "Firmen",
    path: "/companies",
    icon: <BusinessOutlined />,
  },
  {
    label: "Kunden",
    path: "/customers",
    icon: <PeopleOutlined />,
  },
  {
    label: "Banken",
    path: "/banks",
    icon: <AccountBalanceOutlined />,
  },
  {
    label: "Bankkonten",
    path: "/bank-accounts",
    icon: <WalletOutlined />,
  },
  {
    label: "Produkte",
    path: "/products",
    icon: <Inventory2Outlined />,
  },
];

export default function Sidebar() {
  const location = useLocation();

  function isActive(path: string): boolean {
    return (
      location.pathname === path ||
      location.pathname.startsWith(
        `${path}/`
      )
    );
  }

  return (
    <Drawer
      variant="permanent"
      sx={{
        width: drawerWidth,
        flexShrink: 0,
        "& .MuiDrawer-paper": {
          width: drawerWidth,
          boxSizing: "border-box",
        },
      }}
    >
      <Toolbar />

      <List>
        <ListItemButton
          component={Link}
          to="/dashboard"
          selected={isActive("/dashboard")}
          sx={menuItemStyle}
        >
          <ListItemIcon>
            <DashboardOutlined />
          </ListItemIcon>

          <ListItemText
            primary="Dashboard"
          />
        </ListItemButton>
      </List>

      <Divider />

      <Typography
        variant="overline"
        color="text.secondary"
        sx={{
          display: "block",
          px: 2,
          pt: 2,
          pb: 1,
        }}
      >
        Stammdaten
      </Typography>

      <List>
        {masterDataMenuItems.map(
          (item) => (
            <ListItemButton
              key={item.path}
              component={Link}
              to={item.path}
              selected={isActive(
                item.path
              )}
              sx={menuItemStyle}
            >
              <ListItemIcon>
                {item.icon}
              </ListItemIcon>

              <ListItemText
                primary={item.label}
              />
            </ListItemButton>
          )
        )}
      </List>

      <Divider />

      <Typography
        variant="overline"
        color="text.secondary"
        sx={{
          display: "block",
          px: 2,
          pt: 2,
          pb: 1,
        }}
      >
        Rechnungen
      </Typography>

      <List>
        <ListItemButton
          component={Link}
          to="/invoices"
          selected={isActive("/invoices")}
          sx={menuItemStyle}
        >
          <ListItemIcon>
            <ReceiptLongOutlined />
          </ListItemIcon>

          <ListItemText
            primary="Rechnungen"
          />
        </ListItemButton>
      </List>
    </Drawer>
  );
}

const menuItemStyle = {
  mx: 1,
  my: 0.25,
  borderRadius: 1,

  "&.Mui-selected": {
    bgcolor: "primary.main",
    color: "primary.contrastText",

    "& .MuiListItemIcon-root": {
      color: "primary.contrastText",
    },

    "&:hover": {
      bgcolor: "primary.dark",
    },
  },

  "&:hover": {
    bgcolor: "action.hover",
  },
};
