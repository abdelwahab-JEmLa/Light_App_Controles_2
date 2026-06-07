import csv
import os
import sys
import re
from datetime import datetime

# Ensure UTF-8 printing in Windows terminal
if sys.platform.startswith('win'):
    sys.stdout.reconfigure(encoding='utf-8')

def search_codebase_for_todo():
    todo_pattern = re.compile(r'//\s*TODO:\s*csv_d\s+(.+)', re.IGNORECASE)
    search_dir = "app/src/main/java"
    if not os.path.exists(search_dir):
        return None, None, None

    for root, dirs, files in os.walk(search_dir):
        for file in files:
            if file.endswith('.kt') or file.endswith('.java'):
                file_path = os.path.join(root, file)
                with open(file_path, 'r', encoding='utf-8', errors='ignore') as f:
                    lines = f.readlines()
                for idx, line in enumerate(lines):
                    match = todo_pattern.search(line)
                    if match:
                        query = match.group(1).strip()
                        return file_path, idx + 1, query
    return None, None, None

def remove_todo_comment(file_path, line_number):
    if not file_path or not os.path.exists(file_path):
        return
    with open(file_path, 'r', encoding='utf-8', errors='ignore') as f:
        lines = f.readlines()
    
    if 1 <= line_number <= len(lines):
        comment_line = lines[line_number - 1]
        if comment_line.strip().startswith("//"):
            lines[line_number - 1] = ""
        else:
            idx = comment_line.lower().find("//")
            if idx != -1:
                lines[line_number - 1] = comment_line[:idx] + "\n"
                
        with open(file_path, 'w', encoding='utf-8') as f:
            f.writelines(lines)
        print(f"Removed TODO comment from {file_path}:{line_number}")

def main():
    query = None
    source_file = "Direct Chat Prompt"
    line_number = None

    # Check CLI arguments first
    if len(sys.argv) > 1:
        query = " ".join(sys.argv[1:])
    else:
        # Search codebase for TODO: csv_d comment
        source_file, line_number, query = search_codebase_for_todo()

    if not query:
        print("Error: No search query provided. Please pass it via arguments or write a '//TODO: csv_d <query>' comment in the codebase.")
        return

    print(f"Search query: '{query}'")

    # Smart parsing of query
    stop_words = {'de', 'of', 'du', 'des', 'le', 'la', 'les', 'un', 'une', 'd\'', 'l\''}
    words = [w.lower() for w in re.split(r'\s+|_+', query) if w]
    clean_words = [w for w in words if w not in stop_words]

    file_mappings = {
        'tariff': ['m13', 'tariff'],
        'tariffs': ['m13', 'tariff'],
        'tarff': ['m13', 'tariff'],
        'tarffs': ['m13', 'tariff'],
        'tarif': ['m13', 'tariff'],
        'tarifs': ['m13', 'tariff'],
        'tarf': ['m13', 'tariff'],
        'tarfs': ['m13', 'tariff'],
        'tarification': ['m13', 'tariff'],
        'produit': ['m01', 'produit'],
        'product': ['m01', 'produit'],
        'client': ['m02', 'm2', 'client'],
        'couleur': ['m03', 'couleur', 'color'],
        'color': ['m03', 'couleur', 'color'],
        'bon': ['m08', 'bon'],
        'operation': ['m10', 'operation'],
        'periode': ['m14', 'periode', 'period']
    }

    target_file_patterns = []
    search_words = []

    for word in clean_words:
        matched = False
        for key, patterns in file_mappings.items():
            if word == key or word.rstrip('s') == key:
                target_file_patterns.extend(patterns)
                matched = True
        if not matched:
            search_words.append(word)

    # If no target patterns were found, all clean words are search words and we search all files
    if not target_file_patterns:
        search_words = clean_words

    print(f"Target file patterns: {target_file_patterns}")
    print(f"Search terms: {search_words}")

    csv_dir = "copy_/TestDatas"
    if not os.path.exists(csv_dir):
        print(f"Error: CSV directory {csv_dir} not found. Please pull it from the device first.")
        return

    file_results = {}
    total_matches = 0

    # Read and search through CSV files
    for file in os.listdir(csv_dir):
        if file.endswith('.csv'):
            # If target_file_patterns is set, check if the file name matches any pattern
            if target_file_patterns:
                match_file = False
                for pat in target_file_patterns:
                    if pat.lower() in file.lower():
                        match_file = True
                        break
                if not match_file:
                    continue

            file_path = os.path.join(csv_dir, file)
            try:
                with open(file_path, mode='r', encoding='utf-8', errors='ignore') as f:
                    sample = f.read(2048)
                    f.seek(0)
                    delimiter = ';' if ';' in sample else ','
                    
                    reader = csv.reader(f, delimiter=delimiter)
                    headers = next(reader, None)
                    if not headers:
                        continue
                    
                    matches = []
                    for row in reader:
                        row_str = " ".join(row).lower()
                        # Check if all search words are in row_str
                        if all(term in row_str for term in search_words):
                            matches.append(row)
                            total_matches += 1
                    
                    if matches:
                        file_results[file] = {
                            "headers": headers,
                            "rows": matches
                        }
            except Exception as e:
                print(f"Error reading {file}: {e}")

    print(f"Found {total_matches} matching entries across {len(file_results)} CSV files.")

    # Format the results into Markdown
    report_lines = []
    report_lines.append("# CSV Database Search Results")
    report_lines.append(f"\n- **Query**: `{query}`")
    report_lines.append(f"- **Source**: {source_file}" + (f":{line_number}" if line_number else ""))
    report_lines.append(f"- **Timestamp**: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    report_lines.append(f"- **Total Matches**: {total_matches}")
    report_lines.append("\n---")

    for file_name, data in file_results.items():
        headers = data["headers"]
        rows = data["rows"]
        
        report_lines.append(f"\n## 📄 File: `{file_name}` ({len(rows)} matches)")
        
        # Limit columns if there are too many (e.g. max 10 columns for markdown table display)
        display_headers = headers
        display_rows = rows
        
        # Create markdown table
        headers_str = " | ".join(display_headers)
        separator_str = " | ".join(["---"] * len(display_headers))
        report_lines.append(f"| {headers_str} |")
        report_lines.append(f"| {separator_str} |")
        
        # Cap at 50 rows per file for markdown report size
        for row in display_rows[:50]:
            # Align row values to headers length
            if len(row) < len(display_headers):
                row += [""] * (len(display_headers) - len(row))
            row_str = " | ".join([str(val) if val != "" else "-" for val in row])
            report_lines.append(f"| {row_str} |")
            
        if len(rows) > 50:
            report_lines.append(f"\n... and {len(rows) - 50} more matches not shown for this file.")

    # Write report
    report_path = "copy_/csv_d/last_csv_d.md"
    os.makedirs(os.path.dirname(report_path), exist_ok=True)
    with open(report_path, "w", encoding="utf-8") as f:
        f.write("\n".join(report_lines))

    # Write last_query.md log
    query_log = f"""# Last CSV Query Info

- **Source File**: {source_file}
- **Expression**: `{query}`
- **Timestamp**: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}

## Results Summary:
{total_matches} matching rows found across CSV files.
"""
    log_dir = "app/src/main/java/skill_agent/csv_d"
    os.makedirs(log_dir, exist_ok=True)
    with open(os.path.join(log_dir, "last_query.md"), "w", encoding="utf-8") as f:
        f.write(query_log)

    # Remove TODO comment if needed
    if line_number:
        remove_todo_comment(source_file, line_number)

    print("CSV search done. Reports updated.")

if __name__ == "__main__":
    main()
