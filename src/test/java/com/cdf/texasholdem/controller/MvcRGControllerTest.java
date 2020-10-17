package com.cdf.texasholdem.controller;

import com.cdf.texasholdem.bean.Msg;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

/**
 * 测试请求
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MvcRGControllerTest {

    // 传入 springmvc 的 ioc
    @Autowired
    WebApplicationContext context;
    // 虚拟 mvc 请求
    MockMvc mockMvc;

    @Before
    public void initMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void checkCreate() throws Exception {
        // 模拟请求，拿到返回值
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get("/rungame/create")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .param("tableIndex", "1"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        MvcResult result1 = mockMvc.perform(MockMvcRequestBuilders
                .post("/rungame/create")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .param("tableIndex", "1"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();





    }

}
