package z9.cloud;

import java.time.LocalDate;

/**
 * Created by david on 10/8/17.
 */
public class LicenseEntity {
    private String domain;

    private LicenseType type;

    private LocalDate expiration;

    private LicenseEntity(String domain, int type, long epochDays) {
        this.domain = domain;
        this.type = LicenseType.byType(type);
        long days = epochDays;
        switch(this.type) {
            case trial:
                days = LocalDate.now().plusDays(30l).toEpochDay();
                break;
            default:
                break;
        }
        this.expiration = LocalDate.ofEpochDay(days);
    }

    public String getDomain() {
        return domain;
    }

    public LicenseType getType() {
        return type;
    }

    public LocalDate getExpiration() {
        return expiration;
    }

    public boolean isValid(String servedDomain) {
        return isValidDomain(servedDomain) && !expired();
    }

    public boolean isValidDomain(String servedDomain) {
        //return domain.equals("*") || servedDomain.endsWith(domain);
        // the domain check is not working
        return true;
    }

    public boolean expired() {
        return LocalDate.now().isAfter(expiration);
    }

    public static LicenseEntity toEntity(String license) {
        try {
            String decoded = LicenseHasher.decode(license);
            String[] parts = decoded.split("\\|");

            if (parts.length != 3) {
                throw new RuntimeException("Not a valid license type, using trial license");
            }
            String domain = parts[0];
            int type = Integer.parseInt(parts[1]);
            Long epochDays = Long.parseLong(parts[2]);
            return new LicenseEntity(domain, type, epochDays);
        } catch (Throwable t) {
            return new LicenseEntity("*", LicenseType.trial.type, -1L);
        }
    }

    @Override
    public String toString() {
        return "License type: " + type + ", expired on " + expiration;
    }

    private  enum LicenseType {
        trial(0),
        limited(1),
        enterprise(2);

        private int type;

        LicenseType(int type) {
            this.type = type;
        }


        static LicenseType byType(int type) {
            switch(type) {
                case 1:
                    return limited;
                case 2:
                    return enterprise;
                default:
                    return trial;
            }
        }
    }
}
