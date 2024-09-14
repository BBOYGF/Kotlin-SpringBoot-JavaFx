use std::fs;
use serde_json::{from_str};
use serde::{Deserialize, Serialize};

#[derive(Serialize, Deserialize)]
struct JvmOptions {
    module_args: Vec<String>
}

/**
 * 参考：https://stackoverflow.com/questions/30292752/how-do-i-parse-a-json-file
 */
fn main() {
    // let pattern = std::env::args().nth(1).expect("no pattern given");
    // let path = std::env::args().nth(2).expect("no path given");

    // println!("Hello, world! pattern={}, path={}", pattern, path);
    let options = read_jvm_options();
    println!("参数：{}", options);
}

fn read_jvm_options()-> String {
    let data = fs::read_to_string("../../src/jvm.json").expect("Unable to read file 'jvm.json'!");
    let json: JvmOptions = from_str(&data).expect("Parse jvm json failed!");
    let lines:Vec<String> = json.module_args;

    let mut options = String::new();
    for opt in lines {
        options.push_str(&opt);
    }
    return options;
}
