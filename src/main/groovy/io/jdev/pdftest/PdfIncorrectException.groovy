package io.jdev.pdftest

class PdfIncorrectException extends PdfTestException {
    PdfIncorrectException(Class<? extends PdfDocument> cls, Throwable cause) {
        super("Error running isA checker for pdf document class $cls.simpleName", cause)
    }
    PdfIncorrectException(Class<? extends PdfDocument> cls) {
        super("isA checker for pdf document class $cls.simpleName failed")
    }
}
