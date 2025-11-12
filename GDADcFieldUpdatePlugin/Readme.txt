---------------------------------------------------------------------------------------------------------------------------------------
GDADcFieldUpdatePlugin
---------------------------------------------------------------------------------------------------------------------------------------
12.11.25
            Updates a given DC Field in Archives NZ (GDA) IEs - dcterms:isPartOf and dcterms:provenance
            The field to be updated is selected form a dropdown menu at the time of running.
            Takes a CSV file with a list of IEs. Each row contains: an IE Number, the current data to replace, and the
            new data to replace it with.

INITIAL PLUGIN PARAMETERS
=========================
inFileNameAndPath	: Input file name and full path with the R Number list (could sit on Rosetta server or in a location Rosetta can access, example /ndha/dps_export/agency_code_input.csv)
dcField             : The DC Field to be updated

VALUES RETRIEVED FROM THE CSV
=============================
IE              : the IE PID
Current data	: the existing data in the DC field to be replaced
New data    	: The new data to be inserted into the DC field

HOW TO USE
=============================
instal the plugin as custom plugin to your Rosetta instance
create a Task chain using this plugin
create a Set of IEs you want to change in Rosetta (Manage Sets and Processes)
create a Process with the above Task chain
run the Process, connect it to the the above Set and point it to the CSV location (in the Process config field of "Input File name and Location")

---------------------------------------------------------------------------------------------------------------------------------------
UAT Plugins jar location
	/exlibris1/operational_storage/plugins/custom/GDADcFieldUpdatePlugin.jar
	
---------------------------------------------------------------------------------------------------------------------------------------
