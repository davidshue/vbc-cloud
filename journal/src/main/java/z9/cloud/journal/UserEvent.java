package z9.cloud.journal;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;
import z9.cloud.core2.Z9HttpRequest;
import z9.cloud.core2.Z9ResponseData;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Table("user_event")
public class UserEvent {
    private static ObjectMapper mapper = new ObjectMapper();
    @PrimaryKey
    private UserEventKey pk;

    @Column
    private Map<String, String> meta;

    @Column(value="content")
    private String content;


    public UserEventKey getPk() {
        return pk;
    }

    public void setPk(UserEventKey pk) {
        this.pk = pk;
    }

    public Map<String, String> getMeta() {
        return meta;
    }

    public void setMeta(Map<String, String> meta) {
        this.meta = meta;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public static UserEvent constructInputUserEvent(String message, boolean saveBlob) {
        UserEvent event = new UserEvent();
        try {
            Z9HttpRequest input = mapper.readValue(message, Z9HttpRequest.class);
            event.setPk(new UserEventKey(input));
            Map<String, String> meta = new HashMap<>();
            meta.put("uri", input.getRequestLine().getUri());
            meta.put("method", input.getRequestLine().getMethod());
            event.setMeta(meta);
            if (saveBlob) event.setContent(message);

        } catch(IOException e) {
            // do nothing
        }
        return event;
    }

    public static UserEvent constructOutputUserEvent(String message, boolean saveBlob) {
        UserEvent event = new UserEvent();
        try {
            Z9ResponseData data = mapper.readValue(message, Z9ResponseData.class);
            event.setPk(new UserEventKey(data));
            Map<String, String> meta = new HashMap<>();
            meta.put("status", String.valueOf(data.getResponse().getStatusLine().getStatusCode()));
            meta.put("reason", data.getResponse().getStatusLine().getReasonPhrase());
            event.setMeta(meta);
            if (saveBlob) event.setContent(message);

        } catch(IOException e) {
            // do nothing
        }
        return event;
    }
}
