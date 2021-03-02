#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <math.h>

#ifdef WIN32
#include <windows.h>
#endif

#ifndef MACOSX
#include <GL/gl.h>
#include <GL/glu.h>
#else
#include <OpenGL/gl.h>
#include <OpenGL/glu.h>
#endif

//FS Windowing class
#include <fssimplewindow.h>

//ASSIMP - Open Asset Import Library
#include <assimp.h>      			// C importer interface
#include <assimp.hpp>      			// C++ importer interface
#include <aiScene.h>       			// Output data structure
#include <aiPostProcess.h> 			// Post processing flags


const struct aiScene* scene = 0;	// the global Assimp scene object
const float pi = 3.1415926535f;
float cameraPos[3];					//cameraLookAt[0]
float cameraLookAt[3];				//camera 'look-at' vector
int cameraMovementRate;

//ofstream myfile; 					//for debugging

void Color4f(const struct aiColor4D *color)
{
	glColor4f(color->r, color->g, color->b, color->a);
}

void color4_to_float4(const struct aiColor4D *c, float f[4])
{
	f[0] = c->r;
	f[1] = c->g;
	f[2] = c->b;
	f[3] = c->a;
}

void set_float4(float f[4], float a, float b, float c, float d)
{
	f[0] = a;
	f[1] = b;
	f[2] = c;
	f[3] = d;
}

bool matrixSetIdentity(float* a)
{
	for(int j=0; j<16; j++)
		a[j]=0.0f;				//init all values to 0
		
	a[0]=a[5]=a[10]=a[15]=1.0f;	//init to identity matrix
	
	return true;
}

bool vector3Add(float* a, float* b, int multiples)
{
	a[0]=a[0]+multiples*b[0];
	a[1]=a[1]+multiples*b[1];
	a[2]=a[2]+multiples*b[2];
	return true;
}

bool vector3Sub(float* a, float* b, int multiples)
{
	a[0]=a[0]-multiples*b[0];
	a[1]=a[1]-multiples*b[1];
	a[2]=a[2]-multiples*b[2];
	return true;
}

bool matrixMul(float* a, float* b)
{
	float temp[16];
	//try
	{
		temp[0] = a[0]*b[0] + a[4]*b[1] + a[8]*b[2] + a[12]*b[3];
		temp[1] = a[1]*b[0] + a[5]*b[1] + a[9]*b[2] + a[13]*b[3];
		temp[2] = a[2]*b[0] + a[6]*b[1] + a[10]*b[2] + a[14]*b[3];
		temp[3] = a[3]*b[0] + a[7]*b[1] + a[11]*b[2] + a[15]*b[3];
		
		temp[4] = a[0]*b[4] + a[4]*b[5] + a[8]* b[6] + a[12]*b[7];
		temp[5] = a[1]*b[4] + a[5]*b[5] + a[9]* b[6] + a[13]*b[7];
		temp[6] = a[2]*b[4] + a[6]*b[5] + a[10]*b[6] + a[14]*b[7];
		temp[7] = a[3]*b[4] + a[7]*b[5] + a[11]*b[6] + a[15]*b[7];
		
		temp[8] =  a[0]*b[8] + a[4]*b[9] + a[8]* b[10] + a[12]*b[11];
		temp[9] =  a[1]*b[8] + a[5]*b[9] + a[9]* b[10] + a[13]*b[11];
		temp[10] = a[2]*b[8] + a[6]*b[9] + a[10]*b[10] + a[14]*b[11];
		temp[11] = a[3]*b[8] + a[7]*b[9] + a[11]*b[10] + a[15]*b[11];
		
		temp[12] = a[0]*b[12] + a[4]*b[13] + a[8]* b[14] + a[12]*b[15];
		temp[13] = a[1]*b[12] + a[5]*b[13] + a[9]* b[14] + a[13]*b[15];
		temp[14] = a[2]*b[12] + a[6]*b[13] + a[10]*b[14] + a[14]*b[15];
		temp[15] = a[3]*b[12] + a[7]*b[13] + a[11]*b[14] + a[15]*b[15];
	}
	//catch(Exception e)
	//{
	//	return false;
	//}
	for(int i=0; i<16; i++)
	{
		a[i]=temp[i];
	}
	return true;
}

