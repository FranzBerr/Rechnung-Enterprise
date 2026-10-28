package com.bcs.rechnung.invoice.pdf;

import com.bcs.rechnung.company.CompanyEntity;
import com.bcs.rechnung.invoice.InvoiceEntity;
import com.bcs.rechnung.invoice.InvoiceItemEntity;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
public class InvoicePdfGenerator {

    private static final float LEFT_MARGIN =
        45.0f;

    private static final float RIGHT_MARGIN =
        45.0f;

    private static final float TOP_MARGIN =
        42.0f;

    private static final float BOTTOM_MARGIN =
        48.0f;

    private static final float PAGE_WIDTH =
        PDRectangle.A4.getWidth();

    private static final float PAGE_HEIGHT =
        PDRectangle.A4.getHeight();

    private static final float NORMAL_FONT_SIZE =
        9.0f;

    private static final float SMALL_FONT_SIZE =
        8.0f;

    private static final float FOOTER_FONT_SIZE =
        7.0f;

    private static final float TABLE_FONT_SIZE =
        7.2f;

    private static final float TABLE_HEADER_FONT_SIZE =
        7.0f;

    private static final float TITLE_FONT_SIZE =
        18.0f;

    private static final float LINE_HEIGHT =
        13.0f;

    private static final float TABLE_HEADER_HEIGHT =
        20.0f;

    private static final float ITEM_TEXT_LINE_HEIGHT =
        10.5f;

    private static final float ITEM_VERTICAL_PADDING =
        5.0f;

    private static final float ITEM_BOTTOM_SPACING =
        5.0f;

    /*
     * Abstand zwischen der Unterstreichung
     * "Rechnungsempfänger" und dem Kundennamen.
     */
    private static final float CUSTOMER_HEADING_GAP =
        14.5f;

    /*
     * Abstand nach der vollständigen
     * Empfängeranschrift.
     */
    private static final float CUSTOMER_BLOCK_BOTTOM_GAP =
        20.0f;

    /*
     * Abstand zwischen der Trennlinie
     * und der Zeile "Gesamtbetrag".
     */
    private static final float TOTAL_SEPARATOR_BOTTOM_GAP =
        10.0f;

    /*
     * Positionsspalten
     */
    private static final float POSITION_X =
        LEFT_MARGIN;

    private static final float DESCRIPTION_X =
        72.0f;

    private static final float DESCRIPTION_MAX_WIDTH =
        175.0f;

    private static final float QUANTITY_RIGHT_X =
        320.0f;

    private static final float UNIT_PRICE_RIGHT_X =
        395.0f;

    private static final float VAT_RIGHT_X =
        435.0f;

    private static final float VAT_AMOUNT_RIGHT_X =
        490.0f;

    private static final float NET_TOTAL_RIGHT_X =
        PAGE_WIDTH - RIGHT_MARGIN;

    private static final Locale GERMAN_LOCALE =
        Locale.GERMANY;

    private static final DateTimeFormatter DATE_FORMATTER =
        DateTimeFormatter.ofPattern(
            "dd.MM.yyyy"
        );

    private final PDFont fontRegular =
        new PDType1Font(
            Standard14Fonts.FontName.HELVETICA
        );

    private final PDFont fontBold =
        new PDType1Font(
            Standard14Fonts.FontName.HELVETICA_BOLD
        );

    public byte[] generate(
        InvoiceEntity invoice
    ) throws IOException {

        validateInvoice(
            invoice
        );

        try (
            PDDocument document =
                new PDDocument();

            ByteArrayOutputStream output =
                new ByteArrayOutputStream()
        ) {
            PdfContext context =
                createPage(
                    document
                );

            context =
                drawHeader(
                    document,
                    context,
                    invoice
                );

            context =
                drawCustomerAddress(
                    document,
                    context,
                    invoice
                );

            context =
                drawInvoiceReferences(
                    document,
                    context,
                    invoice
                );

            context =
                drawItems(
                    document,
                    context,
                    invoice
                );

            context =
                drawTotals(
                    document,
                    context,
                    invoice
                );

            context =
                drawPaymentTerms(
                    document,
                    context,
                    invoice
                );

            context =
                drawPaymentInformation(
                    document,
                    context,
                    invoice
                );

            context =
                drawNotes(
                    document,
                    context,
                    invoice
                );

            /*
             * Letzten ContentStream des
             * Rechnungsinhalts schließen.
             */
            context.contentStream()
                .close();

            /*
             * Erst nach dem Erzeugen aller Seiten
             * stehen Seitenzahl und Gesamtseitenzahl fest.
             */
            drawFooters(
                document,
                invoice
            );

            document.save(
                output
            );

            return output.toByteArray();
        }
    }

