import os
import re

def find_project_root(start_dir):
    current = os.path.abspath(start_dir)
    while True:
        if os.path.exists(os.path.join(current, "settings.gradle.kts")) or os.path.exists(os.path.join(current, ".git")):
            return current
        parent = os.path.dirname(current)
        if parent == current:
            # Reached filesystem root, fallback to hardcoded relative depth
            return os.path.abspath(os.path.join(start_dir, "..", "..", "..", "..", "..", "..", ".."))
        current = parent

def main():
    project_root = find_project_root(os.path.dirname(__file__))
    ignore_file_path = os.path.join(project_root, ".antigravityignore")
    search_dir = os.path.join(project_root, "app", "src", "main", "java")
    
    is_restricted = False
    allowed_patterns = []
    
    # Read .antigravityignore to determine if restrictions are active
    if os.path.exists(ignore_file_path):
        with open(ignore_file_path, "r", encoding="utf-8") as f:
            content = f.read()
            if "# Context restrictions deactivated" not in content and "*" in content:
                is_restricted = True
                # Extract allowed patterns (lines starting with !)
                for line in content.splitlines():
                    line = line.strip()
                    if line.startswith("!"):
                        pattern = line[1:]
                        # Remove trailing ** or * or /
                        pattern = re.sub(r'/\*\*$', '', pattern)
                        pattern = re.sub(r'/\*$', '', pattern)
                        pattern = pattern.rstrip('/')
                        if pattern:
                            allowed_patterns.append(pattern)

    # Walk directory
    all_files = []
    if os.path.exists(search_dir):
        for root, dirs, files in os.walk(search_dir):
            for file in files:
                full_path = os.path.join(root, file)
                # Make path relative to project root with forward slashes
                rel_path = os.path.relpath(full_path, project_root).replace("\\", "/")
                
                # Check if file is allowed
                if is_restricted:
                    allowed = False
                    for pat in allowed_patterns:
                        if rel_path.startswith(pat) or pat.startswith(rel_path):
                            allowed = True
                            break
                    if not allowed:
                        continue
                
                all_files.append(rel_path)

    # Sort files for deterministic hierarchy
    all_files.sort()

    # Build tree representation
    tree = {}
    for path in all_files:
        parts = path.split('/')
        current = tree
        for part in parts:
            if part not in current:
                current[part] = {}
            current = current[part]

    # Render tree to Markdown
    markdown_lines = ["# files_affiched.md\n", "Voici l'arborescence des dossiers et des fichiers du contexte actif :\n"]
    
    # Start rendering from the first common prefix or just render the root nodes
    curr = tree
    skip_levels = 0
    common_path = []
    while len(curr) == 1:
        key = list(curr.keys())[0]
        if len(curr[key]) == 0: # It's a file
            break
        common_path.append(key)
        curr = curr[key]
        skip_levels += 1

    if common_path:
        markdown_lines.append(f"Racine commune masquée : `{' / '.join(common_path)}/` \n")

    markdown_lines.append("```diff")

    def render(node, prefix="  "):
        keys = sorted(node.keys())
        for i, key in enumerate(keys):
            is_last = (i == len(keys) - 1)
            connector = "└── " if is_last else "├── "
            is_file = (len(node[key]) == 0)
            
            if is_file:
                markdown_lines.append(f"{prefix}{connector}{key}")
            else:
                markdown_lines.append(f"{prefix}{connector}{key}/")
                new_prefix = prefix + ("    " if is_last else "│   ")
                render(node[key], new_prefix)

    if common_path:
        render(curr, "")
    else:
        render(tree, "")

    markdown_lines.append("```")

    # Write to files_affiched.md in the same directory as this script
    output_path = os.path.join(os.path.dirname(__file__), "files_affiched.md")
    with open(output_path, "w", encoding="utf-8") as f:
        f.write("\n".join(markdown_lines) + "\n")
        
    print(f"Map successfully generated at: {output_path}")

if __name__ == "__main__":
    main()
