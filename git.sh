#!/bin/sh
cd /c/Users/Barbuta/Desktop/a/first
git checkout backup
git add .
git commit -m "adding backup"
git push
git checkout master
echo Press Enter...
read