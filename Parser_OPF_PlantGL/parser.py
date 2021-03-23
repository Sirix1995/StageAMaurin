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

		"""Geometry variables"""
		self.tShapeIndex = "0"
		self.matrice = Matrix4()
		self.transformations = []
		self.top = 0.0
		self.bottom = 0.0
		self.upTrue = False
		self.dwnTrue = False
		self.matTrue = False


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

		print("Parsing Topology...")
		for elt in elts:
            		self.dispatch(elt)

	def geometry(self, elts, **props):
		# print("Geometry")
		self.upTrue = False
		self.dwnTrue = False
		self.matTrue = False

		for elt in elts:
            		self.dispatch(elt)

		if self.upTrue and self.dwnTrue:
			print(self.tShapeIndex, " ", self.top, " ", self.bottom)
			# self.lesShapes[self.tShapeIndex].geometry = Tapered(self.bottom, self.top, self.lesShapes[self.tShapeIndex].geometry)

		if self.matTrue:
			self.lesShapes[self.tShapeIndex].geometry = Scaled(self.transformations[0][0], self.transformations[0][1], self.transformations[0][2], self.lesShapes[self.tShapeIndex].geometry)
			self.lesShapes[self.tShapeIndex].geometry = EulerRotated(self.transformations[1][0], self.transformations[1][1], self.transformations[1][2], self.lesShapes[self.tShapeIndex].geometry)
			self.lesShapes[self.tShapeIndex].geometry = Translated(self.transformations[2][0], self.transformations[2][1], self.transformations[2][2], self.lesShapes[self.tShapeIndex].geometry)

	def shapeIndex(self, elts, **props):
		print("shapeIndex")
		self.tShapeIndex = self.text

	def mat(self, elts, **props):
		# print("mat")
		self.matrice = Matrix4( (0,0,0,0),
					(0,0,0,0),
					(0,0,0,0),
					(0,0,0,0))

		lesNombres = self.text.split("	")		

		x = 0
		y = 0
		for nombre in lesNombres:
			estNombre = False
			if nombre != '\n' and len(nombre) > 0:
				leNombre = nombre.split("E")
				if len(leNombre) == 2:
					resultat = float(leNombre[0]) * pow(10.0, float(leNombre[1]))
					estNombre = True
				else:
					resultat = float(leNombre[0])
					estNombre = True
			if estNombre:
				self.matrice[x, y] = resultat
				x += 1
				if x == 5:
					y +=1
					x = 0
		self.transformations = self.matrice.getTransformation()
		self.matTrue = True



	def dUp(self, elts, **props):
		# print("dUp")
		self.top = 0.0
		if self.text != "NaN":
			self.top = float(self.text)
			self.upTrue = True

	def dDwn(self, elts, **props):
		# print("dDwn")
		self.bottom = 0.0
		if self.text != "NaN":
			self.bottom = float(self.text)
			self.dwnTrue = True

	def Orthotropy(self, elts, **props):
		# print("Orthotropy")
		pass

	def InternodeRank(self, elts, **props):
		# print("InternodeRank")
		pass

	def branch(self, elts, **props):
		# print("Branch")
		for elt in elts:
            		self.dispatch(elt)

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

	def Height(self, elts, **props):
		# print("Height")
		pass

	def TopLength(self, elts, **props):
		# print("TopLength")
		pass

	def TopWidth(self, elts, **props):
		# print("TopWidth")
		pass

	def TopHeight(self, elts, **props):
		# print("TopHeight")
		pass

	def XInsertionAngle(self, elts, **props):
		# print("XInsertionAngle")
		pass

	def YInsertionAngle(self, elts, **props):
		# print("YInsertionAngle")
		pass

	def ZInsertionAngle(self, elts, **props):
		# print("ZInsertionAngle")
		pass

	def rachisLength(self, elts, **props):
		# print("rachisLength")
		pass

	def petioleLength(self, elts, **props):
		# print("petioleLength")
		pass

	def angleA(self, elts, **props):
		# print("angleA")
		pass

	def Cangle(self, elts, **props):
		# print("Cangle")
		pass

	def HorizontalAngle(self, elts, **props):
		# print("HorizontalAngle")
		pass

	def Rank(self, elts, **props):
		# print("Rank")
		pass

	def Side(self, elts, **props):
		# print("Side")
		pass

	def LeafletRank(self, elts, **props):
		# print("LeafletRank")
		pass

	def Plane(self, elts, **props):
		# print("Plane")
		pass

	def StifnessTapering(self, elts, **props):
		# print("StifnessTapering")
		pass

	def StifnessAngle(self, elts, **props):
		# print("StifnessAngle")
		pass

	def RelativePosition(self, elts, **props):
		# print("RelativePosition")
		pass

	def Stifness(self, elts, **props):
		# print("Stifness")
		pass

	def Offset(self, elts, **props):
		# print("Offset")
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
