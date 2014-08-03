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

class PdfDocumentSpec extends Specification {

    PdfDocument document
    List<List<String>> pages

    void setup() {
        pages = [
            // page 1
            [
                "Heading",
                "Test Invoice",
                "Account Number: 1234",
                "New Charges",
                "Date\tDescription\tPrice",
                "1999-12-31\tY2K Fixups\t\$1,000,000.00",
                "2000-01-01\tBeer\t\$50.00",
                "Total:\t\$1,000,050.00"
             ],
            // page 2
            [
                "Payment options:",
                "BPAY",
                "BPAY Reference: 123456"
            ]
        ]

    }

    void "content closures work as expected"() {
        given: 'full document'
        document = new TestDocument(pages: pages)

        expect:
        document.accountNumber == '1234'

        and:
        document.charges.size() == 2
        document.charges[0].date == '1999-12-31'
        document.charges[0].description == 'Y2K Fixups'
        document.charges[0].price == '$1,000,000.00'
        document.charges[1].date == '2000-01-01'
        document.charges[1].description == 'Beer'
        document.charges[1].price == '$50.00'
        document.total == '$1,000,050.00'

        and:
        document.bpayReference == '123456'
    }

    void "missing content throws exception"() {
        given: 'doc missing stuff on second page'
        document = new TestDocument(pages: [pages[0], []])

        when: 'ask for matching property'
        document.bpayReference

        then: 'assertion thrown'
        PdfContentMissingException ex = thrown()
        ex.message == "TestDocument content with name 'bpayReference' not found"
    }

    void "missing optional content returns null"() {
        given: 'doc without biller code'
        document = new TestDocument(pages: [pages[0], []])

        when: 'ask for matching property'
        def result = document.bpayBillerCode

        then: 'no assertion thrown'
        notThrown(Throwable)

        and: 'result was null'
        result == null
    }

    void "undefined content throws exception"() {
        given: 'doc'
        document = new TestDocument(pages: [pages[0], []])

        when: 'ask for undefined property'
        document.bpayReferenceNumber

        then: 'assertion thrown'
        PdfContentUndefinedException ex = thrown()
        ex.message == "TestDocument does not define content with name 'bpayReferenceNumber'"
    }

}
