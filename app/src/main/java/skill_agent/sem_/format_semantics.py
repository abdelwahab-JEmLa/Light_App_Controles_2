import sqlite3
import datetime
import os

def format_timestamp(ts, heur):
    if not ts:
        return heur or "-"
    try:
        dt = datetime.datetime.fromtimestamp(ts / 1000)
        months = ["Janvier", "Février", "Mars", "Avril", "Mai", "Juin", "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"]
        return f"{dt.day} {months[dt.month - 1]} | {heur or dt.strftime('%H:%M')}"
    except Exception:
        return f"{ts} | {heur or '-'}"

def format_currency(val):
    if val is None or val == 0.0:
        return "-"
    return f"{val:.2f} دج"

def format_short_id(key):
    if not key:
        return "-"
    return f"`{key[-4:]}`"

def format_client_short(client_key):
    if not client_key:
        return "-"
    return f"`{client_key[-4:]}`"

conn = sqlite3.connect("app_database_temp")
cursor = conn.cursor()

# Retrieve columns
cursor.execute("PRAGMA table_info(M8BonVent);")
columns = cursor.fetchall()
colnames = [col[1] for col in columns]

# Helper to execute query and format rows as Markdown table
def get_table_markdown(query, params=()):
    cursor.execute(query, params)
    rows = cursor.fetchall()
    
    headers = [
        "ID", "Date & Heure", "État (Type)", "Montant Principal", "Versement Fait", 
        "Ancien Crédit", "Nouveau Crédit", "Crédit Cumulé", "Versement", "Crédit Fait", 
        "Nouvelle Situation", "Total Sauvegardé", "Client"
    ]
    
    lines = []
    lines.append("| " + " | ".join(headers) + " |")
    lines.append("| " + " | ".join([":---"] * len(headers)) + " |")
    
    if not rows:
        lines.append("| " + " | ".join(["-"] * len(headers)) + " |")
        return "\n".join(lines)
        
    for row in rows:
        row_dict = dict(zip(colnames, row))
        
        # Extract values
        key_id = row_dict.get("keyID")
        ts = row_dict.get("creationTimestamps")
        heur = row_dict.get("heurDebutInString")
        etat = row_dict.get("etateActuellementEst")
        montant = row_dict.get("montant_principale_du_type")
        vers_fait = row_dict.get("versement_fait")
        anc_cred = row_dict.get("ancien_credit")
        new_cred = row_dict.get("new_credit_apre_tout_fait")
        cred_cum = row_dict.get("sum_De_Credit_Fait")
        vers = row_dict.get("versement")
        cred_fait = row_dict.get("credit_fait")
        new_sit = row_dict.get("new_situation")
        tot_saved = row_dict.get("totale_saved")
        client = row_dict.get("parent_M2Client_KeyID")
        
        row_cols = [
            format_short_id(key_id),
            format_timestamp(ts, heur),
            str(etat or "-"),
            format_currency(montant),
            format_currency(vers_fait),
            format_currency(anc_cred),
            format_currency(new_cred),
            format_currency(cred_cum),
            format_currency(vers),
            format_currency(cred_fait),
            format_currency(new_sit),
            format_currency(tot_saved),
            format_client_short(client)
        ]
        lines.append("| " + " | ".join(row_cols) + " |")
        
    return "\n".join(lines)

# 1. listM8bon_filtered
# parent_M2Client_KeyID == '-OWI8JQlhGjA_HzMCGFD'
q_filtered = "SELECT * FROM M8BonVent WHERE parent_M2Client_KeyID = '-OWI8JQlhGjA_HzMCGFD' ORDER BY creationTimestamps DESC;"
md_filtered = get_table_markdown(q_filtered)

# 2. listM8bon (Top 10)
q_all = "SELECT * FROM M8BonVent ORDER BY creationTimestamps DESC LIMIT 10;"
md_all = get_table_markdown(q_all)

# 3. allBons
# parent_M2Client_KeyID == '-OWI8JQlhGjA_HzMCGFD' and etateActuellementEst in CREDIT_VERSEMENT_STATES
CREDIT_VERSEMENT_STATES = ("COMMANDE_LIVRAI", "Versemment", "Credit", "Cette_Transaction_Type_Est_Credit", "Demande_Versemet", "New_Situation_Credit")
q_allbons = f"SELECT * FROM M8BonVent WHERE parent_M2Client_KeyID = '-OWI8JQlhGjA_HzMCGFD' AND etateActuellementEst IN {CREDIT_VERSEMENT_STATES} ORDER BY creationTimestamps DESC;"
md_allbons = get_table_markdown(q_allbons)

# 4. listM8bon_7xp4
# keyID ending in fqTx or 7xp4
q_7xp4 = "SELECT * FROM M8BonVent WHERE keyID LIKE '%fqTx' OR keyID LIKE '%7xp4' ORDER BY creationTimestamps DESC;"
md_7xp4 = get_table_markdown(q_7xp4)

conn.close()

# Generate Report
report = f"""# Semantics Inspection Report

This report contains the parsed custom semantics properties extracted from the device's active UI component at runtime.

---

## 1. Set `listM8bon_7xp4` (Filtered by last 4 = 7xp4 / fqTx)
*Expression: `listM8bon?.filter {{ it.keyID.takeLast(4) == "7xp4" }} ?: emptyList()`*

{md_7xp4}

---

## 2. Set `listM8bon_filtered` (Filtered by client GFD)
*Expression: `active_Datas.list_M8bon?.filter {{ it.parent_M2Client_KeyID == relative_M2Client?.keyID }} ?: emptyList()`*

{md_filtered}

---

## 3. Set `allBons` (Filtered by client GFD & Credit/Versement status)
*Expression: `listM8bon?.filter {{ it.parent_M2Client_KeyID == relative_M2Client?.keyID && it.etateActuellementEst in CREDIT_VERSEMENT_STATES }}?.sortedByDescending {{ it.creationTimestamps }}`*

{md_allbons}

---

## 4. Set `listM8bon` (Total All Transactions - Top 10)
*Expression: `listM8bon ?: emptyList()`*

{md_all}
"""

os.makedirs("copy_", exist_ok=True)
with open("copy_/last_sem_d.md", "w", encoding="utf-8") as f:
    f.write(report)

print("Report generated successfully.")
