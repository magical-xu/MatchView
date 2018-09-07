package com.magical.lib.model;

public class MatchInfo {

    public static final int START_TYPE_LIST = 0;//从游戏列表中进入
    public static final int START_TYPE_ACTIVITY = 1;//从活动中进入
    public static final int START_TYPE_CHAT = 2;//从聊天卡片进入
    public static final int START_TYPE_STREET = 3;//从玩一把进入
    public static final int START_TYPE_GAMBLE = 4;//从赏金模式进入

    /**
     * bot : 1
     * game : {"gameId":"1","gameUrl":""}
     * from : {"avatar":"hahahah","gender":"2","uid":"123"}
     * to : {"avatar":"hahahahhahahah","gender":"1","uid":"456"}
     */

    private String bot;//当局是否有机器人。0：否，1：是
    private UserInfo from;
    private UserInfo to;
    private String gamePkId;//匹配成功唯一标示
    public int mode;    //模式
    public long totalCoin;   //总金币

    public boolean isRewardMode() {
        return mode == START_TYPE_GAMBLE;
    }

    public String getGamePkId() {
        return gamePkId;
    }

    public void setGamePkId(String gamePkId) {
        this.gamePkId = gamePkId;
    }

    public boolean isBot() {
        if (bot != null && bot.equals("1")) {
            return true;
        }
        return false;
    }

    public void setBot(String bot) {
        this.bot = bot;
    }

    public UserInfo getFrom() {
        return from;
    }

    public void setFrom(UserInfo from) {
        this.from = from;
    }

    public UserInfo getTo() {
        return to;
    }

    public void setTo(UserInfo to) {
        this.to = to;
    }
}
