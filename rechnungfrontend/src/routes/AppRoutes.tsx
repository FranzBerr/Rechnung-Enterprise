import {
  Navigate,
  Route,
  Routes,
} from "react-router-dom";

import DashboardPage from "../pages/dashboard/DashboardPage";

import BankListPage from "../pages/bank/BankListPage";
import BankAccountListPage from "../pages/bankAccount/BankAccountListPage";
import CompanyListPage from "../features/company/pages/CompanyListPage";
import CustomerListPage from "../features/customer/pages/CustomerListPage";

import ProductListPage from "../features/product/page/ProductListPage";
import ProductFormPage from "../features/product/page/ProductFormPage";

// import OfferListPage from "../features/offer/page/OfferListPage";
import DeliveryNoteListPage from "../pages/deliveryNotes/DeliveryNoteListPage";
import InvoiceListPage from "../invoice/pages/InvoiceListPage";
import CustomerFormPage from "../features/customer/pages/CustomerFormPage";
import OfferFormPage from "../features/offer/page/OfferFormPage";
import OfferListPage from "../features/offer/page/OfferListPage";


export default function AppRoutes() {

  return (
    <Routes>

      /* =================================================
       *   STARTSEITE
       * ================================================= */
      <Route
        path="/"
        element={ <Navigate to="/dashboard" replace />}
      />


      /* =================================================
       *   STARTSEITE
       *   DASHBOARD
       * ================================================= */
      <Route
        path="/dashboard"
        element={<DashboardPage />}
      />


      /* =================================================
          STAMMDATEN
      * ================================================= */

      <Route
        path="/companies"
        element={<CompanyListPage />}
      />

      /* =================================================
              Kunden
      * ================================================= */
      <Route
        path="/customers"
        element={<CustomerListPage />}
      />
      <Route
        path="/customers/new"
        element={<CustomerFormPage />}
      />

      <Route
        path="/customers/:id/edit"
        element={<CustomerFormPage />}
      />

      /* =================================================
       *  Bank
       * ================================================= */
      <Route
        path="/banks"
        element={
          <BankListPage />
        }
      />
      /* =================================================
       * Bank - Account
       * ================================================= */
      <Route
        path="/bank-accounts"
        element={
          <BankAccountListPage />
        }
      />


      /* =================================================
       *   PRODUKTE
       * ================================================= */
      <Route
        path="/products"
        element={
          <ProductListPage />
        }
      />

      <Route
        path="/products/new"
        element={
          <ProductFormPage />
        }
      />

      <Route
        path="/products/:id/edit"
        element={
          <ProductFormPage />
        }
      />


      /* =================================================
       *   ANGEBOTE
       * ================================================= */

      <Route
        path="/offers"
        element={
          <OfferListPage />
        }
      />

      <Route
        path="/offers/new"
        element={
          <OfferFormPage />
        }
      />
      <Route
        path="/offers/:id/edit"
        element={
          <OfferFormPage />
        }
      />

      /* =================================================
       *   LIEFERSCHEINE
       * ================================================= */

      <Route
        path="/delivery-notes"
        element={
          <DeliveryNoteListPage />
        }
      />


      /* =================================================
       *   RECHNUNGEN
       * ================================================= */
      <Route
        path="/invoices"
        element={
          <InvoiceListPage />
        }
      />


      /* =================================================
       *   UNBEKANNTE ROUTE
       * ================================================= */

      <Route
        path="*"
        element={
          <Navigate
            to="/dashboard"
            replace
          />
        }
      />

    </Routes>
  );
}
