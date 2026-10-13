package com.bcs.rechnung.invoice.document;

import java.nio.file.Path;

public interface KositValidationService {

    KositValidationResult validate(
        Path xmlFile,
        Path reportDirectory
    );
}
