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
			return self.__getattribute__(elt.tag)(elt.getchildren(), **elt.attrib, )
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

	def textureCoords(self, elts, **props):
		print("Texture coodinates")

	def faces(self, elts, **props):
		print("Faces")

	def materialBDD(self, elts, **props):
		print("MaterialBDD")
		for elt in elts:
            		self.dispatch(elt)

	def material(self, elts, **props):
		print("Material")

	def shapeBDD(self, elts, **props):
		print("ShapeBDD")
		for elt in elts:
            		self.dispatch(elt)

	def shape(self, elts, **props):
		print("Shape")

	def attributeBDD(self, elts, **props):
		print("AttributeBDD")
		for elt in elts:
            		self.dispatch(elt)

	def attribute(self, elts, **props):
		print("Attribute")

	def topology(self, elts, **props):
		print("Topology")
		for elt in elts:
            		self.dispatch(elt)

	def geometry(self, elts, **props):
		print("Geometry")
		for elt in elts:
            		self.dispatch(elt)

	def shapeIndex(self, elts, **props):
		print("shapeIndex")

	def mat(self, elts, **props):
		print("mat")

	def dUp(self, elts, **props):
		print("dUp")

	def dDwn(self, elts, **props):
		print("dDwn")

	def Orthotropy(self, elts, **props):
		print("Orthotropy")

	def InternodeRank(self, elts, **props):
		print("InternodeRank")

	def branch(self, elts, **props):
		print("Branch")

	def decomp(self, elts, **props):
		print("Decomp")
		for elt in elts:
            		self.dispatch(elt)

	def XEuler(self, elts, **props):
		print("XEuler")

	def YEuler(self, elts, **props):
		print("YEuler")

	def Length(self, elts, **props):
		print("Length")

	def Width(self, elts, **props):
		print("Width")

	def Toplength(self, elts, **props):
		print("Topength")

	def Topwidth(self, elts, **props):
		print("Topwidth")

	def follow(self, elts, **props):
		print("Follow")
		for elt in elts:
            		self.dispatch(elt)

	def Name(self, elts, **props):
		print("Name")


leParser = Parser()
leParser.parse("DA1_Average_MAP_90.opf")
