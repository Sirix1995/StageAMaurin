
# Redirect path
import os

cdir = os.path.dirname(__file__)
pdir = os.path.join(cdir, "../../groimp")
pdir = os.path.abspath(pdir)

__path__ = [pdir] + __path__[:]

from openalea.groimp.__init__ import *
