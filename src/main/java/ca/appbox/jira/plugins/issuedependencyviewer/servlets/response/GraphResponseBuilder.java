package ca.appbox.jira.plugins.issuedependencyviewer.servlets.response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.appbox.jira.plugins.issuedependencyviewer.graph.Graph;
import ca.appbox.jira.plugins.issuedependencyviewer.graph.Link;
import ca.appbox.jira.plugins.issuedependencyviewer.graph.Node;

import com.google.gson.Gson;

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
		
		List<Node> nodes = graph.getNodes();
		List<Link> links = graph.getLinks();
		
		GraphResponse obj = new GraphResponse(nodes, links);

		Gson gson = new Gson();
		String json = gson.toJson(obj);
		return json; 
	}
}