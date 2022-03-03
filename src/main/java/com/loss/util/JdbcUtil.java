package com.loss.util;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.loss.entity.*;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

public class JdbcUtil {
    public static Connection conn;
    static PreparedStatement ps;
    static DataSource dataSource;

    static {
        Properties prop = new Properties();
        try {
            String CONF_DIR = System.getProperty("user.dir")+ File.separator;
            String path = CONF_DIR+"database2.properties";
            prop.load(new FileInputStream(path));
            dataSource = DruidDataSourceFactory.createDataSource(prop);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getConnection() {
        try {
            if (conn == null) {
                conn = dataSource.getConnection();
                conn.setAutoCommit(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void resetConn(){
        conn=null;
    }

    public static int update(String sql)
            throws Exception {
        getConnection();
        ps = conn.prepareStatement(sql);
        int num = ps.executeUpdate();
        return num;
    }

    public static int[] insertMax(List<MaxEle> list) throws SQLException {
        getConnection();
        String sql = "insert into dws_elecon.dws_cst_cons_ele_max_df(rid,event_time,ele) values(?,?,?)";
        ps = conn.prepareStatement(sql);
        int size = list.size();
        for (int i = 0; i < size; i++) {
            MaxEle maxEle = list.get(i);
            ps.setString(1, maxEle.getRid());
            ps.setString(2, maxEle.getEventTime());
            ps.setDouble(3, maxEle.getEle());
            ps.addBatch();
        }
        int[] num = ps.executeBatch();
        conn.commit();
        return num;

    }

    public static boolean insertTg(List<TgLineLoss> list) throws SQLException {
        getConnection();
        String sql = "insert into dws_elecon.dws_cst_tg_loss_df(tg_no,ppq,upq,loss_pq,loss_per,event_time,remark) values(?,?,?,?,?,?,?)";
        ps = conn.prepareStatement(sql);
        int size = list.size();
        for (int i = 0; i < size; i++) {
            TgLineLoss tgLineLoss = list.get(i);
            ps.setString(1, tgLineLoss.getTgNo());
            ps.setDouble(2, tgLineLoss.getPpq());
            ps.setDouble(3, tgLineLoss.getUpq());
            ps.setDouble(4, tgLineLoss.getLossPq());
            ps.setDouble(5, tgLineLoss.getLossPer());
            ps.setString(6, tgLineLoss.getEventTime());
            ps.setString(7, tgLineLoss.getRemark());
            ps.addBatch();
        }
        int[] num = ps.executeBatch();
        for (int i = 0; i < num.length; i++) {
            if (num[i] == 0) {
                return false;
            }
        }
        conn.commit();
        return true;

    }

    public static boolean insertConsReport(List<ExcConsReport> list) throws SQLException {
        getConnection();
        String sql = "insert into dws_elecon.dws_cst_exc_cons_report_df(cons_no,cons_name,cons_addr,asset_no,rid,pearson,ele,max_index,ele_array,tg_ele_array,date_day_start,date_day_end,tg_no,time_array) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        ps = conn.prepareStatement(sql);
        int size = list.size();
        for (int i = 0; i < size; i++) {
            ExcConsReport excConsReport = list.get(i);
            ps.setString(1, excConsReport.getConsNo());
            ps.setString(2, excConsReport.getConsName());
            ps.setString(3, excConsReport.getConsAddr());
            ps.setString(4, excConsReport.getAssetNo());
            ps.setString(5, excConsReport.getRid());
            ps.setDouble(6, excConsReport.getPearson());
            ps.setDouble(7, excConsReport.getEle());
            ps.setString(8, excConsReport.getMaxIndex());
            ps.setString(9, excConsReport.getEleArray());
            ps.setString(10, excConsReport.getTgEleArray());
            ps.setString(11, excConsReport.getDateDayStart());
            ps.setString(12, excConsReport.getDateDayEnd());
            ps.setString(13, excConsReport.getTgNo());
            ps.setString(14, excConsReport.getTimeArray());
            ps.addBatch();
        }
        int[] num = ps.executeBatch();
        for (int i = 0; i < num.length; i++) {
            if (num[i] == 0) {
                return false;
            }
        }
        conn.commit();
        return true;

    }

    public static boolean insertTgLossReport(TgLossReport tgLossReport) throws SQLException {
        getConnection();
        String sql = "insert into dws_elecon.dws_cst_tg_loss_report_df(tg_no,tg_name,cons_count,date_day_start,date_day_end,loss_per_avg,loss_ele,tg_type_name,tg_class,merge_lineloss_rate,exc_cons_count,rela_day_count,city_no,county_no,org_no,fz_ry_name,ppq,upq) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        ps = conn.prepareStatement(sql);
        ps.setString(1, tgLossReport.getTgNo());
        ps.setString(2, tgLossReport.getTgName());
        ps.setString(3, tgLossReport.getConsCount());
        ps.setString(4, tgLossReport.getDateDayStart());
        ps.setString(5, tgLossReport.getDateDayEnd());
        ps.setDouble(6, tgLossReport.getLossPerAvg());
        ps.setDouble(7, tgLossReport.getLossEle());
        ps.setString(8, tgLossReport.getTgTypeName());
        ps.setString(9, tgLossReport.getTgClass());
        ps.setString(10, tgLossReport.getMergeLinelossRate());
        ps.setInt(11, tgLossReport.getExcConsCount());
        ps.setInt(12, tgLossReport.getRelaDayCount());
        ps.setString(13, tgLossReport.getCityNo());
        ps.setString(14, tgLossReport.getCountyNo());
        ps.setString(15, tgLossReport.getOrgNo());
        ps.setString(16, tgLossReport.getFzRyName());
        ps.setDouble(17,tgLossReport.getPpq());
        ps.setDouble(18,tgLossReport.getUpq());
        int num = ps.executeUpdate();
        if (num>0){
            return true;
        }
        conn.commit();
        return false;

    }

    public static int insertTgReport(TgReport tgReport) throws SQLException {
        getConnection();
        String sql = "insert into dws_elecon.dws_cst_tg_report_df(tg_no,ppq,upq,loss_pq,loss_per,date_day,count,real_count) values(?,?,?,?,?,?,?,?)";
        ps = conn.prepareStatement(sql);
        ps.setString(1, tgReport.getTgNo());
        ps.setDouble(2, tgReport.getPpq());
        ps.setDouble(3, tgReport.getUpq());
        ps.setDouble(4, tgReport.getLossPq());
        ps.setDouble(5, tgReport.getLossPer());
        ps.setString(6, tgReport.getDateDay());
        ps.setInt(7, tgReport.getCount());
        ps.setInt(8, tgReport.getRealCount());
        int num = ps.executeUpdate();
        conn.commit();
        return num;

    }

    public static int[] insert(List<ConsEle> list)
            throws Exception {
        getConnection();
        String sql = "insert into dws_elecon.dws_cst_cons_ele_result_df(rid,t_factor,event_time,pap_r,pap_r_diff,ele,ua,ub,uc,ia,ib,ic,i0,p,pa,pb,pc,q,qa,qb,qc,remark) " +
                "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        ps = conn.prepareStatement(sql);
        for (int i = 0; i < list.size(); i++) {
            ConsEle consEle = list.get(i);
            ps.setString(1, consEle.getRid());
            ps.setDouble(2, consEle.gettFactor());
            ps.setString(3, consEle.getEventTime());
            ps.setDouble(4, consEle.getPapR());
            ps.setDouble(5, consEle.getPapRDiff());
            ps.setDouble(6, consEle.getEle());
            ps.setString(7, consEle.getUa());
            ps.setString(8, consEle.getUb());
            ps.setString(9, consEle.getUc());
            ps.setString(10, consEle.getIa());
            ps.setString(11, consEle.getIb());
            ps.setString(12, consEle.getIc());
            ps.setString(13, consEle.getI0());
            ps.setString(14, consEle.getP());
            ps.setString(15, consEle.getPa());
            ps.setString(16, consEle.getPb());
            ps.setString(17, consEle.getPc());
            ps.setString(18, consEle.getQ());
            ps.setString(19, consEle.getQa());
            ps.setString(20, consEle.getQb());
            ps.setString(21, consEle.getQc());
            ps.setString(22, consEle.getRemark());
            ps.addBatch();
        }
        int[] num = ps.executeBatch();
        conn.commit();
        return num;
    }



    public static ResultSet select(String sql, Object... objects)
            throws Exception {
        getConnection();
        ps = conn.prepareStatement(sql);
        for (int i = 0; i < objects.length; i++) {
            ps.setObject(i + 1, objects[i]);
        }
        return ps.executeQuery();
    }

    public static ResultSet select(String sql)
            throws Exception {
        getConnection();
        ps = conn.prepareStatement(sql);
        return ps.executeQuery();
    }
}
