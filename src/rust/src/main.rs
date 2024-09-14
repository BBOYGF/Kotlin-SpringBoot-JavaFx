#![windows_subsystem = "windows"]
use std::fs;
use std::process::Command;
use sys_info::mem_info;
use serde_json::{from_str};
use serde::{Deserialize, Serialize};

#[derive(Serialize, Deserialize)]
struct JvmOptions {
    args: Vec<String>
}

// This is the main function
fn main() {
    println!("Start...");
    let jars = list_jars("./lib");
    let classpath = jars_to_classpath(&jars);
    println!("类路径：{}\n", classpath);
    // unwrap 会发出 panic!，如果失败
    let total_mem = mem_info().unwrap().total;
    println!("系统总内存：{}G\n", total_mem/(1024*1024));

    // mx_mem 设置为比总内存少1G
    let mx_mem = total_mem/(1024*1024) - 1;
    let x_mx = format!("-Xmx{}G", mx_mem);
    println!("-Xmx参数：{}\n", x_mx);

    // 读取 jvm 参数文件
    let jvm_options = read_jvm_args();
    let mut command = Command::new("./jre/bin/javaw.exe");
    command.arg(x_mx);
    for arg in jvm_options {
        command.arg(arg);
    }
    let output = command
        .arg("-classpath")
        .arg(classpath)
        .arg("com.telecwin.javafx.Main")
        .output().unwrap_or_else(|e| {
        panic!("启动Java进程出现异常: {}", e)
    });
    println!("已经启动 java");
    if output.status.success() {
        let out = String::from_utf8_lossy(&output.stdout);
        let err = String::from_utf8_lossy(&output.stderr);
        println!("引导程序成功。\n{}\n{}", out, err);
    } else {
        let err = String::from_utf8_lossy(&output.stderr);
        let out = String::from_utf8_lossy(&output.stdout);
        println!("引导程序失败:\n{}\n{}", err, out);
    }
}

fn list_jars(dir: &str) -> Vec<String> {
    let mut jars: Vec<String> = Vec::new();
    let result = fs::read_dir(dir);
    if let Ok(entries) = result {
        for entry in entries {
            if let Ok(entry) = entry {
                let path = entry.path();
                match path.to_str() {
                    Some(path) => jars.push(String::from(path)),
                    None => println!("path entry is not unicode!")
                }
            }
        }
    }
    jars
}

fn jars_to_classpath(jars: &Vec<String>) -> String {
    let mut classpath = String::new();
    for jar in jars {
        classpath.push_str(jar);
        classpath.push_str(";")
    }
    classpath
}

fn read_jvm_args() -> Vec<String> {
    let data = fs::read_to_string("./jvm.json").expect("Unable to read file 'jvm.json'!");
    let json: JvmOptions = from_str(&data).expect("Parse jvm json failed!");
    return json.args;

    // let mut options = String::new();
    // for opt in lines {
    //     options.push_str(&opt);
    // }
    // return options;
}
