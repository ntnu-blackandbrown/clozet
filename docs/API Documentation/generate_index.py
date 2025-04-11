#!/usr/bin/env python3
import os
import argparse
from collections import defaultdict
import sys
from pathlib import Path

def split_group_and_name(subdir):
    """Split 'auth-register' into ('auth', 'register')"""
    parts = subdir.split("-", 1)
    if len(parts) == 2:
        return parts[0], parts[1]
    return "misc", subdir  # fallback group

def find_snippets_dir(base_dir, target="target/generated-snippets"):
    """Find the snippets directory starting from base_dir"""
    # First, check if the target is directly under backend
    backend_path = Path(base_dir) / "backend" / target
    if backend_path.exists():
        return str(backend_path)
    
    # Otherwise, search for it
    base_path = Path(base_dir)
    for root, dirs, _ in os.walk(base_path):
        if "target" in dirs:
            target_path = Path(root) / "target" / "generated-snippets"
            if target_path.exists():
                return str(target_path)
    
    # If we still can't find it, return the default
    return target

def generate_index(snippets_dir, output_file):
    # Check if snippets_dir exists, if not search for it
    if not os.path.exists(snippets_dir):
        print(f"Warning: {snippets_dir} not found. Attempting to locate snippets directory...")
        script_dir = os.path.dirname(os.path.abspath(__file__))
        project_root = Path(script_dir).parent.parent  # Go up two levels from script location
        snippets_dir = find_snippets_dir(project_root)
        
        if not os.path.exists(snippets_dir):
            print(f"Error: Could not find snippets directory at {snippets_dir}")
            print("Please run this script from the project root or specify the correct path.")
            sys.exit(1)
        else:
            print(f"Found snippets directory at: {snippets_dir}")
    
    # Organize subdirs by group prefix
    grouped = defaultdict(list)
    for subdir in sorted(os.listdir(snippets_dir)):
        subdir_path = os.path.join(snippets_dir, subdir)
        if os.path.isdir(subdir_path):
            group, name = split_group_and_name(subdir)
            grouped[group].append((name, subdir))

    with open(output_file, 'w', encoding='utf-8') as f:
        f.write("= API Documentation\n")
        f.write(":toc:\n\n")
        f.write("This documentation is automatically generated from the snippet files in '{}'.\n\n".format(snippets_dir))

        for group in sorted(grouped.keys()):
            f.write("== {}\n\n".format(group.title()))

            for name, subdir in grouped[group]:
                subdir_path = os.path.join(snippets_dir, subdir)
                title = name.replace("-", " ").title()
                f.write("=== {}\n\n".format(title))

                adoc_files = [file for file in sorted(os.listdir(subdir_path)) if file.endswith(".adoc")]
                if not adoc_files:
                    f.write("No snippet files found.\n\n")
                else:
                    for file in adoc_files:
                        file_path = os.path.relpath(os.path.join(snippets_dir, subdir, file), start=os.path.dirname(output_file)).replace("\\", "/")
                        file_title = os.path.splitext(file)[0].replace("-", " ").title()
                        f.write("==== {}\n\n".format(file_title))
                        f.write("include::{}[]\n\n".format(file_path))
            f.write("\n")
    print(f"✅ Index file generated: {output_file}")

if __name__ == "__main__":
    parser = argparse.ArgumentParser(
        description="Generate an index.adoc file from the snippet folder for Spring REST Docs."
    )
    parser.add_argument("--snippets-dir", default="target/generated-snippets",
                        help="Directory where snippet files are located (default: target/generated-snippets)")
    parser.add_argument("--output", default="index.adoc",
                        help="Output index file (default: index.adoc)")
    args = parser.parse_args()
    generate_index(args.snippets_dir, args.output)
