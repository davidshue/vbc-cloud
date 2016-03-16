package z9.cloud.core;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;



public class HttpInput implements Serializable {
	private static final long serialVersionUID = 1L;

	private String title = null;
	private HttpMethod httpMethod = null;
	private String uri;
	private String httpversion;
	private CookieSet cookieSet = new CookieSet();
	private String sessionId = null;	
	private String nodeId = null;
	
	private byte[] payload = null;
	
	public void setPayload(byte[] payload) {
	    this.payload = payload;
	}
	
	public byte[] getPayload() {
		return payload;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public HttpMethod getMethod() {
		return httpMethod;
	}

	public void setMethod(HttpMethod httpMethod) {
		this.httpMethod = httpMethod;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getHttpversion() {
		return httpversion;
	}

	public void setHttpversion(String httpversion) {
		this.httpversion = httpversion;
	}

	public CookieSet getCookieSet() {
		return cookieSet;
	}
	
	public void setCookieSet(CookieSet cookieSet) {
	    this.cookieSet = cookieSet;
	}

	public void addCookie(Cookie cookie) {
		cookieSet.addCookie(cookie);
	}
	
	public void removeCookie(String name) {
		cookieSet.removeCookie(name);
	}

	public String getSessionId() {
		return sessionId;
	}
	
	public void setSessionId(String sessionId) {
	    this.sessionId = sessionId;
	}

    public void write(OutputStream out) throws IOException {
        PrintWriter writer = new PrintWriter(out, true);
        writer.println(title + "\r");
        if (!cookieSet.getCookies().isEmpty()) {
            writer.println("Cookie: " + cookieSet.toString() + "\r");
        }
        out.write(payload);
        writer.println("\r");
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
    
    public void resolveCookie(String cookieLine) {
    	CookieSet cookieSet = new CookieSet(cookieLine);

    	String sessionId = cookieSet.getValue("zsessionid");
    	if (StringUtils.isNotBlank(sessionId)) {
    		setSessionId(sessionId);
    		cookieSet.removeCookie("zsessionid");
    	}
    	String nodeId = cookieSet.getValue("znodeid");
    	if (StringUtils.isNotBlank(nodeId)) {
    		setNodeId(nodeId);
    		cookieSet.removeCookie("znodeid");
    	}
    	this.cookieSet = cookieSet;
    }

}
