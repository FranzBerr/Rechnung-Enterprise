package com.bcs.rechnung.invoice.document;

import java.nio.file.Path;
import java.util.List;

public record KositValidationResult(

    boolean valid,

    Path reportFile,

    List<String> errors,

    List<String> warnings,

    String consoleOutput

) {
}
