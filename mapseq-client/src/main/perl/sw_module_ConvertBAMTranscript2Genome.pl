##########
#
# Script:  sw_module_ConvertBAMTranscript2Genome.pl
# Date:    2010.05.11
# Update:  2010.08.19 - Script does not attempt to handle mate info for SE lanes & returns an error for PE lanes.  Will need to re-address PE lanes later.
# Update:  2011.02.27 - Now handles PE lanes.
# Author:  Sara Grimm [sacheek@med.unc.edu]
#
# Purpose: Translate reads mapped to transcript to genomic coordinates.  Output file is also sorted by coordinate.
#
# Input:   input file name (BAM), output file name, mapfile [specifies pairwise alignments between transcripts & genomic coordinates],
#          header file [contains @SQ records for genome], path to java, path to picard tools SamFormatConverter.jar & SortSam.jar,
#          path to samtools, tempDir
#
# Output:  Sorted BAM file with reads mapped to genomic coordinates & corresponding ~.bai file.
#
##########

use strict;

my $infile = $ARGV[0];          # BAM file with reads mapped to transcripts
my $outfile = $ARGV[1];         # BAM file with reads mapped to genomic coordinates
my $mapfile = $ARGV[2];         # file specifying pairwise alignments between transcripts & genome (e.g. ~/svnroot/mapseq-complete/trunk/mapseq-pipeline/data/annotation_reference/hg19_transcripts.hg19.20091027.trmap)
my $sqheader = $ARGV[3];        # list of @SQ records to use in header of output file (e.g. ~/svnroot/mapseq-complete/trunk/mapseq-pipeline/data/annotation_reference/headerSQ.hg19.txt)
my $java = $ARGV[4];            # e.g. /usr/bin/java
my $picard_convert = $ARGV[5];  # e.g. /home/sacheek/nextgen/picard-tools-1.19/SamFormatConverter.jar
my $picard_sort = $ARGV[6];     # e.g. /home/sacheek/nextgen/picard-tools-1.19/SortSam.jar
my $samtools = $ARGV[7];        # e.g. /usr/bin/samtools
my $tdir = $ARGV[8];            # tempdir


###########################################################################################################################

my %revcom = ('A' => 'T', 'T' => 'A', 'C' => 'G', 'G' => 'C');

open(MAPF, "$mapfile"); my @mapf = <MAPF>; close(MAPF); my %tr2gen = (); my %tr2chr = (); my %tr2str = ();
my ($mline, $c, $str, @mar1, @mar2);
foreach $mline (@mapf) {
  chomp $mline;
  @mar1 = split/\t/, $mline;
  @mar2 = split/\:/, $mar1[3]; $c = $mar2[0]; $str = $mar2[2];
  $tr2gen{$mar1[0]} = "$mar1[4]\t$mar2[1]";
  $tr2chr{$mar1[0]} = $c;
  $tr2str{$mar1[0]} = $str;
}
@mapf = (); undef @mapf;

my ($line, $mod, $trid, $pos1, $cigar, $mapseqtr, $mapchr, $prevT, $prevG, $totalcig, $totalgenrange, $cigsize, $cigtype, $pA, $pB, $genrange, $trarange, $thiscig, $gA, $gB, $useA, $useB, $pt, $size);
my ($i, $j, $k, $findT, $thisG, $outcig, $ag, $gpos, $cont, $n1, $n2, $useseq, $usequal, $useflag, $readID, $readstrand, $flag4, $wread, $out, $a, $a14, $a15, $p1, $p2, $p1diff, $p2diff, $c1, $c2, $preline);
my (@ar1, @ar2a, @ar2b, @mapseqegs, @tsegs, @gsegs, @ar3a, @ar3b, @allg, @allt, @gj, @tj, @tmp, @aa, @ar2, @ar1pre, @ar3);
my (%allreadid, $outL, $readidroot, $readstr1, $readstr2, $readstrM1, $readstrM2, $flag41, $flag42, $flag81, $flag82, @xar1, @xar2, @xar);
my (@readIDlist, $flagagree, $newflag1, $newflag2);

