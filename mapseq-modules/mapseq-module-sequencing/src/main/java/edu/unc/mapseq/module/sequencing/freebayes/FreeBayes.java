package edu.unc.mapseq.module.sequencing.freebayes;

import java.io.File;

import javax.validation.constraints.NotNull;

import edu.unc.mapseq.dao.model.MimeType;
import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.annotations.OutputArgument;
import edu.unc.mapseq.module.annotations.OutputValidations;
import edu.unc.mapseq.module.constraints.FileIsNotEmpty;
import edu.unc.mapseq.module.constraints.FileIsReadable;

@Application(name = "FreeBayes", executable = "$%s_FREEBAYES_HOME/bin/freebayes")
public class FreeBayes extends Module {

    @FileIsReadable(message = "BAM file is not readable", groups = InputValidations.class)
    @InputArgument(flag = "--bam", description = "Add FILE to the set of BAM files to be analyzed.")
    private File bam;

    @InputArgument(flag = "--bam-list", description = "A file containing a list of BAM files to be analyzed.")
    private File bamList;

    @NotNull(message = "fastaReference is required", groups = InputValidations.class)
    @FileIsReadable(message = "fastaReference is not readable", groups = InputValidations.class)
    @InputArgument(flag = "--fasta-reference", description = "Use FILE as the reference sequence for analysis.")
    private File fastaReference;

    @InputArgument(flag = "--targets", description = "Limit analysis to targets listed in the BED-format FILE.")
    private File targets;

    @InputArgument(flag = "--regions", description = "Limit analysis to the specified region, 0-base coordinates, end_position not included (same as BED format). Either '-' or '..' maybe used as a separator.")
    private String region;

    @InputArgument(flag = "--samples", description = "Limit analysis to samples listed (one per line) in the FILE.")
    private File samples;

    @InputArgument(flag = "--populations", description = "Each line of FILE should list a sample and a population which it is part of.  The population-based bayesian inference model will then be partitioned on the basis of the populations.")
    private File populations;

    @InputArgument(flag = "--cnv-map", description = "Read a copy number map from the BED file FILE")
    private File copyNumberMap;

    @NotNull(message = "vcf is required", groups = InputValidations.class)
    @FileIsNotEmpty(message = "vcf is empty", groups = OutputValidations.class)
    @OutputArgument(flag = "--vcf", persistFileData = true, mimeType = MimeType.TEXT_VCF, description = "Output VCF-format results to FILE.")
    private File vcf;

    @OutputArgument(flag = "--gvcf", description = "Write gVCF output, which indicates coverage in uncalled regions.")
    private File gvcf;

    @OutputArgument(flag = "--gvcf-chunk", description = "When writing gVCF output emit a record for every NUM bases.")
    private Integer gvcfChunk;

    @InputArgument(flag = "--variant-input", description = "Use variants reported in VCF file as input to the algorithm.  Variants in this file will included in the output even if there is not enough support in the data to pass input filters.")
    private Integer variantInput;

    @InputArgument(flag = "--only-user-input-alleles", description = "Only provide variant calls and genotype likelihoods for sites and alleles which are provided in the VCF input, and provide output in the VCF for all input alleles, not just those which have support in the data.")
    private Boolean onlyUseInputAlleles;

    @InputArgument(flag = "--haplotype-basis-alleles", description = "When specified, only variant alleles provided in this input VCF will be used for the construction of complex or haplotype alleles.")
    private File haplotypeBasisAlleles;

    @InputArgument(flag = "--report-all-haplotype-alleles", description = "At sites where genotypes are made over haplotype alleles, provide information about all alleles in output, not only those which are called.")
    private Boolean reportAllHaplotypeAlleles;

    @InputArgument(flag = "--report-monomorphic", description = "Report even loci which appear to be monomorphic, and report all considered alleles, even those which are not in called genotypes.  Loci which do not have any potential alternates have '.' for ALT.")
    private Boolean reportMonomorphic;

    @InputArgument(flag = "--pvar", defaultValue = "0.0", description = "Report sites if the probability that there is a polymorphism at the site is greater than N.  default: 0.0.  Note that post-filtering is generally recommended over the use of this parameter.")
    private Double pvar;

