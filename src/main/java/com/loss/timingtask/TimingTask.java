package com.loss.timingtask;

import com.loss.service.ConsEleAndTgLoss;
import com.loss.service.PearsonTest;
import com.loss.util.JdbcUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


@Component
public class TimingTask {
//    @Scheduled(cron = "0/1 * * * * ?")
    @Scheduled(cron = "0 0 8 * * ?")
    public void getLeidianData()  {

        Calendar ca = Calendar.getInstance();//得到一个Calendar的实例
        ca.setTime(new Date()); //设置时间为当前时间

        ca.add(Calendar.DATE, -1);
        Date yesterday = ca.getTime(); //结果
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sf.format(yesterday);
        System.out.println(date);
        for(int i = 0 ; i < 1;i++){
            try {
                ConsEleAndTgLoss.doJob(date);
                PearsonTest.doJob(date,7);
            } catch (Exception e) {
                e.printStackTrace();
            }
            JdbcUtil.resetConn();
            try {
                Thread.sleep(60 * 60 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

}
