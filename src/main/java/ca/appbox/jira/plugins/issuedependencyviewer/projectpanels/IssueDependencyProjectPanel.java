package ca.appbox.jira.plugins.issuedependencyviewer.projectpanels;

import com.atlassian.jira.plugin.projectpanel.ProjectTabPanel;
import com.atlassian.jira.plugin.projectpanel.ProjectTabPanelModuleDescriptor;
import com.atlassian.jira.plugin.projectpanel.impl.AbstractProjectTabPanel;
import com.atlassian.jira.project.browse.BrowseContext;

public final class IssueDependencyProjectPanel extends AbstractProjectTabPanel implements ProjectTabPanel{
	
	private static final String PROJECT_PANEL_VELOCITY_TEMPLATE = "dependency-graph-panel.vm";
	private ProjectTabPanelModuleDescriptor ProjectTabPanelModuleDescriptor;
	
	public IssueDependencyProjectPanel() {
		super();
	}
	
	@Override
	public void init(ProjectTabPanelModuleDescriptor ProjectTabPanelModuleDescriptor) {
		this.ProjectTabPanelModuleDescriptor = ProjectTabPanelModuleDescriptor;
	}
	
	@Override
	public String getHtml(BrowseContext arg0) {
		return ProjectTabPanelModuleDescriptor.getHtml(PROJECT_PANEL_VELOCITY_TEMPLATE);
	}
	
	@Override
	public boolean showPanel(BrowseContext arg0) {
		return true;
	}
}
