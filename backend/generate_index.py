#!/usr/bin/env python3
import os
import argparse

def generate_index(snippets_dir, output_file):
    # Open the output file for writing
    with open(output_file, 'w', encoding='utf-8') as f:
        # Write a header for the documentation
        f.write("= API Documentation\n")
        f.write(":toc:\n\n")
        f.write("This documentation is automatically generated from the snippet files in the '{}' directory.\n\n".format(snippets_dir))

        # Iterate over all subdirectories in the snippets directory
        for subdir in sorted(os.listdir(snippets_dir)):
            subdir_path = os.path.join(snippets_dir, subdir)
            if os.path.isdir(subdir_path):
                # Create a section for each subdirectory, e.g. "auth-register" becomes "Auth Register"
                section_title = subdir.replace("-", " ").title()
                f.write("== {}\n\n".format(section_title))

                # Get all .adoc files in the subdirectory
                adoc_files = [file for file in sorted(os.listdir(subdir_path)) if file.endswith(".adoc")]
                if not adoc_files:
                    f.write("No snippet files found in this folder.\n\n")
                else:
                    for file in adoc_files:
                        # Build the file path, replacing backslashes with forward slashes
                        file_path = os.path.join(snippets_dir, subdir, file).replace("\\", "/")
                        # Use the file name (without extension) as a subsection title
                        file_title = os.path.splitext(file)[0].replace("-", " ").title()
                        f.write("=== {}\n\n".format(file_title))
                        f.write("include::{}[]\n\n".format(file_path))
                f.write("\n")
    print("Index file generated: {}".format(output_file))

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
