import json

def main():
    with open("copy_/parsed_bons.json", "r", encoding="utf-8") as f:
        bons = json.load(f)
        
    client_key = "-OWI8JQlhGjA_HzMCGFD"
    client_bons = [b for b in bons if b.get("parent_M2Client_KeyID") == client_key]
    
    print(f"Found {len(client_bons)} records for client {client_key}")
    
    # Sort them by creationTimestamps descending
    client_bons.sort(key=lambda x: x.get("creationTimestamps", 0), reverse=True)
    
    for i, bon in enumerate(client_bons):
        print(f"\nBon {i+1}:")
        print(json.dumps(bon, ensure_ascii=False, indent=2))

if __name__ == "__main__":
    main()
