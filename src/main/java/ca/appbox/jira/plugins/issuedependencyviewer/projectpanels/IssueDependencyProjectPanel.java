package ca.appbox.jira.plugins.issuedependencyviewer.projectpanels;

import com.atlassian.jira.plugin.projectpanel.ProjectTabPanel;
import com.atlassian.jira.plugin.projectpanel.ProjectTabPanelModuleDescriptor;
import com.atlassian.jira.plugin.projectpanel.impl.AbstractProjectTabPanel;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.browse.BrowseContext;

public final class IssueDependencyProjectPanel extends AbstractProjectTabPanel implements ProjectTabPanel{
	
	private static final String PROJECT_PANEL_VELOCITY_TEMPLATE = "project-dependency-graph-panel.vm";
	private ProjectTabPanelModuleDescriptor ProjectTabPanelModuleDescriptor;
	
	public IssueDependencyProjectPanel() {
		super();
	}
	
	@Override
	public void init(ProjectTabPanelModuleDescriptor ProjectTabPanelModuleDescriptor) {
		this.ProjectTabPanelModuleDescriptor = ProjectTabPanelModuleDescriptor;
	}
	
	@Override
	public String getHtml(BrowseContext ctx) {
		Project project = ctx.getProject();
		String projectHtml = "<input class=\"hidden\" name=\"projectId\" value=\"" + project.getId() + "\"/>";
		return projectHtml + ProjectTabPanelModuleDescriptor.getHtml(PROJECT_PANEL_VELOCITY_TEMPLATE);
	}
	
	@Override
	public boolean showPanel(BrowseContext arg0) {
		return true;
	}
}
