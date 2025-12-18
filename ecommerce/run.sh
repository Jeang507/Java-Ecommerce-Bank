#!/bin/bash

# Limpiar compilaciones anteriores
echo "[1/4] Limpiando compilaciones anteriores..."
rm -rf out
mkdir out
echo "Carpeta out preparada"
echo

# Compilar proyecto
echo "[2/4] Compilando proyecto..."
javac -cp "lib/*" -d out $(find src -name "*.java")

if [ $? -ne 0 ]; then
    echo
    echo "Error durante la compilación"
    echo "Revise los mensajes anteriores"
    exit 1
fi

echo "✔ Compilación exitosa"
echo

# Ejecutar aplicación
echo "[3/4] Ejecutando aplicación..."
echo

java -cp "out:lib/*" app.EcommerceApp

echo
echo "[4/4] Aplicación finalizada"
