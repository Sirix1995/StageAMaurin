package de.grogra.imp3d.anaglyph;

import java.awt.Color;
/**
 * <b>Lizenz:</b><br>
 * Diese Klasse wird kostenfrei verbreitet und darf geaendert werden.<br><br>
 * 
 * <b>Copyright:</b><br>
 * by Aleksej Tokarev 2011 <br>
 *
 * <br><b>Date:</b><br>
 * 29.08.2011
 * 
 * @author Aleksej Tokarev<br>
 * <a href="http://atoks.bplaced.net">{@link http://atoks.bplaced.net}</a>
 * @version 1.0
 * <br><br>
 * Klasse RGBAConverter repr�sentiert kleine Umrechner von Farbeeingaben.<br>
 * In manchen Fellen um aus einem RGB Wert ein Dezimale zu bekommen nutzt man Color Klasse<br>
 * aber um aus Dezimale Wert ein RGB zu bekommen, muss jedes mal eine neue Instanz von Color gebildet werden<br>
 * um Dezimale Wert an Color zu �bergeben.<br>
 * Und genau das ist sehr speicher unfreundlich
 * Genau f�r solche Felle wird diese Klasse geschrieben.<br><br>
 * <b>RGBAConverter</b> erlaubt Ihnen ohne Bildung von weitere Instanz Farbewerte aus verschiedenen Typen zu konvertieren<br>
 * z.B.: M�gliche Konvertierungen <b>Dezimal <=> RGB <=> HEX</b><br>
 * Es reicht nur ein einziges Object von <b>RGBAConverter</b> und alle n�tige Eingaben k�nnen an diese Object
 * �bergeben werden.
 * 
 */
public class RGBAConverter {
	private int rgbValue = 0;
	
