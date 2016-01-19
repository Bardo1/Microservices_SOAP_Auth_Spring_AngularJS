package com.tele2;

import com.tele2.models.dao.EnvironmentDAO;
import com.tele2.models.dao.SoapXMLDAO;
import com.tele2.models.dto.Environment;
import com.tele2.models.dto.SoapXML;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by jakhashr on 15.01.2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SoapApplication.class)
@WebAppConfiguration
public class SoapXMLTest {

    private MockMvc mockMvc;

    @Autowired
    SoapXMLDAO soapXMLDAO;

    @Autowired
    EnvironmentDAO environmentDAO;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();

        Environment environment1 = new Environment("Sweden", "SVE", "http://localhost:8080/soap");
        Environment environment2 = new Environment("Latvia", "LVI", "http://localhost:8080/soap");
        environmentDAO.save(environment1);
        environmentDAO.save(environment2);


        String xml1 = "<>{{test1}}</><>{{test2}}</>";
        SoapXML soapXML1 = new SoapXML("test1",xml1,"this is test1 description", soapXMLDAO.getPlaceholders(xml1), "some action");
        String xml2 = "<head>{{test1}}</head><body>vadfia</body>";
        SoapXML soapXML2 = new SoapXML("test2",xml2,"this is test2 description", soapXMLDAO.getPlaceholders(xml2), "some action2");

        this.soapXMLDAO.deleteAll();
        this.soapXMLDAO.save(Arrays.asList(new SoapXML[]{soapXML1, soapXML2}));
    }
    @Test
    public void test(){

    }

/*    @TestClass
    public void test() throws Exception {
        String bookmarkJson = json(new Bookmark(
                this.account, "http://spring.io", "a bookmark to the best resource for Spring news and information"));
        this.mockMvc.perform(post("/" + userName + "/bookmarks")
                .contentType(contentType)
                .content(bookmarkJson))
                .andExpect(status().isCreated());
    }

    @TestClass
    public void userNotFound() throws Exception {
        mockMvc.perform(get("/api/soap?id=999999")
                .contentType(contentType)
                .andExpect(status().isNotFound());
    }*/

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

     @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream().filter(
                hmc -> hmc instanceof MappingJackson2HttpMessageConverter).findAny().get();

        Assert.assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }




}
