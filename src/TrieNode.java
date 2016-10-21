import java.util.HashMap;

public class TrieNode {
	public char key = ' ';
	public boolean is_end = false;

	public HashMap<Character, TrieNode> children = new HashMap<Character, TrieNode>();

	public TrieNode() {

	}

	public TrieNode(char key) {
		this.key=key;
	}
}
