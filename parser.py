import xml.etree.ElementTree as xml
from openalea.plantgl.all import * 

class Parser():
	"""For reading an XML file"""
	def __init__(self, fn = None):
		"""XML variables"""
		self.text = ""
		self.attribute = {}

		"""Meshes variables"""
		self.compteurMesh = 0
		self.compteurFaces = 0
		self.lesMesh = {}
		self.points = []
		self.normals = []
		self.indices = []

		"""Materials variables"""
		self.compteurMat = 0
		self.lesMaterials = dict()


	def parse(self, fn):
		"""fichier = open(fn)"""
		doc = xml.parse(fn)
		root = doc.getroot()

		self.dispatch(root)
	

	def dispatch(self, elt):
		try:
			self.text = elt.text
			self.attribute.clear()
			self.attribute = elt.attrib
			return self.__getattribute__(elt.tag)(elt.getchildren(), **elt.attrib, )
		except Exception as e:
			print(e)
			raise Exception("Unvalid element %s" % elt.tag)
	
	def opf(self, elts, **props):
		# print("Opf")
		for elt in elts:
            		self.dispatch(elt)	

	def meshBDD(self, elts, **props):
		# print("MeshBDD")
		for elt in elts:
            		self.dispatch(elt)

	def mesh(self, elts, **props):
		# print("Mesh")	
		print("tic")
		for elt in elts:
            		self.dispatch(elt)

		print("tac")

		self.lesMesh[self.attribute["Id"]] = TriangleSet(pointList = self.points, indexList= self.indices)
		self.compteurMesh = compteurMesh + 1


	def points(self, elts, **props):
		# print("Points")

		lesPoints = text.split("	")

		listePoints = []
		self.points = []
		
		"""# print(text)"""
		for point in lesPoints:
			"""# print(point)"""
			if point != '\n' and len(point) > 0:
				nombre = point.split("E")
				"""# print(nombre)"""
				if len(nombre) == 2:
					resultat = float(nombre[0]) * pow(10.0, float(nombre[1]))
				else:
					resultat = float(nombre[0])	
				listePoints.append(resultat)

		for i in range(int(len(listePoints) / 3)):
			self.points.append((listePoints[i * 3], listePoints[i * 3 + 1], listePoints[i * 3 + 2]))

	def normals(self, elts, **props):
		# print("Normals")
		
		lesNormales = text.split("	")

		listeNormales = []
		self.normals = []
		
		"""# print(text)"""
		for normale in lesNormales:
			"""# print(normale)"""
			if normale != '\n' and len(normale) > 0:
				nombre = normale.split("E")
				"""# print(nombre)"""
				if nombre[0] == "NaN":
					resultat = 0.0				
				elif len(nombre) == 2:
					resultat = float(nombre[0]) * pow(10.0, float(nombre[1]))
				else:
					resultat = float(nombre[0])
				listeNormales.append(resultat)

		for i in range(int(len(listeNormales) / 3)):
			self.normals.append((listeNormales[i * 3], listeNormales[i * 3 + 1], listeNormales[i * 3 + 2]))

	def textureCoords(self, elts, **props):
		# print("Texture coodinates")
		pass

	def faces(self, elts, **props):
		# print("Faces")	

		self.compteurFaces = 0
		self.indices = []

		for elt in elts:
            		self.dispatch(elt)

	def face(self, elts, **props):
		# print("Face")

		lesIndices = text.split("	")

		face = []
		
		"""# print(text)"""
		for indice in lesIndices:
			"""# print(indice)"""
			if indice != '\n' and len(indice) > 0:
				face.append(int(indice))
		
		self.indices.append((face[0], face[1], face[2]))

	def materialBDD(self, elts, **props):
		# print("MaterialBDD")
		for elt in elts:
            		self.dispatch(elt)

	def material(self, elts, **props):
		# print("Material")
		pass

	def shapeBDD(self, elts, **props):
		# print("ShapeBDD")
		for elt in elts:
            		self.dispatch(elt)

	def shape(self, elts, **props):
		# print("Shape")
		pass

	def attributeBDD(self, elts, **props):
		# print("AttributeBDD")
		for elt in elts:
            		self.dispatch(elt)

	def attribute(self, elts, **props):
		# print("Attribute")
		pass

	def topology(self, elts, **props):
		# print("Topology")
		for elt in elts:
            		self.dispatch(elt)

	def geometry(self, elts, **props):
		# print("Geometry")
		for elt in elts:
            		self.dispatch(elt)

	def shapeIndex(self, elts, **props):
		# print("shapeIndex")
		pass

	def mat(self, elts, **props):
		# print("mat")
		pass

	def dUp(self, elts, **props):
		# print("dUp")
		pass

	def dDwn(self, elts, **props):
		# print("dDwn")
		pass

	def Orthotropy(self, elts, **props):
		# print("Orthotropy")
		pass

	def InternodeRank(self, elts, **props):
		# print("InternodeRank")
		pass

	def branch(self, elts, **props):
		# print("Branch")
		pass

	def decomp(self, elts, **props):
		# print("Decomp")
		for elt in elts:
            		self.dispatch(elt)

	def XEuler(self, elts, **props):
		# print("XEuler")
		pass

	def YEuler(self, elts, **props):
		# print("YEuler")
		pass

	def Length(self, elts, **props):
		# print("Length")
		pass

	def Width(self, elts, **props):
		# print("Width")
		pass

	def Toplength(self, elts, **props):
		# print("Topength")
		pass

	def Topwidth(self, elts, **props):
		# print("Topwidth")
		pass

	def follow(self, elts, **props):
		# print("Follow")
		for elt in elts:
            		self.dispatch(elt)

	def Name(self, elts, **props):
		# print("Name")
		pass

leParser = Parser()
leParser.parse("DA1_Average_MAP_90.opf")

print(lesMesh)
