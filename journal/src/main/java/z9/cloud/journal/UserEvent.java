package z9.cloud.journal;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;
import z9.cloud.core2.Z9HttpRequest;
import z9.cloud.core2.Z9ResponseData;

import java.io.IOException;

@Table("user_event")
public class UserEvent {
    private static ObjectMapper mapper = new ObjectMapper();
    @PrimaryKey
    private UserEventKey pk;

    @Column(value="content")
    private String content;


    public UserEventKey getPk() {
        return pk;
    }

    public void setPk(UserEventKey pk) {
        this.pk = pk;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public static UserEvent constructInputUserEvent(String message) {
        UserEvent event = new UserEvent();
        try {
            Z9HttpRequest input = mapper.readValue(message, Z9HttpRequest.class);
            event.setPk(new UserEventKey(input));
            event.setContent(message);

        } catch(IOException e) {
            // do nothing
        }
        return event;
    }

    public static UserEvent constructOutputUserEvent(String message) {
        UserEvent event = new UserEvent();
        try {
            Z9ResponseData data = mapper.readValue(message, Z9ResponseData.class);
            event.setPk(new UserEventKey(data));
            event.setContent(message);

        } catch(IOException e) {
            // do nothing
        }
        return event;
    }
}
