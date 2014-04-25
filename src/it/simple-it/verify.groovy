println("Hello World from Groovy");

File touchFile = new File("target/touch.txt" );
touchFile.createNewFile();

touchFile.write("Foo Bar");

assert touchFile.isFile()