    private PdfContext drawHeader(
        PDDocument document,
        PdfContext context,
        InvoiceEntity invoice
    ) throws IOException {

        context =
            ensureSpace(
                document,
                context,
                135.0f
            );

        CompanyEntity company =
            invoice.getCompany();

        float leftY =
            context.y();

        float rightY =
            context.y();

        float rightX =
            PAGE_WIDTH - RIGHT_MARGIN;

        float labelRightX =
            rightX - 125.0f;

        drawText(
            context.contentStream(),
            fontBold,
            TITLE_FONT_SIZE,
            LEFT_MARGIN,
            leftY,
            "Rechnung"
        );

        leftY -= 24.0f;

        if (company != null) {

            drawText(
                context.contentStream(),
                fontBold,
                NORMAL_FONT_SIZE,
                LEFT_MARGIN,
                leftY,
                safe(
                    company.getName()
                )
            );

            leftY -= LINE_HEIGHT;

            if (notBlank(
                company.getStreet()
            )) {

                drawText(
                    context.contentStream(),
                    fontRegular,
                    SMALL_FONT_SIZE,
                    LEFT_MARGIN,
                    leftY,
                    company.getStreet()
                );

                leftY -= LINE_HEIGHT;
            }

            String companyCity =
                joinNonBlank(
                    company.getZipCode(),
                    company.getCity()
                );

            if (notBlank(
                companyCity
            )) {

                drawText(
                    context.contentStream(),
                    fontRegular,
                    SMALL_FONT_SIZE,
                    LEFT_MARGIN,
                    leftY,
                    companyCity
                );

                leftY -= LINE_HEIGHT;
            }
        }

        rightY =
            drawHeaderRightRow(
                context.contentStream(),
                labelRightX,
                rightX,
                rightY,
                "Rechnungsnummer:",
                invoice.getInvoiceNumber()
            );

        rightY =
            drawHeaderRightRow(
                context.contentStream(),
                labelRightX,
                rightX,
                rightY,
                "Rechnungsdatum:",
                formatDate(
                    invoice.getInvoiceDate()
                )
            );

        if (invoice.getServiceDate() != null) {

            rightY =
                drawHeaderRightRow(
                    context.contentStream(),
                    labelRightX,
                    rightX,
                    rightY,
                    "Leistungsdatum:",
                    formatDate(
                        invoice.getServiceDate()
                    )
                );
        }

        if (notBlank(
            invoice.getCustomerNumber()
        )) {

            rightY =
                drawHeaderRightRow(
                    context.contentStream(),
                    labelRightX,
                    rightX,
                    rightY,
                    "Kundennummer:",
                    invoice.getCustomerNumber()
                );
        }

        if (notBlank(
            invoice.getOrderReference()
        )) {

            rightY =
                drawHeaderRightRow(
                    context.contentStream(),
                    labelRightX,
                    rightX,
                    rightY,
                    "Auftragsreferenz:",
                    invoice.getOrderReference()
                );
        }

        if (invoice.getDueDate() != null) {

            rightY =
                drawHeaderRightRow(
                    context.contentStream(),
                    labelRightX,
                    rightX,
                    rightY,
                    "Fällig am:",
                    formatDate(
                        invoice.getDueDate()
                    )
                );
        }

        if (company != null
            && notBlank(
            company.getVatId()
        )) {

            rightY =
                drawHeaderRightRow(
                    context.contentStream(),
                    labelRightX,
                    rightX,
                    rightY,
                    "USt-IdNr.:",
                    company.getVatId()
                );
        }

        if (company != null
            && notBlank(
            company.getTaxNumber()
        )) {

            rightY =
                drawHeaderRightRow(
                    context.contentStream(),
                    labelRightX,
                    rightX,
                    rightY,
                    "Steuernummer:",
                    company.getTaxNumber()
                );
        }

        return context.withY(
            Math.min(
                leftY,
                rightY
            ) - 12.0f
        );
    }

    private float drawHeaderRightRow(
        PDPageContentStream contentStream,
        float labelRightX,
        float valueRightX,
        float y,
        String label,
        String value
    ) throws IOException {

        drawTextRight(
            contentStream,
            fontBold,
            SMALL_FONT_SIZE,
            labelRightX,
            y,
            label
        );

        drawTextRight(
            contentStream,
            fontRegular,
            SMALL_FONT_SIZE,
            valueRightX,
            y,
            safe(
                value
            )
        );

        return y - LINE_HEIGHT;
    }

    private PdfContext drawCustomerAddress(
        PDDocument document,
        PdfContext context,
        InvoiceEntity invoice
    ) throws IOException {

        context =
            ensureSpace(
                document,
                context,
                120.0f
            );

        float y =
            context.y();

        String heading =
            "Rechnungsempfänger";

        drawText(
            context.contentStream(),
            fontBold,
            NORMAL_FONT_SIZE,
            LEFT_MARGIN,
            y,
            heading
        );

        float headingWidth =
            calculateTextWidth(
                fontBold,
                NORMAL_FONT_SIZE,
                heading
            );

        float underlineY =
            y - 2.5f;

        /*
         * Unterstreichung 6 pt länger
         * als die Überschrift.
         */
        drawLine(
            context.contentStream(),
            LEFT_MARGIN,
            underlineY,
            LEFT_MARGIN
                + headingWidth
                + 6.0f,
            underlineY
        );

        /*
         * Abstand zwischen Unterstreichung
         * und Kundenname.
         */
        y -= CUSTOMER_HEADING_GAP;

        if (notBlank(
            invoice.getCustomerName()
        )) {

            drawText(
                context.contentStream(),
                fontBold,
                NORMAL_FONT_SIZE,
                LEFT_MARGIN,
                y,
                invoice.getCustomerName()
            );

            y -= LINE_HEIGHT;
        }

        if (notBlank(
            invoice.getCustomerStreet()
        )) {

            drawText(
                context.contentStream(),
                fontRegular,
                NORMAL_FONT_SIZE,
                LEFT_MARGIN,
                y,
                invoice.getCustomerStreet()
            );

            y -= LINE_HEIGHT;
        }

        String customerCity =
            joinNonBlank(
                invoice.getCustomerZipCode(),
                invoice.getCustomerCity()
            );

        if (notBlank(
            customerCity
        )) {

            drawText(
                context.contentStream(),
                fontRegular,
                NORMAL_FONT_SIZE,
                LEFT_MARGIN,
                y,
                customerCity
            );

            y -= LINE_HEIGHT;
        }

        if (notBlank(
            invoice.getCustomerCountry()
        )) {

            drawText(
                context.contentStream(),
                fontRegular,
                NORMAL_FONT_SIZE,
                LEFT_MARGIN,
                y,
                invoice.getCustomerCountry()
            );

            y -= LINE_HEIGHT;
        }

        return context.withY(
            y - CUSTOMER_BLOCK_BOTTOM_GAP
        );
    }