    @InputArgument(flag = "--strict-vcf", description = "Generate strict VCF format (FORMAT/GQ will be an int)")
    private Boolean strictVCF;

    @InputArgument(flag = "--theta", defaultValue = "0.001", description = "The expected mutation rate or pairwise nucleotide diversity among the population under analysis.  This serves as the single parameter to the Ewens Sampling Formula prior model")
    private Double theta;

    @InputArgument(flag = "--ploidy", defaultValue = "2", description = "Sets the default ploidy for the analysis to N.")
    private Integer ploidy;

    @InputArgument(flag = "--pooled-discrete", description = "Assume that samples result from pooled sequencing. Model pooled samples using discrete genotypes across pools. When using this flag, set --ploidy to the number of alleles in each sample or use the --cnv-map to define per-sample ploidy.")
    private Boolean pooledDiscrete;

    @InputArgument(flag = "--pooled-continuous", description = "Output all alleles which pass input filters, regardles of genotyping outcome or model.")
    private Boolean pooledContinuous;

    @InputArgument(flag = "--use-reference-allele", description = "This flag includes the reference allele in the analysis as if it is another sample from the same population.")
    private Boolean useReferenceAllele;

    @InputArgument(flag = "--reference-quality", defaultValue = "100,60", description = "Assign mapping quality of MQ to the reference allele at each site and base quality of BQ.")
    private String referenceQuality;

    @InputArgument(flag = "--no-snps", description = "Ignore SNP alleles.")
    private Boolean noSNPS;

    @InputArgument(flag = "--no-indels", description = "Ignore insertion and deletion alleles.")
    private Boolean noIndels;

    @InputArgument(flag = "--no-mnps", description = "Ignore multi-nuceotide polymorphisms, MNPs.")
    private Boolean noMNPS;

    @InputArgument(flag = "--no-complex", description = "Ignore complex events")
    private Boolean noComplex;

    @InputArgument(flag = "--use-best-n-alleles", description = "Evaluate only the best N SNP alleles, ranked by sum of supporting quality scores.")
    private Integer useBestNAlleles;

    @InputArgument(flag = "--E", description = "Allow haplotype calls with contiguous embedded matches of up to this length.")
    private Integer maxComplexGap;

    @InputArgument(flag = "--min-repeat-size", defaultValue = "5", description = "When assembling observations across repeats, require the total repeat length at least this many bp.")
    private Integer minRepeatSize;

    @InputArgument(flag = "--min-repeat-entropy", defaultValue = "0", description = "To detect interrupted repeats, build across sequence until it has entropy > N bits per bp.")
    private Integer minRepeatEntropy;

    @InputArgument(flag = "--no-partial-observations", description = "Exclude observations which do not fully span the dynamically-determined detection window.  (default, use all observations, dividing partial support across matching haplotypes when generating haplotypes.)")
    private Boolean noPartialObservations;

    @InputArgument(flag = "--dont-left-align-indels", description = "Turn off left-alignment of indels, which is enabled by default.")
    private Boolean doNotLeftAlignIndels;

    @InputArgument(flag = "--use-duplicate-reads", description = "Include duplicate-marked alignments in the analysis.")
    private Boolean useDuplicateReads;

    @InputArgument(flag = "--min-mapping-quality", defaultValue = "1", description = " Exclude alignments from analysis if they have a mapping quality less than Q.")
    private Integer minMappingQuality;

    @InputArgument(flag = "--min-base-quality", defaultValue = "0", description = "Exclude alleles from analysis if their supporting base quality is less than Q.")
    private Integer minBaseQuality;

    @InputArgument(flag = "--min-supporting-allele-qsum", defaultValue = "0", description = "Consider any allele in which the sum of qualities of supporting observations is at least Q.")
    private Integer minSupportingAlleleQSum;

    @InputArgument(flag = "--min-supporting-mapping-qsum", defaultValue = "0", description = "Consider any allele in which and the sum of mapping qualities of supporting reads is at least Q.")
    private Integer minSupportingMappingQSum;

