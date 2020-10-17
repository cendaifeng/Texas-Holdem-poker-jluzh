package com.cdf.texasholdem.utils;

import com.cdf.texasholdem.bean.Color;
import com.cdf.texasholdem.bean.Kind;
import com.cdf.texasholdem.bean.Poker;

import java.util.*;
import java.util.stream.Collectors;

public class GameUtil {

    static final long STRAIGHT_FLUSH = 900000000;
    static final long FOUR_OF_A_KIND = 800000000;
    static final long FULL_HOUSE = 700000000;
    static final long FLUSH = 600000000;
    static final long STRAIGHT = 500000000;
    static final long THREE_OF_A_KIND = 400000000;
    static final long TWO_PAIR = 300000000;
    static final long ONE_PAIR = 200000000;

    /**
     * 输出所符合的牌型的值
     * @param pokers 5-7张卡牌
     * @return 牌值
     */
    public static long cardValue(ArrayList<Poker> pokers) {

        HashMap<Kind, Integer> sameKindMap = sameKindMap(pokers);
        pokers.sort((o1, o2) -> (int) - (o1.getValue()-o2.getValue()));


        /** STRAIGHT_FLUSH */
        Poker straight_flush = ifStraight_Flush(pokers);
        if (straight_flush != null) {
//            System.out.println("/** STRAIGHT_FLUSH */");
            return STRAIGHT_FLUSH + straight_flush.getValue()*10000;
        }


        /** FOUR_OF_A_KIND */
        if (sameKindMap.containsValue(4)) {
            Kind kind = getSameKind(sameKindMap, 4).get(0);
            Poker pFalse = null, pTrue = null;
            for (Poker p : pokers) {
                // pokers 已排序
                if (pTrue == null && p.getKind() == kind)
                    pTrue = p;
                if (pFalse == null && p.getKind() != kind)
                    pFalse = p;
                if (pTrue != null && pFalse != null) {
//                    System.out.println("/** FOUR_OF_A_KIND */");
                    return FOUR_OF_A_KIND + pTrue.getValue()*10000 + pFalse.getValue()*100;
                }
            }
        }


        /** FULL_HOUSE */
        ArrayList<Kind> kindList3 = getSameKind(sameKindMap, 3);
        ArrayList<Kind> kindList2 = getSameKind(sameKindMap, 2);
        if (sameKindMap.containsValue(3) && sameKindMap.containsValue(2)) {
            Poker p3 = null, p2 = null;
            for (Poker p : pokers) {
                if (p3 == null && p.getKind() == kindList3.get(0)) {
                    p3 = p;
                }
                if (p2 == null && p.getKind() == kindList2.get(0)) {
                    p2 = p;
                }
            }
//            System.out.println("/** FULL_HOUSE */");
            // 对子在骷髅中所占权值较小
            return FULL_HOUSE + p3.getValue()*10000 + p2.getValue()*100;
        }
        if (kindList3.size() == 2) {
            // 7 张牌中有两组 3 张
            Poker p3 = null, p2 = null;
            for (Poker p : pokers) {
                if (p3 == null && p.getKind() == kindList3.get(0))
                    p3 = p;
                if (p2 == null && p.getKind() == kindList3.get(1))
                    p2 = p;
            }
//            System.out.println("/** FULL_HOUSE */");
            return FULL_HOUSE + p3.getValue()*10000 + p2.getValue()*100;
        }


        /** FLUSH */
        Color flushColor = ifFlush(pokers);
        if (flushColor != null) {
            // 在 pokers 中过滤出同花花色的 卡片列表
            List<Poker> flushList = pokers.stream()
                    .filter(x -> x.getColor() == flushColor).collect(Collectors.toList());
            Poker poker = flushList.get(0);
//            System.out.println("/** FLUSH */");
            return FLUSH + poker.getValue()*10000;
        }


        /** STRAIGHT */
        Poker poker = ifStraight(pokers);
        if (poker != null) {
//            System.out.println("/** STRAIGHT */");
            return STRAIGHT + poker.getValue();
        }


        /** THREE_OF_A_KIND */
        if (sameKindMap.containsValue(3)) {
            Poker pTrue = null, pFalse1 = null, pFalse2 = null;
            for (Poker p : pokers) {
                if (pTrue == null && p.getKind() == kindList3.get(0))
                    pTrue = p;
                if (pFalse1 == null && p.getKind() != kindList3.get(0))
                    pFalse1 = p;
                if (pFalse2 == null && p.getKind() != kindList3.get(0))
                    pFalse2 = p;
            }
//            System.out.println("/** THREE_OF_A_KIND */");
            return THREE_OF_A_KIND + pTrue.getValue()*10000 + pFalse1.getValue()*100 + pFalse2.getValue();
        }


        /** TWO_PAIR */
        if (kindList2.size() >= 2) {
            // 7 张牌中有两组以上 2 张
            Poker p1 = null, p2 = null, pFalse = null;
            for (Poker p : pokers) {
                if (p1 == null && p.getKind() == kindList2.get(0)) {
                    p1 = p;
                    continue;
                }
                if (p2 == null && p.getKind() == kindList2.get(1)) {
                    p2 = p;
                    continue;
                }
                if (pFalse == null)
                    pFalse = p;
            }
//            System.out.println("/** TWO_PAIR */");
            return TWO_PAIR + p1.getValue()*10000 + p2.getValue()*100 + pFalse.getValue();
        }


        /** ONE_PAIR */
        if (kindList2.size() == 1) {
            Poker p1 = null, pFalse1 = null, pFalse2 = null, pFalse3 = null;
            for (Poker p : pokers) {
                if (p1 == null && p.getKind() == kindList2.get(0)) {
                    p1 = p;
                    continue;
                }
                if (pFalse1 == null) {
                    pFalse1 = p;
                    continue;
                }
                if (pFalse2 == null) {
                    pFalse2 = p;
                    continue;
                }
                if (pFalse3 == null) {
                    pFalse3 = p;
                }
            }
//            System.out.println("/** ONE_PAIR */");
            return ONE_PAIR + p1.getValue()*10000 + pFalse1.getValue()*100 + pFalse2.getValue() + pFalse3.getValue()/100;
        }


        /** HIGH_CARD */
        Iterator<Poker> iterator = pokers.subList(0, 5).iterator();
        long v1 = iterator.next().getValue();
        long v2 = iterator.next().getValue();
        long v3 = iterator.next().getValue();
        long v4 = iterator.next().getValue();
        long v5 = iterator.next().getValue();
//        System.out.println("/** HIGH_CARD */");
        return v1*10000 + v2*100 + v3 + v4/100 + v5/10000;
    }

