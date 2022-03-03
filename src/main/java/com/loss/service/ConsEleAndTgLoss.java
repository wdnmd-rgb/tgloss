package com.loss.service;

import com.loss.entity.ConsEle;
import com.loss.entity.TgLineLoss;
import com.loss.util.JdbcUtil;
import org.apache.commons.lang3.StringUtils;

import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class ConsEleAndTgLoss {
    public static void main(String[] args) {
        try {
            doJob("2021-08-26");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void doJob(String date) throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date dateNow = simpleDateFormat.parse(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateNow);
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        String dateBefore = simpleDateFormat.format(calendar.getTime());
        String sql = "select tg_no from dws_elecon.dim_cst_monitoring_tg_wf where remark = '1'";
        String sqlUpdate = "update dws_elecon.dws_cst_cons_ele_df set remark='0' where pap_r is null and event_time like '%"+date+"%'";
        JdbcUtil.update(sqlUpdate);
        List<String> tgList = new ArrayList();
        ResultSet resultSet = JdbcUtil.select(sql);
        while (resultSet.next()) {
            String tgNo = resultSet.getString("tg_no");
            tgList.add(tgNo);
        }
        for (String tgNo : tgList) {
            Map<String, String> typeMap = new HashMap<>();
            Set<String> ids = new HashSet<>();
            String sql2 = "select rid,type_code from dws_elecon.dim_cst_record_yf where tg_no ='" + tgNo + "'";
            resultSet = JdbcUtil.select(sql2);
            while (resultSet.next()) {
                String id = resultSet.getString("rid");
                String code = resultSet.getString("type_code");
                typeMap.put(id, code);
                ids.add(id);
            }
            Map<String, Object> map = insertConsEle(tgNo, date, dateBefore);
            boolean flag = (boolean) map.get("flag");
            if (flag) {
                flag = insertTgLineLoss(date, ids, (Map<String, ConsEle>) map.get("map"), tgNo, typeMap);
                if (flag) {
                    System.out.println(tgNo + " " + date + "数据插入完毕");
                }
            }

        }

    }

    public static boolean insertTgLineLoss(String
                                                   date, Set<String> ids, Map<String, ConsEle> consEleMap, String tgNo, Map<String, String> typeMap) throws
            Exception {
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

    public static Map<String, Object> insertConsEle(String tgNo, String date, String dateBefore) throws Exception {
        String sql1 = "select rid from dws_elecon.dim_cst_record_yf where tg_no ='" + tgNo + "'";
        ResultSet resultSet = JdbcUtil.select(sql1);
        Map<String, ConsEle> consEleMap = new HashMap<>();
        Map<String, Object> map = new HashMap<>();
        map.put("flag", false);
        List<String> idsList = new ArrayList<>();
        while (resultSet.next()) {
            String id = resultSet.getString("rid");
            idsList.add(id);
        }
        int count = idsList.size();
        if(count == 0){
            System.out.println("ids size 为 0");
            return map;
        }
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
        if (count3 > 0) {
            System.out.println("跳过");
            return map;
        }
        if (count2 == 0) {
            return map;
        }
        System.out.println(tgNo + "台区用户数：" + count);
        System.out.println("实际用户数：" + count2);
        int loss = count - count2;
        if (loss > 2) {
            System.out.println("缺失用户超过2户");
        }
        String sql = "select * from dws_elecon.dws_cst_cons_ele_df where (event_time  like'%" + date + "%'or event_time = '" + (dateBefore + " 23:00:00") + "') and rid in (" + ids + ")";
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
        map = doJob5(consEleMap, stringDoubleMap, date);
        return map;
    }

    public static Map<String, Object> doJob5
            (Map<String, ConsEle> map, Map<String, Double> stringDoubleMap, String date) throws Exception {
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
                    if ((papR == null) || ("0").equals(remark)) {
                        double result[] = supplement(i, time, rid, map);
                        papR = result[0];
                        Double papRDiff = result[1];
                        ele = papRDiff * t;
                        consEle.setPapR(papR);
                        consEle.setPapRDiff(Double.valueOf(decimalFormat.format(papRDiff)));
                        consEle.setEle(Double.valueOf(decimalFormat.format(ele)));
                    } else {
                        if (i != 0) {
                            ele = (papR - papRp) * t;
                            if (papRp == 0.0) {
                                consEle.setPapRDiff(0.0);
                                consEle.setEle(0.0);
                            } else {
                                consEle.setEle(Double.valueOf(decimalFormat.format(ele)));
                                consEle.setPapRDiff(Double.valueOf(decimalFormat.format((papR - papRp))));
                            }
                        } else {
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
                    if (papRDiff < 0) {
                        papR = 0.0;
                        papRDiff = 0.0;
                    }
                    consEle.setPapR(papR);
                    consEle.setPapRDiff(Double.valueOf(decimalFormat.format(papRDiff)));
                    consEle.setEle(Double.valueOf(decimalFormat.format(papRDiff * t)));
                    consEle.settFactor(t);
                    papRp = papR;
                }
                if (i != 0) {
                    consEles.add(consEle);
                }
                map.put(key, consEle);
            }
        }
        boolean flag = false;
        int num[] = JdbcUtil.insert(consEles);
        for (int i = 0; i < num.length; i++) {
            if (num[i] <= 0) {
                flag = false;
            } else {
                flag = true;
            }
        }
        Map<String, Object> map1 = new HashMap<>();
        map1.put("map", map);
        map1.put("flag", flag);
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
                if (papR != null) {
                    papRP = papR;
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
                if (papR != null && papR != 0.0 && !("0".equals(remark))) {
                    papRN = papR;
                    break;
                }
            }
        }
        map1.put("index", Integer.valueOf(i));
        map1.put("papRN", papRN);
        return map1;
    }

    //补点方法
    public static double[] supplement(int i, Long time, String rid, Map<String, ConsEle> map) {
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        Double papR = 00.00;
        Double papRDiff = 00.00;
        int sum = findPoint(time, rid, map);
        if (sum < 3) {
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
            if ((index == 25) || (index2 == 25)) {
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
                if (i == 1) {
                    papR = papRP;
                    papRDiff = 0.0;
                } else {
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

    public static int findPoint(Long time, String rid, Map<String, ConsEle> map) {
        int sum = 0;
        for (int i = 0; i <= 24; i++) {
            Long time2 = time + i * 900000 * 4;
            String key = rid + time2;
            ConsEle consEle = map.get(key);
            if (consEle != null) {
                Double papR = consEle.getPapR();
                String remark = consEle.getRemark();
                if (papR != null && papR != 0.0 && !("0".equals(remark))) {
                    sum++;
                }
            }
        }
        return sum;
    }
}