    @InputArgument(flag = "--mismatch-base-quality-threshold", defaultValue = "10", description = "Count mismatches toward --read-mismatch-limit if the base quality of the mismatch is >= Q.")
    private Integer mismatchBaseQualityThreshold;

    @InputArgument(flag = "--read-mismatch-limit", description = "Exclude reads with more than N mismatches where each mismatch has base quality >= mismatch-base-quality-threshold.")
    private Integer readMismatchLimit;

    @InputArgument(flag = "--read-max-mismatch-fraction", defaultValue = "1.0", description = "Exclude reads with more than N [0,1] fraction of mismatches where each mismatch has base quality >= mismatch-base-quality-threshold")
    private Double readMaxMismatchFraction;

    @InputArgument(flag = "--read-snp-limit", description = "Exclude reads with more than N base mismatches, ignoring gaps with quality >= mismatch-base-quality-threshold.")
    private Integer readSNPLimit;

    @InputArgument(flag = "--read-indel-limit", description = "Exclude reads with more than N separate gaps.")
    private Integer readIndelLimit;

    @InputArgument(flag = "--standard-filters", description = "Use stringent input base and mapping quality filters")
    private Boolean standardFilters;

    @InputArgument(flag = "--min-alternate-fraction", defaultValue = "0.2", description = "Require at least this fraction of observations supporting an alternate allele within a single individual in the in order to evaluate the position.")
    private Double minAlternateFraction;

    @InputArgument(flag = "--min-alternate-count", defaultValue = "2", description = "Require at least this count of observations supporting an alternate allele within a single individual in order to evaluate the position.")
    private Integer minAlternateCount;

    @InputArgument(flag = "--min-alternate-qsum", defaultValue = "0", description = "Require at least this sum of quality of observations supporting an alternate allele within a single individual in order to evaluate the position.")
    private Integer minAlternateQSum;

    @InputArgument(flag = "--min-alternate-total", defaultValue = "1", description = "Require at least this count of observations supporting an alternate allele within the total population in order to use the allele in analysis.")
    private Integer minAlternateTotal;

    @InputArgument(flag = "--min-coverage", defaultValue = "0", description = "Require at least this coverage to process a site.")
    private Integer minCoverage;

    @InputArgument(flag = "--max-coverage", description = "Do not process sites with greater than this coverage.")
    private Integer maxCoverage;

    @InputArgument(flag = "--no-population-priors", description = "Equivalent to --pooled-discrete --hwe-priors-off and removal of Ewens Sampling Formula component of priors.")
    private Boolean noPopulationPriors;

    @InputArgument(flag = "--hwe-priors-off", description = "Disable estimation of the probability of the combination arising under HWE given the allele frequency as estimated by observation frequency.")
    private Boolean hwePriorsOff;

    @InputArgument(flag = "--binomial-obs-priors-off", description = "Disable incorporation of prior expectations about observations. Uses read placement probability, strand balance probability, and read position (5'-3') probability.")
    private Boolean binomialObsPriorsOff;

    @InputArgument(flag = "--allele-balance-priors-off", description = "Disable use of aggregate probability of observation balance between alleles as a component of the priors.")
    private Boolean alleleBalancePriorsOff;

    @InputArgument(flag = "--observation-bias", description = "Read length-dependent allele observation biases from FILE. The format is [length] [alignment efficiency relative to reference] where the efficiency is 1 if there is no relative observation bias.")
    private File observationBias;

    @InputArgument(flag = "--base-quality-cap", description = "Limit estimated observation quality by capping base quality at Q.")
    private Integer baseQualityCap;

    @InputArgument(flag = "--prob-contamination", description = "An estimate of contamination to use for all samples.")
    private Double probContamination;

    @InputArgument(flag = "--legacy-gls", description = "Use legacy (polybayes equivalent) genotype likelihood calculations")
    private Boolean legacyGLS;

    @InputArgument(flag = "--contamination-estimates", description = "A file containing per-sample estimates of contamination, such as those generated by VerifyBamID.")
    private File contaminationEstimates;

    @InputArgument(flag = "--report-genotype-likelihood-max", description = "Report genotypes using the maximum-likelihood estimate provided from genotype likelihoods.")
    private Boolean reportGenotypeLikelihoodMax;

