package z9.cloud.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CookieSet implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Map<String, Cookie> cookies = new LinkedHashMap<String, Cookie>();
    
    public CookieSet() {}
    
    public CookieSet(String cookieline)
    {
    	String[] cookieNameValues = cookieline.split(";");
    	for (String cookieNameValue : cookieNameValues) {
    		Cookie cookie = new Cookie(cookieNameValue);
    		addCookie(cookie);
    	}
    }
    
    public void addCookie(Cookie cookie) {
        cookies.put(cookie.getName(), cookie);
    }
    
    public void removeCookie(String name) {
    	cookies.remove(name);
    }
    
    public void setCookies(List<Cookie> cookies) {
        for (Cookie cookie: cookies) {
            this.cookies.put(cookie.getName(), cookie);
        }
    }
    public List<Cookie> getCookies() {
        return new ArrayList<Cookie>(cookies.values());
    }
    
    public String getValue(String name) {
    	Cookie cookie = cookies.get(name);
    	if (cookie == null) {
    		return null;
    	}
    	return cookie.getValue();
    }
    
    @Override
    public String toString() {
        String cookieString = "";
        for (Cookie cookie : cookies.values()) {
            if (!cookieString.equals("")) {
                cookieString += "; ";
            }
            cookieString += cookie.toString();
        }
        return cookieString;
    }
 
}
