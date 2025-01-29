---------------------------------------------------------------------------------------------------------------------------------------
				NDHA - GDADcFieldUpdatePlugin
---------------------------------------------------------------------------------------------------------------------------------------
29.01.25
            Updates a given DC Field in Archives NZ (GDA) IEs.
            The field to be updated is selected form a dropdown menu at the time of running.
            Takes a csv file with a list of IEs. Each row contains: an IE Number, the current data to replace, and the
            new data to replace it with.

INITIAL PLUGIN PARAMETERS
=========================
inFileNameAndPath	: Input file name and full path with the R Number list
dcField             : The DC Field to be updated

VALUES RETRIEVED FROM THE CSV
=============================
IE              : the IE PID
Current data	: the existing data in the DC field to be replaced
New data    	: The new data to be inserted into the DC field


---------------------------------------------------------------------------------------------------------------------------------------
UAT Plugins jar location
	/exlibris1/operational_storage/plugins/custom/GDADcFieldUpdatePlugin.jar
	
---------------------------------------------------------------------------------------------------------------------------------------
