import sqlite3
import os

def main():
    db_path = "app_database_temp"
    if not os.path.exists(db_path):
        print(f"Error: {db_path} not found.")
        return

    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()

    # Get all tables
    cursor.execute("SELECT name FROM sqlite_master WHERE type='table';")
    tables = [t[0] for t in cursor.fetchall()]
    print("Tables:", tables)

    # Find tables that sound like tariff or M13
    tariff_tables = [t for t in tables if "13" in t or "tariff" in t.lower()]
    print("Tariff tables:", tariff_tables)

    for table in tariff_tables:
        print(f"\nSchema of {table}:")
        cursor.execute(f"PRAGMA table_info({table})")
        cols = cursor.fetchall()
        for col in cols:
            print(f"  {col[1]} ({col[2]})")

        # Query all rows matching the product KeyID
        # Let's check which columns might be a foreign key or have 'produit' or 'parent' or key
        col_names = [col[1] for col in cols]
        
        # Let's query some rows to see format
        cursor.execute(f"SELECT * FROM {table} LIMIT 5")
        print("Sample rows:")
        for r in cursor.fetchall():
            print(f"  {r}")

        # Let's search for the product keyID in any text columns
        matching_rows = []
        for col in col_names:
            try:
                cursor.execute(f"SELECT * FROM {table} WHERE {col} = '-OV3rmZB2ffLjTxgS0p4'")
                res = cursor.fetchall()
                if res:
                    print(f"Found match in column {col}:")
                    for r in res:
                        print(f"  {r}")
            except Exception as e:
                pass
                
    conn.close()

if __name__ == "__main__":
    main()
