#!/usr/bin/bash
scp "pi@camerapi1721.local:*.dat" . 
git add *.dat
git commit -m "tracking files added"

