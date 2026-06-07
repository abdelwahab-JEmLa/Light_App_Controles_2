import sqlite3

def main():
    conn = sqlite3.connect("app_database_temp")
    cursor = conn.cursor()
    cursor.execute("SELECT name FROM sqlite_master WHERE type='table';")
    tables = cursor.fetchall()
    print("Tables:", tables)
    
    # Check table structure for M8BonVent or similar
    for table in tables:
        t_name = table[0]
        if "M8" in t_name or "bon" in t_name.lower():
            print(f"\nStructure of {t_name}:")
            cursor.execute(f"PRAGMA table_info({t_name});")
            for col in cursor.fetchall():
                print(col)
                
            cursor.execute(f"SELECT COUNT(*) FROM {t_name};")
            print("Row count:", cursor.fetchone()[0])
            
            # Print a few rows
            cursor.execute(f"SELECT * FROM {t_name} LIMIT 5;")
            for row in cursor.fetchall():
                print(row)
                
    conn.close()

if __name__ == "__main__":
    main()
