```Code

rechnungfrontend
│
├── package.json
├── package-lock.json
├── index.html
├── vite.config.ts
├── tsconfig.json
├── tsconfig.app.json
├── tsconfig.node.json
│
├── public
│   └── favicon.svg
│
├── src
│   │
│   ├── App.tsx
│   ├── main.tsx
│   ├── vite-env.d.ts
│   │
│   ├── api
│   │   ├── axiosInstance.ts
│   │   ├── index.ts
│   │   │
│   │   ├── customerApi.ts
│   │   ├── companyApi.ts
│   │   ├── bankApi.ts
│   │   ├── bankAccountApi.ts
│   │   ├── productApi.ts
│   │   └── invoiceApi.ts
│   │
│   ├── config
│   │   └── appConfig.ts
│   │
│   ├── routes
│   │   └── AppRoutes.tsx
│   │
│   ├── theme
│   │   └── appTheme.ts
│   │
│   ├── components
│   │   │
│   │   ├── layout
│   │   │   ├── AppLayout.tsx
│   │   │   ├── Header.tsx
│   │   │   └── Sidebar.tsx
│   │   │
│   │   ├── common
│   │   │   ├── PageHeader.tsx
│   │   │   ├── LoadingIndicator.tsx
│   │   │   ├── ConfirmDialog.tsx
│   │   │   └── ErrorPanel.tsx
│   │   │
│   │   └── dashboard
│   │       └── DashboardCard.tsx
│   │
│   ├── pages
│   │   │
│   │   ├── dashboard
│   │   │   └── DashboardPage.tsx
│   │   │
│   │   ├── settings
│   │   │   └── SettingsPage.tsx
│   │   │
│   │   ├── customer
│   │   │   ├── CustomerListPage.tsx
│   │   │   └── CustomerEditPage.tsx
│   │   │
│   │   ├── company
│   │   │   ├── CompanyListPage.tsx
│   │   │   └── CompanyEditPage.tsx
│   │   │
│   │   ├── bank
│   │   │   ├── BankListPage.tsx
│   │   │   └── BankEditPage.tsx
│   │   │
│   │   ├── bankAccount
│   │   │   ├── BankAccountListPage.tsx
│   │   │   └── BankAccountEditPage.tsx
│   │   │
│   │   └── product
│   │       ├── ProductListPage.tsx
│   │       └── ProductEditPage.tsx
│   │
│   ├── invoice
│   │   │
│   │   ├── pages
│   │   │   ├── InvoiceListPage.tsx
│   │   │   └── InvoiceEditPage.tsx
│   │   │
│   │   ├── components
│   │   │   ├── InvoiceCard.tsx
│   │   │   ├── InvoiceHeader.tsx
│   │   │   ├── InvoiceItemTable.tsx
│   │   │   └── InvoiceTotals.tsx
│   │   │
│   │   └── hooks
│   │       └── useInvoices.ts
│   │
│   ├── types
│   │   │
│   │   ├── index.ts
│   │   │
│   │   ├── customer.ts
│   │   ├── company.ts
│   │   ├── bank.ts
│   │   ├── bankAccount.ts
│   │   └── product.ts
│   │
│   └── invoice
│       ├── invoice.ts
│       ├── InvoiceDto.ts
│       ├── InvoiceItemDto.ts
│       └── InvoiceStatus.ts
│
rechnungfrontend
│
├── package.json
├── package-lock.json
├── index.html
├── vite.config.ts
├── tsconfig.json
├── tsconfig.app.json
├── tsconfig.node.json
│
├── public
│   └── favicon.svg
│
└── src
    │
    ├── App.tsx
    ├── main.tsx
    ├── vite-env.d.ts
    │
    ├── api
    │   ├── axiosInstance.ts
    │   ├── customerApi.ts
    │   ├── companyApi.ts
    │   ├── bankApi.ts
    │   ├── bankAccountApi.ts
    │   ├── productApi.ts
    │   ├── invoiceApi.ts
    │   └── index.ts
    │
    ├── config
    │   └── appConfig.ts
    │
    ├── routes
    │   └── AppRoutes.tsx
    │
    ├── theme
    │   └── appTheme.ts
    │
    ├── components
    │   │
    │   ├── layout
    │   │   ├── AppLayout.tsx
    │   │   ├── Header.tsx
    │   │   └── Sidebar.tsx
    │   │
    │   └── common
    │       ├── LoadingIndicator.tsx
    │       ├── ConfirmDialog.tsx
    │       └── ErrorPanel.tsx
    │
    ├── pages
    │   │
    │   ├── dashboard
    │   │   └── DashboardPage.tsx
    │   │
    │   ├── customer
    │   │   ├── CustomerListPage.tsx
    │   │   └── CustomerEditPage.tsx
    │   │
    │   ├── company
    │   │   ├── CompanyListPage.tsx
    │   │   └── CompanyEditPage.tsx
    │   │
    │   ├── bank
    │   │   ├── BankListPage.tsx
    │   │   └── BankEditPage.tsx
    │   │
    │   ├── bankAccount
    │   │   ├── BankAccountListPage.tsx
    │   │   └── BankAccountEditPage.tsx
    │   │
    │   ├── product
    │   │   ├── ProductListPage.tsx
    │   │   └── ProductEditPage.tsx
    │   │
    │   └── settings
    │       └── SettingsPage.tsx
    │
    ├── invoice
    │   │
    │   ├── pages
    │   │   ├── InvoiceListPage.tsx
    │   │   └── InvoiceEditPage.tsx
    │   │
    │   ├── components
    │   │   ├── InvoiceCard.tsx
    │   │   ├── InvoiceHeader.tsx
    │   │   ├── InvoiceItemTable.tsx
    │   │   └── InvoiceTotals.tsx
    │   │
    │   └── hooks
    │       └── useInvoices.ts
    │
    └── types
        │
        ├── index.ts
        │
        ├── customer.ts
        ├── company.ts
        ├── bank.ts
        ├── bankAccount.ts
        ├── product.ts
        │
        └── invoice
            ├── invoice.ts
            ├── InvoiceDto.ts
            ├── InvoiceItemDto.ts
            └── InvoiceStatus.ts
`````