    private PdfContext drawInvoiceReferences(
        PDDocument document,
        PdfContext context,
        InvoiceEntity invoice
    ) throws IOException {

        List<LabelValue> references =
            new ArrayList<>();

        if (notBlank(
            invoice.getBuyerReference()
        )) {

            references.add(
                new LabelValue(
                    "Käuferreferenz:",
                    invoice.getBuyerReference()
                )
            );
        }

        if (notBlank(
            invoice.getProjectReference()
        )) {

            references.add(
                new LabelValue(
                    "Projektreferenz:",
                    invoice.getProjectReference()
                )
            );
        }

        if (notBlank(
            invoice.getContractReference()
        )) {

            references.add(
                new LabelValue(
                    "Vertragsreferenz:",
                    invoice.getContractReference()
                )
            );
        }

        if (references.isEmpty()) {
            return context;
        }

        context =
            ensureSpace(
                document,
                context,
                references.size()
                    * LINE_HEIGHT
                    + 12.0f
            );

        float y =
            context.y();

        for (LabelValue reference : references) {

            y =
                drawLabelValue(
                    context.contentStream(),
                    LEFT_MARGIN,
                    LEFT_MARGIN + 115.0f,
                    y,
                    reference.label(),
                    reference.value()
                );
        }

        return context.withY(
            y - 10.0f
        );
    }

    private PdfContext drawItems(
        PDDocument document,
        PdfContext context,
        InvoiceEntity invoice
    ) throws IOException {

        context =
            ensureSpace(
                document,
                context,
                TABLE_HEADER_HEIGHT
                    + 25.0f
            );

        context =
            drawTableHeader(
                context
            );

        for (
            InvoiceItemEntity item :
            invoice.getItems()
        ) {
            List<String> descriptionLines =
                createDescriptionLines(
                    item.getDescription()
                );

            float descriptionHeight =
                descriptionLines.size()
                    * ITEM_TEXT_LINE_HEIGHT;

            float rowHeight =
                Math.max(
                    TABLE_HEADER_HEIGHT,
                    descriptionHeight
                        + ITEM_VERTICAL_PADDING
                        * 2.0f
                );

            if (context.y()
                - rowHeight
                - ITEM_BOTTOM_SPACING
                < BOTTOM_MARGIN + 20.0f) {

                context.contentStream()
                    .close();

                context =
                    createPage(
                        document
                    );

                context =
                    drawContinuationHeader(
                        context,
                        invoice
                    );

                context =
                    drawTableHeader(
                        context
                    );
            }

            float firstLineY =
                context.y();

            drawText(
                context.contentStream(),
                fontRegular,
                TABLE_FONT_SIZE,
                POSITION_X,
                firstLineY,
                String.valueOf(
                    item.getPositionNumber()
                )
            );

            float descriptionY =
                firstLineY;

            for (
                String descriptionLine :
                descriptionLines
            ) {
                drawText(
                    context.contentStream(),
                    fontRegular,
                    TABLE_FONT_SIZE,
                    DESCRIPTION_X,
                    descriptionY,
                    descriptionLine
                );

                descriptionY -=
                    ITEM_TEXT_LINE_HEIGHT;
            }

            drawTextRight(
                context.contentStream(),
                fontRegular,
                TABLE_FONT_SIZE,
                QUANTITY_RIGHT_X,
                firstLineY,
                formatQuantity(
                    item.getQuantity()
                )
                    + " "
                    + formatUnit(
                    item.getUnit()
                )
            );

            drawTextRight(
                context.contentStream(),
                fontRegular,
                TABLE_FONT_SIZE,
                UNIT_PRICE_RIGHT_X,
                firstLineY,
                formatMoney(
                    item.getUnitPriceNet()
                )
            );

            drawTextRight(
                context.contentStream(),
                fontRegular,
                TABLE_FONT_SIZE,
                VAT_RIGHT_X,
                firstLineY,
                formatVatRate(
                    item.getVatRate()
                )
            );

            drawTextRight(
                context.contentStream(),
                fontRegular,
                TABLE_FONT_SIZE,
                VAT_AMOUNT_RIGHT_X,
                firstLineY,
                formatMoney(
                    resolveItemTaxAmount(
                        item
                    )
                )
            );

            drawTextRight(
                context.contentStream(),
                fontRegular,
                TABLE_FONT_SIZE,
                NET_TOTAL_RIGHT_X,
                firstLineY,
                formatMoney(
                    item.getLineNet()
                )
            );

            float separatorY =
                firstLineY
                    - rowHeight
                    + 3.0f;

            drawLine(
                context.contentStream(),
                LEFT_MARGIN,
                separatorY,
                PAGE_WIDTH - RIGHT_MARGIN,
                separatorY
            );

            context =
                context.withY(
                    firstLineY
                        - rowHeight
                        - ITEM_BOTTOM_SPACING
                );
        }

        return context.withY(
            context.y() - 8.0f
        );
    }