void apply_material(const struct aiMaterial *mtl)
{
	float c[4];

	GLenum fill_mode;
	int ret1, ret2;
	struct aiColor4D diffuse;
	struct aiColor4D specular;
	struct aiColor4D ambient;
	struct aiColor4D emission;
	float shininess, strength;
	int two_sided;
	int wireframe;
	int max;

	set_float4(c, 0.8f, 0.8f, 0.8f, 1.0f);
	if(AI_SUCCESS == aiGetMaterialColor(mtl, AI_MATKEY_COLOR_DIFFUSE, &diffuse))
		color4_to_float4(&diffuse, c);
	glMaterialfv(GL_FRONT_AND_BACK, GL_DIFFUSE, c);

	set_float4(c, 0.0f, 0.0f, 0.0f, 1.0f);
	if(AI_SUCCESS == aiGetMaterialColor(mtl, AI_MATKEY_COLOR_SPECULAR, &specular))
		color4_to_float4(&specular, c);
	glMaterialfv(GL_FRONT_AND_BACK, GL_SPECULAR, c);

	set_float4(c, 0.2f, 0.2f, 0.2f, 1.0f);
	if(AI_SUCCESS == aiGetMaterialColor(mtl, AI_MATKEY_COLOR_AMBIENT, &ambient))
		color4_to_float4(&ambient, c);
	glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT, c);

	set_float4(c, 0.0f, 0.0f, 0.0f, 1.0f);
	if(AI_SUCCESS == aiGetMaterialColor(mtl, AI_MATKEY_COLOR_EMISSIVE, &emission))
		color4_to_float4(&emission, c);
	glMaterialfv(GL_FRONT_AND_BACK, GL_EMISSION, c);

	max = 1;
	ret1 = aiGetMaterialFloatArray(mtl, AI_MATKEY_SHININESS, &shininess, (unsigned int*)&max);
	max = 1;
	ret2 = aiGetMaterialFloatArray(mtl, AI_MATKEY_SHININESS_STRENGTH, &strength, (unsigned int*)&max);
	if((ret1 == AI_SUCCESS) && (ret2 == AI_SUCCESS))
		glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, shininess * strength);
	else {
		glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, 0.0f);
		set_float4(c, 0.0f, 0.0f, 0.0f, 0.0f);
		glMaterialfv(GL_FRONT_AND_BACK, GL_SPECULAR, c);
	}

	max = 1;
	if(AI_SUCCESS == aiGetMaterialIntegerArray(mtl, AI_MATKEY_ENABLE_WIREFRAME, &wireframe, (unsigned int*)&max))
		fill_mode = wireframe ? GL_LINE : GL_FILL;
	else
		fill_mode = GL_FILL;
	glPolygonMode(GL_FRONT_AND_BACK, fill_mode);

	max = 1;
	if((AI_SUCCESS == aiGetMaterialIntegerArray(mtl, AI_MATKEY_TWOSIDED, &two_sided, (unsigned int*)&max)) && two_sided)
		glEnable(GL_CULL_FACE);
	else 
		glDisable(GL_CULL_FACE);
}

