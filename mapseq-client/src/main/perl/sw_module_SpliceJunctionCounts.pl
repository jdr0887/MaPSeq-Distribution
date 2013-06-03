##########
#
# Script:  sw_module_SpliceJunctionCounts.pl
# Date:    2010.07.12
# Author:  Sara Grimm [sacheek@med.unc.edu]
#
# Purpose: Given a BAM file (aligned reads in genomic coordinates) and a list of splice junctions, calculate the counts per junction.
#
# Input:   - BAM file
#          - splice junction list; format: first column must be junction coordinates (syntax = "chr1:10000:-,chr1:20000:-" or "chr1:10000,chr1:20000");
#            strand info is optional [and not used in this script]; any following columns [such as annotations for those junctions] are ignored
#          - output file
#          - paths to java and picard/SamFormatConvert.jar
#
# Output:  2-column (tab-delimited file) of junction coordinates and counts
#
##########

$bamfile = $ARGV[0];          # aligned reads translated to genomic coordinates
$outfile = $ARGV[1];          # splice junction quantification output (coordinates of junction, counts)
$junclist = $ARGV[2];         # list of known junctions, only first column is used  (expects format "chrN:p1:strand,chrN:p2:strand" or 
                              # "chrN:p1,chrN:p2"); strand info is optional; script assumes junction positions are from SAME chromosome -- 
                              # interchromosomal junctions are unlikely to be properly handled; script will swap p1 & p1 to numerical
                              # order before processing (if necessary)
$java = $ARGV[3];             # path to java
$picard_convert = $ARGV[4];   # e.g. /home/sacheek/nextgen/picard-tools-1.19/SamFormatConverter.jar
$tdir = $ARGV[5];             # temporary directory

###########################################################################################################################


# Step 1: Read in known splice junctions.  Initialize counts at zero.
open(SJ, "$junclist"); %sj2ct = (); @order = (); %sj2outformat = ();
while (<SJ>) {
  chomp $_;
  @ar = split/\t/, $_;
  @ar1 = split/\,/, $ar[0];
  @ar2a = split/\:/, $ar1[0];
  @ar2b = split/\:/, $ar1[1];
  $chrA = $ar2a[0]; $posA = $ar2a[1];
  $chrB = $ar2b[0]; $posB = $ar2b[1];
  if ($chrA eq $chrB && $posA > $posB) { $usesj = "$chrB:$posB,$chrA:$posA"; }
  else { $usesj = "$chrA:$posA,$chrB:$posB"; }
  $sj2ct{$usesj} = 0;
  $sj2outformat{$usesj} = $ar[0];
  push @order, $usesj;
}


# Step 2: Convert BAM to SAM.
system "$java -Xmx2g -jar $picard_convert VALIDATION_STRINGENCY=SILENT TMP_DIR=$tdir INPUT=$bamfile OUTPUT=$tdir/input.sam";


# Step 3:  Scan through SAM file for reads crossing introns.  Determine genomic coordinates of junction and increment count.
open(IN, "$tdir/input.sam");
while (<IN>) {
  next if ($_ =~ /^\@/);
  @ar1 = split/\t/, $_;
  $cigar = $ar1[5];
  $chr = $ar1[2]; $startpos = $ar1[3];
  $bitflag = $ar1[1];
  $hasflag4 = SUB_flag4($bitflag);
  next if ($hasflag4 eq "yes");
  next unless ($cigar =~ /N/); #only evaluate reads crossing introns
  @ar2a = split/\D+/, $cigar;                     #cigar chunk sizes
  @ar2b = split/\d+/, $cigar; splice(@ar2b,0,1);  #cigar chunk types
  @segs = (); $prevpos = "";
  for ($i=0; $i<$#ar2a+1; $i++) {
    next if ($ar2b[$i] eq "I");  #insert relative to genome
    if ($prevpos eq "") { $p1 = $startpos; } else { $p1 = $prevpos+1; }
    $p2 = $p1+$ar2a[$i]-1;
    push @segs, "$ar2b[$i]\t$p1\t$p2";
    $prevpos = $p2;
  }
  for ($i=0; $i<$#segs+1; $i++) {
    @ar3 = split/\t/, $segs[$i];
    next unless ($ar3[0] eq "N");
    @ar3a = split/\t/, $segs[$i-1];
    @ar3b = split/\t/, $segs[$i+1];
    $thisjunc = "$chr:$ar3a[2],$chr:$ar3b[1]";
    if (exists $sj2ct{$thisjunc}) { $sj2ct{$thisjunc}++; }
#    else { print "$ar1[0]\t$ar1[2]\t$ar1[3]\t$ar1[5]\t$ar1[9]\tJUNC: $thisjunc\n"; }
  }
}


# Step 4:  Print counts to output file.
open(OUT, ">$outfile");
foreach $usesj (@order) { print OUT "$sj2outformat{$usesj}\t$sj2ct{$usesj}\n"; }
close(OUT);

system "rm $tdir/input.sam";

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

