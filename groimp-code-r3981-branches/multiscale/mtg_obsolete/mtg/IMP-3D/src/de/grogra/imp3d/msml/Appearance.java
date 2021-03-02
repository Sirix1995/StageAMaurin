
package de.grogra.imp3d.msml;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.grogra.graph.GraphState;
import de.grogra.graph.impl.Node;
import de.grogra.imp.io.ImageReader;
import de.grogra.imp.objects.FixedImageAdapter;
import de.grogra.imp3d.objects.Attributes;
import de.grogra.imp3d.objects.ShadedNull;
import de.grogra.imp3d.shading.AffineUVTransformation;
import de.grogra.imp3d.shading.ImageMap;
import de.grogra.imp3d.shading.Material;
import de.grogra.imp3d.shading.Phong;
import de.grogra.imp3d.shading.RGBAShader;
import de.grogra.imp3d.shading.Shader;
import de.grogra.imp3d.shading.SideSwitchShader;
import de.grogra.math.ChannelMap;
import de.grogra.math.Graytone;
import de.grogra.math.RGBColor;
import de.grogra.pf.registry.Registry;
import de.grogra.pf.ui.Workbench;

public class Appearance extends X3DMSMLDatatype
{

	HashMap textures = new HashMap ();
	HashMap appearances = new HashMap ();

	public void export (Object writer, Document doc, Element data, Node n){
		GraphState gs = GraphState.current(n.getGraph());
		Object shader = gs.getObjectDefault(n, true, Attributes.SHADER, null);
		if (shader!=null){
			exportShader(writer,doc,data,(Shader)shader,null);
		}
	}
	public static void exportShader (Object writer, Document doc, Element data, Shader s, String defname){
		if (s!=null){
			Element appearance=(Element) doc.createElementNS(GROIMPDATATYPE_NAMESPACE,"g:Appearance");
			if (((MSMLWriter)writer).getLibraryMaterials().containsKey(s)){
				//Appearance is contained in library
				appearance.setAttribute("USE",(String)((MSMLWriter)writer).getLibraryMaterials().get(s));
			}else{
				//Appearance is not contained in library
				Element x3dappearance=(Element) doc.createElementNS(X3D_NAMESPACE,"x3d:Appearance");
				appearance.appendChild(x3dappearance);
				if ((defname!=null)&&(!(defname.equals("")))){
					appearance.setAttribute("DEF",defname);
				}
				if (s instanceof RGBAShader){
					Element x3dmaterial=(Element) doc.createElementNS(X3D_NAMESPACE,"x3d:Material");
					RGBAShader color=((RGBAShader)s);
					x3dmaterial.setAttribute("diffuseColor",getStringFromVector3f(
						new Vector3f(color.x,color.y,color.z)));
					x3dmaterial.setAttribute("transparency",String.valueOf(1-color.w));
					x3dappearance.appendChild(x3dmaterial);
				}
				else if (s instanceof SideSwitchShader){
					exportMaterial(doc, x3dappearance,(Material)((SideSwitchShader)s).getFrontShader ());
				}
				else if(s instanceof Material){
					exportMaterial(doc, x3dappearance,(Material)s);
				}
				
			}
			
			data.appendChild(appearance);
		}
	}
	
	private static void exportMaterial(Document doc, Element x3dappearance, Material material){
		Element x3dmaterial=(Element) doc.createElementNS(X3D_NAMESPACE,"x3d:Material");
		if (material instanceof Phong){
			Phong lambert=(Phong)material;
			ChannelMap cm=lambert.getDiffuse();
			Vector3f v3f=null;
			if ((cm instanceof RGBColor)||(cm instanceof Graytone)){
				v3f=getVector3fFromChannelMap(cm);
				if (v3f!=null){
					x3dmaterial.setAttribute("diffuseColor",getStringFromVector3f(v3f));
				}
			}
			else if (cm instanceof ImageMap){
				URL url=((ImageMap)cm).getImageAdapter().getImageSource();
				if (url!=null){
					Element x3dimagetexture=(Element) doc.createElementNS(X3D_NAMESPACE,"x3d:ImageTexture");
					x3dimagetexture.setAttribute("url",url.toString());
					x3dappearance.appendChild(x3dimagetexture);
				}
				else{
					BufferedImage image=((ImageMap)cm).getImageAdapter().getBufferedImage();
					if (image!=null){
						Element x3dpixeltexture=(Element) doc.createElementNS(X3D_NAMESPACE,"x3d:PixelTexture");
						x3dpixeltexture.setAttribute("image",getStringFromBufferedImage(image));
						x3dappearance.appendChild(x3dpixeltexture);
					}
				}
				exportTextureTransform(doc,(ImageMap)cm,x3dappearance);
			}
			v3f=getVector3fFromChannelMap(lambert.getEmissive());
			if (v3f!=null){
				x3dmaterial.setAttribute("emissiveColor",getStringFromVector3f(v3f));
			}
			v3f=getVector3fFromChannelMap(lambert.getAmbient());
			if (v3f!=null){
				x3dmaterial.setAttribute("ambientIntensity",String.valueOf(new Graytone(v3f).getValue()));
			}
			v3f=getVector3fFromChannelMap(lambert.getTransparency());
			if (v3f!=null){
				x3dmaterial.setAttribute("transparency",String.valueOf(new Graytone(v3f).getValue()));
			}
			x3dappearance.appendChild(x3dmaterial);
		}
		if (material instanceof Phong){
			Phong phong=(Phong)material;
			Vector3f v3f=getVector3fFromChannelMap(phong.getSpecular());
			if (v3f!=null){
				x3dmaterial.setAttribute("specularColor",getStringFromVector3f(v3f));
			}
			x3dmaterial.setAttribute("shininess",String.valueOf ((phong.getShininess () instanceof Graytone)
				? Phong.convertShininess (((Graytone) phong.getShininess ()).getValue ()) : 50));
		}
	}
	