	/**
	 * Leeres Konstruktor<br>
	 * RGB wert in diesem Fall ist gleich R=0; G=0; B=0
	 */
	public RGBAConverter(){}
	/**
	 * Konstruktor mit �bergabe von Color<br>
	 * Color wird nicht als Object gespeichert, sondern als dezimale Wert.
	 * @param c �bergabecolor
	 * @see #setColor
	 */
	public RGBAConverter(Color c){
		this.setColor(c);
	}
	/**
	 * Konstruktor mit �bergabe von Dezimalenfarbwert
	 * @param rgba Dezimalefarbwert
	 * @see #setRGBA
	 */
	public RGBAConverter(int rgba){
		this.setRGBA(rgba);
	}
	/**
	 * Konstruktor mit �bergabe von RGB Werten
	 * @param r Intensit�t von rote-Farbe zwischen 0 und 255, einschlie�lich 
	 * @param g Intensit�t von gr�ne-Farbe zwischen 0 und 255, einschlie�lich 
	 * @param b Intensit�t von blaue-Farbe zwischen 0 und 255, einschlie�lich 
	 * @see #setRGB
	 */
	public RGBAConverter(int r, int g, int b){
		this.setRGB(r,g,b);
	}
	/**
	 * Konstruktor mit �bergabe von RGB Werten
	 * @param r Intensit�t von rote-Farbe zwischen 0 und 255, einschlie�lich 
	 * @param g Intensit�t von gr�ne-Farbe zwischen 0 und 255, einschlie�lich 
	 * @param b Intensit�t von blaue-Farbe zwischen 0 und 255, einschlie�lich 
	 * @param a Intensit�t von Alpha-Wert zwischen 0 und 255, einschlie�lich 
	 * @see #setRGBA
	 */
	public RGBAConverter(int r, int g, int b, int a){
		this.setRGBA(r,g,b,a);
	}
	/**
	 * Konstruktor mit �bergabe von HEX Werten
	 * @param hex Hexagonale Wert zwischen #000000 und #FFFFFF, einschlie�lich
	 * @see #setHEX
	 */
	public RGBAConverter(String hex){
		this.setHEX(hex);
	}
	/**
	 * Methode setzt Color fest<br>
	 * Color wird nicht als Object gespeichert, sondern als dezimale Wert.<br>
	 * Dank diese Trick, wird keine neue Instanz von Color ben�tigt um Farbe zu �ndern.
	 * @param c �bergabecolor
	 */
	public void setColor(Color c){
		this.rgbValue = c.getRGB();
	}
	/**
	 * Methode setzt Dezimaler Farbwert von Farbe fest<br>
	 * Da Dezimale Wert kann auch Alphawert enthalten wird hier auch Alphawert �bergeben
	 * @param rgba Dezimaler Farbwert
	 */
	public void setRGBA(int rgba){
		this.rgbValue = rgba;
	}
	/**
	 * Methode setzt Dezimaler Farbwert von Farbe fest<br>
	 * Diese Methode ist eine Kopie von setRGBA.
	 * @param rgb Dezimaler Farbwert
	 * @see #setRGBA(int)
	 */
	public void setRGB(int rgb){
		this.setRGBA(rgb);
	}
	/**
	 * Methode setzt RGB Wert fest<br>
	 * Da diese Methode intern setRGBA(int, int, int, int) nutzt, wird Alphawert auf 255 gesetzt
	 * @param r Intensit�t von rote-Farbe zwischen 0 und 255, einschlie�lich 
	 * @param g Intensit�t von gr�ne-Farbe zwischen 0 und 255, einschlie�lich 
	 * @param b Intensit�t von blaue-Farbe zwischen 0 und 255, einschlie�lich 
	 * @see #setRGBA(int, int, int, int)
	 */
	public void setRGB(int r, int g, int b){
		this.setRGBA(r,g,b,255);
	}
	/**
	 * Methode setzt RGBA Werten fest
	 * @param r Intensit�t von rote-Farbe zwischen 0 und 255, einschlie�lich 
	 * @param g Intensit�t von gr�ne-Farbe zwischen 0 und 255, einschlie�lich 
	 * @param b Intensit�t von blaue-Farbe zwischen 0 und 255, einschlie�lich 
	 * @param a Intensit�t von Alpha-Wert zwischen 0 und 255, einschlie�lich 
	 * @see #setRed(int)
	 * @see #setGreen(int)
	 * @see #setBlue(int)
	 * @see #setAlpha(int)
	 */
	public void setRGBA(int r, int g, int b, int a){
		this.setRed(r);
		this.setGreen(g);
		this.setBlue(b);
		this.setAlpha(a);
	}
	/**
	 * Methode setzt Alphawert fest
	 * @param a Intensit�t von Alpha-Wert zwischen 0 und 255, einschlie�lich
	 */
	public void setAlpha(int a){
		validateValue(a);
		
		this.rgbValue = ((a & 0xFF) << 24) |
        ((this.getRed() & 0xFF) << 16) |
        ((this.getGreen() & 0xFF) << 8)  |
        ((this.getBlue() & 0xFF) << 0);
	}
	/**
	 * Methode setzt Intensit�t von rote-Farbe fest
	 * @param r Intensit�t von rote-Farbe zwischen 0 und 255, einschlie�lich
	 */
	public void setRed(int r){
		validateValue(r);
		
		this.rgbValue = ((this.getAlpha() & 0xFF) << 24) |
        ((r & 0xFF) << 16) |
        ((this.getGreen() & 0xFF) << 8)  |
        ((this.getBlue() & 0xFF) << 0);
	}
	/**
	 * Methode setzt Intensit�t von gr�ne-Farbe fest
	 * @param g Intensit�t von gr�ne-Farbe zwischen 0 und 255, einschlie�lich
	 */
	public void setGreen(int g){
		validateValue(g);
		
		this.rgbValue = ((this.getAlpha() & 0xFF) << 24) |
        ((this.getRed() & 0xFF) << 16) |
        ((g & 0xFF) << 8)  |
        ((this.getBlue() & 0xFF) << 0);
	}
	/**
	 * Methode setzt Intensit�t von blaue-Farbe fest
	 * @param b Intensit�t von blaue-Farbe zwischen 0 und 255, einschlie�lich 
	 */
	public void setBlue(int b){
		validateValue(b);
		
		this.rgbValue = ((this.getAlpha() & 0xFF) << 24) |
        ((this.getRed() & 0xFF) << 16) |
        ((this.getGreen() & 0xFF) << 8)  |
        ((b & 0xFF) << 0);
	}
	/**
	 * Methode setzt Hexagonale Farbenwert 
	 * @param hex Hexagonale Wert zwischen #000000 und #FFFFFF, einschlie�lich
	 * @see #setRed(int)
	 * @see #setGreen(int)
	 * @see #setBlue(int)
	 * @see #setAlpha(int)
	 */
	public void setHEX(String hex){
		hex = hex.trim();
		// Testen ob String mit hex-Color valid ist 
		validateValue(hex);

		// Raute wenn n�tig abschneiden
		if(hex.indexOf("#")>-1){
			hex = hex.substring(1, hex.length());
		}
		
		// F�r volle Format FFFFFF
		if(hex.length() == 6){
			this.setAlpha(255);
			this.setRed(this.hexToInt(hex.substring(0, 2)));
			this.setGreen(this.hexToInt(hex.substring(2, 4)));
			this.setBlue(this.hexToInt(hex.substring(4, 6)));
		}else if(hex.length() == 3){ // F�r kurze Format FFF
			this.setAlpha(255);
			this.setRed(this.hexToInt(hex.substring(0, 1)+hex.substring(0, 1)));
			this.setGreen(this.hexToInt(hex.substring(1, 2)+hex.substring(1, 2)));
			this.setBlue(this.hexToInt(hex.substring(2, 3)+hex.substring(2, 3)));
		}else{ // Kein richtiger Format
			throw new IllegalArgumentException("Your value is illegal : "+hex);
		}
	}
	
