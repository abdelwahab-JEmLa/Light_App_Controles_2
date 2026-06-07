import sqlite3
import os
import sys

# Ensure UTF-8 printing in Windows terminal
if sys.platform.startswith('win'):
    import sys
    sys.stdout.reconfigure(encoding='utf-8')

def main():
    db_path = "app_database_temp"
    if not os.path.exists(db_path):
        print(f"Error: {db_path} not found.")
        return

    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()

    # Query M13TarificationInfos for product KeyID '-OV3rmZB2ffLjTxgS0p4'
    prod_key = '-OV3rmZB2ffLjTxgS0p4'
    query = """
        SELECT keyID, id, typeChoisi, prixCurrency, profitMargin, suggestedUpgrade, 
               parent_M1Produit_KeyId, parent_M1Produit_DebugInfos,
               parent_M2Client_KeyId, parent_M2Client_DebugInfos
        FROM M13TarificationInfos
        WHERE parent_M1Produit_KeyId = ?
    """
    cursor.execute(query, (prod_key,))
    rows = cursor.fetchall()

    # Get column names
    cols = ['keyID', 'id', 'typeChoisi', 'prixCurrency', 'profitMargin', 'suggestedUpgrade', 
            'parent_M1Produit_KeyId', 'parent_M1Produit_DebugInfos',
            'parent_M2Client_KeyId', 'parent_M2Client_DebugInfos']

    conn.close()

    print(f"Found {len(rows)} tariffs for product {prod_key}:")
    
    # Create markdown table
    headers = " | ".join(cols)
    separator = " | ".join(["---"] * len(cols))
    print(f"| {headers} |")
    print(f"| {separator} |")
    for row in rows:
        row_str = " | ".join([str(val) if val is not None else "-" for val in row])
        print(f"| {row_str} |")

if __name__ == "__main__":
    main()
