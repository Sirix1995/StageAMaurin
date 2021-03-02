package de.grogra.mtg;

import java.io.Serializable;

public class MTGNodeDataFeature implements Serializable
{
	private String featureName;
	private int featureNameIndex;
	private int featureTypeIndex;
	
	public MTGNodeDataFeature(String featureName, int featureNameIndex, int featureTypeIndex)
	{
		this.setFeatureName(featureName);
		this.setFeatureNameIndex(featureNameIndex);
		this.setFeatureTypeIndex(featureTypeIndex);
	}

	public String getFeatureName() {
		return featureName;
	}

	public void setFeatureName(String featureName) {
		this.featureName = featureName;
	}

	public int getFeatureNameIndex() {
		return featureNameIndex;
	}

	public void setFeatureNameIndex(int featureNameIndex) {
		this.featureNameIndex = featureNameIndex;
	}

	public int getFeatureTypeIndex() {
		return featureTypeIndex;
	}

	public void setFeatureTypeIndex(int featureTypeIndex) {
		this.featureTypeIndex = featureTypeIndex;
	}
	

}