system "$java -Xmx2g -jar $picard_sort VALIDATION_STRINGENCY=SILENT TMP_DIR=$tdir INPUT=$infile OUTPUT=$tdir/infile.wRG.mod.namesort.bam SORT_ORDER=queryname";
system "$java -Xmx2g -jar $picard_convert VALIDATION_STRINGENCY=SILENT TMP_DIR=$tdir INPUT=$tdir/infile.wRG.mod.namesort.bam OUTPUT=$tdir/infile.wRG.mod.sam";
open(INT1, "$tdir/infile.wRG.mod.sam");
system "cp $sqheader $tdir/conversion.sam";  # put new @SQ lines in header of new SAM file
open(OUTT1, ">>$tdir/conversion.sam");


%allreadid = ();
while (<INT1>) {
  $line = $_; chomp $line;
  if ($line =~ /^\@/) { #header
    if ($line =~ /^\@RG/) { print OUTT1 "$line\n"; } # read group line
    next;
  }
  @ar1 = split/\t/, $line; $outL = "";
  if ($ar1[2] eq "*") {
    $outL = "$line\n"; $useflag = $ar1[1];
  }
  else {
    $trid = $ar1[2];
    $pos1 = $ar1[3];
    $cigar = $ar1[5];  $cigar =~ s/S/I/g;  #replace all occurrences of "soft clip" with "insertion"
    $mapseqtr = $tr2str{$trid}; $mapchr = $tr2chr{$trid};
    @ar2a = split/\D+/, $cigar;                     #cigar chunk sizes
    @ar2b = split/\d+/, $cigar; splice(@ar2b,0,1);  #cigar chunk types
    @mapseqegs = split/\t/, $tr2gen{$trid};
    @tsegs = split/\,/, $mapseqegs[0];
    @gsegs = split/\,/, $mapseqegs[1];
    $prevT = ""; $prevG = ""; $totalcig = ""; $totalgenrange = "";
    for ($i=0; $i<$#ar2a+1; $i++) {
      $cigsize = $ar2a[$i];
      $cigtype = $ar2b[$i];
      $pA = "null"; $pB = "null"; $genrange = ""; $trarange = ""; $thiscig = "";
      if ($cigtype eq "M") {
        $pA = $pos1; $pB = $pos1+$cigsize-1;
        $pos1 += $cigsize;
        for ($j=0; $j<$#tsegs+1; $j++) {
          @ar3a = split/\-/, $tsegs[$j];
          next if ($pB < $ar3a[0] || $pA > $ar3a[1]);
          if ($pA >= $ar3a[0]) { $useA = $pA; } else { $useA = $ar3a[0]; }
          if ($pB >= $ar3a[1]) { $useB = $ar3a[1]; } else { $useB = $pB; }
          @ar3b = split/\-/, $gsegs[$j];
          if ($mapseqtr eq "+") { $gA = $ar3b[0] + ($useA-$ar3a[0]); $gB = $ar3b[0] + ($useB-$ar3a[0]); }
          else { $gA = $ar3b[0] - ($useA-$ar3a[0]); $gB = $ar3b[0] - ($useB-$ar3a[0]); }
          $genrange .= "$gA-$gB,";  $trarange .= "$useA-$useB,";
        }
        chop $genrange; chop $trarange;
        if ($genrange eq "") { $thiscig .= "$cigsize\I"; }
        else {
          @allg = split/\,/, $genrange;
          @allt = split/\,/, $trarange;
          $pt = $pA-1;
          for ($j=0; $j<$#allt+1; $j++) {
            @gj = split/\-/, $allg[$j];
            @tj = split/\-/, $allt[$j];
            if ($prevG ne "") {
              $size = abs($gj[0]-$prevG)-1;
              if ($size > 0) { $thiscig .= "$size"; $thiscig .= "N";}
            }
            if ($tj[0] > $pt+1) { $size = $tj[0]-$pt-1; $thiscig .= "$size\I"; }
            $size = $tj[1]-$tj[0]+1; $thiscig .= "$size\M";
            $pt = $tj[1]; $prevG = $gj[1]; $prevT = $tj[1];
          }
          if ($tj[1] < $pB) { $size = $pB-$tj[1]; $thiscig .= "$size\I"; }
        }
      }
      if ($cigtype eq "I") { $thiscig = "$cigsize\I"; }
      if ($cigtype eq "D") {
        $pos1 += $cigsize;
        for ($k=1; $k<$cigsize+1; $k++) { #for each deleted position that is aligned to genome, add "1D" (will merge later); also check for introns
          $findT = $prevT+$k;
          for ($j=0; $j<$#tsegs+1; $j++) {
            @ar3a = split/\-/, $tsegs[$j];
            @ar3b = split/\-/, $gsegs[$j];
            next unless ($findT >= $ar3a[0] && $ar3a[1] >= $findT);
            if ($mapseqtr eq "+") { $thisG = $ar3b[0] + ($findT-$ar3a[0]); }
            else { $thisG = $ar3b[0] - ($findT-$ar3a[0]); }
            if ($prevG ne "") {
              $size = abs($thisG-$prevG)-1;
              if ($size > 0) { $thiscig .= "$size"; $thiscig .= "N";}
            }
            $thiscig .= "1D";
            $prevG = $thisG;
          }
        }
      }
      $totalcig .= $thiscig;
      if ($genrange ne "") { if ($totalgenrange ne "") { $totalgenrange .= ","; } $totalgenrange .= $genrange; }
    }
    if ($mapseqtr eq "-") { #flip order of cigar string & totalgenrange pieces
      @ar3a = split/\D+/, $totalcig;                     #cigar chunk sizes
      @ar3b = split/\d+/, $totalcig; splice(@ar3b,0,1);  #cigar chunk types
      $outcig = ""; @ar3a = reverse @ar3a; @ar3b = reverse @ar3b;
      for ($j=0; $j<$#ar3a+1; $j++) { $outcig .= "$ar3a[$j]$ar3b[$j]"; }
      @allg = split/\,/, $totalgenrange; @tmp = reverse @allg; $totalgenrange = "";
      foreach $ag (@tmp) { @ar3 = split/\-/, $ag; $totalgenrange .= "$ar3[1]-$ar3[0],"; }
      chop $totalgenrange;
    }
    else { $outcig = $totalcig; }
    @allg = split/\-/, $totalgenrange;
    $gpos = $allg[0];
    if ($gpos eq "") { @aa = split/\-/, $mapseqegs[1]; if ($mapseqtr eq "-") { $gpos = $aa[0]; } else { $gpos = $aa[$#aa]; } }
#'back up' gpos if xD cigar string before xM
    if ($outcig =~ /M/) {
      @ar3a = split/\D+/, $outcig;                     #cigar chunk sizes
      @ar3b = split/\d+/, $outcig; splice(@ar3b,0,1);  #cigar chunk types
      for ($j=0; $j<$#ar3a+1; $j++) {
        last if ($ar3b[$j] eq "M");
        if ($ar3b[$j] eq "D") { $gpos -= $ar3a[$j]; }
      }
    }
#merge consecutive I's, D's, M's
    @ar3a = split/\D+/, $outcig;                     #cigar chunk sizes
    @ar3b = split/\d+/, $outcig; splice(@ar3b,0,1);  #cigar chunk types
    $cont = 1;
    while ($cont == 1) {
      $n1 = @ar3a;
      for ($j=0; $j<$n1-1; $j++) {
        if ($ar3b[$j] eq $ar3b[$j+1] && "MDI" =~ /$ar3b[$j]/) { $ar3a[$j] += $ar3a[$j+1]; splice(@ar3a, $j+1, 1); splice(@ar3b, $j+1, 1); last; }
      }
      $n2 = @ar3a;
      if ($n1 == $n2) { $cont = 0; }
    }   
    $outcig = ""; for ($j=0; $j<$#ar3a+1; $j++) { $outcig .= "$ar3a[$j]$ar3b[$j]"; }
    if ($cigar eq "*") { $outcig = $cigar; }
#swap flag & sequence to match strand info
    if ($mapseqtr eq "-") { $useseq = SUB_revcom($ar1[9]); $usequal = SUB_rev($ar1[10]); $useflag = SUB_flag_swapstr($ar1[1]); }
    else { $useseq = $ar1[9]; $usequal = $ar1[10]; $useflag = $ar1[1]; }
#SE: !qname, flag, rname, pos, !mapq, cigar, !mrnm, mpos, !isize, seq, qual, !tags... (for first pass, treat all mappings as SE)
    $outL = "$ar1[0]\t$useflag\t$mapchr\t$gpos\t$ar1[4]\t$outcig\t$ar1[6]\t$gpos\t$ar1[8]\t$useseq\t$usequal";
    for ($i=11; $i<$#ar1+1; $i++) { $outL .= "\t$ar1[$i]"; } $outL .= "\n";
  }

  $wread = SUB_whichread($useflag);
  @aa = split/\#/, $ar1[0]; $readidroot = $aa[0];
  if ($wread == 0) { print OUTT1 "$outL"; } # read has no mate, or read (first/second) is unknown so treat as SE
  else { $allreadid{$readidroot}{$wread} = $outL; } # read is paired
  if (exists $allreadid{$readidroot}{1} && exists $allreadid{$readidroot}{2}) { # both mates of pair have info, ready for output
    @xar1 = split/\t/, $allreadid{$readidroot}{1};
    @xar2 = split/\t/, $allreadid{$readidroot}{2};
    ($readstr1, $readstrM1, $flag41, $flag81) = SUB_flaginfo($xar1[1]);
    ($readstr2, $readstrM2, $flag42, $flag82) = SUB_flaginfo($xar2[1]);
    $flagagree = 1;
    if ($flag41 eq "m" && $flag82 eq "u") { $flagagree = 0; }
    if ($flag41 eq "u" && $flag82 eq "m") { $flagagree = 0; }
    if ($flag42 eq "m" && $flag81 eq "u") { $flagagree = 0; }
    if ($flag42 eq "u" && $flag81 eq "m") { $flagagree = 0; }
    if ($flagagree == 0) { # set both reads as unmapped
      splice(@xar1, 1, 8, "77", "*", "0", "0", "*", "*", "0", "0");
      splice(@xar2, 1, 8, "141", "*", "0", "0", "*", "*", "0", "0");
    }
    else {
      ($newflag1, $newflag2) = SUB_fixmateflags($xar1[1],$xar2[1]); # fix strand info in bit flag
      splice(@xar1, 1, 1, $newflag1);
      splice(@xar2, 1, 1, $newflag2);
      if ($flag41 eq "m" && $flag42 eq "m") { # both reads mapped
        splice(@xar1, 7, 1, $xar2[3]); splice(@xar2, 7, 1, $xar1[3]);
        if ($xar1[2] eq $xar2[2]) { # same chr
          splice(@xar1, 6, 1, "="); splice(@xar2, 6, 1, "=");
          if ($readstr1 eq "+") { $p1 = $xar1[3]; }
          else {
            @ar3a = split/\D+/, $xar1[5];                     #cigar chunk sizes
            @ar3b = split/\d+/, $xar1[5]; splice(@ar3b,0,1);  #cigar chunk types
            $p1 = $xar1[3]-1;
            for ($i=0; $i<$#ar3a+1; $i++) { if ("MDN" =~ /$ar3b[$i]/) { $p1 += $ar3a[$i]; } }
          }
          if ($readstr2 eq "+") { $p2 = $xar2[3]; }
          else {
            @ar3a = split/\D+/, $xar2[5];                     #cigar chunk sizes
            @ar3b = split/\d+/, $xar2[5]; splice(@ar3b,0,1);  #cigar chunk types
            $p2 = $xar2[3]-1;
            for ($i=0; $i<$#ar3a+1; $i++) { if ("MDN" =~ /$ar3b[$i]/) { $p2 += $ar3a[$i]; } }
          }
          $p1diff = $p2-$p1; splice(@xar1, 8, 1, $p1diff);
          $p2diff = $p1-$p2; splice(@xar2, 8, 1, $p2diff);
        }
        else {
          $c1 = $xar1[2]; $c2 = $xar2[2];
          splice(@xar1, 6, 1, $c2); splice(@xar2, 6, 1, $c1);
          splice(@xar1, 8, 1, 0); splice(@xar2, 8, 1, 0);
        }
      }
      if ($flag41 eq "u" && $flag42 eq "m") { # PEread1 with flag 0x0004 (i.e. PEread1 is unmapped)
        splice(@xar1, 2, 2, $xar2[2], $xar2[3]);
        splice(@xar1, 6, 1, "="); splice(@xar2, 6, 1, "=");
        splice(@xar1, 7, 1, $xar2[7]);
        splice(@xar1, 8, 1, 0); splice(@xar2, 8, 1, 0);
      }
      if ($flag41 eq "m" && $flag42 eq "u") { # PEread2 with flag 0x0004 (i.e. PEread2 is unmapped)
        splice(@xar2, 2, 2, $xar1[2], $xar1[3]);
        splice(@xar2, 6, 1, "="); splice(@xar1, 6, 1, "=");
        splice(@xar2, 7, 1, $xar1[7]);
        splice(@xar2, 8, 1, 0); splice(@xar1, 8, 1, 0);
      }
      if ($flag41 eq "u" && $flag42 eq "u") { # both with flag 0x0004 (i.e. both PE reads are unmapped)
        splice(@xar1, 2, 2, "*", 0); splice(@xar2, 2, 2, "*", 0);
        splice(@xar1, 6, 1, "*"); splice(@xar2, 6, 1, "*");
        splice(@xar1, 7, 1, 0); splice(@xar2, 7, 1, 0);
        splice(@xar1, 8, 1, 0); splice(@xar2, 8, 1, 0);
      }
    }
    $out = "";
    foreach $a (@xar1) { $out .= "$a\t"; } chop $out;
    foreach $a (@xar2) { $out .= "$a\t"; } chop $out;
    print OUTT1 "$out";
    delete($allreadid{$readidroot});
  }
}
@readIDlist = keys %allreadid;
foreach $readID (@readIDlist) {
  if (exists $allreadid{$readID}{1}) { @xar = split/\t/, $allreadid{$readID}{1}; }
  elsif (exists $allreadid{$readID}{2}) { @xar = split/\t/, $allreadid{$readID}{2}; }
  else { next; }
  splice(@xar, 6, 1, "*");
  splice(@xar, 7, 1, 0);
  splice(@xar, 8, 1, 0);
  $out = "";
  foreach $a (@xar) { $out .= "$a\t"; } chop $out;
  print OUTT1 "$out";
}
close(INT1); close(OUTT1);


system "$java -Xmx2g -jar $picard_convert VALIDATION_STRINGENCY=SILENT TMP_DIR=$tdir INPUT=$tdir/conversion.sam OUTPUT=$tdir/unsorted.bam";
system "$java -Xmx2g -jar $picard_sort VALIDATION_STRINGENCY=SILENT TMP_DIR=$tdir INPUT=$tdir/unsorted.bam OUTPUT=$outfile SORT_ORDER=coordinate";
system "$samtools index $outfile";

if (-e "$tdir/infile.wRG.mod.sam") { system "rm $tdir/infile.wRG.mod.sam"; }
if (-e "$tdir/infile.wRG.mod.namesort.bam") { system "rm $tdir/infile.wRG.mod.namesort.bam"; }
if (-e "$tdir/conversion.sam") { system "rm $tdir/conversion.sam"; }
if (-e "$tdir/unsorted.bam") { system "rm $tdir/unsorted.bam"; }

exit(0);

###########################################################################################################################


sub SUB_revcom {
  my $str1 = $_[0];
  my $str2 = ""; my ($z, $str3);
  for ($z=length($str1)-1; $z>-1; $z--) {
    $str3 = substr($str1, $z, 1);
    if (exists $revcom{$str3}) { $str2 .= $revcom{$str3}; }
    else { $str2 .= "N"; }
  }
  return $str2;
}

sub SUB_rev {
  my $str1 = $_[0];
  my $str2 = ""; my $z;
  for ($z=length($str1)-1; $z>-1; $z--) { $str2 .= substr($str1, $z, 1); }
  return $str2;
}

#  flag   z    N
# 0x0001  0     1  the read is paired in sequencing, no matter whether it is mapped in a pair
# 0x0002  1     2  the read is mapped in a proper pair (depends on the protocol, normally inferred during alignment) 1
# 0x0004  2     4  the query sequence itself is unmapped
# 0x0008  3     8  the mate is unmapped 1
# 0x0010  4    16  strand of the query (0 for forward; 1 for reverse strand)
# 0x0020  5    32  strand of the mate 1
# 0x0040  6    64  the read is the first read in a pair 1,2
# 0x0080  7   128  the read is the second read in a pair 1,2
# 0x0100  8   256  the alignment is not primary (a read having split hits may have multiple primary alignment records)
# 0x0200  9   512  the read fails platform/vendor quality checks
# 0x0400 10  1024  the read is either a PCR duplicate or an optical duplicate

sub SUB_flag_swapstr {
  my $flagin = $_[0];
  my $flagout = $flagin;
  my @farray = ();
  my $thisN = 1024*2; my $z;
  for ($z=10; $z>=0; $z--) {
    $thisN = $thisN/2;
    if ($flagin >= $thisN) { $farray[$z] = 1; $flagin -= $thisN; } else { $farray[$z] = 0; }
  }
  if ($farray[4] == 1) { $flagout -= 16; } else { $flagout += 16; }
  return $flagout;
}

sub SUB_whichread {
  my $flagin = $_[0];
  my @farray = ();
  my $thisN = 1024*2; my ($z, $fout);
  for ($z=10; $z>=0; $z--) {
    $thisN = $thisN/2;
    if ($flagin >= $thisN) { $farray[$z] = 1; $flagin -= $thisN; } else { $farray[$z] = 0; }
  }
  if ($farray[6] == 1) { $fout = "1"; } elsif ($farray[7] == 1) { $fout = "2"; } else { $fout = "0"; }
  return $fout;
}

sub SUB_flaginfo {
  my $flagin = $_[0];
  my @farray = ();
  my $thisN = 1024*2; my ($z, $fout1, $fout2, $fout3, $fout4);
  for ($z=10; $z>=0; $z--) {
    $thisN = $thisN/2;
    if ($flagin >= $thisN) { $farray[$z] = 1; $flagin -= $thisN; } else { $farray[$z] = 0; }
  }
  if ($farray[4] == 1) { $fout1 = "-"; } else { $fout1 = "+"; } # strand of mapping
  if ($farray[5] == 1) { $fout2 = "-"; } else { $fout2 = "+"; } # strand of mate's mapping
  if ($farray[2] == 1) { $fout3 = "u"; } else { $fout3 = "m"; } # self is mapped (m) or unmapped (u)
  if ($farray[3] == 1) { $fout4 = "u"; } else { $fout4 = "m"; } # mate is mapped (m) or unmapped (u)
  return $fout1,$fout2,$fout3,$fout4;
}

sub SUB_fixmateflags {
  my $flagin1 = $_[0]; my $flagin2 = $_[1];
  my @farray1 = (); my @farray2 = ();
  my ($thisN, $z);
  my $fout1 = $flagin1; my $fout2 = $flagin2;
  $thisN = 1024*2;
  for ($z=10; $z>=0; $z--) {
    $thisN = $thisN/2;
    if ($flagin1 >= $thisN) { $farray1[$z] = 1; $flagin1 -= $thisN; } else { $farray1[$z] = 0; }
  }
  $thisN = 1024*2;
  for ($z=10; $z>=0; $z--) {
    $thisN = $thisN/2;
    if ($flagin2 >= $thisN) { $farray2[$z] = 1; $flagin2 -= $thisN; } else { $farray2[$z] = 0; }
  }
#if self is unmapped and self-strand is -, set self-strand to +
  if ($farray1[2] == 1 && $farray1[4] == 1) { $fout1 -= 16; }
  if ($farray2[2] == 1 && $farray2[4] == 1) { $fout2 -= 16; }
#if mate is unmapped and mate-strand is -, set mate-strand to +
  if ($farray1[3] == 1 && $farray1[5] == 1) { $fout1 -= 32; }
  if ($farray2[3] == 1 && $farray2[5] == 1) { $fout2 -= 32; }
#if read is mapped, confirm that mate-strand info in mate read agrees (for both)
  if ($farray1[2] == 0) {
    if ($farray1[4] == 0 && $farray2[5] == 1) { $fout2 -= 32; }
    if ($farray1[4] == 1 && $farray2[5] == 0) { $fout2 += 32; }
  }
  if ($farray2[2] == 0) {
    if ($farray2[4] == 0 && $farray1[5] == 1) { $fout1 -= 32; }
    if ($farray2[4] == 1 && $farray1[5] == 0) { $fout1 += 32; }
  }
  return $fout1,$fout2;
}

