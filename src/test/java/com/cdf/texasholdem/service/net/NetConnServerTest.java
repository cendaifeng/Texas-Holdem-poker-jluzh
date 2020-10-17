package com.cdf.texasholdem.service.net;

import com.cdf.texasholdem.service.net.NetClient;
import com.cdf.texasholdem.service.net.NetConnServer;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class NetConnServerTest {

    @Test
    void serverTest() throws IOException {
        new NetConnServer(9901);
    }

    @Test
    void clientTest() throws IOException {
        new NetClient(9901, 0, "小虎");
    }

}