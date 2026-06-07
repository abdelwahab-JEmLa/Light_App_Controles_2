import sqlite3
import os
from datetime import datetime

def main():
    db_path = "app_database_temp"
    if not os.path.exists(db_path):
        print(f"Error: {db_path} not found.")
        return

    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()

    # Get column names of M01Produit
    cursor.execute("PRAGMA table_info(M01Produit)")
    cols_info = cursor.fetchall()
    cols = [col[1] for col in cols_info]

    # Query M01Produit for 'trix'
    query = "SELECT * FROM M01Produit WHERE nom LIKE '%trix%' OR nomArab LIKE '%trix%' ORDER BY nom;"
    cursor.execute(query)
    rows = cursor.fetchall()
    conn.close()

    # Create markdown table
    headers = " | ".join(cols)
    separator = " | ".join(["---"] * len(cols))
    
    table_lines = [f"| {headers} |", f"| {separator} |"]
    for row in rows:
        row_str = " | ".join([str(val) if val is not None else "-" for val in row])
        table_lines.append(f"| {row_str} |")
    
    table_md = "\n".join(table_lines)
    timestamp_str = datetime.now().strftime("%Y-%m-%d %H:%M:%S")

    # Write copy_/room_d/last_room_d.md
    report_md = f"""# Room Database Query Results

- **Expression**: `trix`
- **SQL Executed**: `{query}`
- **Database**: `app_database_temp`
- **Timestamp**: {timestamp_str}

---

## Query Results for `M01Produit` matching 'trix'

{table_md}
"""
    os.makedirs("copy_/room_d", exist_ok=True)
    with open("copy_/room_d/last_room_d.md", "w", encoding="utf-8") as f:
        f.write(report_md)

    # Write app/src/main/java/skill_agent/room_d/last_query.md
    query_log = f"""# Last Room Query Info

- **Source File**: Direct Chat Prompt
- **Expression**: `trix`
- **SQL Statement**: {query}
- **Timestamp**: {timestamp_str}

## Results Summary:
{len(rows)} matching rows found.
"""
    os.makedirs("app/src/main/java/skill_agent/room_d", exist_ok=True)
    with open("app/src/main/java/skill_agent/room_d/last_query.md", "w", encoding="utf-8") as f:
        f.write(query_log)

    print(f"Successfully queried {len(rows)} rows. Reports updated.")

if __name__ == "__main__":
    main()
