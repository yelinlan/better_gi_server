package com.yll.better_gi_server.bgi.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import com.yll.better_gi_server.bgi.config.LogFileConfig;
import com.yll.better_gi_server.bgi.controller.enums.EventEnum;
import com.yll.better_gi_server.bgi.controller.req.WebHookReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;

/**
 *@项目名称: better_gi_server
 *@类名称: BgiWebHookController
 *@类描述:
 *@创建人: yll
 *@创建时间: 2025/2/13 19:36
 **/
@RequestMapping("/")
@RestController
@Slf4j
@RequiredArgsConstructor
public class BgiWebHookController {

	private final LogFileConfig logFileConfig;

	@RequestMapping("/shutdown")
	public String test(@RequestBody WebHookReq webHookReq) throws IOException {
		if (EventEnum.isExist(webHookReq.getEvent())) {
			String ymd = DateUtil.format(DateUtil.date(), logFileConfig.getTimeStamp());
			String file = logFileConfig.getPath() + ymd + logFileConfig.getSuffix();
			//记录日志
			FileUtil.appendUtf8String(webHookReq.getTimestamp() + ": " + JSONUtil.toJsonPrettyStr(webHookReq) + "\n",
					file);
			log.info("收到事件！{}", JSONUtil.toJsonPrettyStr(webHookReq));
			if (EventEnum.isExistAndEqual(webHookReq.getEvent(), logFileConfig.getEvent())) {
				if (webHookReq.getResult().equals("Success")) {
					log.info(logFileConfig.getEvent().getDescription() + " 执行，准备关机！");
				} else {
					//生成一个执行失败日志文件
					FileUtil.rename(new File(file), ymd + "_failed" + logFileConfig.getSuffix(), true);
				}
				Runtime.getRuntime().exec(logFileConfig.getEndScript());
				//按下任意键后就执行取消关机命令
				cancelShutdown();
			}
			if (!webHookReq.getResult().equals("Success")) {
				//生成一个执行失败日志文件
				FileUtil.rename(new File(file), ymd + "_failed" + logFileConfig.getSuffix(), true);
				FileUtil.appendUtf8String(
						webHookReq.getTimestamp() + ": " + JSONUtil.toJsonPrettyStr(webHookReq) + "\n",
						logFileConfig.getPath() + ymd + "_failed" + logFileConfig.getSuffix());
				Runtime.getRuntime().exec(logFileConfig.getEndScript());
			}
		} else {
			//记录日志
			FileUtil.appendUtf8String(webHookReq.getTimestamp() + ": " + JSONUtil.toJsonPrettyStr(webHookReq) + "\n",
					logFileConfig.getPath() + "unknown_event" + logFileConfig.getPath());
		}
		return "test";
	}

	private void cancelShutdown() {
		new Thread(() -> {
			try {
				//按下任意键后就执行取消关机命令
				log.info("按下任意键取消关机！");
				System.in.read();
				Runtime.getRuntime().exec("shutdown -a");
				log.info("{}s后退出应用！", logFileConfig.getCloseSecond());
				Thread.sleep(logFileConfig.getCloseSecond() * 1000);
				System.exit(0);
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}).start();
	}
}