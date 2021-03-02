import sys, urllib2, urllib, zlib
from urllib2 import URLError, HTTPError

#read graph
f = file("sample.xeg")
graph = f.read()
f.close()
#graph = zlib.compress(graph, 1)

#read xl
g = file("sample.xl")
xl = g.read()
g.close()
#xl = zlib.compress(xl)

#prepare data
url = 'http://localhost:58070'
data = urllib.urlencode({'graph': graph, 'xlcode': xl, 'command': 'init;3run'})

#send
req = urllib2.Request(url)
try:
	fd = urllib2.urlopen(req, data)
	print fd.read()
except HTTPError, e:
	print "HTTPError code: "+str(e.code)
	print e.read()
except URLError, e:
	print "URLError: "+str(e.reason)
except:
	print "Error: ", sys.exc_info()[0]

