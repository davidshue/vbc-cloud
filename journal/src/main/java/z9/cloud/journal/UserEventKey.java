package z9.cloud.journal;

import com.datastax.driver.core.DataType;
import com.datastax.driver.core.utils.UUIDs;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.cassandra.core.Ordering;
import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.cassandra.mapping.CassandraType;
import org.springframework.data.cassandra.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;
import z9.cloud.core2.Z9HttpRequest;
import z9.cloud.core2.Z9ResponseData;

import java.io.Serializable;
import java.util.UUID;

@PrimaryKeyClass
public class UserEventKey implements Serializable {
    @PrimaryKeyColumn(name = "user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String personId;


    @PrimaryKeyColumn(name = "event_ts", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    @CassandraType(type = DataType.Name.UUID)
    private UUID time;

    @PrimaryKeyColumn(name = "type", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    private int type;

    public UserEventKey(Z9HttpRequest request) {
        this.personId = request.getZ9SessionId();
        this.time = UUIDs.startOf(request.getTimestamp());
        this.type = 1;
    }

    public UserEventKey(Z9ResponseData data) {
        this.personId = data.getId();
        this.time = UUIDs.startOf(data.getTimestamp());
        this.type = 2;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }


    public UUID getTime() {
        return time;
    }

    public void setTime(UUID time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        UserEventKey that = (UserEventKey) o;

        return new EqualsBuilder()
                .append(personId, that.personId)
                .append(time, that.time)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(personId)
                .append(time)
                .toHashCode();
    }
}
