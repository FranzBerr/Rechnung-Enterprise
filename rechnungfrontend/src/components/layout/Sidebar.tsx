import type {
  ReactNode,
} from "react";

import {
  Box,
  Divider,
  Drawer,
  List,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Typography,
} from "@mui/material";

import DashboardOutlinedIcon
  from "@mui/icons-material/DashboardOutlined";

import BusinessOutlinedIcon
  from "@mui/icons-material/BusinessOutlined";

import PeopleOutlinedIcon
  from "@mui/icons-material/PeopleOutlined";

import AccountBalanceOutlinedIcon
  from "@mui/icons-material/AccountBalanceOutlined";

import CreditCardOutlinedIcon
  from "@mui/icons-material/CreditCardOutlined";

import Inventory2OutlinedIcon
  from "@mui/icons-material/Inventory2Outlined";

import RequestQuoteOutlinedIcon
  from "@mui/icons-material/RequestQuoteOutlined";

import LocalShippingOutlinedIcon
  from "@mui/icons-material/LocalShippingOutlined";

import ReceiptLongOutlinedIcon
  from "@mui/icons-material/ReceiptLongOutlined";

import {
  NavLink,
} from "react-router-dom";

interface SidebarProps {
  width: number;
  headerHeight: number;
}

interface NavigationItem {
  label: string;
  path: string;
  icon: ReactNode;
}

const masterDataItems: NavigationItem[] = [
  {
    label: "Firmen",
    path: "/companies",
    icon: <BusinessOutlinedIcon />,
  },
  {
    label: "Kunden",
    path: "/customers",
    icon: <PeopleOutlinedIcon />,
  },
  {
    label: "Banken",
    path: "/banks",
    icon: <AccountBalanceOutlinedIcon />,
  },
  {
    label: "Bankkonten",
    path: "/bank-accounts",
    icon: <CreditCardOutlinedIcon />,
  },
  {
    label: "Produkte",
    path: "/products",
    icon: <Inventory2OutlinedIcon />,
  },
];

const transactionItems: NavigationItem[] = [
  {
    label: "Angebote",
    path: "/offers",
    icon: <RequestQuoteOutlinedIcon />,
  },
  {
    label: "Lieferscheine",
    path: "/delivery-notes",
    icon: <LocalShippingOutlinedIcon />,
  },
  {
    label: "Rechnungen",
    path: "/invoices",
    icon: <ReceiptLongOutlinedIcon />,
  },
];

export default function Sidebar({
                                  width,
                                  headerHeight,
                                }: SidebarProps) {

  const renderItem = (
    item: NavigationItem,
  ) => {

    return (
      <ListItemButton
        key={item.path}
        component={NavLink}
        to={item.path}
        sx={{
          mx: 1.5,
          mb: 0.5,
          px: 2,
          minHeight: 46,
          borderRadius: 2,

          color: "text.primary",

          "& .MuiListItemIcon-root": {
            minWidth: 40,
            color: "text.secondary",
          },

          "&.active": {
            color: "primary.main",
            bgcolor: "action.selected",

            "& .MuiListItemIcon-root": {
              color: "primary.main",
            },

            "& .MuiListItemText-primary": {
              fontWeight: 600,
            },
          },

          "&:hover": {
            bgcolor: "action.hover",
          },
        }}
      >
        <ListItemIcon>
          {item.icon}
        </ListItemIcon>

        <ListItemText
          primary={item.label}
        />
      </ListItemButton>
    );
  };

  return (
    <Drawer
      variant="permanent"
      sx={{
        width,
        flexShrink: 0,

        "& .MuiDrawer-paper": {
          width,
          top: `${headerHeight}px`,

          height:
            `calc(100vh - ${headerHeight}px)`,

          boxSizing: "border-box",

          borderRight: 1,
          borderColor: "divider",

          bgcolor: "background.paper",
        },
      }}
    >
      <Box
        sx={{
          py: 2,
          height: "100%",
          overflowY: "auto",
        }}
      >
        {/* Dashboard */}

        <List
          disablePadding
        >
          {renderItem({
            label: "Dashboard",
            path: "/dashboard",
            icon: (
              <DashboardOutlinedIcon />
            ),
          })}
        </List>

        <Divider
          sx={{
            my: 2,
          }}
        />

        {/* Stammdaten */}

        <Typography
          variant="overline"
          sx={{
            display: "block",
            px: 3,
            mb: 1,
            color: "text.secondary",
            fontWeight: 700,
          }}
        >
          Stammdaten
        </Typography>

        <List
          disablePadding
        >
          {masterDataItems.map(
            renderItem,
          )}
        </List>

        <Divider
          sx={{
            my: 2,
          }}
        />

        {/* Vorgänge */}

        <Typography
          variant="overline"
          sx={{
            display: "block",
            px: 3,
            mb: 1,
            color: "text.secondary",
            fontWeight: 700,
          }}
        >
          Vorgänge
        </Typography>

        <List
          disablePadding
        >
          {transactionItems.map(
            renderItem,
          )}
        </List>
      </Box>
    </Drawer>
  );
}
