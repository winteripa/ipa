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

queryRes = grass.run_command("v.in.ogr", dsn=sys.argv[5], output=sys.argv[6], flags="zo", snap="-1", min_area="0.0001", overwrite=True)

print queryRes