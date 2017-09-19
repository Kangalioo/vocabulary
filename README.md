# Vocabulary Testing Program
Self built vocabulary testing program with GUI. The vocabulary itself is read from the vocabulary.json file. The vocabulary.json file contains data for the German Spanish learning book "Línea amarilla". But you can easily edit it to suit your own needs (if you know JSON).

## Dependencies
This program depends on [minimal-json](https://github.com/ralfstx/minimal-json), a lightweight Java library on GitHub by ralfstx for reading JSON files.

## Using It
### Manually
Just compile all java files, including the minimal-json jar file.

Windows:
```javac -cp ".;path/to/minimal-json-x.x.x.jar" *.java```

Unix-like:
```javac -cp ".:path/to/minimal-json-x.x.x.jar" *.java```

Launching is similarly simple.

Windows:
```java -cp ".;path/to/minimal-json-x.x.x.jar" Main```

Unix-like:
```java -cp ".:path/to/minimal-json-x.x.x.jar" Main```

### Automatically
I am providing a zip-file with everything you need included (Not yet, actually, but I am planning to do that... somewhen in the future). Unpack it and run the ```START_WINDOWS.BAT``` or the ```START_UNIX.sh``` script, depending on your OS (```START_UNIX.sh``` works for any unix-like OS with bash, so Linux and MacOS should be able to handle it).
