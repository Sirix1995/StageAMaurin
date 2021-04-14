import xml.etree.ElementTree as xml
from openalea.plantgl.all import * 

class Parser():
	"""For reading an XML file"""
	def __init__(self, fn = None):
		"""XML variables"""
		self.text = ""
		self.lesAttrib = {}

		"""Meshes variables"""
		self.meshList = {}
		self.tPoints = []
		self.tNormals = []
		self.tIndexes = []

		"""Materials variables"""
		self.materialsList = {}
		self.tEmission = Color3()
		self.tAmbient = Color3()
		self.tDiffuse = 0.0
		self.tSpecular = Color3()
		self.tShininess = 0.0

		"""Shape variables"""
		self.tuplesList = {}
		self.shapeList = {}
		self.tMeshID = ""
		self.tMatID = ""

		"""Geometry variables"""
		self.tShapeIndex = "0"
		self.tMatrix = Matrix4()
		self.transformations = []
		self.top = 0.0
		self.bottom = 0.0
		self.upTrue = False
		self.dwnTrue = False
		self.matTrue = False

	def readNumbers(self, text):
		textList = self.text.split("	")

		numberList = []

		"""# print(text)"""
		for numberText in textList:
			"""# print(normale)"""
			if numberText != '\n' and len(numberText) > 0:
				number = numberText.split("E")
				"""# print(nombre)"""
				if number[0] == "NaN":
					result = 0.0				
				elif len(number) == 2:
					result = float(number[0]) * pow(10.0, float(number[1]))
				else:
					result = float(number[0])
				numberList.append(result)
		return numberList


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
			self.__getattribute__(elt.tag)(list(elt.getchildren()), **elt.attrib)
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

		self.meshList[identifiant] = TriangleSet(pointList = self.tPoints, indexList= self.tIndexes)

	def points(self, elts, **props):
		# print("Points")

		listePoints = self.readNumbers(self.text)
		self.tPoints = []

		for i in range(int(len(listePoints) / 3)):
			self.tPoints.append((listePoints[i * 3], listePoints[i * 3 + 1], listePoints[i * 3 + 2]))

	def normals(self, elts, **props):
		# print("Normals")

		listeNormales = self.readNumbers(self.text)
		self.tNormals = []

		for i in range(int(len(listeNormales) / 3)):
			self.tNormals.append((listeNormales[i * 3], listeNormales[i * 3 + 1], listeNormales[i * 3 + 2]))

	def textureCoords(self, elts, **props):
		# print("Texture coodinates")
		pass

	def faces(self, elts, **props):
		# print("Faces")
	
		self.tIndexes = []

		for elt in elts:
            		self.dispatch(elt)

	def face(self, elts, **props):
		# print("Face")

		face = self.readNumbers(self.text)
		
		assert(len(face)==3)

		self.tIndexes.append((int(face[0]), int(face[1]), int(face[2])))

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

		self.materialsList[identifiant] = Material(str(identifiant), self.tAmbient, self.tDiffuse, self.tSpecular, self.tEmission, self.tShininess)

	def emission(self, elts, **props):
		# print("Emission")

		liste = self.readNumbers(self.text)
		
		assert(len(liste)==4)

		self.tEmission = Color3(int(liste[0] * 255), int(liste[1] * 255), int(liste[2] * 255))

	def ambient(self, elts, **props):
		#print("Ambient")

		liste = self.readNumbers(self.text)
		
		assert(len(liste)==4)

		self.tAmbient = Color3(int(liste[0] * 255), int(liste[1] * 255), int(liste[2] * 255))

	def diffuse(self, elts, **props):
		#print("Diffuse")
		
		liste = self.readNumbers(self.text)
		
		assert(len(liste)==4)

		self.tDiffuse = (self.tEmission.red + self.tEmission.green + self.tEmission.blue) / (liste[0] + liste[1] + liste[2])

	def specular(self, elts, **props):
		#print("Specular")

		liste = self.readNumbers(self.text)
		
		assert(len(liste)==4)

		self.tSpecular = Color3(int(liste[0] * 255), int(liste[1] * 255), int(liste[2] * 255))

	def shininess(self, elts, **props):
		#print("Shininess")
		
		liste = self.readNumbers(self.text)
		
		assert(len(liste)==1)

		self.tShininess = liste[0] / 100

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

#		self.shapeList[identifiant] = Shape(self.meshList[self.tMeshID], self.materialsList[self.tMatID])
		self.tuplesList[identifiant] = (self.meshList[self.tMeshID], self.materialsList[self.tMatID])

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

		temp = self.tuplesList[self.tShapeIndex][0]

		if self.upTrue and self.dwnTrue:
			# print(self.tShapeIndex, " ", self.top, " ", self.bottom)
			temp = Tapered(self.bottom, self.top, temp)

		if self.matTrue:
			temp = Scaled(self.transformations[0], temp)
			temp = EulerRotated(self.transformations[1][0], self.transformations[1][1], self.transformations[1][2], temp)
			temp = Translated(self.transformations[2], temp)

		self.shapeList[self.tShapeIndex] = Shape(temp, self.tuplesList[self.tShapeIndex][1])
		self.shapeList[self.tShapeIndex].id = int(self.tShapeIndex)

	def shapeIndex(self, elts, **props):
		# print("shapeIndex")
		self.tShapeIndex = self.text

	def mat(self, elts, **props):
		# print("mat")
		"""self.tMatrix = Matrix4( (0,0,0,0),
					(0,0,0,0),
					(0,0,0,0),
					(0,0,0,0))

		numberList = self.text.split("	")		

		x = 0
		y = 0
		for nombre in numberList:
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
				self.tMatrix[x, y] = resultat
				x += 1
				if x == 5:
					y +=1
					x = 0 """

		numberList = list(map(float,self.text.strip().split()))
		assert(len(numberList)==12)
		self.tMatrix = Matrix4(Vector4(numberList[0],numberList[4],numberList[8],0),
							   Vector4(numberList[1],numberList[5],numberList[9],0),
							   Vector4(numberList[2],numberList[6],numberList[10],0),
							   Vector4(numberList[3],numberList[7],numberList[11],1))

		self.transformations = self.tMatrix.getTransformation()
		self.matTrue = True



	def dUp(self, elts, **props):
		# print("dUp")
		self.top = 0.5
		if self.text != "NaN":
			self.top = float(self.text)
			self.upTrue = True

	def dDwn(self, elts, **props):
		# print("dDwn")
		self.bottom = 0.5
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

	def StiffnessAngle(self, elts, **props):
		# print("StiffnessAngle")
		pass
	

	def Notes(self, elts, **props):
		# print("Notes")
		pass

leParser = Parser()
leParser.parse("test.opf")

# fTest = [leParser.shapeList["12"]]

formes = list(leParser.shapeList.values())

laScene = Scene(formes)
Viewer.display(laScene)