    private PdfContext drawTableHeader(
        PdfContext context
    ) throws IOException {

        float y =
            context.y();

        drawText(
            context.contentStream(),
            fontBold,
            TABLE_HEADER_FONT_SIZE,
            POSITION_X,
            y,
            "Pos."
        );

        drawText(
            context.contentStream(),
            fontBold,
            TABLE_HEADER_FONT_SIZE,
            DESCRIPTION_X,
            y,
            "Beschreibung"
        );

        drawTextRight(
            context.contentStream(),
            fontBold,
            TABLE_HEADER_FONT_SIZE,
            QUANTITY_RIGHT_X,
            y,
            "Menge"
        );

        drawTextRight(
            context.contentStream(),
            fontBold,
            TABLE_HEADER_FONT_SIZE,
            UNIT_PRICE_RIGHT_X,
            y,
            "Einzelpreis"
        );

        drawTextRight(
            context.contentStream(),
            fontBold,
            TABLE_HEADER_FONT_SIZE,
            VAT_RIGHT_X,
            y,
            "MwSt."
        );

        drawTextRight(
            context.contentStream(),
            fontBold,
            TABLE_HEADER_FONT_SIZE,
            VAT_AMOUNT_RIGHT_X,
            y,
            "MwSt.-Betrag"
        );

        drawTextRight(
            context.contentStream(),
            fontBold,
            TABLE_HEADER_FONT_SIZE,
            NET_TOTAL_RIGHT_X,
            y,
            "Netto"
        );

        drawLine(
            context.contentStream(),
            LEFT_MARGIN,
            y - 5.0f,
            PAGE_WIDTH - RIGHT_MARGIN,
            y - 5.0f
        );

        return context.withY(
            y - TABLE_HEADER_HEIGHT
        );
    }

    private List<String> createDescriptionLines(
        String description
    ) throws IOException {

        List<String> result =
            new ArrayList<>();

        if (!notBlank(
            description
        )) {

            result.add(
                ""
            );

            return result;
        }

        String normalized =
            description
                .replace(
                    "\\r\\n",
                    "\n"
                )
                .replace(
                    "\\n",
                    "\n"
                )
                .replace(
                    "\\r",
                    "\n"
                )
                .replaceAll(
                    "(?i)<br\\s*/?>",
                    "\n"
                )
                .replace(
                    "\r\n",
                    "\n"
                )
                .replace(
                    '\r',
                    '\n'
                )
                .replace(
                    '\t',
                    '\n'
                )
                .replace(
                    '\u00A0',
                    ' '
                )
                .replaceAll(
                    " {2,}",
                    "\n"
                )
                .trim();

        String[] sourceLines =
            normalized.split(
                "\\n+"
            );

        for (String sourceLine : sourceLines) {

            String trimmedLine =
                sourceLine.trim();

            if (!trimmedLine.isEmpty()) {

                result.addAll(
                    wrapDescriptionLine(
                        trimmedLine
                    )
                );
            }
        }

        if (result.isEmpty()) {

            result.add(
                ""
            );
        }

        return result;
    }

    private List<String> wrapDescriptionLine(
        String text
    ) throws IOException {

        List<String> lines =
            new ArrayList<>();

        String[] words =
            text.trim()
                .split(
                    "\\s+"
                );

        StringBuilder currentLine =
            new StringBuilder();

        for (String word : words) {

            String candidate =
                currentLine.isEmpty()
                    ? word
                    : currentLine
                    + " "
                    + word;

            if (calculateTextWidth(
                fontRegular,
                TABLE_FONT_SIZE,
                candidate
            ) <= DESCRIPTION_MAX_WIDTH) {

                currentLine.setLength(
                    0
                );

                currentLine.append(
                    candidate
                );

                continue;
            }

            if (!currentLine.isEmpty()) {

                lines.add(
                    currentLine.toString()
                );

                currentLine.setLength(
                    0
                );
            }

            if (calculateTextWidth(
                fontRegular,
                TABLE_FONT_SIZE,
                word
            ) <= DESCRIPTION_MAX_WIDTH) {

                currentLine.append(
                    word
                );

            } else {

                lines.addAll(
                    splitLongWord(
                        word
                    )
                );
            }
        }

        if (!currentLine.isEmpty()) {

            lines.add(
                currentLine.toString()
            );
        }

        return lines;
    }

    private List<String> splitLongWord(
        String word
    ) throws IOException {

        List<String> result =
            new ArrayList<>();

        StringBuilder currentPart =
            new StringBuilder();

        for (
            int index = 0;
            index < word.length();
            index++
        ) {
            char character =
                word.charAt(
                    index
                );

            String candidate =
                currentPart.toString()
                    + character;

            if (calculateTextWidth(
                fontRegular,
                TABLE_FONT_SIZE,
                candidate
            ) <= DESCRIPTION_MAX_WIDTH) {

                currentPart.append(
                    character
                );

                continue;
            }

            if (!currentPart.isEmpty()) {

                result.add(
                    currentPart.toString()
                );

                currentPart.setLength(
                    0
                );
            }

            currentPart.append(
                character
            );
        }

        if (!currentPart.isEmpty()) {

            result.add(
                currentPart.toString()
            );
        }

        return result;
    }

