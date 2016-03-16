package z9.cloud.core;

import java.io.Serializable;

public class Cookie implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String name = null;
    private String value = null;
    
    public Cookie(){}
    
    public Cookie(String name, String value) {
        this.name = name;
        this.value = value;
    }
    
    public Cookie(String line) {
        this.name = constructName(line);
        this.value = constructValue(line);
    }
    
    private String constructName(String line) {
        int endIndex = line.indexOf("=");
        return line.substring(0, endIndex).trim();
    }
    
    private String constructValue(String line) {
        int startIndex = line.indexOf("=") + 1;
        int endIndex = line.indexOf(";");
        if (endIndex == -1) {
            return line.substring(startIndex).trim();
        }
        return line.substring(startIndex, endIndex).trim();
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    
    @Override
    public String toString() {
        return name + "=" + value;
    }
}
