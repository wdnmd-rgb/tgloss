package com.loss.service;

import com.loss.entity.TgLineLoss;
import com.loss.entity.ExcConsReport;
import com.loss.entity.TgLossReport;
import com.loss.util.JdbcUtil;
import com.loss.entity.ConsEle;
import org.apache.commons.lang3.StringUtils;

import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class PearsonTest {
    public static void main(String[] args) {
        doJob("2021-12-01",7);
    }
    public static void doJob(String date,int day) {
        String sql = "select tg_no from dws_elecon.dim_cst_monitoring_tg_wf where remark = 1";
        List<String> tgList = new ArrayList();
        String[] dates = new String[day+1];
        dates[0] =date;
        try {
            ResultSet resultSet = JdbcUtil.select(sql);
            while (resultSet.next()) {
                String tgNo = resultSet.getString("tg_no");
                tgList.add(tgNo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date dateNow = null;
        try {
            dateNow = simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateNow);
        for (int i=0;i<day;i++){
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            dates[i+1] = simpleDateFormat.format(calendar.getTime());
        }
        Set<String> ids = new HashSet<>();
        Map<String, String> typeMap = new HashMap<>();
        int s = 0;
        int size  = tgList.size();
        for (String tgNo : tgList) {
            System.out.println(tgNo);
            try {
                boolean flag = check2(tgNo,date);
                if (flag){
                    System.out.println("跳过该台区");
                    s++;
                    continue;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                boolean flag = check3(tgNo,dates[(day-1)],dates[0]);
                if (flag){
                    System.out.println("跳过该台区");
                    s++;
                    continue;
                }
                String sql2 = "select rid,type_code from dws_elecon.dim_cst_record_yf where tg_no ='" + tgNo + "'";
                ResultSet resultSet = JdbcUtil.select(sql2);
                while (resultSet.next()) {
                    String id = resultSet.getString("rid");
                    String code = resultSet.getString("type_code");
                    typeMap.put(id, code);
                    ids.add(id);
                }
                if (typeMap.size() == 0){
                    continue;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                for (int i =0;i<day;i++){
                    boolean flag = check(tgNo,dates[i]);
                    if (flag){
                    }else {
                        String sql2 = "update dws_elecon.dws_cst_cons_ele_df set remark = '0' where pap_r is null and event_time like '%" + dates[i] + "%'";
                        JdbcUtil.update(sql2);
                            Map<String,Object> map = insertConsEle(tgNo,dates[i],dates[i+1]);
                            boolean flag2 = (boolean) map.get("flag");
                            if (flag2){
                                flag2 = insertTgLineLoss(dates[i],ids, (Map<String, ConsEle>) map.get("map"),tgNo,typeMap);
                                if (flag2){
                                    System.out.println(dates[i]+"数据插入完毕");
                                }
                            }
                    }

                }
                Map<String, ConsEle> consEleMap = doJob(tgNo,dates[(day-1)],dates[0]);
                Map<Long, TgLineLoss> tgLineLossMap = selectTgLoss(tgNo,dates[(day-1)],dates[0]);
                boolean flag = doJob2(dates[(day-1)],date,consEleMap,tgNo,ids,typeMap, tgLineLossMap, day);
                if (flag){
                    s++;
                    System.out.println("已完成"+s+"/"+size);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    public static boolean check(String tgNo,String date)throws Exception{
        String sql = "select count(*) as count from dws_elecon.dws_cst_cons_ele_result_df where event_time like '%"+date+"%' and rid in" +
                "(select rid from dws_elecon.dim_cst_record_yf where tg_no ='"+tgNo+"')";
        ResultSet resultSet = JdbcUtil.select(sql);
        int count = 0;
        while (resultSet.next()){
            count=resultSet.getInt("count");
        }
        if (count>0){
            return true;
        }
        return false;
    }
    public static boolean check2(String tgNo,String date)throws Exception{
        String sql = "select count(*) as count from dws_elecon.dws_cst_tg_loss_report_df where date_day_end like '%"+date+"%' and tg_no ='"+tgNo+"'";
        ResultSet resultSet = JdbcUtil.select(sql);
        int count = 0;
        while (resultSet.next()){
            count=resultSet.getInt("count");
        }
        if (count>0){
            return true;
        }
        return false;
    }

    public static boolean check3(String tgNo, String date,String dateEnd) throws Exception {
        date = date+" 00:00:00";
        dateEnd = dateEnd+" 23:00:00";
        String sql = "select count(DISTINCT rid) from dws_elecon.dim_cst_record_yf where tg_no = '"+tgNo+"'";
        String sql2 = "select count(DISTINCT rid) from dws_elecon.dws_cst_cons_ele_df where event_time between '"+date+"' and '"+dateEnd+"' and rid in " +
                "(select rid from dws_elecon.dim_cst_record_yf where tg_no = '"+tgNo+"')";
        ResultSet resultSet = JdbcUtil.select(sql);

        int count = 0;
        int count2 = 0;
        while (resultSet.next()){
            count = resultSet.getInt("count");
        }
        resultSet = JdbcUtil.select(sql2);
        while (resultSet.next()){
            count2 = resultSet.getInt("count");
        }
        if ((count-count2)>(count*0.05)){
            return true;
        }
        return false;
    }


    public static Map<String, ConsEle> doJob(String tgNo, String date,String dateEnd) throws Exception {
        date = date+" 00:00:00";
        dateEnd = dateEnd+" 23:00:00";
        String sql = "select * from dws_elecon.dws_cst_cons_ele_result_df where event_time between '"+date+"'and'"+dateEnd+"'and rid in " +
                "(select rid from dws_elecon.dim_cst_record_yf where tg_no ='"+tgNo+"')";
        ResultSet resultSet = JdbcUtil.select(sql);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map<String, ConsEle> consEleMap = new HashMap<>();
        while (resultSet.next()) {
            ConsEle consEle = new ConsEle();
            String rid = resultSet.getString("rid");
            Double t = resultSet.getDouble("t_factor");
            String date2 = resultSet.getString("event_time");
            Date date3 = simpleDateFormat.parse(date2);
            String key = rid + date3.getTime();
            consEle.setRid(rid);
            consEle.settFactor(t);
            consEle.setEventTime(date2);
            consEle.setPapR(resultSet.getDouble("pap_r"));
            consEle.setPapRDiff(resultSet.getDouble("pap_r_diff"));
            consEle.setEle(resultSet.getDouble("ele"));
            consEle.setUa(resultSet.getString("ua"));
            consEle.setUb(resultSet.getString("ub"));
            consEle.setUc(resultSet.getString("uc"));
            consEle.setIa(resultSet.getString("ia"));
            consEle.setIb(resultSet.getString("ib"));
            consEle.setIc(resultSet.getString("ic"));
            consEle.setI0(resultSet.getString("i0"));
            consEle.setPa(resultSet.getString("pa"));
            consEle.setPb(resultSet.getString("pb"));
            consEle.setPc(resultSet.getString("pc"));
            consEle.setP(resultSet.getString("p"));
            consEle.setQa(resultSet.getString("qa"));
            consEle.setQb(resultSet.getString("qb"));
            consEle.setQc(resultSet.getString("qc"));
            consEle.setQ(resultSet.getString("q"));
            consEle.setRemark(resultSet.getString("remark"));
            consEleMap.put(key,consEle);
        }
        return consEleMap;
    }

    public static Map<Long, TgLineLoss> selectTgLoss(String tgNo, String date,String dateEnd) throws Exception {
        date = date+" 00:00:00";
        dateEnd = dateEnd+" 23:00:00";
        String sql = "select * from dws_elecon.dws_cst_tg_loss_df where event_time between '"+date+"'and'"+dateEnd+"'and tg_no = '"+tgNo+"'";
        ResultSet resultSet = JdbcUtil.select(sql);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map<Long, TgLineLoss> tgLineLossMap = new HashMap<>();
        while (resultSet.next()) {
            TgLineLoss tgLineLoss = new TgLineLoss();
            String date2 = resultSet.getString("event_time");
            Date date3 = simpleDateFormat.parse(date2);
            Long time = date3.getTime();
            tgLineLoss.setEventTime(date2);
            tgLineLoss.setTgNo(resultSet.getString("tg_no"));
            tgLineLoss.setRemark(resultSet.getString("remark"));
            tgLineLoss.setLossPq(resultSet.getDouble("loss_pq"));
            tgLineLoss.setLossPer(resultSet.getDouble("loss_per"));
            tgLineLoss.setUpq(resultSet.getDouble("upq"));
            tgLineLoss.setPpq(resultSet.getDouble("ppq"));
            tgLineLossMap.put(time,tgLineLoss);
        }
        return tgLineLossMap;
    }

    public static boolean doJob2(String date, String dateEnd,Map<String, ConsEle> consEleMap, String tgNo,Set<String> ids,Map<String, String> typeMap,Map<Long,TgLineLoss> tgLineLossMap,int day) throws Exception {
        System.out.println("起始时间："+date+" 终止时间："+dateEnd);
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        List<ExcConsReport> excConsReportList = new ArrayList<>();
        TgLossReport tgLossReport = new TgLossReport();
        tgLossReport.setTgNo(tgNo);
        tgLossReport.setDateDayStart(date);
        tgLossReport.setDateDayEnd(dateEnd);
        String date2 = date+" 00:00:00";
        String dateEnd2 = dateEnd+" 23:00:00";
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = simpleDateFormat1.parse(date);
        Long time = date1.getTime();
        int index = 3;
        int sum = 0;
        String sql = "select sum(loss_pq) as sum,sum(ppq) as ppq,sum(upq) as upq from dws_elecon.dws_cst_tg_loss_df \n" +
                "where tg_no = '"+tgNo+"' and event_time between '"+date2+"' and '"+dateEnd2+"'";
        ResultSet resultSet = JdbcUtil.select(sql);
        while (resultSet.next()){
            Double lossPq = resultSet.getDouble("sum");
            Double ppq2 = resultSet.getDouble("ppq");
            Double avg = 0.0;
            if (ppq2 != 0.0){
                avg = lossPq/ppq2;
            }
            tgLossReport.setLossEle(lossPq);
            tgLossReport.setLossPerAvg(Double.valueOf(decimalFormat.format(avg)));
            tgLossReport.setPpq(ppq2);
            tgLossReport.setUpq(resultSet.getDouble("upq"));
        }
        sql = "select tg_type_name,tg_class,merge_lineloss_rate,b.*,c.city_no,c.county_no,c.org_no,d.fz_ry_name from buf_khbq_khxf.line_loss_class_report_detail a,(\n" +
                "select tg_no,tg_name,count(*) from dws_elecon.dim_cst_record_yf where tg_no = '"+tgNo+"' group by tg_no,tg_name)b ,dws_elecon.dim_cst_relation_yf c,buf_amr_sea.tx_tq_zb_new d\n" +
                "where a.tg_no = b.tg_no and a.tg_no=c.tg_no and a.stat_month = (select max(stat_month) from \n" +
                "buf_khbq_khxf.line_loss_class_report_detail) and a.tg_no = d.tg_no order by d.stat_date desc limit 1";
        resultSet = JdbcUtil.select(sql);
        while (resultSet.next()){
            tgLossReport.setFzRyName(resultSet.getString("fz_ry_name"));
            tgLossReport.setCityNo(resultSet.getString("city_no"));
            tgLossReport.setCountyNo(resultSet.getString("county_no"));
            tgLossReport.setOrgNo(resultSet.getString("org_no"));
            tgLossReport.setTgTypeName(resultSet.getString("tg_type_name"));
            tgLossReport.setTgClass(resultSet.getString("tg_class"));
            tgLossReport.setMergeLinelossRate(resultSet.getString("merge_lineloss_rate"));
            tgLossReport.setTgName(resultSet.getString("tg_name"));
            tgLossReport.setConsCount(resultSet.getString("count"));
        }
        for (String rid : ids){
            String code = typeMap.get(rid);
            if ("02".equals(code)){
                continue;
            }
            double[] upq = new double[((day*24)/index)-1];
            double[] ppq = new double[((day*24)/index)-1];
            String[] times = new String[((day*24)/index)-1];
            double sumEle=0.0;
            double ele = 0.0;
            double eleLoss = 0.0;
            for (int i = 1,j=0; i < (day*24); i++) {
                Long time1 = time + (i * 900000 * 4);
                String key = rid + time1;
                ConsEle consEle = consEleMap.get(key);
                if(consEle==null){
                    continue;
                }
                TgLineLoss tgLineLoss = tgLineLossMap.get(time1);
                sumEle+=Double.valueOf(decimalFormat.format(consEle.getEle()));
                ele+= consEle.getEle();
                eleLoss+= tgLineLoss.getLossPq();
                if (i%index == 0){
                    upq[j] = Double.valueOf(decimalFormat.format(ele));
                    ppq[j] = Double.valueOf(decimalFormat.format(eleLoss));
                    times[j] = consEle.getEventTime();
                    ele = 0.0;
                    eleLoss = 0.0;
                    j++;
                }
            }
            Map<String,Object> objectMap = Similarity.test(upq, ppq);
            double score = (double) objectMap.get("score");
            int index2 = (int) objectMap.get("index");
            if (Math.abs(score)>=0.7){
                String sql2 = "select count(*) from dws_elecon.dws_cst_cons_ele_result_df where rid = '"+rid+"'and event_time between '"+date2+"' and '"+dateEnd2+"' and (remark='0' or remark='3')";
                int cons_point = 0;
                ResultSet resultSet2 = JdbcUtil.select(sql2);
                while (resultSet2.next()){
                    cons_point = resultSet2.getInt("count");
                }
                int point = (day*24)-1;
                if (cons_point>(point*0.1)){
                    continue;
                }
                sum++;
                ExcConsReport excConsReport = new ExcConsReport();
                excConsReport.setPearson(score);
                excConsReport.setRid(rid);
                excConsReport.setDateDayEnd(dateEnd);
                excConsReport.setDateDayStart(date);
                excConsReport.setTgNo(tgNo);
                sql2 = "select cons_no,cons_name,asset_no,cons_addr from dws_elecon.dim_cst_record_yf where rid = '"+rid+"';";
                resultSet2 = JdbcUtil.select(sql2);
                while (resultSet2.next()){
                    excConsReport.setConsAddr(resultSet2.getString("cons_addr"));
                    excConsReport.setConsNo(resultSet2.getString("cons_no"));
                    excConsReport.setConsName(resultSet2.getString("cons_name"));
                    excConsReport.setAssetNo(resultSet2.getString("asset_no"));
                }
                switch (index2){
                    case 0:excConsReport.setMaxIndex("00:00 - 12:00");break;
                    case 1:excConsReport.setMaxIndex("03:00 - 15:00");break;
                    case 2:excConsReport.setMaxIndex("06:00 - 18:00");break;
                    case 3:excConsReport.setMaxIndex("09:00 - 21:00");break;
                    case 4:excConsReport.setMaxIndex("12:00 - 次日00:00");break;
                    case 5:excConsReport.setMaxIndex("15:00 - 次日03:00");break;
                    case 6:excConsReport.setMaxIndex("18:00 - 次日06:00");break;
                    case 7:excConsReport.setMaxIndex("21:00 - 次日09:00");break;
                }
                excConsReport.setEle(Double.valueOf(decimalFormat.format(sumEle)));
                excConsReport.setEleArray(Arrays.toString(upq));
                excConsReport.setTgEleArray(Arrays.toString(ppq));
                excConsReport.setTimeArray(Arrays.toString(times));
                excConsReportList.add(excConsReport);
            }
        }
        tgLossReport.setExcConsCount(sum);
        System.out.println(tgLossReport);
        boolean flag = JdbcUtil.insertConsReport(excConsReportList);
        if (flag){
            flag = JdbcUtil.insertTgLossReport(tgLossReport);
        }
        return flag;
    }
    public static boolean insertTgLineLoss(String date, Set<String> ids, Map<String, ConsEle> consEleMap, String tgNo, Map<String, String> typeMap) throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
        List<TgLineLoss> tgLineLosses = new ArrayList<>();
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        Date date1 = simpleDateFormat1.parse(date);
        Long time = date1.getTime();
        time = time - (900000 * 4);
        for (int i = 1; i <= 24; i++) {
            Long time1 = time + (i * 900000 * 4);
            String date2 = simpleDateFormat.format(new Date(time1));
            Double ppq = 0.0;
            Double upq = 0.0;
            int sum = 0;
            for (String rid : ids) {
                String key = rid + time1;
                ConsEle consEle = consEleMap.get(key);
                if (consEle == null) {
                    continue;
                }
                String code = typeMap.get(rid);
                String remark = consEle.getRemark();
                if ("3".equals(remark) || "0".equals(remark)) {
                    sum++;
                }
                Double ele = consEle.getEle();
                if ("02".equals(code)) {
                    ppq += ele;
                } else {
                    upq += ele;
                }
            }
            Double lossPq = 0.0;
            Double lossPer = 0.0;
            if (ppq == 0.0) {
                lossPq = ppq - upq;
                lossPer = 0.0;
            } else {
                lossPq = ppq - upq;
                lossPer = lossPq / ppq;
            }
            TgLineLoss tgLineLoss = new TgLineLoss();
            tgLineLoss.setPpq(Double.valueOf(decimalFormat.format(ppq)));
            tgLineLoss.setUpq(Double.valueOf(decimalFormat.format(upq)));
            tgLineLoss.setTgNo(tgNo);
            tgLineLoss.setEventTime(date2);
            tgLineLoss.setRemark(sum + "");
            tgLineLoss.setLossPq(Double.valueOf(decimalFormat.format(lossPq)));
            tgLineLoss.setLossPer(Double.valueOf(decimalFormat.format(lossPer)));
            tgLineLosses.add(tgLineLoss);
        }
        boolean flag = JdbcUtil.insertTg(tgLineLosses);
        return flag;
    }
    public static Map<String,Object>  insertConsEle(String tgNo, String date,String dateBefore) throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String sql1 = "select rid from dws_elecon.dim_cst_record_yf where tg_no ='" + tgNo + "'";
        ResultSet resultSet = JdbcUtil.select(sql1);
        Map<String, ConsEle> consEleMap = new HashMap<>();
        Map<String,Object> map = new HashMap<>();
        map.put("flag",false);
        List<String> idsList = new ArrayList<>();
        while (resultSet.next()) {
            String id = resultSet.getString("rid");
            idsList.add(id);
        }
        int count = idsList.size();
        String ids = StringUtils.join(Arrays.asList(idsList.toArray()), ",");
        String sql2 = "select count(DISTINCT rid) as sum from dws_elecon.dws_cst_cons_ele_df where event_time like '%" + date + "%' and rid in (" + ids + ")";
        String testsql = "select count(DISTINCT rid) as sum from dws_elecon.dws_cst_cons_ele_result_df where event_time like '%" + date + "%' and rid in (" + ids + ")";
        int count2 = 0;
        int count3 = 0;
        ResultSet resultSet1 = JdbcUtil.select(sql2);
        while (resultSet1.next()) {
            count2 = resultSet1.getInt("sum");
        }
        resultSet1 = JdbcUtil.select(testsql);
        while (resultSet1.next()) {
            count3 = resultSet1.getInt("sum");
        }
        if(count3>0){
            System.out.println("跳过");
            return map;
        }
        if (count2==0){
            return map;
        }
        System.out.println(tgNo + "台区用户数：" + count);
        System.out.println("实际用户数：" + count2);
        int loss = count - count2;
        if (loss > 2) {
            System.out.println("缺失用户超过2户");
        }
        String sql = "select * from dws_elecon.dws_cst_cons_ele_df where (event_time  like'%" + date+ "%'or event_time = '"+(dateBefore+" 23:00:00")+"') and rid in (" + ids + ")";
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map<String, Double> stringDoubleMap = new HashMap<>();
        try {
            ResultSet resultSet3 = JdbcUtil.select(sql);
            while (resultSet3.next()) {
                ConsEle consEle = new ConsEle();
                String rid = resultSet3.getString("rid");
                Double t = resultSet3.getDouble("t_factor");
                String date2 = resultSet3.getString("event_time");
                Date date3 = simpleDateFormat1.parse(date2);
                String key = rid + date3.getTime();
                stringDoubleMap.put(rid, t);
                consEle.setRid(rid);
                consEle.settFactor(t);
                consEle.setEventTime(date2);
                consEle.setPapR(resultSet3.getDouble("pap_r"));
                consEle.setUa(resultSet3.getString("ua"));
                consEle.setUb(resultSet3.getString("ub"));
                consEle.setUc(resultSet3.getString("uc"));
                consEle.setIa(resultSet3.getString("ia"));
                consEle.setIb(resultSet3.getString("ib"));
                consEle.setIc(resultSet3.getString("ic"));
                consEle.setI0(resultSet3.getString("i0"));
                consEle.setPa(resultSet3.getString("pa"));
                consEle.setPb(resultSet3.getString("pb"));
                consEle.setPc(resultSet3.getString("pc"));
                consEle.setP(resultSet3.getString("p"));
                consEle.setQa(resultSet3.getString("qa"));
                consEle.setQb(resultSet3.getString("qb"));
                consEle.setQc(resultSet3.getString("qc"));
                consEle.setQ(resultSet3.getString("q"));
                consEle.setRemark(resultSet3.getString("remark"));
                consEle.setRapR(resultSet3.getDouble("rap_r"));
                consEleMap.put(key, consEle);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        map =  doJob5(consEleMap, stringDoubleMap, date);
        return map;
    }
    public static Map<String,Object> doJob5(Map<String, ConsEle> map, Map<String, Double> stringDoubleMap, String date) throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        Date date1 = simpleDateFormat1.parse(date);
        Long time = date1.getTime();
        time = time - (900000 * 4);
        List<ConsEle> consEles = new ArrayList<>();
        for (String rid : stringDoubleMap.keySet()) {
            Double papRp = 0.0;
            Double t = stringDoubleMap.get(rid);
            for (int i = 0; i <= 24; i++) {
                Long time1 = time + (i * 900000 * 4);
                String key = rid + time1;
                ConsEle consEle = map.get(key);
                if (consEle != null) {
                    String remark = consEle.getRemark();
                    Double papR = consEle.getPapR();
                    Double ele = 0.0;
                    if ((papR == null) ||("0").equals(remark)) {
                        double result[] = supplement(i, time, rid, map);
                        papR = result[0];
                        Double papRDiff = result[1];
                        ele = papRDiff * t;
                        consEle.setPapR(papR);
                        consEle.setPapRDiff(Double.valueOf(decimalFormat.format(papRDiff)));
                        consEle.setEle(Double.valueOf(decimalFormat.format(ele)));
                    }else{
                        if (i!=0){
                            ele = (papR - papRp) * t;
                            if (papRp==0.0){
                                ele=0.0;
                                consEle.setPapRDiff(0.0);
                                consEle.setEle(0.0);
                            }else {
                                consEle.setEle(Double.valueOf(decimalFormat.format(ele)));
                                consEle.setPapRDiff(Double.valueOf(decimalFormat.format((papR - papRp))));
                            }
                        }else{
                            consEle.setEle(0.0);
                            consEle.setPapRDiff(0.0);
                        }
                    }
                    if (papR > 0) {
                        papRp = consEle.getPapR();
                    }
                } else {
                    consEle = new ConsEle();
                    consEle.setRid(rid);
                    String date2 = simpleDateFormat.format(new Date(time1));
                    consEle.setEventTime(date2);
                    consEle.setRemark("3");
                    double result[] = supplement(i, time, rid, map);
                    Double papR = result[0];
                    Double papRDiff = result[1];
                    if (papRDiff<0){
                        papR=0.0;
                        papRDiff=0.0;
                    }
                    consEle.setPapR(papR);
                    consEle.setPapRDiff(Double.valueOf(decimalFormat.format(papRDiff)));
                    consEle.setEle(Double.valueOf(decimalFormat.format(papRDiff*t)));
                    consEle.settFactor(t);
                    papRp = papR;
                }
                if (i != 0){
                    consEles.add(consEle);
                }
                map.put(key,consEle);
            }
        }
        boolean flag =false;
        int num[] = JdbcUtil.insert(consEles);
        for (int i = 0; i < num.length; i++) {
            if (num[i] <= 0) {
                flag = false;
            }else {
                flag=true;
            }
        }
        Map<String,Object> map1 = new HashMap<>();
        map1.put("map",map);
        map1.put("flag",flag);
        return map1;
    }
    public static Map<String, Object> findPre(int i, Long time, String rid, Map<String, ConsEle> map) {
        Map<String, Object> map1 = new HashMap();
        Double papRP = 0.0;
        for (; i >= 0; i--) {
            Long time1 = time + i * 900000 * 4;
            String key = rid + time1;
            ConsEle consEle = map.get(key);
            if (consEle != null) {
                Double papR = consEle.getPapR();
                if(papR != null){
                    papRP=papR;
                    break;
                }
            }
        }
        map1.put("index", Integer.valueOf(i));
        map1.put("papRP", papRP);
        return map1;
    }

    public static Map<String, Object> findNext(int i, Long time, String rid, Map<String, ConsEle> map) {
        Map<String, Object> map1 = new HashMap();
        Double papRN = 0.0;
        for (; i <= 24; i++) {
            Long time2 = time + i * 900000 * 4;
            String key = rid + time2;
            ConsEle consEle = map.get(key);
            if (consEle != null) {
                Double papR = consEle.getPapR();
                String remark = consEle.getRemark();
                if(papR != null && papR != 0.0&& !("0".equals(remark))){
                    papRN=papR;
                    break;
                }
            }
        }
        map1.put("index", Integer.valueOf(i));
        map1.put("papRN", papRN);
        return map1;
    }

    public static double[] supplement(int i, Long time, String rid, Map<String, ConsEle> map) {
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        Double papR = 00.00;
        Double papRDiff = 00.00;
        int sum = findPoint(time,rid,map);
        if (sum<3){
            return new double[]{papR, papRDiff};
        }
        if (i == 0) {
            Map<String, Object> map1 = findNext((i + 1), time, rid, map);
            Double papRN = (Double) map1.get("papRN");
            int index = (int) map1.get("index");
            map1 = findNext((index + 1), time, rid, map);
            Double papRN2 = (Double) map1.get("papRN");
            int index2 = (int) map1.get("index");
            Double diff = Double.valueOf(decimalFormat.format((papRN2 - papRN) / (index2 - index)));
            diff = index * diff;
            papR = papRN - diff;
            papR = Double.valueOf(decimalFormat.format(papR));
            if ((index==25)||(index2==25)){
                papR = 0.0;
            }
        } else {
            Map<String, Object> map1 = findNext((i + 1), time, rid, map);
            int index = (int) map1.get("index");
            if (index <= 24) {
                Map<String, Object> map3 = findPre((i - 1), time, rid, map);
                int index2 = (int) map3.get("index");
                Double papRN = (Double) map1.get("papRN");
                Double papRP = (Double) map3.get("papRP");
                Double diff = Double.valueOf(decimalFormat.format((papRN - papRP) / (index - index2)));
                papR = papRP + diff;
                papR = Double.valueOf(decimalFormat.format(papR));
                papRDiff = papR - papRP;
                papRDiff = Double.valueOf(decimalFormat.format(papRDiff));
            } else {
                Map<String, Object> map3 = findPre((i - 1), time, rid, map);
                int index2 = (int) map3.get("index");
                Map<String, Object> map4 = findPre((index2 - 1), time, rid, map);
                Double papRP = (Double) map3.get("papRP");
                Double papRP2 = (Double) map4.get("papRP");
                Double diff = papRP - papRP2;
                if (i==1){
                    papR = papRP;
                    papRDiff=0.0;
                }else{
                    diff = Double.valueOf(decimalFormat.format(diff));
                    papR = papRP + diff;
                    papR = Double.valueOf(decimalFormat.format(papR));
                    papRDiff = papR - papRP;
                    papRDiff = Double.valueOf(decimalFormat.format(papRDiff));
                }

            }
        }
        double result[] = {papR, papRDiff};
        return result;
    }

    public static int findPoint(Long time, String rid, Map<String, ConsEle> map){
        int sum=0;
        for (int i=0; i <= 24; i++) {
            Long time2 = time + i * 900000 * 4;
            String key = rid + time2;
            ConsEle consEle = map.get(key);
            if (consEle != null) {
                Double papR = consEle.getPapR();
                String remark = consEle.getRemark();
                if(papR != null && papR != 0.0&& !("0".equals(remark))){
                    sum++;
                }
            }
        }
        return sum;
    }
}
