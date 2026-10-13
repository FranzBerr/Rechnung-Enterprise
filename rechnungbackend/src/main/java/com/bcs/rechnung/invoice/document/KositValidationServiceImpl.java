package com.bcs.rechnung.invoice.document;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class KositValidationServiceImpl
    implements KositValidationService {

    private static final String REPORT_NAMESPACE =
        "http://www.xoev.de/de/validator/varl/1";

    private static final String VALIDATOR_LOG_FILE =
        "validator-console.log";

    private final InvoiceDocumentProperties properties;

    @Override
    public KositValidationResult validate(
        Path xmlFile,
        Path reportDirectory
    ) {
        Path normalizedXmlFile =
            requireFile(
                xmlFile,
                "XRechnung-XML"
            );

        Path validatorJar =
            requireFile(
                Path.of(
                    properties.getValidatorJar()
                ),
                "KoSIT-Validator"
            );

        Path validatorScenario =
            requireFile(
                Path.of(
                    properties.getValidatorScenario()
                ),
                "KoSIT-Szenariokonfiguration"
            );

        Path validatorRepository =
            requireDirectory(
                Path.of(
                    properties.getValidatorRepository()
                ),
                "KoSIT-Konfigurationsverzeichnis"
            );

        Path normalizedReportDirectory =
            reportDirectory
                .toAbsolutePath()
                .normalize();

        try {
            Files.createDirectories(
                normalizedReportDirectory
            );

            Path validatorLogFile =
                normalizedReportDirectory.resolve(
                    VALIDATOR_LOG_FILE
                );

            ProcessBuilder processBuilder =
                new ProcessBuilder(
                    javaExecutable(),
                    "-jar",
                    validatorJar.toString(),
                    "-s",
                    validatorScenario.toString(),
                    "-r",
                    validatorRepository.toString(),
                    "-o",
                    normalizedReportDirectory.toString(),
                    "-h",
                    normalizedXmlFile.toString()
                );

            processBuilder.redirectErrorStream(
                true
            );

            processBuilder.redirectOutput(
                validatorLogFile.toFile()
            );

            Process process =
                processBuilder.start();

            boolean completed =
                process.waitFor(
                    properties.getValidatorTimeoutSeconds(),
                    TimeUnit.SECONDS
                );

            if (!completed) {
                terminateProcess(
                    process
                );

                String consoleOutput =
                    readTextFile(
                        validatorLogFile
                    );

                throw new IllegalStateException(
                    "Die KoSIT-Validierung wurde nach "
                        + properties.getValidatorTimeoutSeconds()
                        + " Sekunden abgebrochen."
                        + System.lineSeparator()
                        + "Validator-Protokoll: "
                        + validatorLogFile
                        + System.lineSeparator()
                        + consoleOutput
                );
            }

            int exitCode =
                process.exitValue();

            String consoleOutput =
                readTextFile(
                    validatorLogFile
                );

            Path reportFile =
                findXmlReport(
                    normalizedReportDirectory,
                    validatorLogFile
                );

            if (reportFile == null) {
                throw new IllegalStateException(
                    "Der KoSIT-Validator hat keinen "
                        + "XML-Prüfbericht erzeugt."
                        + System.lineSeparator()
                        + "Exit-Code: "
                        + exitCode
                        + System.lineSeparator()
                        + "Validator-Protokoll: "
                        + validatorLogFile
                        + System.lineSeparator()
                        + consoleOutput
                );
            }

            ParsedValidationReport parsedReport =
                parseReport(
                    reportFile
                );

            return new KositValidationResult(
                parsedReport.valid(),
                reportFile,
                parsedReport.errors(),
                parsedReport.warnings(),
                consoleOutput
            );

        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                "Die KoSIT-Validierung wurde unterbrochen.",
                exception
            );

        } catch (IOException exception) {
            throw new IllegalStateException(
                "Der KoSIT-Validator konnte nicht "
                    + "ausgeführt werden.",
                exception
            );
        }
    }

    private ParsedValidationReport parseReport(
        Path reportFile
    ) {
        try {
            DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();

            factory.setNamespaceAware(
                true
            );

            factory.setFeature(
                XMLConstants.FEATURE_SECURE_PROCESSING,
                true
            );

            factory.setFeature(
                "http://apache.org/xml/features/"
                    + "disallow-doctype-decl",
                true
            );

            factory.setFeature(
                "http://xml.org/sax/features/"
                    + "external-general-entities",
                false
            );

            factory.setFeature(
                "http://xml.org/sax/features/"
                    + "external-parameter-entities",
                false
            );

            factory.setXIncludeAware(
                false
            );

            factory.setExpandEntityReferences(
                false
            );

            setFactoryAttribute(
                factory,
                XMLConstants.ACCESS_EXTERNAL_DTD,
                ""
            );

            setFactoryAttribute(
                factory,
                XMLConstants.ACCESS_EXTERNAL_SCHEMA,
                ""
            );

            DocumentBuilder builder =
                factory.newDocumentBuilder();

            Document document =
                builder.parse(
                    reportFile.toFile()
                );

            Element root =
                document.getDocumentElement();

            boolean valid =
                Boolean.parseBoolean(
                    root.getAttribute(
                        "valid"
                    )
                );

            List<String> errors =
                extractMessages(
                    document,
                    "error"
                );

            List<String> warnings =
                extractMessages(
                    document,
                    "warning"
                );

            return new ParsedValidationReport(
                valid,
                errors,
                warnings
            );

        } catch (
            ParserConfigurationException
            | SAXException
            | IOException exception
        ) {
            throw new IllegalStateException(
                "Der KoSIT-Prüfbericht konnte nicht "
                    + "gelesen werden: "
                    + reportFile,
                exception
            );
        }
    }

    private List<String> extractMessages(
        Document document,
        String requestedLevel
    ) {
        NodeList messages =
            document.getElementsByTagNameNS(
                REPORT_NAMESPACE,
                "message"
            );

        List<String> result =
            new ArrayList<>();

        for (
            int index = 0;
            index < messages.getLength();
            index++
        ) {
            Element message =
                (Element) messages.item(
                    index
                );

            String level =
                message.getAttribute(
                    "level"
                );

            if (!requestedLevel.equalsIgnoreCase(
                level
            )) {
                continue;
            }

            String code =
                message.getAttribute(
                    "code"
                );

            String text =
                message.getTextContent() == null
                    ? ""
                    : message
                    .getTextContent()
                    .trim();

            if (code == null || code.isBlank()) {
                result.add(
                    text
                );
            } else {
                result.add(
                    code + ": " + text
                );
            }
        }

        return List.copyOf(
            result
        );
    }

    private Path findXmlReport(
        Path reportDirectory,
        Path validatorLogFile
    ) throws IOException {
        try (
            Stream<Path> paths =
                Files.walk(
                    reportDirectory
                )
        ) {
            return paths
                .filter(
                    Files::isRegularFile
                )
                .filter(
                    path -> !path.equals(
                        validatorLogFile
                    )
                )
                .filter(
                    this::isXmlFile
                )
                .max(
                    Comparator.comparingLong(
                        this::lastModified
                    )
                )
                .orElse(
                    null
                );
        }
    }

    private boolean isXmlFile(
        Path path
    ) {
        return path
            .getFileName()
            .toString()
            .toLowerCase(
                Locale.ROOT
            )
            .endsWith(
                ".xml"
            );
    }

    private long lastModified(
        Path path
    ) {
        try {
            return Files
                .getLastModifiedTime(
                    path
                )
                .toMillis();

        } catch (IOException exception) {
            return Long.MIN_VALUE;
        }
    }

    private String readTextFile(
        Path file
    ) {
        if (file == null
            || !Files.isRegularFile(file)) {

            return "";
        }

        try {
            return Files.readString(
                file,
                StandardCharsets.UTF_8
            );

        } catch (IOException exception) {
            return "Die Validator-Ausgabe konnte nicht "
                + "gelesen werden: "
                + exception.getMessage();
        }
    }

    private void terminateProcess(
        Process process
    ) throws InterruptedException {
        if (process == null
            || !process.isAlive()) {

            return;
        }

        process.destroy();

        boolean terminated =
            process.waitFor(
                5,
                TimeUnit.SECONDS
            );

        if (terminated) {
            return;
        }

        process.destroyForcibly();

        process.waitFor(
            5,
            TimeUnit.SECONDS
        );
    }

    private Path requireFile(
        Path path,
        String description
    ) {
        if (path == null) {
            throw new IllegalStateException(
                description
                    + " wurde nicht konfiguriert."
            );
        }

        Path normalized =
            path
                .toAbsolutePath()
                .normalize();

        if (!Files.isRegularFile(
            normalized
        )) {
            throw new IllegalStateException(
                description
                    + " wurde nicht gefunden: "
                    + normalized
            );
        }

        return normalized;
    }

    private Path requireDirectory(
        Path path,
        String description
    ) {
        if (path == null) {
            throw new IllegalStateException(
                description
                    + " wurde nicht konfiguriert."
            );
        }

        Path normalized =
            path
                .toAbsolutePath()
                .normalize();

        if (!Files.isDirectory(
            normalized
        )) {
            throw new IllegalStateException(
                description
                    + " wurde nicht gefunden: "
                    + normalized
            );
        }

        return normalized;
    }

    private String javaExecutable() {
        String javaHome =
            System.getProperty(
                "java.home"
            );

        if (javaHome == null
            || javaHome.isBlank()) {

            return "java";
        }

        Path executable =
            Path.of(
                javaHome,
                "bin",
                isWindows()
                    ? "java.exe"
                    : "java"
            );

        if (Files.isRegularFile(
            executable
        )) {
            return executable.toString();
        }

        return "java";
    }

    private boolean isWindows() {
        String operatingSystem =
            System.getProperty(
                "os.name",
                ""
            );

        return operatingSystem
            .toLowerCase(
                Locale.ROOT
            )
            .contains(
                "win"
            );
    }

    private void setFactoryAttribute(
        DocumentBuilderFactory factory,
        String name,
        String value
    ) {
        try {
            factory.setAttribute(
                name,
                value
            );

        } catch (
            IllegalArgumentException ignored
        ) {
        }
    }

    private record ParsedValidationReport(

        boolean valid,

        List<String> errors,

        List<String> warnings

    ) {
    }
}
