/**
 * 
 */
package com.alten.mercato.server.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alten.mercato.client.service.TransferService;

/**
 * @author Huage Chen
 *
 */
@Service("transferController")
public class TransferServiceImpl implements TransferService {
	
	// log4j
	Logger logger = LoggerFactory.getLogger(TransferServiceImpl.class);
}
