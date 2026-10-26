/**
 * Zentrale Export-Datei für alle REST-API-Funktionen.
 *
 * Dadurch können API-Funktionen zentral importiert werden:
 *
 * import {
 *   getCompanies,
 *   getCustomers,
 *   getBanks,
 * } from "../../../api";
 *
 * Statt:
 *
 * import { getCompanies } from "../../../api/companyApi";
 * import { getCustomers } from "../../../api/customerApi";
 */


// =========================================================
// Company API
// =========================================================

export {
  getCompanies,
  getActiveCompanies,
  getCompanyById,
  createCompany,
  updateCompany,
  activateCompany,
  deactivateCompany,
  deleteCompany,
} from "./companyApi";


// =========================================================
// Customer API
// =========================================================

export {
  getCustomers,
  getActiveCustomers,
  getCustomerById,
  getCustomersByCompanyId,
  createCustomer,
  updateCustomer,
  activateCustomer,
  deactivateCustomer,
  deleteCustomer,
} from "./customerApi";


// =========================================================
// Bestehende objektbasierte APIs
// =========================================================

export {
  invoiceApi,
} from "./invoiceApi";

// export {
//   productApi,
// } from "./productApi";

export {
  bankApi,
} from "./bankApi";
