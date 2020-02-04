package com.reconcile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class TaqseetReconcileUtillity {
	static Logger logger = Logger.getLogger(TaqseetReconcileUtillity.class);

	public static void main(String[] batchParam) throws SQLException {

		configureLog4j();
		Boolean dateParam = false;
		Boolean tranParam = false;
		Boolean missedTranParam = false;
		String method=null;
		String filepath=null;
		
		PosServiceUtil service = new PosServiceUtil();
		
		 if (batchParam.length==2){
			 method = batchParam[0];
			 filepath = batchParam[1];
			if (method.equalsIgnoreCase("ByTrans")){
				tranParam=true;
			}else if (method.equalsIgnoreCase("ByDate")){
				dateParam=true;
				filepath = null;
			}else if (method.equalsIgnoreCase("MissingTrans")){
				missedTranParam=true;
				filepath = null;
			}
			else{
				logger.info("Invalid Or No paramters passed");
			}
		}else{
			logger.info("Invalid Or No paramters passed");
		}
		
		if (dateParam) {
			
			logger.info("Call the Date Query" + method);
			logger.info("----------------Start---------------------");
			DBManager dbManager = DBManager.getInstance();
			List<CancelledTransactionDAO> cancelledTrans = dbManager.getCancelledTransactionByDate();
			for (CancelledTransactionDAO cancelledTransaction : cancelledTrans) {
				boolean transactionStatus = service.getTransactionStatus(cancelledTransaction.getRetailRefNo());
				if(transactionStatus){
				service.callPostTransactionReversal(cancelledTransaction);
				}
			}
			logger.info("----------------End---------------------");

		} else if (tranParam) {
			
			logger.info("Call the trans Query  " + method + " " + filepath);
			logger.info("----------------Start---------------------");
			DBManager dbManager = DBManager.getInstance();
			List<CancelledTransactionDAO> cancelledTrans = dbManager.getCancelledTransactionByTrans(filepath);
			for (CancelledTransactionDAO cancelledTransaction : cancelledTrans) {
				boolean transactionStatus = service.getTransactionStatus(cancelledTransaction.getRetailRefNo());
				if( transactionStatus){
				service.callPostTransactionReversal(cancelledTransaction);
				}
			}
			logger.info("----------------End---------------------");
			
		} 
		else if (missedTranParam) {
			
			logger.info("Call the missing transaction Query  " + method + " " + filepath);
			logger.info("----------------Start---------------------");
			DBManager dbManager = DBManager.getInstance();
			List<CancelledTransactionDAO> cancelledTrans = dbManager.getMissedCancelledTransactionByTrans();
			for (CancelledTransactionDAO cancelledTransaction : cancelledTrans) {
				boolean transactionStatus = service.getTransactionStatus(cancelledTransaction.getRetailRefNo());
				if( transactionStatus){
				service.callPostTransactionReversal(cancelledTransaction);
				}
			}
			logger.info("----------------End---------------------");
			
		} else {
			logger.info("Invalid Or No paramters passed");
		}
		
	}

	private static void configureLog4j() {
		
		BasicConfigurator.configure();
		Properties prop = new Properties();
		try {
			prop.load(PosServiceUtil.class.getClass().getResourceAsStream("/log4j.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		PropertyConfigurator.configure(prop);
		
	}
}
