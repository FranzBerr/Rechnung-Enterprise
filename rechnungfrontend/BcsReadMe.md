rechnungfrontend
│
├── .env
├── .env.development
├── .env.production
├── .env.example
│
├── package.json
├── tsconfig.json
├── tsconfig.app.json
├── vite.config.ts
├── eslint.config.js
├── .prettierrc
├── .editorconfig
│
├── public
│   ├── favicon.ico
│   ├── logo.png
│   └── manifest.json
│
├── src
│   │
│   ├── main.tsx
│   ├── App.tsx
│   ├── vite-env.d.ts
│   │
│   ├── api
│   │   ├── axios.ts
│   │   ├── baseClient.ts
│   │   ├── errorUtils.ts
│   │   ├── customerApi.ts
│   │   ├── companyApi.ts
│   │   ├── productApi.ts
│   │   ├── bankAccountApi.ts
│   │   ├── invoiceApi.ts
│   │   └── index.ts
│   │
│   ├── config
│   │   ├── appConfig.ts
│   │   ├── routes.ts
│   │   ├── menuConfig.ts
│   │   ├── securityConfig.ts
│   │   └── swaggerConfig.ts
│   │
│   ├── theme
│   │   ├── theme.ts
│   │   ├── colors.ts
│   │   └── typography.ts
│   │
│   ├── layouts
│   │   ├── AppLayout.tsx
│   │   ├── MainLayout.tsx
│   │   ├── Navigation.tsx
│   │   ├── Sidebar.tsx
│   │   └── Footer.tsx
│   │
│   ├── router
│   │   └── AppRouter.tsx
│   │
│   ├── hooks
│   │   ├── useApi.ts
│   │   ├── useSnackbar.ts
│   │   ├── useLoading.ts
│   │   └── useConfirmDialog.ts
│   │
│   ├── store
│   │   ├── authStore.ts
│   │   └── appStore.ts
│   │
│   ├── utils
│   │   ├── dateUtils.ts
│   │   ├── currencyUtils.ts
│   │   ├── numberUtils.ts
│   │   ├── validationUtils.ts
│   │   └── downloadUtils.ts
│   │
│   ├── components
│   │   │
│   │   ├── common
│   │   │   ├── LoadingOverlay.tsx
│   │   │   ├── ErrorPanel.tsx
│   │   │   ├── ConfirmDialog.tsx
│   │   │   ├── SearchField.tsx
│   │   │   └── Toolbar.tsx
│   │   │
│   │   ├── grid
│   │   │   ├── DataGridToolbar.tsx
│   │   │   └── ServerDataGrid.tsx
│   │   │
│   │   ├── pdf
│   │   │   └── PdfViewerDialog.tsx
│   │   │
│   │   └── forms
│   │       ├── CurrencyField.tsx
│   │       ├── PercentageField.tsx
│   │       └── DateField.tsx
│   │
│   ├── pages
│   │   │
│   │   ├── dashboard
│   │   │   └── DashboardPage.tsx
│   │   │
│   │   ├── company
│   │   │   ├── CompanyListPage.tsx
│   │   │   └── CompanyEditPage.tsx
│   │   │
│   │   ├── bankAccount
│   │   │   ├── BankAccountListPage.tsx
│   │   │   └── BankAccountEditPage.tsx
│   │   │
│   │   ├── customer
│   │   │   ├── CustomerListPage.tsx
│   │   │   ├── CustomerEditPage.tsx
│   │   │   └── CustomerDetailsPage.tsx
│   │   │
│   │   ├── product
│   │   │   ├── ProductListPage.tsx
│   │   │   ├── ProductEditPage.tsx
│   │   │   └── ProductDetailsPage.tsx
│   │   │
│   │   └── invoice
│   │       ├── InvoiceListPage.tsx
│   │       ├── InvoiceEditPage.tsx
│   │       ├── InvoiceDetailsPage.tsx
│   │       ├── InvoicePdfPage.tsx
│   │       ├── InvoicePositionGrid.tsx
│   │       ├── InvoiceTotalsPanel.tsx
│   │       └── calculateInvoice.ts
│   │
│   ├── types
│   │   ├── common.ts
│   │   ├── customer.ts
│   │   ├── company.ts
│   │   ├── bankAccount.ts
│   │   ├── product.ts
│   │   ├── invoice.ts
│   │   └── index.ts
│   │
│   ├── test
│   │   ├── setup.ts
│   │   ├── renderWithProviders.tsx
│   │   └── testUtils.ts
│   │
│   └── assets
│       ├── images
│       └── icons
│
└── coverage
