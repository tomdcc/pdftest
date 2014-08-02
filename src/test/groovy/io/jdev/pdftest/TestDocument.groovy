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

class TestDocument extends PdfDocument {
    static isA = { List<String> firstPageLines -> firstPageLines.contains('Test Invoice') }
    static content = {
        accountNumber { findLine(~/Account Number: (\d+)/) }
        total         { findLine(~/Total:\s(\$[,\.\d]+)/) }
        charges       {
            findBetweenLines(~/Date.*Price/, ~/Total:.*/).collect {
                def fields = it.split('\t')
                [date: fields[0], description: fields[1], price: fields[2]]
            }
        }
        bpayReference { findLineOnPage(~/BPAY Reference:\s(\d+)/, 1) }
        bpayBillerCode(required: false) { findLineOnPage(~/BPAY Biller Code:\s(\d+)/, 1) }
    }
}
