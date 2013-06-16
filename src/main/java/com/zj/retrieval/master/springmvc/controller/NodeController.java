package com.zj.retrieval.master.springmvc.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.zj.retrieval.master.entity.Node;
import com.zj.retrieval.master.service.NodeService;
import com.zj.retrieval.master.util.JSONUtils;

@Controller
public class NodeController {
	
	private final static Logger logger = LoggerFactory.getLogger(NodeController.class);
	
	@Resource(name="NodeService")
	private NodeService ns;
	
	@RequestMapping(value="/node/add", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView test() {
		return new ModelAndView("/test.jsp", "message", "hello efeworld by zhaoje.");
	}
	
	@RequestMapping(value="/node/get_node_list", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String getNodeList() throws JSONException {
		List<Node> nodes = ns.getNodeList(1, 20);
		return JSONUtils.getJSONArray(nodes).toString();
	}
	
	@RequestMapping(value="/node/get_ztree_node", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String getZTreeNode(@RequestParam(value="pid", required=false) String pid) throws JSONException {
		List<Map<String, Object>> nds = ns.getNodeListByPid(pid == null ? "-1" : pid);
		return JSONUtils.getJSONArray4ZTree(nds).toString();
	}
}