    @InputArgument(flag = "--genotyping-max-iterations", defaultValue = "1000", description = "Iterate no more than N times during genotyping step.")
    private Integer genotypingMaxIterations;

    @InputArgument(flag = "--genotyping-max-banddepth", defaultValue = "6", description = "Integrate no deeper than the Nth best genotype by likelihood when genotyping.")
    private Integer genotypingMaxBandDepth;

    @InputArgument(flag = "--posterior-integration-limits", defaultValue = "1,3", description = "Integrate all genotype combinations in our posterior space which include no more than N samples with their Mth best data likelihood.")
    private String posteriorIntegrationLimits;

    @InputArgument(flag = "--exclude-unobserved-genotypes", description = "Skip sample genotypings for which the sample has no supporting reads.")
    private Boolean excludeUnobservedGenotypes;

    @InputArgument(flag = "--genotype-variant-threshold", description = "Limit posterior integration to samples where the second-best genotype likelihood is no more than log(N) from the highest genotype likelihood for the sample.")
    private Integer genotypeVariantThreshold;

    @InputArgument(flag = "--use-mapping-quality", description = "Use mapping quality of alleles when calculating data likelihoods.")
    private Boolean useMappingQuality;

    @InputArgument(flag = "--harmonic-indel-quality", description = "Use a weighted sum of base qualities around an indel, scaled by the distance from the indel.")
    private Boolean harmonicIndelQuality;

    @InputArgument(flag = "--read-dependence-factor", defaultValue = "0.9", description = "Incorporate non-independence of reads by scaling successive observations by this factor during data likelihood calculations.")
    private Double readDependenceFactor;

    @InputArgument(flag = "--genotype-qualities", description = "Calculate the marginal probability of genotypes and report as GQ in each sample field in the VCF output.")
    private Boolean genotypeQualities;

