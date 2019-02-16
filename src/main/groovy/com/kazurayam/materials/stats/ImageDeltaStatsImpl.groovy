package com.kazurayam.materials.stats

import com.kazurayam.materials.ImageDeltaStats
import com.kazurayam.materials.TSuiteName

/**
 * 
 * @author kazurayam
 */
class ImageDeltaStatsImpl extends ImageDeltaStats implements Comparable<ImageDeltaStatsImpl> {
    
    public static final double SUGGESTED_CRITERIA_PERCENTAGE = 5.0
    
    private double defaultCriteriaPercentage
    
    private List<ImageDeltaStatsEntry> imageDeltaStatsEntries
    
    /**
     *
     */
    static class Builder {
        private double defaultCriteriaPercentage
        private List<ImageDeltaStatsEntry> imageDeltaStatsEntries
        Builder() {
            defaultCriteriaPercentage = SUGGESTED_CRITERIA_PERCENTAGE
            imageDeltaStatsEntries = new ArrayList<ImageDeltaStatsEntry>()
        }
        Builder defaultCriteriaPercentage(double value) {
            if (value < 0.0 || value > 100.0) {
                throw new IllegalArgumentException(
                    "defaultCrieteriaPercentage(${value}) must be positive; less than or equal to 100.0")
            }
            defaultCriteriaPercentage = value
            return this
        }
        Builder addImageDeltaStatsEntry(ImageDeltaStatsEntry entry) {
            imageDeltaStatsEntries.add(entry)
            return this
        }
        ImageDeltaStats build() {
            return new ImageDeltaStatsImpl(this)
        }
    }
    
    private ImageDeltaStatsImpl(Builder builder) {
        this.defaultCriteriaPercentage = builder.defaultCriteriaPercentage
        this.imageDeltaStatsEntries = builder.imageDeltaStatsEntries
    }

    @Override
    double getDefaultCriteriaPercentage() {
        return this.defaultCriteriaPercentage
    }
    
    @Override
    List<ImageDeltaStatsEntry> getImageDeltaStatsEntries() {
        return imageDeltaStatsEntries
    }
    
    @Override
    ImageDeltaStatsEntry getImageDeltaStatsEntry(TSuiteName tSuiteName) {
        for (ImageDeltaStatsEntry entry: imageDeltaStatsEntries) {
            if (entry.getTSuiteName().equals(tSuiteName)) {
                return imageDeltaStatsEntries.get(tSuiteName)
            }
        }
        return ImageDeltaStatsEntry.NULL
    }
    
    void addStatsEntry(ImageDeltaStatsEntry entry ) {
        statsEntries.add(entry)
    }

    @Override
    String toString() {
        return this.toJson()
    }
    
    String toJson() {
        StringBuilder sb = new StringBuilder()
        sb.append("{")
        sb.append("\"defaultCriteriaPercentage\":")
        sb.append("${this.getDefaultCriteriaPercentage()}")
        sb.append("}")
        return sb.toString()
    }
    
    @Override
    int compareTo(ImageDeltaStatsImpl other) {
        double d = this.getDefaultCriteriaPercentage()
        return this.getDefaultCriteriaPercentage() - d
    }
}
