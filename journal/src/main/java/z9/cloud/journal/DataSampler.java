package z9.cloud.journal;

import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataSampler {
    private final Session session;

    private static final String USERID_SELECT = "select distinct user_id from user_event limit 50";
    private final PreparedStatement select;

    @Autowired
    public DataSampler(Session session) {
        this.session = session;
        select = session.prepare(USERID_SELECT);
        System.out.println("inside data sampler");
    }

    public List<String> getUserIds() {
        List<String> result = new ArrayList<>();

        ResultSet rs = session.execute(select.bind());
        while(!rs.isExhausted()) {
            Row row = rs.one();
            result.add(row.getString("user_id"));
        }

        return result;
    }
}
