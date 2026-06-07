import sqlite3

def main():
    conn = sqlite3.connect("fb_db_temp")
    cursor = conn.cursor()
    cursor.execute("SELECT name FROM sqlite_master WHERE type='table';")
    tables = cursor.fetchall()
    print("Tables:", tables)
    for table in tables:
        t_name = table[0]
        cursor.execute(f"SELECT COUNT(*) FROM [{t_name}];")
        print(f"  {t_name}: {cursor.fetchone()[0]} rows")
    conn.close()

if __name__ == "__main__":
    main()
