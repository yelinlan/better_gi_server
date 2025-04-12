package com.yll.better_gi_server.bgi.controller.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EventEnum {
    NOTIFY_TEST("notify.test", "测试通知"),
    DOMAIN_REWARD("domain.reward", "自动秘境奖励"),
    DOMAIN_START("domain.start", "自动秘境启动"),
    DOMAIN_END("domain.end", "自动秘境结束"),
    DOMAIN_RETRY("domain.retry", "自动秘境重试"),
    TASK_CANCEL("task.cancel", "任务启动"),
    TASK_ERROR("task.error", "任务错误"),
    GROUP_START("group.start", "配置组启动"),
    GROUP_END("group.end", "配置组结束"),
    DRAGON_START("dragon.start", "一条龙启动"),
	DRAGON_END("dragon.end", "一条龙结束"),
    TCG_START("tcg.start", "七圣召唤启动"),
    TCG_END("tcg.end", "七圣召唤结束"),
    ALBUM_START("album.start", "自动音游专辑启动"),
    ALBUM_END("album.end", "自动音游专辑结束"),
    ALBUM_ERROR("album.error", "自动音游专辑错误"),
	DAILY_REWARD("daily.reward", "检查到每日奖励未领取，请手动查看！");

    private final String code;
    private final String description;

	//判断枚举是否存在
	public static boolean isExist(String code) {
		for (EventEnum eventEnum : EventEnum.values()) {
			if (eventEnum.getCode().equals(code)) {
				return true;
			}
		}
		return false;
	}

	//判断枚举是否存在并且为传入枚举
	public static int index(String code, EventEnum[] enums) {
		for (int i = 0; i < enums.length; i++) {
			if (enums[i].getCode().equals(code)) {
				return i;
			}
		}
		return -1;
	}


}
