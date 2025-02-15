package com.yll.better_gi_server.bgi.config;

import com.yll.better_gi_server.bgi.controller.enums.EventEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "log.file")
@Data
public class LogFileConfig {

	private String path = "C:\\Users\\Administrator\\Desktop\\auto\\";
	private String suffix = ".log";
	private String timeStamp = "yyyyMMdd";
	private String startScript = "C:\\Program Files\\BetterGI\\BetterGI.exe startOneDragon";
	private String endScript = "shutdown -s -t 60";
	private long closeSecond = 3;
	private String ip = "localhost";
	private EventEnum event = EventEnum.DRAGON_END;

}
