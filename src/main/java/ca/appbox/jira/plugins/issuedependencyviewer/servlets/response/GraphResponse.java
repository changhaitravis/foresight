package ca.appbox.jira.plugins.issuedependencyviewer.servlets.response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.appbox.jira.plugins.issuedependencyviewer.graph.Link;
import ca.appbox.jira.plugins.issuedependencyviewer.graph.Node;

public class GraphResponse {

	class node {
		public Long key;
		public String name;
		public String group;
		public String summary;
		public String type;
		public String status;
		public node(int index, List<Node> nodes) {
			key = nodes.get(index).getId();
			name = nodes.get(index).getName();
			group = nodes.get(index).getGroup();
			summary = nodes.get(index).getSummary();
			type = nodes.get(index).getType();
			status = nodes.get(index).getStatus();
		}
	}
	
	class link {
		public Integer source;
		public Integer target;
		public String type = "resolved";
		public String name;
		public String outward;
		public String inward;
		public Boolean systemLink;
		public link(int index, List<Link> links, Integer source, Integer target) {
			this.source = source;
			this.target = target;
			name = links.get(index).getName();
			outward = links.get(index).getOutward();
			inward = links.get(index).getInward();
			systemLink = links.get(index).isSystemLink();
		}
		
		public link(int index, List<Link> links, Map<Long, Integer> nodeIndex){
			source = nodeIndex.get(links.get(index).getSource());
			target = nodeIndex.get(links.get(index).getTarget());
			name = links.get(index).getName();
			outward = links.get(index).getOutward();
			inward = links.get(index).getInward();
			systemLink = links.get(index).isSystemLink();
		}
	}
	
	// key = issue id, value = position in array.
	private Map<Long, Integer> nodeIndex;
	public node[] nodes;
	public link[] links;
	
	public GraphResponse(List<Node> nodes, List<Link> links) {
		nodeIndex = new HashMap<Long, Integer>();
		this.nodes = new node [nodes.size()];
		this.links = new link [links.size()];
		for (int i=0;i<nodes.size();i++) {
			nodeIndex.put(nodes.get(i).getId(), Integer.valueOf(i));
			this.nodes[i] = new node(i, nodes);
		}
		for (int i=0;i<links.size();i++) {
			Integer source = nodeIndex.get(links.get(i).getSource());
			Integer target = nodeIndex.get(links.get(i).getTarget());
			this.links[i] = new link(i, links, source, target);
		}
	}

}
