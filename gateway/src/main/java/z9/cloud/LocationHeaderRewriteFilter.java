package z9.cloud;

import com.netflix.util.Pair;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UrlPathHelper;

import java.util.Optional;

public class LocationHeaderRewriteFilter extends ZuulFilter {

    private final UrlPathHelper urlPathHelper = new UrlPathHelper();
    private final RouteLocator routeLocator ;

    public LocationHeaderRewriteFilter(RouteLocator routeLocator) {
        this.routeLocator = routeLocator;
    }

    @Override
    public String filterType() {
        return "post";
    }

    @Override
    public int filterOrder() {
        return 100;
    }

    @Override
    public boolean shouldFilter() {
        return extractLocationHeader(RequestContext.getCurrentContext()).isPresent();
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        Route route = routeLocator.getMatchingRoute(urlPathHelper.getPathWithinApplication(ctx.getRequest()));
        if (route != null) {
            Pair<String, String> lh = extractLocationHeader(ctx).get();
            String origin = RequestContext.getCurrentContext().getRequest().getHeader("NG-Origin");
            if (origin != null) {
                lh.setSecond("http://" + origin + lh.second());
            }
//            lh.setSecond(route.getPrefix() + lh.second());
//                    ServletUriComponentsBuilder.fromCurrentContextPath().path(route.getPrefix()).build().toUriString()));
            System.out.println(lh.first() + " : " + lh.second());
        }
        return null;
    }


    private Optional<Pair<String, String>> extractLocationHeader(RequestContext ctx) {

        return ctx.getZuulResponseHeaders()
                .stream()
                .filter(p -> "Location".equals(p.first()))
                .findFirst();
    }
}