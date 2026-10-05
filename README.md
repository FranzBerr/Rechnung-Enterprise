# Rechnung Enterprise

Formatierte Gesamtanwendung mit Backend, Frontend, SQL-Migrationen und Tests.

## Start

```powershell
docker compose up -d
mvn -pl rechnungbackend spring-boot:run
cd rechnungfrontend
npm install
npm run dev
```

## Tests

```powershell
mvn -pl rechnungbackend test
cd rechnungfrontend
npm run test:run
```
