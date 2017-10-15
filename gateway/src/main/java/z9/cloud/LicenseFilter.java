package z9.cloud;

import com.netflix.zuul.ZuulFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;

/**
 * Created by david on 10/15/17.
 */
public class LicenseFilter extends ZuulFilter {
    private static Logger log = LoggerFactory.getLogger(LicenseFilter.class);

    @Value("${vbc.license:trial}")
    private String licenseKey;

    private LicenseEntity le;

    @PostConstruct
    public void postConstruct() {
        le = LicenseEntity.toEntity(licenseKey);
        log.info("License Info: " + le);
    }

    @Override
    public String filterType() {
        return "route";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        if (le.expired()) {
            throw new UnsupportedOperationException("Your license has expired");
        }
        return null;
    }

    @Scheduled(cron="*/5 0 * * * ?") // fired every 5 minutes
    public void license() {
        if (le.expired()) {
            log.error("You are running an expired license");
        }
    }
}
