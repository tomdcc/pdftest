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

public class PdfBrowser {

    PdfParser pdfParser = new PdfParser()

    PdfData data
    PdfDocument document

    void fetchDocumentFromUrl(String url, Map<String,String> cookies = [:]) {
        document = null
        data = pdfParser.parsePdf(url, cookies)
    }

    void fetchDocumentFromUrl(String url, def webDriver) {
		Map sessionCookies = webDriver.manage().cookies.collectEntries { [it.name, it.value] }
		fetchDocumentFromUrl(url, sessionCookies)
	}

    void fetchDocument(byte[] bytes) {
        document = null
        data = pdfParser.parsePdf(bytes)
    }

    void fetchDocumentFromFile(File file) {
        document = null
        data = pdfParser.parsePdf(file)
    }

    boolean documentIsA(Class<? extends PdfDocument> requiredClass) {
        def result = requiredClass.isA.call(data.pages[0])
        if(result) {
            document = requiredClass.newInstance()
            document.pages = data.pages
            document.filename = data.filename
            document.bytes = data.bytes
            true
        } else {
            throw new PdfIncorrectException(requiredClass)
        }
    }
}
