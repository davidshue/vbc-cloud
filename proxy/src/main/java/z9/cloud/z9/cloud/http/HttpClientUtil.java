package z9.cloud.z9.cloud.http;

import com.zeronines.enums.HttpMethod;
import com.zeronines.service.HttpInput;
import org.apache.commons.httpclient.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.InputStream;


public abstract class HttpClientUtil  {
	private static Log logger = LogFactory.getLog(HttpClientUtil.class);
	
	private static final String LINE_RETURN = System.getProperty("line.separator");
    private static final String HTTP_ELEMENT_CHARSET = "US-ASCII";

    public static HttpInput readFromClient(InputStream input) throws IOException {
    	HttpInput httpInput = new HttpInput();
    	java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();

    	String line = null;
    	do {
    		line = HttpParser.readLine(input, HTTP_ELEMENT_CHARSET);
    	} while (line != null && line.length() == 0);

    	logger.debug("request " + line);
    	if (line == null) {
    		return null;
    	}
    	httpInput.setTitle(line);
    	RequestLine requestLine = RequestLine.parseLine(line);
    	Header[] headers = HttpParser.parseHeaders(input, HTTP_ELEMENT_CHARSET);

    	for (Header h : headers) {
        	if (h.getName().equals("Cookie")) {
        		httpInput.resolveCookie(h.getValue().trim());
        	}
        	else {
        	    buffer.write(h.toExternalForm().getBytes());
        		//buffer += h.toExternalForm();
        	}
    	}
    	buffer.write(LINE_RETURN.getBytes());
    	//buffer += LINE_RETURN;

    	String methodname = requestLine.getMethod();
    	httpInput.setMethod(HttpMethod.valueOf(methodname));
    	httpInput.setUri(requestLine.getUri());
    	httpInput.setHttpversion(requestLine.getHttpVersion().toString());
    	if (httpInput.getMethod() == HttpMethod.POST || httpInput.getMethod() == HttpMethod.PUT) {
    		HeaderGroup headerGroup = new HeaderGroup();
    		headerGroup.setHeaders(headers);
    		Header contentLength = headerGroup.getFirstHeader("Content-Length");
    		Header transferEncoding = headerGroup.getFirstHeader("Transfer-Encoding");
    		InputStream in = input;
    		if (transferEncoding != null) {
    			if (transferEncoding.getValue().indexOf("chunked") != -1) {
    				in = new ChunkedInputStream(in);
    			}
    		} else if (contentLength != null) {
    			long len = getContentLength(contentLength);
    			if (len >= 0) {
    				in = new ContentLengthInputStream(in, len);
    			}
    		}
    		
    		if (in != null) {
    			byte[] tmp = new byte[4096];
    			int bytesRead = 0;
    			while ((bytesRead = in.read(tmp)) != -1) {
    			    buffer.write(tmp, 0, bytesRead);
    				//buffer += EncodingUtil.getString(tmp, 0, bytesRead, "UTF-8");
    			}
    		}


//    		buffer += EncoderUtil.encodeB(baos.toByteArray());
    	}

    	httpInput.setPayload(buffer.toByteArray());
    	return httpInput;
    }
    
    private static long getContentLength(Header contentLength) {
        if (contentLength != null) {
            try {
                return Long.parseLong(contentLength.getValue());
            } catch (NumberFormatException e) {
                return -1;
            }
        } else {
            return -1;
        }
    }
}
