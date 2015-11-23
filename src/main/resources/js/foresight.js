var pluginSettings;
var viewType;

function init_plugin_settings() {
	AJS.$.get(contextPath + "/plugins/servlet/foresight-settings", function(data) {
		pluginSettings = JSON.parse(data);
		show_legend();
	})
// 	.fail(function(){
// 	  pluginSettings = null; //TODO Graph failed to load error msg
// 	});
}

function update_description_types() {
	var descriptionType = AJS.$('#dependencyDescriptionTypes').val();
	d3.selectAll(".foresight-text").text(function(data) {
		var linkLabel;
		switch (descriptionType) {
		case 'none':
			linkLabel = "";
			break;
		case 'name':
			linkLabel = data.name;
			break;
		case 'inward':
			linkLabel = data.inward;
			break;
		case 'outward':
			linkLabel = data.outward;
			break;
		}
		return linkLabel;
	});
}

function show_graph() {
	
	var issue_id, project_id;
	switch (viewType){
	  case 'issueNav':
	      issue_id = JIRA.API.IssueSearch.getActiveIssueId();
	      break;
	  case 'projectTab':
	      project_id = JIRA.API.Projects.getCurrentProjectId();
	      break;
	  case 'issue':
	      issue_id = JIRA.Issue.getIssueId();
	      break;
	}
	//var project_id=AJS.$("input[name=projectId]").val();
	//var issue_id=AJS.$("input[name=issueId]").val();
	var includeInwardLinks=AJS.$("#issue-dependency-viewer-form input[name=includeInward]").is(':checked');
	var includeOutwardLinks=AJS.$("#issue-dependency-viewer-form input[name=includeOutward]").is(':checked');
	var includeSystemLinks=AJS.$("#issue-dependency-viewer-form input[name=includeSystemLinks]").is(':checked');
	
	AJS.$.get(contextPath + "/plugins/servlet/foresight-dependency-graph" 
			+ "?currentIssueId="+issue_id
			+ "&currentProjectId="+project_id
			+ "&includeOutward="+includeOutwardLinks
			+ "&includeInward="+includeInwardLinks 
			+ "&includeSystemLinks="+includeSystemLinks,function(data) {
		
		if(data == undefined || data == ""){
			return;
		}
		
		var graph = JSON.parse(data);
		
		var height = 400;
		var heightGrowth = (graph.nodes.length + graph.links.length) * 5;
		
		height = height + heightGrowth;
		
		// clean up old svg object
		d3.select("#issue-dependency-viewer-graph").remove();
		var container = d3.select('#issue-dependency-viewer-graph-container');
		  
		var svg = container.append('svg:svg')
			.attr("id", "issue-dependency-viewer-graph")
			.attr("width", 1000)
			.attr("height", height);
		  
		
			
		var links = graph.links;
		var nodes = graph.nodes;
			
		// link the actual nodes
		links.forEach(function(link) {
			  link.source = nodes[link.source];
			  link.target = nodes[link.target];
		});
			
		// create the layout
		var force = d3.layout.force()
		    .nodes(nodes)
		    .links(links)
		    .size([1000, height])
		    .linkDistance(120)
		    .charge(-150)
		    .gravity(.05)
		    .distance(100)
		    .on("tick", tick)
		    .start();

		var node_drag = d3.behavior.drag()
			.on("dragstart", dragstart)
			.on("drag", dragmove)
			.on("dragend", dragend);
		
	    function dragstart(d, i) {
	        force.stop();
	    }

	    function dragmove(d, i) {
	        d.px += d3.event.dx;
	        d.py += d3.event.dy;
	        d.x += d3.event.dx;
	        d.y += d3.event.dy; 
	        tick();
	    }

	    function dragend(d, i) {
	        d.fixed = true; // of course set the node to fixed so the force doesn't include the node in its auto positioning stuff
	        tick();
	        force.resume();
	    }
	    
		svg.append("svg:defs").selectAll("marker")
		    .data(["resolved"])
		  .enter().append("svg:marker")
		    .attr("id", String)
		    .attr("viewBox", "0 -5 10 10")
		    .attr("refX", 15)
		    .attr("refY", -1.5)
		    .attr("markerWidth", 6)
		    .attr("markerHeight", 6)
		    .attr("orient", "auto")
		  .append("svg:path")
		    .attr("d", "M0,-5L10,0L0,5");
		
		svg.append("svg:defs").selectAll("marker")
		    .data(["this"])
		  .enter().append("svg:marker")
		    .attr("id", String)
		    .attr("viewBox", "0 -5 10 10")
		    .attr("refX", 18)
		    .attr("refY", -2)
		    .attr("markerWidth", 9)
		    .attr("markerHeight", 9)
		    .attr("orient", "auto")
		  .append("svg:path")
		    .attr("d", "M0,-5L10,0L0,5");

		var path = svg.append("svg:g").selectAll("path")
	    	.data(force.links())
	      .enter().append("svg:path")
	        .attr("class", function(d) { return "link normal" })
	        .attr("class", function(d) {
	        	if (d.systemLink) {
	        		return "foresight-path-link-system";
	        	} else {
	        		return "foresight-path-link";
	        	}
	        })
	        .attr("marker-end", function(d) { 
	        	if(issue_id == d.target.key){
	        		return "url(#this)";
	        	}else{
	        		return "url(#" + d.type + ")"; 
	        	}
	        });

		var circle = svg.append("svg:g").selectAll("circle")
		    .data(force.nodes())
		    .enter()
		    
		    .append("svg:a") // http://stackoverflow.com/questions/13728513/how-to-include-links-in-a-d3-visualisations-text-element
			.attr("xlink:href", function(d){
				return contextPath + "/browse/" + d.name;
			})
		    
		    .append("svg:circle")
		    .attr("class", function(d) { if (d.status == 'Resolved' || d.status == 'Closed' || d.status == 'Done') { return "foresight-circle foresight-resolved" }
		    else {return "foresight-circle"} })
		    .attr("fill", function(data) {
//		    	if (issue_id == data.key) {
//		    		return pluginSettings.nodecolors.This;
//		    	}
//		    	else {
		    		return pluginSettings.nodecolors[data.type];
//		    	}
		    	})
		    .attr("r", function(d){
		    	//Make the circle bigger if the it's the issue whose browse context it is.
		    	if (issue_id == d.key) {
	    			return 14;
	    		}else{
	    			return 7;
	    		}
		    })
		    .call(node_drag);
		
		circle
		.append("svg:title")
		.text(function(d) { return d.name + ": " +d.summary; });

		var labels = svg.selectAll('text')
		    .data(graph.links)
		  .enter().append('text')
		    .attr("x", function(d) { return (d.source.y + d.target.y) / 2; }) 
		    .attr("y", function(d) { return (d.source.x + d.target.x) / 2; }) 
		    .attr("text-anchor", "middle")
		    .attr("class", "foresight-text");
			
		var link = svg.append("svg:g").selectAll("g")
		    .data(force.nodes())
		    .enter().append("svg:g");
		
		var text = svg.append("svg:g").selectAll("g")
		    .data(force.nodes())
		  .enter().append("svg:g");
		
		// show the task key
		text.append("svg:text")
		    .attr("x", 8)
		    .attr("y", ".31em")
		    .text(function(d) { 
		    	if (issue_id == d.key) {
		    		return null;
		    	}else{
		    		return d.name; 
		    	}
		    })
		    .attr("class", function(d) { if (d.status == 'Resolved' || d.status == 'Closed' || d.status == 'Done') { return "foresight-text-striked foresight-resolved" } })
			.append("svg:title")
			.text(function(d) { return d.summary; });
		
		text.append("svg:line")
		    .attr("x1", 5)
		    .attr("y1", 0)
		    .attr("x2", function() {return this.parentNode.firstChild.getComputedTextLength()+5;})
		    .attr("y2", 0)
		    .attr("style", function(d) {
		    	//var associatedLabel = this.parentNode.firstChild
		    	if (d.status == 'Resolved' || d.status == 'Closed' || d.status == 'Done') {
		    		return "stroke:rgb(0,0,0);stroke-width:1"
		    	} else {
		    		return "display:none"
		    	}
		    });

		
		update_description_types();
		
		// curve the arcs between the nodes to correctly show cycles.
		function tick() {
		  path.attr("d", function(d) {
		    var dx = d.target.x - d.source.x,
		        dy = d.target.y - d.source.y,
		        dr = Math.sqrt(dx * dx + dy * dy);
		    return "M" + d.source.x + "," + d.source.y + "A" + dr + "," + dr + " 0 0,1 " + d.target.x + "," + d.target.y;
		  });

		  circle.attr("transform", function(d) {
		    return "translate(" + d.x + "," + d.y + ")";
		  });

		  text.attr("transform", function(d) {
		    return "translate(" + d.x + "," + d.y + ")";
		  });
		  
		  labels.attr("x", function(d) { return (d.source.x + d.target.x) / 2; }) 
	        .attr("y", function(d) { return (d.source.y + d.target.y) / 2; }) 
		}
  });
}

