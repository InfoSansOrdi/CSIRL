#! /bin/sh

set -e

if [ $# -le 1 ] ; then
   echo "Usage: petit-livre input output"
   exit 1
fi

pwdir=`pwd`
tmpdir=`mktemp -d /tmp/petit-livre-XXXXX`
basname=`basename $1 .pdf`

cp "$1" $tmpdir

cd $tmpdir
pdfjam --outfile A.pdf --papersize {84cm,297mm} --nup 4x1 --angle 180 "$1" 7,6,5,4
pdfjam --outfile B.pdf --papersize {84cm,297mm} --nup 4x1             "$1" 8,1,2,3
pdfjam --outfile "$pwdir/$2" --landscape --nup 1x2 A.pdf B.pdf

cd $pwdir
rm -r $tmpdir

echo "$basname-livret.pdf generated (temp dir: $tmpdir)."