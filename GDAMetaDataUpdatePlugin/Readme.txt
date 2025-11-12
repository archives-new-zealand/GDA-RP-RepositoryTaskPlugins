---------------------------------------------------------------------------------------------------------------------------------------
GDAMetaDataUpdatePlugin
---------------------------------------------------------------------------------------------------------------------------------------
12.11.25
            Updates Metadata in Archives NZ (GDA) IEs - dc:identifier field in DC and objectIdentifierValue in DNX.

            Takes a CSV file with a list of IEs. Each row contains: an IE Number, an dc:identifier value to replace, and the dc:identifier value to replace it with.

		 
INITIAL PLUGIN PARAMETERS
=========================
inFileNameAndPath	: Input file name and full path with the identifier list (as CSV). This should be a location which Rosetta can access (on Rosetta server for example, could looks like this /ndha/dps_export/MD_fix.csv )

VALUES RETRIEVED FROM THE CSV
=============================
IE              : the IE PID
Old ID			: the existing value of dc:identifier in the IE
New ID			: The correct/new value of dc:identifier to replace the old value in the IE
comma delimiter, no column names or headlines

HOW TO USE
=============================
instal the plugin as custom plugin to your Rosetta instance
create a Task chain using this plugin
create a Set of IEs you want to change in Rosetta (Manage Sets and Processes)
create a Process with the above Task chain
run the Process, connect it to the the above Set and point it to the CSV location (in the Process config field of "Input File name and Location")

---------------------------------------------------------------------------------------------------------------------------------------
UAT Plugins jar location
	/exlibris1/operational_storage/plugins/custom/GDAMetaDataUpdatePlugin.jar
	
---------------------------------------------------------------------------------------------------------------------------------------