    private float calculateTextWidth(
        PDFont font,
        float fontSize,
        String text
    ) throws IOException {

        String printableText =
            sanitizePdfText(
                text
            );

        return font.getStringWidth(
            printableText
        )
            / 1000.0f
            * fontSize;
    }

    private PdfContext drawContinuationHeader(
        PdfContext context,
        InvoiceEntity invoice
    ) throws IOException {

        float y =
            context.y();

        drawText(
            context.contentStream(),
            fontBold,
            NORMAL_FONT_SIZE,
            LEFT_MARGIN,
            y,
            "Rechnung "
                + safe(
                invoice.getInvoiceNumber()
            )
        );

        drawTextRight(
            context.contentStream(),
            fontRegular,
            SMALL_FONT_SIZE,
            PAGE_WIDTH - RIGHT_MARGIN,
            y,
            "Fortsetzung"
        );

        return context.withY(
            y - 25.0f
        );
    }

    private BigDecimal resolveItemTaxAmount(
        InvoiceItemEntity item
    ) {

        if (item.getTaxAmount() != null) {

            return item.getTaxAmount()
                .setScale(
                    2,
                    RoundingMode.HALF_UP
                );
        }

        if (item.getLineNet() == null
            || item.getVatRate() == null) {

            return BigDecimal.ZERO;
        }

        return item.getLineNet()
            .multiply(
                item.getVatRate()
            )
            .divide(
                BigDecimal.valueOf(
                    100
                ),
                2,
                RoundingMode.HALF_UP
            );
    }

    private PdfContext drawTotals(
        PDDocument document,
        PdfContext context,
        InvoiceEntity invoice
    ) throws IOException {

        Map<BigDecimal, TaxSummary> taxSummaries =
            calculateTaxSummaries(
                invoice
            );

        float requiredHeight =
            75.0f
                + Math.max(
                1,
                taxSummaries.size()
            )
                * LINE_HEIGHT;

        context =
            ensureSpace(
                document,
                context,
                requiredHeight
            );

        float labelX =
            PAGE_WIDTH
                - RIGHT_MARGIN
                - 230.0f;

        float amountX =
            PAGE_WIDTH - RIGHT_MARGIN;

        float y =
            context.y();

        drawAmountLine(
            context.contentStream(),
            labelX,
            amountX,
            y,
            "Nettobetrag",
            invoice.getSubtotalNet(),
            false
        );

        y -= LINE_HEIGHT;

        if (!taxSummaries.isEmpty()) {

            for (
                TaxSummary taxSummary :
                taxSummaries.values()
            ) {
                String vatLabel =
                    "MwSt. "
                        + formatVatRate(
                        taxSummary.rate()
                    );

                drawAmountLine(
                    context.contentStream(),
                    labelX,
                    amountX,
                    y,
                    vatLabel,
                    taxSummary.taxAmount(),
                    false
                );

                y -= LINE_HEIGHT;
            }

        } else {

            drawAmountLine(
                context.contentStream(),
                labelX,
                amountX,
                y,
                "MwSt.",
                invoice.getTaxAmount(),
                false
            );

            y -= LINE_HEIGHT;
        }

        /*
         * Trennlinie nach dem Umsatzsteuerbereich.
         */
        float separatorY =
            y + 5.0f;

        drawLine(
            context.contentStream(),
            labelX,
            separatorY,
            amountX,
            separatorY
        );

        /*
         * Vergrößerter Abstand zwischen
         * Trennlinie und Gesamtbetrag.
         */
        y -= TOTAL_SEPARATOR_BOTTOM_GAP;

        drawAmountLine(
            context.contentStream(),
            labelX,
            amountX,
            y,
            "Gesamtbetrag",
            invoice.getTotalGross(),
            true
        );

        return context.withY(
            y - 25.0f
        );
    }

    private void drawAmountLine(
        PDPageContentStream contentStream,
        float labelX,
        float amountX,
        float y,
        String label,
        BigDecimal amount,
        boolean bold
    ) throws IOException {

        PDFont font =
            bold
                ? fontBold
                : fontRegular;

        drawText(
            contentStream,
            font,
            NORMAL_FONT_SIZE,
            labelX,
            y,
            label
        );

        drawTextRight(
            contentStream,
            font,
            NORMAL_FONT_SIZE,
            amountX,
            y,
            formatMoney(
                amount
            )
        );
    }

    private Map<BigDecimal, TaxSummary>
    calculateTaxSummaries(
        InvoiceEntity invoice
    ) {

        Map<BigDecimal, TaxSummary> result =
            new LinkedHashMap<>();

        if (invoice.getItems() == null) {
            return result;
        }

        for (
            InvoiceItemEntity item :
            invoice.getItems()
        ) {
            BigDecimal taxRate =
                item.getVatRate();

            BigDecimal lineNet =
                item.getLineNet();

            if (taxRate == null
                || lineNet == null) {

                continue;
            }

            BigDecimal normalizedRate =
                taxRate.stripTrailingZeros();

            BigDecimal taxAmount =
                resolveItemTaxAmount(
                    item
                );

            TaxSummary existing =
                result.get(
                    normalizedRate
                );

            if (existing == null) {

                result.put(
                    normalizedRate,
                    new TaxSummary(
                        normalizedRate,
                        lineNet,
                        taxAmount
                    )
                );

            } else {

                result.put(
                    normalizedRate,
                    new TaxSummary(
                        normalizedRate,
                        existing.netAmount()
                            .add(
                                lineNet
                            ),
                        existing.taxAmount()
                            .add(
                                taxAmount
                            )
                    )
                );
            }
        }

        return result;
    }

