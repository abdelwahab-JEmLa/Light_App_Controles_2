import xml.etree.ElementTree as ET
import sys

sys.stdout.reconfigure(encoding='utf-8')

try:
    tree = ET.parse("window_dump.xml")
    root = tree.getRoot()
except Exception as e:
    # Let's try reading as string and parsing
    with open("window_dump.xml", "r", encoding="utf-8") as f:
        xml_str = f.read()
    # Replace some invalid characters if any
    try:
        root = ET.fromstring(xml_str)
    except Exception as e2:
        print("XML parse error:", e2)
        sys.exit(1)

def print_nodes(node, depth=0):
    text = node.get("text", "")
    content_desc = node.get("content-desc", "")
    cls = node.get("class", "")
    if text or content_desc:
        print("  " * depth + f"[{cls}] Text: '{text}' | Desc: '{content_desc}' | Bounds: {node.get('bounds')}")
    for child in node:
        print_nodes(child, depth + 1)

print("--- Nodes in window_dump.xml ---")
print_nodes(root)
