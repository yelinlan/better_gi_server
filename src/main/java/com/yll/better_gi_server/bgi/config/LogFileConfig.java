package com.yll.better_gi_server.bgi.config;

import com.yll.better_gi_server.bgi.controller.enums.EventEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 日志文件配置
 */
@Component
@ConfigurationProperties(prefix = "log.file")
@Data
public class LogFileConfig {

	private String path = "C:\\Users\\Administrator\\Desktop\\auto\\";
	private String[] startScript = new String[]{"C:\\Program Files\\BetterGI\\BetterGI.exe startOneDragon"};
	private String[] endScript = new String[]{"TASKKILL /F /IM BetterGI.exe", "shutdown /s /t 60"};
	private long closeSecond = 3;
	private EventEnum[] event = new EventEnum[]{EventEnum.DRAGON_END};

}
