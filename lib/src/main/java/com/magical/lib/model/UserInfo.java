package com.magical.lib.model;

import java.io.Serializable;

/**
 * Created by laimo.li on 2018/2/26.
 */

public class UserInfo implements Serializable {

    public interface LoginType {

        int TYPE_MOBILE = 1;
        int TYPE_WX = 2;
        int TYPE_QQ = 3;
    }

    public String id;
    public String ucid;
    public String nickname;
    public String openid;
    public String unionid;
    public int gender = -1;
    public String avatar;
    public String signature;//个性签名
    public String province;
    public String city;
    public int isCompleted;
    public int age;
    public String constellatory;
    public String birthday;
    public String phone;
    public int level;
    public int cons;
    public int winsPoint;
    public String createTime;
    public String updateTime;
    public int platform;        //登录方式：1乐逗用户中心(手机或帐号)，2 Wechat，3 QQ，4 Email
    public int userType;
    public String sign;//校验签名 用于H5
    public String token;
    public String gameToken;
    public int nowLevelExp;//当前exp
    public int nextLevelExp;//升级需要的总exp
    public int nextLevelRate;//当前进度条显示
    public String levelName;//等级名称
    public String winsLevelIcon;//勋章icon
    public int firstLogin;
    public String loginProcess;
    public String winsLevelName;
    public int friendStatus;    //服务端并没有给 本地记的
    public int achvCount;
    public int charm;
    int entryInhibited;
    int testers;

    public boolean isMale() {
        return gender == 0;
    }

    /**
     * 是否完善了资料
     */
    public boolean isComplete() {
        //return false;
        return isCompleted == 1;
    }

    /**
     * 是否需要选择街区
     */
    public boolean isNeedSelectStreet() {
        //return firstLogin <= 0;
        return false;
    }

    /**
     * 是否需要播放欢迎动画
     */
    public boolean isNeedWelcomeAnim() {
        return null == loginProcess || !loginProcess.contains("4");
    }

    /**
     * 转化为性别代码
     *
     * @param isMale 是否男性
     * @return 性别: 0、男，1、女
     */
    public int toGenderCode(boolean isMale) {
        return isMale ? 0 : 1;
    }

    /**
     * 是否需要选择标签
     *
     * @return true : 跳兴趣选择页 false : 下一个判断
     */
    public boolean isNeedSelectTag() {
        return null == loginProcess || !loginProcess.contains("1");
    }

    /**
     * 是否需要玩游戏
     *
     * @return true : 跳玩游戏页 false : 下一个判断
     */
    public boolean isNeedPlayGame() {
        return null == loginProcess || !loginProcess.contains("2");
    }

    /**
     * 是否为机器人
     *
     * @return true:机器人
     */
    public boolean isBot() {
        return this.userType == 1;
    }
}
