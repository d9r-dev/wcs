package de.d9r;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

@Command(name = "jwc", version = "1.0.0", mixinStandardHelpOptions = true)
public class JWC implements Runnable{
    private static final Logger logger = Logger.getLogger(JWC.class.getName());
    @Option(names = { "-w", "--words"}, description = "print count of words")
    Boolean countWords = false;

    @Option(names = { "-c", "--chars"}, description = "print count of bytes")
    Boolean countBytes = false;

    @Option(names = { "-l", "--lines"}, description = "print count of lines")
    Boolean countLines = false;

    @Parameters(paramLabel = "<file>", defaultValue = "-", description = "file to process")
    private String file;

    @Override
    public void run() {
        int wordCount = 0;
        int byteCount = 0;
        int lineCount = 0;
        boolean isPrevCharSpace = false;
        boolean showChars = false;


        try (BufferedReader reader = readFile(file, showChars)) {
            if (reader == null) {
                return;
            }

            int c;
            while ((c = reader.read()) != -1) {
                char character = (char) c;
                byteCount++;
                if (Character.isWhitespace(character)) {
                    if (character == '\n') {
                        lineCount++;
                    }
                    if (!isPrevCharSpace) {
                        wordCount++;
                        isPrevCharSpace = true;
                    }
                } else {
                    if(character != '\n') {
                        isPrevCharSpace = false;
                    }
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to read file: " +file, e);
            return;
        }

        printResult(wordCount, byteCount, lineCount);
    }

    private void printResult(int wordCount, int byteCount, int lineCount) {
        if (countWords) {
            System.out.print("Words: " + wordCount + "\t");
        }

        if (countBytes) {
            System.out.print("Bytes: " + byteCount + "\t");
        }

        if (countLines) {
            System.out.print("Lines: " + lineCount + "\t");
        }
        System.out.print(file);
    }

    private BufferedReader readFile(String file, boolean showChars) {
        BufferedReader reader;
        Charset charset = showChars
                ? StandardCharsets.UTF_8
                : StandardCharsets.ISO_8859_1;

        if (file.equals("-")) {
            reader = new BufferedReader(new java.io.InputStreamReader(System.in, charset));
            return reader;
        }

        try {
            reader = new BufferedReader(Files.newBufferedReader(Path.of(file), charset));
            return reader;

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to read file: " +file, e);
            return null;
        }
    }
}