void recursive_render (const struct aiScene *sc, const struct aiNode* nd)
{
	int i;
	unsigned int n = 0, t;
	struct aiMatrix4x4 m = nd->mTransformation;

	// update transform
	aiTransposeMatrix4(&m);
	glPushMatrix();
	glMultMatrixf((float*)&m);
	
	glTranslatef(0.0f,-15.0f,-70.0f);

	// draw all meshes assigned to this node
	for (; n < nd->mNumMeshes; ++n) {
		const struct aiMesh* mesh = scene->mMeshes[nd->mMeshes[n]];

		apply_material(sc->mMaterials[mesh->mMaterialIndex]);

		if(mesh->mNormals == NULL) {
			glDisable(GL_LIGHTING);
		} else {
			glEnable(GL_LIGHTING);
		}

		if(mesh->mColors[0] != NULL) {
			glEnable(GL_COLOR_MATERIAL);
		} else {
			glDisable(GL_COLOR_MATERIAL);
		}

		for (t = 0; t < mesh->mNumFaces; ++t) {
			const struct aiFace* face = &mesh->mFaces[t];
			GLenum face_mode;

			switch(face->mNumIndices) {
				case 1: face_mode = GL_POINTS; break;
				case 2: face_mode = GL_LINES; break;
				case 3: face_mode = GL_TRIANGLES; break;
				default: face_mode = GL_POLYGON; break;
			}
			
			glBegin(face_mode);
			for(i = 0; i < (int)(face->mNumIndices); i++) {
				int index = face->mIndices[i];
				if(mesh->mColors[0] != NULL)
					Color4f(&mesh->mColors[0][index]);
				if(mesh->mNormals != NULL) 
					glNormal3fv(&mesh->mNormals[index].x);
				glVertex3fv(&mesh->mVertices[index].x);
			}
			glEnd();
		}

	}

	// draw all children
	for (n = 0; n < nd->mNumChildren; ++n) {
		recursive_render(sc, nd->mChildren[n]);
	}

	glPopMatrix();
}

