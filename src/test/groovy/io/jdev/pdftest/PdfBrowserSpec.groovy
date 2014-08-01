package io.jdev.pdftest

import spock.lang.Specification

class PdfBrowserSpec extends Specification {

    private final static byte[] DATA = 'foo'.bytes

    PdfParser pdfParser
    PdfBrowser pdfBrowser

    void setup() {
        pdfBrowser = new PdfBrowser()
        pdfBrowser.pdfParser = pdfParser = Mock(PdfParser)
    }

    void "pdf browser parses a byte array and can assert page identity"() {
        when: 'ask for document'
        pdfBrowser.fetchDocument(DATA)

        then: 'parses pdf'
        1 * pdfParser.pdfToText(DATA) >> [['Test Invoice', 'Account Number: 1234']]

        when: 'verify at that document'
        def result = pdfBrowser.documentIsA(TestDocument)

        then: 'returns true'
        result == true

        and: 'sets document accordingly'
        pdfBrowser.document instanceof TestDocument

        and: 'wires it up properly'
        pdfBrowser.document.accountNumber == '1234'
    }

    void "pdf browser assert incorrect page identity"() {
        when: 'ask for document'
        pdfBrowser.fetchDocument(DATA)

        then: 'parses pdf'
        1 * pdfParser.pdfToText(DATA) >> [['Different PDF']]

        when: 'verify at that document'
        pdfBrowser.documentIsA(TestDocument)

        then: 'exception thrown'
        PdfIncorrectException ex = thrown()
        ex.message == "isA checker for pdf document class TestDocument failed"
    }
}