# Data hub Country Cities

Loads country data from https://datahub.io/core/country-list#data-cli and city data from https://datahub.io/core/world-cities

Links cities to countries. Making fake ISO codes for countries like "Republic of the Congo" which probably should be "Congo, the Democratic Republic of the",CD

Prints out the jsons of countries with cities as a sub array. This is for keeping in a mostly read only cached mongo database.

Thank you Data hub for the data

TODO : map mis spelled countries to actual and hope that data hub cleans up their data. best would be to have ISO code in the cities list.
