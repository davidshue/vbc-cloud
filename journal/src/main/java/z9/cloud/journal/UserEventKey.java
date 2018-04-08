package z9.cloud.journal;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.cassandra.core.Ordering;
import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.cassandra.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;
import z9.cloud.core2.Z9HttpRequest;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@PrimaryKeyClass
public class UserEventKey implements Serializable {
    private static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
    @PrimaryKeyColumn(name = "user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String personId;

    @PrimaryKeyColumn(name = "day", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    private String day;

    @PrimaryKeyColumn(name = "event_ts", ordinal = 2, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    private Date time;

    public UserEventKey(Z9HttpRequest request) {
        this.personId = request.getZ9SessionId();
        this.time = new Date();
        this.day = DATE_FORMAT.format(this.time);
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        UserEventKey that = (UserEventKey) o;

        return new EqualsBuilder()
                .append(personId, that.personId)
                .append(day, that.day)
                .append(time, that.time)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(personId)
                .append(day)
                .append(time)
                .toHashCode();
    }
}
