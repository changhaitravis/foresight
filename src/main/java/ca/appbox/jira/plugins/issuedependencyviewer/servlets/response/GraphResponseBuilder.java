package ca.appbox.jira.plugins.issuedependencyviewer.servlets.response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.appbox.jira.plugins.issuedependencyviewer.graph.Graph;
import ca.appbox.jira.plugins.issuedependencyviewer.graph.Link;
import ca.appbox.jira.plugins.issuedependencyviewer.graph.Node;

/**
 * Builds the json representation of a graph.
 * 
 * @author Jean Arcand
 */
public class GraphResponseBuilder {

	/**
	 * Poor man's implementation of Gson.
	 * 
	 * @param graph The graph to generate the json.
	 * 
	 * @return Json string representation of the graph.
	 */
	public String toJson(Graph graph) {
	
		StringBuilder json = new StringBuilder();
		
		json.append("{")
			.append("  \"nodes\":[");
		
		// key = issue id, value = position in array.
		Map<Long, Integer> nodeIndex = new HashMap<Long, Integer>();
		List<Node> nodes = graph.getNodes();
		for (int i=0;i<nodes.size();i++) {

			nodeIndex.put(nodes.get(i).getId(), Integer.valueOf(i));
		
			json.append("{\"key\":")
				.append(nodes.get(i).getId())
			    .append(",\"name\":\"")
				.append(nodes.get(i).getName())
				.append("\",\"group\":\"")
				.append(nodes.get(i).getGroup())
				.append("\",\"summary\":\"")
				.append(nodes.get(i).getSummary())
				.append("\",")
				.append("\"type\":\"")
				.append(nodes.get(i).getType())
				.append("\",")
				.append("\"status\":\"")
				.append(nodes.get(i).getStatus())
				.append("\"}");
			
			if (i != nodes.size() - 1) {
				json.append(",");
			}
		}
		
		json.append("  ],");
		json.append("  \"links\":[");
		
		List<Link> links = graph.getLinks();
		for (int i=0;i<links.size();i++) {
			
			json.append("{\"source\":")
				.append(nodeIndex.get(links.get(i).getSource()))
				.append(",\"target\":")
				.append(nodeIndex.get(links.get(i).getTarget()))
				.append(",\"type\":\"")
				.append("resolved")
				.append("\",\"name\":\"")
				.append(links.get(i).getName())
				.append("\",\"outward\":\"")
				.append(links.get(i).getOutward())
				.append("\",\"inward\":\"")
				.append(links.get(i).getInward())
				.append("\",\"systemLink\":")
				.append(links.get(i).isSystemLink())
				.append("}");
			
			if (i != links.size() - 1) {
				json.append(",");
			}
		}
		
		json.append("  ]")
			.append("}");
		
		return json.toString();
	}
}