    private PdfContext drawPaymentTerms(
        PDDocument document,
        PdfContext context,
        InvoiceEntity invoice
    ) throws IOException {

        List<String> paymentTermLines =
            createPaymentTermLines(
                invoice
            );

        if (paymentTermLines.isEmpty()) {
            return context;
        }

        float requiredHeight =
            22.0f
                + paymentTermLines.size()
                * LINE_HEIGHT;

        context =
            ensureSpace(
                document,
                context,
                requiredHeight
            );

        float y =
            context.y();

        drawText(
            context.contentStream(),
            fontBold,
            NORMAL_FONT_SIZE,
            LEFT_MARGIN,
            y,
            "Zahlungsbedingungen"
        );

        y -= LINE_HEIGHT;

        for (
            String paymentTermLine :
            paymentTermLines
        ) {
            drawText(
                context.contentStream(),
                fontRegular,
                NORMAL_FONT_SIZE,
                LEFT_MARGIN,
                y,
                paymentTermLine
            );

            y -= LINE_HEIGHT;
        }

        return context.withY(
            y - 8.0f
        );
    }

    private List<String> createPaymentTermLines(
        InvoiceEntity invoice
    ) {

        List<String> lines =
            new ArrayList<>();

        Integer paymentTermDays =
            invoice.getPaymentTermDays();

        LocalDate dueDate =
            invoice.getDueDate();

        if (paymentTermDays != null
            && paymentTermDays > 0) {

            StringBuilder paymentLine =
                new StringBuilder();

            paymentLine.append(
                "Zahlbar innerhalb von "
            );

            paymentLine.append(
                paymentTermDays
            );

            paymentLine.append(
                paymentTermDays == 1
                    ? " Tag"
                    : " Tagen"
            );

            if (dueDate != null) {

                paymentLine.append(
                    ", spätestens bis zum "
                );

                paymentLine.append(
                    formatDate(
                        dueDate
                    )
                );
            }

            paymentLine.append(
                "."
            );

            lines.add(
                paymentLine.toString()
            );

        } else if (dueDate != null) {

            lines.add(
                "Zahlbar ohne Abzug bis zum "
                    + formatDate(
                    dueDate
                )
                    + "."
            );

        } else {

            lines.add(
                "Zahlbar ohne Abzug."
            );
        }

        if (hasDiscount(
            invoice
        )) {

            StringBuilder discountLine =
                new StringBuilder();

            discountLine.append(
                "Bei Zahlung innerhalb von "
            );

            discountLine.append(
                invoice.getDiscountDays()
            );

            discountLine.append(
                invoice.getDiscountDays() == 1
                    ? " Tag"
                    : " Tagen"
            );

            discountLine.append(
                " gewähren wir "
            );

            discountLine.append(
                formatPercent(
                    invoice.getDiscountPercent()
                )
            );

            discountLine.append(
                " % Skonto."
            );

            lines.add(
                discountLine.toString()
            );
        }

        return lines;
    }

    private boolean hasDiscount(
        InvoiceEntity invoice
    ) {

        return invoice.getDiscountPercent() != null
            && invoice.getDiscountPercent()
            .compareTo(
                BigDecimal.ZERO
            ) > 0
            && invoice.getDiscountDays() != null
            && invoice.getDiscountDays() > 0;
    }

    private PdfContext drawPaymentInformation(
        PDDocument document,
        PdfContext context,
        InvoiceEntity invoice
    ) throws IOException {

        context =
            ensureSpace(
                document,
                context,
                90.0f
            );

        float y =
            context.y();

        drawText(
            context.contentStream(),
            fontBold,
            NORMAL_FONT_SIZE,
            LEFT_MARGIN,
            y,
            "Zahlungsinformationen"
        );

        y -= LINE_HEIGHT;

        if (notBlank(
            invoice.getBankName()
        )) {

            y =
                drawLabelValue(
                    context.contentStream(),
                    LEFT_MARGIN,
                    LEFT_MARGIN + 80.0f,
                    y,
                    "Bank:",
                    invoice.getBankName()
                );
        }

        if (notBlank(
            invoice.getIban()
        )) {

            y =
                drawLabelValue(
                    context.contentStream(),
                    LEFT_MARGIN,
                    LEFT_MARGIN + 80.0f,
                    y,
                    "IBAN:",
                    formatIban(
                        invoice.getIban()
                    )
                );
        }

        if (notBlank(
            invoice.getBic()
        )) {

            y =
                drawLabelValue(
                    context.contentStream(),
                    LEFT_MARGIN,
                    LEFT_MARGIN + 80.0f,
                    y,
                    "BIC:",
                    invoice.getBic()
                );
        }

        if (notBlank(
            invoice.getPaymentReference()
        )) {

            y =
                drawLabelValue(
                    context.contentStream(),
                    LEFT_MARGIN,
                    LEFT_MARGIN + 80.0f,
                    y,
                    "Verwendung:",
                    invoice.getPaymentReference()
                );
        }

        return context.withY(
            y - 12.0f
        );
    }

