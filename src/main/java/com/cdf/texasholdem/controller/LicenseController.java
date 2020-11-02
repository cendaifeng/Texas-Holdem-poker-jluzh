package com.cdf.texasholdem.controller;

import com.cdf.texasholdem.bean.Poker;
import com.cdf.texasholdem.bean.ServerResponse;
import com.cdf.texasholdem.service.RunGame;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class LicenseController {

    private static ArrayList<Poker> list = new RunGame(null).CardInOrder();

    @RequestMapping("/license")
    public String testHtml() {
        return "playing-01";
    }

    @ResponseBody
    @PostMapping("/license")
    public ServerResponse licensing(int num) {
        ArrayList<Poker> list = this.list;
        ServerResponse serverResponse = new ServerResponse();

        System.out.println("=========== 洗牌前 ===========");
        List<String> beforeS = list.stream()
                .map(x -> x.getKind().getName() + x.getColor().getName().substring(0, 1) + ".png")
                .collect(Collectors.toList());
        System.out.println(beforeS);
        serverResponse.add("beforeShuffle", beforeS);

        System.out.println("=========== 洗牌后 ===========");
        Collections.shuffle(list);
        List<String> afterS = list.stream()
                .map(x -> x.getKind().getName() + x.getColor().getName().substring(0, 1) + ".png")
                .collect(Collectors.toList());
        System.out.println(afterS);
        serverResponse.add("afterShuffle", afterS);

        System.out.println("=========== 排序后 ===========");
        Collections.sort(list);
        List<String> afterSort = list.stream()
                .map(x -> x.getKind().getName() + x.getColor().getName().substring(0, 1) + ".png")
                .collect(Collectors.toList());
        serverResponse.add("afterSort", afterSort);
        System.out.println(afterSort);

//        for (int i = 0; i < num; i++) {
//            serverResponse.add( String.valueOf(i+1),
//                    list.subList((list.size()/num)*i, (list.size()/num)*(i+1)) );
//        }

        return serverResponse;
    }
}
