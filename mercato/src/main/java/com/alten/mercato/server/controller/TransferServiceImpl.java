/**
 * 
 */
package com.alten.mercato.server.controller;

import java.util.List;

import org.gwtwidgets.server.spring.ServletUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.alten.mercato.client.service.TransferService;
import com.alten.mercato.server.controller.dto.InfoTransfer;
import com.alten.mercato.server.manager.interf.TransferManager;
import com.alten.mercato.server.model.Personne;

/**
 * @author Huage Chen
 *
 */
@Service("transferController")
public class TransferServiceImpl implements TransferService {
	
	// log4j
	Logger logger = LoggerFactory.getLogger(TransferServiceImpl.class);
	
	@Autowired
	@Qualifier("transferManager")
	private TransferManager transferManager = null;
	

	/* (non-Javadoc)
	 * @see com.alten.mercato.client.service.TransferService#startAndAskTransferProcess(long, long)
	 */
	public Personne startAndAskTransferProcess(long transDepEntrId,
			long transDepConsulId) throws Exception {
		try {
			String assignee = ServletUtils.getRequest().getUserPrincipal().getName();
			Personne res = transferManager.startAndAskTransferProcess(transDepEntrId, transDepConsulId, assignee);
			
			// lazy loading
			res.getTransferCourant().getDepEntr().getDepLib();
			return res;
		} catch (Exception e) {
			logger.error(e.toString());
			return null;
		}
	}


	/* (non-Javadoc)
	 * @see com.alten.mercato.client.service.TransferService#getConsultantWithTransferInfo()
	 */
	public List<InfoTransfer> getConsultantWithTransferInfo() throws Exception {
		try {
			logger.info("getting the transfer information");
			List<InfoTransfer> res = transferManager.getConsultantsWithTransferInfo();
			
			//lazy loading
			logger.info("lazy loading");
			for (InfoTransfer infoTransfer: res) {
				infoTransfer.getPersonne().getDepartement().getDepLib();
				if (infoTransfer.getPersonne().getTransferCourant() != null) {
					infoTransfer.getPersonne().getTransferCourant().getDepEntr().getDepLib();
				}
			}
			logger.info("lazy loading finished, returning the transfer informations to client side");
			return res;
		} catch (Exception e) {
			logger.error(e.toString());
			return null;
		}
	}
	
}
