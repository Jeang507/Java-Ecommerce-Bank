#!/bin/bash

echo "Compilando proyecto..."

rm -rf bin
mkdir bin

javac -d bin $(find src -name "*.java")

if [ $? -eq 0 ]; then
    echo "Compilacion exitosa"
else
    echo "Error en compilacion"
fi

