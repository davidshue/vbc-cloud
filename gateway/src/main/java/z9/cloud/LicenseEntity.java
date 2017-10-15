package z9.cloud;

import java.time.LocalDate;

/**
 * Created by david on 10/8/17.
 */
public class LicenseEntity {
    private LicenseType type;

    private LocalDate expiration;

    private LicenseEntity(int type, long epochDays) {
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

    public LicenseType getType() {
        return type;
    }

    public LocalDate getExpiration() {
        return expiration;
    }

    public boolean expired() {
        return LocalDate.now().isAfter(expiration);
    }

    public static LicenseEntity toEntity(String license) {
        try {
            String decoded = LicenseHasher.decode(license);
            String[] parts = decoded.split("\\|");

            if (parts.length != 2) {
                throw new RuntimeException("Not a valid license type, using trial license");
            }

            int type = Integer.parseInt(parts[0]);
            Long epochDays = Long.parseLong(parts[1]);
            return new LicenseEntity(type, epochDays);
        } catch (Throwable t) {
            return new LicenseEntity(LicenseType.trial.type, -1L);
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
