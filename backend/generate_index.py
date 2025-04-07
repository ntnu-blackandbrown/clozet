#!/usr/bin/env python3
import os
import argparse
from collections import defaultdict

def split_group_and_name(subdir):
    """Split 'auth-register' into ('auth', 'register')"""
    parts = subdir.split("-", 1)
    if len(parts) == 2:
        return parts[0], parts[1]
    return "misc", subdir  # fallback group

def generate_index(snippets_dir, output_file):
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
                        file_path = os.path.join(snippets_dir, subdir, file).replace("\\", "/")
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
