# AzureDevOps-Get-All-Releases
Get a List/History of all releases in all projects that are going on/completed/succeeded/cancelled..


How to run the program?

1) Update the details in the propFile.properties

	a) update the project name and token, to access the REST API.
##############################################################
##provide the project and token details in the below format.

##project=

##token=

##############################################################

Example :
--> If the AzureDevops link is https://dev.azure.com/srikanthkoya/CustomProject/_release then the project is below:

	project=srikanthkoya/CustomProject
	
--> token, you can generate one to access the REST API.

2) Run the "Main.java" Class in the program, by sending the argument propFile.properties

	Usage: java -jar <jar-name> <propFile.properties>
	
	

The output will generate a .csv file, with the details of

RELEASE_NAME,PROJECT,STATUS,ENVIRONMENT,DATETIME