	private static void exportTextureTransform (Document doc,ImageMap imgMap, Element x3dappearance)
	{
		ChannelMap transformation = imgMap.getInput();
		if (transformation instanceof AffineUVTransformation){
			AffineUVTransformation aUVt=(AffineUVTransformation)transformation;
			Element x3dtexturetransform=(Element) doc.createElementNS(X3D_NAMESPACE,"x3d:TextureTransform");
			boolean hasChanged=false;
			//translation
			Vector2f translation=new Vector2f(aUVt.getOffsetU(),aUVt.getOffsetV());
			if (translation.length()!=0f){
				x3dtexturetransform.setAttribute("translation",getStringFromVector2f(translation));
				hasChanged=true;
			}
			//rotation
			float rotation=aUVt.getAngle();
			if ((rotation % (2*Math.PI))!=0f){
				x3dtexturetransform.setAttribute("rotation",String.valueOf(rotation));
				hasChanged=true;
			}
			//scale
			Vector2f scale=new Vector2f(aUVt.getScaleU(),aUVt.getScaleV());
			if (scale.length()!=1f){
				x3dtexturetransform.setAttribute("scale",getStringFromVector2f(scale));
				hasChanged=true;
			}
			if (hasChanged){
				x3dappearance.appendChild(x3dtexturetransform);
			}
		}
	}
	
	public Node export (Registry registry, org.w3c.dom.Node node, Node n, URL baseURL)
			throws IOException
	{
		if (node.getNamespaceURI ().equals (GROIMPDATATYPE_NAMESPACE))
		{
			Material material=null;
			String usename = getAttributeContent (node, "USE");
			if (usename.equals(""))
			{
				material=createMaterial(registry, node, baseURL);
				String defname = getAttributeContent (node, "DEF");
				if (!(defname.equals(""))){
					registry.addSharedObject
					("/project/objects/3d/materials", material, defname, true);
					appearances.put (defname, material);
				}
			}
			else
			{
				material=(Material) appearances.get (usename);
			}
			if ((n instanceof ShadedNull)&&(material!=null)){
				((ShadedNull) n).setMaterial (material);
			}
		}
		return n;
	}