    private PdfContext drawNotes(
        PDDocument document,
        PdfContext context,
        InvoiceEntity invoice
    ) throws IOException {

        if (!notBlank(
            invoice.getNotes()
        )) {
            return context;
        }

        List<String> noteLines =
            wrapText(
                invoice.getNotes(),
                fontRegular,
                NORMAL_FONT_SIZE,
                PAGE_WIDTH
                    - LEFT_MARGIN
                    - RIGHT_MARGIN
            );

        context =
            ensureSpace(
                document,
                context,
                noteLines.size()
                    * LINE_HEIGHT
                    + 10.0f
            );

        float y =
            context.y();

        for (String line : noteLines) {

            drawText(
                context.contentStream(),
                fontRegular,
                NORMAL_FONT_SIZE,
                LEFT_MARGIN,
                y,
                line
            );

            y -= LINE_HEIGHT;
        }

        return context.withY(
            y - 12.0f
        );
    }

    private List<String> wrapText(
        String text,
        PDFont font,
        float fontSize,
        float maximumWidth
    ) throws IOException {

        List<String> lines =
            new ArrayList<>();

        if (!notBlank(
            text
        )) {
            return lines;
        }

        String[] words =
            safe(
                text
            ).split(
                "\\s+"
            );

        StringBuilder currentLine =
            new StringBuilder();

        for (String word : words) {

            String candidate =
                currentLine.isEmpty()
                    ? word
                    : currentLine
                    + " "
                    + word;

            if (calculateTextWidth(
                font,
                fontSize,
                candidate
            ) <= maximumWidth) {

                currentLine.setLength(
                    0
                );

                currentLine.append(
                    candidate
                );

            } else {

                if (!currentLine.isEmpty()) {

                    lines.add(
                        currentLine.toString()
                    );
                }

                currentLine.setLength(
                    0
                );

                currentLine.append(
                    word
                );
            }
        }

        if (!currentLine.isEmpty()) {

            lines.add(
                currentLine.toString()
            );
        }

        return lines;
    }

    /*
     * Fügt nach Fertigstellung des Dokuments
     * auf jeder Seite Rechnungsnummer und Seitenzahl ein.
     */
    private void drawFooters(
        PDDocument document,
        InvoiceEntity invoice
    ) throws IOException {

        int totalPages =
            document.getNumberOfPages();

        for (
            int pageIndex = 0;
            pageIndex < totalPages;
            pageIndex++
        ) {
            PDPage page =
                document.getPage(
                    pageIndex
                );

            try (
                PDPageContentStream contentStream =
                    new PDPageContentStream(
                        document,
                        page,
                        PDPageContentStream.AppendMode.APPEND,
                        true,
                        true
                    )
            ) {
                drawFooter(
                    contentStream,
                    invoice,
                    pageIndex + 1,
                    totalPages
                );
            }
        }
    }

    private void drawFooter(
        PDPageContentStream contentStream,
        InvoiceEntity invoice,
        int currentPage,
        int totalPages
    ) throws IOException {

        float footerY =
            BOTTOM_MARGIN - 5.0f;

        String invoiceText =
            "Rechnung "
                + safe(
                invoice.getInvoiceNumber()
            );

        String pageText =
            "Seite "
                + currentPage
                + " von "
                + totalPages;

        /*
         * Rechnungsnummer linksbündig.
         */
        drawText(
            contentStream,
            fontRegular,
            FOOTER_FONT_SIZE,
            LEFT_MARGIN,
            footerY,
            invoiceText
        );

        /*
         * Seitenangabe rechtsbündig.
         */
        drawTextRight(
            contentStream,
            fontRegular,
            FOOTER_FONT_SIZE,
            PAGE_WIDTH - RIGHT_MARGIN,
            footerY,
            pageText
        );
    }

    private PdfContext ensureSpace(
        PDDocument document,
        PdfContext context,
        float requiredHeight
    ) throws IOException {

        if (context.y()
            - requiredHeight
            >= BOTTOM_MARGIN + 20.0f) {

            return context;
        }

        context.contentStream()
            .close();

        return createPage(
            document
        );
    }

    private PdfContext createPage(
        PDDocument document
    ) throws IOException {

        PDPage page =
            new PDPage(
                PDRectangle.A4
            );

        document.addPage(
            page
        );

        PDPageContentStream contentStream =
            new PDPageContentStream(
                document,
                page
            );

        return new PdfContext(
            page,
            contentStream,
            PAGE_HEIGHT - TOP_MARGIN
        );
    }

    private float drawLabelValue(
        PDPageContentStream contentStream,
        float labelX,
        float valueX,
        float y,
        String label,
        String value
    ) throws IOException {

        drawText(
            contentStream,
            fontBold,
            SMALL_FONT_SIZE,
            labelX,
            y,
            label
        );

        drawText(
            contentStream,
            fontRegular,
            SMALL_FONT_SIZE,
            valueX,
            y,
            safe(
                value
            )
        );

        return y - LINE_HEIGHT;
    }