    /**
     * 接收 sameKindMap() 返回的 map 和 所需的相同牌型数目
     * 得到符合该数目的 牌型列表
     * @param sameKindMap HashMap<Kind, Integer> for one kind cards
     * @param numOfCards 相同牌型的数目
     * @return 牌型列表
     */
    static ArrayList<Kind> getSameKind(HashMap<Kind, Integer> sameKindMap, int numOfCards) {
        Iterator<Map.Entry<Kind, Integer>> i = sameKindMap.entrySet().iterator();
        ArrayList<Kind> kindList = new ArrayList<>();
        while (i.hasNext()) {
            Map.Entry<Kind, Integer> entry = i.next();
            if (entry.getValue() == numOfCards)
                kindList.add( entry.getKey() );
        }
        return kindList;
    }


    /**
     * 判断是否为同花顺
     * @param pokers 玩家手牌和台面公牌结合的列表
     * @return 若为同花顺返回 同花顺组合中最大的一张卡牌，否则返回 null
     */
    static Poker ifStraight_Flush(ArrayList<Poker> pokers) {
        Color color = ifFlush(pokers);
        if (color != null && ifStraight(pokers) != null) {
            // 在 pokers 中过滤出同花花色的 卡片列表
            List<Poker> flushList = pokers.stream()
                    .filter(x -> x.getColor() == color).collect(Collectors.toList());
            Poker straightPoker = ifStraight((ArrayList<Poker>) flushList);
            if (straightPoker != null) {
                Kind kind = straightPoker.getKind();
                return new Poker(kind, color);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * 判断是否为同花
     * @param pokers 玩家手牌和台面公牌结合的列表
     * @return 若为同花返回 该花色，否则返回 null
     */
    static Color ifFlush(ArrayList<Poker> pokers) {
        HashMap<Color, Integer> map = new HashMap<>();
        for (Poker x : pokers) {
            if (map.get(x.getColor()) == null) {
                map.put(x.getColor(), 1);
            } else {
                map.put(x.getColor(), map.get(x.getColor()).intValue()+1);
            }
        }
        Iterator<Map.Entry<Color, Integer>> i = map.entrySet().iterator();
        /* 88000:3900000 40倍速度差异
        Map<Color, Long> map = pokers.stream()
                .collect(Collectors.groupingBy(Poker::getColor, Collectors.counting()));
        */
        while (i.hasNext()) {
            Map.Entry<Color, Integer> next = i.next();
            if (next.getValue() >= 5){
                return next.getKey();
            }
        }
        return null;
    }

    /**
     * 判断是否为顺子
     * @param pokers 玩家手牌和台面公牌结合的列表
     * @return 若为顺子返回 顺子组合中最大的一张卡牌，否则返回 null
     */
    static Poker ifStraight(ArrayList<Poker> pokers) {
        int straightMax = 1, straight = 1;
        Poker poker = null;
        // 从大到小排好顺序
        pokers.sort((o1, o2) -> (int) - (o1.getValue()-o2.getValue()));
        for (int i = 0; i < pokers.size()-1; i++) {
            if (pokers.get(i).getKind().ordinal() == pokers.get(i+1).getKind().ordinal() + 1) {
                straight++;
            } else if (pokers.get(i).getKind() == pokers.get(i+1).getKind()) {
                continue;
            } else if (straight>straightMax) {
                poker = pokers.get(i - (straight-1));
                straightMax = straight;
                straight = 1;
            }
        }
        // 最后两张牌也为顺子的情况
        if (straight>straightMax) {
            poker = pokers.get((pokers.size()-1) - (straight-1));
            straightMax = straight;
        }
        if (straightMax >= 5) {
            return poker;
        } else {
            return null;
        }
    }

    /**
     * 找出“一对”“两对”“三条”等 one kind cards，放入 map 中
     * 每一种 牌型 为一个 key， value 为数目；最终只需遍历出 value 超过 1 的 key-value
     * @param pokers 玩家手牌和台面公牌结合的列表
     * @return HashMap<Kind, Integer>
     */
    static HashMap<Kind, Integer> sameKindMap(ArrayList<Poker> pokers) {
        HashMap<Kind, Integer> map = new HashMap<>();
        pokers.sort((o1, o2) -> (int) - (o1.getValue()-o2.getValue()));
        for (int i = 0; i < pokers.size(); i++) {
            if (null != map.get(pokers.get(i).getKind())) {
                map.put(pokers.get(i).getKind(), map.get(pokers.get(i).getKind()).intValue()+1);
            } else {
                map.put(pokers.get(i).getKind(), 1);
            }
        }
        // 抽取出 value 大于 1 的 key-value
        Map<Kind, Integer> collect = map.entrySet().stream()
                .filter(x -> x.getValue() > 1)
                .collect(Collectors.toMap(x -> x.getKey(), x -> x.getValue()));
        return (HashMap<Kind, Integer>) collect;
    }

    /**
     * 暂时弃用
     * 将输入的卡牌组合成 5 张最大的组合，并输出它的大小
     * @param pokers 5-7张卡牌
     * @return 最大组合值
     */
    public static long cardValue_7_to_5 (ArrayList<Poker> pokers) {
        int testsum = 0;
        long maxValue = 0;
        for (int i = 0; i < pokers.size()-1; i++) {
            for (int j = i+1; j < pokers.size(); j++) {
                ArrayList<Poker> arr = new ArrayList<>();
                arr.addAll(pokers);
                arr.remove(i);
                arr.remove(j);
                long value_5 = cardValue(arr);
                if (maxValue < value_5)
                    maxValue = value_5;
                testsum++;
            }
        }
        System.out.println(testsum);
        return maxValue;
    }

}
