package edu.unc.mapseq.module.impl;

public class PileupBean {

    private String chromosome;

    private Integer coordinate;

    private String consensusBases;

    private Integer consensusQuality;

    private Integer snpQuality;

    private Integer maxMappingQuality;

    private String referenceBase;

    private Integer readBases;

    private String readQualities;

    private String alignmentMappingQualities;

    public PileupBean() {
        super();
    }

    public PileupBean(Integer coordinate, String referenceBase, Integer readBases, String readQualities,
            String alignmentMappingQualities) {
        super();
        this.coordinate = coordinate;
        this.referenceBase = referenceBase;
        this.readBases = readBases;
        this.readQualities = readQualities;
        this.alignmentMappingQualities = alignmentMappingQualities;
    }

    public PileupBean(Integer coordinate, String consensusBases, Integer consensusQuality, Integer snpQuality,
            Integer maxMappingQuality, String referenceBase, Integer readBases, String readQualities,
            String alignmentMappingQualities) {
        super();
        this.coordinate = coordinate;
        this.consensusBases = consensusBases;
        this.consensusQuality = consensusQuality;
        this.snpQuality = snpQuality;
        this.maxMappingQuality = maxMappingQuality;
        this.referenceBase = referenceBase;
        this.readBases = readBases;
        this.readQualities = readQualities;
        this.alignmentMappingQualities = alignmentMappingQualities;
    }

    public String getChromosome() {
        return chromosome;
    }

    public void setChromosome(String chromosome) {
        this.chromosome = chromosome;
    }

    public Integer getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Integer coordinate) {
        this.coordinate = coordinate;
    }

    public String getConsensusBases() {
        return consensusBases;
    }

    public void setConsensusBases(String consensusBases) {
        this.consensusBases = consensusBases;
    }

    public Integer getConsensusQuality() {
        return consensusQuality;
    }

    public void setConsensusQuality(Integer consensusQuality) {
        this.consensusQuality = consensusQuality;
    }

    public Integer getSnpQuality() {
        return snpQuality;
    }

    public void setSnpQuality(Integer snpQuality) {
        this.snpQuality = snpQuality;
    }

    public Integer getMaxMappingQuality() {
        return maxMappingQuality;
    }

    public void setMaxMappingQuality(Integer maxMappingQuality) {
        this.maxMappingQuality = maxMappingQuality;
    }

    public String getReferenceBase() {
        return referenceBase;
    }

    public void setReferenceBase(String referenceBase) {
        this.referenceBase = referenceBase;
    }

    public Integer getReadBases() {
        return readBases;
    }

    public void setReadBases(Integer readBases) {
        this.readBases = readBases;
    }

    public String getReadQualities() {
        return readQualities;
    }

    public void setReadQualities(String readQualities) {
        this.readQualities = readQualities;
    }

    public String getAlignmentMappingQualities() {
        return alignmentMappingQualities;
    }

    public void setAlignmentMappingQualities(String alignmentMappingQualities) {
        this.alignmentMappingQualities = alignmentMappingQualities;
    }

}
