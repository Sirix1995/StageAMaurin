package de.grogra.grogra;

import javax.vecmath.Matrix3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

public  final class ShootInfo extends Point3f {
	private static final long serialVersionUID = -7309802469983926478L;
	
	public DTGShoot shoot;

	float laenge; // Sprosslaenge (gewoehnl. in mm)
	float edur; // Durchmesser
	float nad; // Nadelparameter
	int izahl; // Internodienzahl
	int farbe; // Zeichenfarbe
	int or; // Zweigordnung
	int gen; // Generations-Nr.; zusaetzl. Verwendung
	float q; // rel. Position auf Mutterspross
	int akurztr;

	String name; // Name des Shoot
	String mName;

	final Matrix3f xf;
	final Vector3f tip;

	Vector3f panf; // Ortskoordinaten des Sprossanfangs
	Vector3f pend; // Ortskoordinaten des Sprossendes

	/* Orientierung des Sprosses im Raume */
	Vector3f sh;
	Vector3f sl;
	Vector3f su;
	
	public ShootInfo() {
		super();
		xf = new Matrix3f();
		tip = new Vector3f();
		panf = new Vector3f();
		pend = new Vector3f();
		sh = new Vector3f();
		sl = new Vector3f();
		su = new Vector3f();
	}
	
	private ShootInfo(ShootInfo si) {
		super(si);
		this.shoot = new DTGShoot();
		this.shoot.color = si.shoot.color;
		this.shoot.diameter = si.shoot.diameter;
		this.shoot.length = si.shoot.length;
		this.laenge = si.laenge;
		this.edur = si.edur;
		this.nad = si.nad;
		this.izahl = si.izahl;
		this.farbe = si.farbe;
		this.or = si.or;
		this.gen = si.gen;
		this.q = si.q;
		this.akurztr = si.akurztr;
		this.name = si.name;
		this.mName = si.mName;
		
		this.xf = (Matrix3f) si.xf.clone();
		this.tip = (Vector3f) si.tip.clone();
		this.panf = (Vector3f) si.panf.clone();
		this.pend = (Vector3f) si.pend.clone();
		this.sh = (Vector3f) si.sh.clone();
		this.sl = (Vector3f) si.sl.clone();
		this.su = (Vector3f) si.su.clone();
	}
	
	public Object clone() {
		return new ShootInfo(this);
	}
}
