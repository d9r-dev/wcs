package de.d9r;

import picocli.CommandLine;

public class Main {

    public static void main(String[] args) {
        int exitCode = new CommandLine(new JWC()).execute(args);
        System.exit(exitCode);
    }
}