package z9.license;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RequestMapping("/ws/v1")
@RestController
public class LicenseController {
    @RequestMapping(method=RequestMethod.GET)
    public String help() {
        return "refer to /ws/v1/sample to get the sample input data format for generating license key, " +
                "License type: 0-trial, 1-limited, 2-enterprise";
    }

    @RequestMapping(value="/sample", method=RequestMethod.GET)
    public Object sample() {
        Data sampleData = new Data();
        sampleData.setDomain("*.xyz.com");
        sampleData.setType(1);
        sampleData.setExpDate(LocalDate.of(2018, 12, 24));
        return sampleData;
    }

    @RequestMapping(value="", method=RequestMethod.POST)
    public String license(@RequestBody Data data) {
        return LicenseHasher.encode(data);
    }
}
