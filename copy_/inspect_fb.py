import sqlite3

def inspect(db_name):
    print(f"\n--- Inspecting {db_name} ---")
    conn = sqlite3.connect(db_name)
    cursor = conn.cursor()
    cursor.execute("SELECT name FROM sqlite_master WHERE type='table';")
    tables = cursor.fetchall()
    print("Tables:", tables)
    for table in tables:
        t_name = table[0]
        try:
            cursor.execute(f"SELECT COUNT(*) FROM [{t_name}];")
            count = cursor.fetchone()[0]
            print(f"  {t_name}: {count} rows")
            
            # Show a few sample columns/rows if they have columns like 'path', 'value', 'key'
            cursor.execute(f"PRAGMA table_info([{t_name}]);")
            cols = [c[1] for c in cursor.fetchall()]
            print(f"    Columns: {cols}")
            
            cursor.execute(f"SELECT * FROM [{t_name}] LIMIT 2;")
            rows = cursor.fetchall()
            for r in rows:
                print(f"    Row: {r[:3]}") # Show first few fields
        except Exception as e:
            print(f"  {t_name}: error {e}")
    conn.close()

def main():
    inspect("fb_db")
    inspect("fb_db_93f88")

if __name__ == "__main__":
    main()
