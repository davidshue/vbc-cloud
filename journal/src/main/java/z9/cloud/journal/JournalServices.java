package z9.cloud.journal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class JournalServices {
    private static Log logger = LogFactory.getLog(JournalServices.class);

    private final DataSampler sampler;

    @Autowired
    public JournalServices(DataSampler sampler) {
        this.sampler = sampler;
    }

    @RequestMapping("/v1/test")
    public String test() {
        return "work1";
    }

    @RequestMapping("/v1/sample")
    public List<String> sample() {
        return sampler.getUserIds();
    }
}