    public FreeBayes() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return FreeBayes.class;
    }

    @Override
    public String getExecutable() {
        return String.format(getModuleClass().getAnnotation(Application.class).executable(),
                getWorkflowName().toUpperCase());
    }

    public File getBam() {
        return bam;
    }

    public void setBam(File bam) {
        this.bam = bam;
    }

    public File getBamList() {
        return bamList;
    }

    public void setBamList(File bamList) {
        this.bamList = bamList;
    }

    public File getFastaReference() {
        return fastaReference;
    }

    public void setFastaReference(File fastaReference) {
        this.fastaReference = fastaReference;
    }

    public File getTargets() {
        return targets;
    }

    public void setTargets(File targets) {
        this.targets = targets;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public File getSamples() {
        return samples;
    }

    public void setSamples(File samples) {
        this.samples = samples;
    }

    public File getPopulations() {
        return populations;
    }

    public void setPopulations(File populations) {
        this.populations = populations;
    }

    public File getCopyNumberMap() {
        return copyNumberMap;
    }

    public void setCopyNumberMap(File copyNumberMap) {
        this.copyNumberMap = copyNumberMap;
    }

    public File getVcf() {
        return vcf;
    }

    public void setVcf(File vcf) {
        this.vcf = vcf;
    }

    public File getGvcf() {
        return gvcf;
    }

    public void setGvcf(File gvcf) {
        this.gvcf = gvcf;
    }

    public Integer getGvcfChunk() {
        return gvcfChunk;
    }

    public void setGvcfChunk(Integer gvcfChunk) {
        this.gvcfChunk = gvcfChunk;
    }

    public Integer getVariantInput() {
        return variantInput;
    }

    public void setVariantInput(Integer variantInput) {
        this.variantInput = variantInput;
    }

    public Boolean getOnlyUseInputAlleles() {
        return onlyUseInputAlleles;
    }

    public void setOnlyUseInputAlleles(Boolean onlyUseInputAlleles) {
        this.onlyUseInputAlleles = onlyUseInputAlleles;
    }

    public File getHaplotypeBasisAlleles() {
        return haplotypeBasisAlleles;
    }

    public void setHaplotypeBasisAlleles(File haplotypeBasisAlleles) {
        this.haplotypeBasisAlleles = haplotypeBasisAlleles;
    }

    public Boolean getReportAllHaplotypeAlleles() {
        return reportAllHaplotypeAlleles;
    }

    public void setReportAllHaplotypeAlleles(Boolean reportAllHaplotypeAlleles) {
        this.reportAllHaplotypeAlleles = reportAllHaplotypeAlleles;
    }

    public Boolean getReportMonomorphic() {
        return reportMonomorphic;
    }

    public void setReportMonomorphic(Boolean reportMonomorphic) {
        this.reportMonomorphic = reportMonomorphic;
    }

    public Double getPvar() {
        return pvar;
    }

    public void setPvar(Double pvar) {
        this.pvar = pvar;
    }

    public Boolean getStrictVCF() {
        return strictVCF;
    }

    public void setStrictVCF(Boolean strictVCF) {
        this.strictVCF = strictVCF;
    }

    public Double getTheta() {
        return theta;
    }

    public void setTheta(Double theta) {
        this.theta = theta;
    }

    public Integer getPloidy() {
        return ploidy;
    }

    public void setPloidy(Integer ploidy) {
        this.ploidy = ploidy;
    }

    public Boolean getPooledDiscrete() {
        return pooledDiscrete;
    }

    public void setPooledDiscrete(Boolean pooledDiscrete) {
        this.pooledDiscrete = pooledDiscrete;
    }

    public Boolean getPooledContinuous() {
        return pooledContinuous;
    }

    public void setPooledContinuous(Boolean pooledContinuous) {
        this.pooledContinuous = pooledContinuous;
    }

    public Boolean getUseReferenceAllele() {
        return useReferenceAllele;
    }

    public void setUseReferenceAllele(Boolean useReferenceAllele) {
        this.useReferenceAllele = useReferenceAllele;
    }

    public String getReferenceQuality() {
        return referenceQuality;
    }

    public void setReferenceQuality(String referenceQuality) {
        this.referenceQuality = referenceQuality;
    }

    public Boolean getNoSNPS() {
        return noSNPS;
    }

    public void setNoSNPS(Boolean noSNPS) {
        this.noSNPS = noSNPS;
    }

    public Boolean getNoIndels() {
        return noIndels;
    }

    public void setNoIndels(Boolean noIndels) {
        this.noIndels = noIndels;
    }

    public Boolean getNoMNPS() {
        return noMNPS;
    }

    public void setNoMNPS(Boolean noMNPS) {
        this.noMNPS = noMNPS;
    }

    public Boolean getNoComplex() {
        return noComplex;
    }

    public void setNoComplex(Boolean noComplex) {
        this.noComplex = noComplex;
    }

    public Integer getUseBestNAlleles() {
        return useBestNAlleles;
    }

    public void setUseBestNAlleles(Integer useBestNAlleles) {
        this.useBestNAlleles = useBestNAlleles;
    }

    public Integer getMaxComplexGap() {
        return maxComplexGap;
    }

    public void setMaxComplexGap(Integer maxComplexGap) {
        this.maxComplexGap = maxComplexGap;
    }

    public Integer getMinRepeatSize() {
        return minRepeatSize;
    }

    public void setMinRepeatSize(Integer minRepeatSize) {
        this.minRepeatSize = minRepeatSize;
    }

    public Integer getMinRepeatEntropy() {
        return minRepeatEntropy;
    }

    public void setMinRepeatEntropy(Integer minRepeatEntropy) {
        this.minRepeatEntropy = minRepeatEntropy;
    }

    public Boolean getNoPartialObservations() {
        return noPartialObservations;
    }

    public void setNoPartialObservations(Boolean noPartialObservations) {
        this.noPartialObservations = noPartialObservations;
    }

    public Boolean getDoNotLeftAlignIndels() {
        return doNotLeftAlignIndels;
    }

    public void setDoNotLeftAlignIndels(Boolean doNotLeftAlignIndels) {
        this.doNotLeftAlignIndels = doNotLeftAlignIndels;
    }

    public Boolean getUseDuplicateReads() {
        return useDuplicateReads;
    }

    public void setUseDuplicateReads(Boolean useDuplicateReads) {
        this.useDuplicateReads = useDuplicateReads;
    }

    public Integer getMinMappingQuality() {
        return minMappingQuality;
    }

    public void setMinMappingQuality(Integer minMappingQuality) {
        this.minMappingQuality = minMappingQuality;
    }

    public Integer getMinBaseQuality() {
        return minBaseQuality;
    }

    public void setMinBaseQuality(Integer minBaseQuality) {
        this.minBaseQuality = minBaseQuality;
    }

    public Integer getMinSupportingAlleleQSum() {
        return minSupportingAlleleQSum;
    }

    public void setMinSupportingAlleleQSum(Integer minSupportingAlleleQSum) {
        this.minSupportingAlleleQSum = minSupportingAlleleQSum;
    }

    public Integer getMinSupportingMappingQSum() {
        return minSupportingMappingQSum;
    }

    public void setMinSupportingMappingQSum(Integer minSupportingMappingQSum) {
        this.minSupportingMappingQSum = minSupportingMappingQSum;
    }

    public Integer getMismatchBaseQualityThreshold() {
        return mismatchBaseQualityThreshold;
    }

    public void setMismatchBaseQualityThreshold(Integer mismatchBaseQualityThreshold) {
        this.mismatchBaseQualityThreshold = mismatchBaseQualityThreshold;
    }

    public Integer getReadMismatchLimit() {
        return readMismatchLimit;
    }

    public void setReadMismatchLimit(Integer readMismatchLimit) {
        this.readMismatchLimit = readMismatchLimit;
    }

    public Double getReadMaxMismatchFraction() {
        return readMaxMismatchFraction;
    }

    public void setReadMaxMismatchFraction(Double readMaxMismatchFraction) {
        this.readMaxMismatchFraction = readMaxMismatchFraction;
    }

    public Integer getReadSNPLimit() {
        return readSNPLimit;
    }

    public void setReadSNPLimit(Integer readSNPLimit) {
        this.readSNPLimit = readSNPLimit;
    }

    public Integer getReadIndelLimit() {
        return readIndelLimit;
    }

    public void setReadIndelLimit(Integer readIndelLimit) {
        this.readIndelLimit = readIndelLimit;
    }

    public Boolean getStandardFilters() {
        return standardFilters;
    }

    public void setStandardFilters(Boolean standardFilters) {
        this.standardFilters = standardFilters;
    }

    public Double getMinAlternateFraction() {
        return minAlternateFraction;
    }

    public void setMinAlternateFraction(Double minAlternateFraction) {
        this.minAlternateFraction = minAlternateFraction;
    }

    public Integer getMinAlternateCount() {
        return minAlternateCount;
    }

    public void setMinAlternateCount(Integer minAlternateCount) {
        this.minAlternateCount = minAlternateCount;
    }

    public Integer getMinAlternateQSum() {
        return minAlternateQSum;
    }

    public void setMinAlternateQSum(Integer minAlternateQSum) {
        this.minAlternateQSum = minAlternateQSum;
    }

    public Integer getMinAlternateTotal() {
        return minAlternateTotal;
    }

    public void setMinAlternateTotal(Integer minAlternateTotal) {
        this.minAlternateTotal = minAlternateTotal;
    }

    public Integer getMinCoverage() {
        return minCoverage;
    }

    public void setMinCoverage(Integer minCoverage) {
        this.minCoverage = minCoverage;
    }

    public Integer getMaxCoverage() {
        return maxCoverage;
    }

    public void setMaxCoverage(Integer maxCoverage) {
        this.maxCoverage = maxCoverage;
    }

    public Boolean getNoPopulationPriors() {
        return noPopulationPriors;
    }

    public void setNoPopulationPriors(Boolean noPopulationPriors) {
        this.noPopulationPriors = noPopulationPriors;
    }

    public Boolean getHwePriorsOff() {
        return hwePriorsOff;
    }

    public void setHwePriorsOff(Boolean hwePriorsOff) {
        this.hwePriorsOff = hwePriorsOff;
    }

    public Boolean getBinomialObsPriorsOff() {
        return binomialObsPriorsOff;
    }

    public void setBinomialObsPriorsOff(Boolean binomialObsPriorsOff) {
        this.binomialObsPriorsOff = binomialObsPriorsOff;
    }

    public Boolean getAlleleBalancePriorsOff() {
        return alleleBalancePriorsOff;
    }

    public void setAlleleBalancePriorsOff(Boolean alleleBalancePriorsOff) {
        this.alleleBalancePriorsOff = alleleBalancePriorsOff;
    }

    public File getObservationBias() {
        return observationBias;
    }

    public void setObservationBias(File observationBias) {
        this.observationBias = observationBias;
    }

    public Integer getBaseQualityCap() {
        return baseQualityCap;
    }

    public void setBaseQualityCap(Integer baseQualityCap) {
        this.baseQualityCap = baseQualityCap;
    }

    public Double getProbContamination() {
        return probContamination;
    }

    public void setProbContamination(Double probContamination) {
        this.probContamination = probContamination;
    }

    public Boolean getLegacyGLS() {
        return legacyGLS;
    }

    public void setLegacyGLS(Boolean legacyGLS) {
        this.legacyGLS = legacyGLS;
    }

    public File getContaminationEstimates() {
        return contaminationEstimates;
    }

    public void setContaminationEstimates(File contaminationEstimates) {
        this.contaminationEstimates = contaminationEstimates;
    }

    public Boolean getReportGenotypeLikelihoodMax() {
        return reportGenotypeLikelihoodMax;
    }

    public void setReportGenotypeLikelihoodMax(Boolean reportGenotypeLikelihoodMax) {
        this.reportGenotypeLikelihoodMax = reportGenotypeLikelihoodMax;
    }

    public Integer getGenotypingMaxIterations() {
        return genotypingMaxIterations;
    }

    public void setGenotypingMaxIterations(Integer genotypingMaxIterations) {
        this.genotypingMaxIterations = genotypingMaxIterations;
    }

    public Integer getGenotypingMaxBandDepth() {
        return genotypingMaxBandDepth;
    }

    public void setGenotypingMaxBandDepth(Integer genotypingMaxBandDepth) {
        this.genotypingMaxBandDepth = genotypingMaxBandDepth;
    }

    public String getPosteriorIntegrationLimits() {
        return posteriorIntegrationLimits;
    }

    public void setPosteriorIntegrationLimits(String posteriorIntegrationLimits) {
        this.posteriorIntegrationLimits = posteriorIntegrationLimits;
    }

    public Boolean getExcludeUnobservedGenotypes() {
        return excludeUnobservedGenotypes;
    }

    public void setExcludeUnobservedGenotypes(Boolean excludeUnobservedGenotypes) {
        this.excludeUnobservedGenotypes = excludeUnobservedGenotypes;
    }

    public Integer getGenotypeVariantThreshold() {
        return genotypeVariantThreshold;
    }

    public void setGenotypeVariantThreshold(Integer genotypeVariantThreshold) {
        this.genotypeVariantThreshold = genotypeVariantThreshold;
    }

    public Boolean getUseMappingQuality() {
        return useMappingQuality;
    }

    public void setUseMappingQuality(Boolean useMappingQuality) {
        this.useMappingQuality = useMappingQuality;
    }

    public Boolean getHarmonicIndelQuality() {
        return harmonicIndelQuality;
    }

    public void setHarmonicIndelQuality(Boolean harmonicIndelQuality) {
        this.harmonicIndelQuality = harmonicIndelQuality;
    }

    public Double getReadDependenceFactor() {
        return readDependenceFactor;
    }

    public void setReadDependenceFactor(Double readDependenceFactor) {
        this.readDependenceFactor = readDependenceFactor;
    }

    public Boolean getGenotypeQualities() {
        return genotypeQualities;
    }

    public void setGenotypeQualities(Boolean genotypeQualities) {
        this.genotypeQualities = genotypeQualities;
    }

    public static void main(String[] args) {
        FreeBayes module = new FreeBayes();
        module.setWorkflowName("TEST");
        module.setFastaReference(new File("/tmp", "asdf.fa"));
        module.setBam(new File("/tmp", "asdf.bam"));
        module.setVcf(new File("/tmp", "asdf.vcf"));
        try {
            module.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
