import sqlite3
import json
import os
import sys
import re
from datetime import datetime

# Ensure UTF-8 printing in Windows terminal
if sys.platform.startswith('win'):
    sys.stdout.reconfigure(encoding='utf-8')

def encode_firebase_path(path):
    # Strip leading/trailing slashes
    p = path.strip('/')
    # Replace '/' with '~2F'
    return "~2F" + p.replace('/', '~2F')

def search_codebase_for_todo():
    todo_pattern = re.compile(r'//\s*TODO:\s*fb_d\s+(.+)', re.IGNORECASE)
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
        # Remove the comment line or clear it
        comment_line = lines[line_number - 1]
        # Check if the line is just the comment, if so, remove it. Otherwise, strip the comment part.
        if comment_line.strip().startswith("//"):
            lines[line_number - 1] = ""
        else:
            # Try to strip '// TODO: fb_d ...'
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
        # Search codebase for TODO: fb_d comment
        source_file, line_number, query = search_codebase_for_todo()

    if not query:
        print("Error: No search query provided. Please pass it via arguments or write a '//TODO: fb_d <query>' comment in the codebase.")
        return

    print(f"Search query: '{query}'")

    db_path = "fb_db_temp"
    if not os.path.exists(db_path):
        print(f"Error: Database {db_path} not found. Please pull it from the device first.")
        return

    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()

    cursor.execute("SELECT path, value FROM serverCache")
    rows = cursor.fetchall()
    
    matches = []
    for path, val_blob in rows:
        try:
            val_str = val_blob.decode('utf-8', errors='ignore')
            # Check if query is in path or in value
            if query.lower() in path.lower() or query.lower() in val_str.lower():
                matches.append((path, val_str))
        except Exception as e:
            pass

    conn.close()

    print(f"Found {len(matches)} matching entries in Firebase cache.")

    # Sort matches by path length or name
    matches.sort(key=lambda x: x[0])

    # Format the results
    result_lines = []
    result_lines.append("# Firebase Database Search Results")
    result_lines.append(f"\n- **Query**: `{query}`")
    result_lines.append(f"- **Source**: {source_file}" + (f":{line_number}" if line_number else ""))
    result_lines.append(f"- **Timestamp**: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    result_lines.append(f"- **Matches Found**: {len(matches)}")
    result_lines.append("\n---")

    for idx, (path, val_str) in enumerate(matches[:150]):  # Cap at 150 results for markdown report size
        # Format JSON value if possible
        try:
            val_json = json.loads(val_str)
            formatted_val = json.dumps(val_json, indent=2, ensure_ascii=False)
        except:
            formatted_val = val_str

        encoded_path = encode_firebase_path(path)
        console_url = f"https://console.firebase.google.com/project/abdelwahab-jemla-com/database/abdelwahab-jemla-com-default-rtdb/data/{encoded_path}"
        json_url = f"https://abdelwahab-jemla-com-default-rtdb.europe-west1.firebasedatabase.app{path}.json"

        result_lines.append(f"\n### {idx + 1}. Path: `{path}`")
        result_lines.append(f"- **Firebase Console**: [Open in Console]({console_url})")
        result_lines.append(f"- **Raw JSON URL**: [Open JSON Endpoint]({json_url})")
        result_lines.append("```json")
        result_lines.append(formatted_val)
        result_lines.append("```")

    if len(matches) > 150:
        result_lines.append(f"\n... and {len(matches) - 150} more matches not shown.")

    # Write report
    report_path = "copy_/fb_db/last_fb_d.md"
    os.makedirs(os.path.dirname(report_path), exist_ok=True)
    with open(report_path, "w", encoding="utf-8") as f:
        f.write("\n".join(result_lines))

    # Write last_query.md log
    query_log = f"""# Last Firebase Query Info

- **Source File**: {source_file}
- **Expression**: `{query}`
- **Timestamp**: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}

## Results Summary:
{len(matches)} matching rows found in Firebase cache.
"""
    log_dir = "app/src/main/java/skill_agent/fb_db"
    os.makedirs(log_dir, exist_ok=True)
    with open(os.path.join(log_dir, "last_query.md"), "w", encoding="utf-8") as f:
        f.write(query_log)

    # Remove TODO comment if needed
    if line_number:
        remove_todo_comment(source_file, line_number)

    print("Firebase search done. Reports updated.")

if __name__ == "__main__":
    main()
