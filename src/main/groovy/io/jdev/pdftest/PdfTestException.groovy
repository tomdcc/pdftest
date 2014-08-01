package io.jdev.pdftest

class PdfTestException extends RuntimeException {
    PdfTestException() {
        super()
    }

    PdfTestException(String message) {
        super(message)
    }

    PdfTestException(String message, Throwable cause) {
        super(message, cause)
    }
}
