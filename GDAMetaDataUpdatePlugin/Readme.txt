---------------------------------------------------------------------------------------------------------------------------------------
				NDHA - GDAMetaDataUpdatePlugin
---------------------------------------------------------------------------------------------------------------------------------------
20.11.24
            Updates Metadata in Archives NZ (GDA) IEs.
            Takes a csv file a list of IEs. Each row contains: and IE Number, an R Number to replace, and the new R
            number to replace it with.

		    Replaces the R Number in the DC and DNX records.

INITIAL PLUGIN PARAMETERS
=========================
inFileNameAndPath	: Input file name and full path with the R Number list

VALUES RETRIEVED FROM THE CSV
=============================
IE              : the IE PID
Old R Number	: the existing R Number in the IE
New R Number	: The correct R Number to replace the old R Number in the IE


---------------------------------------------------------------------------------------------------------------------------------------
UAT Plugins jar location
	/exlibris1/operational_storage/plugins/custom/GDAMetaDataUpdatePlugin.jar
	
---------------------------------------------------------------------------------------------------------------------------------------
