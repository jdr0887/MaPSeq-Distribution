##########
#
# Script:  sw_module_GeneCountsRPKM.pl
# Date:    2010.05.14
# Author:  Sara Grimm [sacheek@med.unc.edu]
#
# Purpose: Calculate counts & RPKM for given BAM file; both at transcript & gene level.
#
# Input:   input file name (BAM), names of output files, flat file giving transcript/gene assocations & transcript lengths,
#          type of gene length for RPKM calculation, path to java, path to picard SamFormatConverter.jar, tempdir
#
# Output:  transcript-level & gene-level output files
#          [format = tab-delimited, 4 columns: id, counts, coverage, RPKM]
#
##########


$infile = $ARGV[0];          # BAM file
$outTR = $ARGV[1];           # output, by transcript
$outGENE = $ARGV[2];         # output, collapsed on gene
$mapfile = $ARGV[3];         # flat file describing transcript/gene relationship, coordinates, etc (flat file output of PrepTranscriptDB module)
                             # e.g. ~/mapseq-pipeline/data/annotation_reference/hg19_transcripts.hg19.20091027.trmap
$genelength = $ARGV[4];      # [median,mean,shortest,longest] determine length based on transcripts for each gene in $mapfile
$java = $ARGV[5];            # e.g. /usr/bin/java
$picard_convert = $ARGV[6];  # e.g. /home/sacheek/nextgen/picard-tools-1.19/SamFormatConverter.jar
$tdir = $ARGV[7];            # temporary directory for writing intermediate files

###########################################################################################################################

open(TDB, "$mapfile");
%trid2gene = (); %gene2lengthlist = (); @go = (); @to = ();
%trlen = (); %genelen = ();
while (<TDB>) {
  chomp $_; @ar = split/\t/, $_;
  $trlen{$ar[0]} = $ar[2];
  push @to, $ar[0];
  if ($ar[1] ne "") {
    $trid2gene{$ar[0]} = $ar[1];
    $gene2lengthlist{$ar[1]} .= "$ar[2],";
  }
}
close(TDB);
foreach $gene (keys %gene2lengthlist) {
  chop $gene2lengthlist{$gene};
  @ar = split/\,/, $gene2lengthlist{$gene};
  @ar = sort {$a <=> $b} @ar;
  $n = @ar; $uselen = "";
  if ($genelength eq "shortest") { $uselen = $ar[0]; }
  if ($genelength eq "longest") { $uselen = $ar[$#ar]; }
  if ($genelength eq "mean") {
    $sum = 0; foreach $a (@ar) { $sum += $a; } $uselen = sprintf("%.0f", $sum/$n);
  }
  if ($genelength eq "median") {
    if (($#ar+1)%2 == 0) { $median = ($ar[($#ar-1)/2] + $ar[($#ar+1)/2])/2; } else { $median = $ar[$#ar/2]; }
    $uselen = sprintf("%.0f", $median);
  }
  if ($uselen ne "") { $genelen{$gene} = $uselen; }
  push @go, $gene;
}
@go = sort @go;
@to = sort @to;

system "$java -Xmx2g -jar $picard_convert VALIDATION_STRINGENCY=SILENT TMP_DIR=$tdir INPUT=$infile OUTPUT=$tdir/infile.sam";

%gene2cts = (); %tr2cts = (); %gene2bases = (); %tr2bases = (); $readtot = 0;
open(IN, "$tdir/infile.sam");
while (<IN>) {
  next if ($_ =~ /^\@/);
  @ar1 = split/\t/, $_;
  $readtot++;
  $bitflag = $ar1[1];
  $hasflag4 = SUB_flag4($bitflag);
  next if ($hasflag4 eq "yes");
  $cigar = $ar1[5];
  @ar2a = split/\D+/, $cigar;                     #cigar chunk sizes
  @ar2b = split/\d+/, $cigar; splice(@ar2b,0,1);  #cigar chunk types
  $basect = 0;
  for ($i=0; $i<$#ar2a+1; $i++) { if ($ar2b[$i] eq "M" || $ar2b[$i] eq "I") { $basect += $ar2a[$i]; } }
  $tr2bases{$ar1[2]} += $basect;
  $tr2cts{$ar1[2]}++;
  if (exists $trid2gene{$ar1[2]}) {
    $gene = $trid2gene{$ar1[2]};
    $gene2cts{$gene}++;
    $gene2bases{$gene} += $basect;
  }
}
close(IN);

open(OUT1, ">$outTR");
foreach $trid (@to) {
  $bct = $tr2bases{$trid}; if ($bct eq "") { $bct = 0; }
  $rct = $tr2cts{$trid}; if ($rct eq "") { $rct = 0; }
  $cov = $bct/$trlen{$trid};
  $rpkm = ($rct*(10**9))/($readtot*$trlen{$trid});
  print OUT1 "$trid\t$rct\t$cov\t$rpkm\n";
}
close(OUT1);
open(OUT2, ">$outGENE");
foreach $gene (@go) {
  $bct = $gene2bases{$gene}; if ($bct eq "") { $bct = 0; }
  $rct = $gene2cts{$gene}; if ($rct eq "") { $rct = 0; }
  $cov = $bct/$genelen{$gene};
  $rpkm = ($rct*(10**9))/($readtot*$genelen{$gene});
  print OUT2 "$gene\t$rct\t$cov\t$rpkm\n";
}
close(OUT2);

if (-e "$tdir/infile.sam") { system "rm $tdir/infile.sam"; }

exit(0);

###########################################################################################################################


sub SUB_flag4 {
  $flagin = $_[0];
  @farray = ();
  $thisN = 1024*2;
  for ($z=10; $z>=0; $z--) {
    $thisN = $thisN/2;
    if ($flagin >= $thisN) { $farray[$z] = 1; $flagin -= $thisN; } else { $farray[$z] = 0; }
  }
  if ($farray[2] == 1) { return "yes"; } else { return "no"; } # here, 'yes' means unmapped (as in, 'yes, flag=4 is set')
}

