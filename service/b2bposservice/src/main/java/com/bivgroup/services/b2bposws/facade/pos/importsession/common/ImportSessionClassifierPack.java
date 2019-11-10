package com.bivgroup.services.b2bposws.facade.pos.importsession.common;

import java.util.HashMap;
import java.util.Map;

public class ImportSessionClassifierPack {

    private Map<String, ImportSessionClassifier> importSessionClassifierMap = new HashMap<>();

    public ImportSessionClassifierPack(String... classifierNames) throws ImportSessionException {
        for (String classifierName : classifierNames) {
            addImportSessionClassifierToPack(classifierName);
        }
    }

    private ImportSessionClassifier addImportSessionClassifierToPack(String classifierName) throws ImportSessionException {
        ImportSessionClassifier importSessionClassifier;
        try {
            importSessionClassifier = new ImportSessionClassifier(classifierName);
            importSessionClassifierMap.put(classifierName, importSessionClassifier);
        } catch (Exception ex) {
            throw new ImportSessionException(
                    "Ошибка при получении сведений классификаторов, необходимых для корректного импорта данных!",
                    "Unable to create ImportSessionClassifier for required classifier!",
                    ex
            );
        }
        return importSessionClassifier;
    }

    /*
    public ImportSessionClassifier get(String classifierName) throws ImportSessionException {
        ImportSessionClassifier importSessionClassifier = importSessionClassifierMap.get(classifierName);
        if (importSessionClassifier == null) {
            throw new ImportSessionException(
                    "Ошибка при получении сведений классификаторов, необходимых для корректного импорта данных!",
                    "Unable to get requested ImportSessionClassifier - probably it was not loaded earlier!"
            );
        }
        return importSessionClassifier;
    }
    */

    public ImportSessionClassifier get(String classifierName) throws ImportSessionException {
        ImportSessionClassifier importSessionClassifier = importSessionClassifierMap.get(classifierName);
        if (importSessionClassifier == null) {
            importSessionClassifier = addImportSessionClassifierToPack(classifierName);
        }
        return importSessionClassifier;
    }

}
