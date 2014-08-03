<%--
  ~ The MIT License (MIT)
  ~
  ~ Copyright (c) 2014 the original author or authors
  ~
  ~ Permission is hereby granted, free of charge, to any person obtaining a copy
  ~ of this software and associated documentation files (the "Software"), to deal
  ~ in the Software without restriction, including without limitation the rights
  ~ to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  ~ copies of the Software, and to permit persons to whom the Software is
  ~ furnished to do so, subject to the following conditions:
  ~
  ~ The above copyright notice and this permission notice shall be included in
  ~ all copies or substantial portions of the Software.
  ~
  ~ THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  ~ IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  ~ FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  ~ AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  ~ LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  ~ OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
  ~ THE SOFTWARE.
  --%>

<!doctype>
<html>
<head>
    <title>PDF Test App</title>
</head>
<body>
    <h1>PDF Test App</h1>
    <div>
        <a id='downloadNew' href="<%= request.getContextPath() %>/pdf">Download new PDF</a>
    </div>
    <div>
        <a id='saveInSession' href="<%= request.getContextPath() %>/pdf?saveInSession=true">Save PDF in Session</a>
    </div>
    <% if(session.getAttribute("pdf") != null) { %>
        <div>
            <a id='downloadFromSession' href="<%= request.getContextPath() %>/pdfdownload">Download PDF in Session</a>
        </div>
    <% } %>
</body>
</html>