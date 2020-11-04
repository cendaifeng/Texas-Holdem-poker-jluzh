package com.cdf.texasholdem.service;

import com.cdf.texasholdem.bean.*;
import com.cdf.texasholdem.mapper.PersonMapper;
import com.cdf.texasholdem.utils.GameUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
public class RunGame {

    @Autowired
    PersonMapper personMapper;

    protected Board board;

    protected ArrayList<Poker> cards;

    protected CopyOnWriteArrayList<Person> players;
    // 最小跟注限制
    private int callLimited = 0;

    private int tableIndex = 0;

    private String gameStatus = null;

    private boolean isRun = false;

    private Person winner = null;
    // RunGameController.begin() 调用时定义
    private ArrayBlockingQueue<Person> playerQueue = null;
    // this.run() 时定义
    private ArrayBlockingQueue<String> gameStatusQueue = null;

    /**
     * 以下字段用于 PlayerContoller 对牌桌的游戏状态监听
     */
    // 专用于状态监听，玩家在弃牌后不会被删除
    private CopyOnWriteArrayList<Person> listenerArrayList = null;
    // nextPlayer()时置为true。在所有玩家接受完状态变更信息后置为false
    private boolean ifMsgChange = false;

    private String playerOperation = null;

    private Integer operationValue = null;

    private String playerName;

    private CountDownLatch countDownLatch = null;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    public RunGame() {
        this.players = new CopyOnWriteArrayList<Person>();
        this.board = new Board();
        this.cards = CardOutOfOrder();
    }

    // for test
    public RunGame(CopyOnWriteArrayList<Person> players) {
        this.players = players;
        this.board = new Board();
        this.cards = CardOutOfOrder();
    }

    public void RunGameReset() {
        players = new CopyOnWriteArrayList<Person>();
        board = new Board();
        cards = CardOutOfOrder();
        callLimited = 0;
        playerQueue = null;
        gameStatus = null;
        listenerArrayList = null;
        winner = null;
        isRun = false;
    }

    public CopyOnWriteArrayList<Person> getListenerArrayList() {
        return listenerArrayList;
    }

    public void setListenerArrayList(CopyOnWriteArrayList<Person> listenerArrayList) {
        this.listenerArrayList = listenerArrayList;
    }

    public boolean isRun() {
        return isRun;
    }

    public void setRun(boolean run) {
        isRun = run;
    }

    public Person getWinner() {
        return winner;
    }

    public void setWinner(Person winner) {
        this.winner = winner;
    }

    public String getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(String gameStatus) {
        this.gameStatus = gameStatus;
    }

    public ArrayBlockingQueue<Person> getPlayerQueue() {
        return playerQueue;
    }

    public void setPlayerQueue(ArrayBlockingQueue<Person> playerQueue) {
        this.playerQueue = playerQueue;
    }

    public Board getBoard() {
        return board;
    }

    public CopyOnWriteArrayList<Person> getPlayers() {
        return players;
    }

    public void addPlayer(Person player) {
        players.add(player);
    }

    public int getCallLimited() {
        return callLimited;
    }

    public void setCallLimited(int wager) {
        this.callLimited = wager;
    }

    public void setTableIndex(int tableIndex) {
        this.tableIndex = tableIndex;
    }

    public boolean isMsgChange() {
        return ifMsgChange;
    }

    public void setIfMsgChange(boolean ifMsgChange) {
        this.ifMsgChange = ifMsgChange;
    }