function show_legend(){
	//draw legend
	var canvas = document.getElementById("foresight-legend");
	var ctx = canvas.getContext("2d");
	ctx.clearRect(0, 0, canvas.width, canvas.height);
	ctx.lineWidth = 4;
	ctx.strokeStyle = 'black';
	ctx.font="14px Arial";
	var startX = 10, startY = 10, count = 0;
	for(var node in pluginSettings.nodecolors){
        count++;
	    ctx.fillStyle = pluginSettings.nodecolors[node];
		ctx.fillRect(startX, startY, 75, 50);
	    ctx.strokeRect(startX, startY, 75, 50);
	    ctx.fillStyle = "#000";
	    ctx.fillText(node, startX, startY + 70);
	    //ctx.fill();
	   startX += 125;
	  //Do a line break every 7
	  if(count%7 == 0){
	      startY += 100;
	      startX = 10;
	  }
	}
}

AJS.toInit(function(){
	AJS.$(document).ready(function(){
	  //TODO Better way of determining viewType
		if(JIRA.API !== undefined){
		  if(JIRA.API.IssueSearch !== undefined && JIRA.ViewIssueTabs !== undefined){
		    //issue navigator
		  viewType = 'issueNav';
		  //JIRA.ViewIssueTabs.onTabReady(function() {
		  JIRA.ViewIssueTabs.onTabReady(function() {
			      //run foresight_show if this is the first time loading an issue, or loading a new issue.
			    if(AJS.$('div#foresight, canvas#foresight-legend').length == 2){
				    foresight_show();
			      }
			});
		  }else if(JIRA.API.Projects !== undefined){
		  //project tab
		    viewType = 'projectTab';
		    AJS.$('input#includeOutward, input#includeInward').parent().hide();
		    foresight_show()
		  }
		}else if(JIRA.Issue !== undefined){
		  //issue view
		    viewType = 'issue';
		    foresight_show()
		}
		
	});
});

function foresight_show(){
  
	if(!pluginSettings){
	  init_plugin_settings();
	}else{
	  show_legend();
	}

	show_graph();
	
	// on change functions of the show inward/outward checkboxes
	AJS.$("#issue-dependency-viewer-form input[name=includeInward]").change(function(){
		show_graph();
	});
	AJS.$("#issue-dependency-viewer-form input[name=includeOutward]").change(function(){
		show_graph();
	});
	AJS.$("#issue-dependency-viewer-form input[name=includeSystemLinks]").change(function(){
		show_graph();
	});
	
	// on change function for the description types drop-down.
	AJS.$("#dependencyDescriptionTypes").change(function(){
		update_description_types();
	});
}