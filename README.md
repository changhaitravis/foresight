#foresight
Jira add-on to visualize dependencies between issues.


## Goal
The goal of this fork is to create a viable Free and Open-Source version alternative to Issue-Link Graph Visualization Add-ons such as Vivid Trace.

### Why?
Since there was a project out there that already does most of the work and was mentioned by a Twitter Tech blog, I thought I would carry it to completion. Especially since Vivid Trace isn't really great either.

## Features to be implemented in this Fork
The following features should bring it to parity with Vivid Trace
- Project Panel View
- Hover mouse over node to view title of issue
- Click on Node to go to Issue
- Remove a lot of the controls for Enabling naming on Edges and Inward vs. Outward, and just have there be a default that shows everything.
- Perhaps the D3 Circles should be large enough to encompass the Issue name + key?
- Escape/Sanitize the Summaries being sent to D3 as JSON.
- Legend
- Hide (or maybe 'grey out') Completed 'Sets' Switch
- User Configuration JIRA Default Issue Type colors.

### JIRA 7 specific features to be implemented.
Read Issue Type colors from JIRA Software, instead of from the plugin settings.

## Version Support
Because of when the fork was made, I've removed support for JIRA 4.x.x and JIRA 5.x.x
Currently Only Jira 6.4 and Jira 7.1 are supported. However, the entirety of Jira 6 & 7 likely work
