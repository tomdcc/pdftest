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
