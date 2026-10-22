package com.bcs.rechnung.offer.pdf;

import com.bcs.rechnung.company.CompanyEntity;
import com.bcs.rechnung.offer.OfferEntity;
import com.bcs.rechnung.offer.OfferItemEntity;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Component
public class OfferPdfGeneratorImpl
    implements OfferPdfGenerator {

    private static final PDRectangle PAGE_SIZE =
        PDRectangle.A4;

    private static final float LEFT_MARGIN = 40.0f;
    private static final float RIGHT_MARGIN = 40.0f;
    private static final float TOP_MARGIN = 42.0f;
    private static final float BOTTOM_MARGIN = 52.0f;

    private static final float CONTENT_WIDTH =
        PAGE_SIZE.getWidth()
            - LEFT_MARGIN
            - RIGHT_MARGIN;

    private static final float RIGHT_X =
        PAGE_SIZE.getWidth()
            - RIGHT_MARGIN;

    private static final float NORMAL_FONT_SIZE = 9.0f;
    private static final float SMALL_FONT_SIZE = 7.5f;
    private static final float TITLE_FONT_SIZE = 18.0f;

    private static final float TABLE_HEADER_HEIGHT = 26.0f;
    private static final float TABLE_LINE_HEIGHT = 10.0f;
    private static final float TABLE_ROW_PADDING = 5.0f;

    private static final float POSITION_X =
        LEFT_MARGIN;

    private static final float DESCRIPTION_X =
        67.0f;

    private static final float DESCRIPTION_WIDTH =
        213.0f;

    private static final float QUANTITY_RIGHT_X =
        326.0f;

    private static final float UNIT_X =
        335.0f;

    private static final float UNIT_PRICE_RIGHT_X =
        430.0f;

    private static final float DISCOUNT_RIGHT_X =
        476.0f;

    private static final float TOTAL_RIGHT_X =
        RIGHT_X;

    private static final float TOTAL_BLOCK_WIDTH =
        220.0f;

    private static final float TOTAL_BLOCK_HEIGHT =
        96.0f;

    private static final float OPTIONAL_LEGEND_HEIGHT =
        20.0f;

    /*
     * Dezentes Hellblau für optionale Positionen.
     */
    private static final float OPTIONAL_BACKGROUND_R =
        0.965f;

    private static final float OPTIONAL_BACKGROUND_G =
        0.980f;

    private static final float OPTIONAL_BACKGROUND_B =
        0.995f;

    /*
     * Dezentes Blau für die Kennzeichnung.
     */
    private static final float OPTIONAL_TEXT_R =
        0.18f;

    private static final float OPTIONAL_TEXT_G =
        0.38f;

    private static final float OPTIONAL_TEXT_B =
        0.58f;

    private static final DateTimeFormatter DATE_FORMAT =
        DateTimeFormatter.ofPattern(
            "dd.MM.yyyy",
            Locale.GERMANY
        );

    @Override
    public byte[] generate(
        OfferEntity offer
    ) throws IOException {

        validateOffer(offer);

        try (
            PDDocument document = new PDDocument();
            ByteArrayOutputStream output =
                new ByteArrayOutputStream()
        ) {
            PdfFonts fonts =
                createFonts();

            PageState state =
                openPage(
                    document,
                    fonts
                );

            writeFirstPageHeader(
                state,
                offer
            );

            state =
                writeIntroductionText(
                    document,
                    state,
                    offer,
                    fonts
                );

            state =
                newPageIfNeeded(
                    document,
                    state,
                    offer,
                    fonts,
                    TABLE_HEADER_HEIGHT + 25.0f,
                    false
                );

            writeTableHeader(
                state
            );

            state =
                writeItems(
                    document,
                    state,
                    offer,
                    fonts
                );

            state =
                writeOptionalLegend(
                    document,
                    state,
                    offer,
                    fonts
                );

            state =
                writeTotals(
                    document,
                    state,
                    offer,
                    fonts
                );

            state =
                writeClosingText(
                    document,
                    state,
                    offer,
                    fonts
                );

            state =
                writeNotes(
                    document,
                    state,
                    offer,
                    fonts
                );

            state =
                writePaymentTerms(
                    document,
                    state,
                    offer,
                    fonts
                );

            state.close();

            addPageNumbers(
                document,
                fonts.regular()
            );

            document.save(
                output
            );

            return output.toByteArray();
        }
    }

    // ---------------------------------------------------------
    // Fonts
    // ---------------------------------------------------------

    private PdfFonts createFonts() {

        return new PdfFonts(
            new PDType1Font(
                Standard14Fonts.FontName.HELVETICA
            ),
            new PDType1Font(
                Standard14Fonts.FontName.HELVETICA_BOLD
            )
        );
    }

    // ---------------------------------------------------------
    // Seiten
    // ---------------------------------------------------------

    private PageState openPage(
        PDDocument document,
        PdfFonts fonts
    ) throws IOException {

        PDPage page =
            new PDPage(
                PAGE_SIZE
            );

        document.addPage(
            page
        );

        PDPageContentStream content =
            new PDPageContentStream(
                document,
                page
            );

        return new PageState(
            content,
            fonts,
            PAGE_SIZE.getHeight()
                - TOP_MARGIN
        );
    }

    private PageState newPage(
        PDDocument document,
        PageState currentState,
        OfferEntity offer,
        PdfFonts fonts,
        boolean includeTableHeader
    ) throws IOException {

        currentState.close();

        PageState newState =
            openPage(
                document,
                fonts
            );

        writeContinuationHeader(
            newState,
            offer
        );

        if (includeTableHeader) {
            writeTableHeader(
                newState
            );
        }

        return newState;
    }

    private PageState newPageIfNeeded(
        PDDocument document,
        PageState state,
        OfferEntity offer,
        PdfFonts fonts,
        float requiredHeight,
        boolean includeTableHeader
    ) throws IOException {

        if (
            hasSpace(
                state,
                requiredHeight
            )
        ) {
            return state;
        }

        return newPage(
            document,
            state,
            offer,
            fonts,
            includeTableHeader
        );
    }

    private boolean hasSpace(
        PageState state,
        float requiredHeight
    ) {

        return state.y
            - requiredHeight
            >= BOTTOM_MARGIN;
    }

    // ---------------------------------------------------------
    // Kopf
    // ---------------------------------------------------------

    private void writeFirstPageHeader(
        PageState state,
        OfferEntity offer
    ) throws IOException {

        writeCompanyHeader(
            state,
            offer.getCompany()
        );

        state.y =
            PAGE_SIZE.getHeight()
                - 112.0f;

        drawLine(
            state,
            state.y,
            0.7f,
            0.55f
        );

        state.moveDown(
            22.0f
        );

        float addressBottom =
            writeCustomerAddress(
                state,
                offer
            );

        float informationBottom =
            writeDocumentInformation(
                state,
                offer
            );

        state.y =
            Math.min(
                addressBottom,
                informationBottom
            ) - 26.0f;
    }

    private void writeCompanyHeader(
        PageState state,
        CompanyEntity company
    ) throws IOException {

        writeText(
            state.content,
            state.fonts.bold(),
            14.0f,
            LEFT_MARGIN,
            state.y,
            company == null
                ? ""
                : company.getName()
        );

        writeTextRightAligned(
            state.content,
            state.fonts.bold(),
            TITLE_FONT_SIZE,
            RIGHT_X,
            state.y,
            "ANGEBOT"
        );

        state.moveDown(
            18.0f
        );

        if (company == null) {
            return;
        }

        writeOptionalLine(
            state,
            company.getStreet()
        );

        writeOptionalLine(
            state,
            joinNonBlank(
                company.getZipCode(),
                company.getCity()
            )
        );

        writeOptionalLine(
            state,
            company.getCountry()
        );
    }

    private float writeCustomerAddress(
        PageState state,
        OfferEntity offer
    ) throws IOException {

        writeText(
            state.content,
            state.fonts.regular(),
            SMALL_FONT_SIZE,
            LEFT_MARGIN,
            state.y,
            "Angebotsempfänger"
        );

        state.moveDown(
            16.0f
        );

        writeText(
            state.content,
            state.fonts.bold(),
            11.0f,
            LEFT_MARGIN,
            state.y,
            offer.getCustomerName()
        );

        state.moveDown(
            15.0f
        );

        writeOptionalLine(
            state,
            offer.getCustomerDepartment()
        );

        writeOptionalLine(
            state,
            offer.getCustomerContactPerson()
        );

        writeOptionalLine(
            state,
            offer.getCustomerStreet()
        );

        writeOptionalLine(
            state,
            joinNonBlank(
                offer.getCustomerZipCode(),
                offer.getCustomerCity()
            )
        );

        writeOptionalLine(
            state,
            offer.getCustomerCountry()
        );

        return state.y;
    }

    private float writeDocumentInformation(
        PageState state,
        OfferEntity offer
    ) throws IOException {

        float labelX =
            358.0f;

        float y =
            PAGE_SIZE.getHeight()
                - 150.0f;

        y = writeLabelValue(
            state,
            labelX,
            y,
            "Angebotsnummer:",
            offerNumber(
                offer
            )
        );

        y = writeLabelValue(
            state,
            labelX,
            y,
            "Angebotsdatum:",
            formatDate(
                offer.getOfferDate()
            )
        );

        y = writeLabelValue(
            state,
            labelX,
            y,
            "Gültig bis:",
            formatDate(
                offer.getValidUntil()
            )
        );

        y = writeLabelValue(
            state,
            labelX,
            y,
            "Kundennummer:",
            offer.getCustomerNumber()
        );

        y = writeLabelValue(
            state,
            labelX,
            y,
            "Status:",
            offer.getStatus() == null
                ? ""
                : offer.getStatus()
                .name()
        );

        return writeLabelValue(
            state,
            labelX,
            y,
            "Währung:",
            offer.getCurrencyCode()
        );
    }

    private void writeContinuationHeader(
        PageState state,
        OfferEntity offer
    ) throws IOException {

        writeText(
            state.content,
            state.fonts.bold(),
            12.0f,
            LEFT_MARGIN,
            state.y,
            "Angebot "
                + offerNumber(
                offer
            )
        );

        writeTextRightAligned(
            state.content,
            state.fonts.regular(),
            NORMAL_FONT_SIZE,
            RIGHT_X,
            state.y,
            "Fortsetzung"
        );

        state.moveDown(
            18.0f
        );

        drawLine(
            state,
            state.y,
            0.7f,
            0.55f
        );

        state.moveDown(
            18.0f
        );
    }

    // ---------------------------------------------------------
    // Einleitung
    // ---------------------------------------------------------

    private PageState writeIntroductionText(
        PDDocument document,
        PageState state,
        OfferEntity offer,
        PdfFonts fonts
    ) throws IOException {

        String introduction =
            normalize(
                offer.getIntroductionText()
            );

        if (introduction == null) {
            return state;
        }

        List<String> lines =
            wrapText(
                introduction,
                fonts.regular(),
                NORMAL_FONT_SIZE,
                CONTENT_WIDTH
            );

        for (String line : lines) {

            if (
                !hasSpace(
                    state,
                    14.0f
                )
            ) {
                state =
                    newPage(
                        document,
                        state,
                        offer,
                        fonts,
                        false
                    );
            }

            writeText(
                state.content,
                state.fonts.regular(),
                NORMAL_FONT_SIZE,
                LEFT_MARGIN,
                state.y,
                line
            );

            state.moveDown(
                13.0f
            );
        }

        state.moveDown(
            16.0f
        );

        return state;
    }

    // ---------------------------------------------------------
    // Tabellenkopf
    // ---------------------------------------------------------

    private void writeTableHeader(
        PageState state
    ) throws IOException {

        float topY =
            state.y;

        state.content.setNonStrokingColor(
            0.90f,
            0.92f,
            0.95f
        );

        state.content.addRect(
            LEFT_MARGIN,
            topY - TABLE_HEADER_HEIGHT,
            CONTENT_WIDTH,
            TABLE_HEADER_HEIGHT
        );

        state.content.fill();

        resetTextColor(
            state
        );

        float y =
            topY - 17.0f;

        writeText(
            state.content,
            state.fonts.bold(),
            SMALL_FONT_SIZE,
            POSITION_X,
            y,
            "Pos."
        );

        writeText(
            state.content,
            state.fonts.bold(),
            SMALL_FONT_SIZE,
            DESCRIPTION_X,
            y,
            "Beschreibung"
        );

        writeTextRightAligned(
            state.content,
            state.fonts.bold(),
            SMALL_FONT_SIZE,
            QUANTITY_RIGHT_X,
            y,
            "Menge"
        );

        writeText(
            state.content,
            state.fonts.bold(),
            SMALL_FONT_SIZE,
            UNIT_X,
            y,
            "Einh."
        );

        writeTextRightAligned(
            state.content,
            state.fonts.bold(),
            SMALL_FONT_SIZE,
            UNIT_PRICE_RIGHT_X,
            y,
            "Einzelpreis"
        );

        writeTextRightAligned(
            state.content,
            state.fonts.bold(),
            SMALL_FONT_SIZE,
            DISCOUNT_RIGHT_X,
            y,
            "Rabatt"
        );

        writeTextRightAligned(
            state.content,
            state.fonts.bold(),
            SMALL_FONT_SIZE,
            TOTAL_RIGHT_X,
            y,
            "Gesamt netto"
        );

        state.y =
            topY
                - TABLE_HEADER_HEIGHT;
    }

    // ---------------------------------------------------------
    // Positionen
    // ---------------------------------------------------------

    private PageState writeItems(
        PDDocument document,
        PageState state,
        OfferEntity offer,
        PdfFonts fonts
    ) throws IOException {

        List<OfferItemEntity> items =
            offer.getItems() == null
                ? Collections.emptyList()
                : offer.getItems();

        if (items.isEmpty()) {

            state =
                newPageIfNeeded(
                    document,
                    state,
                    offer,
                    fonts,
                    45.0f,
                    true
                );

            state.moveDown(
                18.0f
            );

            writeText(
                state.content,
                state.fonts.regular(),
                NORMAL_FONT_SIZE,
                DESCRIPTION_X,
                state.y,
                "Keine Positionen vorhanden."
            );

            state.moveDown(
                28.0f
            );

            return state;
        }

        for (
            OfferItemEntity item : items
        ) {
            state =
                writeItem(
                    document,
                    state,
                    offer,
                    item,
                    fonts
                );
        }

        return state;
    }

    private PageState writeItem(
        PDDocument document,
        PageState state,
        OfferEntity offer,
        OfferItemEntity item,
        PdfFonts fonts
    ) throws IOException {

        List<String> lines =
            wrapText(
                buildDescription(
                    item
                ),
                fonts.regular(),
                SMALL_FONT_SIZE,
                DESCRIPTION_WIDTH
            );

        int index = 0;

        boolean firstPart =
            true;

        while (
            index < lines.size()
        ) {
            int availableLines =
                calculateAvailableLines(
                    state
                );

            if (availableLines <= 0) {

                state =
                    newPage(
                        document,
                        state,
                        offer,
                        fonts,
                        true
                    );

                availableLines =
                    calculateAvailableLines(
                        state
                    );
            }

            int lineCount =
                Math.min(
                    lines.size()
                        - index,
                    availableLines
                );

            List<String> visibleLines =
                new ArrayList<>(
                    lines.subList(
                        index,
                        index + lineCount
                    )
                );

            float rowHeight =
                calculateRowHeight(
                    lineCount
                );

            writeItemRow(
                state,
                offer,
                item,
                visibleLines,
                rowHeight,
                firstPart
            );

            index +=
                lineCount;

            firstPart =
                false;

            if (
                index < lines.size()
            ) {
                state =
                    newPage(
                        document,
                        state,
                        offer,
                        fonts,
                        true
                    );
            }
        }

        return state;
    }

    private int calculateAvailableLines(
        PageState state
    ) {

        float availableHeight =
            state.y
                - BOTTOM_MARGIN
                - TABLE_ROW_PADDING
                * 2.0f;

        if (
            availableHeight
                < TABLE_LINE_HEIGHT
        ) {
            return 0;
        }

        return Math.max(
            1,
            (int) Math.floor(
                availableHeight
                    / TABLE_LINE_HEIGHT
            )
        );
    }

    private float calculateRowHeight(
        int lineCount
    ) {

        return Math.max(
            TABLE_LINE_HEIGHT
                + TABLE_ROW_PADDING
                * 2.0f,

            lineCount
                * TABLE_LINE_HEIGHT
                + TABLE_ROW_PADDING
                * 2.0f
        );
    }

    private void writeItemRow(
        PageState state,
        OfferEntity offer,
        OfferItemEntity item,
        List<String> lines,
        float rowHeight,
        boolean firstPart
    ) throws IOException {

        float rowTop =
            state.y;

        /*
         * Optionale Position dezent hellblau hinterlegen.
         */
        if (item.isOptional()) {

            state.content.setNonStrokingColor(
                OPTIONAL_BACKGROUND_R,
                OPTIONAL_BACKGROUND_G,
                OPTIONAL_BACKGROUND_B
            );

            state.content.addRect(
                LEFT_MARGIN,
                rowTop - rowHeight,
                CONTENT_WIDTH,
                rowHeight
            );

            state.content.fill();

            resetTextColor(
                state
            );
        }

        float y =
            rowTop
                - TABLE_ROW_PADDING
                - SMALL_FONT_SIZE;

        if (firstPart) {

            if (item.isOptional()) {
                setOptionalTextColor(
                    state
                );
            }

            writeText(
                state.content,
                item.isOptional()
                    ? state.fonts.bold()
                    : state.fonts.regular(),
                SMALL_FONT_SIZE,
                POSITION_X,
                y,
                item.getPositionNumber() == null
                    ? ""
                    : item
                    .getPositionNumber()
                    .toString()
            );

            resetTextColor(
                state
            );
        }

        float textY =
            y;

        for (
            String line : lines
        ) {
            boolean optionalHeading =
                item.isOptional()
                    && "OPTIONALE POSITION".equals(
                    line
                );

            if (optionalHeading) {
                setOptionalTextColor(
                    state
                );
            }

            writeText(
                state.content,
                optionalHeading
                    ? state.fonts.bold()
                    : state.fonts.regular(),
                SMALL_FONT_SIZE,
                DESCRIPTION_X,
                textY,
                line
            );

            resetTextColor(
                state
            );

            textY -=
                TABLE_LINE_HEIGHT;
        }

        if (firstPart) {

            writeTextRightAligned(
                state.content,
                state.fonts.regular(),
                SMALL_FONT_SIZE,
                QUANTITY_RIGHT_X,
                y,
                formatQuantity(
                    item.getQuantity()
                )
            );

            writeText(
                state.content,
                state.fonts.regular(),
                SMALL_FONT_SIZE,
                UNIT_X,
                y,
                item.getUnit()
            );

            writeTextRightAligned(
                state.content,
                state.fonts.regular(),
                SMALL_FONT_SIZE,
                UNIT_PRICE_RIGHT_X,
                y,
                formatMoney(
                    item.getUnitPriceNet(),
                    offer.getCurrencyCode()
                )
            );

            writeTextRightAligned(
                state.content,
                state.fonts.regular(),
                SMALL_FONT_SIZE,
                DISCOUNT_RIGHT_X,
                y,
                formatPercent(
                    item.getDiscountPercent()
                )
            );

            String lineTotal =
                formatMoney(
                    item.getLineNet(),
                    offer.getCurrencyCode()
                );

            if (item.isOptional()) {

                lineTotal =
                    lineTotal + " *";

                setOptionalTextColor(
                    state
                );
            }

            writeTextRightAligned(
                state.content,
                item.isOptional()
                    ? state.fonts.bold()
                    : state.fonts.regular(),
                SMALL_FONT_SIZE,
                TOTAL_RIGHT_X,
                y,
                lineTotal
            );

            resetTextColor(
                state
            );
        }

        state.y =
            rowTop - rowHeight;

        drawLine(
            state,
            state.y,
            0.3f,
            0.82f
        );
    }

    private String buildDescription(
        OfferItemEntity item
    ) {

        List<String> heading =
            new ArrayList<>();

        addText(
            heading,
            item.getProductNumber()
        );

        addText(
            heading,
            item.getProductName()
        );

        List<String> lines =
            new ArrayList<>();

        if (item.isOptional()) {

            lines.add(
                "OPTIONALE POSITION"
            );
        }

        if (!heading.isEmpty()) {

            lines.add(
                String.join(
                    " - ",
                    heading
                )
            );
        }

        addText(
            lines,
            item.getDescription()
        );

        addText(
            lines,
            item.getNotes()
        );

        return String.join(
            "\n",
            lines
        );
    }

    // ---------------------------------------------------------
    // Legende
    // ---------------------------------------------------------

    private PageState writeOptionalLegend(
        PDDocument document,
        PageState state,
        OfferEntity offer,
        PdfFonts fonts
    ) throws IOException {

        if (
            !hasOptionalItems(
                offer
            )
        ) {
            return state;
        }

        state =
            newPageIfNeeded(
                document,
                state,
                offer,
                fonts,
                OPTIONAL_LEGEND_HEIGHT
                    + 20.0f,
                false
            );

        state.moveDown(
            8.0f
        );

        float legendTop =
            state.y;

        state.content.setNonStrokingColor(
            OPTIONAL_BACKGROUND_R,
            OPTIONAL_BACKGROUND_G,
            OPTIONAL_BACKGROUND_B
        );

        state.content.addRect(
            LEFT_MARGIN,
            legendTop
                - OPTIONAL_LEGEND_HEIGHT,
            CONTENT_WIDTH,
            OPTIONAL_LEGEND_HEIGHT
        );

        state.content.fill();

        setOptionalTextColor(
            state
        );

        writeText(
            state.content,
            state.fonts.bold(),
            SMALL_FONT_SIZE,
            LEFT_MARGIN + 8.0f,
            legendTop - 13.0f,
            "Legende:"
        );

        writeText(
            state.content,
            state.fonts.regular(),
            SMALL_FONT_SIZE,
            LEFT_MARGIN + 53.0f,
            legendTop - 13.0f,
            "Hellblau markierte Positionen sind optional "
                + "und nicht im Gesamtbetrag enthalten."
        );

        resetTextColor(
            state
        );

        state.y =
            legendTop
                - OPTIONAL_LEGEND_HEIGHT
                - 8.0f;

        return state;
    }

    private boolean hasOptionalItems(
        OfferEntity offer
    ) {

        return offer.getItems() != null
            && offer
            .getItems()
            .stream()
            .anyMatch(
                OfferItemEntity::isOptional
            );
    }

    // ---------------------------------------------------------
    // Summen
    // ---------------------------------------------------------

    private PageState writeTotals(
        PDDocument document,
        PageState state,
        OfferEntity offer,
        PdfFonts fonts
    ) throws IOException {

        state =
            newPageIfNeeded(
                document,
                state,
                offer,
                fonts,
                TOTAL_BLOCK_HEIGHT,
                false
            );

        state.moveDown(
            18.0f
        );

        float blockLeft =
            RIGHT_X
                - TOTAL_BLOCK_WIDTH;

        float labelRight =
            RIGHT_X
                - 88.0f;

        float y =
            state.y;

        y = writeTotalLine(
            state,
            labelRight,
            RIGHT_X,
            y,
            "Nettosumme:",
            formatMoney(
                offer.getSubtotalNet(),
                offer.getCurrencyCode()
            ),
            false
        );

        y = writeTotalLine(
            state,
            labelRight,
            RIGHT_X,
            y,
            "Umsatzsteuer:",
            formatMoney(
                offer.getTaxAmount(),
                offer.getCurrencyCode()
            ),
            false
        );

        drawPartialLine(
            state,
            blockLeft,
            RIGHT_X,
            y + 4.0f,
            0.6f,
            0.55f
        );

        y -=
            12.0f;

        y = writeTotalLine(
            state,
            labelRight,
            RIGHT_X,
            y,
            "Gesamtbetrag:",
            formatMoney(
                offer.getTotalGross(),
                offer.getCurrencyCode()
            ),
            true
        );

        state.y =
            y - 10.0f;

        return state;
    }

    private float writeTotalLine(
        PageState state,
        float labelRightX,
        float valueRightX,
        float y,
        String label,
        String value,
        boolean bold
    ) throws IOException {

        PDFont font =
            bold
                ? state.fonts.bold()
                : state.fonts.regular();

        float fontSize =
            bold
                ? 10.0f
                : NORMAL_FONT_SIZE;

        writeTextRightAligned(
            state.content,
            font,
            fontSize,
            labelRightX,
            y,
            label
        );

        writeTextRightAligned(
            state.content,
            font,
            fontSize,
            valueRightX,
            y,
            value
        );

        return y - 16.0f;
    }

    // ---------------------------------------------------------
    // Schlusstext
    // ---------------------------------------------------------

    private PageState writeClosingText(
        PDDocument document,
        PageState state,
        OfferEntity offer,
        PdfFonts fonts
    ) throws IOException {

        String value =
            normalize(
                offer.getClosingText()
            );

        if (value == null) {
            return state;
        }

        return writeTextSection(
            document,
            state,
            offer,
            fonts,
            "Schlusstext",
            value
        );
    }

    // ---------------------------------------------------------
    // Notizen
    // ---------------------------------------------------------

    private PageState writeNotes(
        PDDocument document,
        PageState state,
        OfferEntity offer,
        PdfFonts fonts
    ) throws IOException {

        String value =
            normalize(
                offer.getNotes()
            );

        if (value == null) {
            return state;
        }

        return writeTextSection(
            document,
            state,
            offer,
            fonts,
            "Anmerkungen",
            value
        );
    }

    private PageState writeTextSection(
        PDDocument document,
        PageState state,
        OfferEntity offer,
        PdfFonts fonts,
        String heading,
        String value
    ) throws IOException {

        List<String> lines =
            wrapText(
                value,
                fonts.regular(),
                NORMAL_FONT_SIZE,
                CONTENT_WIDTH
            );

        state =
            newPageIfNeeded(
                document,
                state,
                offer,
                fonts,
                38.0f,
                false
            );

        state.moveDown(
            12.0f
        );

        writeText(
            state.content,
            state.fonts.bold(),
            10.0f,
            LEFT_MARGIN,
            state.y,
            heading
        );

        state.moveDown(
            17.0f
        );

        for (
            String line : lines
        ) {

            if (
                !hasSpace(
                    state,
                    14.0f
                )
            ) {

                state =
                    newPage(
                        document,
                        state,
                        offer,
                        fonts,
                        false
                    );

                writeText(
                    state.content,
                    state.fonts.bold(),
                    10.0f,
                    LEFT_MARGIN,
                    state.y,
                    heading
                        + " - Fortsetzung"
                );

                state.moveDown(
                    17.0f
                );
            }

            writeText(
                state.content,
                state.fonts.regular(),
                NORMAL_FONT_SIZE,
                LEFT_MARGIN,
                state.y,
                line
            );

            state.moveDown(
                13.0f
            );
        }

        state.moveDown(
            10.0f
        );

        return state;
    }

    // ---------------------------------------------------------
    // Zahlungsbedingungen
    // ---------------------------------------------------------

    private PageState writePaymentTerms(
        PDDocument document,
        PageState state,
        OfferEntity offer,
        PdfFonts fonts
    ) throws IOException {

        if (
            offer.getPaymentTermDays()
                == null
        ) {
            return state;
        }

        state =
            newPageIfNeeded(
                document,
                state,
                offer,
                fonts,
                48.0f,
                false
            );

        state.moveDown(
            10.0f
        );

        writeText(
            state.content,
            state.fonts.bold(),
            10.0f,
            LEFT_MARGIN,
            state.y,
            "Zahlungsbedingungen"
        );

        state.moveDown(
            17.0f
        );

        String text =
            offer.getPaymentTermDays() == 0
                ? "Zahlbar sofort ohne Abzug."
                : "Zahlbar innerhalb von "
                + offer.getPaymentTermDays()
                + " Tagen ohne Abzug.";

        writeText(
            state.content,
            state.fonts.regular(),
            NORMAL_FONT_SIZE,
            LEFT_MARGIN,
            state.y,
            text
        );

        state.moveDown(
            22.0f
        );

        return state;
    }

    // ---------------------------------------------------------
    // Seitenzahlen
    // ---------------------------------------------------------

    private void addPageNumbers(
        PDDocument document,
        PDFont font
    ) throws IOException {

        int pageCount =
            document.getNumberOfPages();

        for (
            int index = 0;
            index < pageCount;
            index++
        ) {

            PDPage page =
                document.getPage(
                    index
                );

            try (
                PDPageContentStream content =
                    new PDPageContentStream(
                        document,
                        page,
                        PDPageContentStream
                            .AppendMode
                            .APPEND,
                        true,
                        true
                    )
            ) {

                writeTextRightAligned(
                    content,
                    font,
                    SMALL_FONT_SIZE,
                    page
                        .getMediaBox()
                        .getWidth()
                        - RIGHT_MARGIN,
                    24.0f,
                    "Seite "
                        + (index + 1)
                        + " von "
                        + pageCount
                );
            }
        }
    }

    // ---------------------------------------------------------
    // PDF-Hilfsmethoden
    // ---------------------------------------------------------

    private float writeLabelValue(
        PageState state,
        float labelX,
        float y,
        String label,
        String value
    ) throws IOException {

        writeText(
            state.content,
            state.fonts.bold(),
            SMALL_FONT_SIZE,
            labelX,
            y,
            label
        );

        writeTextRightAligned(
            state.content,
            state.fonts.regular(),
            SMALL_FONT_SIZE,
            RIGHT_X,
            y,
            value
        );

        return y - 14.0f;
    }

    private void writeOptionalLine(
        PageState state,
        String value
    ) throws IOException {

        String normalized =
            normalize(
                value
            );

        if (normalized == null) {
            return;
        }

        writeText(
            state.content,
            state.fonts.regular(),
            NORMAL_FONT_SIZE,
            LEFT_MARGIN,
            state.y,
            normalized
        );

        state.moveDown(
            13.0f
        );
    }

    private void writeText(
        PDPageContentStream content,
        PDFont font,
        float fontSize,
        float x,
        float y,
        String value
    ) throws IOException {

        String text =
            sanitize(
                value
            );

        if (text.isBlank()) {
            return;
        }

        content.beginText();

        content.setFont(
            font,
            fontSize
        );

        content.newLineAtOffset(
            x,
            y
        );

        content.showText(
            text
        );

        content.endText();
    }

    private void writeTextRightAligned(
        PDPageContentStream content,
        PDFont font,
        float fontSize,
        float rightX,
        float y,
        String value
    ) throws IOException {

        String text =
            sanitize(
                value
            );

        if (text.isBlank()) {
            return;
        }

        float width =
            textWidth(
                font,
                fontSize,
                text
            );

        writeText(
            content,
            font,
            fontSize,
            rightX - width,
            y,
            text
        );
    }

    private float textWidth(
        PDFont font,
        float fontSize,
        String value
    ) throws IOException {

        return font.getStringWidth(
            sanitize(
                value
            )
        ) / 1000.0f
            * fontSize;
    }

    private void drawLine(
        PageState state,
        float y,
        float lineWidth,
        float gray
    ) throws IOException {

        drawPartialLine(
            state,
            LEFT_MARGIN,
            LEFT_MARGIN
                + CONTENT_WIDTH,
            y,
            lineWidth,
            gray
        );
    }

    private void drawPartialLine(
        PageState state,
        float startX,
        float endX,
        float y,
        float lineWidth,
        float gray
    ) throws IOException {

        state.content.setStrokingColor(
            gray,
            gray,
            gray
        );

        state.content.setLineWidth(
            lineWidth
        );

        state.content.moveTo(
            startX,
            y
        );

        state.content.lineTo(
            endX,
            y
        );

        state.content.stroke();
    }

    /*
     * KORRIGIERT:
     * setNonStrokingColor kann IOException auslösen.
     */
    private void setOptionalTextColor(
        PageState state
    ) throws IOException {

        state.content.setNonStrokingColor(
            OPTIONAL_TEXT_R,
            OPTIONAL_TEXT_G,
            OPTIONAL_TEXT_B
        );
    }

    /*
     * KORRIGIERT:
     * Auch hier IOException deklarieren.
     */
    private void resetTextColor(
        PageState state
    ) throws IOException {

        state.content.setNonStrokingColor(
            0.0f,
            0.0f,
            0.0f
        );
    }

    // ---------------------------------------------------------
    // Zeilenumbruch
    // ---------------------------------------------------------

    private List<String> wrapText(
        String value,
        PDFont font,
        float fontSize,
        float maxWidth
    ) throws IOException {

        String normalized =
            normalize(
                value
            );

        if (normalized == null) {

            return Collections
                .singletonList("");
        }

        List<String> result =
            new ArrayList<>();

        for (
            String paragraph
            : normalized.split(
            "\\R"
        )
        ) {

            wrapParagraph(
                paragraph,
                font,
                fontSize,
                maxWidth,
                result
            );
        }

        return result.isEmpty()
            ? Collections.singletonList("")
            : result;
    }

    private void wrapParagraph(
        String paragraph,
        PDFont font,
        float fontSize,
        float maxWidth,
        List<String> result
    ) throws IOException {

        String trimmed =
            paragraph.trim();

        if (trimmed.isEmpty()) {

            result.add("");

            return;
        }

        StringBuilder line =
            new StringBuilder();

        for (
            String word
            : trimmed.split(
            "\\s+"
        )
        ) {

            String candidate =
                line.isEmpty()
                    ? word
                    : line
                    + " "
                    + word;

            if (
                textWidth(
                    font,
                    fontSize,
                    candidate
                ) <= maxWidth
            ) {

                line.setLength(
                    0
                );

                line.append(
                    candidate
                );

                continue;
            }

            if (!line.isEmpty()) {

                result.add(
                    line.toString()
                );

                line.setLength(
                    0
                );
            }

            if (
                textWidth(
                    font,
                    fontSize,
                    word
                ) <= maxWidth
            ) {

                line.append(
                    word
                );

            } else {

                splitLongWord(
                    word,
                    font,
                    fontSize,
                    maxWidth,
                    result,
                    line
                );
            }
        }

        if (!line.isEmpty()) {

            result.add(
                line.toString()
            );
        }
    }

    private void splitLongWord(
        String word,
        PDFont font,
        float fontSize,
        float maxWidth,
        List<String> result,
        StringBuilder remainder
    ) throws IOException {

        StringBuilder part =
            new StringBuilder();

        for (
            char character
            : word.toCharArray()
        ) {

            String candidate =
                part.toString()
                    + character;

            if (
                part.isEmpty()
                    || textWidth(
                    font,
                    fontSize,
                    candidate
                ) <= maxWidth
            ) {

                part.append(
                    character
                );

                continue;
            }

            result.add(
                part.toString()
            );

            part.setLength(
                0
            );

            part.append(
                character
            );
        }

        remainder.append(
            part
        );
    }

    // ---------------------------------------------------------
    // Formatierung
    // ---------------------------------------------------------

    private String offerNumber(
        OfferEntity offer
    ) {

        String number =
            normalize(
                offer.getOfferNumber()
            );

        return number == null
            ? "ENTWURF"
            : number;
    }

    private String formatDate(
        LocalDate date
    ) {

        return date == null
            ? ""
            : DATE_FORMAT.format(
            date
        );
    }

    private String formatQuantity(
        BigDecimal value
    ) {

        if (value == null) {
            return "";
        }

        return value
            .stripTrailingZeros()
            .toPlainString()
            .replace(
                '.',
                ','
            );
    }

    private String formatPercent(
        BigDecimal value
    ) {

        if (
            value == null
                || value.compareTo(
                BigDecimal.ZERO
            ) == 0
        ) {
            return "";
        }

        return value
            .stripTrailingZeros()
            .toPlainString()
            .replace(
                '.',
                ','
            )
            + " %";
    }

    private String formatMoney(
        BigDecimal value,
        String currencyCode
    ) {

        BigDecimal amount =
            value == null
                ? BigDecimal.ZERO
                : value;

        String currency =
            normalize(
                currencyCode
            );

        return amount
            .setScale(
                2,
                RoundingMode.HALF_UP
            )
            .toPlainString()
            .replace(
                '.',
                ','
            )
            + (
            currency == null
                ? ""
                : " " + currency
        );
    }

    private String joinNonBlank(
        String first,
        String second
    ) {

        String firstValue =
            normalize(
                first
            );

        String secondValue =
            normalize(
                second
            );

        if (firstValue == null) {

            return safe(
                secondValue
            );
        }

        if (secondValue == null) {

            return firstValue;
        }

        return firstValue
            + " "
            + secondValue;
    }

    private void addText(
        List<String> target,
        String value
    ) {

        String normalized =
            normalize(
                value
            );

        if (normalized != null) {

            target.add(
                normalized
            );
        }
    }

    private String normalize(
        String value
    ) {

        if (value == null) {
            return null;
        }

        String normalized =
            value.trim();

        return normalized.isEmpty()
            ? null
            : normalized;
    }

    private String safe(
        String value
    ) {

        String normalized =
            normalize(
                value
            );

        return normalized == null
            ? ""
            : normalized;
    }

    private String sanitize(
        String value
    ) {

        return safe(
            value
        )
            .replace(
                "\u00A0",
                " "
            )
            .replace(
                "€",
                "EUR"
            )
            .replace(
                "–",
                "-"
            )
            .replace(
                "—",
                "-"
            )
            .replace(
                "„",
                "\""
            )
            .replace(
                "“",
                "\""
            )
            .replace(
                "”",
                "\""
            )
            .replace(
                "’",
                "'"
            )
            .replace(
                "…",
                "..."
            );
    }

    // ---------------------------------------------------------
    // Validierung
    // ---------------------------------------------------------

    private void validateOffer(
        OfferEntity offer
    ) {

        if (offer == null) {

            throw new IllegalArgumentException(
                "Das Angebot darf nicht null sein."
            );
        }

        if (
            offer.getOfferDate()
                == null
        ) {

            throw new IllegalArgumentException(
                "Das Angebotsdatum darf nicht null sein."
            );
        }

        if (
            normalize(
                offer.getCustomerName()
            ) == null
        ) {

            throw new IllegalArgumentException(
                "Der Angebotsempfänger darf nicht leer sein."
            );
        }

        if (
            offer.getSubtotalNet() == null
                || offer.getTaxAmount() == null
                || offer.getTotalGross() == null
        ) {

            throw new IllegalArgumentException(
                "Die Angebotssummen dürfen nicht null sein."
            );
        }
    }

    // ---------------------------------------------------------
    // Interne Typen
    // ---------------------------------------------------------

    private record PdfFonts(
        PDFont regular,
        PDFont bold
    ) {
    }

    private static final class PageState {

        private final PDPageContentStream content;

        private final PdfFonts fonts;

        private float y;

        private PageState(
            PDPageContentStream content,
            PdfFonts fonts,
            float y
        ) {

            this.content =
                content;

            this.fonts =
                fonts;

            this.y =
                y;
        }

        private void moveDown(
            float distance
        ) {

            y -=
                distance;
        }

        private void close()
            throws IOException {

            content.close();
        }
    }
}
