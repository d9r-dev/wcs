#! /usr/bin/env bun

import yargs from "yargs";
import { hideBin } from "yargs/helpers";

type Options = {
  words: boolean;
  lines: boolean;
  chars: boolean;
};

const argv = yargs(hideBin(process.argv))
  .scriptName("wcbun")
  .usage("Usage: [file] $0 -w -c -l ")
  .option("w", {
    alias: "words",
    description: "print all words",
    type: "boolean",
  })
  .option("l", {
    alias: "lines",
    description: "print all lines",
    type: "boolean",
  })
  .option("c", {
    alias: "chars",
    description: "print all chars/bytes",
    type: "boolean",
  })
  .parseSync();

const options: Options = {
  words: argv.w ? true : false,
  lines: argv.l ? true : false,
  chars: argv.c ? true : false,
};

const whitespaces = [9, 10, 11, 12, 13, 32];

let chars = 0;
let words = 0;
let lines = 0;

let stream: ReadableStream<Uint8Array> | null = null;
if (argv._.length === 0) {
  stream = Bun.stdin.stream();
} else {
  try {
    const filePath = argv._[0] as string;
    const file = Bun.file(filePath);
    stream = file.stream();
  } catch (e) {
    console.error("could not open file");
  }
}
let preWhite = false;

for await (const chunk of stream) {
  for (let c of chunk as Uint8Array) {
    chars++;
    if (c === 10) {
      lines++;
    }

    if (whitespaces.includes(c)) {
      preWhite = true;
    } else {
      if (preWhite) {
        words++;
        preWhite = false;
      }
    }
  }
}

let result = "";
if (options.words) {
  result += words + " ";
}
if (options.lines) {
  result += lines + " ";
}
if (options.chars) {
  result += chars + " ";
}

console.log(result);
