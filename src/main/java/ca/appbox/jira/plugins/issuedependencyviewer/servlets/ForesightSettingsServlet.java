package ca.appbox.jira.plugins.issuedependencyviewer.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import ca.appbox.jira.plugins.issuedependencyviewer.graph.GraphBuilder;
//import ca.appbox.jira.plugins.issuedependencyviewer.servlets.response.GraphResponseBuilder;
import ca.appbox.jira.plugins.issuedependencyviewer.servlets.response.SettingsResponseBuilder;
import ca.appbox.jira.plugins.issuedependencyviewer.settings.ForesightSettings;

import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;

/**
 * Servlet that handles the plugin settings.
 * 
 * @author Jean Arcand
 */
public class ForesightSettingsServlet extends HttpServlet {

	private static final long serialVersionUID = -4486823641817126268L;
	
	private ConstantsManager constantsManager;
	private final PluginSettingsFactory pluginSettingsFactory;

	public ForesightSettingsServlet(ConstantsManager constantsManager, PluginSettingsFactory pluginSettingsFactory) {
		super();
		this.constantsManager = constantsManager;
		this.pluginSettingsFactory = pluginSettingsFactory;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		ForesightSettings pluginSettings = new ForesightSettings(constantsManager, pluginSettingsFactory);
		
		// output the response
		String graphResponse = new SettingsResponseBuilder().toJson(pluginSettings);
		
		resp.getWriter().write(graphResponse.toString());
	}
}
