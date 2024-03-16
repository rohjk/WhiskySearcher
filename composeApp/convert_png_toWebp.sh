ech "run cwebp"

cd removeBg

for FILE in *.{jpg,jpeg,png,svg,tif,tiff}; do
  [ -e "$FILE" ] || continue
  # Here "$FILE" exists
  cwebp $PARAMS "$FILE" -o ../images/"${FILE%.*}".webp;

done