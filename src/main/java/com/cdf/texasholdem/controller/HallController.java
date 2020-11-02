package com.cdf.texasholdem.controller;

import com.cdf.texasholdem.service.RunGame;
import com.cdf.texasholdem.service.RunGameManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class HallController {

    /**
     * 查询牌桌数据
     * @return 转发到 hall 页面
     */
    @RequestMapping("/rungame/list")
    public String getRungames(Model model) {

        ArrayList<Integer> list = new ArrayList<Integer>();
        // for test front
        list.add(1);
        list.add(2);
        list.add(5);

        ConcurrentHashMap<Integer, RunGame> runGameMap = RunGameManager.getRunGameHashMap();
        Enumeration<Integer> keys = runGameMap.keys();
        for (int i = 0; i < 10; i++) {
            if (keys.hasMoreElements()) {
                list.add(keys.nextElement());
            }
        }
        model.addAttribute("num", runGameMap.size());
        model.addAttribute("pageList", list);
        return "hall";
    }

    @RequestMapping("/rungame/test")
    public String testHtml() {
        return "playing";
    }
}
