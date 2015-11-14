package ca.appbox.jira.plugins.issuedependencyviewer.issuetabpanels;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.plugin.issuetabpanel.AbstractIssueTabPanel;
import com.atlassian.jira.plugin.issuetabpanel.IssueAction;
import com.atlassian.jira.plugin.issuetabpanel.IssueTabPanelModuleDescriptor;
//import com.atlassian.jira.project.browse.BrowseContext;

/**
 * Manages the issue tab panel for viewing dependencies between issues.
 * 
 * @author Jean Arcand
 */
public final class IssueDependencyTabPanel extends AbstractIssueTabPanel {

	private static final String ISSUE_TAB_PANEL_VELOCITY_TEMPLATE = "dependency-graph-panel.vm";

	private IssueTabPanelModuleDescriptor issueTabPanelModuleDescriptor;
	
	public IssueDependencyTabPanel() {
		super();
	}

	@Override
	public List<IssueAction> getActions(Issue currentIssue, User currentUser) {
		final Issue issue = currentIssue;
		final List<IssueAction> actions = new ArrayList<IssueAction>();
		actions.add(new IssueAction() {
			
			@Override
			public boolean isDisplayActionAllTab() {
				return false;
			}
			
			@Override
			public Date getTimePerformed() {
				return new Date();
			}
			
			@Override
			public String getHtml() {
				String issueHtml = "<input class=\"hidden\" name=\"issueId\" value=\"" + issue.getId() + "\"/>";
				return issueHtml + issueTabPanelModuleDescriptor.getHtml(ISSUE_TAB_PANEL_VELOCITY_TEMPLATE);
			}
		});
		return actions;
	}

	@Override
	public void init(IssueTabPanelModuleDescriptor issueTabPanelModuleDescriptor) {
		this.issueTabPanelModuleDescriptor = issueTabPanelModuleDescriptor;
	}

	@Override
	public boolean showPanel(Issue currentIssue, User currentUser) {
		return true;
	}
}