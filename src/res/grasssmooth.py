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

queryRes = grass.run_command("v.generalize", input=sys.argv[5], type=sys.argv[6], layer=sys.argv[7], flags=sys.argv[8], method=sys.argv[9], threshold=sys.argv[10], look_ahead=sys.argv[11], reduction=sys.argv[12], slide=sys.argv[13], angle_thresh=sys.argv[14], degree_thresh=sys.argv[15], closeness_thresh=sys.argv[16], betweeness_thresh=sys.argv[17], alpha=sys.argv[18], beta=sys.argv[19], iterations=sys.argv[20], output=sys.argv[21])

print queryRes