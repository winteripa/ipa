import os
import sys

gisbase = os.environ['GISBASE'] = sys.argv[1]
gisdbase = sys.argv[2]
location = sys.argv[3]
mapset = sys.argv[4]

sys.path.append(os.path.join(os.environ['GISBASE'], "etc", "python"))

import grass.script as grass
import grass.script.setup as gsetup
 
gsetup.init(gisbase,
            gisdbase, location, mapset)

queryRes = grass.run_command("v.out.ogr", input=sys.argv[5], type=sys.argv[6], layer=sys.argv[7], format=sys.argv[8], dsn=sys.argv[9], olayer=sys.argv[10], flags=sys.argv[11])

print queryRes