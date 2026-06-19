#!/bin/bash
# MindArena — compile and run script

export PATH="/opt/homebrew/opt/openjdk@21/bin:$PATH"

echo "🔨 Compiling MindArena..."
mkdir -p out
javac -d out src/mindarena/*.java

if [ $? -ne 0 ]; then
  echo "❌ Compilation failed. Fix errors and try again."
  exit 1
fi

echo "✅ Compilation successful!"
echo ""
echo "🎯 Launching MindArena..."
echo ""
java -cp out mindarena.Main
