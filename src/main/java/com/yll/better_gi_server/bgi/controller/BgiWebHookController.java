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
	public String test(@RequestBody WebHookReq webHookReq) throws IOException, InterruptedException {
		if (EventEnum.isExist(webHookReq.getEvent())) {
			String ymd = DateUtil.format(DateUtil.date(), logFileConfig.getTimeStamp());
			String file = logFileConfig.getPath() + ymd + logFileConfig.getSuffix();
			//记录日志
			FileUtil.appendUtf8String(webHookReq.getTimestamp() + ": " + JSONUtil.toJsonPrettyStr(webHookReq) + "\n",
					file);
			log.info("收到事件！{}", JSONUtil.toJsonPrettyStr(webHookReq));
			int index = EventEnum.index(webHookReq.getEvent(), logFileConfig.getEvent());
			if (index != -1) {
				if (index == logFileConfig.getEvent().length - 1) {
					if (webHookReq.getResult().equals("0")) {
						log.info(logFileConfig.getEvent()[index].getDescription() + " 执行完毕，恢复音量！");
						Runtime.getRuntime().exec("nircmd.exe mutesysvolume 0");
					} else {
						//生成一个执行失败日志文件
						FileUtil.rename(new File(file),
								ymd + "_failed_" + index + "_" + logFileConfig.getEvent()[index].getDescription()
										+ logFileConfig.getSuffix(), true);
					}
					exit();
				} else {
					if (webHookReq.getResult().equals("0")) {
						log.info(  "【{}】 执行完毕！",logFileConfig.getEvent()[index].getDescription());
						log.info(  "【{}】 开始执行！",logFileConfig.getEvent()[index + 1].getDescription());
					} else {
						//生成一个执行失败日志文件
						FileUtil.rename(new File(file),
								ymd + "_failed_" + index + "_" + logFileConfig.getEvent()[index].getDescription()
										+ logFileConfig.getSuffix(), true);
					}
					Runtime.getRuntime().exec(logFileConfig.getEndScript()[0]);
					//睡眠3000ms 保证BGI被关闭
					Thread.sleep(3000);
					//启动BGI 下一条脚本
					Runtime.getRuntime().exec(logFileConfig.getStartScript()[index+1]);
				}
			}
			if (!webHookReq.getResult().equals("0")) {
				//生成一个执行失败日志文件
				FileUtil.rename(new File(file), ymd + "_failed" + logFileConfig.getSuffix(), true);
				FileUtil.appendUtf8String(
						webHookReq.getTimestamp() + ": " + JSONUtil.toJsonPrettyStr(webHookReq) + "\n",
						logFileConfig.getPath() + ymd + "_failed" + logFileConfig.getSuffix());
				Runtime.getRuntime().exec(logFileConfig.getEndScript()[1]);
			}
		} else {
			//记录日志
			FileUtil.appendUtf8String(webHookReq.getTimestamp() + ": " + JSONUtil.toJsonPrettyStr(webHookReq) + "\n",
					logFileConfig.getPath() + "unknown_event" + logFileConfig.getSuffix());
		}
		if ( webHookReq.getEvent().contains("error") ){
			exit();
		}
		return "test";
	}

	private void exit() throws IOException, InterruptedException {
		Runtime.getRuntime().exec(logFileConfig.getEndScript()[1]);
		//睡眠3000ms 保证BGI被关闭
		Thread.sleep(3000);
		//启动BGI 下一条脚本
		cancelShutdown();
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