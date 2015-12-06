package ca.appbox.jira.plugins.issuedependencyviewer.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;

/**
 * Settings of the plugin.
 * 
 * @author Jean Arcand
 */
public class ForesightSettings {

	private ConstantsManager constantsManager;
	private static final String PLUGIN_STORAGE_KEY = "ca.appbox.jira.plugins.issuedependencyviewer.colors";
	private final PluginSettingsFactory pluginSettingsFactory;
	
	public ForesightSettings(ConstantsManager constantsManager, PluginSettingsFactory pluginSettingsFactory) {
		super();
		this.constantsManager = constantsManager;
		this.pluginSettingsFactory = pluginSettingsFactory;
	}

	public Map<String, String> getColorCodesByIssueTypes() {
		
		PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();
		Map<String, String> colorCodesByIssueTypes = new HashMap<String, String>();
		List<IssueType> allIssueTypes = new ArrayList<IssueType>(constantsManager.getAllIssueTypeObjects());
		
		for (IssueType currentIssueType : allIssueTypes) {
			String currentIssueTypeName = currentIssueType.getName();
			String thisColor = (String) pluginSettings.get(PLUGIN_STORAGE_KEY + "." + currentIssueTypeName);
			if (thisColor != null){
				colorCodesByIssueTypes.put(currentIssueTypeName, thisColor);
			}else{
			colorCodesByIssueTypes.put(currentIssueTypeName, 
					JiraDefaultIssueTypes.fromJiraName(currentIssueTypeName).getColorCode());
			}
		}
		return colorCodesByIssueTypes;
	}
}

