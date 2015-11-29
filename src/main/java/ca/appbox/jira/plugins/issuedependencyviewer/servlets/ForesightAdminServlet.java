package ca.appbox.jira.plugins.issuedependencyviewer.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.net.URI;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;

import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.google.common.collect.Maps;

import ca.appbox.jira.plugins.issuedependencyviewer.settings.JiraDefaultIssueTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.issue.issuetype.IssueType;

public class ForesightAdminServlet extends HttpServlet {
	
	  private static final long serialVersionUID = 5152347930198494576L;
	  private static final String PLUGIN_STORAGE_KEY = "ca.appbox.jira.plugins.issuedependencyviewer.colors";
	  private final UserManager userManager;
	  private final LoginUriProvider loginUriProvider;
	  private final TemplateRenderer renderer;
	  private final PluginSettingsFactory pluginSettingsFactory;
	  private ConstantsManager constantsManager;

	  public ForesightAdminServlet(UserManager userManager, LoginUriProvider loginUriProvider, 
	  TemplateRenderer renderer, PluginSettingsFactory pluginSettingsFactory, ConstantsManager constantsManager)
	  {
	    this.userManager = userManager;
	    this.loginUriProvider = loginUriProvider;
	    this.renderer = renderer;
	    this.pluginSettingsFactory = pluginSettingsFactory;
	    this.constantsManager = constantsManager;
	  }
	
	  @Override
	  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	  {
		    String username = userManager.getRemoteUser(request).getUsername();
		    UserKey userKey = userManager.getRemoteUser(request).getUserKey();
		    if (username == null || !userManager.isSystemAdmin(userKey))
		    {
		      redirectToLogin(request, response);
		      return;
		    }
		    
		    //Map objects
		    Map<String, Object> context = Maps.newHashMap();
		    
		    List<IssueType> allIssueTypes = new ArrayList<IssueType>(constantsManager.getAllIssueTypeObjects());
		    
		    PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();
		    
			for (IssueType currentIssueType : allIssueTypes) {
				String currentIssueTypeName = currentIssueType.getName();
				if (pluginSettings.get(PLUGIN_STORAGE_KEY) == null){
					String noColor = JiraDefaultIssueTypes.fromJiraName(currentIssueTypeName).getColorCode();
					pluginSettings.put(PLUGIN_STORAGE_KEY + "." + currentIssueTypeName, noColor);
				}
				context.put(currentIssueTypeName, pluginSettings.get(PLUGIN_STORAGE_KEY + "." + currentIssueTypeName));
			}
		    
			
			//End Map objects
		    
		    response.setContentType("text/html;charset=utf-8");
		    renderer.render("admin.vm", response.getWriter());
	  }
	  
	  @Override
	  protected void doPost(HttpServletRequest req, HttpServletResponse response)
		throws ServletException, IOException {
			PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();
			List<IssueType> allIssueTypes = new ArrayList<IssueType>(constantsManager.getAllIssueTypeObjects());
			for (IssueType currentIssueType : allIssueTypes) {
				String currentIssueTypeName = currentIssueType.getName();
				pluginSettings.put(PLUGIN_STORAGE_KEY + "." + currentIssueTypeName, req.getParameter(currentIssueTypeName));
			}
			response.sendRedirect("test");
	  }

	  
	  private void redirectToLogin(HttpServletRequest request, HttpServletResponse response) throws IOException
	  {
	    response.sendRedirect(loginUriProvider.getLoginUri(getUri(request)).toASCIIString());
	  }
	   
	  private URI getUri(HttpServletRequest request)
	  {
	    StringBuffer builder = request.getRequestURL();
	    if (request.getQueryString() != null)
	    {
	      builder.append("?");
	      builder.append(request.getQueryString());
	    }
	    return URI.create(builder.toString());
	  }
	  
	  
}
