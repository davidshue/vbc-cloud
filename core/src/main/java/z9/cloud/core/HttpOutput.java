package z9.cloud.core;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class HttpOutput implements Serializable {
	private static final long serialVersionUID = 1L;

	private String title;
	private HttpMethod method = null;
	private List<String> cookies = new LinkedList<String>();
	private String sessionId = null;
	private String nodeId;
	private byte[] payload;
	private String origin;

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public byte[] getPayload() {
		return payload;
	}
	
	public void setPayload(byte[] payload) {
		this.payload = payload;
	}
	
	public void write(OutputStream out) throws IOException {
		PrintWriter writer = new PrintWriter(out, true);
		// \r is needed to get it working on IIS
		// must do println to make it flush
		writer.println(title + "\r");
		// 	Do some cookies handling
		for (String cookie : cookies) {
			// \r is needed to get it working on IIS
			// must do println to make it flush
			writer.println(cookie + "\r");
		}
		out.write(payload);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the method
	 */
	public HttpMethod getMethod() {
		return method;
	}

	/**
	 * @param method the method to set
	 */
	public void setMethod(HttpMethod method) {
		this.method = method;
	}

	public List<String> getCookies() {
		return cookies;
	}
	
	public void setCookies(List<String> cookies) {
		this.cookies = cookies;
	}

	public void addCookie(String cookie) {
		this.cookies.add(cookie);
	}
	
	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	/**
	 * @return the nodeId
	 */
	public String getNodeId() {
		return nodeId;
	}

	/**
	 * @param nodeId the nodeId to set
	 */
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
}
