package com.example.stock.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class StockDAO {
	
	private static final Logger logger = LoggerFactory.getLogger(StockDAO.class);
	
	
	public void createIndexTable(Connection conn) throws Exception{
		logger.info("-------start createIndexTable-------");
		try {
			Statement st = conn.createStatement();
			
			st.executeUpdate("CREATE INDEX IF NOT EXISTS STKCOD_AP ON STCRD_AP(STKCOD)");
			st.executeUpdate("CREATE INDEX IF NOT EXISTS STKCOD_MC ON STCRD_MC(STKCOD)");
			st.executeUpdate("CREATE INDEX IF NOT EXISTS STKCOD_SF ON STCRD_SF(STKCOD)");
			st.executeUpdate("CREATE INDEX IF NOT EXISTS STKCOD_SP ON STCRD_SP(STKCOD)");
			
			st.executeUpdate("CREATE INDEX IF NOT EXISTS DOCNUM_AP ON STCRD_AP(DOCNUM)");
			st.executeUpdate("CREATE INDEX IF NOT EXISTS DOCNUM_MC ON STCRD_MC(DOCNUM)");
			st.executeUpdate("CREATE INDEX IF NOT EXISTS DOCNUM_SF ON STCRD_SF(DOCNUM)");
			st.executeUpdate("CREATE INDEX IF NOT EXISTS DOCNUM_SP ON STCRD_SP(DOCNUM)");
			
			//conn.commit();
			st.close();
		}catch(Exception e) {
			logger.error(e.getMessage());
			throw e;
		}finally {
			logger.info("-------End createIndexTable-------");
		}
	}
	
	public Integer countData(Connection conn) throws Exception
	{
		ResultSet rs = null;
		Statement st = null;
		Integer result = null;
		try {
			
			st = conn.createStatement();
			
			//SP
			String query  = "select count(*) as cc from  STCRD_SP t";
			rs = st.executeQuery(query);
			while (rs.next()) {
				result = rs.getInt("cc");
			}
		}catch(Exception e) {
			//e.printStackTrace();
			logger.error(e.getMessage());
			throw e;
		}finally {
			try { rs.close(); } catch (Exception e) { /* Ignored */ }
			try { st.close(); } catch (Exception e) { /* Ignored */ }
		}
		
		return result;
	}

	public Double checkStockRemain0102(String stkCode , Connection conn) throws Exception {
		
		ResultSet rs = null;
		Statement st = null;
		
		Double remain = 0D;
		
		String query = null;
		
		try {
			
			st = conn.createStatement();
			
			//SP
			query  = "select sum(a.XTRNQTY) as cal ,a.sign from  " + 
					"	( " + 
					"		SELECT  " + 
					"			t.DOCNUM, " + 
					"			t.XTRNQTY, " + 
					"			CASE " + 
					"				WHEN " + 
					"					TRIM(t.LOCCOD) = '04' " + 
					"				THEN " + 
					"					CASE " + 
					"						WHEN TRIM(t.POSOPR) = '9' THEN '-' " + 
					"						ELSE '+' " + 
					"					END " + 
					"				WHEN t.DOCNUM LIKE 'SP%' THEN '-' " + 
					"				WHEN t.DOCNUM LIKE 'RR%' THEN '+' " + 
					"				WHEN t.DOCNUM LIKE 'TK%' THEN '+' " + 
					"				WHEN t.DOCNUM LIKE 'PI%' THEN '+' " + 
					"				WHEN t.DOCNUM LIKE 'PD%' THEN '-' " + 
					"				WHEN t.DOCNUM LIKE 'SR%' THEN '+' " + 
					"				WHEN t.DOCNUM LIKE 'OU%' THEN '-' " + 
					"				WHEN t.DOCNUM LIKE 'XS%' THEN '-' " + 
					"				WHEN t.DOCNUM LIKE 'GR%' THEN '-' " + 
					"				WHEN t.DOCNUM LIKE 'ยอดยกมา%' THEN '+' " + 
					"				WHEN " + 
					"					t.DOCNUM LIKE 'JU%' " + 
					"				THEN " + 
					"					CASE " + 
					"						WHEN t.XTRNQTY < 0 THEN '---' " + 
					"						ELSE '+' " + 
					"					END " + 
					"				WHEN " + 
					"					t.DOCNUM LIKE 'RL%' " + 
					"				THEN " + 
					"					CASE " + 
					"						WHEN " + 
					"							TRIM(t.LOCCOD) = '01' OR TRIM(t.LOCCOD) = '02' " + 
					"						THEN " + 
					"							CASE " + 
					"								WHEN TRIM(t.POSOPR) = '3' AND (TRIM(t.LOCCOD) = '01' OR TRIM(t.LOCCOD) = '02'OR TRIM(t.LOCCOD) = '03') THEN '+' " + 
					"								WHEN TRIM(t.POSOPR) = '4' AND (TRIM(t.LOCCOD) = '01' OR TRIM(t.LOCCOD) = '02' OR TRIM(t.LOCCOD) = '03') THEN '--' " + 
					"							END " + 
					"					END " + 
					"			END AS SIGN " + 
					"		FROM " + 
					"			STCRD_SP t " + 
					"		WHERE " + 
					"			TRIM(t.STKCOD) = '"+stkCode+"' " + 
					"			AND TRIM(t.LOCCOD) in ('01','02') " + 
					"		ORDER BY t.DOCDAT " + 
					") a group by SIGN " + 
					"";
			
			rs = st.executeQuery(query);
			while (rs.next()) {
				if("+".equals(rs.getString("SIGN"))) {
					remain+= rs.getDouble("cal");
				}else if("-".equals(rs.getString("SIGN"))) {
					remain-=rs.getDouble("cal");
				}else if("--".equals(rs.getString("SIGN"))) {
					remain-=rs.getDouble("cal");
				}
				else if("---".equals(rs.getString("SIGN"))) {
					remain+=rs.getDouble("cal");
				}
			}
			//rs.close();
			//MC
			query  = "select sum(a.XTRNQTY) as cal ,a.sign from  " + 
					"	( " + 
					"		SELECT  " + 
					"			t.DOCNUM, " + 
					"			t.XTRNQTY, " + 
					"			CASE " + 
					"				WHEN " + 
					"					TRIM(t.LOCCOD) = '04' " + 
					"				THEN " + 
					"					CASE " + 
					"						WHEN TRIM(t.POSOPR) = '9' THEN '-' " + 
					"						ELSE '+' " + 
					"					END " + 
					"				WHEN t.DOCNUM LIKE 'MC%' THEN '-' " + 
					"				WHEN t.DOCNUM LIKE 'RR%' THEN '+' " + 
					"				WHEN t.DOCNUM LIKE 'TK%' THEN '+' " + 
					"				WHEN t.DOCNUM LIKE 'PI%' THEN '+' " + 
					"				WHEN t.DOCNUM LIKE 'PD%' THEN '-' " + 
					"				WHEN t.DOCNUM LIKE 'SR%' THEN '+' " + 
					"				WHEN t.DOCNUM LIKE 'OU%' THEN '-' " + 
					"				WHEN t.DOCNUM LIKE 'XS%' THEN '-' " + 
					"				WHEN t.DOCNUM LIKE 'GR%' THEN '-' " + 
					"				WHEN t.DOCNUM LIKE 'ยอดยกมา%' THEN '+' " + 
					"				WHEN " + 
					"					t.DOCNUM LIKE 'JU%' " + 
					"				THEN " + 
					"					CASE " + 
					"						WHEN t.XTRNQTY < 0 THEN '---' " + 
					"						ELSE '+' " + 
					"					END " + 
					"				WHEN " + 
					"					t.DOCNUM LIKE 'RL%' " + 
					"				THEN " + 
					"					CASE " + 
					"						WHEN " + 
					"							TRIM(t.LOCCOD) = '01' OR TRIM(t.LOCCOD) = '02' " + 
					"						THEN " + 
					"							CASE " + 
					"								WHEN TRIM(t.POSOPR) = '3' AND (TRIM(t.LOCCOD) = '01' OR TRIM(t.LOCCOD) = '02'OR TRIM(t.LOCCOD) = '03') THEN '+' " + 
					"								WHEN TRIM(t.POSOPR) = '4' AND (TRIM(t.LOCCOD) = '01' OR TRIM(t.LOCCOD) = '02' OR TRIM(t.LOCCOD) = '03') THEN '--' " + 
					"							END " + 
					"					END " + 
					"			END AS SIGN " + 
					"		FROM " + 
					"			STCRD_MC t " + 
					"		WHERE " + 
					"			TRIM(t.STKCOD) = '"+stkCode+"' " + 
					"			AND TRIM(t.LOCCOD) in ('01','02') " + 
					"		ORDER BY t.DOCDAT " + 
					") a group by SIGN " + 
					"";
			
			rs = st.executeQuery(query);
			while (rs.next()) {
				if("+".equals(rs.getString("SIGN"))) {
					remain+= rs.getDouble("cal");
				}else if("-".equals(rs.getString("SIGN"))) {
					remain-=rs.getDouble("cal");
				}else if("--".equals(rs.getString("SIGN"))) {
					remain-=rs.getDouble("cal");
				}else if("---".equals(rs.getString("SIGN"))) {
					remain+=rs.getDouble("cal");
				}
			}
			//rs.close();
			//SF
			query  = "select sum(a.XTRNQTY) as cal ,a.sign from  " + 
					"	( " + 
					"		SELECT  " + 
					"			t.DOCNUM, " + 
					"			t.XTRNQTY, " + 
					"			CASE " + 
					"				WHEN " + 
					"					TRIM(t.LOCCOD) = '04' " + 
					"				THEN " + 
					"					CASE " + 
					"						WHEN TRIM(t.POSOPR) = '9' THEN '-' " + 
					"						ELSE '+' " + 
					"					END " + 
					"				WHEN t.DOCNUM LIKE 'SF%' THEN '-' " + 
					"				WHEN t.DOCNUM LIKE 'RR%' THEN '+' " + 
					"				WHEN t.DOCNUM LIKE 'TK%' THEN '+' " + 
					"				WHEN t.DOCNUM LIKE 'PI%' THEN '+' " + 
					"				WHEN t.DOCNUM LIKE 'PD%' THEN '-' " + 
					"				WHEN t.DOCNUM LIKE 'SR%' THEN '+' " + 
					"				WHEN t.DOCNUM LIKE 'OU%' THEN '-' " + 
					"				WHEN t.DOCNUM LIKE 'XS%' THEN '-' " + 
					"				WHEN t.DOCNUM LIKE 'GR%' THEN '-' " + 
					"				WHEN t.DOCNUM LIKE 'ยอดยกมา%' THEN '+' " + 
					"				WHEN " + 
					"					t.DOCNUM LIKE 'JU%' " + 
					"				THEN " + 
					"					CASE " + 
					"						WHEN t.XTRNQTY < 0 THEN '---' " + 
					"						ELSE '+' " + 
					"					END " + 
					"				WHEN " + 
					"					t.DOCNUM LIKE 'RL%' " + 
					"				THEN " + 
					"					CASE " + 
					"						WHEN " + 
					"							TRIM(t.LOCCOD) = '01' OR TRIM(t.LOCCOD) = '02' " + 
					"						THEN " + 
					"							CASE " + 
					"								WHEN TRIM(t.POSOPR) = '3' AND (TRIM(t.LOCCOD) = '01' OR TRIM(t.LOCCOD) = '02'OR TRIM(t.LOCCOD) = '03') THEN '+' " + 
					"								WHEN TRIM(t.POSOPR) = '4' AND (TRIM(t.LOCCOD) = '01' OR TRIM(t.LOCCOD) = '02' OR TRIM(t.LOCCOD) = '03') THEN '--' " + 
					"							END " + 
					"					END " + 
					"			END AS SIGN " + 
					"		FROM " + 
					"			STCRD_SF t " + 
					"		WHERE " + 
					"			TRIM(t.STKCOD) = '"+stkCode+"' " + 
					"			AND TRIM(t.LOCCOD) in ('01','02') " + 
					"		ORDER BY t.DOCDAT " + 
					") a group by SIGN " + 
					"";
			
			rs = st.executeQuery(query);
			while (rs.next()) {
				if("+".equals(rs.getString("SIGN"))) {
					remain+= rs.getDouble("cal");
				}else if("-".equals(rs.getString("SIGN"))) {
					remain-=rs.getDouble("cal");
				}else if("--".equals(rs.getString("SIGN"))) {
					remain-=rs.getDouble("cal");
				}else if("---".equals(rs.getString("SIGN"))) {
					remain+=rs.getDouble("cal");
				}
			}
			//rs.close();
			//AP
			query  = "select sum(a.XTRNQTY) as cal ,a.sign from  " + 
					"	( " + 
					"		SELECT  " + 
					"			t.DOCNUM, " + 
					"			t.XTRNQTY, " + 
					"			CASE " + 
					"				WHEN " + 
					"					TRIM(t.LOCCOD) = '04' " + 
					"				THEN " + 
					"					CASE " + 
					"						WHEN TRIM(t.POSOPR) = '9' THEN '-' " + 
					"						ELSE '+' " + 
					"					END " + 
					"				WHEN t.DOCNUM LIKE 'AP%' THEN '-' " + 
					"				WHEN t.DOCNUM LIKE 'RR%' THEN '+' " + 
					"				WHEN t.DOCNUM LIKE 'TK%' THEN '+' " + 
					"				WHEN t.DOCNUM LIKE 'PI%' THEN '+' " + 
					"				WHEN t.DOCNUM LIKE 'PD%' THEN '-' " + 
					"				WHEN t.DOCNUM LIKE 'SR%' THEN '+' " + 
					"				WHEN t.DOCNUM LIKE 'OU%' THEN '-' " + 
					"				WHEN t.DOCNUM LIKE 'XS%' THEN '-' " + 
					"				WHEN t.DOCNUM LIKE 'GR%' THEN '-' " + 
					"				WHEN t.DOCNUM LIKE 'ยอดยกมา%' THEN '+' " + 
					"				WHEN " + 
					"					t.DOCNUM LIKE 'JU%' " + 
					"				THEN " + 
					"					CASE " + 
					"						WHEN t.XTRNQTY < 0 THEN '---' " + 
					"						ELSE '+' " + 
					"					END " + 
					"				WHEN " + 
					"					t.DOCNUM LIKE 'RL%' " + 
					"				THEN " + 
					"					CASE " + 
					"						WHEN " + 
					"							TRIM(t.LOCCOD) = '01' OR TRIM(t.LOCCOD) = '02' " + 
					"						THEN " + 
					"							CASE " + 
					"								WHEN TRIM(t.POSOPR) = '3' AND (TRIM(t.LOCCOD) = '01' OR TRIM(t.LOCCOD) = '02'OR TRIM(t.LOCCOD) = '03') THEN '+' " + 
					"								WHEN TRIM(t.POSOPR) = '4' AND (TRIM(t.LOCCOD) = '01' OR TRIM(t.LOCCOD) = '02' OR TRIM(t.LOCCOD) = '03') THEN '--' " + 
					"							END " + 
					"					END " + 
					"			END AS SIGN " + 
					"		FROM " + 
					"			STCRD_AP t " + 
					"		WHERE " + 
					"			TRIM(t.STKCOD) = '"+stkCode+"' " + 
					"			AND TRIM(t.LOCCOD) in ('01','02') " + 
					"		ORDER BY t.DOCDAT " + 
					") a group by SIGN " + 
					"";
			
			rs = st.executeQuery(query);
			while (rs.next()) {
				if("+".equals(rs.getString("SIGN"))) {
					remain+= rs.getDouble("cal");
				}else if("-".equals(rs.getString("SIGN"))) {
					remain-=rs.getDouble("cal");
				}else if("--".equals(rs.getString("SIGN"))) {
					remain-=rs.getDouble("cal");
				}else if("---".equals(rs.getString("SIGN"))) {
					remain+=rs.getDouble("cal");
				}
			}
			//rs.close();
			return remain;
		}catch(Exception e) {
			//e.printStackTrace();
			logger.error(e.getMessage());
			throw e;
		}finally {
			try { rs.close(); } catch (Exception e) { /* Ignored */ }
			try { st.close(); } catch (Exception e) { /* Ignored */ }
			query = null;
		}
	}
	
public Double checkStockRemain03(String stkCode , Connection conn) throws Exception {
		
		ResultSet rs = null;
		Statement st = null;
		
		Double remain = 0D;
		
		String query = null;
		
		try {
			
			st = conn.createStatement();
			
			//SP
			query  = "select sum(a.XTRNQTY) as cal ,a.sign from  " + 
					"	( " + 
					"		SELECT  " + 
					"			t.DOCNUM, " + 
					"			t.XTRNQTY, " + 
					"			CASE " + 
					"				WHEN " + 
					"					TRIM(t.LOCCOD) = '04' " + 
					"				THEN " + 
					"					CASE " + 
					"						WHEN TRIM(t.POSOPR) = '9' THEN '-' " + 
					"						ELSE '+' " + 
					"					END " + 
					"				WHEN t.DOCNUM LIKE 'SP%' THEN '-' " + 
					"				WHEN t.DOCNUM LIKE 'RR%' THEN '+' " + 
					"				WHEN t.DOCNUM LIKE 'TK%' THEN '+' " + 
					"				WHEN t.DOCNUM LIKE 'PI%' THEN '+' " + 
					"				WHEN t.DOCNUM LIKE 'PD%' THEN '-' " + 
					"				WHEN t.DOCNUM LIKE 'SR%' THEN '+' " + 
					"				WHEN t.DOCNUM LIKE 'OU%' THEN '-' " + 
					"				WHEN t.DOCNUM LIKE 'XS%' THEN '-' " + 
					"				WHEN t.DOCNUM LIKE 'GR%' THEN '-' " + 
					"				WHEN t.DOCNUM LIKE 'ยอดยกมา%' THEN '+' " + 
					"				WHEN " + 
					"					t.DOCNUM LIKE 'JU%' " + 
					"				THEN " + 
					"					CASE " + 
					"						WHEN t.XTRNQTY < 0 THEN '---' " + 
					"						ELSE '+' " + 
					"					END " + 
					"				WHEN " + 
					"					t.DOCNUM LIKE 'RL%' " + 
					"				THEN " + 
					"					CASE " + 
					"						WHEN " + 
					"							TRIM(t.LOCCOD) = '01' OR TRIM(t.LOCCOD) = '02' OR TRIM(t.LOCCOD) = '03' " + 
					"						THEN " + 
					"							CASE " + 
					"								WHEN TRIM(t.POSOPR) = '3' AND (TRIM(t.LOCCOD) = '01' OR TRIM(t.LOCCOD) = '02'OR TRIM(t.LOCCOD) = '03') THEN '+' " + 
					"								WHEN TRIM(t.POSOPR) = '4' AND (TRIM(t.LOCCOD) = '01' OR TRIM(t.LOCCOD) = '02' OR TRIM(t.LOCCOD) = '03') THEN '--' " + 
					"							END " + 
					"					END " + 
					"			END AS SIGN " + 
					"		FROM " + 
					"			STCRD_SP t " + 
					"		WHERE " + 
					"			TRIM(t.STKCOD) = '"+stkCode+"' " + 
					"			AND TRIM(t.LOCCOD) in ('01','02','03') " + 
					"		ORDER BY t.DOCDAT " + 
					") a group by SIGN " + 
					"";
			
			rs = st.executeQuery(query);
			while (rs.next()) {
				if("+".equals(rs.getString("SIGN"))) {
					remain+= rs.getDouble("cal");
				}else if("-".equals(rs.getString("SIGN"))) {
					remain-=rs.getDouble("cal");
				}else if("--".equals(rs.getString("SIGN"))) {
					remain-=rs.getDouble("cal");
				}else if("---".equals(rs.getString("SIGN"))) {
					remain+=rs.getDouble("cal");
				}
			}
			//rs.close();
			//MC
			query  = "select sum(a.XTRNQTY) as cal ,a.sign from  " + 
					"	( " + 
					"		SELECT  " + 
					"			t.DOCNUM, " + 
					"			t.XTRNQTY, " + 
					"			CASE " + 
					"				WHEN " + 
					"					TRIM(t.LOCCOD) = '04' " + 
					"				THEN " + 
					"					CASE " + 
					"						WHEN TRIM(t.POSOPR) = '9' THEN '-' " + 
					"						ELSE '+' " + 
					"					END " + 
					"				WHEN t.DOCNUM LIKE 'MC%' THEN '-' " + 
					"				WHEN t.DOCNUM LIKE 'RR%' THEN '+' " + 
					"				WHEN t.DOCNUM LIKE 'TK%' THEN '+' " + 
					"				WHEN t.DOCNUM LIKE 'PI%' THEN '+' " + 
					"				WHEN t.DOCNUM LIKE 'PD%' THEN '-' " + 
					"				WHEN t.DOCNUM LIKE 'SR%' THEN '+' " + 
					"				WHEN t.DOCNUM LIKE 'OU%' THEN '-' " + 
					"				WHEN t.DOCNUM LIKE 'XS%' THEN '-' " + 
					"				WHEN t.DOCNUM LIKE 'GR%' THEN '-' " + 
					"				WHEN t.DOCNUM LIKE 'ยอดยกมา%' THEN '+' " + 
					"				WHEN " + 
					"					t.DOCNUM LIKE 'JU%' " + 
					"				THEN " + 
					"					CASE " + 
					"						WHEN t.XTRNQTY < 0 THEN '---' " + 
					"						ELSE '+' " + 
					"					END " + 
					"				WHEN " + 
					"					t.DOCNUM LIKE 'RL%' " + 
					"				THEN " + 
					"					CASE " + 
					"						WHEN " + 
					"							TRIM(t.LOCCOD) = '01' OR TRIM(t.LOCCOD) = '02' OR TRIM(t.LOCCOD) = '03' " + 
					"						THEN " + 
					"							CASE " + 
					"								WHEN TRIM(t.POSOPR) = '3' AND (TRIM(t.LOCCOD) = '01' OR TRIM(t.LOCCOD) = '02'OR TRIM(t.LOCCOD) = '03') THEN '+' " + 
					"								WHEN TRIM(t.POSOPR) = '4' AND (TRIM(t.LOCCOD) = '01' OR TRIM(t.LOCCOD) = '02' OR TRIM(t.LOCCOD) = '03') THEN '--' " + 
					"							END " + 
					"					END " + 
					"			END AS SIGN " + 
					"		FROM " + 
					"			STCRD_MC t " + 
					"		WHERE " + 
					"			TRIM(t.STKCOD) = '"+stkCode+"' " + 
					"			AND TRIM(t.LOCCOD) in ('01','02','03') " + 
					"		ORDER BY t.DOCDAT " + 
					") a group by SIGN " + 
					"";
			
			rs = st.executeQuery(query);
			while (rs.next()) {
				if("+".equals(rs.getString("SIGN"))) {
					remain+= rs.getDouble("cal");
				}else if("-".equals(rs.getString("SIGN"))) {
					remain-=rs.getDouble("cal");
				}else if("--".equals(rs.getString("SIGN"))) {
					remain-=rs.getDouble("cal");
				}else if("---".equals(rs.getString("SIGN"))) {
					remain+=rs.getDouble("cal");
				}
			}
			//rs.close();
			//SF
			query  = "select sum(a.XTRNQTY) as cal ,a.sign from  " + 
					"	( " + 
					"		SELECT  " + 
					"			t.DOCNUM, " + 
					"			t.XTRNQTY, " + 
					"			CASE " + 
					"				WHEN " + 
					"					TRIM(t.LOCCOD) = '04' " + 
					"				THEN " + 
					"					CASE " + 
					"						WHEN TRIM(t.POSOPR) = '9' THEN '-' " + 
					"						ELSE '+' " + 
					"					END " + 
					"				WHEN t.DOCNUM LIKE 'SF%' THEN '-' " + 
					"				WHEN t.DOCNUM LIKE 'RR%' THEN '+' " + 
					"				WHEN t.DOCNUM LIKE 'TK%' THEN '+' " + 
					"				WHEN t.DOCNUM LIKE 'PI%' THEN '+' " + 
					"				WHEN t.DOCNUM LIKE 'PD%' THEN '-' " + 
					"				WHEN t.DOCNUM LIKE 'SR%' THEN '+' " + 
					"				WHEN t.DOCNUM LIKE 'OU%' THEN '-' " + 
					"				WHEN t.DOCNUM LIKE 'XS%' THEN '-' " + 
					"				WHEN t.DOCNUM LIKE 'GR%' THEN '-' " + 
					"				WHEN t.DOCNUM LIKE 'ยอดยกมา%' THEN '+' " + 
					"				WHEN " + 
					"					t.DOCNUM LIKE 'JU%' " + 
					"				THEN " + 
					"					CASE " + 
					"						WHEN t.XTRNQTY < 0 THEN '---' " + 
					"						ELSE '+' " + 
					"					END " + 
					"				WHEN " + 
					"					t.DOCNUM LIKE 'RL%' " + 
					"				THEN " + 
					"					CASE " + 
					"						WHEN " + 
					"							TRIM(t.LOCCOD) = '01' OR TRIM(t.LOCCOD) = '02' OR TRIM(t.LOCCOD) = '03' " + 
					"						THEN " + 
					"							CASE " + 
					"								WHEN TRIM(t.POSOPR) = '3' AND (TRIM(t.LOCCOD) = '01' OR TRIM(t.LOCCOD) = '02'OR TRIM(t.LOCCOD) = '03') THEN '+' " + 
					"								WHEN TRIM(t.POSOPR) = '4' AND (TRIM(t.LOCCOD) = '01' OR TRIM(t.LOCCOD) = '02' OR TRIM(t.LOCCOD) = '03') THEN '--' " + 
					"							END " + 
					"					END " + 
					"			END AS SIGN " + 
					"		FROM " + 
					"			STCRD_SF t " + 
					"		WHERE " + 
					"			TRIM(t.STKCOD) = '"+stkCode+"' " + 
					"			AND TRIM(t.LOCCOD) in ('01','02','03') " + 
					"		ORDER BY t.DOCDAT " + 
					") a group by SIGN " + 
					"";
			
			rs = st.executeQuery(query);
			while (rs.next()) {
				if("+".equals(rs.getString("SIGN"))) {
					remain+= rs.getDouble("cal");
				}else if("-".equals(rs.getString("SIGN"))) {
					remain-=rs.getDouble("cal");
				}else if("--".equals(rs.getString("SIGN"))) {
					remain-=rs.getDouble("cal");
				}else if("---".equals(rs.getString("SIGN"))) {
					remain+=rs.getDouble("cal");
				}
			}
			//rs.close();
			//AP
			query  = "select sum(a.XTRNQTY) as cal ,a.sign from  " + 
					"	( " + 
					"		SELECT  " + 
					"			t.DOCNUM, " + 
					"			t.XTRNQTY, " + 
					"			CASE " + 
					"				WHEN " + 
					"					TRIM(t.LOCCOD) = '04' " + 
					"				THEN " + 
					"					CASE " + 
					"						WHEN TRIM(t.POSOPR) = '9' THEN '-' " + 
					"						ELSE '+' " + 
					"					END " + 
					"				WHEN t.DOCNUM LIKE 'AP%' THEN '-' " + 
					"				WHEN t.DOCNUM LIKE 'RR%' THEN '+' " + 
					"				WHEN t.DOCNUM LIKE 'TK%' THEN '+' " + 
					"				WHEN t.DOCNUM LIKE 'PI%' THEN '+' " + 
					"				WHEN t.DOCNUM LIKE 'PD%' THEN '-' " + 
					"				WHEN t.DOCNUM LIKE 'SR%' THEN '+' " + 
					"				WHEN t.DOCNUM LIKE 'OU%' THEN '-' " + 
					"				WHEN t.DOCNUM LIKE 'XS%' THEN '-' " + 
					"				WHEN t.DOCNUM LIKE 'GR%' THEN '-' " + 
					"				WHEN t.DOCNUM LIKE 'ยอดยกมา%' THEN '+' " + 
					"				WHEN " + 
					"					t.DOCNUM LIKE 'JU%' " + 
					"				THEN " + 
					"					CASE " + 
					"						WHEN t.XTRNQTY < 0 THEN '---' " + 
					"						ELSE '+' " + 
					"					END " + 
					"				WHEN " + 
					"					t.DOCNUM LIKE 'RL%' " + 
					"				THEN " + 
					"					CASE " + 
					"						WHEN " + 
					"							TRIM(t.LOCCOD) = '01' OR TRIM(t.LOCCOD) = '02' OR TRIM(t.LOCCOD) = '03' " + 
					"						THEN " + 
					"							CASE " + 
					"								WHEN TRIM(t.POSOPR) = '3' AND (TRIM(t.LOCCOD) = '01' OR TRIM(t.LOCCOD) = '02'OR TRIM(t.LOCCOD) = '03') THEN '+' " + 
					"								WHEN TRIM(t.POSOPR) = '4' AND (TRIM(t.LOCCOD) = '01' OR TRIM(t.LOCCOD) = '02' OR TRIM(t.LOCCOD) = '03') THEN '--' " + 
					"							END " + 
					"					END " + 
					"			END AS SIGN " + 
					"		FROM " + 
					"			STCRD_AP t " + 
					"		WHERE " + 
					"			TRIM(t.STKCOD) = '"+stkCode+"' " + 
					"			AND TRIM(t.LOCCOD) in ('01','02','03') " + 
					"		ORDER BY t.DOCDAT " + 
					") a group by SIGN " + 
					"";
			
			rs = st.executeQuery(query);
			while (rs.next()) {
				if("+".equals(rs.getString("SIGN"))) {
					remain+= rs.getDouble("cal");
				}else if("-".equals(rs.getString("SIGN"))) {
					remain-=rs.getDouble("cal");
				}else if("--".equals(rs.getString("SIGN"))) {
					remain-=rs.getDouble("cal");
				}else if("---".equals(rs.getString("SIGN"))) {
					remain+=rs.getDouble("cal");
				}
			}
			
			//rs.close();
			
			return remain;
		}catch(Exception e) {
			//e.printStackTrace();
			logger.error(e.getMessage());
			throw e;
		}finally {
			try { rs.close(); } catch (Exception e) { /* Ignored */ }
			try { st.close(); } catch (Exception e) { /* Ignored */ }
			query = null;
		}
	}
}