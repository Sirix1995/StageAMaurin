import xml.etree.ElementTree as xml

class Parser():
	"""Pour lire le fichier XML"""

	def parse(self, fn):
		"""fichier = open(fn)"""
		doc = xml.parse(fn)
		root = doc.getroot()

		self.dispatch(root)
	

	def dispatch(self, elt):
        	try:
            		return self.__getattribute__(elt.tag)(elt.getchildren(), **elt.attrib)
        	except Exception as e:
            		print(e)
            		raise Exception("Unvalid element %s" % elt.tag)
	
	def opf():
		print("Opf")	

	def meshBDD():
		print("MeshBDD")

	def mesh():
		print("Mesh")

	def points():
		print("Points")

	def normals():
		print("Normals")

	def texturecoord():
		print("Texture coodinates")

	def faces():
		print("Faces")


leParser = Parser()
leParser.parse("DA1_Average_MAP_90.opf")
