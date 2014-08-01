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