	public Color getColor(){
		return new Color(getRGBA());
	}
	
	public int getRGBA(){
		return this.rgbValue;
	}
	
	public int getRGB(){
		return this.getRGBA();
	}
	
	public int getAlpha(){
		return (getRGBA() >> 24) & 0xff;
	}
	
	public int getRed(){
		return (getRGBA() >> 16) & 0xFF;
	}
	
	public int getGreen(){
		return (getRGBA() >> 8) & 0xFF;
	}
	
	public int getBlue(){
		return (getRGBA() >> 0) & 0xFF;
	}
	
	public String getHEX(){
		return intToHex(this.getRed())+intToHex(this.getGreen())+intToHex(this.getBlue());
	}
	
	@Override
	public String toString(){
		return "at.imagelibrary.RGBAConverter[r="+this.getRed()+",g="+this.getGreen()+",b="+this.getBlue()+",a="+this.getAlpha()+"] <=> [#"+this.getHEX()+"]";
	}
	
	public boolean equals(RGBAConverter o){
		return this.getRGBA() == o.getRGBA();
	}
	
	private int hexToInt(String hexString){
		return Integer.parseInt(hexString,16);
	}

	private String intToHex(int i){
		String s = "0123456789ABCDEF";
		int id1 = (i-i%16)/16;
		int id2 = i%16;
		return s.substring(id1, id1+1)+s.substring(id2, id2+1);
	}
	
	private void validateValue(int v){
		if( v < 0 || v > 255){
			throw new IllegalArgumentException("Your value is illegal : "+v+". Ligall value mus be between 0 and 255");
		}
	}
	
	private void validateValue(String hex){
		if(!hex.matches("^#?(([a-fA-F0-9]){3}){1,2}$")){
			throw new IllegalArgumentException("Your value is illegal : "+hex);
		}
	}
	
}
