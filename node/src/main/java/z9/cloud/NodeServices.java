package z9.cloud;


import org.apache.commons.httpclient.ContentLengthInputStream;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HeaderGroup;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpParser;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import z9.cloud.core.HttpInput;
import z9.cloud.core.HttpMethod;
import z9.cloud.core.HttpOutput;
import z9.cloud.core.Input;
import z9.cloud.core.Output;

import javax.annotation.PostConstruct;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by dshue1 on 3/13/16.
 */
@RestController
public class NodeServices {
	private static Log logger = LogFactory.getLog(NodeServices.class);
	private static final String LINE_RETURN = "\r\n";
	private static final String HTTP_ELEMENT_CHARSET = "US-ASCII";

	@Autowired
	private Environment environment;

	@Value("${http.waittime}")
	private long waitTime;

	@Value("${http.port}")
	private int serverPort;

	@Value("${http.server}")
	private String serverAddress;

	@Value("${http.protocol}")
	private String protocol;

	private String nodeId = "node1";

	private HostConfiguration config;

	@PostConstruct
	public void afterInit() {
		config = new HostConfiguration();
		config.setHost(serverAddress, serverPort, protocol);
	}

	@RequestMapping(value = "/v1", method=RequestMethod.POST)
	public String v1() {
		return "hi there!";
	}


	@RequestMapping(value = "/v1/http", method=RequestMethod.POST)
	public Object httpV1(@RequestBody HttpInput input) {
		return executeHttp(input);
	}

	@RequestMapping(value= "v1/test", method=RequestMethod.POST)
	public Output testV1(@RequestBody Input input) {
		Output output = new Output();
		output.setOutput(input.getName() + " on " + environment.getActiveProfiles()[0]);
		output.setCode(200);

		return output;
	}



	private HttpOutput executeHttp(HttpInput content) {
		HttpConnection httpConnection = null;
		try {
			SimpleHttpConnectionManager connectionManager = new SimpleHttpConnectionManager();
			httpConnection = connectionManager.getConnectionWithTimeout(config, 200);
			if (!httpConnection.isOpen()) {
				httpConnection.open();
			}
			sendToServer(httpConnection.getRequestOutputStream(), content);

			HttpOutput output = readFromServer(httpConnection.getResponseInputStream(), content);
			output.setMethod(content.getMethod());
			output.setSessionId(content.getSessionId());
			output.setNodeId(nodeId);

			return output;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (httpConnection != null) httpConnection.releaseConnection();
		}
	}

	private void sendToServer(OutputStream os, HttpInput content) throws IOException {
		content.write(os);
	}

	private HttpOutput readFromServer(InputStream input, HttpInput content) throws IOException {
		HttpOutput output = new HttpOutput();
		long start = System.currentTimeMillis();
		BufferedInputStream bis = new BufferedInputStream(input);
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		String line = null;
		do {
			line = HttpParser.readLine(bis, HTTP_ELEMENT_CHARSET);
		} while (line != null && line.length() == 0);

		logger.debug(line);
		if (line == null) {
			return null;
		}
		output.setTitle(line);
		Header[] headers = HttpParser.parseHeaders(bis, HTTP_ELEMENT_CHARSET);

		for (Header h : headers) {
			if (h.getName().equalsIgnoreCase("Set-Cookie") || h.getName().equalsIgnoreCase("Set-Cookie2")) {
				output.addCookie(h.toString().trim());
			}
			else {
				bout.write(h.toString().getBytes());
			}
		}
		bout.write(LINE_RETURN.getBytes());
		long end = System.currentTimeMillis();
		logger.debug("Took " + (end-start) + " ms to write headers.");

		if (content.getMethod() != HttpMethod.HEAD) {
			HeaderGroup headerGroup = new HeaderGroup();
			headerGroup.setHeaders(headers);

			Header contentLength = headerGroup.getFirstHeader("Content-Length");
			//Header transferEncoding = headerGroup.getFirstHeader("Transfer-Encoding");

			if (contentLength != null) {
				long len = getContentLength(contentLength);
				if (len >= 0) {
					ContentLengthInputStream in = new ContentLengthInputStream(bis, len);
					readFromContentLengthStream(bout, in, (int)len);
				}
				else {
					readFromOrdinaryLengthStream(bout, bis);
				}
			}
			else {
				readFromOrdinaryLengthStream(bout, bis);
			}
		}

		bout.flush();
		long end1 = System.currentTimeMillis();
		logger.debug("Took " + (end1-end) + " ms to write contents.");

		output.setPayload(bout.toByteArray());
		return output;
	}

	private long getContentLength(Header contentLength) {
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

	private void readFromContentLengthStream(ByteArrayOutputStream baos, ContentLengthInputStream cis, int length) throws IOException {
		byte[] tmp = new byte[8192];
		int bytesRead = 0;
		while ((bytesRead = cis.read(tmp)) != -1) {
			baos.write(tmp, 0, bytesRead);
		}
	}

	private void readFromOrdinaryLengthStream(ByteArrayOutputStream baos, BufferedInputStream in) throws IOException {
		byte[] tmp = new byte[4096];
		int bytesRead = 0;
		long start = System.currentTimeMillis();
		while ( isAvailable(in) && (bytesRead = in.read(tmp)) != -1) {
			baos.write(tmp, 0, bytesRead);
			long end = System.currentTimeMillis();
			logger.debug("Took " + (end - start) + " to write subcontents");
			start = end;
		}
		long end = System.currentTimeMillis();
		logger.debug("Took " + (end - start) + " to finish subcontents");
	}

	private boolean isAvailable(BufferedInputStream in) throws IOException {
		int available = in.available();
		if (available > 0) {
			return true;
		}
		try {
			Thread.sleep(waitTime);
		} catch (InterruptedException e) {
			// Do nothing
		}
		available = in.available();
		return available > 0;
	}
}
