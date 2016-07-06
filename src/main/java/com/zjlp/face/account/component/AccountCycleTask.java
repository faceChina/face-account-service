package com.zjlp.face.account.component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zjlp.face.account.service.AccountService;
import com.zjlp.face.util.file.PropertiesUtil;
@Component
public class AccountCycleTask {
	private Logger log=LoggerFactory.getLogger(getClass());
	@Autowired
	private AccountService accountService;
	static ScheduledExecutorService executor=Executors.newSingleThreadScheduledExecutor();
	{	
		log.info("AccountCycleTask创建成功!!");
		String serverId=PropertiesUtil.getContexrtParam("server.id");
		log.info("server.id:"+serverId);
		if(serverId==null || "2".equals(serverId)){
			executor.scheduleWithFixedDelay(new Runnable() {
				@Override
				public void run() {
					try{
						Long amount=accountService.operationRecordTask();
						log.info("AccountCycleTask开始执行,平台金额变动:"+amount);
					}catch(Exception e){
						log.error(e.getMessage());
					}
				}
				
			}, 1, 2, TimeUnit.MINUTES);
		}
	}
	
}
