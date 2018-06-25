package z9.license;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Data {
    private String domain;
    private int type;
    private String expiration;
    private LocalDate expDate;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getExpiration() {
        return expiration;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
        this.expDate = LocalDate.parse(expiration, DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public LocalDate getExpDate() {
        return expDate;
    }

    public void setExpDate(LocalDate expDate) {
        this.expDate = expDate;
        this.expiration = expDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    @Override
    public String toString() {
        return domain + "|" + type + "|" + expDate.toEpochDay();
    }

    public static Data parse(String s) {
        String[] parts = s.split("|");
        if (parts.length != 3) throw new IllegalArgumentException(s + "{} has wrong format");

        String domain = parts[0];
        int type = Integer.valueOf(parts[1]);
        String expiration = parts[2];
        Data out = new Data();
        out.setDomain(domain);
        out.setType(type);
        out.setExpiration(expiration);

        return out;
    }
}
