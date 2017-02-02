package z9.cloud.core2

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.http.message.BasicHeader
import org.apache.http.message.BasicHttpEntityEnclosingRequest
import org.apache.http.message.BasicHttpRequest
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue
/**
 * Created by david on 1/11/17.
 */
class Z9HttpRequestTest {
    private ObjectMapper mapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
    @Test
    void testEntityEnclosingMapping() {
        println 'this is a test'.bytes
        Z9HttpRequest request = new Z9HttpRequest(
                headers: [new Z9Header(name: 'header1', value: 'value1'), new Z9Header(name: 'header2', value: 'value2')],
                requestLine: new Z9RequestLine(method: 'post', uri: 'http://www.cnn.com', protocolVersion: new Z9ProtocolVersion(protocol: 'https', major: 1, minor: 1)),
                content: 'this is a test'.bytes
        )
        println request
        def json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(request)

        println json

        Z9HttpRequest out = mapper.readValue(json, Z9HttpRequest.class)

        println out

        assertEquals request, out

        assertTrue(request.toBasicHttpRequest() instanceof BasicHttpEntityEnclosingRequest)
        assertTrue(out.toBasicHttpRequest() instanceof BasicHttpEntityEnclosingRequest)

        BasicHttpEntityEnclosingRequest httpIn = request.toBasicHttpRequest()
        assertEquals httpIn.requestLine.protocolVersion.protocol, 'https'
        assertEquals httpIn.requestLine.protocolVersion.major, 1
        assertEquals httpIn.requestLine.protocolVersion.minor, 1
        assertEquals httpIn.requestLine.method, 'post'
        assertEquals httpIn.requestLine.uri, 'http://www.cnn.com'
        assertEquals httpIn.allHeaders.collect{it.toString()}, [new BasicHeader('header1', 'value1').toString(), new BasicHeader('header2', 'value2').toString()]

        Z9HttpRequest another = Z9HttpRequest.toZ9HttpRequest(httpIn)

        assertEquals request, another
    }

    @Test
    void testMapping() {
        Z9HttpRequest request = new Z9HttpRequest(
                headers: [new Z9Header(name: 'header1', value: 'value1'), new Z9Header(name: 'header2', value: 'value2'), new Z9Header(name: 'Cookie', value: 'z9sessionid=abcd1234')],
                requestLine: new Z9RequestLine(method: 'post', uri: '/www.cnn.com', protocolVersion: new Z9ProtocolVersion(protocol: 'https', major: 1, minor: 1))
        )

        println request
        assertEquals 'abcd1234', request.z9SessionId
        def json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(request)

        println json

        Z9HttpRequest out = mapper.readValue(json, Z9HttpRequest.class)

        println out

        assertEquals request, out


        assertTrue(request.toBasicHttpRequest() instanceof BasicHttpRequest)
        assertTrue(out.toBasicHttpRequest() instanceof BasicHttpRequest)

        BasicHttpRequest httpRequest = request.toBasicHttpRequest()
        assertEquals 0, httpRequest.getHeaders("zid").length

        request.newZid = 'abc'

        httpRequest = request.toBasicHttpRequest()
        assertEquals 'abc', httpRequest.getHeaders('zid')[0].value
    }
}
