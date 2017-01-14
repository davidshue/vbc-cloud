package z9.cloud.core2

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.http.entity.ContentType
import org.junit.Test

import static junit.framework.Assert.assertEquals

/**
 * Created by david on 1/12/17.
 */
class Z9HttpResponseTest {
    private ObjectMapper mapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_EMPTY)

    @Test
    void testResponseMapper() {
        def response = new Z9HttpResponse(
                statusLine: new Z9StatusLine(protocolVersion: new Z9ProtocolVersion(protocol: 'https', major: 1, minor: 1),
                                statusCode: 200),
                headers: [new Z9Header(name: 'header1', value: 'value1'), new Z9Header(name: 'header2', value: 'value2')],
                contentType: ContentType.DEFAULT_BINARY,
                content: 'this is a test'.bytes
        )

        println response
        def json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(response)

        println json

        Z9HttpResponse out = mapper.readValue(json, Z9HttpResponse.class)

        println out

        assertEquals response, out
    }
}