    private void drawText(
        PDPageContentStream contentStream,
        PDFont font,
        float fontSize,
        float x,
        float y,
        String text
    ) throws IOException {

        String printableText =
            sanitizePdfText(
                text
            );

        contentStream.beginText();

        contentStream.setFont(
            font,
            fontSize
        );

        contentStream.newLineAtOffset(
            x,
            y
        );

        contentStream.showText(
            printableText
        );

        contentStream.endText();
    }

    private void drawTextRight(
        PDPageContentStream contentStream,
        PDFont font,
        float fontSize,
        float rightX,
        float y,
        String text
    ) throws IOException {

        String printableText =
            sanitizePdfText(
                text
            );

        float width =
            font.getStringWidth(
                printableText
            )
                / 1000.0f
                * fontSize;

        drawText(
            contentStream,
            font,
            fontSize,
            rightX - width,
            y,
            printableText
        );
    }

    private void drawLine(
        PDPageContentStream contentStream,
        float x1,
        float y1,
        float x2,
        float y2
    ) throws IOException {

        contentStream.moveTo(
            x1,
            y1
        );

        contentStream.lineTo(
            x2,
            y2
        );

        contentStream.stroke();
    }

    private String formatMoney(
        BigDecimal value
    ) {

        BigDecimal safeValue =
            value == null
                ? BigDecimal.ZERO
                : value;

        NumberFormat numberFormat =
            NumberFormat.getNumberInstance(
                GERMAN_LOCALE
            );

        numberFormat.setMinimumFractionDigits(
            2
        );

        numberFormat.setMaximumFractionDigits(
            2
        );

        return numberFormat.format(
            safeValue
        )
            + " EUR";
    }

    private String formatQuantity(
        BigDecimal value
    ) {

        if (value == null) {
            return "0";
        }

        return value
            .stripTrailingZeros()
            .toPlainString()
            .replace(
                '.',
                ','
            );
    }

    private String formatVatRate(
        BigDecimal vatRate
    ) {

        if (vatRate == null) {
            return "";
        }

        return vatRate
            .stripTrailingZeros()
            .toPlainString()
            .replace(
                '.',
                ','
            )
            + " %";
    }

    private String formatPercent(
        BigDecimal value
    ) {

        if (value == null) {
            return "0";
        }

        return value
            .stripTrailingZeros()
            .toPlainString()
            .replace(
                '.',
                ','
            );
    }

    private String formatDate(
        LocalDate date
    ) {

        if (date == null) {
            return "";
        }

        return date.format(
            DATE_FORMATTER
        );
    }

    private String formatUnit(
        String unit
    ) {

        if (!notBlank(
            unit
        )) {
            return "";
        }

        String normalized =
            unit.trim()
                .toUpperCase(
                    Locale.ROOT
                );

        return switch (normalized) {

            case "STD", "HUR" ->
                "Std.";

            case "STK", "C62" ->
                "Stk.";

            case "TAG", "DAY" ->
                "Tage";

            default ->
                unit.trim();
        };
    }

    private String formatIban(
        String iban
    ) {

        if (!notBlank(
            iban
        )) {
            return "";
        }

        String normalized =
            iban
                .replaceAll(
                    "[\\s\\u00A0]+",
                    ""
                )
                .toUpperCase(
                    Locale.ROOT
                );

        StringBuilder result =
            new StringBuilder();

        for (
            int index = 0;
            index < normalized.length();
            index++
        ) {

            if (index > 0
                && index % 4 == 0) {

                result.append(
                    ' '
                );
            }

            result.append(
                normalized.charAt(
                    index
                )
            );
        }

        return result.toString();
    }

    private String joinNonBlank(
        String first,
        String second
    ) {

        if (notBlank(
            first
        )
            && notBlank(
            second
        )) {

            return first.trim()
                + " "
                + second.trim();
        }

        if (notBlank(
            first
        )) {
            return first.trim();
        }

        return safe(
            second
        );
    }

    private boolean notBlank(
        String value
    ) {

        return value != null
            && !value.trim()
            .isEmpty();
    }

    private String safe(
        String value
    ) {

        return value == null
            ? ""
            : value.trim();
    }

    private String sanitizePdfText(
        String value
    ) {

        return safe(
            value
        )
            .replace(
                "\r",
                " "
            )
            .replace(
                "\n",
                " "
            )
            .replace(
                '\u00A0',
                ' '
            );
    }

    private void validateInvoice(
        InvoiceEntity invoice
    ) {

        if (invoice == null) {

            throw new IllegalArgumentException(
                "Die Rechnung darf nicht null sein."
            );
        }

        if (!notBlank(
            invoice.getInvoiceNumber()
        )) {

            throw new IllegalArgumentException(
                "Die Rechnungsnummer fehlt."
            );
        }

        if (invoice.getCompany() == null) {

            throw new IllegalArgumentException(
                "Die Firmendaten fehlen."
            );
        }

        if (invoice.getItems() == null
            || invoice.getItems().isEmpty()) {

            throw new IllegalArgumentException(
                "Die Rechnung enthält keine Positionen."
            );
        }
    }

    private record PdfContext(
        PDPage page,
        PDPageContentStream contentStream,
        float y
    ) {

        private PdfContext withY(
            float newY
        ) {

            return new PdfContext(
                page,
                contentStream,
                newY
            );
        }
    }

    private record TaxSummary(
        BigDecimal rate,
        BigDecimal netAmount,
        BigDecimal taxAmount
    ) {
    }

    private record LabelValue(
        String label,
        String value
    ) {
    }
}
