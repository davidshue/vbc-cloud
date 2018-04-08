package z9.cloud;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.exception.ZuulException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.zuul.util.ZuulRuntimeException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

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
        return "pre";
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
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request =  sra.getRequest();

        String servedDomain = request.getHeader("Domain") != null? request.getHeader("Domain") : "undefined";
        System.out.println("servedDomain: " + servedDomain);

        if (!le.isValid(servedDomain)) {
            ZuulRuntimeException zre = new ZuulRuntimeException(new ZuulException("Your License Is Not Valid", 400, "License Invalid"));
            throw zre;
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
