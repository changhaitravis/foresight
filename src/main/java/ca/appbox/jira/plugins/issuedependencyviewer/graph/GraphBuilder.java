package ca.appbox.jira.plugins.issuedependencyviewer.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.ofbiz.core.entity.GenericEntityException;

import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.link.IssueLink;
import com.atlassian.jira.issue.link.IssueLinkManager;

/**
 * Builder of the dependency graph.
 * 
 * @author Jean Arcand
 */
public final class GraphBuilder {

	private IssueManager issueManager;
	private IssueLinkManager issueLinkManager;
	private boolean includeInwardLinks = true;
	private boolean includeOutwardLinks = true;
	private boolean includeSystemLinks = true;
	
	private List<Long> knownIssueIds = new ArrayList<Long>();
	
	public GraphBuilder(IssueManager issueManager,
			IssueLinkManager issueLinkManager) {
		
		this.issueManager = issueManager;
		this.issueLinkManager = issueLinkManager;
	}

	public Graph buildGraph(Long currentIssueId) {
		
		final Graph graph = new Graph();

		MutableIssue issueObject = issueManager.getIssueObject(currentIssueId);
		
		addAdjacentNodesToGraph(graph, issueObject.getId());
		
		return graph;
	}
	
	//Work In Progress
	/**
	 * @param currentProjectID
	 * @return Graph
	 * @throws GenericEntityException
	 */
	public Graph buildGraphForProject(Long currentProjectID){
		
		final Graph graph = new Graph();
		
		Collection<Long> IssueIdsInProject = null;
		
		try {
			IssueIdsInProject = issueManager.getIssueIdsForProject(currentProjectID);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (Long IssueId : IssueIdsInProject){
			
			MutableIssue issueObject = issueManager.getIssueObject(IssueId);
			
			addAdjacentNodesToGraph(graph, issueObject.getId());
			
		}
		
		return graph;
	}

	private void addAdjacentNodesToGraph(Graph graph, Long issueId) {
		
		if (!knownIssueIds.contains(issueId)) {
			MutableIssue issueObject = issueManager.getIssueObject(issueId);
			Node newNode = new Node(issueObject);
			knownIssueIds.add(issueId);
			graph.addNode(newNode);
		}
		
		if (this.includeOutwardLinks) {
			for (IssueLink currentOutwardLink : issueLinkManager.getOutwardLinks(issueId)) {
				
				if (includeLink(currentOutwardLink)) {
					if (!knownIssueIds.contains(currentOutwardLink.getDestinationId())) {
						addAdjacentNodesToGraph(graph, currentOutwardLink.getDestinationId());
					}
					
					addLink(graph, currentOutwardLink);
				}
			}
		}
		
		if (this.includeInwardLinks) {
			for (IssueLink currentInwardLink : issueLinkManager.getInwardLinks(issueId)) {

				if (includeLink(currentInwardLink)) {
					if (!knownIssueIds.contains(currentInwardLink.getSourceId())) {
						addAdjacentNodesToGraph(graph, currentInwardLink.getSourceId());
					}
					
					addLink(graph, currentInwardLink);
				}
			}
		}
	}

	private void addLink(Graph graph, IssueLink currentInwardLink) {
		Link potentialNewLink = new Link(currentInwardLink);
		
		if (!graph.containsLink(potentialNewLink)) {
			graph.addLink(potentialNewLink);
		}
	}

	private boolean includeLink(IssueLink currentOutwardLink) {
		boolean includeLink = this.includeSystemLinks
				|| (!this.includeSystemLinks && !currentOutwardLink.isSystemLink());
		
		return includeLink;
	}
	
	public GraphBuilder setIncludeInwardLinks(boolean includeInwardLinks) {
		this.includeInwardLinks = includeInwardLinks;
		return this;
	}
	
	public GraphBuilder setIncludeOutwardLinks(boolean includeOutwardLinks) {
		this.includeOutwardLinks = includeOutwardLinks;
		return this;
	}
	
	public GraphBuilder setIncludeSystemLinks(boolean includeSystemLinks) {
		this.includeSystemLinks = includeSystemLinks;
		return this;
	}	
}