/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 the original author or authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

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
        1 * pdfParser.parsePdf(DATA) >> new PdfData(pages: [['Test Invoice', 'Account Number: 1234']])

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
        1 * pdfParser.parsePdf(DATA) >> new PdfData(pages: [['Different PDF']])

        when: 'verify at that document'
        pdfBrowser.documentIsA(TestDocument)

        then: 'exception thrown'
        PdfIncorrectException ex = thrown()
        ex.message == "isA checker for pdf document class TestDocument failed"
    }
}