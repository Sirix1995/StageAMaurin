import xml.etree.ElementTree as xml
from openalea.plantgl.all import * 

class Parser():
	"""For reading an XML file"""
	def __init__(self, fn = None):
		"""XML variables"""
		self.text = ""
		self.lesAttrib = {}

		"""Meshes variables"""
		self.lesMesh = {}
		self.tPoints = []
		self.tNormals = []
		self.tIndices = []

		"""Materials variables"""
		self.lesMaterials = {}
		self.tEmission = Color3()
		self.tAmbient = Color3()
		self.tDiffuse = 0.0
		self.tSpecular = Color3()
		self.tShininess = 0.0

		"""Shape variables"""
		self.lesShapes = {}
		self.tMeshID = ""
		self.tMatID = ""


	def parse(self, fn):
		"""fichier = open(fn)"""
		doc = xml.parse(fn)
		root = doc.getroot()

		self.dispatch(root)
	

	def dispatch(self, elt):
		try:
			self.text = elt.text
			self.lesAttrib.clear()
			self.lesAttrib = elt.attrib
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
		print("Parsing Meshes...")

		for elt in elts:
            		self.dispatch(elt)

	def mesh(self, elts, **props):
		# print("Mesh")	

		identifiant = self.lesAttrib["Id"]

		for elt in elts:
            		self.dispatch(elt)

		self.lesMesh[identifiant] = TriangleSet(pointList = self.tPoints, indexList= self.tIndices)

	def points(self, elts, **props):
		# print("Points")

		lesPoints = self.text.split("	")

		listePoints = []
		self.tPoints = []
		
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
			self.tPoints.append((listePoints[i * 3], listePoints[i * 3 + 1], listePoints[i * 3 + 2]))

	def normals(self, elts, **props):
		# print("Normals")
		
		lesNormales = self.text.split("	")

		listeNormales = []
		self.tNormals = []
		
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
			self.tNormals.append((listeNormales[i * 3], listeNormales[i * 3 + 1], listeNormales[i * 3 + 2]))

	def textureCoords(self, elts, **props):
		# print("Texture coodinates")
		pass

	def faces(self, elts, **props):
		# print("Faces")
	
		self.tIndices = []

		for elt in elts:
            		self.dispatch(elt)

	def face(self, elts, **props):
		# print("Face")

		lesIndices = self.text.split("	")

		face = []
		
		"""# print(text)"""
		for indice in lesIndices:
			"""# print(indice)"""
			if indice != '\n' and len(indice) > 0:
				face.append(int(indice))
		
		self.tIndices.append((face[0], face[1], face[2]))

	def materialBDD(self, elts, **props):
		# print("MaterialBDD")
		print("Parsing Materials...")

		for elt in elts:
            		self.dispatch(elt)

	def material(self, elts, **props):
		# print("Material")
		
		identifiant = self.lesAttrib["Id"]

		for elt in elts:
            		self.dispatch(elt)

		self.lesMaterials[identifiant] = Material(str(identifiant), self.tAmbient, self.tDiffuse, self.tSpecular, self.tEmission, self.tShininess)

	def emission(self, elts, **props):
	
		couleur = self.text.split("	")

		liste = []
		
		"""# print(text)"""
		for nombre in couleur:
			"""# print(indice)"""
			if nombre != '\n' and len(nombre) > 0:
				liste.append(float(nombre))
		self.tEmission = Color3(int(liste[0] * 255), int(liste[1] * 255), int(liste[2] * 255))

	def ambient(self, elts, **props):
	
		couleur = self.text.split("	")

		liste = []
		
		"""# print(text)"""
		for nombre in couleur:
			"""# print(indice)"""
			if nombre != '\n' and len(nombre) > 0:
				liste.append(float(nombre))
		self.tAmbient = Color3(int(liste[0] * 255), int(liste[1] * 255), int(liste[2] * 255))

	def diffuse(self, elts, **props):
	
		couleur = self.text.split("	")

		liste = []
		
		"""# print(text)"""
		for nombre in couleur:
			"""# print(indice)"""
			if nombre != '\n' and len(nombre) > 0:
				liste.append(float(nombre))
		self.tDiffuse = (self.tEmission.red + self.tEmission.green + self.tEmission.blue) / (liste[0] + liste[1] + liste[2])

	def specular(self, elts, **props):

		couleur = self.text.split("	")

		liste = []
		
		"""# print(text)"""
		for nombre in couleur:
			"""# print(indice)"""
			if nombre != '\n' and len(nombre) > 0:
				liste.append(float(nombre))
		self.tSpecular = Color3(int(liste[0] * 255), int(liste[1] * 255), int(liste[2] * 255))

	def shininess(self, elts, **props):

		couleur = self.text.split("	")

		liste = []
		
		"""# print(text)"""
		for nombre in couleur:
			"""# print(indice)"""
			if nombre != '\n' and len(nombre) > 0:
				liste.append(float(nombre))
		self.tShininess = liste[0] / 10

	def shapeBDD(self, elts, **props):
		# print("ShapeBDD")
		print("Parsing Shapes...")

		for elt in elts:
            		self.dispatch(elt)

	def shape(self, elts, **props):
		# print("Shape")

		identifiant = self.lesAttrib["Id"]
		
		for elt in elts:
            		self.dispatch(elt)

		self.lesShapes[identifiant] = Shape(self.lesMesh[self.tMeshID], self.lesMaterials[self.tMatID])

	def name(self, elts, **props):
		pass
	
	def meshIndex(self, elts, **props):
		self.tMeshID = self.text
	
	def materialIndex(self, elts, **props):
		self.tMatID = self.text

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

formes = list(leParser.lesShapes.values())

laScene = Scene(formes)
Viewer.display(laScene)
