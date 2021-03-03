import xml.etree.ElementTree as xml
from openalea.plantgl.all import * 

class Parser():
	"""For reading an XML file"""

	def parse(self, fn):
		"""fichier = open(fn)"""
		doc = xml.parse(fn)
		root = doc.getroot()

		self.dispatch(root)
	

	def dispatch(self, elt):
		try:
			global text
			text = elt.text
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

		global lesMesh
		global compteur
		global points
		global normals
		global indices

		if compteur > 0:
			lesMesh.append(QuadSet(points, normals, indices))		
		compteur = compteur + 1

		for elt in elts:
            		self.dispatch(elt)

	def points(self, elts, **props):
		print("Points")
		
		global points
		global text

		lesPoints = text.split("	")

		points = []
		
		print(text)
		for point in lesPoints:
			print(point)
			if point != '\n' and len(point) > 0:
				nombre = point.split("E")
				if len(nombre) == 2:
					resultat = float(nombre[0]) * pow(10, float(nombre[1]))
				else:
					resultat = float(nombre[0])	
				points.append(resultat)

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

compteur = 0
lesMesh = []
points = []
normals = []
indices = []
text = ""
leParser = Parser()
leParser.parse("DA1_Average_MAP_90.opf")