    public CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }

    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    public String getPlayerOperation() {
        return playerOperation;
    }

    public void setPlayerOperation(String playerOperation) {
        this.playerOperation = playerOperation;
    }

    public Integer getOperationValue() {
        return operationValue;
    }

    public void setOperationValue(Integer operationValue) {
        this.operationValue = operationValue;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    /**
     * 由 RunGameController 中的 begin() 调用
     */
    public void run() {

        this.gameStatusQueue = new ArrayBlockingQueue<String>(5, false, Arrays.asList(
                "begin","flop","turn","river","over"));
        try {
            setGameStatus(gameStatusQueue.take());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.licensingToPlayer();

        // 开启一条新线程检测是否直接来到over状态
        CompletableFuture<Person> future = CompletableFuture.supplyAsync(() -> {
            while (gameStatus != "over") {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return this.compareValue();
        });
        // 注册事件“监听”
        future.whenComplete((v, e) -> {
			System.out.println(v);
			System.out.println(e);
            try {
                Person person = future.get();  // 阻塞
                setWinner(person);
                person.setBankRoll(person.getBankRoll() + board.getPot());
                personMapper.updateBankRoll(person);
                logger.trace("game over! the winner is "+person.toString());
                this.RunGameReset();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        /** Flop */
        while (gameStatus != "flop" && gameStatus != "over") {
        }
        this.initBoardCards();
        Poker[] boardCards = board.getBoardCards();
        System.out.println(boardCards[0]+"\n"+boardCards[1]+"\n"+boardCards[2]);

        /** Turn */
        while (gameStatus != "turn" && gameStatus != "over") {
        }
        this.addBoardCards();
//        System.out.println(board.getBoardCards()[3]);

        /** River */
        while (gameStatus != "river" && gameStatus != "over") {
        }
        this.addBoardCards();
//        System.out.println(board.getBoardCards()[4]);
        return;
    }

    /**
     * 切换到 下一位选手 或 下一回合
     * 若队列中的元素已经取尽，则判断游戏是否应该进入下一阶段
     * 调用ArrayBlockingQueue的阻塞等待出队入队方法
     * 将出队元素的status设为true
     * @param playerOperation 玩家操作
     * @param operationValue 操作数值
     * 该信息作为变更信息供Controller进行获取，传递给前端
     */
    public void nextPlayer(String playerName, String playerOperation, Integer operationValue) {
        this.playerName = playerName;
        this.playerOperation = playerOperation;
        this.operationValue = operationValue;
        ServerResponse serverResponse = new ServerResponse();

        logger.debug("nextPlayer");

        ServerResponse newServerResponse = buildResponse(serverResponse);
        players.stream().map(x -> x.getMsgQueue()).forEach(x -> {
            try {
                x.put(newServerResponse.setCode(100));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

//        countDownLatch = new CountDownLatch(listenerArrayList.size());
//        ifMsgChange = true;
//        try {
//            countDownLatch.await();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        try {
            // 若玩家队列已用尽，则搜寻玩家列表中尚未完成下注要求的玩家
            if (playerQueue.size() == 0) {
                Iterator<Person> i = players.iterator();
                while (i.hasNext()) {
                    Person next = i.next();
                    if (next.getCurrentWager()<callLimited && !next.isAllin()) {
                        playerQueue.put(next);
                    }
                }
                if (playerQueue.size() == 0)
                    nextRound();
            }
            // 若玩家队列没有用尽，则取出一个，置其状态为true
            if (playerQueue.size() > 0) {
                Person person = playerQueue.take();
                logger.debug("P:"+person.toString());
                person.setStatus(true);
                //this.playerQueue.put(person);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private ServerResponse buildResponse(ServerResponse serverResponse) {
        Integer pot = this.getBoard().getPot();
        System.out.println("======================"+pot+"===================");
        if (pot != null) {
            serverResponse.add("pot", pot);
        }

        Poker[] boardCards = this.getBoard().getBoardCards();
        if (boardCards[0] != null) {
            System.out.println("======================"+boardCards[0]+"===================");
            List<String> cardsList = Arrays.stream(boardCards)
                    .map(x -> x.getKind().getName() + x.getColor().getName().substring(0, 1) + ".png")
                    .collect(Collectors.toList());
            serverResponse.add("boardCards", cardsList);
        }

//        System.out.println("======================"+playerName+"===================");
//        if (playerName != null) {
//            serverResponse.add("playerName", playerName);
//        }
//
//        System.out.println("======================"+playerOperation+"===================");
//        if (playerOperation != null) {
//            serverResponse.add("playerOperation", playerOperation);
//        }
//
//        System.out.println("======================"+operationValue+"===================");
//        if (operationValue != null) {
//            serverResponse.add("operationValue", operationValue);
//        }
//
//        if (this.getWinner() != null)
//            serverResponse.add("winner", this.getWinner());

        return serverResponse;
    }

    private void nextRound() {
        logger.debug("nextRound");
        if (players.size() == 1) {
            setGameStatus("over");
            gameStatusQueue.clear();
        }
        if (gameStatusQueue.size() > 0) {
            try {
                String status = gameStatusQueue.take();
                setGameStatus(status);
                playerQueue.addAll(players);
                logger.debug("now gameStatus: "+status);
                nextPlayer(null, null, null);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 每人发两张牌，并展示
     * 输入包含所有玩家的列表，在Run对象中获取洗过的一副牌，
     */
    public void licensingToPlayer() {
        for (Person p : players) {
            p.getPokers()[0] = cards.remove(0);
            p.getPokers()[1] = cards.remove(0);
        }
        // for test
        players.stream().map( x -> Arrays.asList(x.getPokers()) )
                .forEach(System.out::println);
    }

    /**
     * 翻牌Flop
     * 发出首三张公共牌(在board中的poker[])
     */
    public void initBoardCards() {
        board.Flop(cards);
        this.callLimited = 0;
    }

    /**
     * 转牌Turn 或者 河牌River
     * 加入一张牌(在board中的poker[])
     */
    public void addBoardCards() {
        board.addCard(cards.remove(0));
        this.callLimited = 0;
    }

    /**
     * 玩家下注
     * 包含 Bet押注，Call跟注，Raise加注，All-in全押
     * @param index 下注玩家序列号
     * @param wager 下注金额
     * @return 若下注数目不满足要求返回 false
     */
    public boolean playerBet(int index, int wager) {
        Person player = this.players.get(index);
        // 检验是否在玩家回合
        if (!player.getStatus())
            return false;
        if ( player.getBankRoll() >= wager ) {
            player.setBankRoll(player.getBankRoll() - wager);
            personMapper.updateBankRoll(player);
            this.board.addPot(wager);
            // 新最小跟注限制 = 下注 + 已下注金额
            this.callLimited = wager + player.getCurrentWager();
            player.setCurrentWager(wager + player.getCurrentWager());
            return true;
        } else {
            return false;
        }
    }

    /**
     * 玩家下注（不检验玩家回合）
     * 用于初始化时大小盲的下注
     * @param index 下注玩家序列号
     * @param wager 下注金额
     * @return 若下注数目不满足要求返回 false
     */
    public boolean playerBetWithoutCheck(int index, int wager) {
        Person player = this.players.get(index);
        if ( player.getBankRoll() >= wager ) {
            player.setBankRoll(player.getBankRoll() - wager);
            personMapper.updateBankRoll(player);
            this.board.addPot(wager);
            // 新最小跟注限制 = 下注 + 已下注金额
            this.callLimited = wager + player.getCurrentWager();
            player.setCurrentWager(wager + player.getCurrentWager());
            return true;
        } else {
            return false;
        }
    }

    /**
     * 玩家弃牌
     * @param index 下注玩家序列号
     * @return 是否成功
     */
    public boolean playerFold(int index) {
        Person player = null;
        try {
            player = this.players.get(index);
            //this.getPlayerQueue().remove(player);
        } catch (Exception e) {
            logger.error("playerFold失败，可能是重复提交请求");
        }
        return this.players.remove(player);
    }

    /**
     * 输入玩家列表，将每个玩家的卡片与台面上的卡片结合
     * 计算各个牌值，返回最大牌值所对应的 Person
     * @return 胜利者
     */
    public Person compareValue() {
        // 这里的 HashMap 旨在排序牌组大小，并依其值来找到所属 Person
        // 所以 key 为 cardValue, value 为 Person
        HashMap<Long, Person> map = new HashMap<>();
        for (Person p : players) {
            ArrayList<Poker> pokers = new ArrayList<>(Arrays.asList(p.getPokers()));
            pokers.addAll( Arrays.asList(board.getBoardCards()) );
            long value = GameUtil.cardValue(pokers);
            map.put(value, p);
        }
        Long aLong = map.keySet().stream().sorted((o1, o2) -> (int) (o2 - o1)).findFirst().get();
        Person winner = map.get(aLong);

        ////////// 流式计算 HashMap 取不出值 ///////////////////////
//        HashMap<ArrayList<Poker>, Person> map2 = new HashMap<>();
//        ArrayList<Poker> pokerArrayList = people.stream().map(x -> {
//                    ArrayList<Poker> pokerList = new ArrayList<Poker>(Arrays.asList(x.getPokers()));
//                    pokerList.addAll(new ArrayList<Poker>(Arrays.asList(board.getBoardCards())));
//                    map2.put(pokerList, x);
//                    return pokerList;
//                }
//        ).sorted((o1, o2) -> (int) -(GameUtil.cardValue(o1) - GameUtil.cardValue(o2)))
//                .findFirst().get();
//        Person winner2 = map2.get(pokerArrayList);
        ///////////////////////////////////////////////////////////
        return winner;
    }

    /**
     * 利用枚举类型 Kind 和 Color 组合遍历出所有的卡牌组合
     * @return 按顺序排列的一副卡牌
     */
    public static ArrayList<Poker> CardInOrder() {
        ArrayList<Poker> pokers = new ArrayList<>(52);
        for (Kind k : Kind.values()) {
            for (Color c : Color.values()) {
                pokers.add(new Poker(k, c));
            }
        }
        return pokers;
    }

    /**
     * 调用容器类 shuffle() 方法将顺序卡牌打乱
     * @return 乱序卡牌
     */
    public static ArrayList<Poker> CardOutOfOrder() {
        ArrayList<Poker> pokers = CardInOrder();
        Collections.shuffle(pokers);
        Collections.shuffle(pokers);
        return pokers;
    }
}
