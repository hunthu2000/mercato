/**
 * 
 */
package com.alten.mercato.server.controller;

import java.util.ArrayList;
import java.util.List;

import org.gwtwidgets.server.spring.ServletUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.alten.mercato.client.service.TransferService;
import com.alten.mercato.server.controller.dto.InfoTransfer;
import com.alten.mercato.server.manager.interf.PersonManager;
import com.alten.mercato.server.manager.interf.TransferManager;
import com.alten.mercato.server.model.Personne;
import com.alten.mercato.server.model.Transfert;

/**
 * @author Huage Chen
 *
 */
@Service("transferController")
public class TransferServiceImpl implements TransferService {

	

	@Autowired
	@Qualifier("transferManager")
	private TransferManager transferManager = null;
	
	@Autowired
	@Qualifier("personManager")
	private PersonManager personManager = null;
	
	
	// log4j
	Logger logger = LoggerFactory.getLogger(TransferServiceImpl.class);

	/* (non-Javadoc)
	 * @see com.alten.mercato.client.service.TransferService#startAndAskTransferProcess(long, long)
	 */
	public Personne startAndAskTransferProcess(long transDepEntrId,
			long transDepConsulId) throws Exception {
		try {
			String assignee = ServletUtils.getRequest().getUserPrincipal().getName();
			Personne res = transferManager.startAndAskTransferRequestProcess(transDepEntrId, transDepConsulId, assignee);

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
			List<InfoTransfer> res = personManager.getConsultantsWithTransferInfo();

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


	/* (non-Javadoc)
	 * @see com.alten.mercato.client.service.TransferService#validateTransferProcess(com.alten.mercato.server.model.Transfert, java.lang.String)
	 */
	public Personne validateTransferRequestProcess(Transfert transfert, String validation)
	throws Exception {
		try {

			String assignee = ServletUtils.getRequest().getUserPrincipal().getName();
			logger.info("completing validation");
			Personne res = transferManager.signalValidateTransferRequest(transfert, assignee, validation);

			logger.info("lazy loading");
			// lazy loading
			res.getDepartement().getDepLib();
			logger.info("lazy loading finished, returning the transfered consultant  to client side");
			return res;
		} catch (Exception e) {
			logger.error(e.toString());
			return null;
		}
	}


	public Personne startAndProposeTransferProcess(long transDepEntrId,
			long transDepConsulId) throws Exception {
		try {
			String assignee = ServletUtils.getRequest().getUserPrincipal().getName();
			Personne res = transferManager.startAndAskTransferProposalProcess(transDepEntrId, transDepConsulId, assignee);

			// lazy loading
			res.getTransferCourant().getDepEntr().getDepLib();
			return res;
		} catch (Exception e) {
			logger.error(e.toString());
			return null;
		}
	}


	/* (non-Javadoc)
	 * @see com.alten.mercato.client.service.TransferService#validateTransferProposalProcess(com.alten.mercato.server.model.Transfert, java.lang.String)
	 */
	public Personne validateTransferProposalProcess(Transfert transfert,
			String validation) throws Exception {
		try {

			String assignee = ServletUtils.getRequest().getUserPrincipal().getName();
			logger.info("completing validation");
			Personne res = transferManager.signalValidateTransferProposal(transfert, assignee, validation);

			logger.info("lazy loading");
			// lazy loading
			res.getDepartement().getDepLib();
			logger.info("lazy loading finished, returning the transfered consultant  to client side");
			return res;
		} catch (Exception e) {
			logger.error(e.toString());
			return null;
		}
	}


	/* (non-Javadoc)
	 * @see com.alten.mercato.client.service.TransferService#signalACommentHR1(com.alten.mercato.server.model.Transfert, java.lang.String)
	 */
	public boolean signalCommentHR1(Transfert transfert, String comment)
			throws Exception {
		try {

			String assignee = ServletUtils.getRequest().getUserPrincipal().getName();
			logger.info("Comment HR1");
			transferManager.signalCommentHR1(transfert, assignee, comment);
			return true;
		} catch (Exception e) {
			logger.error(e.toString());
			return false;
		}
	}


	public boolean signalCommentHR2(Transfert transfert, String comment)
			throws Exception {
		try {

			String assignee = ServletUtils.getRequest().getUserPrincipal().getName();
			logger.info("Comment HR2");
			transferManager.signalCommentHR2(transfert, assignee, comment);
			return true;
		} catch (Exception e) {
			logger.error(e.toString());
			return false;
		}
	}


	public Personne signalCancelTransfer(Transfert transfert) throws Exception {
		try {

			String assignee = ServletUtils.getRequest().getUserPrincipal().getName();
			logger.info("cancelling transfer..");
			Personne res = transferManager.signalCancelTransfer(transfert, assignee);

			logger.info("lazy loading");
			// lazy loading
			res.getDepartement().getDepLib();
			logger.info("lazy loading finished, returning the transfered consultant  to client side");
			return res;
		} catch (Exception e) {
			logger.error(e.toString());
			return null;
		}
	}


	public Personne signalValidateTransferProposalV2(Transfert transfert,
			String validation) throws Exception {
		try {

			String assignee = ServletUtils.getRequest().getUserPrincipal().getName();
			logger.info("completing validation");
			Personne res = transferManager.signalValidateTransferProposalV2(transfert, assignee, validation);

			logger.info("lazy loading");
			// lazy loading
			res.getDepartement().getDepLib();
			logger.info("lazy loading finished, returning the transfered consultant  to client side");
			return res;
		} catch (Exception e) {
			logger.error(e.toString());
			return null;
		}
	}


	public Personne startAndProposeTransferProcessV2(long transDepEntrId,
			long transDepConsulId) throws Exception {
		try {

			String assignee = ServletUtils.getRequest().getUserPrincipal().getName();
			logger.info("starting transfer proposal");
			Personne res = transferManager.startAndProposeTransferProcessV2(transDepEntrId, transDepConsulId, assignee);

			logger.info("lazy loading");
			// lazy loading
			res.getDepartement().getDepLib();
			logger.info("lazy loading finished, returning the transfered consultant  to client side");
			return res;
		} catch (Exception e) {
			logger.error(e.toString());
			return null;
		}
	}


	public List<Personne> signalCancelTransfer(List<Transfert> transferts)
			throws Exception {
		try {

			String assignee = ServletUtils.getRequest().getUserPrincipal().getName();
			logger.info("cancelling transfer..");
			
			List<Personne> res = new ArrayList<Personne>();
			Personne pers = null; 
			for (Transfert transfert: transferts) {
				pers = transferManager.signalCancelTransfer(transfert, assignee);
				if (pers != null) {
					logger.info("lazy loading");
					// lazy loading
					pers.getDepartement().getDepLib();
					res.add(pers);
				}
			}
			logger.info("lazy loading finished, returning the transfered consultant  to client side");
			return res;
		} catch (Exception e) {
			logger.error(e.toString());
			return null;
		}
	}

}
