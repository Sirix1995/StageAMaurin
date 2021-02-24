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
	
	def opf(self, elts, **props):
		print("Opf")
		for elt in elts:
            		self.dispatch(elt)	

	def meshBDD(self, elts, **props):
		print("MeshBDD")
		for elt in elts:
            		self.dispatch(elt)

	def mesh(self, elts, **props):
		print("Mesh")
		for elt in elts:
            		self.dispatch(elt)

	def points(self, elts, **props):
		print("Points")

	def normals(self, elts, **props):
		print("Normals")

	def texturecoord(self, elts, **props):
		print("Texture coodinates")

	def faces(self, elts, **props):
		print("Faces")

leParser = Parser()
leParser.parse("DA1_Average_MAP_90.opf")
