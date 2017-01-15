package z9.cloud.core2

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.http.message.BasicHeader
import org.apache.http.message.BasicHttpResponse
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
                content: 'this is a test'.bytes
        )

        println response
        def json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(response)

        println json

        Z9HttpResponse out = mapper.readValue(json, Z9HttpResponse.class)

        println out

        assertEquals response, out

        BasicHttpResponse responseIn = response.toBasicHttpResponse()

        assertEquals responseIn.statusLine.protocolVersion.protocol, 'https'
        assertEquals responseIn.statusLine.protocolVersion.major, 1
        assertEquals responseIn.statusLine.protocolVersion.minor, 1

        assertEquals responseIn.allHeaders.collect{it.toString()}, [new BasicHeader('header1', 'value1').toString(), new BasicHeader('header2', 'value2').toString()]

        Z9HttpResponse another = Z9HttpResponse.toZ9HttpResponse(responseIn)

        assertEquals response, another


    }
}
