
```Bash
rrechnungfrontend
│
├── public
│   ├── favicon.ico
│   └── logo.png
│
├── src
│
│   ├── api
│   │   ├── axios.ts
│   │   ├── bankApi.ts
│   │   ├── bankAccountApi.ts
│   │   ├── companyApi.ts
│   │   ├── customerApi.ts
│   │   ├── productApi.ts
│   │   ├── invoiceApi.ts
│   │   └── index.ts
│   │
│   ├── types
│   │   ├── bank.ts
│   │   ├── bankAccount.ts
│   │   ├── company.ts
│   │   ├── customer.ts
│   │   ├── product.ts
│   │   ├── invoice.ts
│   │   └── index.ts
│   │
│   ├── components
│   │   │
│   │   ├── common
│   │   │   ├── ConfirmDialog.tsx
│   │   │   ├── LoadingOverlay.tsx
│   │   │   └── PageHeader.tsx
│   │   │
│   │   ├── bank
│   │   │   ├── BankGrid.tsx
│   │   │   └── BankForm.tsx
│   │   │
│   │   ├── bankAccount
│   │   │   ├── BankAccountGrid.tsx
│   │   │   └── BankAccountForm.tsx
│   │   │
│   │   ├── company
│   │   │   ├── CompanyGrid.tsx
│   │   │   └── CompanyForm.tsx
│   │   │
│   │   ├── customer
│   │   │   ├── CustomerGrid.tsx
│   │   │   └── CustomerForm.tsx
│   │   │
│   │   ├── product
│   │   │   ├── ProductGrid.tsx
│   │   │   └── ProductForm.tsx
│   │   │
│   │   └── invoice
│   │       ├── InvoiceGrid.tsx
│   │       ├── InvoiceForm.tsx
│   │       └── InvoiceItemGrid.tsx
│   │
│   ├── pages
│   │   │
│   │   ├── dashboard
│   │   │   └── DashboardPage.tsx
│   │   │
│   │   ├── bank
│   │   │   ├── BankListPage.tsx
│   │   │   ├── BankCreatePage.tsx
│   │   │   └── BankEditPage.tsx
│   │   │
│   │   ├── bankAccount
│   │   │   ├── BankAccountListPage.tsx
│   │   │   ├── BankAccountCreatePage.tsx
│   │   │   └── BankAccountEditPage.tsx
│   │   │
│   │   ├── company
│   │   │   ├── CompanyListPage.tsx
│   │   │   ├── CompanyCreatePage.tsx
│   │   │   └── CompanyEditPage.tsx
│   │   │
│   │   ├── customer
│   │   │   ├── CustomerListPage.tsx
│   │   │   ├── CustomerCreatePage.tsx
│   │   │   └── CustomerEditPage.tsx
│   │   │
│   │   ├── product
│   │   │   ├── ProductListPage.tsx
│   │   │   ├── ProductCreatePage.tsx
│   │   │   └── ProductEditPage.tsx
│   │   │
│   │   └── invoice
│   │       ├── InvoiceListPage.tsx
│   │       ├── InvoiceCreatePage.tsx
│   │       ├── InvoiceEditPage.tsx
│   │       └── InvoiceDetailsPage.tsx
│   │
│   ├── layouts
│   │   ├── AppLayout.tsx
│   │   ├── Sidebar.tsx
│   │   ├── Header.tsx
│   │   └── Footer.tsx
│   │
│   ├── router
│   │   └── AppRouter.tsx
│   │
│   ├── theme
│   │   └── theme.ts
│   │
│   ├── utils
│   │   ├── formatDate.ts
│   │   ├── formatCurrency.ts
│   │   └── validation.ts
│   │
│   ├── App.tsx
│   ├── main.tsx
│   └── vite-env.d.ts
│
├── package.json
├── tsconfig.json
├── tsconfig.app.json
├── vite.config.ts
└── index.html
``
```
