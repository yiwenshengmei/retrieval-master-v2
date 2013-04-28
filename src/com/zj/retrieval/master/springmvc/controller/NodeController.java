package com.zj.retrieval.master.springmvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class NodeController {
	
	@RequestMapping(value="/node/add", method = { RequestMethod.GET, RequestMethod.POST })
	public void addNode() {
		
	}
}
