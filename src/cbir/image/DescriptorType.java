/*
 * Copyright (C) 2013 Justus Piater,
 * Intelligent and Interactive Systems Group,
 * University of Innsbruck, Austria.
 */
package cbir.image;

import java.io.Serializable;

/**
 * An enum which specifies the single types of descriptors used.
 * 
 * @author Matej Stanic
 * 
 */
public enum DescriptorType implements Serializable {
	CEDD, COLOR_HISTO, MPEG_EHD, MERGED, NORMALIZED, OTHER
}
