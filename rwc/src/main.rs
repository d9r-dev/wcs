use std::{fs::File, io::{BufReader, Read}};

use clap::Parser;

#[derive(Parser, Debug)]
#[command(version, about, long_about = None)]
struct Args {
    #[arg(short, long)]
    words: bool,

    #[arg(short, long)]
    lines: bool,

    #[arg(short, long)]
    chars: bool,

    path: std::path::PathBuf,
}

fn main() {
    let mut words = 0;
    let mut lines = 0;
    let mut chars = 0;

    let args = Args::parse();
    
    let file = File::open(args.path).unwrap();
    let mut reader = BufReader::new(file);
    let mut buffer = Vec::new();

    reader.read_to_end(&mut buffer).unwrap();

    let mut pre_white = false;

    for value in buffer {
        chars += 1;
        match value {
            9 | 10 | 11 | 12 | 13 | 32 => {
                if value == 10 {
                    lines += 1;
                }
                pre_white = true;
            }
            _ => {
                if pre_white {
                    words += 1;
                }
                pre_white = false;
            }
        }
    }

    let mut result = String::new();

    if args.words {
        result.push_str(&words.to_string());
        result.push_str(" ");
    }

    if args.lines {
        result.push_str(&lines.to_string());
        result.push_str(" ");
    }

    if args.chars {
        result.push_str(&chars.to_string());
    }   

    println!("{}", result);

}
