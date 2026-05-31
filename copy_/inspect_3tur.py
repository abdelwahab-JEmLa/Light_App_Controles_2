import json

def main():
    with open("copy_/parsed_bons.json", "r", encoding="utf-8") as f:
        bons = json.load(f)
    for b in bons:
        if b.get("keyID", "").endswith("3tUR"):
            print(json.dumps(b, ensure_ascii=False, indent=2))

if __name__ == "__main__":
    main()
