package z9.license;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/ws/v1")
@RestController
public class LicenseController {

    @RequestMapping(value="", method=RequestMethod.GET)
    public String hello() {
        return "Hello!";
    }

    @RequestMapping(value="", method=RequestMethod.POST)
    public String license(@RequestBody Data data) {
        return LicenseHasher.encode(data);
    }
}
