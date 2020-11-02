package com.cdf.texasholdem.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

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
                .param("tableIndex", "1"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        MvcResult result1 = mockMvc.perform(MockMvcRequestBuilders
                .post("/rungame/create")
                .param("tableIndex", "1"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

}
