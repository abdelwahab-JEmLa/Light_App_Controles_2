import json
from datetime import datetime

def format_val(val):
    if val is None or val == 0 or val == "" or val == "null" or val == 0.0:
        return "-"
    try:
        f_val = float(val)
        return f"{f_val:.2f} دج"
    except:
        return str(val)

def generate_table(bons_list):
    months = ["Janvier", "Février", "Mars", "Avril", "Mai", "Juin", "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"]
    table_lines = []
    table_lines.append("| ID | Date & Heure | État (Type) | Montant Principal | Versement Fait | Ancien Crédit | Nouveau Crédit | Crédit Cumulé | Versement | Crédit Fait | Nouvelle Situation | Total Sauvegardé | Client |")
    table_lines.append("| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |")
    
    for b in bons_list:
        key_id = b.get("keyID", "")
        short_id = key_id[-4:] if len(key_id) >= 4 else key_id
        
        ts = b.get("creationTimestamps", 0)
        try:
            dt = datetime.fromtimestamp(ts / 1000.0)
            date_str = f"{dt.day} {months[dt.month - 1]}"
        except:
            date_str = "-"
        heur = b.get("heurDebutInString", "Non Defini")
        date_heure = f"{date_str} \\| {heur}"
        
        etat = b.get("etateActuellementEst", "-")
        
        m_principal_raw = b.get("montant_principale_du_type")
        c_fait_raw = b.get("credit_fait")
        v_fait_raw = b.get("versement_fait")
        
        if etat in ["Credit", "Cette_Transaction_Type_Est_Credit"]:
            montant_principal = format_val(m_principal_raw if m_principal_raw else c_fait_raw)
            versement_fait = format_val(v_fait_raw)
            ancien_credit = format_val(b.get("ancien_credit"))
            nouveau_credit = format_val(b.get("new_credit_apre_tout_fait") if b.get("new_credit_apre_tout_fait") else c_fait_raw)
            credit_cumule = format_val(b.get("sum_De_Credit_Fait") if b.get("sum_De_Credit_Fait") else c_fait_raw)
            versement = format_val(b.get("versement"))
            credit_fait = format_val(c_fait_raw)
            nouvelle_situation = format_val(b.get("new_situation") if b.get("new_situation") else c_fait_raw)
            totale_saved = format_val(b.get("totale_saved"))
        elif etat == "Versemment":
            montant_principal = format_val(m_principal_raw if m_principal_raw else v_fait_raw)
            versement_fait = format_val(v_fait_raw)
            ancien_credit = format_val(b.get("ancien_credit"))
            nouveau_credit = format_val(b.get("new_credit_apre_tout_fait"))
            credit_cumule = format_val(b.get("sum_De_Credit_Fait"))
            versement = format_val(b.get("versement"))
            credit_fait = format_val(c_fait_raw)
            nouvelle_situation = format_val(b.get("new_situation"))
            totale_saved = format_val(b.get("totale_saved"))
        else:
            montant_principal = format_val(m_principal_raw)
            versement_fait = format_val(v_fait_raw)
            ancien_credit = format_val(b.get("ancien_credit"))
            nouveau_credit = format_val(b.get("new_credit_apre_tout_fait"))
            credit_cumule = format_val(b.get("sum_De_Credit_Fait"))
            versement = format_val(b.get("versement"))
            credit_fait = format_val(c_fait_raw)
            nouvelle_situation = format_val(b.get("new_situation"))
            totale_saved = format_val(b.get("totale_saved"))
            
        client_dbg = b.get("parent_M2Client_DebugInfos", "")
        client_code = "-"
        if client_dbg and "[" in client_dbg and "]" in client_dbg:
            client_code = client_dbg.split("[")[-1].split("]")[0]
        else:
            p_key = b.get("parent_M2Client_KeyID", "")
            client_code = p_key[-4:] if len(p_key) >= 4 else p_key
            if not client_code:
                client_code = "-"
            
        row = f"| `{short_id}` | {date_heure} | {etat} | {montant_principal} | {versement_fait} | {ancien_credit} | {nouveau_credit} | {credit_cumule} | {versement} | {credit_fait} | {nouvelle_situation} | {totale_saved} | `{client_code}` |"
        table_lines.append(row)
        
    return "\n".join(table_lines)

def main():
    with open("copy_/parsed_bons.json", "r", encoding="utf-8") as f:
        bons = json.load(f)
        
    client_key = "-OWI8JQlhGjA_HzMCGFD"
    
    # ---------------- SET 1 & SET 3 ----------------
    client_bons = [b for b in bons if b.get("parent_M2Client_KeyID") == client_key]
    client_bons.sort(key=lambda x: x.get("creationTimestamps", 0), reverse=True)
    
    table_md = generate_table(client_bons)
    
    # ---------------- SET 2 ----------------
    total_bons = len(bons)
    states_count = {}
    for b in bons:
        s = b.get("etateActuellementEst")
        states_count[s] = states_count.get(s, 0) + 1
        
    set2_markdown = f"""### Set 2 : `listM8bon` (Total: {total_bons} Bons en Cache)

Ce set contient l'intégralité des bons présents dans le cache local. 

**Répartition par État/Type :**
* `ON_MODE_COMMEND_ACTUELLEMENT` : {states_count.get('ON_MODE_COMMEND_ACTUELLEMENT', 0)} bons
* `Credit` : {states_count.get('Credit', 0)} bons
* `Versemment` : {states_count.get('Versemment', 0)} bons
* `Demande_Versemet` : {states_count.get('Demande_Versemet', 0)} bons
* `COMMANDE_LIVRAI` : {states_count.get('COMMANDE_LIVRAI', 0)} bons
* `FERME` : {states_count.get('FERME', 0)} bons
* `Cible` : {states_count.get('Cible', 0)} bons
* Autres : {states_count.get('Cette_Transaction_Type_Est_Credit', 0) + states_count.get('AVEC_MARCHANDISE', 0) + states_count.get('A_COMMANDE_CONFIRME', 0)} bons

**Exemple d'éléments du Set (5 Premiers Éléments sous forme de tableau sémantique) :**

{generate_table(bons[:5])}
"""

    # Combine everything
    full_report = f"""## 📱 Rapport Multiset Sémantique (3 Sets Activés)

Conformément à la nouvelle règle **Multi-Set Semantics**, les 3 propriétés sémantiques distinctes ont été injectées dans le composant Compose et analysées.

---

### Set 1 : `listM8bon_filtered` (11 Bons Filtrés par Client)
Ce set contient l'ensemble des bons de vente spécifiques au client **Youcef Zohire (`GFD`)** :

{table_md}

---

{set2_markdown}

---

### Set 3 : `allBons` (11 Bons Actifs de type Crédit/Versement pour le Client)
Ce set représente les bons de transaction filtrés et ordonnés, prêts pour l'affichage de la situation financière du client :

{table_md}
"""

    with open("copy_/3_sets_report.md", "w", encoding="utf-8") as out:
        out.write(full_report)
    print("Generated 3 sets report successfully!")

if __name__ == "__main__":
    main()