	private Material createMaterial(Registry registry, org.w3c.dom.Node node, URL baseURL)throws IOException{
		Phong material=null;
		for (org.w3c.dom.Node sub_g_app = node.getFirstChild (); sub_g_app != null; sub_g_app = sub_g_app
			.getNextSibling ())
		{
			if ((sub_g_app.getNodeType () == org.w3c.dom.Node.ELEMENT_NODE)
				&& (sub_g_app.getNamespaceURI ()
					.equals (X3D_NAMESPACE)))
			{
				if (sub_g_app.getLocalName ().equals ("Appearance"))
				{
					material = new Phong ();
					//	X3D-Standardwerte
					material.setDiffuse (new RGBColor (0.8f, 0.8f, 0.8f));
					material.setEmissive (new RGBColor (0f, 0f, 0f));
					material.setSpecular (new RGBColor (0f, 0f, 0f));
					material.setShininess (new Graytone (0.2f));
					material.setTransparency (new Graytone (0));
					material.setAmbient (new Graytone (0.2f));
					for (org.w3c.dom.Node sub_x3d_app = sub_g_app
						.getFirstChild (); sub_x3d_app != null; sub_x3d_app = sub_x3d_app
						.getNextSibling ())
					{
						if ((sub_x3d_app.getNodeType () == org.w3c.dom.Node.ELEMENT_NODE)
							&& (sub_x3d_app.getNamespaceURI ()
								.equals (X3D_NAMESPACE)))
						{
							if (sub_x3d_app.getLocalName ().equals (
								"Material"))
							{
								String s = getAttributeContent (
									sub_x3d_app, "diffuseColor");
								if (s != "")
								{
									material
										.setDiffuse (new RGBColor (
											getVector3fFromString (s)));
								}
								s = getAttributeContent (
									sub_x3d_app, "emissiveColor");
								if (s != "")
								{
									material
										.setEmissive (new RGBColor (
											getVector3fFromString (s)));
								}
								s = getAttributeContent (
									sub_x3d_app, "specularColor");
								if (s != "")
								{
									material
										.setSpecular (new RGBColor (
											getVector3fFromString (s)));
								}
								s = getAttributeContent (
									sub_x3d_app, "shininess");
								if (s != "")
								{
									material.setShininess (new Graytone(Float
										.valueOf (s).floatValue ()));
								}
								s = getAttributeContent (
									sub_x3d_app, "transparency");
								if (s != "")
								{
									material
										.setTransparency (new Graytone (
											Float.valueOf (s)
												.floatValue ()));
								}
								s = getAttributeContent (
									sub_x3d_app, "ambientIntensity");
								if (s != "")
								{
									material
										.setAmbient (new Graytone (
											Float.valueOf (s)
												.floatValue ()));
								}

							}
							else if (sub_x3d_app.getLocalName ()
								.equals ("ImageTexture"))
							{
								FixedImageAdapter img = null;
								String urlname = getAttributeContent (
									sub_x3d_app, "url");
								if (urlname != "")
								{
									URL url = getURLfromString (
										baseURL, urlname);
									img = (FixedImageAdapter) textures
										.get (url);
									if (img == null)
									{
										Workbench w = Workbench.current ();
										img = (FixedImageAdapter) ImageReader
											.getFactory (registry)
											.addFromURL (registry, url,
												null, w);
										textures.put (url, img);
									}
									ImageMap imgMap = new ImageMap ();
									imgMap.setImageAdapter (img);
									imgMap = setTextureTransform (
										imgMap, sub_x3d_app
											.getParentNode ());
									material.setDiffuse (imgMap);
								}
							}
							else if (sub_x3d_app.getLocalName ()
								.equals ("PixelTexture"))
							{
								FixedImageAdapter img = null;
								String sfimage = getAttributeContent (
									sub_x3d_app, "image");
								if (sfimage != "")
								{
									//Problem: hashCode for Pixelimage!!!
									int hashkey=sfimage.hashCode();
									BufferedImage bi = getBufferedImageFromSFImage (sfimage);
									img = (FixedImageAdapter) textures.get (new Integer(hashkey));
									if (img == null)
									{
										img = new FixedImageAdapter (bi);
										registry.addSharedObject
										("/project/objects/images", img, "Pixelimage", true);
										textures.put (new Integer(hashkey), img);
									}
									ImageMap imgMap = new ImageMap ();
									imgMap.setImageAdapter (img);
									imgMap = setTextureTransform (
										imgMap, sub_x3d_app
											.getParentNode ());
									material.setDiffuse (imgMap);
								}
							}
						}
					}
				}
			}
		}
		return material;
	}
	
	private ImageMap setTextureTransform (ImageMap imgMap, org.w3c.dom.Node node)
	{
		AffineUVTransformation transformation = new AffineUVTransformation ();
		for (org.w3c.dom.Node n = node.getFirstChild (); n != null; n = n
			.getNextSibling ())
		{
			if ((n.getNodeType () == org.w3c.dom.Node.ELEMENT_NODE)
				&& (n.getNamespaceURI ().equals (X3D_NAMESPACE)))
			{
				if (n.getLocalName ().equals ("TextureTransform"))
				{
					String attributeContent = getAttributeContent (n,
						"translation");
					if (attributeContent != "")
					{
						Vector2f translation = getVector2fFromString (attributeContent);
						transformation.setOffsetU (translation.x);
						transformation.setOffsetV (translation.y);
					}
					attributeContent = getAttributeContent (n, "rotation");
					if (attributeContent != "")
					{
						transformation.setAngle (Float.valueOf (
							attributeContent).floatValue ());
					}
					attributeContent = getAttributeContent (n, "scale");
					if (attributeContent != "")
					{
						Vector2f scale = getVector2fFromString (attributeContent);
						transformation.setScaleU (scale.x);
						transformation.setScaleV (scale.y);
					}

					imgMap.setInput (transformation);
				}
			}
		}
		return imgMap;
	}
}