int main(int argc, char *argv[])
{
	/*
	*
	*	Import Scene or Object or Model
	*
	*/
		
	// Create an instance of the Importer class
	Assimp::Importer importer;

	// And have it read the given file with some example postprocessing
	// Usually - if speed is not the most important aspect for you - you'll 
	// propably to request more postprocessing than we do in this example.
	
	scene = importer.ReadFile("C:\\workspace_multiscale\\assimp--2.0.863-sdk\\test\\models-nonbsd\\X\\dwarf.x", 
		aiProcessPreset_TargetRealtime_Quality);
	
	// If the import failed, report it
	if( !scene)
	{
		//DoTheErrorLogging( importer.GetErrorString());
		return false;
	}
  
	/*
	*
	*	Create Window and begin OpenGL calls
	*
	*/
	FsOpenWindow(32,32,800,600,1); // 800x600 pixels, useDoubleBuffer=1

	//OpenGL setup in assimp sample
	glClearColor(0.9f,0.9f,0.9f,1.f);

	glEnable(GL_LIGHTING);
	glEnable(GL_LIGHT0);

	glEnable(GL_DEPTH_TEST);

	glLightModeli(GL_LIGHT_MODEL_TWO_SIDE, GL_TRUE);
	glEnable(GL_NORMALIZE);

	//docs say all polygons are emitted CCW, but tests show that some aren't.
	if(getenv("MODEL_IS_BROKEN"))  
		glFrontFace(GL_CW);

	glColorMaterial(GL_FRONT_AND_BACK, GL_DIFFUSE);
	
	srand((unsigned int)time(NULL));

	//initialize camera
	cameraPos[0]=0;
	cameraPos[1]=0;
	cameraPos[2]=0;
	cameraLookAt[0]=0;
	cameraLookAt[1]=0;
	cameraLookAt[2]=-1.0f;
	cameraMovementRate=5;

	//key input variable
	int key=0;
	
	//mouse state variables
	int mouseX=0;
	int mouseY=0;
	int mouseLButton=0;
	int mouseRButton=0;
	int mouseMButton=0;
	
	int mouseXPrev=0;
	int mouseYPrev=0;
	int mouseLButtonPrev=0;
	int mouseRButtonPrev=0;
	int mouseMButtonPrev=0;
	
	int mDownCoordX=0;
	int mDownCoordY=0;
	
	//Window dimensions
	int wid,hei;
	FsGetWindowSize(wid,hei);

	//viewport aspect ratio calculation and field of view angle declaration
	const double aspectRatio = (float) wid / hei, fieldOfView = 60.0;
	
	//opengl projection setup
	glMatrixMode(GL_PROJECTION);
	glLoadIdentity();
	gluPerspective(fieldOfView, aspectRatio,0.1f, 10000.0f);
	glViewport(0, 0, wid, hei);
	
	//flag to prevent initial mouse coordinates from affecting camera
	bool firstInput=true;

	key=FsInkey();
	while(key!=FSKEY_ESC)
	{
		FsPollDevice();
		
		//save previous mouse status
		mouseXPrev		=mouseX;
		mouseYPrev		=mouseY;
		mouseLButtonPrev=mouseLButton;
		mouseRButtonPrev=mouseRButton;
		mouseMButtonPrev=mouseMButton;
		
		//fetch new mouse status
		FsGetMouseState(mouseLButton,mouseMButton,mouseRButton,mouseX,mouseY);
		
		if(firstInput)
		{
			mouseXPrev		=mouseX;
			mouseYPrev		=mouseY;
			mouseLButtonPrev=mouseLButton;
			mouseRButtonPrev=mouseRButton;
			mouseMButtonPrev=mouseMButton;
			firstInput=false;
		}
		
		//Mouse Left button handling
		// if(((mouseLButton==1)&&(mouseLButtonPrev==0)) || 
			// ((mouseMButton==1)&&(mouseMButtonPrev==0)) ||
			// ((mouseRButton==1)&&(mouseRButtonPrev==0)) )
		// {
			// mDownCoordX=mouseX;
			// mDownCoordY=mouseY;
		// }
		// if(((mouseLButton==0)&&(mouseLButtonPrev==1)) ||
			// ((mouseMButton==0)&&(mouseMButtonPrev==1)) ||
			// ((mouseRButton==0)&&(mouseRButtonPrev==1)) )
		// {
			// mDownCoordX=0;
			// mDownCoordY=0;
		// }

		//mouse interactivity	
		if((mouseX!=mouseXPrev)||(mouseY!=mouseYPrev))
		{
			cameraLookAt[0]=cameraLookAt[0]+sin((mouseX-mouseXPrev)*pi/180.0f);
			cameraLookAt[1]=cameraLookAt[1]-sin((mouseY-mouseYPrev)*pi/180.0f);
		}
		
		//keyboard interactivity
		switch(key)
		{
			case FSKEY_W:
				vector3Add(&cameraPos[0],&cameraLookAt[0],cameraMovementRate);
				break;
			case FSKEY_S:
				vector3Sub(&cameraPos[0],&cameraLookAt[0],cameraMovementRate);
				break;
			case FSKEY_A:
				cameraPos[0]=cameraPos[0]+cameraMovementRate*cameraLookAt[2];
				cameraPos[2]=cameraPos[2]-cameraMovementRate*cameraLookAt[0];
				break;
			case FSKEY_D:
				cameraPos[0]=cameraPos[0]-cameraMovementRate*cameraLookAt[2];
				cameraPos[2]=cameraPos[2]+cameraMovementRate*cameraLookAt[0];
				break;
			default:
				break;
		}
		
		//opengl clear buffers
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glShadeModel(GL_SMOOTH);
		
		//opengl modelview setup
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		gluLookAt(cameraPos[0],
				cameraPos[1],
				cameraPos[2],
				cameraPos[0]+cameraLookAt[0],
				cameraPos[1]+cameraLookAt[1],
				cameraPos[2]+cameraLookAt[2],
				0.f,1.f,0.f);
		
		//rendering call
		recursive_render(scene, scene->mRootNode);
		
		//buffer swap
		FsSwapBuffers();
		
		//delay
		FsSleep(10);
		
		//get next key input
		key=FsInkey();
	}
	
	return 0;
}


