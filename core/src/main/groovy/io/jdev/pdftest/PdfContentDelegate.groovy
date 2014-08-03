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

class PdfContentDelegate {
	Map<String,PdfContentDescriptor> contentClosures = [:]
	
	def methodMissing(String name, Object argsParam) {
        Object[] args = argsParam
        Map options
        Closure closure
		if(args.length == 1 && args[0] instanceof Closure) {
            options = [:]
            closure = args[0] as Closure
        } else if(args.length == 2 && args[0] instanceof Map && args[1] instanceof Closure){
            options = args[0] as Map
            closure = args[1] as Closure
		} else {
			throw new NoSuchMethodException(name)
		}
        Class<? extends Closure> closureClass = closure.getClass() as Class<? extends Closure>
        PdfContentDescriptor desc = new PdfContentDescriptor()
        desc.name = name
        desc.contentClosure = closureClass
        if(options.containsKey('required')) {
            desc.required = options.required
        }
        contentClosures[name] = desc
	}
}
